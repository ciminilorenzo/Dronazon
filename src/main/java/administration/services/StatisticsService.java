package administration.services;

import administration.resources.SmartCity;
import administration.resources.statistics.GlobalStatistic;
import administration.resources.statistics.StatisticsContainer;
import tools.DronesServerResponse;
import tools.StatisticsServerResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


@Path("statistics")
public class StatisticsService
{
    public static final String GET_POPULATION      = "http://localhost:1337/statistics/population";
    public static final String GET_LAST_STATISTICS = "http://localhost:1337/statistics/global/";
    public static final String AVG_DELIVERY        = "http://localhost:1337/statistics/global/average_delivery";
    public static final String AVG_DISTANCE        = "http://localhost:1337/statistics/global/average_distance";

    /**
     * @return -> list of smartcity's drone population
     */
    @Path("population")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPopulation()
    {
        return Response
                .ok(new DronesServerResponse(SmartCity
                        .getInstance()
                        .getDrones()))
                .build();
    }


    /**
     *
     * @param number -> number of global statistics client wants to retrieve
     * @return -> last smartcity's n global statistics (with timestamps)
     *
     * * EXCEPTIONS:
     *      *  -   Empty result already handled in StatisticsContainer class;
     *      *  -   ParseExceptions handled.
     */
    @Path("global/{number}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getGlobalStatistics(
            @PathParam("number") int number){

        System.out.println("[SMARTCITY SERVICE INFO] ADMINISTRATOR HAS JUST ASKED TO RETRIEVE LAST " + number + "GLOBAL STATISTICS");
        StatisticsServerResponse response = new StatisticsServerResponse(StatisticsContainer
                .getInstance()
                .getLastGlobalStatistics(number));

        if(response.getGlobalStatistics() == null) return Response.status(Response.Status.BAD_REQUEST).build();
        return Response
                .ok(response)
                .build();
    }


    /**
     *
     * @param firstBound -> first time bound
     * @param secondBound -> second time bound
     * @return -> Average completed delivery made by drones in the smartcity between two timestamps
     *
     * EXCEPTIONS:
     *  -   Empty result already handled in StatisticsContainer class;
     *  -   ParseExceptions handled.
     */
    @Path("global/average_delivery")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAverageDeliveryCompleted(
            @QueryParam("first") String firstBound,
            @QueryParam("second") String secondBound) throws ParseException {

        SimpleDateFormat    formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.ITALY);
        System.out.println("[SMARTCITY SERVICE INFO] ADMINISTRATOR HAS JUST ASKED TO RETRIEVE DELIVERY AVERAGE");
        return Response
                .ok(StatisticsContainer
                        .getInstance()
                        .getDeliveryAverage(
                                formatter.parse(firstBound.replace("__", " ")),
                                formatter.parse(secondBound.replace("__", " "))))
                .build();
    }

    /**
     *
     * @param firstBound -> first time bound
     * @param secondBound -> second time bound
     * @return -> Average distance (km) made by drones in the smartcity between two timestamps
     *
     * EXCEPTIONS:
     *  -   Empty result already handled in StatisticsContainer class;
     *  -   ParseExceptions handled.
     */
    @Path("global/average_distance")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAverageDistanceMade(
            @QueryParam("first") String firstBound,
            @QueryParam("second") String secondBound) throws ParseException {

        SimpleDateFormat    formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.ITALY);
        System.out.println("[SMARTCITY SERVICE INFO] ADMINISTRATOR HAS JUST ASKED TO RETRIEVE DISTANCE AVERAGE");
        return Response.ok(StatisticsContainer
                .getInstance()
                .getDistanceAverage(
                        formatter.parse(firstBound.replace("__", " ")),
                        formatter.parse(secondBound.replace("__", " "))))
                .build();
    }


    @Path("global/insertion")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response insertGlobalStatistic(GlobalStatistic globalStatistic){
        StatisticsContainer.getInstance().insertGlobalStatistic(globalStatistic);
        return Response.ok().build();
    }

}
