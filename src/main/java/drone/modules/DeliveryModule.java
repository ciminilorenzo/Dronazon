package drone.modules;

import drone.Drone;
import tools.Delivery;

public class DeliveryModule extends Thread
{
    Drone drone;
    Delivery delivery;

    public DeliveryModule(Drone drone, Delivery delivery) {
        this.drone = drone;
        this.delivery = delivery;
    }

    public void run()
    {
        try
        {
            System.out.println("\n\n[DELIVERY MODULE] A DELIVERY HAS JUST BEEN ASSIGNED TO THIS DRONE");
            System.out.println("[DELIVERY MODULE] Delivering . . .");
            Thread.sleep(5000);
            System.out.println("[DELIVERY MODULE] Delivery successfully done");
            drone.setBusy(false);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
