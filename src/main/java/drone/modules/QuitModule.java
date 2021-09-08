package drone.modules;

import administration.resources.SmartCity;
import administration.services.SmartCityService;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import drone.Drone;
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
                        System.out.println("[QUIT MODULE]   Drone must assign " + drone.getMasterThread().deliveryNotAssigned.size() + " deliveries yet");
                        synchronized (dummyObject){
                            dummyObject.wait();
                        }
                    }
                    System.out.println("[QUIT MODULE]   Deliveries are all assigned now");
                }

                // If the drone is still delivering this makes the process wait until the delivery is successful complete
                if(drone.getDeliveryModule().isAlive()){
                    System.out.println("[QUIT MODULE]   Drone is delivering now ... must wait");
                    // Waits until the delivery is finished.
                    drone.getDeliveryModule().join();
                    drone.isBusy = true;
                }
                quitFromTheSmartCity(drone);

            }
            catch (InterruptedException | MqttException exception){
                System.out.println("[QUIT MODULE] Exception detected");
                System.out.println(exception.getMessage());
            }
        }
        else
        {
            try
            {
                // If the drone is still delivering this makes the process wait until the delivery is successful complete
                if(drone.getDeliveryModule().isAlive()){
                    System.out.println("[QUIT MODULE]   Drone is delivering now ... must wait");
                    // Waits until the delivery is finished.
                    drone.getDeliveryModule().join();
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
