package com.campus.banking.app;

import org.eclipse.microprofile.config.ConfigProvider;

public class Main {

    public static void main(String[] args) throws ServerFailureException {
        Server server = new TomcatEmbedded(ConfigProvider.getConfig());
        server.start();
    }

}