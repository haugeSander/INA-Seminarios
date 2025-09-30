package ejercicio11;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import java.util.Arrays;

public class MaestroEsclavoController implements MqttCallback {

    private static final String TOPIC_BASE = "es/upv/inf/muiinf/ina/";

    private final String brokerUrl;
    private final String maestroId;
    private final String[] esclavos;
    private final String funcionId;
    private MqttClient client;

    public MaestroEsclavoController(String brokerUrl, String maestroId, String[] esclavos, String funcionId) {
        this.brokerUrl = brokerUrl;
        this.maestroId = maestroId;
        this.esclavos = esclavos;
        this.funcionId = (funcionId == null || funcionId.isBlank()) ? "f1" : funcionId;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Uso: java -jar maestro-esclavo.jar <broker> <maestroId> <esclavo1,esclavo2,...> [funcion]");
            System.out.println("Ejemplo: java -jar maestro-esclavo.jar tcp://localhost:1883 ttmi050 ttmi051,ttmi052 f1");
            return;
        }

        String broker = args[0];
        String maestro = args[1];
        String[] esclavos = Arrays.stream(args[2].split(",")).map(String::trim).toArray(String[]::new);
        String funcion = (args.length >= 4) ? args[3] : "f1";

        MaestroEsclavoController controller = new MaestroEsclavoController(broker, maestro, esclavos, funcion);
        controller.start();

        synchronized (MaestroEsclavoController.class) {
            MaestroEsclavoController.class.wait();
        }
    }

    public void start() throws MqttException {
        String clientId = "maestro-esclavo-" + System.currentTimeMillis();
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setCleanSession(true);

        client = new MqttClient(brokerUrl, clientId);
        client.setCallback(this);
        client.connect(opts);

        String topicMaestroWildcard = TOPIC_BASE + "dispositivo/" + maestroId + "/funcion/" + funcionId + "/#";
        client.subscribe(topicMaestroWildcard, 0);
        System.out.println("Suscrito a: " + topicMaestroWildcard);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("Conexión perdida con broker: " + cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("Mensaje recibido en " + topic + " -> " + payload);

        JSONObject json;
        try {
            json = new JSONObject(payload);
        } catch (Exception e) {
            System.out.println("Payload no JSON, ignorado.");
            return;
        }

        String accion = null;

        if (json.has("estado")) {
            accion = mapEstadoToAccion(json.optString("estado", ""));
        } else if (json.has("accion")) {
            String a = json.optString("accion", "").toLowerCase();
            if (isValidAccion(a)) accion = a;
        }

        if (accion != null) publishToEsclavos(accion);
        else System.out.println("Mensaje maestro sin 'estado' o 'accion' válido -> ignorado.");
    }

    private boolean isValidAccion(String accion) {
        return accion != null && (accion.equals("encender") || accion.equals("apagar") || accion.equals("parpadear"));
    }

    private String mapEstadoToAccion(String estado) {
        if (estado == null) return null;
        switch (estado.toUpperCase()) {
            case "ON": return "encender";
            case "OFF": return "apagar";
            case "BLINK": case "BLINKING": return "parpadear";
            default: return null;
        }
    }

    private void publishToEsclavos(String accion) {
        JSONObject comando = new JSONObject();
        comando.put("accion", accion);

        for (String esclavo : esclavos) {
            try {
                String topicComando = TOPIC_BASE + "dispositivo/" + esclavo + "/funcion/" + funcionId + "/comandos";
                MqttMessage msg = new MqttMessage(comando.toString().getBytes());
                msg.setQos(0);
                client.publish(topicComando, msg);
                System.out.println("Publicado a " + esclavo + " -> " + comando);
            } catch (Exception ex) {
                System.err.println("Error publicando a " + esclavo + ": " + ex.getMessage());
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}