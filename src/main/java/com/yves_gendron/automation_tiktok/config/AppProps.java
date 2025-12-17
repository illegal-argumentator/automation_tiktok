package com.yves_gendron.automation_tiktok.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties("app.props")
public class AppProps {

    private String accountsPassword;

    private int processingThreads;

    private int accountsPerProxy;
}
