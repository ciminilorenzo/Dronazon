package tools;

import java.util.ArrayList;

public class MasterNotDeliveredDeliveryQueue {
    private static MasterNotDeliveredDeliveryQueue queue = null;
    private final ArrayList<Delivery> notDeliveredYet;

    private MasterNotDeliveredDeliveryQueue(){
        notDeliveredYet = new ArrayList<>();
    }

    public synchronized static MasterNotDeliveredDeliveryQueue getInstance(){
        if(queue == null){
            queue = new MasterNotDeliveredDeliveryQueue();
        }
        return queue;
    }

    public synchronized ArrayList<Delivery> getDeliveryNotAlreadyDelivered(){
        return notDeliveredYet;
    }

    public synchronized void addDelivery(Delivery delivery){
        notDeliveredYet.add(delivery);
    }

    public synchronized void removeDelivery(Delivery delivery){
        notDeliveredYet.remove(delivery);
    }
}
