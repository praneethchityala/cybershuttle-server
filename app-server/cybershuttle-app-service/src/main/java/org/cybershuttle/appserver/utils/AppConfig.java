package org.cybershuttle.appserver.utils;

import org.cybershuttle.appserver.ingress.ConsulClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("AppConfig")
public class AppConfig {

    @org.springframework.beans.factory.annotation.Value("${consul.host}")
    String consulHost;

    @org.springframework.beans.factory.annotation.Value("${consul.port}")
    Integer consulPort;

    @org.springframework.beans.factory.annotation.Value("${consul.token}")
    String consulToken;

    @Bean
    public ConsulClient consulClient() {
        return new ConsulClient(consulHost, consulPort, consulToken);
    }
}
