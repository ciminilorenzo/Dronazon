package drone;

import java.util.Scanner;

/*
    TODO: WHEN A DRONE IS QUITTING MUST:
        - IF IT IS A NORMAL ONE:
            - TERMINATE THE ACTUAL DELIVER
            - FORCE QUIT COMMUNICATION WITH OTHERS DRONES WITHOUT COMMUNICATING HIS QUIT
            - ASK TO ADMINISTRATION SERVER TO QUIT
        - IF IT'S MASTER:
            - TERMINATE ACTUAL DELIVER
            - DISCONNECT TO THE MQTT BROKER
            - ASSIGN EACH DELIVER TO THE SMARTCITY'S DRONES
            - FORCE QUIT COMMUNICATION WITH OTHERS DRONES WITHOUT COMMUNICATING HIS QUIT
            - SEND TO THE SERVER ADMINISTRATOR SMARTCITY'S GLOBAL STATISTICS
            - ASK TO ADMINISTRATION SERVER TO QUIT
 */

public class QuitThread extends Thread
{
    Scanner         scanner = new Scanner(System.in);

    public QuitThread(){}

    public void run() {
        System.out.println("[SYSTEM INFO] Type 'quit' to shutdown the drone.");
        try
        {
            // scanner.nextLine() is blocking.
            while(true)
            {
                if(scanner.nextLine().equals("quit"))
                {
                    System.out.println("********** DRONE IS QUITTING *********");
                    System.exit(0);
                }
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
