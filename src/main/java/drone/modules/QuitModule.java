package drone.modules;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import drone.Drone;
import drone.GreetingServiceImplementation;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Scanner;


public class QuitModule extends Thread
{
    private final Scanner         scanner = new Scanner(System.in);
    private final Drone           drone;
    public  final Object          dummyObject = new Object();

    public QuitModule(Drone drone){
        this.drone = drone;
    }

    public void run() {
        System.out.println("[SYSTEM INFO] Type 'quit' to shutdown the drone.");
        try
        {
            // scanner.nextLine() is a blocking method.
            while(true)
            {
                if(scanner.nextLine().equals("quit"))
                {
                    System.out.println("[QUIT MODULE]   Drone is starting exit process");
                    exit();
                }
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }



    public void exit(){
        if(drone.isMasterFlag())
        {
            try
            {
                // This method is called in order to close the connection to the broker
                drone.getMasterThread().closeConnection();

                // This is done for waiting until all deliveries are assigned
                if(!drone.getMasterThread().deliveryNotAssigned.isEmpty()){
                    while(!drone.getMasterThread().deliveryNotAssigned.isEmpty()){
                        System.out.println("[QUIT MODULE]   Drone must assign " + drone.getMasterThread().deliveryNotAssigned.size() + " deliveries yet in order to quit");
                        synchronized (dummyObject){
                            dummyObject.wait();
                        }
                    }
                    System.out.println("[QUIT MODULE]   Deliveries are all assigned now");
                }

                // If the drone is still delivering this makes the process wait until the delivery is successful complete
                if(drone.getDeliveryModule() != null && drone.getDeliveryModule().isAlive()){
                    System.out.println("[QUIT MODULE]   Drone is delivering now . . . must wait");

                    // Waits until the delivery is finished.
                    //
                    // 1.   This timeout is set in order to be sure that the quit procedure goes ahead since last setBusy() inside
                    //      DeliveryModule will call setBusy() with a parameter that is lower than 20 thus will call this exit() method
                    //      and waiting his conclusion. All this makes the DeliveryModule not finish and, furthermore, without having DeliveryModule
                    //      concluded this method won't go ahead blocking himself during the check if whatever DeliveryModule is finished or not.
                    //
                    // 2.   This same kind of join is made inside the try/catch block related to the case in which se drone which wants to exit
                    //      is not the master one.
                    //
                    // 3.   This amount of time is set to be sure that both all data from other drone's coming back and
                    //      the delivery currently ongoing is finished.

                    drone.getDeliveryModule().join(5010);
                    drone.isBusy = true;
                    System.out.println("[QUIT MODULE]   Delivery has finished. Proceeding quit procedure . . .");
                }
                quitFromTheSmartCity(drone);

            }
            catch (InterruptedException | MqttException exception){
                System.out.println("[QUIT MODULE] Exception detected");
                System.out.println(exception.getMessage());
            }
        }
        // If it isn't the master drone:
        else
        {
            try
            {
                // If the drone is still delivering this makes the process wait until the delivery is successful complete
                if(drone.getDeliveryModule() != null && drone.getDeliveryModule().isAlive()){
                    System.out.println("[QUIT MODULE]   Drone is delivering now ... must wait");
                    // Waits until the delivery is finished.
                    drone.getDeliveryModule().join(5010);
                }

                // Flag used to know if this drone is most likely to become the next master
                while(GreetingServiceImplementation.getNextMaster()){
                    System.out.println("[QUIT MODULE]   Drone is most likely to be the next drone ... must wait");
                    synchronized (GreetingServiceImplementation.getDummyObjectElection()){
                        GreetingServiceImplementation.getDummyObjectElection().wait();
                    }
                }

                quitFromTheSmartCity(drone);
                System.exit(0);

            }
            catch (InterruptedException exception){
                System.out.println("[QUIT MODULE] Exception detected");
                System.out.println(exception.getMessage());
            }
        }
    }

    private void quitFromTheSmartCity(Drone drone){
        Client client = Client.create();
        String address = "http://localhost:1337/smartcity/delete";
        WebResource webResource = client.resource(address);
        String         input = new Gson().toJson(drone);
        ClientResponse response;

        try
        {
            response = webResource.type("application/json").delete(ClientResponse.class, input);
            System.out.println("[QUIT MODULE]   QUIT PROCESS RESULT: " + response);
            System.out.println("[QUIT MODULE]   Quitting . . .");
            System.exit(0);
        }
        catch (ClientHandlerException exception){
            System.out.println("[ERROR DURING EXIT PHASE] \n" +
                    "\n\t" + exception              +
                    "\n\t" + exception.getMessage() +
                    "\n\t" + exception.getCause());
            exception.printStackTrace();
        }
    }
}
