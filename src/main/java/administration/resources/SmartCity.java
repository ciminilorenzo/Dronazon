package administration.resources;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * This class should contain the list of drones
 */

@XmlRootElement
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
     * TODO: Implement insertion phase checks (e.g unique ID)
     * @param drone new drone that wants to enter in the smart city
     */
    public synchronized String insertDrone(Drone drone){
        drones.add(drone);
        return "ok";
    }

    public String toString(){
        String result = "[PRINTING SMART CITY POPULATION}";
        for (Drone current:drones) {
            result += "\n\t ID:\t" + current.ID +
                    "\n\t PORT:\t" + current.port +
                    "\n\t POSITION:\t" + current.getPosition() +
                    "\n\t BATTERY:\t" + current.battery;
        }
        return result;
    }
}
