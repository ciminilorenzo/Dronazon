package tools;

import drone.Drone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Comparator;


/**
 * This class gives instances of 'RING'. A Ring is a representation of each drone's view of the smartcity.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Ring
{
    @XmlElement(name="drones")
    private ArrayList<Drone> droneArrayList = new ArrayList<>();

    public Ring(ArrayList<Drone> drones) {
        this.droneArrayList = drones;
    }

    public Ring(){}

    public ArrayList<Drone> getDroneArrayList() {
        return droneArrayList;
    }

    public void setDroneArrayList(ArrayList<Drone> droneArrayList) {
        this.droneArrayList = droneArrayList;
    }




    /**
     *
     * @param drone that is entering into the drone smartcity's representation.
     */
    public void insertDrone(Drone drone) {
        System.out.println("\n\n[RING] INSERTING NEW DRONE");
        droneArrayList.add(drone);
        droneArrayList.sort(Comparator.comparing(Drone::getPort));
        System.out.println("[RING] JUST ORDERED");
        System.out.println(this);
    }

    public void insertListOfDrones(ArrayList<Drone> list){
        System.out.println("\n\n[RING] INSERTING BUNCH OF DRONES");
        droneArrayList.addAll(list);
        droneArrayList.sort(Comparator.comparing(Drone::getPort));;
        System.out.println(this);
    }


    public ArrayList<Drone> getDroneArrayListFromRing(){
        return this.droneArrayList;
    }


    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("[RING] Printing... \n");

        for (Drone drone:droneArrayList){
            result.append("\t ---> ").append(drone.getPort());
        }
        return result.toString();
    }

}
