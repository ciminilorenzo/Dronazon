package administration.services;

import drone.Drone;
import administration.resources.SmartCity;
import tools.ServerResponse;

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
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response insertDroneIntoTheSmartCity(Drone drone) {
        ServerResponse response = SmartCity.getInstance().insertDrone(drone);

        if(response.isErrorFlag())  {
            System.out.println("[SMARTCITY SERVICE INFO] Drone with ID: " + drone.getID() + " cannot enter into the city!");
            return Response.ok(response).build();
        }
        else {
            System.out.println("[SMARTCITY SERVICE INFO] Drone with ID: " + drone.getID() + " has just entered into the city");
            return Response.ok(response).build();
        }
    }


    @Path("delete")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.TEXT_PLAIN)
    public Response takeOutADroneFromTheSmartCity(Drone drone){
        SmartCity.getInstance().takeOutDrone(drone);
        System.out.println("[SMARTCITY SERVICE INFO] Drone " + drone.getID() + "has just left the smartcity");
        return Response.ok().build();
    }

}
