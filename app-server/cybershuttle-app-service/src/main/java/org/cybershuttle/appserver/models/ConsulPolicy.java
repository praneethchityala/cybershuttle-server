package org.cybershuttle.appserver.models;

import com.orbitz.consul.model.acl.Policy;

import java.util.List;
import java.util.Optional;

public class ConsulPolicy extends Policy {


    @Override
    public Optional<String> id() {
        return Optional.empty();
    }

    @Override
    public Optional<String> description() {
        return Optional.empty();
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Optional<String> rules() {
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> datacenters() {
        return Optional.empty();
    }
}
