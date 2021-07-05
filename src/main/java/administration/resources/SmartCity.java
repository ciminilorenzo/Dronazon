package administration.resources;


import administration.resources.drone.Drone;
import administration.tools.Position;
import administration.tools.ServerResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * This class should contain the list of drones
 */

@XmlRootElement(name="smartcity")
@XmlAccessorType(XmlAccessType.FIELD)
public class SmartCity
{
    private static SmartCity smartCity = null;

    public void setDrones(ArrayList<Drone> drones) {
        this.drones = drones;
    }

    @XmlElement
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

    public ArrayList<Drone> getDrones(){
        return getInstance().drones;
    }

    /**
     * @param drone new drone that wants to enter in the smart city
     * @return 'ok' if the operation is successful else 'NOT_UNIQUE'
     */

    /*

        TODO: CONCURRENCY CHECK HERE MUST BE DONE
        TODO: JAVADOC
     */
    public synchronized ServerResponse insertDrone(Drone drone) {
        final ArrayList<Drone> listToReturn = new ArrayList<>(drones);
        final Position positionToReturn = Position.getRandomPosition();

        for (Drone current: drones) {
            if(current.getID() == drone.getID() || current.getPort() == drone.getPort()){
                return new ServerResponse(null, null, true);
            }
        }

        ServerResponse response = new ServerResponse(positionToReturn, listToReturn, false);
        drone.setPosition(positionToReturn);
        drones.add(drone);
        return response;
    }


    public String toString(){
        String result = "[PRINTING SMART CITY POPULATION]";
        for (Drone current:drones) {
            result += "\n\t ID:\t" + current.getID() +
                    "\n\t PORT:\t" + current.getPort() +
                    "\n\t POSITION:\t" + current.getPosition() +
                    "\n\t BATTERY:\t" + current.getBattery();
        }
        return result;
    }
}
