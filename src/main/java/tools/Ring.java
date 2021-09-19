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

    private final transient Object dummyObject = new Object();

    private Drone drone;

    public Ring(ArrayList<Drone> drones) {
        this.droneArrayList = drones;
    }

    public Ring(){}

    public  ArrayList<Drone> getDroneArrayList() {
        synchronized (dummyObject){
            return droneArrayList;
        }
    }


    public void cleanList(){
        synchronized (dummyObject) {
            this.droneArrayList.clear();
        }

    }



    public void insertDrone(Drone drone) {
        System.out.println("\n\n[RING] INSERTING NEW DRONE");
        synchronized (dummyObject)
        {
            droneArrayList.add(drone);
            droneArrayList.sort(Comparator.comparing(Drone::getPort));
        }
        System.out.println("[RING] JUST ORDERED");
        System.out.println(this);
    }

    public void insertListOfDrones(ArrayList<Drone> list){
        System.out.println("\n\n[RING] INSERTING BUNCH OF DRONES");
        synchronized (dummyObject)
        {
            droneArrayList.addAll(list);
            droneArrayList.sort(Comparator.comparing(Drone::getPort));;
        }
        System.out.println(this);
    }

    public void removeDrone(Drone droneToDelete){
        System.out.println("[RING] Updating . . .");

        synchronized (dummyObject){
            droneArrayList.remove(droneToDelete);
            droneArrayList.sort(Comparator.comparing(Drone::getPort));
        }
        System.out.println(this);
    }

    public boolean isAlone(){
        return (getDroneArrayList().size() == 1);
    }


    /**
     *
     * @return null if the drone sent as parameter hasn't a 'next' drone
     */
    public Drone getNext(){
        synchronized (dummyObject)
        {
            if(droneArrayList.size() == 1){
                return null;
            }

            for(int i = 0; i < droneArrayList.size(); i++){
                if(i == droneArrayList.size() - 1){
                    return droneArrayList.get(0);
                }
                else if(this.drone.getID() == droneArrayList.get(i).getID()){
                    return droneArrayList.get(i+1);
                }
            }
        }
        return null;
    }



    // Assertion: This method is called by master drone after receiving delivery's data from deliverer drone for updating his view.
    public void modifyDroneAfterDelivery(UUID id, Position position, int battery, boolean busy){
        synchronized (dummyObject)
        {
            for (Drone current: droneArrayList) {
                if(current.getID().compareTo(id) == 0){
                    current.setPosition(position);
                    current.setBattery(battery);
                    current.isBusy = busy; // This procedure is called both in the master and drone view.
                    return;
                }
            }
        }
    }

    public void setBusy(UUID id, boolean value){
        System.out.println("[MASTER RING] Drone with id: " + id + " has just updated his busy state as: " + value);
        synchronized (dummyObject){
            for(Drone current: droneArrayList){
                if(current.getID().compareTo(id) == 0){
                    current.isBusy = value;
                }
            }
        }
    }

    public int getNumberOfDrones(){
        return this.droneArrayList.size();
    }


    /**
     * Method used to get the list of drones except the one got as parameter.
     * @param drone that we don't want in the list that the method is going to return
     * @return the list with the whole list of drones except the one received as parameter.
     */
    public ArrayList<Drone> getListOfDronesWithoutOne(Drone drone){
        ArrayList<Drone> drones = getDroneArrayList();
        drones.remove(drone);
        return drones;
    }





    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        ArrayList<Drone> copy = getDroneArrayList();

        for(int i = 0; i < copy.size(); i++)
        {
            if(i == 0) result.append("\t").append(copy.get(i).getPort());
            else result.append("\t --->\t").append(copy.get(i).getPort());
        }

        for(int i = 0; i < copy.size(); i++)
        {
            if(i == 0) result.append("\n\t|______");
            else if(i == copy.size() -1) result.append("___________________|");
            else result.append("____________");
        }
        return result.toString();
    }

    public void setCurrentDrone(Drone drone){
        this.drone = drone;
        this.droneArrayList.add(drone);
    }

}
