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
 * TODO: Shall we synchronize this data structure? Calls are received only from updateView() in GreetingServiceImplementation for now.
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


    public Drone getNext(Drone drone){
        if(droneArrayList.size() == 1){
            return null;
        }

        for(int i = 0; i < droneArrayList.size(); i++){
            if(drone.getID() == droneArrayList.get(i).getID()){
                return droneArrayList.get(i+1);
            }
            if(i == droneArrayList.size() - 1){
                return droneArrayList.get(0);
            }
        }
        return null;
    }


    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("[RING] Printing... \n");

        for(int i = 0; i < droneArrayList.size(); i++)
        {
            if(i == 0) result.append("\t").append(droneArrayList.get(i).getPort());
            else result.append("\t --->\t").append(droneArrayList.get(i).getPort());
        }

        for(int i = 0; i < droneArrayList.size(); i++)
        {
            if(i == 0) result.append("\n\t|______");
            else if(i == droneArrayList.size() -1) result.append("___________________|");
            else result.append("____________");
        }
        return result.toString();
    }

}
