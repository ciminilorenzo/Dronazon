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
    private final Drone masterDrone;

    public PingModule(Drone drone, Drone masterDrone){
        this.drone = drone;
        this.masterDrone = masterDrone;
        System.out.println("[PING MODULE] Started");
    }

    public void run(){
        ping();
    }


    private void ping(){
        try
        {
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:" + masterDrone.getPort()).usePlaintext().build();
            ChattingGrpc.ChattingBlockingStub chattingBlockingStub = ChattingGrpc.newBlockingStub(managedChannel);
            Services.Empty response =  Services.Empty.newBuilder().build();
            chattingBlockingStub.ping(response);
            managedChannel.shutdown();
        }
        catch (StatusRuntimeException exception){
            System.out.println("[PING MODULE] Master drone has fallen.");
            drone.setMasterDrone(null);
        }
    }
}
