package org.cybershuttle.appserver.models;

import java.util.List;

public class AgentInfo {

    private String id;
    private String user;
    private String sessionId;
    private String sessionConsulToken;

    public AgentInfo(String id, String user, String sessionId, String sessionConsulToken){
        this.id = id;
        this.user = user;
        this.sessionId = sessionId;
        this.sessionConsulToken = sessionConsulToken;
    }

    public String getId() {
        return id;
    }

    public AgentInfo setId(String id) {
        this.id = id;
        return this;
    }


    public String getUser() {
        return user;
    }

    public AgentInfo setUser(String user) {
        this.user = user;
        return this;
    }


    public String getSessionId() {
        return sessionId;
    }


    public AgentInfo setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getSessionConsulToken() {
        return sessionConsulToken;
    }

    public AgentInfo setSessionConsulToken(String sessionConsulToken) {
        this.sessionConsulToken = sessionConsulToken;
        return this;
    }
}
