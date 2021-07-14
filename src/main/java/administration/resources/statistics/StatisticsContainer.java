package administration.resources.statistics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@XmlRootElement(name="container")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatisticsContainer
{
    private static StatisticsContainer instance = null;

    @XmlElement
    private ArrayList<GlobalStatistic> statistics = new ArrayList<>();

    private StatisticsContainer(){}

    public synchronized static StatisticsContainer getInstance(){
        if(instance == null){
            instance = new StatisticsContainer();
            instance.statistics.add(new GlobalStatistic(2.0,2.0,2.0,2.0, "11-07-1997 12:00:00" ));
        }
        return instance;
    }

    public synchronized  ArrayList<GlobalStatistic> getStatistics(){
        return this.statistics;
    }

    public synchronized void insertGlobalStatistic(GlobalStatistic statistic){
        this.statistics.add(statistic);
    }

    public synchronized void clearBuffer(){
        this.statistics.clear();
    }

    /*
        THIS CLASS OFFERS:
            -> Last smartcity's n global statistics (with timestamps);
            -> Average completed delivery made by drones in the smartcity between two timestamps;
            -> Average distance (km) made by drones in the smartcity between two timestamps.
     */


    /**
     *
     * @param numberOfStatisticsToRetrieve : number of statistics to retrieve from buffer.
     * @return last n statistics contained into the buffer.
     *
     * Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive.
     */

    public synchronized List<GlobalStatistic> getLastGlobalStatistics(int numberOfStatisticsToRetrieve){
        if(numberOfStatisticsToRetrieve <= statistics.size()){
            return statistics.subList((statistics.size())-numberOfStatisticsToRetrieve, statistics.size());
        }
        else return null;

    }


    /**
     *
     * @param firstBound : first time bound
     * @param secondBound : second time bound
     * @return average completed delivery made by drones in the smartcity between two timestamps.
     */
    public String getDeliveryAverage(Date firstBound, Date secondBound) throws ParseException {
        ArrayList<GlobalStatistic> copy = getBetweenBounds(firstBound, secondBound);
        double sum = 0;

        for (GlobalStatistic current: copy) {
            sum = sum + current.getDeliveryAvg();
        }

        if(sum == 0) return "NO DATA BETWEEN THESE TWO BOUNDS";
        return String.valueOf(sum / copy.size());
    }


    /**
     *
     * @param firstBound : : first time bound
     * @param secondBound : : second time bound
     * @return : Average distance (km) made by drones in the smartcity between two timestamps
     */
    public String getDistanceAverage(Date firstBound, Date secondBound) throws ParseException {
        ArrayList<GlobalStatistic> copy = getBetweenBounds(firstBound, secondBound);
        double sum = 0;

        for (GlobalStatistic current: copy) {
            sum = sum + current.getDistanceAvg();
        }

        if(sum == 0) return "NO DATA BETWEEN THESE TWO BOUNDS";
        return String.valueOf(sum / copy.size());
    }

    /**
     *
     * @return new arraylist which contains global statistics between bounds.
     */
    private ArrayList<GlobalStatistic> getBetweenBounds(Date firstBound, Date secondBound) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.ITALY);
        ArrayList<GlobalStatistic> copy = this.getStatistics();

        for (GlobalStatistic current: statistics){
            if (formatter.parse(current.getTimeStamp()).before(firstBound) || formatter.parse(current.getTimeStamp()).after(secondBound)){
                copy.remove(current);
            }
        }
        return copy;
    }
}
