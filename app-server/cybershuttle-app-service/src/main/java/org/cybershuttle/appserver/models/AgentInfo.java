package org.cybershuttle.appserver.models;

import java.util.List;

public class AgentInfo {

    private String id;
    private String host;
    private String user;
    private boolean sudo;
    private String sessionId;
    private List<String> supportedProtocols;
    private List<String> localStorages;

    public String getId() {
        return id;
    }

    public AgentInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getHost() {
        return host;
    }

    public AgentInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUser() {
        return user;
    }

    public AgentInfo setUser(String user) {
        this.user = user;
        return this;
    }

    public boolean isSudo() {
        return sudo;
    }

    public AgentInfo setSudo(boolean sudo) {
        this.sudo = sudo;
        return this;
    }

    public List<String> getSupportedProtocols() {
        return supportedProtocols;
    }

    public AgentInfo setSupportedProtocols(List<String> supportedProtocols) {
        this.supportedProtocols = supportedProtocols;
        return this;
    }

    public List<String> getLocalStorages() {
        return localStorages;
    }

    public AgentInfo setLocalStorages(List<String> localStorages) {
        this.localStorages = localStorages;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public AgentInfo setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }
}
