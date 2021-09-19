package drone.modules;

import drone.Drone;
import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


public class CommunicationThread extends Thread{
    private final Drone sendingDrone;
    private final Drone receivingDrone;
    private final String action;

    private Services.SimpleGreetingResponse greetingResponse;
    private Services.RechargePermissionResponse rechargePermissionResponse;

    private static final String GREETING = "GREETING";
    private static final String RECHARGE = "RECHARGE";

    public CommunicationThread(Drone sendingDrone, Drone receivingDrone, String action){
        this.sendingDrone = sendingDrone;
        this.receivingDrone = receivingDrone;
        this.action = action;
    }

    public Drone getReceivingDrone(){
        return receivingDrone;
    }


    public void run() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + receivingDrone.getPort()).usePlaintext().build();
        ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);

        switch (action) {
            case GREETING: {
                try
                {
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
                    greetingResponse = chattingStub.simpleGreeting(simpleGreetingRequest);
                    managedChannel.shutdown();
                }
                catch (RuntimeException runtimeException){
                    System.out.println("[COMMUNICATION THREAD]  Drone with id: " + receivingDrone.getID() + " is not available");
                    System.out.println("[COMMUNICATION THREAD]  Updating view . . .");
                    sendingDrone.getSmartcity().removeDrone(receivingDrone);
                }
                break;
            }
            case RECHARGE: {
                try
                {
                    String timestamp = java.time.LocalDateTime.now().toString();

                    // This timestamp will be used in case the drone will receive other recharge permission requests
                    // in order to compare this timestamp with the one inside the message which has been received.
                    sendingDrone.getRechargeModule().setTimestamp(timestamp);

                    Services.RechargePermission rechargePermission = Services.RechargePermission.newBuilder()
                            .setResource("RECHARGE")
                            .setId(sendingDrone.getID().toString())
                            .setTimestamp(timestamp)
                            .setPort(sendingDrone.getPort())
                            .build();

                    rechargePermissionResponse = chattingStub.requireRechargePermission(rechargePermission);
                    managedChannel.shutdown();
                }
                catch (RuntimeException runtimeException)
                {
                    System.out.println("[COMMUNICATION THREAD]  Drone with id: " + receivingDrone.getID() + " is not available");
                    System.out.println("[COMMUNICATION THREAD]  Updating view . . .");
                    sendingDrone.getSmartcity().removeDrone(receivingDrone);
                }
                break;
            }
        }

    }

    public Services.SimpleGreetingResponse getResponse(){
        return greetingResponse;
    }

    public Services.RechargePermissionResponse getRechargePermissionResponse(){
        if(rechargePermissionResponse != null)
            return rechargePermissionResponse;
        else
            return null;
    }


}
