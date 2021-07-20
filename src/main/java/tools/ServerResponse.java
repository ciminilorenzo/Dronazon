package tools;

import drone.Drone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name="ServerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerResponse {

    @XmlElement(name="position")
    private Position position;

    @XmlElement(name="drones")
    private ArrayList<Drone> drones;

    private boolean errorFlag = false;

    private ServerResponse(){}

    public ServerResponse(Position position, ArrayList<Drone> drones, boolean errorFlag){
        this.position = position;
        this.drones = drones;
        this.errorFlag = errorFlag;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public ArrayList<Drone> getDrones() {
        return drones;
    }

    public void setDrones(ArrayList<Drone> drones) {
        this.drones = drones;
    }

    public boolean isErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }

    public String toString(){
        return "[RESPONSE FROM SERVER] \n" +
                "\t POSITION: " + getPosition() +
                "\n\t DRONES" + getDrones().toString();
    }


}
