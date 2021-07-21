package drone;

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
     *  This is an unary gRPC since the drone will send the single SimpleGreeting message and will receive one single
     *  SimpleGreetingResponse message.
     *
     */
    @Override
    public void simpleGreeting(Services.SimpleGreetingRequest request, StreamObserver<Services.SimpleGreetingResponse> responseStreamObserver)
    {
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
    }

    private void updateView(Services.SimpleGreetingRequest newDrone){
        Drone droneToInsert = new Drone(UUID.fromString(newDrone.getId()), newDrone.getPort(), new Position(newDrone.getPosition().getX(), newDrone.getPosition().getY()));
        this.drone.getSmartcity().insertDrone(droneToInsert);

    }

    /**
     * As soon as master drone is unable to determine which one is the delivery drone, it makes a grpc call to it.
     *
     * @param request DeliveryAssignationMessage used for communicating delivery information to the specified drone.
     * @param responseObserver stream observer
     */
    @Override
    public void deliveryAssignationService(Services.DeliveryAssignationMessage request, StreamObserver<Services.DeliveryAssignationResponse> responseObserver) {
        System.out.println("\n\n[DRONE COMMUNICATION MODULE - INPUT] DELIVERY ASSIGNATION - Preparing the drone to the delivery ");
        Services.DeliveryAssignationMessage.Delivery deliveryReceived = request.getDelivery();
        Delivery delivery = new Delivery(deliveryReceived.getId(), deliveryReceived.getPickup(), deliveryReceived.getDelivery());
        drone.setBusy(true);
        DeliveryModule deliveryModule = new DeliveryModule(drone, delivery);
        deliveryModule.start();+/
    }
}
