package administration.resources.drone;

/**
 * EACH 10 SECONDS THIS THREAD HAVE TO PRINT:
 *      - NUMBER OF DELIVERY MADE BY THE DRONE
 *      - NUMBER OF KILOMETERS MADE BY THE DRONE
 *      - BATTERY LEFT
 *
 * Is it right to don't synchronize the whole set of codes?
 */

public class DataPrinter extends Thread
{
    Drone drone;

    public DataPrinter(Drone drone)
    {
        this.drone = drone;
    }

    public void run()
    {
        System.out.println("\n\n[DRONE'S AUTOMATED ROUTINE] " +
                "\n\t NUMBER OF DELIVERY THAT THIS DRONE HAS MADE: " + drone.getNumberOfDeliveryDone() +
                "\n\t NUMBER OF KILOMETERS THIS DRONE TRAVELLED: " + drone.getDistanceMade() +
                "\n\t BATTERY LEFT: "+ drone.getBattery() + "%" +
                "\n\n"
        );
    }
}
