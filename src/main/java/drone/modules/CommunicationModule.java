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
        System.out.println("\n\n\n[DRONE COMMUNICATION MODULE]    Initializing ...");
        try
        {
            Server server = ServerBuilder.forPort(drone.getPort()).addService(new GreetingServiceImplementation(drone)).build();
            server.start();
            System.out.println("[DRONE COMMUNICATION MODULE]    Drone has successfully started to listen on port: " + drone.getPort());
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
        System.out.println("[DRONE COMMUNICATION MODULE]    Start chatting process");
        ArrayList<Drone> drones = drone.getSmartcity().getDroneArrayList();

        if(drones != null)
        {
            ArrayList<CommunicationThread> threadArrayList = new ArrayList<>();

            for (Drone currentDrone : drones){
                // Receiving drone must be different from the sender
                if(currentDrone.getID() != drone.getID()){
                    CommunicationThread communicationThread = new CommunicationThread(drone, currentDrone, "GREETING");
                    threadArrayList.add(communicationThread);
                    communicationThread.start();
                }
            }

            System.out.println("[DRONE COMMUNICATION MODULE]    All greeting have been sent");
            for(CommunicationThread thread: threadArrayList){
                thread.join();

                Services.SimpleGreetingResponse response = thread.getResponse();
                /*
                    3 cases here:
                        1.  If the response is null it means that we had an error during the communication process.
                            then we have to remove the drone from the ring; this operation is done inside the CommunicationThread;
                        2.  If the response is equal to 'false' then the drone is active but, it isn't the master one then we don't
                            have to do nothing;
                        3.  If the response is equal to 'true' then the drone is both active and the master one then we will update the
                            master pointer.
                 */
                if(response != null && response.getMaster()){
                    drone.setMasterDrone(thread.getReceivingDrone());
                    System.out.println("[DRONE COMMUNICATION MODULE]    Master drone is: " + drone.getMasterDrone().getID());
                }
            }
        }
        else
        {
            System.out.println("[DRONE COMMUNICATION MODULE]    This is the master drone");
        }

        System.out.println("[DRONE COMMUNICATION MODULE]    Chatting process has just finished");
    }




    /**
     * Assertion: Drone that is calling this method is master drone.
     *
     * @param receivingDrone elected drone to delivery
     * @param delivery delivery that must be delivered
     * @throws IllegalArgumentException This method throws this exception if the drone calling this method isn't the master one.
     */
    public static boolean askToMakeADelivery(Drone masterDrone, Drone receivingDrone, Delivery delivery) {
        if(!masterDrone.isMasterFlag()) throw new IllegalArgumentException("Drone that calls this method must be the master drone");


        try
        {
            System.out.println("[DRONE COMMUNICATION MODULE]    Asking if drone with id: " + receivingDrone.getID() + " can deliver delivery with id: " + delivery.getID() + " on port: " + receivingDrone.getPort());

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

            if(response.getResponse()){
                System.out.println("[DRONE COMMUNICATION MODULE]    Drone with id: " + receivingDrone.getID() + " has just accepted to handle delivery with id: " + delivery.getID());
                managedChannel.shutdown();
                return true;
            }
            else
            {
                System.out.println("[DRONE COMMUNICATION MODULE]    Drone with id: " + receivingDrone.getID() + " isn't available now");
                masterDrone.getSmartcity().setBusy(receivingDrone.getID(), true);
                return false;
            }
        }
        catch (StatusRuntimeException exception){
            System.out.println("[DRONE COMMUNICATION MODULE]    Drone with id: " + receivingDrone.getID() + " is not reachable");
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
            System.out.println("[DRONE COMMUNICATION MODULE]    Delivery's data has been sent");
            managedChannel.shutdown();
            return true;
        }
        catch (StatusRuntimeException | NullPointerException exception){
            System.out.println("[DRONE COMMUNICATION MODULE]    Master drone is not reachable");
            return false;
        }
    }


    /**
     * This method is used from the drone starting the election process. In this method the first ElectionMessage is built
     * in order to forward it into the ring.
     *
     * @param drone that is trying to send the election message
     * @param nextInTheRing that will receive the message
     * @return true if the communication was successful else it returns false
     */
    public static boolean sendElectionMessageToTheNextInTheRing(Drone drone, Drone nextInTheRing){
        try
        {
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + nextInTheRing.getPort()).usePlaintext().build();
            ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);

            Services.Position position = Services.Position.newBuilder()
                    .setX(drone.getPosition().getX())
                    .setY(drone.getPosition().getY())
                    .build();

            Services.Drone current = Services.Drone.newBuilder()
                    .setId(drone.getID().toString())
                    .setPort(drone.getPort())
                    .setPosition(position)
                    .setBattery(drone.getBattery())
                    .build();

            ArrayList<Services.Drone> currentList = new ArrayList<>();
            currentList.add(current);

            Services.ElectionMessage electionMessage = Services.ElectionMessage.newBuilder()
                    .setMaster(current)
                    .addAllListOfDrones(currentList)
                    .build();


            System.out.println("[ELECTION]  Election message has been sent to the next drone into the ring (with port: " + nextInTheRing.getPort() + ")");
            Services.Empty response = chattingStub.election(electionMessage);
            managedChannel.shutdown();
            return true;
        }
        catch (StatusRuntimeException exception) {
            return false;
        }
    }

    /**
     * This method is used to forward the ElectionMessage into the net. This message is going to be updated everytime a drone
     * receives it.
     *
     * @param drone drone that is trying to forward the election message
     * @param nextInTheRing drone that will receive the message
     * @param message ElectionMessage message that is circulating through the ring
     * @return true if the communication was successful else it returns false
     */
    public static boolean sendElectionMessageToTheNextInTheRing(Drone drone, Drone nextInTheRing, Services.ElectionMessage message){
        try
        {
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + nextInTheRing.getPort()).usePlaintext().build();
            ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);
            System.out.println("[ELECTION]   Election message has been sent to the next drone into the ring (with port: " + nextInTheRing.getPort() + ")");
            chattingStub.election(message);
            managedChannel.shutdown();
            return true;
        }
        catch (StatusRuntimeException exception) {
            System.out.println("[ELECTION]   Drone with id: " + nextInTheRing.getID() + " and port: " + nextInTheRing.getPort() + " is not reachable");
            drone.getSmartcity().removeDrone(nextInTheRing);
            return false;
        }
    }

    public static boolean sendElectedMessageToTheNextInTheRing(Drone drone, Drone nextInTheRing, Services.ElectedMessage message) {
        try {
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + nextInTheRing.getPort()).usePlaintext().build();
            ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);
            System.out.println("[ELECTED]   Elected message has been sent to the next drone into the ring (with port: " + nextInTheRing.getPort() + ")");
            chattingStub.elected(message);
            managedChannel.shutdown();
            return true;
        }
        catch (StatusRuntimeException exception) {
            System.out.println("[ELECTED]   Drone with id: " + nextInTheRing.getID() + " and port: " + nextInTheRing.getPort() + " is not reachable");
            drone.getSmartcity().removeDrone(nextInTheRing);
            return false;
        }
    }


    public static void sendDataAfterRechargeToTheMaster(Drone drone) {
        try {
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + drone.getMasterDrone().getPort()).usePlaintext().build();
            ChattingGrpc.ChattingBlockingStub chattingStub = ChattingGrpc.newBlockingStub(managedChannel);
            System.out.println("[RECHARGE MODULE]   Drone is trying to update the master about new data after recharge");
            chattingStub.getDataAfterRecharge(Drone.convertDroneToServicesDrone(drone));
            managedChannel.shutdown();
        }
        catch (StatusRuntimeException exception) {
            System.out.println("[RECHARGE MODULE]   Master drone has fallen");
            drone.getSmartcity().removeDrone(drone.getMasterDrone());
            drone.setMasterDrone(null);
        }
    }

}
