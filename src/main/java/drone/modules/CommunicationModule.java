package drone.modules;

import drone.Drone;
import drone.GreetingServiceImplementation;
import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.*;
import tools.Delivery;

import java.util.ArrayList;


/**
 * If current drone is alone into the smartcity then he mustn't do anything. Whereas it's not the master drone it has to
 * understand which is the master between the ones already in. This is done by making a broadcast using
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
            System.out.println("[DRONE COMMUNICATION MODULE - INPUT    -> " + Thread.currentThread().getId() + "] Drone has successfully started to listen on port: " + drone.getPort());
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
     *  This is the method used for making the broadcast of greeting message into the smartcity.
     * @param drone Drone which wants to enter into the smartcity.
     *
     * In this method a new thread will be created for each communication. After this first phase has finished CommunicationModule
     * will perform a join() call which makes the thread wait for each thread termination.
     *
     */
    private static void chatting(Drone drone) throws InterruptedException {
        System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] Start chatting process");
        ArrayList<Drone> drones = drone.getSmartcity().getDroneArrayList();

        if(drones != null)
        {
            ArrayList<CommunicationThread> threadArrayList = new ArrayList<>();

            for (Drone currentDrone : drones){
                // Receiving drone must be different from the sender
                if(currentDrone.getID() != drone.getID()){
                    System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> "
                            + Thread.currentThread().getId() + "] Starting the process for communicating to drone: " + currentDrone.getID()
                            + " on port: " + currentDrone.getPort());

                    CommunicationThread communicationThread = new CommunicationThread(drone, currentDrone);
                    threadArrayList.add(communicationThread);
                    communicationThread.start();
                }
            }

            System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] All threads have been started");
            for(CommunicationThread thread: threadArrayList){
                thread.join();
                Services.SimpleGreetingResponse response = thread.getResponse();

                if(response != null && response.getMaster()){
                    drone.setMasterDrone(thread.receivingDrone);
                }
            }
        }


        System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] Chatting process has just finished");

        if(drone.getMasterDrone() == null) System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] THIS IS THE MASTER DRONE\n\n");
        else System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] Master drone is: \n" + drone.getMasterDrone() + "\n\n");
    }



    /**
     * Assertion: Drone that is calling this method is master drone.
     *
     * @param receivingDrone elected drone to delivery
     * @param delivery delivery that must be delivered
     */
    public static boolean askToMakeADelivery(Drone masterDrone, Drone receivingDrone, Delivery delivery) {
        assert masterDrone.isMasterFlag();
        try
        {
            System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] Starting the process for communicating to drone: " + receivingDrone.getID() + " on port: " + receivingDrone.getPort());
            System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] Asking if drone with id: " + receivingDrone.getID() + " can deliver delivery with id: " + delivery.getID());

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

            Services.DeliveryAssignationResponse response = chattingStub.deliveryAssignationService(deliveryAssignationMessage);
            System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] Drone with id: " + receivingDrone.getID() + " has just accepted to handle delivery with id: " + delivery.getID());
            managedChannel.shutdown();
            return true;
        }
        catch (StatusRuntimeException exception){
            System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] Drone with id: " + receivingDrone.getID() + " is not reachable");
            masterDrone.getSmartcity().removeDrone(receivingDrone);
            return false;
        }
    }


    /**
     * Assertion: Drone which is calling this method is not master drone. Master drone has his private data structure.
     *
     * @return true if master has received drone's data.
     */
    public static boolean sendCompletedDeliveryData(Drone masterDrone, Services.DeliveryComplete deliveryComplete){

        try
        {
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + masterDrone.getPort()).usePlaintext().build();
            ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);
            Services.DeliveryCompleteResponse response = chattingStub.deliveryCompleteService(deliveryComplete);
            System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT] Delivery's data has been sent");
            return true;
        }
        catch (StatusRuntimeException exception){
            System.out.println("[DRONE COMMUNICATION MODULE - OUTPUT    -> " + Thread.currentThread().getId() + "] Master drone is not reachable");
            //TODO: ELEZIONE ---> QUI DOVREI FARE IL JOIN CON IL THREAD ELEZIONE?? DEVO RIMANDARE LE STATISTICHE APPNA POSSO
            return false;
        }
    }

}
