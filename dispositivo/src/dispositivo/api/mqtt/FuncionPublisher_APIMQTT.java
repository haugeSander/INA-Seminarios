package dispositivo.api.mqtt;

import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import dispositivo.utils.MySimpleLogger;

public class FuncionPublisher_APIMQTT {
	protected MqttClient myClient;
	protected MqttConnectOptions connOpt;
	protected String clientId = null;
    protected String mqttBroker = null;

    public FuncionPublisher_APIMQTT(String brokerUrl, String clientId) throws MqttException {
        this.clientId = clientId;
        myClient = new MqttClient(brokerUrl, clientId);
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);
        myClient.connect(connOpt);
    }

    public void publish_status(String topic, String funcionId, String estado) {
        String json = String.format("{\"id\":\"%s\", \"estado\":\"%s\"}", funcionId, estado);
        byte[] payload = json.getBytes(StandardCharsets.UTF_8);


        try {
            myClient.publish(
                topic, // topic
                payload, // payload
                1, // QoS level
                false // retained?
            );
        } catch (MqttException e) {
            MySimpleLogger.warn("FuncionPublisher", "Error publicando estado en topic " + topic + ": " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (myClient.isConnected()) {
                myClient.disconnect();
            }
        } catch (MqttException e) {
            MySimpleLogger.warn("FuncionPublisher", "Error al desconectar: " + e.getMessage());
        }
    }

}
