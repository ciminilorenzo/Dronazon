package administration.resources.drone;

import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.util.ArrayList;

public class CommunicationModule extends Thread
{
    private final Drone drone;

    public CommunicationModule(Drone drone){
        this.drone = drone;
    }

    public void run(){
        startListening(drone);
    }

    private static void startListening(Drone drone){
        System.out.println("\n\n\n*************** STARTING DRONE'S COMMUNICATION MODULE ***************");
        try
        {
            Server server = ServerBuilder.forPort(drone.getPort()).addService(new GreetingServiceImplementation(drone)).build();
            server.start();
            System.out.println("\t[DRONE COMMUNICATION MODULE - INPUT] Drone has successfully started to listen on port: " + drone.getPort());
            chatting(drone);
            server.awaitTermination();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     *
     * @param drone Drone which wants to enter into the smartcity.
     *
     *
     */
    private static void chatting(Drone drone){
        System.out.println("\t[DRONE COMMUNICATION MODULE - OUTPUT] Start chatting process");
        ArrayList<Drone> population = drone.getSmartCityDrones();

        for (Drone currentDrone: population) {
            synchronousCall(drone, currentDrone);
        }

        System.out.println("\t[DRONE COMMUNICATION MODULE - OUTPUT] Chatting process has just finished");
    }



    private static void synchronousCall(Drone sendingDrone, Drone receivingDrone){
        System.out.println("\t[DRONE COMMUNICATION MODULE - OUTPUT] Starting the process for communicating to drone: " + receivingDrone.getID() + " on port: " + receivingDrone.getPort());

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

        Services.SimpleGreetingResponse response = chattingStub.simpleGreeting(simpleGreetingRequest);
        System.out.println("\t[DRONE COMMUNICATION MODULE - OUTPUT] Response obtained from drone: " + response.getMaster());

        if(response.getMaster()){
            sendingDrone.setMasterDrone(receivingDrone);
        }
    }
}
