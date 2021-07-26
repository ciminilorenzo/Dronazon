package drone.modules;

import com.google.gson.Gson;
import drone.Drone;
import org.eclipse.paho.client.mqttv3.*;
import tools.Delivery;
import tools.Position;

import java.util.*;


public class MasterModule extends Thread {
    private final Drone drone;
    private final String clientId = MqttClient.generateClientId();
    private final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    private final ArrayList<Delivery> deliveryNotAssigned = new ArrayList<>();


    public MasterModule(Drone drone) {
        this.drone = drone;
    }

    public void run() {
        try {
            String broker = "tcp://localhost:1883";
            MqttClient client = new MqttClient(broker, clientId);
            String topic = "dronazon/smartcity/orders/";
            connect(client, mqttConnectOptions);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("\n\n[MQTT MODULE] MASTER HAS JUST LOST HIS CONNECTION]");
                    cause.printStackTrace();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // This is the new order arriving from Dronazon.

                    Delivery arrivedDelivery = new Gson().fromJson(new String(message.getPayload()), Delivery.class);
                    System.out.println("\n\n[MQTT MODULE] NEW ORDER HAS JUST PUBLISHED " +
                            "\n\t CONTENT: " + arrivedDelivery +
                            "\n\t QOS: " + message.getQos() +
                            "\n\t TOPIC: " + topic);
                    assignDelivery(arrivedDelivery);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }

            });

            client.subscribe(topic);
            System.out.println("[MASTER MODULE] MASTER HAS JUST SUCCESSFULLY SUBSCRIBED TO THE TOPIC " + topic + "]\n\n");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private static void connect(MqttClient client, MqttConnectOptions connectionOptions) throws MqttException {
        client.connect(connectionOptions);
        System.out.println("\n\n[MASTER MODULE] HAS JUST CONNECTED TO THE BROKER");
    }


    private static void disconnect(MqttClient client) throws MqttException {
        if (client.isConnected()) {
            client.disconnect();
            System.out.println("[MASTER MODULE] HAS JUST DISCONNECTED\n\n");
        }
    }


    private boolean assignDelivery(Delivery delivery) {
        System.out.println("\n\n[MASTER MODULE] Starting deliverer election process . . .");
        // Deep copy
        ArrayList<Drone> copyList = new ArrayList<>(drone.getSmartcity().getDroneArrayList());
        ArrayList<Drone> finalList = new ArrayList<>();

        // If the master is the only one into the smartcity and he is busy then we have to add it to the specified queue.
        if (copyList.size() == 1 && drone.isBusy()) {
            System.out.println("[MASTER MODULE] Only master drone into the smartcity and he's busy. Adding this delivery to queue");
            deliveryNotAssigned.add(delivery);
            return false;
        }

        // If the master is the only one into the smartcity and he's free then we are gonna assign the delivery to him.
        else if (copyList.size() == 1) {
            assignation(delivery, drone);
            return true;
        }

        double shortestDistance = 1000;
        int maximumBatteryLevel = 0;

        // If are present more than 1 drone into the smartcity we are gonna select the 'winner' using project's parameters.
        for (Drone current : copyList) {
            double distance = Position.getDistanceBetweenTwoPoints(delivery.getPickupPoint(), current.getPosition());
            int batteryLevel = current.getBattery();

            // Possible winner must be free.
            if (!current.isBusy()) {

                // We need to distinguish over due cases: if it's equal or strictly less


                if (distance == shortestDistance) {
                    // If the distance is the same we have to distinguish over two cases: if the battery level is smaller or equal to
                    if (batteryLevel > maximumBatteryLevel) {
                        maximumBatteryLevel = batteryLevel;
                        finalList.clear();
                        finalList.add(current);
                    } else if (batteryLevel == maximumBatteryLevel) {
                        finalList.add(current);
                    }
                } else if (distance < shortestDistance) {
                    shortestDistance = distance;
                    maximumBatteryLevel = batteryLevel;
                    finalList.clear();
                    finalList.add(current);
                }
            }
        }
        // If the final list is empty no one can deliver the delivery
        if (finalList.size() == 0){
            deliveryNotAssigned.add(delivery);
            System.out.println("[MASTER MODULE] No one is available to deliver. Adding this delivery to queue");
            return false;
        }

        // If the list contains more than one drone which has the shortest (it's equal) distance and same level battery
        // then we are gonna sort the array using IDs. We are gonna return the maximum one.
        if (finalList.size() > 1) {
            finalList.sort(Comparator.comparing(Drone::getID));
        }

        Drone winner = finalList.get(0);
        // If the winner of the election is the master drone then we don't need to make any gRPC.
        if (winner.isMasterFlag()) {
            System.out.println("[MASTER MODULE] Master drone won the election");
            this.drone.setBusy(true);
            DeliveryModule deliveryModule = new DeliveryModule(drone, delivery);
            deliveryModule.start();
            System.out.println("[MASTER MODULE] Delivery assignation successfully done");
            return true;
        }
        else return assignation(delivery, finalList.get(0));
    }







    /**
     * TODO: This might be changed cause only master drone is allowed to assign deliveries then only it has the capacity
     *          to know if a drone is busy or not. Remember that when one drone finishes his delivery must communicate to
     *          the master drone the related statistics (in this moment master drone will update his view).
     * After delivery's assignation process master thread is gonna ask to the elected one (through Grpc call) to make the
     * delivery (master drone have to ask cause maybe elected drone have unrecorded changes (is busy).
     *
     *
     * @param delivery that must be handled
     * @param drone that won the election
     */
    private boolean assignation (Delivery delivery, Drone drone){
        System.out.println("[MASTER MODULE] Delivery assignation . . .");

        // If winner drone is master then we are gonna set it as busy and make the delivery start.
        if(drone.isMasterFlag()){
            System.out.println("[MASTER MODULE] Master drone won the election");
            this.drone.setBusy(true);
            DeliveryModule deliveryModule = new DeliveryModule(drone, delivery);
            deliveryModule.start();
            return true;
        }

        else if(CommunicationModule.askToMakeADelivery(drone, delivery)){
            System.out.println("[MASTER MODULE] Delivery assignation successfully done");
            // Setting deliverer drone as busy in master's view
            this.drone.getSmartcity().setBusy(drone.getID());
            return true;
        }
        else {
            System.out.println("[MASTER MODULE] Same errors during assignation . . .");
            return false;
        }

    }




    /**
     * Each time one drone enters into the smartcity/one drone sets his busy flag as 'false': has to check if there is any delivery that is waiting to be assigned.
     * If the assignation process returns 'true' then the delivery is successfully assigned and we can delete it from the undelivered ones.
     * TODO: INVECE IMPLEMENTARE UNA CODA CON WAIT E NOTIFY() però comunque notify dovrebbe essere fatto ogni qualvolta entra un drone
     * TODO: VOLATILE???
     */
    public void checkIfDelivery(){
        if(!deliveryNotAssigned.isEmpty()){
            if(assignDelivery(deliveryNotAssigned.get(0))){
                System.out.println("[MASTER MODULE] One drone available has just been found. Removing this deliver from the queue");
                deliveryNotAssigned.remove(0);
            }
        }
    }
}
