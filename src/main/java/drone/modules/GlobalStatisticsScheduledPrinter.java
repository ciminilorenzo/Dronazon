package drone.modules;

import administration.resources.statistics.GlobalStatistic;
import administration.services.StatisticsService;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import drone.Drone;

public class GlobalStatisticsScheduledPrinter extends Thread
{
    private final Drone drone;

    public GlobalStatisticsScheduledPrinter(Drone drone){
        this.drone = drone;
    }

    /**
     * Each 10 seconds master drone has to calculate a new global statistic using data received from drones who have
     * delivered at least 1  delivery.
     * Once master drone has calculated the global statistic, it have to clear the whole data structure.
     *
     * Inside GlobalStatistic class, each global statistic is calculated using the number of drones which
     */
    public void run(){
        if(!this.drone.getMasterDroneStatistics().isEmpty()) {
            GlobalStatistic     globalStatistic = new GlobalStatistic(this.drone.getMasterDroneStatistics());

            // Each time master drone calculates global statistics he have to clear the data structure.
            this.drone.getMasterDroneStatistics().clear();

            System.out.println("[GLOBAL STATISTICS SCHEDULED MODULE] Global statistic has just been calculated");
            Client              client = new Client();
            ClientResponse      clientResponse = postRequest(client, StatisticsService.INSERT, globalStatistic);
            System.out.println("[GLOBAL STATISTICS SCHEDULED MODULE] Response from server about global statistic publication: " + clientResponse);
        }
    }





    private ClientResponse postRequest(Client client, String url, GlobalStatistic globalStatistic){
        try
        {
            WebResource webResource = client.resource(url);
            String input = new Gson().toJson(globalStatistic);
            return webResource.type("application/json").post(ClientResponse.class, input);
        }
        catch (ClientHandlerException exception){
            exception.printStackTrace();
            return null;
        }
    }
}
