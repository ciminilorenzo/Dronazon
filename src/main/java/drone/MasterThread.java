package drone;

import com.google.gson.Gson;
import drone.modules.CommunicationModule;
import drone.modules.DeliveryModule;
import org.eclipse.paho.client.mqttv3.*;
import tools.Delivery;
import tools.Position;

import java.util.*;


public class MasterThread extends Thread
{
    private final    Drone   drone;
    private final    String  clientId    = MqttClient.generateClientId();
    private final    MqttConnectOptions mqttConnectOptions  = new MqttConnectOptions();
    private final    ArrayList<Delivery> deliveryNotAssigned = new ArrayList<>();


    public MasterThread(Drone drone){
        this.drone = drone;
    }

    public void run()
    {
        try
        {
            String      broker = "tcp://localhost:1883";
            MqttClient  client = new MqttClient(broker, clientId);
            String      topic = "dronazon/smartcity/orders/";
            connect(client, mqttConnectOptions);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("\n\n[MASTER DRONE HAS JUST LOST HIS CONNECTION]");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // This is the new order arriving from Dronazon.
                    Delivery    arrivedDelivery = new Gson().fromJson(new String(message.getPayload()), Delivery.class);
                    assignDelivery(arrivedDelivery);

                    System.out.println("\n\n[MQTT MODULE] NEW ORDER HAS JUST PUBLISHED " +
                            "\n\t CONTENT: " + arrivedDelivery +
                            "\n\t QOS: " + message.getQos() +
                            "\n\t TOPIC: " + topic);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}

            });

            client.subscribe(topic);
            System.out.println("[MASTER DRONE HAS JUST SUCCESSFULLY SUBSCRIBED TO THE TOPIC " + topic + "]\n\n");

        }
        catch (MqttException e)
        {
            e.printStackTrace();
        }
    }

    private static void connect(MqttClient client, MqttConnectOptions connectionOptions) throws MqttException {
        client.connect(connectionOptions);
        System.out.println("\n\n[MASTER] HAS JUST CONNECTED TO THE BROKER");
    }

    private static void disconnect(MqttClient client) throws MqttException {
        if (client.isConnected()){
            client.disconnect();
            System.out.println("[MASTER] HAS JUST DISCONNECTED\n\n");
        }
    }

    private boolean assignDelivery(Delivery delivery){
        System.out.println("\n\n[MASTER] Trying to find a drone for a delivery");
        // Deep copy
        ArrayList<Drone> copyList = new ArrayList<>(drone.getSmartcity().getDroneArrayList());
        ArrayList<Drone> finalList = new ArrayList<>();

        // If the master is the only one into the smartcity and he is busy then we have to add it to the specified queue.
        if(copyList.size() == 1 && drone.isBusy()) {
            deliveryNotAssigned.add(delivery);
            return false;
        }

        // If the master is the only one into the smartcity and he's free then we are gonna assign the delivery to him.
        else if(copyList.size() == 1){
            assignation(delivery, drone);
            return true;
        }

        double shortestDistance = 1000;
        int maximumBatteryLevel = 0;

        // If are present more than 1 drone into the smartcity we are gonna select the 'winner' using project's parameters.
        for (Drone current: copyList) {
            double distance = Position.getDistanceBetweenTwoPoints(delivery.getPickupPoint(), current.getPosition());
            int batteryLevel = current.getBattery();
            if(!current.isBusy() && distance <= shortestDistance){
                if(batteryLevel >= maximumBatteryLevel){
                    finalList.add(current);
                    shortestDistance = distance;
                    maximumBatteryLevel = batteryLevel;
                }
            }
        }
        // If the list contains more than one drone which has the shortest (it's equal) distance and same level battery
        // then we are gonna sort the array using IDs. We are gonna return the maximum one.
        if(finalList.size() != 1) finalList.sort(Comparator.comparing(Drone::getID));
        assignation(delivery, finalList.get(0));
        return true;
    }

    private void assignation (Delivery delivery, Drone drone){
        System.out.println("[MASTER] DELIVERY ASSIGNATION . . .");
        //TODO QUI HO UN BOOL CHE MI RITORNA. SE E' TRUE FINISCO ALTRIMENTI RIFACCIO PROCESSO DI ELEZIONE (MODIFICO VISTA DRONE MASTER?)
        CommunicationModule.askToMakeADelivery(this.drone, drone, delivery);
        System.out.println("[MASTER] DELIVERY ASSIGNATION DONE");
    }

    /**
     * Each time one drone enters into the smartcity has to check if there is any delivery that is waiting to be assigned.
     * If the assignation process returns 'true' then the delivery is successfully assigned and we can delete it from the undelivered ones.
     * TODO: INVECE IMPLEMENTARE UNA CODA CON WAIT E NOTIFY() però comunque notify dovrebbe essere fatto ogni qualvolta entra un drone
     * TODO: VOLATILE???
     */
    public void checkIfDelivery(Drone drone){
        if(!deliveryNotAssigned.isEmpty()){
            if(assignDelivery(deliveryNotAssigned.get(0))){
                deliveryNotAssigned.remove(0);
            }
        }
    }
}
