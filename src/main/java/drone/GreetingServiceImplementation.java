package drone;

import administration.resources.statistics.Statistic;
import drone.modules.DeliveryModule;
import tools.Delivery;
import tools.Position;
import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.stub.StreamObserver;

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
     * Assertion: This method is called just in case in which this drone is the master one when it has to perform a
     * delivery assignment
     *
     * As soon as master drone is unable to determine which one is the delivery drone, it makes a grpc call to it.
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
}
