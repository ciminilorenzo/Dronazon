package administration.resources;

import administration.tools.Position;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

/**
 * TODO: Create a thread for this process which allow to quit
 */

@XmlRootElement
public class Drone
{
    final UUID              ID = UUID.randomUUID();
    final int               port = getUniquePort();
    Position               position;
    final static String     serverAddress = "http://localhost:1337/";
    int                    battery = 100;

    public Drone(){}

    public Drone(Position position, int battery){
        this.position = position;
        this.battery = battery;
    }

    public static void main(String[] argv) {
        Drone drone = new Drone();
        getAdministratorAuthorizationToEnter(drone);
    }


    private UUID getID(){
        return ID;
    }

    private int getPort(){
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
        return "DRONE " +       this.ID + "\n" +
                "\t PORT: " +   this.port + "\n" +
                "\t POSITION: " + this.port;
    }


    private static void getAdministratorAuthorizationToEnter(Drone drone){
        final Client    client = Client.create();
        final String    insertionAddress = serverAddress + "insertion";
        WebResource     webResource = client.resource(insertionAddress);
        String          input = new Gson().toJson(drone);
        ClientResponse        response;

        try
        {
            response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);
            System.out.println(response.toString());
        }
        catch (Exception exception){
            System.out.println("[SERVER ADMINISTRATOR ERROR] \n" +
                    "\t" + exception.getMessage() +
                    "\t" + exception.getCause());
        }

    }
}
