package administration.resources.drone;

import administration.tools.Position;
import administration.tools.ServerResponse;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * TODO: Create a thread for this process which allow to quit
 */

@XmlRootElement(name="drone")
@XmlAccessorType(XmlAccessType.FIELD)
public class Drone
{
    @XmlElement
    final UUID              ID = UUID.randomUUID();

    @XmlElement
    final int               port = getUniquePort();

    @XmlElement
    Position               position;

    @XmlElement
    int                    battery = 100;

    @Ignore
    final static String     serverAddress = "http://localhost:1337/";

    ArrayList<Drone>       smartCityDrones = new ArrayList<>();



    public Drone(){}

    public static void main(String[] argv) {
        Drone drone = new Drone();
        System.out.println("*************** STARTING NEW DRONE ***************" + drone + "\n\n\n");
        getAdministratorAuthorizationToEnter(drone);
        System.out.println(drone.smartCityDrones);
    }


    public UUID getID(){
        return ID;
    }

    public int getPort(){
        return port;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    /**
     * @return a port number randomly taken between specified bounds.
     */
    private int getUniquePort(){
        return (int) (Math.random() * (65536 - 49152)) + 49151;
    }

    public String toString(){
        return "\n" +
                "\t ID: "       + this.ID + "\n" +
                "\t PORT: "     + this.port + "\n" +
                "\t POSITION: " + this.position + "\n" +
                "\t BATTERY: "  + this.battery + "\n";
    }


    /**
     * TODO: JAVA DOC
     * @param drone Drone which wants to enter into the smartcity
     */
    private static void getAdministratorAuthorizationToEnter(Drone drone){
        System.out.println("*************** TRYING TO ENTER INTO THE SMARTCITY ***************");
        final Client    client = Client.create();
        final String    insertionAddress = serverAddress + "smartcity/insertion";
        WebResource    webResource = client.resource(insertionAddress);
        String         input = new Gson().toJson(drone);
        ClientResponse response;

        try
        {
            response = webResource.type("application/json").post(ClientResponse.class, input);
            ServerResponse serverResponse = response.getEntity(ServerResponse.class);

            if(serverResponse.getDrones() == null)
            {
                System.out.println(
                        " ----- FROM SERVER: \n" +
                        "\tNEW POSITION ACQUIRED: " + serverResponse.getPosition() + "\n" +
                        "\tCURRENTLY THIS DRONE IS THE ONLY ONE INTO THE SMARTCITY");
            }
            else
            {
                System.out.println(
                        " ----- FROM SERVER: \n"                +
                        "\tNEW POSITION ACQUIRED: "             + serverResponse.getPosition() + "\n" +
                        "\tDRONES ALREADY IN OVER THIS ONE: "   + serverResponse.getDrones().toString());
                drone.smartCityDrones = serverResponse.getDrones();
            }
            drone.position = serverResponse.getPosition();
            System.out.println(" ----- DRONE HAS SUCCESSFULLY ENTERED INTO THE SMARTCITY \n\n");
        }
        catch (ClientHandlerException exception){
            System.out.println("[ERROR DURING INSERTION PHASE] \n" +
                    "\n\t" + exception              +
                    "\n\t" + exception.getMessage() +
                    "\n\t" + exception.getCause());
            exception.printStackTrace();
        }
    }
}
