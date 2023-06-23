package org.cybershuttle.appserver.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.cybershuttle.appserver.*;

@GrpcService
public class CreateUserImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        String newUser = request.getNewUser().toString();
        System.out.println(newUser);

        CreateUserResponse createUserResponse = CreateUserResponse.newBuilder()
                .setId(101)
                .build();

        responseObserver.onNext(createUserResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {

        System.out.println("I got a ping");

        HelloReply helloReply = HelloReply.newBuilder().setMessage("I am up and running").build();

        responseObserver.onNext(helloReply);
        responseObserver.onCompleted();
    }
}
