package org.cybershuttle.appserver.utils;

public class StaticData {

    public static String serverNomadJson = "{\n" +
            "  \"Stop\": false,\n" +
            "  \"Region\": \"global\",\n" +
            "  \"Namespace\": \"default\",\n" +
            "  \"ID\": \"hello-world-json\",\n" +
            "  \"ParentID\": \"\",\n" +
            "  \"Name\": \"hello-world-json\",\n" +
            "  \"Type\": \"service\",\n" +
            "  \"Priority\": 50,\n" +
            "  \"AllAtOnce\": false,\n" +
            "  \"Datacenters\": [\n" +
            "    \"*\"\n" +
            "  ],\n" +
            "  \"Constraints\": null,\n" +
            "  \"Affinities\": null,\n" +
            "  \"Spreads\": null,\n" +
            "  \"TaskGroups\": [\n" +
            "    {\n" +
            "      \"Name\": \"servers\",\n" +
            "      \"Count\": 1,\n" +
            "      \"Update\": {\n" +
            "        \"Stagger\": 30000000000,\n" +
            "        \"MaxParallel\": 1,\n" +
            "        \"HealthCheck\": \"checks\",\n" +
            "        \"MinHealthyTime\": 10000000000,\n" +
            "        \"HealthyDeadline\": 300000000000,\n" +
            "        \"ProgressDeadline\": 600000000000,\n" +
            "        \"AutoRevert\": false,\n" +
            "        \"AutoPromote\": false,\n" +
            "        \"Canary\": 0\n" +
            "      },\n" +
            "      \"Migrate\": {\n" +
            "        \"MaxParallel\": 1,\n" +
            "        \"HealthCheck\": \"checks\",\n" +
            "        \"MinHealthyTime\": 10000000000,\n" +
            "        \"HealthyDeadline\": 300000000000\n" +
            "      },\n" +
            "      \"Constraints\": [\n" +
            "        {\n" +
            "          \"LTarget\": \"${attr.nomad.service_discovery}\",\n" +
            "          \"RTarget\": \"true\",\n" +
            "          \"Operand\": \"=\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"Scaling\": null,\n" +
            "      \"RestartPolicy\": {\n" +
            "        \"Attempts\": 2,\n" +
            "        \"Interval\": 1800000000000,\n" +
            "        \"Delay\": 15000000000,\n" +
            "        \"Mode\": \"fail\"\n" +
            "      },\n" +
            "      \"Tasks\": [\n" +
            "        {\n" +
            "          \"Name\": \"web\",\n" +
            "          \"Driver\": \"docker\",\n" +
            "          \"User\": \"\",\n" +
            "          \"Config\": {\n" +
            "            \"ports\": [\n" +
            "              \"www\"\n" +
            "            ],\n" +
            "            \"image\": \"busybox:1\",\n" +
            "            \"command\": \"httpd\",\n" +
            "            \"args\": [\n" +
            "              \"-v\",\n" +
            "              \"-f\",\n" +
            "              \"-p\",\n" +
            "              \"${NOMAD_PORT_www}\",\n" +
            "              \"-h\",\n" +
            "              \"/local\"\n" +
            "            ]\n" +
            "          },\n" +
            "          \"Env\": null,\n" +
            "          \"Services\": null,\n" +
            "          \"Vault\": null,\n" +
            "          \"Templates\": [\n" +
            "            {\n" +
            "              \"SourcePath\": \"\",\n" +
            "              \"DestPath\": \"local/index.html\",\n" +
            "              \"EmbeddedTmpl\": \"                        <h1>Hello, Nomad!</h1>\\n                        <ul>\\n                          <li>Task: {{env \\\"NOMAD_TASK_NAME\\\"}}</li>\\n                          <li>Group: {{env \\\"NOMAD_GROUP_NAME\\\"}}</li>\\n                          <li>Job: {{env \\\"NOMAD_JOB_NAME\\\"}}</li>\\n                          <li>Metadata value for foo: {{env \\\"NOMAD_META_foo\\\"}}</li>\\n                          <li>Currently running on port: {{env \\\"NOMAD_PORT_www\\\"}}</li>\\n                        </ul>\\n\",\n" +
            "              \"ChangeMode\": \"restart\",\n" +
            "              \"ChangeSignal\": \"\",\n" +
            "              \"ChangeScript\": null,\n" +
            "              \"Splay\": 5000000000,\n" +
            "              \"Perms\": \"0644\",\n" +
            "              \"Uid\": null,\n" +
            "              \"Gid\": null,\n" +
            "              \"LeftDelim\": \"{{\",\n" +
            "              \"RightDelim\": \"}}\",\n" +
            "              \"Envvars\": false,\n" +
            "              \"VaultGrace\": 0,\n" +
            "              \"Wait\": null,\n" +
            "              \"ErrMissingKey\": false\n" +
            "            }\n" +
            "          ],\n" +
            "          \"Constraints\": null,\n" +
            "          \"Affinities\": null,\n" +
            "          \"Resources\": {\n" +
            "            \"CPU\": 50,\n" +
            "            \"Cores\": 0,\n" +
            "            \"MemoryMB\": 64,\n" +
            "            \"MemoryMaxMB\": 0,\n" +
            "            \"DiskMB\": 0,\n" +
            "            \"IOPS\": 0,\n" +
            "            \"Networks\": null,\n" +
            "            \"Devices\": null\n" +
            "          },\n" +
            "          \"RestartPolicy\": {\n" +
            "            \"Attempts\": 2,\n" +
            "            \"Interval\": 1800000000000,\n" +
            "            \"Delay\": 15000000000,\n" +
            "            \"Mode\": \"fail\"\n" +
            "          },\n" +
            "          \"DispatchPayload\": null,\n" +
            "          \"Lifecycle\": null,\n" +
            "          \"Meta\": null,\n" +
            "          \"KillTimeout\": 5000000000,\n" +
            "          \"LogConfig\": {\n" +
            "            \"MaxFiles\": 10,\n" +
            "            \"MaxFileSizeMB\": 10,\n" +
            "            \"Disabled\": false\n" +
            "          },\n" +
            "          \"Artifacts\": null,\n" +
            "          \"Leader\": false,\n" +
            "          \"ShutdownDelay\": 0,\n" +
            "          \"VolumeMounts\": null,\n" +
            "          \"ScalingPolicies\": null,\n" +
            "          \"KillSignal\": \"\",\n" +
            "          \"Kind\": \"\",\n" +
            "          \"CSIPluginConfig\": null,\n" +
            "          \"Identity\": null\n" +
            "        }\n" +
            "      ],\n" +
            "      \"EphemeralDisk\": {\n" +
            "        \"Sticky\": false,\n" +
            "        \"SizeMB\": 300,\n" +
            "        \"Migrate\": false\n" +
            "      },\n" +
            "      \"Meta\": null,\n" +
            "      \"ReschedulePolicy\": {\n" +
            "        \"Attempts\": 0,\n" +
            "        \"Interval\": 0,\n" +
            "        \"Delay\": 30000000000,\n" +
            "        \"DelayFunction\": \"exponential\",\n" +
            "        \"MaxDelay\": 3600000000000,\n" +
            "        \"Unlimited\": true\n" +
            "      },\n" +
            "      \"Affinities\": null,\n" +
            "      \"Spreads\": null,\n" +
            "      \"Networks\": [\n" +
            "        {\n" +
            "          \"Mode\": \"\",\n" +
            "          \"Device\": \"\",\n" +
            "          \"CIDR\": \"\",\n" +
            "          \"IP\": \"\",\n" +
            "          \"Hostname\": \"\",\n" +
            "          \"MBits\": 0,\n" +
            "          \"DNS\": null,\n" +
            "          \"ReservedPorts\": null,\n" +
            "          \"DynamicPorts\": [\n" +
            "            {\n" +
            "              \"Label\": \"www\",\n" +
            "              \"Value\": 0,\n" +
            "              \"To\": 8001,\n" +
            "              \"HostNetwork\": \"default\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"Consul\": {\n" +
            "        \"Namespace\": \"\"\n" +
            "      },\n" +
            "      \"Services\": [\n" +
            "        {\n" +
            "          \"Name\": \"hello-world-servers\",\n" +
            "          \"TaskName\": \"\",\n" +
            "          \"PortLabel\": \"www\",\n" +
            "          \"AddressMode\": \"auto\",\n" +
            "          \"Address\": \"\",\n" +
            "          \"EnableTagOverride\": false,\n" +
            "          \"Tags\": null,\n" +
            "          \"CanaryTags\": null,\n" +
            "          \"Checks\": null,\n" +
            "          \"Connect\": null,\n" +
            "          \"Meta\": null,\n" +
            "          \"CanaryMeta\": null,\n" +
            "          \"TaggedAddresses\": null,\n" +
            "          \"Namespace\": \"default\",\n" +
            "          \"OnUpdate\": \"require_healthy\",\n" +
            "          \"Provider\": \"nomad\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"Volumes\": null,\n" +
            "      \"ShutdownDelay\": null,\n" +
            "      \"StopAfterClientDisconnect\": null,\n" +
            "      \"MaxClientDisconnect\": null\n" +
            "    }\n" +
            "  ],\n" +
            "  \"Update\": {\n" +
            "    \"Stagger\": 30000000000,\n" +
            "    \"MaxParallel\": 1,\n" +
            "    \"HealthCheck\": \"\",\n" +
            "    \"MinHealthyTime\": 0,\n" +
            "    \"HealthyDeadline\": 0,\n" +
            "    \"ProgressDeadline\": 0,\n" +
            "    \"AutoRevert\": false,\n" +
            "    \"AutoPromote\": false,\n" +
            "    \"Canary\": 0\n" +
            "  },\n" +
            "  \"Multiregion\": null,\n" +
            "  \"Periodic\": null,\n" +
            "  \"ParameterizedJob\": null,\n" +
            "  \"Dispatched\": false,\n" +
            "  \"DispatchIdempotencyToken\": \"\",\n" +
            "  \"Payload\": null,\n" +
            "  \"Meta\": {\n" +
            "    \"foo\": \"bar\"\n" +
            "  },\n" +
            "  \"ConsulToken\": \"\",\n" +
            "  \"ConsulNamespace\": \"\",\n" +
            "  \"VaultToken\": \"\",\n" +
            "  \"VaultNamespace\": \"\",\n" +
            "  \"NomadTokenID\": \"\",\n" +
            "  \"Status\": \"running\",\n" +
            "  \"StatusDescription\": \"\",\n" +
            "  \"Stable\": true,\n" +
            "  \"Version\": 0,\n" +
            "  \"SubmitTime\": 1689636392581663000,\n" +
            "  \"CreateIndex\": 342,\n" +
            "  \"ModifyIndex\": 352,\n" +
            "  \"JobModifyIndex\": 342\n" +
            "}";
}
