# cybershuttle-server

## Project Prerequisites

make sure you have envoy installed.
To install envoy follow: https://www.envoyproxy.io/docs/envoy/latest/start/install

You need to have consul run with a binary
Follow this link to get consul binary: https://developer.hashicorp.com/consul/docs/install#precompiled-binaries

## Project setup

Run the envoy.yaml using below command
```
envoy -c envoy.yaml
```

Run consul using below command <<make sure to use the config files in consul folder>>
```
/consul agent -dev -config-dir=/consul/config
```

After starting consul use below command to generate consul token to be used by cybershuttle-server with complete access
```
./consul acl bootstrap
```
Use the Secret-ID generated from above command in cybershuttle-server for complete access to consul.

### Customize configuration
app-server/cybershuttle-app-service/src/main/resources/application.properties
