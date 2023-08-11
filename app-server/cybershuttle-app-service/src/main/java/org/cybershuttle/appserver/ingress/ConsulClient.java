package org.cybershuttle.appserver.ingress;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.*;
import com.orbitz.consul.model.acl.*;
import com.orbitz.consul.model.kv.Value;
import com.orbitz.consul.option.DeleteOptions;
import com.orbitz.consul.option.PutOptions;
import org.cybershuttle.appserver.models.AgentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.net.HostAndPort.fromParts;
@Component
public class ConsulClient {


    private static final Logger logger = LoggerFactory.getLogger(ConsulClient.class);

    @org.springframework.beans.factory.annotation.Value("${consul.host}")
    String consulHost;

    @org.springframework.beans.factory.annotation.Value("${consul.port}")
    Integer consulPort;

    @org.springframework.beans.factory.annotation.Value("${consul.token}")
    String consulToken;

    public String testConsul = "ConsulClient is reachable";

    private Consul client;
    private KeyValueClient kvClient;
    private AclClient aclClient;
    private SessionClient sessionClient;
    private ObjectMapper mapper = new ObjectMapper();

    public static final String JOB_STATE_PATH = "cs/job/state/";
    public static final String AGENTS_RPC_REQUEST_MESSAGE_PATH = "cs/agents/rpcmessages/";
    public static final String AGENTS_INFO_PATH = "cs/agents/info/";
    public static final String LIVE_AGENTS_PATH = "cs/agent/live/";

    public static final String JOB_PENDING_PATH = "cs/job/pending/";
    public static final String JOB_PROCESSED_PATH = "cs/job/processed/";
    public static final String AGENTS_JOB_REQUEST_MESSAGE_PATH = "cs/agents/jobmessages/";
    public static final String AGENTS_SCHEDULED_PATH = "cs/agents/scheduled/";
    public static final String AGENTS_PENDING_JOB_COUNT_PATH = "cs/agents/pendingjobs/";

    public static final String CONTROLLER_STATE_MESSAGE_PATH = "cs/controller/messages/states/";
    public static final String CONTROLLER_JOB_MESSAGE_PATH = "cs/controller/messages/jobs/";

//    public ConsulClient(String consulHostPorts, Integer token) {
//        List<HostAndPort> hostAndPorts = consulHostPorts.entrySet().stream()
//                .map(entry -> fromParts(entry.getKey(), entry.getValue()))
//                .collect(Collectors.toList());
//        this.client = Consul.builder().withMultipleHostAndPort(hostAndPorts, 100000)
//                .withTokenAuth(token)
//                .withReadTimeoutMillis(11000).withWriteTimeoutMillis(1000).build();
//        this.kvClient = client.keyValueClient();
//        this.aclClient = client.aclClient();
//        this.sessionClient = client.sessionClient();
//    }

    public ConsulClient(String host, int port, String token) {
        this.client = Consul.builder().withHostAndPort(HostAndPort.fromParts(host, port)).withTokenAuth(token).build();
        this.kvClient = client.keyValueClient();
        this.aclClient = client.aclClient();
        this.sessionClient = client.sessionClient();
    }


//    public ConsulClient ConsulClient() {
//        return new ConsulClient(consulHost, consulPort, consulToken);
//    }

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


    public synchronized void updateAgentPendingJobCount(String agentId, long jobCount) {
        try {
            kvClient.putValue(AGENTS_PENDING_JOB_COUNT_PATH + agentId, jobCount + "");
        } catch (Exception e) {
            logger.error("Failed update pending job count {} in consul for agent {}. But Continuing the execution",
                    jobCount, agentId);
        }
    }

    public long getAgentPendingJobCount(String agentId) {
        try {
            Optional<Value> valueOp = kvClient.getValue(AGENTS_PENDING_JOB_COUNT_PATH + agentId);
            if (valueOp.isEmpty()) {
                return 0;
            }
            String countAsStr = valueOp.get().getValueAsString().get();
            return Long.parseLong(countAsStr);
        } catch (ConsulException e) {
            if (e.getCode() == 404) {
                return 0;
            } else {
                throw e;
            }
        }
    }

    public String submitJob(String jobRequest) throws Exception {
        try {
            String jobId = UUID.randomUUID().toString();
            kvClient.putValue(CONTROLLER_JOB_MESSAGE_PATH + jobId, Arrays.toString(jobRequest.toCharArray()),
                    0L, PutOptions.BLANK);
            return jobId;
        } catch (Exception e) {
            throw new Exception("Error in serializing job request", e);
        }
    }

