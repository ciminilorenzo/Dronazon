package administration.resources.statistics;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
    private String            timeStamp;


    /**
     *  Master will create a GlobalStatistics object to insert into StatisticsContainer.
     *
     * @param deliveryAvg -> AVERAGE OF DELIVERY COMPLETED BY DRONES
     * @param distanceAvg -> AVERAGE OF DISTANCE MADE BY DRONES
     * @param pollutionAvg -> AVERAGE OF POLLUTION LEVEL ACQUIRED BY DRONES
     * @param batteryAvg -> AVERAGE OF DRONES LEVEL BATTERY
     * @param timeStamp -> WHEN THE DELIVERY IS CALCULATED
     */
    public GlobalStatistic(double deliveryAvg, double distanceAvg, double pollutionAvg, double batteryAvg, String timeStamp)
    {
        this.deliveryAvg = deliveryAvg;
        this.distanceAvg = distanceAvg;
        this.pollutionAvg = pollutionAvg;
        this.batteryAvg = batteryAvg;
        this.timeStamp = timeStamp;
    }

    public GlobalStatistic(){}

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

    public String getTimeStamp() {
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

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String toString(){
        return "[GLOBAL STATISTIC] \n" +
                "\t AVERAGE OF DELIVERY COMPLETED BY DRONES: " + this.deliveryAvg + "\n" +
                "\t AVERAGE OF DISTANCE MADE BY DRONES: " + this.distanceAvg + "\n" +
                "\t AVERAGE OF POLLUTION LEVEL ACQUIRED BY DRONES: " + this.pollutionAvg + "\n" +
                "\t AVERAGE OF DRONES LEVEL BATTERY: " + this.batteryAvg + "\n" +
                "\t ACQUIRED AT: " + this.getTimeStamp() + "\n";
    }

}
