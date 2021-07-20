package drone;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import tools.Delivery;

import java.sql.Timestamp;

public class MasterThread extends Thread
{
    private final       Drone   drone;
    private final       String  topic       = "dronazon/smartcity/orders/";
    private final       String  broker      = "tcp://localhost:1883";
    private final       String  clientId    = MqttClient.generateClientId();
    private            MqttClient client;
    MqttConnectOptions mqttConnectOptions  = new MqttConnectOptions();

    public MasterThread(Drone drone){
        this.drone = drone;
    }

    public void run()
    {
        try
        {
            client = new MqttClient(broker, clientId);
            connect(client, mqttConnectOptions);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("\n\n[MASTER DRONE HAS JUST LOST HIS CONNECTION]");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String      arrivedMessage = new String(message.getPayload());
                    Delivery    arrivedDelivery = new Gson().fromJson(arrivedMessage, Delivery.class);
                    String      time = new Timestamp(System.currentTimeMillis()).toString();

                    System.out.println("\n\n[MQTT MODULE] NEW ORDER HAS JUST PUBLISHED " +
                            "\n\t CONTENT: " + arrivedDelivery +
                            "\n\t TIME: " + time +
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
}