    public PolicyResponse createPolicy(String name, String description, String rules){

        ImmutablePolicy newPolicy = ImmutablePolicy.builder().name(name)
                .description(description).rules(rules).build();

        PolicyResponse newPolicyResponse = aclClient.createPolicy(newPolicy);

        return newPolicyResponse;

    }

    public TokenResponse createToken(PolicyResponse policyResponse){

        ImmutablePolicyLink newPolicyLink = ImmutablePolicyLink.builder()
                .id(policyResponse.id()).name(policyResponse.name()).build();

        ImmutableToken newToken = ImmutableToken.builder()
                .addPolicies(newPolicyLink).build();

        TokenResponse newTokenResponse = aclClient.createToken(newToken);

        return newTokenResponse;
    }

//    /**
//     * Submits a {@link JobApiRequest} to a target agent
//     *
//     * @param agentId Agent Id
//     * @param jobRequest Target job request
//     * @throws Exception If {@link JobApiRequest} can not be delivered to consul store
//     */
//    public void commandJobToAgent(String agentId, String jobId, AgentJobRequest jobRequest)
//            throws Exception {
//        try {
//            byte[] jobReqBytes = jobRequest.toByteArray();
//            kvClient.putValue(AGENTS_JOB_REQUEST_MESSAGE_PATH + agentId + "/" + jobId + "/" + jobRequest.getRequestId(), jobReqBytes,
//                    0L, PutOptions.BLANK);
//
//        } catch (Exception e) {
//            throw new Exception("Error in submitting job command to Agent through consul", e);
//        }
//    }

    public List<String> listPendingAgentJobs(String agentId) throws Exception  {
        try {
            try {
                return kvClient.getKeys(AGENTS_JOB_REQUEST_MESSAGE_PATH + agentId);
            } catch (ConsulException e) {
                if (e.getCode() == 404) {
                    return Collections.emptyList();
                } else {
                    throw e;
                }
            }
        } catch (Exception e) {
            throw new Exception("Failed to list pending agent jobs for agent " + agentId, e);
        }
    }

//    public void sendSyncRPCToAgent(String agentId, SyncRPCRequest rpcRequest) throws Exception {
//        try {
//            String asString = mapper.writeValueAsString(rpcRequest);
//            kvClient.putValue(AGENTS_RPC_REQUEST_MESSAGE_PATH + agentId + "/" + rpcRequest.getMessageId(), asString);
//        } catch (JsonProcessingException e) {
//            throw new Exception("Error in serializing rpc request", e);
//        }
//    }
//
//    public void sendSyncRPCResponseFromAgent(String returnAddress, SyncRPCResponse rpcResponse) throws Exception {
//        try {
//            String asString = mapper.writeValueAsString(rpcResponse);
//            kvClient.putValue(returnAddress, asString);
//        } catch (JsonProcessingException e) {
//            throw new Exception("Error in serializing rpc response", e);
//        }
//    }

    /**
     * List all currently registered agents.
     *
     * @return A list of {@link AgentInfo}
     */
    public List<AgentInfo> listAgents() {
        List<AgentInfo> agents = new ArrayList<>();
        List<String> keys = kvClient.getKeys(AGENTS_INFO_PATH);
        for (String key : keys) {
            Optional<AgentInfo> agentInfo = getAgentInfo(key.substring(key.lastIndexOf("/") + 1));
            agentInfo.ifPresent(agents::add);
        }
        return agents;
    }

