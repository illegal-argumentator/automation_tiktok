package com.yves_gendron.automation_tiktok.postconfig;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowServerUrl implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ShowServerUrl.class);
    private final Environment env;

    @Override
    public void run(String... args) throws Exception {
        String serverAddress = env.getProperty("server.address", "localhost");
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");

        String serverUrl = String.join("", "http://", serverAddress, ":", serverPort, contextPath);
        LOG.info("\n\nServer url: " + serverUrl + "\n");
    }
}
