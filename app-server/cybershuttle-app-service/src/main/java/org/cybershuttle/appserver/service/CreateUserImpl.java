package org.cybershuttle.appserver.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.cybershuttle.appserver.*;
import org.cybershuttle.appserver.ingress.ConsulClient;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class CreateUserImpl extends UserServiceGrpc.UserServiceImplBase {

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
        String user = request.getLogin().toString();
        System.out.println("Creating token for");
        System.out.println(user);

        CreateTokenResponse createTokenResponse = CreateTokenResponse.newBuilder().setToken("qwerty1234567890").build();

        responseObserver.onNext(createTokenResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void isAuthenticated(IsAuthenticatedRequest request, StreamObserver<IsAuthenticatedResponse> responseObserver) {

        String token = request.getToken().toString();
        System.out.println(token);

        IsAuthenticatedResponse isAuthenticatedResponse = IsAuthenticatedResponse.newBuilder()
                .setOk(true)
                .setUser(User.newBuilder()
                        .setId(101)
                        .setEmail("test@cybershuttle.org")
                        .setFirstName("Bob")
                        .setLastName("Richardson")
                        .setUsername("BobRich")
                        .build())
                .build();

        responseObserver.onNext(isAuthenticatedResponse);
        responseObserver.onCompleted();
    }
}
