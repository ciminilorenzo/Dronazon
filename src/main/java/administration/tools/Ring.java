package administration.tools;

import administration.resources.drone.Drone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Ring
{
    private ArrayList<Drone> droneArrayList;
    Comparator<Drone> droneComparator = Comparator.comparing(Drone::getBattery).thenComparing(Drone::getID);

    public Ring() {}


    public void insertDrone(Drone drone) {
        if (droneArrayList.size() == 0) {
            droneArrayList.add(drone);
            return;
        }

        droneArrayList.add(drone);
        Collections.sort(droneArrayList, droneComparator);


    }



    @Override
    public String toString(){
        String result = "\n[PRINTING RING] \n";

        for (Drone drone:droneArrayList){
            result += drone.toString();
        }
        return result;
    }
}
