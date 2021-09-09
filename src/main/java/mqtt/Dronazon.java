package mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import tools.Delivery;


/**
 * This a process that simulates an e-commerce. In particular, Dronazon must generate randomly made orders every 5 seconds.
 */
public class Dronazon
{
    private static final String  topic = "dronazon/smartcity/orders/";
    private static final String  broker = "tcp://localhost:1883";
    private static final String  clientId = MqttClient.generateClientId();

    public static void main(String[] argv) throws MqttException, InterruptedException {
        MqttClient          client = new MqttClient(broker, clientId);
        MqttConnectOptions  connectOptions = new MqttConnectOptions();
        connect(client, connectOptions);
        publish(client);
    }


    private static void connect(MqttClient client, MqttConnectOptions connectionOptions){
        try
        {
            System.out.println("[ DRONAZON ] Trying to connect to the broker");
            client.connect(connectionOptions);
            System.out.println("[ DRONAZON ] Has just connected to the broker");
        }
        catch (MqttException e)
        {
            System.out.println("[ERROR]\n\n"
                    + "[REASON] " + e.getReasonCode()
                    + "\n[MESSAGE] " + e.getMessage()
                    + "\n[LOC] " + e.getLocalizedMessage()
                    + "\n[CAUSE] " + e.getCause() + "\n[EXE] " + e);
            e.printStackTrace();
        }
    }


    /**
     *
     * @param client that wants to send the message
     *  With this level of Quality-of-Service we want that our message will be send exactly one time.
     *  This is based on 4 messages: 1. Message's send 2. PUBREC 3. PUBREL 4. PUBCOMP
     */
    private static void publish(MqttClient client) throws MqttException, InterruptedException{
        final int        qos = 2;

        while (true) {
            System.out.println("[ DRONAZON ] preparing delivery");
            String payload = Delivery.createRandomDelivery();
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(qos);
            client.publish(topic, message);
            System.out.println("[ DRONAZON ] Has just published a new delivery:\n" +
                    message);
            Thread.sleep(3000);
        }
    }
}
