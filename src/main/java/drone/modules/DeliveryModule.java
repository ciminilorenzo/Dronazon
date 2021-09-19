package drone.modules;

import administration.resources.statistics.Statistic;
import drone.Drone;
import grpc.Services;
import tools.Delivery;
import tools.Position;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DeliveryModule extends Thread
{
    Drone               drone;
    Delivery            delivery;
    SimpleDateFormat    formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.ITALY);

    public DeliveryModule(Drone drone, Delivery delivery) {
        this.drone = drone;
        this.delivery = delivery;
    }

    public void run()
    {
        try
        {
            System.out.println("[DELIVERY MODULE] A DELIVERY WITH ID: "+ delivery.getID() + " HAS JUST BEEN ASSIGNED TO THIS DRONE");
            System.out.println("[DELIVERY MODULE] Delivering . . .");
            Thread.sleep(5000);
            System.out.println("[DELIVERY MODULE] . . . delivery has just finished!!!");

            String id         = this.drone.getID().toString();
            String timestamp  = formatter.format(new Date());
            Position position = delivery.getDeliveryPoint();

            double distance   = Position.getDistanceBetweenTwoPoints(
                    this.drone.getPosition(),
                    delivery.getPickupPoint()) + Position.getDistanceBetweenTwoPoints(delivery.getPickupPoint(), delivery.getDeliveryPoint());

            // Calculating pollution average of averages
            ArrayList<Double> measurementsCopy = drone.getMeasurementsDataStructure();
            double pollution = measurementsCopy
                    .stream()
                    .mapToDouble(i -> i).sum() / measurementsCopy.size();

            int battery = drone.getBattery() - 10;


            // New drone's position is equal to the delivery's pickup point one
            drone.setPosition(delivery.getDeliveryPoint());
            // Decreasing level battery of 10%
            drone.setBattery(drone.getBattery() - 10);
            // Updating distance made by the drone
            drone.setDistanceMade(this.drone.getDistanceMade() + distance);
            // Updating number of delivery successfully completed by the drone
            drone.setNumberOfDeliveryDone(this.drone.getNumberOfDeliveryDone() + 1);

            // If this is master drone we don't have to perform a grpc.
            if(this.drone.isMasterFlag()){
                Statistic statistic = new Statistic(timestamp, position, distance, pollution, battery, this.drone.getID().toString());
                this.drone.addStatisticToMasterDroneDataStructure(statistic);
                System.out.println("[DELIVERY MODULE] Delivery's data completely inserted.");
                this.drone.setBusy(false);
                this.drone.getSmartcity().modifyDroneAfterDelivery(drone.getID(), drone.getPosition(), drone.getBattery(), false);
            }
            else
            {
                Services.DeliveryComplete deliveryComplete = Services.DeliveryComplete.newBuilder()
                        .setDroneId(id)
                        .setTimestamp(timestamp)
                        .setNewPosition(Services.Position.newBuilder().setX(position.getX()).setY(position.getY()))
                        .setDistance(distance)
                        .setPollution(pollution)
                        .setBatteryLeft(battery)
                        .build();
                this.drone.setBusy(false);
                boolean response = CommunicationModule.sendCompletedDeliveryData(this.drone.getMasterDrone(), deliveryComplete);
            }
            System.out.println("[DELIVERY MODULE] Drone's information updated after delivery");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
