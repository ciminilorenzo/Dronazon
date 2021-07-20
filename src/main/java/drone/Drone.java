package drone;

import tools.CityMap;
import tools.Position;
import tools.Ring;
import tools.ServerResponse;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.UUID;


@XmlRootElement(name="Drone")
@XmlAccessorType(XmlAccessType.FIELD)
public class Drone
{
    @XmlElement
    private UUID ID = UUID.randomUUID();

    @XmlElement
    private int port = getUniquePort();

    @XmlElement
    private Position position;

    @XmlElement
    private int battery = 100;

    private final static String serverAddress = "http://localhost:1337/";

    private Ring smartcity;

    private int numberOfDeliveryDone = 0;

    private double distanceMade = 0.0;

    private boolean masterFlag = false;

    private Drone masterDrone;


    public double getDistanceMade(){ return this.distanceMade; }

    public Ring getSmartcity() {
        return smartcity;
    }

    public void setSmartcity(Ring smartcity) {
        this.smartcity = smartcity;
    }

    public void setDistanceMade(double distanceMade){ this.distanceMade = distanceMade; }

    public int getNumberOfDeliveryDone() {
        return numberOfDeliveryDone;
    }

    public void setNumberOfDeliveryDone(int numberOfDeliveryDone) {
        this.numberOfDeliveryDone = numberOfDeliveryDone;
    }

    public void setID(UUID id){
        this.ID = id;
    }

    public UUID getID(){
        return ID;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
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

    public boolean isMasterFlag() {
        return masterFlag;
    }

    public void setMasterFlag(boolean masterFlag) {
        if(masterFlag){
            MasterThread masterThread = new MasterThread(this);
            masterThread.start();
        }
        this.masterFlag = masterFlag;
    }

    public Drone getMasterDrone() {
        return masterDrone;
    }

    public void setMasterDrone(Drone masterDrone) {
        this.masterDrone = masterDrone;
    }

    public Drone(UUID id, int port, Position position){
        this.ID = id;
        this.port = port;
        this.position = position;
    }


    public String toString(){
        return "\n" +
                "\t ID: "       + this.ID + "\n" +
                "\t PORT: "     + this.port + "\n" +
                "\t POSITION: " + this.position + "\n" +
                "\t BATTERY: "  + this.battery + "\n";
    }



    private Drone(){}

    public static void main(String[] argv) {
        Drone       drone = new Drone();
        // Setting quit thread to allow the user to quit.
        QuitThread quitThread = new QuitThread();
        quitThread.start();

        System.out.println("*************** STARTING NEW DRONE ***************" + drone + "\n\n\n");

        // Entering into the smartcity
        getAdministratorAuthorizationToEnter(drone);

        // Starting drone's communication module
        CommunicationModule communicationModule = new CommunicationModule(drone);
        communicationModule.start();

        //TODO: SET OF FOR DEBUGGING PURPOSES
        // Setting scheduled task for printing each 10 seconds drone's data.
        //ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        //scheduledExecutorService.scheduleAtFixedRate(new DataPrinter(drone), 10,10, TimeUnit.SECONDS);

    }


    /**
     * @return a port number randomly taken between specified bounds.
     */
    private int getUniquePort(){
        return (int) (Math.random() * (65536 - 49152)) + 49151;
    }

    /**
     * This method tries to let the drone enter into the smartcity. This method will make a POST request at the address
     * http://localhost:1337/smartcity/insertion. If the drone's ID and the drone's PORT are unique the server will
     * insert the drone in and will send back all the information required: position and list of drones already in.
     *
     * @param drone Drone which wants to enter into the smartcity.
     */
    private static void getAdministratorAuthorizationToEnter(Drone drone){
        System.out.println("*************** TRYING TO ENTER INTO THE SMARTCITY ***************");
        final Client    client = Client.create();
        final String    insertionAddress = serverAddress + "smartcity/insertion";
        WebResource    webResource = client.resource(insertionAddress);
        String         input = new Gson().toJson(drone);
        ClientResponse response;
        drone.smartcity = new Ring();

        try
        {
            response = webResource.type("application/json").post(ClientResponse.class, input);
            ServerResponse serverResponse = response.getEntity(ServerResponse.class);
            ArrayList<Drone> dronesAlreadyIn = serverResponse.getDrones();

            if(dronesAlreadyIn == null)
            {
                System.out.println(
                        " ----- FROM SERVER: \n" +
                        "\tNEW POSITION ACQUIRED: " + serverResponse.getPosition() + "\n" +
                        "\tCURRENTLY THIS DRONE IS THE ONLY ONE INTO THE SMARTCITY");
                drone.setMasterFlag(true);
                drone.smartcity.insertDrone(drone);
            }
            else
            {
                System.out.println(
                        " ----- FROM SERVER: \n"                +
                        "\tNEW POSITION ACQUIRED: "             + serverResponse.getPosition() + "\n" +
                        "\tDRONES ALREADY IN OVER THIS ONE: "   + serverResponse.getDrones().toString());

                /*
                    Se here i'm receiving the list from the ServerAdministration and creating my own representation of the
                    smartcity.
                 */
                dronesAlreadyIn.add(drone);
                drone.smartcity.insertListOfDrones(dronesAlreadyIn);
            }

            drone.position = serverResponse.getPosition();
            CityMap.printPositionIntoTheSmartCity(drone.position);
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
