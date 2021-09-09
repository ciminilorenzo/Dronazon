package drone.modules;

import drone.Drone;
import grpc.ChattingGrpc;
import grpc.Services;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 * ASSUNTION:   No random timeout is needed since every drone, as synchronizations exists, must enter into the smartcity
 *              in a different moment.
 */
public class PingModule extends Thread
{
    private final Drone drone;
    private Drone masterDrone;

    public PingModule(Drone drone, Drone masterDrone){
        this.drone = drone;
        this.masterDrone = masterDrone;
    }

    public void run(){
        ping();
    }


    private void ping(){
        try
        {
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + masterDrone.getPort()).usePlaintext().build();
            ChattingGrpc.ChattingBlockingStub chattingBlockingStub = ChattingGrpc.newBlockingStub(managedChannel);
            Services.PingRequest response =  Services.PingRequest.newBuilder().build();
            chattingBlockingStub.ping(response);
        }
        catch (StatusRuntimeException exception){
            System.out.println("[PING MODULE] Master drone has fallen.");
            // That's done in order to stop this module
            drone.setMasterDrone(null);
            //TODO: ELEZIONE
        }
    }
}
