package administration.resources;


import drone.Drone;
import tools.Position;
import tools.ServerResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;



@XmlRootElement(name="smartcity")
@XmlAccessorType(XmlAccessType.FIELD)
public class SmartCity
{
    private static SmartCity smartCity = null;


    @XmlElement(name="drones")
    private ArrayList<Drone> drones;

    private SmartCity(){
        drones = new ArrayList<>();
    }

    public synchronized static SmartCity getInstance(){
        if(smartCity == null){
            smartCity = new SmartCity();
        }
        return smartCity;
    }

    public synchronized ArrayList<Drone> getDrones(){
        return drones;
    }

    public synchronized void setDrones(ArrayList<Drone> drones) {
        this.drones = drones;
    }

    private synchronized void addDrone(Drone drone){
        drones.add(drone);
    }




    /**
     *  Checking that the drone who wants to enter has unique ID and unique PORT number.
     *  If the assumptions are true method is going to return a ServerResponse object composed by: drones already in
     *  - without himself -, acquired position and a flag used to understand if the insertion has been successful.
     *
        @param drone which wants to enter into the smartcity.
     */
    public ServerResponse insertDrone(Drone drone) {

        ArrayList<Drone> listOfDronesAlreadyIn = new ArrayList<>(getDrones());

        for (Drone current: listOfDronesAlreadyIn) {
            if(current.getID().compareTo(drone.getID()) == 0 || current.getPort() == drone.getPort()){
                return new ServerResponse(null, null, true);
            }
        }

        final Position positionToReturn = Position.getRandomPosition();
        ServerResponse response = new ServerResponse(positionToReturn, listOfDronesAlreadyIn, false);
        drone.setPosition(positionToReturn);
        addDrone(drone);
        return response;
    }

    // Since we are trying to remove a not native object we have to implement the method in the following way
    public synchronized void takeOutDrone(Drone drone){
        for (Drone drone1: drones) {
            if(drone1.getID().equals(drone.getID())){
                drones.remove(drone1);
                return;
            }
        }
    }

    public String toString(){
        StringBuilder result = new StringBuilder();
        ArrayList<Drone> copy = getDrones();

        result.append("[PRINTING SMART CITY POPULATION]");
        for (Drone current:copy) {
            result.append("\n\t ID:\t").append(current.getID()).append("\n\t PORT:\t").append(current.getPort()).append("\n\t POSITION:\t").append(current.getPosition()).append("\n\t BATTERY:\t").append(current.getBattery());
        }
        return result.toString();
    }
}
