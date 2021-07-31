package tools;

import drone.Drone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;


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


    //  TODO: getNext() is currently returning next drone in the ring representation. This method must implement
    //      a gRPC 'ping' call in order to detect if the next drone is available or not. In the case it isn't the ring must be
    //      rebuild and getNext() must be recalled.
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



    // Assertion: This method is called by master drone after receiving delivery's data from deliverer drone for updating his view.
    public void modifyDroneAfterDelivery(UUID id, Position position, int battery, boolean busy){
        for (Drone current: droneArrayList) {
            if(current.getID().compareTo(id) == 0){
                current.setPosition(position);
                current.setBattery(battery);
                current.setBusy(false); // This procedure is called both in the master and drone view.
                System.out.println("[MASTER RING] DRONE WITH ID: " + id + "HAS JUST UPDATED HIS DATA WITH: position -> " + position + "; battery -> " + battery + "; busy ->" + busy);
                return;
            }
        }
    }

    public void setBusy(UUID id){
        for(Drone current: droneArrayList){
            if(current.getID().compareTo(id) == 0){
                current.setBusy(true);
            }
        }
    }

    public int getNumberOfDrones(){
        return this.droneArrayList.size();
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
