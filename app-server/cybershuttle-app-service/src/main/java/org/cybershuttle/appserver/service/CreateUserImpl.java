package org.cybershuttle.appserver.service;

import com.orbitz.consul.model.acl.PolicyResponse;
import com.orbitz.consul.model.acl.TokenResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.cybershuttle.appserver.*;
import org.cybershuttle.appserver.ingress.ConsulClient;
import org.cybershuttle.appserver.models.AgentInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@GrpcService
public class CreateUserImpl extends UserServiceGrpc.UserServiceImplBase {

    @org.springframework.beans.factory.annotation.Value("${consul.host}")
    String consulHost;

    @org.springframework.beans.factory.annotation.Value("${consul.port}")
    Integer consulPort;

    @org.springframework.beans.factory.annotation.Value("${consul.token}")
    String consulToken;

    @Autowired
    private ConsulClient consulClient;

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        String newUser = request.getNewUser().toString();
        System.out.println("new user");
        System.out.println(newUser);
        System.out.println(consulClient.testConsul);

        CreateUserResponse createUserResponse = CreateUserResponse.newBuilder().setId(101).build();

        responseObserver.onNext(createUserResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void createToken(CreateTokenRequest request, StreamObserver<CreateTokenResponse> responseObserver) {

        Login login = request.getLogin();
        String userstring = login.getPassword();

        User newUser = User.newBuilder()
                .setId(144).setFirstName(userstring).setLastName(userstring)
                .setUsername(userstring)
                .setEmail(userstring)
                .build();

        CreateTokenResponse createTokenResponse = CreateTokenResponse.newBuilder().setToken("qwerty1234567890").setUser(newUser).build();

        responseObserver.onNext(createTokenResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void isAuthenticated(IsAuthenticatedRequest request, StreamObserver<IsAuthenticatedResponse> responseObserver) {

        User user = request.getUser();

        String sessionId = String.valueOf(UUID.randomUUID());
        PolicyResponse policyResponse = consulClient.createWritePrefixPolicy(user.getUsername(),user.getFirstName(),sessionId);
        String consulToken = consulClient.createToken(policyResponse).secretId();
//        AgentInfo newAgent = new AgentInfo(user.getId(), user.getUsername(),sessionId,consulToken);

        ConsulAuthParams consulAuthParams = ConsulAuthParams.newBuilder()
                .setConsulHost(consulHost)
                .setConsulPort(consulPort)
                .setConsulPath(sessionId)
                .setConsulToken(consulToken)
                .build();

        IsAuthenticatedResponse isAuthenticatedResponse = IsAuthenticatedResponse.newBuilder()
                .setOk(true)
                .setUser(user)
                .setConsulAuthParams(consulAuthParams)
                .build();

        responseObserver.onNext(isAuthenticatedResponse);
        responseObserver.onCompleted();
    }
}
