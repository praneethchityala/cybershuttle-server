package org.cybershuttle.appserver.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.cybershuttle.appserver.*;

@GrpcService
public class CreateCyberShuttleAppImpl extends CyberShuttleServiceGrpc.CyberShuttleServiceImplBase {

    @Override
    public void getApps(AppsRequest request, StreamObserver<MyApps> responseObserver) {
        Integer userId = request.getUserId();
        System.out.println("getapps initiated");
        System.out.println(userId);

        App app0 = App.newBuilder().setName("Jupyter Notebooks").setIcon("Jupyter.png").setAppId(0).setNoOfItems(6).build();
        App app1 = App.newBuilder().setName(".py files").setIcon("Spyder.png").setAppId(1).setNoOfItems(4).build();
        App app2 = App.newBuilder().setName("Neural Networks").setIcon("nn.png").setAppId(2).setNoOfItems(5).build();
        App app3 = App.newBuilder().setName("Machine Learning nodes").setIcon("ml.png").setAppId(3).setNoOfItems(2).build();

        MyApps userApps = MyApps.newBuilder().addApps(app0).addApps(app1).addApps(app2).addApps(app3).setNoOfApps(4).build();
        responseObserver.onNext(userApps);
        responseObserver.onCompleted();
    }

    @Override
    public void getItems(AppRequest request, StreamObserver<ListItems> responseObserver) {
        Integer appId = request.getAppId();
        System.out.println(appId);

        Item item0 = Item.newBuilder().setName("Item 0").setDescription("description 0").setItemId(0).setItemStatus(ItemStatus.newBuilder()
                .setIsItemLaunched(true).setItemStatus("ready").build()).build();
        Item item1 = Item.newBuilder().setName("Item 1").setDescription("description 1").setItemId(1).setItemStatus(ItemStatus.newBuilder()
                .setIsItemLaunched(true).setItemStatus("ready").build()).build();
        Item item2 = Item.newBuilder().setName("Item 2").setDescription("description 2").setItemId(2).setItemStatus(ItemStatus.newBuilder()
                .setIsItemLaunched(true).setItemStatus("ready").build()).build();
        Item item3 = Item.newBuilder().setName("Item 3").setDescription("description 3").setItemId(3).setItemStatus(ItemStatus.newBuilder()
                .setIsItemLaunched(true).setItemStatus("ready").build()).build();
        Item item4 = Item.newBuilder().setName("Item 4").setDescription("description 4").setItemId(4).setItemStatus(ItemStatus.newBuilder()
                .setIsItemLaunched(true).setItemStatus("ready").build()).build();

        ListItems userItems = ListItems.newBuilder().addItems(item0).addItems(item1).addItems(item2).addItems(item3).addItems(item4).build();

        responseObserver.onNext(userItems);
        responseObserver.onCompleted();
    }

    @Override
    public void launchItem(ItemRequest request, StreamObserver<ItemStatus> responseObserver) {
        Integer itemId = request.getItemId();
        System.out.println(itemId);

        ItemStatus itemStatus = ItemStatus.newBuilder().setIsItemLaunched(true).setItemStatus("launched").build();

        responseObserver.onNext(itemStatus);
        responseObserver.onCompleted();
    }

    @Override
    public void stopItem(ItemRequest request, StreamObserver<ItemStatus> responseObserver) {
        Integer itemId = request.getItemId();
        System.out.println(itemId);

        ItemStatus itemStatus = ItemStatus.newBuilder().setIsItemLaunched(false).setItemStatus("ready").build();

        responseObserver.onNext(itemStatus);
        responseObserver.onCompleted();
    }
}
