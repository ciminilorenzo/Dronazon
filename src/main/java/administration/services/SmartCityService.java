package administration.services;

import administration.resources.drone.Drone;
import administration.resources.SmartCity;
import administration.tools.ServerResponse;

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
        System.out.println("[SMARTCITY SERVICE INFO] Drone with ID: " + drone.getID() + " is trying to enter into the city!");
        ServerResponse response = SmartCity.getInstance().insertDrone(drone);

        if(response.isErrorFlag())  {
            System.out.println("[SMARTCITY SERVICE INFO] Drone with ID: " + drone.getID() + " cannot enter into the city!");
            return Response.status(Response.Status.CONFLICT).build();
        }
        else {
            System.out.println("[SMARTCITY SERVICE INFO] Drone with ID: " + drone.getID() + " has just entered into the city!");
            return Response.ok(response).build();
        }
    }

    /**
     * TODO: IMPLEMENTARE RIMOZIONE DRONE DA SMARTCITY ATTRAVERSO DELETE
     */

}
