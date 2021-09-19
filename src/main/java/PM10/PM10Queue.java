package PM10;

import drone.Drone;

import java.util.ArrayList;
import java.util.List;

public class PM10Queue implements Buffer{

    private List<Measurement> measurementArrayList = new ArrayList<>();
    private final Object dummyArray = new Object();
    private final Drone drone;

    public PM10Queue(Drone drone){
        this.drone = drone;
    }

    @Override
    public void addMeasurement(Measurement m)
    {
        // ArrayList.add() isn't threadsafe
        synchronized (dummyArray){
            measurementArrayList.add(m);
        }

        if(measurementArrayList.size() == 8) {
            this.drone.takeEightMeasurements();
        }
    }

    @Override
    public List<Measurement> readAllAndClean() {
        synchronized (dummyArray){
            List<Measurement> result = measurementArrayList.subList(0,3);
            measurementArrayList = measurementArrayList.subList(4,8);
            return result;
        }
    }
}
