package com.campus.banking.app;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardVirtualThreadExecutor;
import org.apache.catalina.loader.ParallelWebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.eclipse.microprofile.config.Config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TomcatEmbedded implements Server {

    private Config config;

    private static final String ADDITION_WEB_INF_CLASSES = "target/classes";
    private static final String WEB_APP_MOUNT = "/WEB-INF/classes";
    private static final String INTERNAL_PATH = "/";
    private static final String WEB_APP_DIR = "src/main/webapp/";;
    private static final String BASE_DIR = "/tmp";;

    public TomcatEmbedded(Config config) {
        this.config = config;
    }

    @Override
    public void start() throws ServerFailureException {
        log.debug("Starting Tomcat...");
        try {
            var tomcat = setupTomcat();
            var context = addApp(tomcat);
            tomcat.start();

            var classLoader = (ParallelWebappClassLoader) context.getLoader().getClassLoader();
            classLoader.setDelegate(true);

            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new ServerFailureException(e);
        }
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
        
        if (config.getValue("server.virtual_threads", Boolean.class)){
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
