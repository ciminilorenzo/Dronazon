package administration;

import drone.Drone;
import administration.services.StatisticsService;
import tools.DronesServerResponse;
import tools.StatisticsServerResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ClientAdministrator
{

    public static void main (String[] argv)
    {
        Scanner         scanner = new Scanner(System.in);
        ClientResponse  clientResponse;
        ClientConfig     clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,Boolean.TRUE);
        Client          client = Client.create(clientConfig);

        System.out.println("********** CLIENT ADMINISTRATOR INTERFACE **********");

        while(true)
        {
            System.out.println("\nPlease type the number associated to your request\n" +
                    "\t\t 1] List of drones which are in the smartcity;\n" +
                    "\t\t 2] Last 'n' global statistics of the smartcity;\n" +
                    "\t\t 3] Average of delivery made by drones between two bounds;\n" +
                    "\t\t 4] Average of distance made by drones between two bounds\n");

            String administratorChoice = scanner.nextLine();

            switch (administratorChoice)
            {
                case "1":
                {
                    System.out.println(". . . printing list of drones in the smartcity\n");
                    clientResponse = getRequest(client, StatisticsService.GET_POPULATION);
                    DronesServerResponse response =  clientResponse.getEntity(DronesServerResponse.class);

                    if(response.getDrones() != null){
                        for (Drone current: response.getDrones()) {
                            System.out.println(current.toString());
                        }
                    }
                    else System.out.println("Smartcity has no population yet");
                    break;
                }

                case "2":
                {
                    try {
                        System.out.println("How many statistics you want to retrieve? ");
                        int number = scanner.nextInt();

                        clientResponse = getRequest(client, StatisticsService.GET_LAST_STATISTICS + number);
                        StatisticsServerResponse result = clientResponse.getEntity(StatisticsServerResponse.class);
                        System.out.println(result);
                        break;
                    }
                    catch (Exception exception) {
                        System.out.println("Number isn't valid");
                        break;
                    }
                }

                case "3": case "4":
                {
                    String              firstBoundToSend;
                    String              secondBoundToSend;
                    Date                firstBoundToCheck;
                    Date                secondBoundToCheck;
                    SimpleDateFormat    formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

                    try
                    {
                        System.out.println("Please insert bounds according to this pattern: dd-M-yyyy hh:mm:ss");

                        System.out.print("Insert first bound: ");
                        firstBoundToSend  = scanner.nextLine();
                        firstBoundToCheck = formatter.parse(firstBoundToSend);

                        System.out.print("Insert second bound: ");
                        secondBoundToSend = scanner.nextLine();
                        secondBoundToCheck = formatter.parse(secondBoundToSend);

                        String result;

                        if(administratorChoice.equals("3")){
                            clientResponse = getRequest(client,
                                    StatisticsService.AVG_DELIVERY + "?first=" + firstBoundToSend.replace(" ", "__") + "&second=" + secondBoundToSend.replace(" ", "__"));
                        }
                        else {
                            clientResponse = getRequest(client,
                                    StatisticsService.AVG_DISTANCE + "?first=" +  firstBoundToSend.replace(" ", "__") + "&second=" + secondBoundToSend.replace(" ", "__"));
                        }
                        result = clientResponse.getEntity(String.class);
                        System.out.println("The average is: " + result);
                    }
                    catch (ParseException exception){
                        System.out.println("Invalid date inserted. It must be in the form dd-M-yyyy hh:mm:ss\n");
                    }
                    break;
                }

                default: System.out.println("Invalid input"); break;


            }
        }
    }

    private static ClientResponse getRequest(Client client, String url){
        WebResource webResource = client.resource(url);
        try
        {
            return webResource.type("application/json").get(ClientResponse.class);
        }
        catch (ClientHandlerException e){
            e.printStackTrace();
            return null;
        }
    }
}
