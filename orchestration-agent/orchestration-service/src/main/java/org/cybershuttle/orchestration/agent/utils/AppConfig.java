package org.cybershuttle.orchestration.agent.utils;


import org.cybershuttle.orchestration.agent.JobOrchestrator;
import org.cybershuttle.orchestration.agent.ingress.ConsulClient;
import org.cybershuttle.orchestration.agent.service.NomadController;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
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

    @org.springframework.beans.factory.annotation.Value("${consul.path}")
    String sessionPath;

    @org.springframework.beans.factory.annotation.Value("${nomad.host}")
    String nomadHost;

    @Bean
    public ConsulClient consulClient() {
        return new ConsulClient(consulHost, consulPort, consulToken, sessionPath);
    }

    @Bean
    public NomadController nomadController() {
        return new NomadController(nomadHost);
    }

    @Bean
    public JobOrchestrator jobOrchestrator() {
        return new JobOrchestrator();
    }
}

