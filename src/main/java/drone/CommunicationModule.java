package drone;

import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;


/**
 * If current drone is alone into the smartcity then he mustn't do anything. Whereas it's not the master drone it have to
 * understand which is the master between the ones already in . This is done by making a broadcast using
 * a greeting message.
 */
public class CommunicationModule extends Thread
{
    private final Drone drone;

    public CommunicationModule(Drone drone){
        this.drone = drone;
    }

    public void run(){
        startListening(drone);
    }

    /**
     *
     * @param drone that wants to start to listening on his port.
     */
    private static void startListening(Drone drone){
        System.out.println("\n\n\n*************** STARTING DRONE'S COMMUNICATION MODULE ***************");
        try
        {
            Server server = ServerBuilder.forPort(drone.getPort()).addService(new GreetingServiceImplementation(drone)).build();
            server.start();
            System.out.println("[DRONE COMMUNICATION MODULE - INPUT] Drone has successfully started to listen on port: " + drone.getPort());
            chatting(drone);
            server.awaitTermination();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     *  This is the method used for making the broadcast of greeting message into the smartcity.
     * @param drone Drone which wants to enter into the smartcity.
     *
     */
    private static void chatting(Drone drone){
        System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Start chatting process");


        if(drone.getSmartcity().getDroneArrayListFromRing() != null) {
            for (Drone currentDrone : drone.getSmartcity().getDroneArrayListFromRing()) {
                if(currentDrone.getID() != drone.getID())
                synchronousCall(drone, currentDrone);
            }
        }
        System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Chatting process has just finished");

        if(drone.getMasterDrone() == null) System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] THIS IS THE MASTER DRONE\n\n");
        else System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Master drone is: \n" + drone.getMasterDrone() + "\n\n");
    }



    private static void synchronousCall(Drone sendingDrone, Drone receivingDrone){
        System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Starting the process for communicating to drone: " + receivingDrone.getID() + " on port: " + receivingDrone.getPort());

        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + receivingDrone.getPort()).usePlaintext().build();
        ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);

        Services.SimpleGreetingRequest.Position position = Services.SimpleGreetingRequest.Position
                .newBuilder()
                .setX(sendingDrone.getPosition().getX())
                .setY(sendingDrone.getPosition().getY())
                .build();

        Services.SimpleGreetingRequest simpleGreetingRequest = Services.SimpleGreetingRequest.newBuilder()
                .setId(sendingDrone.getID().toString())
                .setPort(sendingDrone.getPort())
                .setPosition(position)
                .build();

        // Here i'm gonna receive 'true' or 'false' based on the fact that the other drone is whatever master or not.
        Services.SimpleGreetingResponse response = chattingStub.simpleGreeting(simpleGreetingRequest);
        System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Response obtained from drone: " + response.getMaster());
        if(response.getMaster()){
            sendingDrone.setMasterDrone(receivingDrone);
        }
    }
}
