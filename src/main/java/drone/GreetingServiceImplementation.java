package drone;

import administration.resources.statistics.Statistic;
import drone.modules.CommunicationModule;
import drone.modules.DeliveryModule;
import tools.Delivery;
import tools.Position;
import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GreetingServiceImplementation extends ChattingGrpc.ChattingImplBase
{
    private final Drone drone;

    public GreetingServiceImplementation(Drone drone){
        this.drone = drone;
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
    public void simpleGreeting(Services.SimpleGreetingRequest request, StreamObserver<Services.SimpleGreetingResponse> responseStreamObserver)
    {
        // This call is made in order to update drone's view with new drones entering into the smartcity.
        updateView(request);
        System.out.println("\n\n[DRONE COMMUNICATION MODULE - INPUT] SIMPLE GREETING SERVICE - Preparing to answer to a request ");
        Services.SimpleGreetingResponse simpleGreetingResponse;

        if(drone.isMasterFlag()){
            simpleGreetingResponse = Services.SimpleGreetingResponse.newBuilder().setMaster(true).build();
        }
        else{
            simpleGreetingResponse = Services.SimpleGreetingResponse.newBuilder().setMaster(false).build();
        }
        responseStreamObserver.onNext(simpleGreetingResponse);
        responseStreamObserver.onCompleted();
        System.out.println("[DRONE COMMUNICATION MODULE - INPUT] SIMPLE GREETING SERVICE - Answer successfully sent to drone " + request.getId());

        // New drone has just entered into the smartcity. This is one situation where if the current drone is the master one, it could check if there are any undelivered deliveries
        // it could be assigned of one of them.
        if(drone.isMasterFlag()){
            drone.communicateAvailability();
        }
    }

    private void updateView(Services.SimpleGreetingRequest newDrone){
        Drone droneToInsert = new Drone(UUID.fromString(newDrone.getId()), newDrone.getPort(), new Position(newDrone.getPosition().getX(), newDrone.getPosition().getY()));
        this.drone.getSmartcity().insertDrone(droneToInsert);

    }

    /**
     * Assertion: This gRPC call is made by the master drone when he has to perform a delivery assignment.
     *
     * @param request DeliveryAssignationMessage used for communicating delivery information to the specified drone.
     * @param responseObserver stream observer
     */
    @Override
    public void deliveryAssignationService(Services.DeliveryAssignationMessage request, StreamObserver<Services.DeliveryAssignationResponse> responseObserver) {
        System.out.println("\n\n[DRONE COMMUNICATION MODULE - INPUT] DELIVERY ASSIGNATION - Preparing the drone to the delivery ");
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
        System.out.println("\n\n[DRONE COMMUNICATION MODULE - INPUT] DELIVERY COMPLETE MESSAGE - Inserting received statistic into the data structure . . .");

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
        //TODO: QUI DEVO SETTARE COME NOT BUSY SOLO SE BATTERIA SUPERIORE A 10%
        this.drone.getSmartcity().modifyDroneAfterDelivery(UUID.fromString(request.getDroneId()), newPosition, battery, false);  // Modifying deliverer's data inside master drone data structure
        this.drone.communicateAvailability();       // This is another situation where if there are any undelivered deliveries this drone, due he is just been assigned as 'not busy', could deliverer it


        System.out.println("[DRONE COMMUNICATION MODULE - INPUT] DELIVERY COMPLETE MESSAGE - Received statistic successfully saved");
        Services.DeliveryCompleteResponse response = Services.DeliveryCompleteResponse.newBuilder().setResponse(true).build();
        responseObserver.onNext(response);
        System.out.println("[DRONE COMMUNICATION MODULE - INPUT] DELIVERY COMPLETE MESSAGE - Sending receipt confirmation message");
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
        if(drone.getPingModule() != null && !drone.getPingModule().isShutdown()){
            drone.getPingModule().shutdown();
            drone.setPingModule(null);
        }
        System.out.println("[ELECTION]  Election message received");

        // If it's not participant
        if(!drone.isParticipantToElection()){
            System.out.println("[ELECTION]  It means that master drone has fallen");
            drone.getSmartcity().removeDrone(drone.getMasterDrone());
            System.out.println("[ELECTION]  Setting participant as true");
            drone.setParticipantToElection(true);

            if(drone.getBattery() > request.getMaster().getBattery()){
                // If the current drone's battery level is greater than the one into the message
                // then we have to do modify the message in order to update the new master
                Services.Drone newMaster = Drone.convertDroneToServicesDrone(drone);
                List<Services.Drone> newList = new ArrayList<>(request.getListOfDronesList());
                newList.add(newMaster);

                Services.ElectionMessage newMessage = Services.ElectionMessage.newBuilder()
                        .setMaster(newMaster)
                        .addAllListOfDrones(newList)
                        .build();

                //TODO: GESTIRE CASO IN CUI RITORNA NULL (SI ERA IN 2 MA UNO CADE DURANTE L'ELEZIONE)
                Drone next = drone.getSmartcity().getNext();
                //TODO: MAGARI UTILIZZANDO QUESTA VARIABILE
                boolean result = CommunicationModule.sendElectionMessageToTheNextInTheRing(drone, next, newMessage);
            }
            else if (drone.getBattery() < request.getMaster().getBattery()){
                // If the current drone's battery level is smaller than the one into the message
                // then we have to simply forward the message adding the drone to the list inside the message
                Drone next = drone.getSmartcity().getNext();
                Services.Drone newMaster = Drone.convertDroneToServicesDrone(drone);
                List<Services.Drone> newList = new ArrayList<>(request.getListOfDronesList());
                newList.add(newMaster);
                Services.ElectionMessage newMessage = Services.ElectionMessage.newBuilder()
                        .setMaster(request.getMaster())
                        .addAllListOfDrones(newList)
                        .build();

                boolean result = CommunicationModule.sendElectionMessageToTheNextInTheRing(drone, next, newMessage);
            }

            else{
                // If the current drone's battery level is equal to the one into the message
                // then we have to make some checks
                if(drone.getID().compareTo(UUID.fromString(request.getMaster().getId())) > 0){
                    // If the current drone's battery level is equal to the one than we have in the message
                    // and the current drone's ID is bigger than the master's one then we have to update the master
                    Drone next = drone.getSmartcity().getNext();
                    Services.Drone newMaster = Drone.convertDroneToServicesDrone(drone);
                    List<Services.Drone> newList = new ArrayList<>(request.getListOfDronesList());
                    newList.add(newMaster);

                    Services.ElectionMessage newMessage = Services.ElectionMessage.newBuilder()
                            .setMaster(newMaster)
                            .addAllListOfDrones(newList)
                            .build();
                    boolean result = CommunicationModule.sendElectionMessageToTheNextInTheRing(drone, next, newMessage);
                }
                else
                {
                    // If the current drone's battery level is equal to the one than we have in the message
                    // and the current drone's ID is smaller than the master's one then we have to simply forward the message
                    Drone next = drone.getSmartcity().getNext();

                    Services.Drone newMaster = Drone.convertDroneToServicesDrone(drone);
                    List<Services.Drone> newList = new ArrayList<>(request.getListOfDronesList());
                    newList.add(newMaster);
                    Services.ElectionMessage newMessage = Services.ElectionMessage.newBuilder()
                            .setMaster(request.getMaster())
                            .addAllListOfDrones(newList)
                            .build();
                    boolean result = CommunicationModule.sendElectionMessageToTheNextInTheRing(drone, next, newMessage);
                }
            }

        }
        else {
            // If the drone is already participant we have to distinguish between two cases:
            //  - current drone has a battery level (or ID in case battery level is equal) that it's bigger than the one
            //      present inside the message;
            if ((drone.getBattery() > request.getMaster().getBattery())
                    ||
                    (drone.getBattery() == request.getMaster().getBattery() && drone.getID().compareTo(UUID.fromString(request.getMaster().getId())) > 0)) {
                // then we have that the current drone has an important battery level or ID, and it's present was already
                // forwarded into the net thus we don't have to do anything.
                System.out.println("[ELECTION]  Already participant to a election . . . blocking this election");
            }
            //  - current drone has a battery level (or ID in case battery level is equal) that's lower than the one
            //      present inside the message;
            else if((drone.getBattery() < request.getMaster().getBattery())
                    ||
                    (drone.getBattery() == request.getMaster().getBattery() && drone.getID().compareTo(UUID.fromString(request.getMaster().getId())) < 0)) {
                // then we have that the current drone has a less important battery level or ID, and it's presence was already
                // forwarded into the net thus we have to simply forward the message but, without adding this drone to the list
                // present into the message
                Services.ElectionMessage newMessage = Services.ElectionMessage.newBuilder()
                        .setMaster(request.getMaster())
                        .addAllListOfDrones(request.getListOfDronesList())
                        .build();
                System.out.println("[ELECTION]  Already participant to a election . . . forwarding the message");
                CommunicationModule.sendElectionMessageToTheNextInTheRing(drone, drone.getSmartcity().getNext(), newMessage);
            }
            else if(request.getMaster().getId().equals(drone.getID().toString())){
                //drone.setMasterFlag(true);
                System.out.println("[ELECTION] This drone won the election. Setting as master . . .");

                Services.ElectedMessage message = Services.ElectedMessage.newBuilder()
                                .setMaster(Drone.convertDroneToServicesDrone(drone)).build();
                Drone next = drone.getSmartcity().getNext();

                CommunicationModule.sendElectedMessageToTheNextInTheRing(drone, next, message);
            }
        }
    }


    @Override
    public void elected(Services.ElectedMessage request, StreamObserver<Services.Empty> responseObserver) {
        // If the drone's id contained into the message is the same of the current one then the broadcast has finished
        if(UUID.fromString(request.getMaster().getId()).compareTo(drone.getID()) == 0){
            System.out.println("[ELECTED] Elected message has been sent to all the drones into the net");
            System.out.println("[ELECTED] Starting master's duties ");
            drone.setMasterFlag(true);
            drone.setParticipantToElection(false);
        }
        // If the drone's id contained into the message is different from the one that is receiving the message
        // then we have to save the information and forward the message into the net
        else
        {
            System.out.println("[ELECTED] Elected message received");
            drone.setMasterDrone(Drone.convertServicesDroneToDrone(request.getMaster()));
            drone.setParticipantToElection(false);
            System.out.println("[ELECTED] New master is drone with id: " + drone.getMasterDrone().getID() + " and port: " + drone.getMasterDrone().getPort());

            //TODO: GESTIRE CASO FALLISCE PROSSIMO

            Drone next = drone.getSmartcity().getNext();
            //TODO: CON QUESTA
            Boolean response = CommunicationModule.sendElectedMessageToTheNextInTheRing(drone, next, request);
        }
    }
}
