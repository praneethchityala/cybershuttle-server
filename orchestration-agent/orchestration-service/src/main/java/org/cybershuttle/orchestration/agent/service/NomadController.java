package org.cybershuttle.orchestration.agent.service;

import com.google.common.net.HostAndPort;
import com.hashicorp.nomad.apimodel.Job;
import com.hashicorp.nomad.apimodel.JobListStub;
import com.hashicorp.nomad.javasdk.*;
import com.orbitz.consul.Consul;
import org.cybershuttle.orchestration.agent.JobOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;


@Component
public class NomadController {

//    private static final Logger LOG = Logger.getLogger(NomadController.class.getName());
    private static final Logger logger = LoggerFactory.getLogger(NomadController.class);

    private NomadApiClient nomadApiClient;

    public NomadController(String host) {
        NomadApiConfiguration config =
                new NomadApiConfiguration.Builder()
                        .setAddress(host)
                        .build();

         this.nomadApiClient = new NomadApiClient(config);
    }


    public NomadController() {
    }

    public static NomadApiClient createConnection(String host) {
        NomadApiConfiguration config =
                new NomadApiConfiguration.Builder()
                        .setAddress(host)
                        .build();

        NomadApiClient apiClient = new NomadApiClient(config);

        return apiClient;
    }

    public static void closeClient(NomadApiClient nomadApiClient) {
        try {
            if (nomadApiClient != null) {
                nomadApiClient.close();
            }
        } catch (IOException e) {
            logger.error("Error closing client: %s", e.getMessage());
        }
    }

    public boolean startJob(Job job) {

        try {
            EvaluationResponse response = nomadApiClient.getJobsApi().register(job);
            logger.info("Submitted job to nomad: " + response);
            return true;
        } catch (IOException | NomadException e) {
            logger.error("Failed to submit the job: ", e);
        }
        return false;
    }

    public boolean startJob(String jobID) {

        Job job = getJob(jobID);

        if (job != null) {
            return startJob(job);
        } else {
            logger.info("Job not found with job ID");
        }

        return false;
    }

    public boolean killJob(String jobID) {

        logger.info("Killing Job " + jobID);
        try {
            Job nomadJob = getJob(jobID);
            if (nomadJob == null) {
                logger.info("Cannot find the running job: " + jobID);
                return false;
            }
            nomadApiClient.getJobsApi().deregister(nomadJob.getId());
        } catch (RuntimeException | IOException | NomadException e) {
            logger.error("Failed to terminate job " + jobID
                    + " with error: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public boolean killJob(Job job) {

        String jobID = job.getId();

        return killJob(jobID);
    }

    public List<JobListStub> getJobList() {
        ServerQueryResponse<List<JobListStub>> response;
        try {
            response = nomadApiClient.getJobsApi().list();
        } catch (IOException | NomadException e) {
            logger.error("Error when attempting to fetch job list", e);
            throw new RuntimeException(e);
        }
        return response.getValue();
    }

    public Job getJob(String jobID) {
        List<JobListStub> jobs = getJobList();
        for (JobListStub job : jobs) {
            Job jobActual;
            try {
                jobActual = nomadApiClient.getJobsApi().info(job.getId()).getValue();
            } catch (IOException | NomadException e) {
                String msg = "Failed to retrieve job info for job " + job.getId()
                        + " part of job " + jobID;
                logger.error(msg, e);
                throw new RuntimeException(msg, e);
            }
            if (jobID.equals(jobActual.getId())) {
                return jobActual;
            }
        }
        return null;
    }
}
