package drone;

import PM10.Measurement;
import PM10.PM10Queue;
import PM10.PM10Simulator;
import administration.resources.statistics.Statistic;
import drone.modules.*;
import tools.*;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@XmlRootElement(name="Drone")
@XmlAccessorType(XmlAccessType.FIELD)
public class Drone implements EventListener
{
    @XmlElement
    private UUID ID = UUID.randomUUID();

    @XmlElement
    private int port = getUniquePort();

    @XmlElement
    private Position position;

    @XmlElement
    private int battery = 100;

    private transient final static String serverAddress = "http://localhost:1337/";

    private transient Ring smartcity;

    private transient int numberOfDeliveryDone = 0;

    private transient double distanceMade = 0.0;

    // Flag used to set if the current drone is the master or not.
    private transient volatile boolean masterFlag = false;

    // Pointer to the master drone.
    private transient Drone masterDrone;

    // Flag used by master drone for understanding if a specific drone is already doing a delivery or not.
    public transient volatile boolean isBusy = false;

    // Data structure used by master drone for saving drones' statistics.
    private transient ArrayList<Statistic> masterDroneStatistics;

    private transient MasterModule masterThread;

    private transient QuitModule quitModule;

    private transient DeliveryModule deliveryModule;

    private transient ScheduledExecutorService pingModule;

    public  transient  ArrayList<Double> measurementsDataStructure = new ArrayList<>();
    private static transient final Object measurementArrayDummyLock = new Object();

    private transient PM10Queue measurements;

    private transient PM10Simulator simulator;




    public QuitModule getQuitModule() {
        return quitModule;
    }

    public void setQuitModule(QuitModule quitModule) {
        this.quitModule = quitModule;
    }

    public DeliveryModule getDeliveryModule() {
        return deliveryModule;
    }

    public void setDeliveryModule(DeliveryModule deliveryModule) {
        this.deliveryModule = deliveryModule;
    }

    public ArrayList<Double> getMeasurementsDataStructure(){
        synchronized (measurementArrayDummyLock){
            return measurementsDataStructure;
        }
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        if(battery < 20 && !busy){
            System.out.println("[DRONE MODULE -> "+  Thread.currentThread().getId() + "] Drone's battery is low. Starting quitting process");
            // Drone can't deliver any delivery anymore
            isBusy = true;
            this.quitModule.exit();
        }
        else{
            System.out.println("[DRONE MODULE -> "+  Thread.currentThread().getId() + "] Setting busy flag of this drone as: " + busy);
            isBusy = busy;
        }

        // Each time master drone finishes his delivery checks if there is an undelivered delivery
        if(this.isMasterFlag() && !busy){
            communicateAvailability();
        }
    }

    public MasterModule getMasterThread() {
        return masterThread;
    }

    public void setMasterThread(MasterModule masterThread) {
        this.masterThread = masterThread;
    }

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

    public ScheduledExecutorService getPingModule() {
        return pingModule;
    }

    public void setPingModule(ScheduledExecutorService pingModule) {
        this.pingModule = pingModule;
    }



    /**
     * If the current drone is the master drone then:
     *  -   A new MasterModule is going to start for satisfying master's services;
     *  -   Master's statistics data structure is going to be initialized;
     *  -   A new GlobalStatisticsScheduledPrinter is going to be started for sending master's statistics to the administrator server each 10 seconds.
     *
     *
     * @param masterFlag flag used to say if a drone is the master one or not.
     */
    public void setMasterFlag(boolean masterFlag) {
        if(masterFlag){
            masterThread = new MasterModule(this);
            masterThread.start();
            this.masterDroneStatistics = new ArrayList<>();

            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleAtFixedRate(new GlobalStatisticsScheduledPrinter(this), 10,10, TimeUnit.SECONDS);
        }
        this.masterFlag = masterFlag;
    }

    public Drone getMasterDrone() {
        return masterDrone;
    }

    public void setMasterDrone(Drone masterDrone) {
        if(masterDrone != null){
            this.masterDrone = masterDrone;
            ScheduledExecutorService pingScheduler = Executors.newScheduledThreadPool(1);
            pingScheduler.scheduleAtFixedRate(new PingModule(this, masterDrone), 10,10, TimeUnit.SECONDS);
            this.pingModule = pingScheduler;
        }
        else {
            if(!this.pingModule.isShutdown()){
                this.pingModule.shutdown();
                this.pingModule = null;
            }
        }

    }

    public ArrayList<Statistic> getMasterDroneStatistics() {
        return masterDroneStatistics;
    }

    public void setMasterDroneStatistics(ArrayList<Statistic> masterDroneStatistics) {
        this.masterDroneStatistics = masterDroneStatistics;
    }

    public void addStatisticToMasterDroneDataStructure(Statistic statistic){
        if(getMasterDroneStatistics() != null){
            System.out.println("[DRONE MODULE -> "+  Thread.currentThread().getId() + "] New statistic has just been received and inserted into the master drone data structure");
            this.masterDroneStatistics.add(statistic);
        }
    }

    public String toString(){
        return "\n" +
                "\t ID: "       + this.ID + "\n" +
                "\t PORT: "     + this.port + "\n" +
                "\t POSITION: " + this.position + "\n" +
                "\t BATTERY: "  + this.battery + "\n";
    }






    public Drone(UUID id, int port, Position position){
        this.ID = id;
        this.port = port;
        this.position = position;
    }

    private Drone(){}






    public static void main(String[] argv) {
        Drone       drone = new Drone();
        // Setting quit thread to allow the user to quit.
        drone.quitModule = new QuitModule(drone);
        drone.quitModule.start();

        System.out.println("*************** STARTING NEW DRONE ***************" + drone + "\n\n\n");

        // Entering into the smartcity
        drone.getAdministratorAuthorizationToEnter(drone);

        // Starting drone's communication module
        CommunicationModule communicationModule = new CommunicationModule(drone);
        communicationModule.start();

        ScheduledExecutorService dataPrinter = Executors.newScheduledThreadPool(1);
        dataPrinter.scheduleAtFixedRate(new DataPrinterModule(drone), 10,10, TimeUnit.SECONDS);

        drone.measurements = new PM10Queue(drone);
        drone.simulator = new PM10Simulator(drone.measurements);
        drone.simulator.start();

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
    private void getAdministratorAuthorizationToEnter(Drone drone){
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


                //    Se here I'm receiving the list from the ServerAdministration and creating my own representation of the
                //    smartcity.

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

    //  This method is called in the following scenarios:
    //      -   Master drone becomes not busy
    //      -   New drone enters into the smartcity
    //      -   Master drone receives delivery confirmation by one of the smartcity's drones
    public void communicateAvailability(){
        if(masterThread != null) {
            System.out.println("[DRONE MODULE -> "+  Thread.currentThread().getId() + "] Checking presence or not of undelivered deliveries");
            this.masterThread.checkIfDelivery();
        }
    }


    @Override
    public void takeEightMeasurements() {
        List<Measurement> measurements = this.measurements.readAllAndClean();
        this.measurementsDataStructure.add(measurements.stream().mapToDouble(Measurement::getValue).sum() / measurements.size());
    }
}
