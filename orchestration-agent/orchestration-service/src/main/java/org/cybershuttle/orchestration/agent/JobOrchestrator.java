package org.cybershuttle.orchestration.agent;

import com.hashicorp.nomad.apimodel.Job;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.KVCache;
import com.orbitz.consul.model.kv.Value;
import org.cybershuttle.orchestration.agent.ingress.ConsulClient;
import org.cybershuttle.orchestration.agent.service.NomadController;
import org.cybershuttle.orchestration.agent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Component
public class JobOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(JobOrchestrator.class);

    @Autowired
    private NomadController nomadController;

    @Autowired
    private ConsulClient consulClient;

    private KVCache scheduleJobsCache;
    private KVCache removeJobsCache;

    private ConsulCache.Listener<String, Value> scheduleJobsCacheListener;
    private ConsulCache.Listener<String, Value> removeJobsCacheListener;

    @PostConstruct
    public void init() {
        scheduleJobsCache = KVCache.newCache(consulClient.getKvClient(), consulClient.getSessionPath() + "/"+ consulClient.JOBS_SCHEDULED_PATH, 9);
        removeJobsCache = KVCache.newCache(consulClient.getKvClient(), consulClient.getSessionPath() + "/"+ consulClient.JOBS_REMOVE_PATH, 9);

        setScheduleJobsListener();
        setRemoveJobsListener();
    }

    public void setScheduleJobsListener(){
        scheduleJobsCacheListener = newValues -> {
            newValues.values().forEach(value -> {
                String key = value.getKey();
                String jobString = value.getValueAsString().get();
                try {
                    scheduleJob(key, jobString);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                logger.info("scheduling Job for key: "+key);
            });
        };
        scheduleJobsCache.addListener(scheduleJobsCacheListener);
        scheduleJobsCache.start();
    }

    public void setRemoveJobsListener(){
        removeJobsCacheListener = newValues -> {
            newValues.values().forEach(value -> {
                logger.info("removing Job for value: "+value);
                String key = value.getKey();
                String jobString = value.getValueAsString().get();
                try {
                    removeJob(key, jobString);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                logger.info("removing Job for key: "+key);
            });
        };
        removeJobsCache.addListener(removeJobsCacheListener);
        removeJobsCache.start();
    }

    @PreDestroy
    public void destroy() {
        scheduleJobsCache.removeListener(scheduleJobsCacheListener);
        removeJobsCache.removeListener(removeJobsCacheListener);
        logger.info("Transfer orchestrator turned off");
    }

    public void scheduleJob(String key, String value) throws Exception {

        Job newJob = Job.fromJson(value);
        String jobId = key.substring(key.lastIndexOf("/")+1,key.length());
        newJob = newJob.setId(jobId);
        Boolean startedJob = nomadController.startJob(newJob);

        if(startedJob){
            consulClient.deleteScheduleJob(jobId);
            consulClient.addProcessingJob(newJob.getId(),newJob.getId());
        }
    }

    public void removeJob(String key, String value) throws Exception {

        Boolean killedJob = nomadController.killJob(value);

        if(killedJob){
            consulClient.deleteProcessingJob(value);
            consulClient.deleteRemoveJob(value);
        }
    }

}
