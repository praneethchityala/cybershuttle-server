package org.cybershuttle.orchestration.agent.ingress;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.*;
import com.orbitz.consul.model.acl.*;
import com.orbitz.consul.model.kv.Value;
import com.orbitz.consul.option.PutOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ConsulClient {


    private static final Logger logger = LoggerFactory.getLogger(ConsulClient.class);
//
//    @org.springframework.beans.factory.annotation.Value("${consul.host}")
//    String consulHost;
//
//    @org.springframework.beans.factory.annotation.Value("${consul.port}")
//    Integer consulPort;
//
//    @org.springframework.beans.factory.annotation.Value("${consul.token}")
//    String consulToken;

//    @org.springframework.beans.factory.annotation.Value("${consul.path}")
//    String sessionPath;

    private String sessionPath;

    private Consul client;
    private KeyValueClient kvClient;
    private AclClient aclClient;

    public String getSessionPath() {
        return sessionPath;
    }

    private SessionClient sessionClient;
    private ObjectMapper mapper = new ObjectMapper();

    public static final String JOBS_PENDING_PATH = "cs/job/pending/";
    public static final String JOBS_PROCESSING_PATH = "cs/job/processing/";
    public static final String JOBS_SCHEDULED_PATH = "cs/job/scheduled/";
    public static final String JOBS_REMOVE_PATH = "cs/job/remove/";


    public ConsulClient(String host, int port, String token, String sessionPath) {
        this.client = Consul.builder().withHostAndPort(HostAndPort.fromParts(host, port)).withTokenAuth(token).build();
        this.kvClient = this.client.keyValueClient();
        this.aclClient = this.client.aclClient();
        this.sessionClient = this.client.sessionClient();
        this.sessionPath = sessionPath;
    }


    public ConsulClient() {
    }

    public KeyValueClient getKvClient() {
        return kvClient;
    }

    public AclClient getAclClient() {
        return aclClient;
    }

    public SessionClient getSessionClient() {
        return sessionClient;
    }


    public List<String> getScheduleJobs() {
        List<String> valueOp;
        try {
            valueOp = kvClient.getKeys(sessionPath +"/"+ JOBS_SCHEDULED_PATH);
            return valueOp;
        } catch (ConsulException e) {
                throw e;
        }
    }

    public Optional<Value> getScheduleJob(String jobID) {
        Optional<Value> valueOp;
        try {
            valueOp = kvClient.getValue(sessionPath +"/"+ JOBS_SCHEDULED_PATH + jobID);
            return valueOp;
        } catch (ConsulException e) {
            throw e;
        }
    }

    public List<String> getRemoveJobs() {
        List<String> valueOp;
        try {
            valueOp = kvClient.getKeys(sessionPath +"/"+ JOBS_REMOVE_PATH);
            return valueOp;
        } catch (ConsulException e) {
            throw e;
        }
    }

    public Optional<Value> getRemoveJob(String jobID) {
        Optional<Value> valueOp;
        try {
            valueOp = kvClient.getValue(sessionPath +"/"+ JOBS_REMOVE_PATH + jobID);
            return valueOp;
        } catch (ConsulException e) {
            throw e;
        }
    }

    public String addProcessingJob(String jobId, String jobRequest) throws Exception {
        try {
            kvClient.putValue(sessionPath +"/"+ JOBS_PROCESSING_PATH + jobId, jobRequest,
                    0L, PutOptions.BLANK);
            return jobId;
        } catch (Exception e) {
            throw new Exception("Error in serializing job request", e);
        }
    }

    public void deleteScheduleJob(String key) throws Exception {
        try {
            kvClient.deleteKey(sessionPath +"/"+ JOBS_SCHEDULED_PATH + key);
        } catch (Exception e) {
            throw new Exception("Error in serializing job request", e);
        }
    }

    public void deleteRemoveJob(String key) throws Exception {
        try {
            kvClient.deleteKey(sessionPath +"/"+ JOBS_REMOVE_PATH + key);
        } catch (Exception e) {
            throw new Exception("Error in serializing job request", e);
        }
    }

    public void deleteProcessingJob(String key) throws Exception {
        try {
            kvClient.deleteKey(sessionPath +"/"+ JOBS_PROCESSING_PATH + key);
        } catch (Exception e) {
            throw new Exception("Error in serializing job request", e);
        }
    }

}

