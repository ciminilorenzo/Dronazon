package administration;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

public class ServerAdministrator
{
    public static void main (String[] argv)
    {
        final String    localhost = "localhost";
        final int       port = 1337;

        try
        {
            HttpServer server = HttpServerFactory.create("http://"+ localhost +":" +port+ "/");
            server.start();
        }
        catch (IOException exception)
        {
            System.out.println("[SERVER ADMINISTRATOR ERROR] \n" +
                    "\t" + exception.getMessage() +
                    "\t" + exception.getCause());
        }

    }
}
