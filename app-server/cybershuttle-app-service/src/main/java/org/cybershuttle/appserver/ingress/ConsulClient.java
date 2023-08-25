package org.cybershuttle.appserver.ingress;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.*;
import com.orbitz.consul.model.acl.*;
import com.orbitz.consul.model.kv.Value;
import com.orbitz.consul.option.PutOptions;
import org.cybershuttle.appserver.models.AgentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    public static final String JOBS_SCHEDULED_PATH = "cs/job/scheduled/";
    public static final String JOBS_REMOVE_PATH = "cs/job/remove/";


    public ConsulClient(String host, int port, String token) {
        this.client = Consul.builder().withHostAndPort(HostAndPort.fromParts(host, port)).withTokenAuth(token).build();
        this.kvClient = this.client.keyValueClient();
        this.aclClient = this.client.aclClient();
        this.sessionClient = this.client.sessionClient();
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

    public String submitJob(String sessionId, String jobId, String jobRequest) throws Exception {
        try {
            kvClient.putValue(sessionId +"/"+ JOBS_SCHEDULED_PATH + jobId, jobRequest,
                    0L, PutOptions.BLANK);
            return jobId;
        } catch (Exception e) {
            throw new Exception("Error in serializing job request", e);
        }
    }

    public String removeJob(String sessionId, String jobId) throws Exception {
        try {
            kvClient.putValue(sessionId +"/"+ JOBS_REMOVE_PATH, jobId,
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

    public PolicyResponse createWritePrefixPolicy(String name, String description, String prefix){

        String rule = "key_prefix \""+prefix+"\" {\n" +

                "  policy = \"write\"\n" +
                "}";

        return createPolicy(name,description,rule);

    }

    public TokenResponse createToken(PolicyResponse policyResponse){

        ImmutablePolicyLink newPolicyLink = ImmutablePolicyLink.builder()
                .id(policyResponse.id()).name(policyResponse.name()).build();

        ImmutableToken newToken = ImmutableToken.builder()
                .addPolicies(newPolicyLink).build();

        TokenResponse newTokenResponse = aclClient.createToken(newToken);

        return newTokenResponse;
    }

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


    public List<AgentInfo> getLiveAgentInfos() throws Exception {
        List<String> liveAgentIds = getLiveAgentIds();
        return liveAgentIds.stream().map(id -> getAgentInfo(id).get()).collect(Collectors.toList());
    }

}
