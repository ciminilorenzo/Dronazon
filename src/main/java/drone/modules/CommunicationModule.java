package drone.modules;

import com.google.protobuf.InvalidProtocolBufferException;
import drone.Drone;
import drone.GreetingServiceImplementation;
import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import tools.Delivery;


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


        if(drone.getSmartcity().getDroneArrayList() != null) {
            for (Drone currentDrone : drone.getSmartcity().getDroneArrayList()) {
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
        try
        {
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

            // Here i'm gonna receive 'true' or 'false' based on the fact that the other drone is whatever master or not.
            Services.SimpleGreetingResponse response = chattingStub.simpleGreeting(simpleGreetingRequest);
            System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Response obtained from drone: " + response.getMaster());
            if(response.getMaster()){
                sendingDrone.setMasterDrone(receivingDrone);
            }
            managedChannel.shutdown();
        }
        catch (Exception exception){
            exception.printStackTrace();
        }


    }

    /**
     * Assertion: Drone that is calling this method is master drone.
     *
     * @param receivingDrone elected drone to delivery
     * @param delivery delivery that must be delivered
     * @return true or false whatever the assignation is successfully or not
     */
    public static boolean askToMakeADelivery(Drone receivingDrone, Delivery delivery) {
        System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Starting the process for communicating to drone: " + receivingDrone.getID() + " on port: " + receivingDrone.getPort());
        System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Asking if drone with id: " + receivingDrone.getID() + " can deliver delivery with id: " + delivery.getID());

        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + receivingDrone.getPort()).usePlaintext().build();
        ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);

        Services.Position pickup = Services.Position
                .newBuilder()
                .setX(delivery.getPickupPoint().getX())
                .setY(delivery.getPickupPoint().getY())
                .build();

        Services.Position deliv = Services.Position
                .newBuilder()
                .setX(delivery.getDeliveryPoint().getX())
                .setY(delivery.getDeliveryPoint().getY())
                .build();

        Services.Delivery finalDelivery = Services.Delivery
                .newBuilder()
                .setPickup(pickup)
                .setDelivery(deliv)
                .setId(delivery.getID().toString())
                .build();

        Services.DeliveryAssignationMessage deliveryAssignationMessage = Services.DeliveryAssignationMessage.newBuilder()
                .setDelivery(finalDelivery)
                .build();

        // Here i'm gonna receive 'true' or 'false' based on the fact that the other drone is whatever master or not.
        Services.DeliveryAssignationResponse response = chattingStub.deliveryAssignationService(deliveryAssignationMessage);
        managedChannel.shutdown();
        return response.getResponse();
    }


    /**
     * Assertion: Drone which is calling this method is not master drone. Master drone has his private data structure.
     *
     * TODO: Se ritorna false vuol dire che il drone master è offline. Nuova elezione? ---> Non può ritornare false se è offline lol
     *      Come gestire non risposta?
     * @return true if master has received drone's data.
     */
    public static boolean sendCompletedDeliveryData(Drone masterDrone, Services.DeliveryComplete deliveryComplete){
        try
        {
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + masterDrone.getPort()).usePlaintext().build();
            ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);
            Services.DeliveryCompleteResponse response = chattingStub.deliveryCompleteService(deliveryComplete);
            System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Delivery's data has been sent");
            return response.getResponse();
        }
        catch (Exception exception){
            exception.printStackTrace();
            return false;
        }
    }

}
