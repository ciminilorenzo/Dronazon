package drone;

import administration.resources.statistics.Statistic;
import drone.modules.CommunicationModule;
import drone.modules.DeliveryModule;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import tools.Delivery;
import tools.Position;
import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.stub.StreamObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GreetingServiceImplementation extends ChattingGrpc.ChattingImplBase
{
    private final Drone drone;
    private ArrayList<Drone> dronesIntoTheMessage = new ArrayList<>();

    private static final Object dummyObject = new Object();
    private static final Object dummyObjectElection = new Object();
    private static volatile boolean nextMaster = false;

    // This dummy object is used to synchronize the state in which the drone is recharging. In fact the drone, using
    // this dummy, waits until the recharge is finished in order to make the broadcast.
    public static final Object dummyObjectToRecharge = new Object();

    // We're going to save drones' port in order to reply when this drone finishes consuming the resource
    private ArrayList<StreamObserver<Services.RechargePermissionResponse>> dronesToNotify = new ArrayList<>();

    public static final Object dummyObjectToNotify = new Object();

    public GreetingServiceImplementation(Drone drone){
        this.drone = drone;
    }

    public static Object getDummy(){
        return dummyObject;
    }

    public static Object getDummyObjectElection(){
        return dummyObjectElection;
    }

    public static Object getDummyObjectToRecharge () {
        return dummyObjectToRecharge;
    }

    public static boolean getNextMaster(){
        return nextMaster;
    }

    public static void setNextMaster(boolean flag){
        nextMaster = flag;
    }




    private ArrayList<StreamObserver<Services.RechargePermissionResponse>> getDronesToNotify(){
        synchronized (dummyObjectToNotify){
            return dronesToNotify;
        }
    }

    private void addDroneToNotify(StreamObserver<Services.RechargePermissionResponse> drone){
        synchronized (dummyObjectToNotify){
            System.out.println("[RECHARGE MODULE]   One new drone has been added to the queue");
            dronesToNotify.add(drone);
        }
    }


    /**
     * Simple method called when a drone wants to send a SimpleGreeting message into the net.
     * This function is used in:
     *  - When a drone is trying to enter into the net proceeds sending a SimpleGreeting message to all the
     *      drones into the net.
     *      The answer that will receive will be a SimpleGreetingResponse that let the drone know which one is the master.
     * @param request SimpleGreetingRequest object that contains all data needed for this purpose.
     * @param responseStreamObserver stream observer
     *
     *  This is a unary gRPC since the drone will send the single SimpleGreeting message and will receive one single
     *  SimpleGreetingResponse message.
     *
     */
    @Override
    public void simpleGreeting(Services.SimpleGreetingRequest request, StreamObserver<Services.SimpleGreetingResponse> responseStreamObserver) {
        Services.SimpleGreetingRequest data = request;

        try
        {

            if(drone.isParticipantToElection()){
                synchronized (dummyObject){
                    System.out.println("[DRONE COMMUNICATION MODULE]    This drone is currently following an election. This greeting must wait ");
                    dummyObject.wait();
                }
            }

            // This call is made in order to update drone's view with new drones entering into the smartcity.
            updateView(data);
            System.out.println("\n\n[DRONE COMMUNICATION MODULE]    SIMPLE GREETING SERVICE - Preparing to answer to a request ");
            Services.SimpleGreetingResponse simpleGreetingResponse;

            if(drone.isMasterFlag()){
                simpleGreetingResponse = Services.SimpleGreetingResponse.newBuilder().setMaster(true).build();
            }
            else{
                simpleGreetingResponse = Services.SimpleGreetingResponse.newBuilder().setMaster(false).build();
            }
            responseStreamObserver.onNext(simpleGreetingResponse);
            responseStreamObserver.onCompleted();
            System.out.println("[DRONE COMMUNICATION MODULE]    SIMPLE GREETING SERVICE - Answer successfully sent to drone " + data.getId());

            // New drone has just entered into the smartcity. This is one situation where if the current drone is the master one, it could check if there are any undelivered deliveries
            // it could be assigned of one of them.
            if(drone.isMasterFlag()){
                drone.communicateAvailability();
            }
        }
        catch (InterruptedException exception){
            System.out.println(exception.getMessage());
        }
    }

    private void updateView(Services.SimpleGreetingRequest newDrone){
        Drone droneToInsert = new Drone(UUID.fromString(newDrone.getId()), newDrone.getPort(), new Position(newDrone.getPosition().getX(), newDrone.getPosition().getY()));
        this.drone.getSmartcity().insertDrone(droneToInsert);

    }


    @Override
    public void deliveryAssignationService(Services.DeliveryAssignationMessage request, StreamObserver<Services.DeliveryAssignationResponse> responseObserver) {
        if(drone.isBusy())
        {
            System.out.println("\n\n[DRONE COMMUNICATION MODULE]    Drone is busy. Can't delivery: preparing negative availability");
            Services.DeliveryAssignationResponse deliveryAssignationResponse = Services.DeliveryAssignationResponse.newBuilder()
                    .setResponse(false)
                    .build();
            responseObserver.onNext(deliveryAssignationResponse);
        }
        else
        {
            System.out.println("\n\n[DRONE COMMUNICATION MODULE]    Preparing the drone to the delivery ");
            Services.DeliveryAssignationResponse deliveryAssignationResponse;

            deliveryAssignationResponse         = Services.DeliveryAssignationResponse.newBuilder().setResponse(true).build();
            Services.Delivery deliveryReceived  = request.getDelivery();
            Delivery delivery                   = new Delivery(deliveryReceived.getId(), deliveryReceived.getPickup(), deliveryReceived.getDelivery());

            // This call changes drone busy state in itself view. The master drone will also call this procedure to change in his view the same fact.
            drone.setBusy(true);
            DeliveryModule deliveryModule = new DeliveryModule(drone, delivery);

            // This field is set in order to make the join possibile inside the quit module.
            drone.setDeliveryModule(deliveryModule);
            deliveryModule.start();
            responseObserver.onNext(deliveryAssignationResponse);
        }
        responseObserver.onCompleted();
    }


    /**
     * Assertion: This method is called just in case in which this drone is the master one.
     *
     * @param request DeliveryComplete message received from drone which had completed the delivery
     * @param responseObserver master drone has to send receipt confirmation
     */
    @Override
    public void deliveryCompleteService(Services.DeliveryComplete request, StreamObserver<Services.DeliveryCompleteResponse> responseObserver) {
        System.out.println("\n\n[DRONE COMMUNICATION MODULE]    Inserting received statistic into the data structure . . .");

        Position    newPosition =  new Position(request.getNewPosition().getX(), request.getNewPosition().getY());
        int         battery = request.getBatteryLeft();

        Statistic statistic = new Statistic(
                request.getTimestamp(),
                newPosition,
                request.getDistance(),
                request.getPollution(),
                battery,
                request.getDroneId()
        );

        // Updating master's view
        this.drone.addStatisticToMasterDroneDataStructure(statistic);   // Adding statistic to master's data structure.
        this.drone.getSmartcity().modifyDroneAfterDelivery(UUID.fromString(request.getDroneId()), newPosition, battery, false);  // Modifying deliverer's data inside master drone data structure



        System.out.println("[DRONE COMMUNICATION MODULE]    Received statistic successfully saved");
        Services.DeliveryCompleteResponse response = Services.DeliveryCompleteResponse.newBuilder().setResponse(true).build();
        responseObserver.onNext(response);
        System.out.println("[DRONE COMMUNICATION MODULE]    Sending receipt confirmation message");
        this.drone.communicateAvailability();       // This is another situation where if there are any undelivered deliveries this drone, due he is just been assigned as 'not busy', could deliverer it
        responseObserver.onCompleted();
    }



    @Override
    public void ping(Services.Empty request, StreamObserver<Services.Empty> responseObserver){
        Services.Empty response = Services.Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void election(Services.ElectionMessage request, StreamObserver<Services.Empty> responseObserver) {
        System.out.println("[ELECTION]   Election message received");

        int actualBatteryLevel =  (drone.getDeliveryModule() != null && drone.getDeliveryModule().isAlive()) ? drone.getBattery() - 10 : drone.getBattery();
        // If it's not participant
        if(!drone.isParticipantToElection()){
            System.out.println("[ELECTION]   It means that master drone has fallen");
            System.out.println("[ELECTION]   Setting participant as true");
            drone.setParticipantToElection(true);
            drone.setMasterDrone(null);

            if(actualBatteryLevel > request.getMaster().getBattery())
            {
                // If the current drone's battery level is greater than the one into the message
                // then we have to do modify the message in order to update the new master
                nextMaster = true;
                buildAndForward(request);
            }
            else if (actualBatteryLevel < request.getMaster().getBattery())
            {
                // If the current drone's battery level is smaller than the one into the message
                // then we have to simply forward the message adding the drone to the list inside the message
                notifyState();
                simplyForward(request);
            }

            else
            {
                // If the current drone's battery level is equal to the one into the message
                // then we have to make some checks
                if(drone.getID().compareTo(UUID.fromString(request.getMaster().getId())) > 0) {
                    // If the current drone's battery level is equal to the one than we have in the message
                    // and the current drone's ID is bigger than the master's one then we have to update the master
                    nextMaster = true;
                    System.out.println("SETTO A TRUE");
                    buildAndForward(request);
                }
                else {
                    // If the current drone's battery level is equal to the one than we have in the message
                    // and the current drone's ID is smaller than the master's one then we have to simply forward the message
                    simplyForward(request);
                    notifyState();
                }
            }

        }
        else {
            // If the drone is already participant we have to distinguish over two cases:
            //  - current drone has a battery level (or ID in case battery level is equal) that it's bigger than the one
            //      present inside the message;
            if ((actualBatteryLevel > request.getMaster().getBattery())
                    ||
                    (actualBatteryLevel == request.getMaster().getBattery() && drone.getID().compareTo(UUID.fromString(request.getMaster().getId())) > 0)) {
                // then we have that the current drone has an important battery level or ID, and it's present was already
                // forwarded into the net thus we don't have to do anything.
                System.out.println("[ELECTION]   Already participant to a election . . . blocking this election");
            }
            //  - current drone has a battery level (or ID in case battery level is equal) that's lower than the one
            //      present inside the message;
            else if((actualBatteryLevel < request.getMaster().getBattery())
                    ||
                    (actualBatteryLevel == request.getMaster().getBattery() && drone.getID().compareTo(UUID.fromString(request.getMaster().getId())) < 0)) {
                // then we have that the current drone has a less important battery level or ID, and it's presence was already
                // forwarded into the net thus we have to simply forward the message but, without adding this drone to the list
                // present into the message
                Services.ElectionMessage newMessage = Services.ElectionMessage.newBuilder()
                        .setMaster(request.getMaster())
                        .addAllListOfDrones(request.getListOfDronesList())
                        .build();
                System.out.println("[ELECTION]   Already participant to a election . . . forwarding the message");
                notifyState();

                // If forward method hasn't been able to forward the message to, at least, one drone it means that the
                // current drone is alone into the smartcity
                if(!forward(drone, newMessage)){
                    System.out.println("[ELECTED]   This drone is the only one into the smartcity. Setting as master . . .");
                    setAsMaster();
                }
            }
            else if(request.getMaster().getId().equals(drone.getID().toString())){
                System.out.println("[ELECTION]   This drone won the election.");
                System.out.println("[ELECTION]   Preparing the elected message to forward into the net");

                List<Services.Drone> newList = new ArrayList<>(request.getListOfDronesList());
                dronesIntoTheMessage.clear();
                for (Services.Drone drone: newList) {
                    dronesIntoTheMessage.add(Drone.convertServicesDroneToDrone(drone));
                }

                Services.ElectedMessage message = Services.ElectedMessage.newBuilder()
                                .setMaster(Drone.convertDroneToServicesDrone(drone)).build();

                if(!forward(drone, message)){
                    System.out.println("[ELECTED]   This drone is the only one into the smartcity. Setting as master . . .");
                    setAsMaster();
                }
                // After the election's winner has won the election he has to send at least one ELECTED message in order to
                // be sure that, if it falls, the drones are going to be capable of detect it.
                else{
                    notifyState();
                }

            }
        }

        Services.Empty response = Services.Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void elected(Services.ElectedMessage request, StreamObserver<Services.Empty> responseObserver) {
        // If the drone's id contained into the message is the same of the current one then the broadcast has finished
        if(UUID.fromString(request.getMaster().getId()).compareTo(drone.getID()) == 0){
            System.out.println("[ELECTED]   Elected message has been sent to all the drones into the net");
            System.out.println("[ELECTED]   Starting master's duties ");
            drone.setMasterFlag(true);
            drone.setParticipantToElection(false);
            drone.getSmartcity().cleanList();
            drone.getSmartcity().insertListOfDrones(dronesIntoTheMessage);
        }
        // If the drone's id contained into the message is different from the one that is receiving the message
        // then we have to save the information and forward the message into the net
        else
        {
            System.out.println("[ELECTED]   Elected message received");
            if(!forward(drone, request)){
                System.out.println("[ELECTED]   This drone is the only one into the smartcity. Setting as master . . .");
                setAsMaster();
               }
            else
            {
                drone.setMasterDrone(Drone.convertServicesDroneToDrone(request.getMaster()));
                drone.setParticipantToElection(false);
                System.out.println("[ELECTED]   New master is drone with id: " + drone.getMasterDrone().getID() + " and port: " + drone.getMasterDrone().getPort());
            }
        }

        Services.Empty response = Services.Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // This method is used to let QuitModule that it can go ahead with the quitting process because the drone is not
    // inside the election message as the 'master' one.
    private void notifyState(){
        synchronized (dummyObjectElection){
            nextMaster = false;
            dummyObjectElection.notify();
        }
    }

    private void setAsMaster(){
        drone.setMasterFlag(true);
        drone.setParticipantToElection(false);
        drone.getSmartcity().cleanList();
        drone.getSmartcity().insertListOfDrones(dronesIntoTheMessage);
        System.out.println("[ELECTION]   Starting master's duties ");
    }

    private void buildAndForward(Services.ElectionMessage request){
        Services.Drone newMaster = Drone.convertDroneToServicesDrone(drone);
        List<Services.Drone> newList = new ArrayList<>(request.getListOfDronesList());
        newList.add(newMaster);

        // TODO: MAYBE NOT EFFICIENT
        dronesIntoTheMessage.clear();
        for (Services.Drone drone: newList) {
            dronesIntoTheMessage.add(Drone.convertServicesDroneToDrone(drone));
        }

        Services.ElectionMessage newMessage = Services.ElectionMessage.newBuilder()
                .setMaster(newMaster)
                .addAllListOfDrones(newList)
                .build();

        // If forward method hasn't been able to forward the message to, at least, one drone it means that the
        // current drone is alone into the smartcity
        if(!forward(drone, newMessage)){
            System.out.println("[ELECTED]   This drone is the only one into the smartcity. Setting as master . . .");
            setAsMaster();
        }

    }

    private void simplyForward(Services.ElectionMessage request){
        Services.Drone droneToAdd = Drone.convertDroneToServicesDrone(drone);
        List<Services.Drone> newList = new ArrayList<>(request.getListOfDronesList());
        newList.add(droneToAdd);

        // TODO: MAYBE NOT EFFICIENT
        dronesIntoTheMessage.clear();
        for (Services.Drone drone: newList) {
            dronesIntoTheMessage.add(Drone.convertServicesDroneToDrone(drone));
        }

        Services.ElectionMessage newMessage = Services.ElectionMessage.newBuilder()
                .setMaster(request.getMaster())
                .addAllListOfDrones(newList)
                .build();

        // If forward method hasn't been able to forward the message to, at least, one drone it means that the
        // current drone is alone into the smartcity
        if(!forward(drone, newMessage)){
            System.out.println("[ELECTED]   This drone is the only one into the smartcity. Setting as master . . .");
            setAsMaster();
        }
    }


    private boolean forward(Drone drone, Object object){
        if(!(object instanceof Services.ElectionMessage || object instanceof Services.ElectedMessage))
            throw new IllegalArgumentException();

        while(true){
            Drone next = drone.getSmartcity().getNext();

            if(next == null)
                return false;

            else if(object instanceof Services.ElectionMessage){
                if(CommunicationModule.sendElectionMessageToTheNextInTheRing(drone, next, (Services.ElectionMessage) object)){
                    return true;
                }
                else if(next.getID().toString().equals(((Services.ElectionMessage) object).getMaster().getId())){
                    // It means that election communication to the next drone inside the ring wasn't successful.
                    // This check here is to be sure that the drone that han fallen wasn't the "strongest possibile master" (the one into the message)
                    // In fact, in that case, we are going to modify the message's drone master in order to update the other drones about this crash
                    // and restart the election with the current id
                }
            }
            else {
                if (CommunicationModule.sendElectedMessageToTheNextInTheRing(drone, next, (Services.ElectedMessage) object)){
                    return true;
                }
            }
        }
    }


    /**
     *
     * @param request that is coming from the drone which has terminated the recharge
     * @throws IllegalArgumentException if drone which is receiving this call is not the master one
     */
    @Override
    public void getDataAfterRecharge(Services.Drone request, StreamObserver<Services.Empty> responseObserver) {
        if(!drone.isMasterFlag()) throw new IllegalArgumentException("Only master drone can receive this call");

        drone.getSmartcity().modifyDroneAfterDelivery
                (
                UUID.fromString(request.getId()),
                new Position(request.getPosition().getX(), request.getPosition().getY()),
                request.getBattery(),
                false
                );

        Services.Empty response = Services.Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();


    }


    @Override
    public void requireRechargePermission(Services.RechargePermission request, StreamObserver<Services.RechargePermissionResponse> responseObserver) {
        if(!request.getResource().equals("RECHARGE")) throw new IllegalArgumentException("This method is used to handle recharge permissions");

        Services.RechargePermissionResponse rechargePermissionResponse = Services.RechargePermissionResponse.newBuilder()
                .setResponse(true)
                .build();

        try
        {
            if(drone.getRechargeModule().currentlyRecharging){
                synchronized (dummyObjectToRecharge){
                    System.out.println("[RECHARGE MODULE]   Now the drone is charging his battery. As soon as it finishes it will reply to the request");
                    addDroneToNotify(responseObserver);
                    dummyObjectToRecharge.wait();
                }
                broadcast();
            }
            else if(drone.getRechargeModule().isInterestedInRecharging)
            {
                String myTimeStamp = drone.getRechargeModule().getTimestamp();
                String receivedTimeStamp = request.getTimestamp();

                // If current drone's timestamp is older than the one just received then we have the priority
                if(LocalDateTime.parse(myTimeStamp).isBefore(LocalDateTime.parse(receivedTimeStamp))){
                    synchronized (dummyObjectToRecharge){
                        System.out.println("[RECHARGE MODULE]   The drone has just received a request with a lower timestamp. As soon as it finishes it will reply to the request");
                        addDroneToNotify(responseObserver);
                        dummyObjectToRecharge.wait();
                    }
                    broadcast();
                }
                // If the timestamps are equal we have to distinguish over tre cases:
                // 1: If the ids are equal:
                else if (LocalDateTime.parse(myTimeStamp).isEqual(LocalDateTime.parse(receivedTimeStamp))){
                        // The current drone's ID is bigger than the one which has sent the request
                        // then here we have the priority
                        if(drone.getID().compareTo(UUID.fromString(request.getId())) > 0)
                        {
                            synchronized (dummyObjectToRecharge){
                                System.out.println("[RECHARGE MODULE]   The drone has just received a request with a lower timestamp. As soon as it finishes it will reply to the request");
                                addDroneToNotify(responseObserver);
                                dummyObjectToRecharge.wait();
                            }
                            broadcast();
                        }
                        // The current drone's ID is smaller than the one which has sent the request
                        else
                            givePermission(rechargePermissionResponse, responseObserver);
                    }
                // Else if the current drone's timestamp is newer then we do have to give the permission and wait
                else
                {
                    givePermission(rechargePermissionResponse, responseObserver);
                }
            }
            // If the drone is both not recharging and isn't looking for it then we can reply positively
            else
                givePermission(rechargePermissionResponse, responseObserver);
        }
        catch (RuntimeException | InterruptedException exception){
            System.out.println("[RECHARGE MODULE]   Receiver drone is not available anymore");
        }
    }

    private void givePermission(Services.RechargePermissionResponse rechargePermissionResponse, StreamObserver<Services.RechargePermissionResponse> responseObserver){
        responseObserver.onNext(rechargePermissionResponse);
        responseObserver.onCompleted();
    }


    public void broadcast(){
        Services.RechargePermissionResponse rechargePermissionResponse = Services.RechargePermissionResponse.newBuilder()
                .setResponse(true)
                .build();

        ArrayList<StreamObserver<Services.RechargePermissionResponse>> copy = dronesToNotify;
        System.out.println("RECHARGE MODULE: Devo mandare " + copy.size() + " permessi");

        for (StreamObserver<Services.RechargePermissionResponse> currentDrone: copy) {
            Thread newThread = new Thread(
                    new Runnable() {

                        public void run() {
                            givePermission(rechargePermissionResponse, currentDrone);
                            System.out.println("[RECHARGE MODULE] Response has been sent.");
                        }
                    });
            newThread.start();
        }
    }
}
