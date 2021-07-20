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

    public ArrayList<Drone> getDrones(){
        return this.drones;
    }

    public void setDrones(ArrayList<Drone> drones) {
        this.drones = drones;
    }




    /**
     *  Simply checking that the drone who wants to enter has unique ID and unique PORT number.
     *  If the assumptions are true method is gonna return a ServerResponse object composed by: drones already in
     *  - without himself -, acquired position and a flag used to understand if the insertion has been successful.
     *
        @param drone who wants to enter into the smartcity.
        TODO: CONCURRENCY CHECK HERE MUST BE DONE

     */
    public synchronized ServerResponse insertDrone(Drone drone) {
        final ArrayList<Drone> listToReturn = new ArrayList<>(drones);
        final Position positionToReturn = Position.getRandomPosition();

        for (Drone current: drones) {
            if(current.getID() == drone.getID() || current.getPort() == drone.getPort()){
                return new ServerResponse(null, null, true);
            }
        }

        ServerResponse response = new ServerResponse(positionToReturn,listToReturn, false);
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
