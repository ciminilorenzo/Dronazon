package administration.tools;

import administration.resources.drone.Drone;
import administration.resources.statistics.GlobalStatistic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DronesServerResponse {
    @XmlElement
    private ArrayList<Drone> drones;

    public ArrayList<Drone> getDrones() {
        if(drones.size() == 0) return null;
        return drones;
    }

    public void setDrones(ArrayList<Drone> drones) {
        this.drones = drones;
    }

    private DronesServerResponse(){}

    public DronesServerResponse(ArrayList<Drone> drones){
        this.drones = drones;
    }

    public String toString(){
        return "[RESPONSE FROM SERVER]\n" +
                "\t" + this.getDrones();
    }
}
