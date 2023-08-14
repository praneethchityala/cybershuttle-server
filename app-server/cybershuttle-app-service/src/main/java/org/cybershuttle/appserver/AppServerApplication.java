package org.cybershuttle.appserver;

import com.orbitz.consul.AclClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.acl.TokenResponse;
import org.cybershuttle.appserver.ingress.ConsulClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class AppServerApplication {

//	@org.springframework.beans.factory.annotation.Value("${consul.host}")
//	public static String consulHost;
//
//	@org.springframework.beans.factory.annotation.Value("${consul.port}")
//	public static Integer consulPort;
//
//	@org.springframework.beans.factory.annotation.Value("${consul.token}")
//	public static String consulToken;

	public static void main(String[] args) {

		SpringApplication.run(AppServerApplication.class, args);



////		@org.springframework.beans.factory.annotation.Value("${consul.host}")
//		String consulHost = "localhost";
//
////		@org.springframework.beans.factory.annotation.Value("${consul.port}")
//		Integer consulPort = 8500;
//
////		@org.springframework.beans.factory.annotation.Value("${consul.token}")
//		String consulToken = "b2546c5c-a3d9-daaf-dede-19a060f85f79";

//
//		ConsulClient consulClient = new ConsulClient(consulHost, consulPort, consulToken);
//
//		String newRule = "key \"foo\" {\n" +
//
//				"  policy = \"write\"\n" +
//				"}";
//
//		TokenResponse newToken = consulClient.
//				createToken(consulClient.createPolicy("Java-token","Java",newRule));
//
//		System.out.println(newToken.secretId());

//		read client
//		Consul client1 = Consul.builder().withTokenAuth("a6e7a71a-34fc-f763-6198-7865dac0207c").build();

//		write client
//		Consul client1 = Consul.builder().withTokenAuth("440077b9-6b58-83bd-4eee-54ee35ee2d54").build();

//		KeyValueClient kvClient = client1.keyValueClient();

//		AclClient aclClient = client1.aclClient();

//		kvClient.putValue("hey/test", "I am write key");

//		Optional<String> val = kvClient.getValueAsString("hey/read/test");

//		System.out.println(val);
	}

}