    /**
     * Get the {@link AgentInfo} for a given agent id
     * @param agentId Agent Id
     * @return AgentInfo if such agent is available
     */
    public Optional<AgentInfo> getAgentInfo(String agentId) {
        Optional<Value> value = kvClient.getValue(AGENTS_INFO_PATH + agentId);
        if (value.isPresent()) {
            Value absVal = value.get();
            if (absVal.getValue().isPresent()) {
                String asStr = absVal.getValueAsString().get();
                try {
                    return Optional.of(mapper.readValue(asStr, AgentInfo.class));
                } catch (IOException e) {
                    logger.error("Errored while fetching agent {} info", agentId, e);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Agents are supposed to register themselves in MFT using this method
     *
     * @param agentInfo {@link AgentInfo} of the source Agents
     * @throws Exception If {@link AgentInfo} can not be saved in consul store
     */
    public void registerAgent(AgentInfo agentInfo) throws Exception {
        try {
            String asString = mapper.writeValueAsString(agentInfo);
            kvClient.putValue(AGENTS_INFO_PATH + agentInfo.getId(), asString);
        } catch (JsonProcessingException e) {
            throw new Exception("Error in serializing agent information", e);
        }
    }

    /**
     * List all currently live agents
     *
     * @return A list of live agent ids
     * @throws Exception If live agents can not be fetched from consul store
     */
    public List<String> getLiveAgentIds() throws Exception {
        try {
            List<String> keys = kvClient.getKeys(LIVE_AGENTS_PATH);
            return keys.stream().map(key -> key.substring(key.lastIndexOf("/") + 1)).collect(Collectors.toList());
        } catch (ConsulException e) {
            if (e.getCode() == 404) {
                return Collections.emptyList();
            }
            throw new Exception("Error in fetching live agents", e);
        } catch (Exception e) {
            throw new Exception("Error in fetching live agents", e);
        }
    }

    /**
     * Lists all currently processing job id for the given agent
     *
     * @param agentInfo
     * @return
     * @throws Exception
     */
    public List<String> getAgentActiveJobs(AgentInfo agentInfo) throws Exception {
        try {
            List<String> keys = kvClient.getKeys(AGENTS_SCHEDULED_PATH + agentInfo.getId() + "/" + agentInfo.getSessionId());
            return keys.stream().map(key -> key.substring(key.lastIndexOf("/") + 1)).collect(Collectors.toList());
        } catch (ConsulException e) {
            if (e.getCode() == 404) {
                return Collections.emptyList();
            }
            throw new Exception("Error in fetching active jobs for agent " + agentInfo.getId(), e);
        } catch (Exception e) {
            throw new Exception("Error in fetching active jobs for agent " + agentInfo.getId(), e);
        }
    }

    public int getEndpointHookCountForAgent(String agentId) throws Exception {
        Optional<String> sessionOp = getKvClient().getSession(LIVE_AGENTS_PATH + agentId);

        try {
            try {
                if (sessionOp.isPresent()) {
                    List<String> jobs = getKvClient().getKeys(AGENTS_SCHEDULED_PATH + agentId + "/" + sessionOp.get());
                    return jobs.size();
                } else {
                    return 0;
                }
            } catch (ConsulException e) {
                if (e.getCode() == 404) {
                    return 0;
                } else {
                    throw e;
                }
            }
        } catch (Exception e) {
            throw new Exception("Failed to fetch endpoint hook count for agent " + agentId, e);
        }
    }
//    /**
//     * Agents should call this method to submit {@link JobState}. These status are received by the controller and reorder
//     * status messages and put in the final status array.
//     *
//     * @param jobId
//     * @param agentId
//     * @param jobState
//     * @throws Exception
//     */
//    public void submitFileJobStateToProcess(String jobId, String agentRequestId,
//                                                 EndpointPaths endpointPath,
//                                                 String agentId, JobState jobState) throws Exception {
//        try {
//
//            String pathMD5 = getEndpointPathHash(endpointPath);
//            kvClient.putValue(CONTROLLER_STATE_MESSAGE_PATH + jobId + "/" + agentId + "/" + agentRequestId + "/" + pathMD5 + "/" + jobState.getUpdateTimeMils(),
//                    mapper.writeValueAsString(jobState));
//        } catch (Exception e) {
//            logger.error("Error in submitting job status to process for job {} and agent {}", jobId, agentId, e);
//            throw new Exception("Error in submitting job status", e);
//        }
//    }
//
//    public String getEndpointPathHash(EndpointPaths endpointPath) {
//        return DigestUtils.md5DigestAsHex((endpointPath.getSourcePath() + ":" + endpointPath.getDestinationPath()).getBytes());
//    }
//
//    public String getEndpointPathHash(org.apache.airavata.mft.api.service.EndpointPaths endpointPath) {
//        return DigestUtils.md5DigestAsHex((endpointPath.getSourcePath() + ":" + endpointPath.getDestinationPath()).getBytes());
//    }
//
//    /**
//     * Add the {@link JobState} to the aggregated state array. This method should only be called by the
//     * Controller and API server once the job is accepted. Agents should NEVER call this method as it would corrupt
//     * state array when multiple clients are writing at the same time
//     *
//     * @param jobId
//     * @param jobState
//     * @throws Exception
//     */
//    public void saveJobState(String jobId, String agentRequestId, JobState jobState) throws Exception {
//        try {
//            String asStr = mapper.writeValueAsString(jobState);
//            if (agentRequestId == null) {
//                kvClient.putValue(JOB_STATE_PATH + jobId + "/" + UUID.randomUUID().toString(), asStr);
//            } else {
//                kvClient.putValue(JOB_STATE_PATH + jobId + "/" + agentRequestId + "/" + UUID.randomUUID().toString(), asStr);
//            }
//            logger.info("Saved job status " + asStr);
//
//        } catch (Exception e) {
//            throw new Exception("Error in serializing job status", e);
//        }
//    }
//
//    /**
//     * Get the latest {@link JobState} for given job id
//     *
//     * @param jobId Job Id
//     * @return Optional {@link JobState } is there is any
//     * @throws Exception
//     */
//    public Optional<JobState> getLastJobState(String jobId) throws Exception {
//
//        try {
//            List<JobState> states = getJobStates(jobId);
//
//            Optional<JobState> lastStatusOp = states.stream().min((o1, o2) -> {
//                if (o1.getUpdateTimeMils() == o2.getUpdateTimeMils()) {
//                    return 0;
//                } else {
//                    return o1.getUpdateTimeMils() - o2.getUpdateTimeMils() < 0 ? 1 : -1;
//                }
//            });
//
//            return lastStatusOp;
//
//        } catch (ConsulException e) {
//            throw new Exception("Error in fetching job status " + jobId, e);
//        } catch (Exception e) {
//            throw new Exception("Error in fetching job status " + jobId, e);
//        }
//    }
//
//    /**
//     * Provide all {@link JobState} for given job id
//     *
//     * @param jobId Job Id
//     * @return The list of all {@link JobState}
//     * @throws IOException
//     */
//    public List<JobState> getJobStates(String jobId) throws IOException {
//        return getJobStates(jobId, null);
//    }
//
//    public List<JobState> getJobStates(String jobId, String agentRequestId) throws IOException {
//        List<String> keys = kvClient.getKeys(JOB_STATE_PATH + jobId + (agentRequestId == null? "" : "/" + agentRequestId));
//
//        List<JobState> allStates = new ArrayList<>();
//
//        for (String key: keys) {
//            Optional<Value> valueOp = kvClient.getValue(key);
//            String stateAsStr = valueOp.get().getValueAsString().get();
//            JobState jobState = mapper.readValue(stateAsStr, JobState.class);
//            allStates.add(jobState);
//        }
//        List<JobState> sortedStates = allStates.stream().sorted((o1, o2) ->
//                (o1.getUpdateTimeMils() - o2.getUpdateTimeMils()) < 0 ? -1 :
//                        (o1.getUpdateTimeMils() - o2.getUpdateTimeMils()) == 0 ? 0 : 1).collect(Collectors.toList());
//        return sortedStates;
//    }
//
//    public void markJobAsProcessed(String jobId, JobApiRequest jobRequest) {
//        kvClient.putValue(JOB_PROCESSED_PATH + jobId,
//                jobRequest.toByteArray(), 0L, PutOptions.BLANK);
//    }
//    public Optional<JobApiRequest> getProcessedJob(String jobId) throws InvalidProtocolBufferException {
//        Optional<Value> value = kvClient.getValue(JOB_PROCESSED_PATH + jobId);
//        if (value.isPresent()) {
//            return Optional.of(JobApiRequest.newBuilder().mergeFrom(value.get().getValueAsBytes().get()).build());
//        } else {
//            return Optional.empty();
//        }
//    }

    public void removeJob(String jobId) {
        kvClient.deleteKey(JOB_STATE_PATH + jobId, DeleteOptions.RECURSE);
        kvClient.deleteKey(JOB_PROCESSED_PATH + jobId, DeleteOptions.RECURSE);
    }

    public List<AgentInfo> getLiveAgentInfos() throws Exception {
        List<String> liveAgentIds = getLiveAgentIds();
        return liveAgentIds.stream().map(id -> getAgentInfo(id).get()).collect(Collectors.toList());
    }

}
