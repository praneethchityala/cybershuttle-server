package org.cybershuttle.orchestration.agent.service;

import com.hashicorp.nomad.apimodel.Job;
import com.hashicorp.nomad.apimodel.JobListStub;
import com.hashicorp.nomad.javasdk.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class NomadController {

    private static final Logger LOG = Logger.getLogger(NomadController.class.getName());

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
            LOG.log(Level.SEVERE, String.format("Error closing client: %s", e.getMessage()), e);
        }
    }

    public static boolean startJob(Job job, NomadApiClient nomadApiClient) {

        try {
            EvaluationResponse response = nomadApiClient.getJobsApi().register(job);
            LOG.log(Level.INFO, "Submitted job to nomad: " + response);
            return true;
        } catch (IOException | NomadException e) {
            LOG.log(Level.SEVERE, "Failed to submit the job: ", e);
        }
        return false;
    }

    public static boolean startJob(String jobID, NomadApiClient nomadApiClient) {

        Job job = getJob(jobID, nomadApiClient);

        if (job != null) {
            return startJob(job, nomadApiClient);
        } else {
            LOG.log(Level.INFO, "Job not found with job ID");
        }

        return false;
    }

    public static boolean killJob(String jobID, NomadApiClient nomadApiClient) {

        LOG.log(Level.INFO, "Killing Job " + jobID);
        try {
            Job nomadJob = getJob(jobID, nomadApiClient);
            if (nomadJob == null) {
                LOG.log(Level.INFO, "Cannot find the running job: " + jobID);
                return false;
            }
            nomadApiClient.getJobsApi().deregister(nomadJob.getId());
        } catch (RuntimeException | IOException | NomadException e) {
            LOG.log(Level.SEVERE, "Failed to terminate job " + jobID
                    + " with error: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public static boolean killJob(Job job, NomadApiClient nomadApiClient) {

        String jobID = job.getId();

        return killJob(jobID, nomadApiClient);
    }

    public static List<JobListStub> getJobList(NomadApiClient apiClient) {
        ServerQueryResponse<List<JobListStub>> response;
        try {
            response = apiClient.getJobsApi().list();
        } catch (IOException | NomadException e) {
            LOG.log(Level.SEVERE, "Error when attempting to fetch job list", e);
            throw new RuntimeException(e);
        }
        return response.getValue();
    }

    public static Job getJob(String jobID, NomadApiClient apiClient) {
        List<JobListStub> jobs = getJobList(apiClient);
        for (JobListStub job : jobs) {
            Job jobActual;
            try {
                jobActual = apiClient.getJobsApi().info(job.getId()).getValue();
            } catch (IOException | NomadException e) {
                String msg = "Failed to retrieve job info for job " + job.getId()
                        + " part of job " + jobID;
                LOG.log(Level.SEVERE, msg, e);
                throw new RuntimeException(msg, e);
            }
            if (jobID.equals(jobActual.getName())) {
                return jobActual;
            }
        }
        return null;
    }

//    private Map<String, String> getMetaData(Job job) {
//    }
//
//    private TaskGroup getTaskGroup(Job job) {
//
//    }
}
