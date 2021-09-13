package drone.modules;

import drone.Drone;
import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


public class CommunicationThread extends Thread{
    private final Drone sendingDrone;
    public final Drone receivingDrone;
    private volatile Services.SimpleGreetingResponse response;

    public CommunicationThread(Drone sendingDrone, Drone receivingDrone){
        this.sendingDrone = sendingDrone;
        this.receivingDrone = receivingDrone;
    }

    public void run() {
        try
        {
            System.out.println("[COMMUNICATION THREAD]  Starting a new communication thread");
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + receivingDrone.getPort()).usePlaintext().build();
            ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);

            Services.Position position = Services.Position
                    .newBuilder()
                    .setX(sendingDrone.getPosition().getX())
                    .setY(sendingDrone.getPosition().getY())
                    .build();

            Services.SimpleGreetingRequest simpleGreetingRequest = Services.SimpleGreetingRequest.newBuilder()
                    .setId(sendingDrone.getID().toString())
                    .setPort(sendingDrone.getPort())
                    .setPosition(position)
                    .build();

            // Here I'm going to receive 'true' or 'false' based on the fact that the other drone is whatever master or not.
            response = chattingStub.simpleGreeting(simpleGreetingRequest);
            managedChannel.shutdown();
        }
        catch (StatusRuntimeException exception){
            System.out.println("[COMMUNICATION THREAD]  Drone with id: " + receivingDrone.getID() + " is not available");
            System.out.println("[COMMUNICATION THREAD]  Updating view . . .");
            sendingDrone.getSmartcity().removeDrone(receivingDrone);
        }

    }

    public Services.SimpleGreetingResponse getResponse(){
        return response;
    }

}
