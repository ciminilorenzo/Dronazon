package tools;


import administration.resources.statistics.GlobalStatistic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="StatisticsServerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatisticsServerResponse {

    @XmlElement
    private List<GlobalStatistic> globalStatistics;

    public StatisticsServerResponse(List<GlobalStatistic> globalStatistics){
        this.globalStatistics = globalStatistics;
    }

    private StatisticsServerResponse(){}

    public List<GlobalStatistic> getGlobalStatistics() {
        return globalStatistics;
    }

    public void setGlobalStatistics(List<GlobalStatistic> globalStatistics) {
        this.globalStatistics = globalStatistics;
    }

    public String toString(){
        return "[RESPONSE FROM SERVER]\n" +
                "\t" + this.globalStatistics;
    }
}
