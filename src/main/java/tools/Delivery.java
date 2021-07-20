package tools;

import com.google.gson.Gson;

import java.util.UUID;

public class Delivery
{
    private final UUID ID = UUID.randomUUID();
    private final Position pickupPoint;
    private final Position deliveryPoint;

    public Delivery(Position pickupPoint, Position deliveryPoint){
        this.pickupPoint = pickupPoint;
        this.deliveryPoint = deliveryPoint;
    }

    public UUID getID() {
        return ID;
    }

    public Position getPickupPoint() {
        return pickupPoint;
    }

    public Position getDeliveryPoint() {
        return deliveryPoint;
    }

    @Override
    public String toString() {
        return "ID: " + this.getID()
                + "\tPICKUP POINT: " + this.getPickupPoint()
                + "\tDELIVERY POINT: " + this.getDeliveryPoint();
    }

    public static String createRandomDelivery(){
        return new Gson().toJson(new Delivery(Position.getRandomPosition(), Position.getRandomPosition()));
    }
}
