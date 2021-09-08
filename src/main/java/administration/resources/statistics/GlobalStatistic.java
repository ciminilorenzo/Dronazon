package administration.resources.statistics;



import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.xml.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.*;

@XmlRootElement(name="statistic")
@XmlAccessorType(XmlAccessType.FIELD)
public class GlobalStatistic
{
    @XmlElement
    private double          deliveryAvg;

    @XmlElement
    private double          distanceAvg;

    @XmlElement
    private double          pollutionAvg;

    @XmlElement
    private double          batteryAvg;

    @XmlElement
    private long            timeStamp;



    /**
     * deliveryAvg -> AVERAGE OF DELIVERY COMPLETED BY DRONES
     *      * distanceAvg -> AVERAGE OF DISTANCE MADE BY DRONES
     *      * pollutionAvg -> AVERAGE OF POLLUTION LEVEL ACQUIRED BY DRONES
     *      * batteryAvg -> AVERAGE OF DRONES LEVEL BATTERY
     *      * timeStamp -> WHEN THE DELIVERY IS CALCULATED
     *
     * ASSUMPTION: GlobalStatic is calculated using as divisor the number of drones which are in the data received from the master drone
     *            and not the number of drones in the smartcity.
     *
     *
     * Everytime master drone sends the set of statistics received during each period (made by 10 seconds) it calculates
     * a new global statistic to send to the server administrator.
     *
     * The global statistic is then calculated in the following way:
     *  -   (1) : Some parameters are instantiated as we are going to need them for calculating the global statistic;
     *  -   (2) : A MultiMap (MultiMap can handle one key with a list of values) is instantiated for grouping drones' deliveries data using id of drones.
     *  -   (3) : For each drone the algorithm:
     *              -   (3.1) Uses a currentBattery param for saving the last information about drone's battery level (the one which is going to be used
     *                        for calculating the deliveryAvg;
     *              -   (3.2) Updates distance total;
     *              -   (3.3) Updates pollution total;
     *              -   (3.4) Updated total battery level.
     *
     * @param statistic represents the arraylist which contains the whole set of statistics sent by master drone.
     */
    public GlobalStatistic(ArrayList<Statistic> statistic){
        //  (1)
        double battery      = 0;

        //  (2)
        Multimap<String, Statistic> hashMap = ArrayListMultimap.create();
        for (Statistic current: statistic){
            hashMap.put(current.getId(), current);
        }

        //  (3)
        for (String currentKey: hashMap.keySet()) {
            ArrayList<Statistic> currentSetOfStatistics = new ArrayList<>(hashMap.get(currentKey));

            //  (3.\)
            double currentBattery = 0;

            for (Statistic current: currentSetOfStatistics) {
                //  (3.\)
                currentBattery = current.getBattery();
                //  (3.2)
                this.distanceAvg += current.getDistance();
                //  (3.3)
                this.pollutionAvg += current.getPollution();

            }

            // (3.4)
            battery += currentBattery;
        }

        int divisor = hashMap.keySet().size();
        this.deliveryAvg = statistic.size() / (double) divisor;
        this.distanceAvg = this.distanceAvg / divisor;
        this.pollutionAvg = this.pollutionAvg / divisor;
        this.batteryAvg = battery / divisor;
        this.timeStamp = new Date().getTime();;

    }


    private GlobalStatistic(){}

    public double getDeliveryAvg() {
        return deliveryAvg;
    }

    public double getDistanceAvg() {
        return distanceAvg;
    }

    public double getPollutionAvg() {
        return pollutionAvg;
    }

    public double getBatteryAvg() {
        return batteryAvg;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }


    public void setDeliveryAvg(double deliveryAvg) {
        this.deliveryAvg = deliveryAvg;
    }

    public void setDistanceAvg(double distanceAvg) {
        this.distanceAvg = distanceAvg;
    }

    public void setPollutionAvg(double pollutionAvg) {
        this.pollutionAvg = pollutionAvg;
    }

    public void setBatteryAvg(double batteryAvg) {
        this.batteryAvg = batteryAvg;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String toString(){
        Date date = new Date(this.getTimeStamp());

        return "[GLOBAL STATISTIC] \n" +
                "\t AVERAGE OF DELIVERY COMPLETED BY DRONES: " + this.deliveryAvg + "\n" +
                "\t AVERAGE OF DISTANCE MADE BY DRONES: " + this.distanceAvg + "\n" +
                "\t AVERAGE OF POLLUTION LEVEL ACQUIRED BY DRONES: " + this.pollutionAvg + "\n" +
                "\t AVERAGE OF DRONES LEVEL BATTERY: " + this.batteryAvg + "\n" +
                "\t ACQUIRED AT: " + date + "\n";
    }
}
