package com.campus.banking.app;

import java.io.File;
import java.util.Arrays;

import org.apache.catalina.Context;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardVirtualThreadExecutor;
import org.apache.catalina.loader.ParallelWebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.realm.DataSourceRealm;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.eclipse.microprofile.config.Config;

import com.campus.banking.model.Role;
import com.campus.banking.service.HashService;
import com.campus.banking.service.Argon2HashService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TomcatEmbedded implements Server {

    private Config config;

    private Tomcat server;

    private static final String ADDITION_WEB_INF_CLASSES = "target/classes";
    private static final String WEB_APP_MOUNT = "/WEB-INF/classes";
    private static final String INTERNAL_PATH = "/";
    private static final String WEB_APP_DIR = "target/classes/META-INF/resources/";
    private static final String BASE_DIR = System.getProperty("java.io.tmpdir");

    public TomcatEmbedded(Config config) {
        this.config = config;
    }

    @Override
    public void start() throws ServerFailureException {
        log.debug("Starting Tomcat...");
        try {
            var tomcat = setupTomcat();
            var context = addApp(tomcat);
            addJDBC(context);
            addRoles(context);
            addRealm(context);
            tomcat.start();

            var classLoader = (ParallelWebappClassLoader) context.getLoader().getClassLoader();
            classLoader.setDelegate(true);
            server = tomcat;

            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new ServerFailureException(e);
        }
    }

    @Override
    public void stop() throws ServerFailureException {
        try {
            server.stop();
            server.destroy();
        } catch (LifecycleException e) {
            throw new ServerFailureException(e);
        }
    }

    private void addRoles(Context context) {
        Arrays.stream(Role.values())
                .map(Role::toString)
                .forEach(context::addSecurityRole);
    }

    private void addJDBC(Context context) {
        var resource = new ContextResource();
        var user = config.getValue("datasource.user", String.class);
        var password = config.getValue("datasource.password", String.class);
        var url = config.getValue("datasource.url", String.class);
        var driver = config.getValue("datasource.driver", String.class);
        resource.setAuth("Container");
        resource.setName("jdbc/bank");
        resource.setType("javax.sql.DataSource");
        resource.setProperty("maxTotal", "100");
        resource.setProperty("maxIdle", "30");
        resource.setProperty("maxWaitMillis", "10000");
        resource.setProperty("driverClassName", driver);
        resource.setProperty("username", user);
        resource.setProperty("password", password);
        resource.setProperty("url", url);
        context.getNamingResources().addResource(resource);
    }

    private void addRealm(Context context) {
        var realm = new DataSourceRealm();
        realm.setDataSourceName("jdbc/bank");
        realm.setUserTable("users");
        realm.setUserRoleTable("user_roles");
        realm.setUserNameCol("username");
        realm.setUserCredCol("password");
        realm.setRoleNameCol("name");
        realm.setLocalDataSource(true);
        addCredentialHandler(realm);
        context.setRealm(realm);
    }

    private void addCredentialHandler(DataSourceRealm realm) {
        var handler = new CredentialHandler() {
            HashService hashService = new Argon2HashService();

            @Override
            public boolean matches(String input, String hash) {
                return hashService.matches(input, hash);
            }

            @Override
            public String mutate(String input) {
                return hashService.hashOf(input);
            }

        };
        realm.setCredentialHandler(handler);
    }

    private Tomcat setupTomcat() throws LifecycleException {
        var tomcat = new Tomcat();
        var host = config.getValue("server.host", String.class);
        var port = config.getValue("server.port", Integer.class);

        tomcat.setHostname(host);
        tomcat.setPort(port);
        tomcat.setBaseDir(BASE_DIR);
        tomcat.enableNaming();
        tomcat.getConnector();

        if (config.getValue("server.virtual_threads", Boolean.class)) {
            log.debug("Adding virtual thread executor");
            var executor = new StandardVirtualThreadExecutor();
            executor.start();
            tomcat.getConnector().getProtocolHandler().setExecutor(executor);
        }
        return tomcat;
    }

    private Context addApp(Tomcat tomcat) {
        var path = config.getValue("app.path", String.class);
        var webDirPath = new File(WEB_APP_DIR).getAbsolutePath();
        Context ctx = tomcat.addWebapp(path, webDirPath);
        WebResourceRoot webResourceRoot = new StandardRoot(ctx);
        var additionalClasses = new File(ADDITION_WEB_INF_CLASSES).getAbsolutePath();
        var resources = new DirResourceSet(webResourceRoot, WEB_APP_MOUNT, additionalClasses, INTERNAL_PATH);
        webResourceRoot.addPreResources(resources);
        ctx.setResources(webResourceRoot);
        addClassLoader(ctx);

        return ctx;
    }

    private void addClassLoader(Context context) {
        var loader = new WebappLoader();
        loader.setContext(context);
        loader.setLoaderInstance(new ParallelWebappClassLoader());
        loader.setDelegate(true);
        context.setLoader(loader);
    }
}
