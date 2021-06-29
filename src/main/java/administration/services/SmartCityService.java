package administration.services;

import administration.resources.Drone;
import administration.resources.SmartCity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class provides methods for handling the smart city.
 */

@Path("smartcity")
public class SmartCityService
{

    @Path("insertion")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertDroneIntoTheSmartCity(Drone drone){
        String response = SmartCity.getInstance().insertDrone(drone);
        if(response.equals("ok"))
            return Response.ok().build();
        else if(response.equals("error"))
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     *
     * @return the whole smartcity's population
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPopulation(){
        return Response.ok(SmartCity.getInstance().getDrones()).build();
    }



}
