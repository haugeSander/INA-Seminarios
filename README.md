# IoT Device Management System - Educational Project

A Java-based IoT device management system with REST and MQTT APIs, developed for the UPV Intelligent Environments (INA) course. Supports both virtual devices and physical Raspberry Pi deployment with GPIO control.

## Prerequisites

- **Java 11 or higher** (Required)
- **Maven 3.6+** (For building)
- **MQTT Broker** (e.g., Mosquitto for local testing)

### Platform-Specific Installation

**Windows:**
```cmd
# Install Java 11
choco install openjdk11
# Install Maven
choco install maven
```

**macOS:**
```bash
brew install openjdk@11 maven
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-11-jdk maven
```

## Project Structure

```
├── dispositivo-core/          # Core IoT device functionality
├── dispositivo-pi4j/          # Raspberry Pi specific implementation
├── ejercicio_11/              # Separate MQTT master-slave exercise
├── build.sh                   # Unix/Linux/macOS build script
├── build.bat                  # Windows build script
└── pom.xml                    # Parent Maven configuration
```


## Core Concepts

### Device Architecture
- **Device (Dispositivo)**: An IoT smart device with multiple functions
- **Functions (Funciones)**: Controllable features (f1, f2, f3) that can be ON, OFF, or BLINKING
- **APIs**: 
  - REST API for synchronous communication (port 8182+)
  - MQTT API for asynchronous pub/sub messaging

### GPIO Pin Mapping (Raspberry Pi)
- BCM 17 (Pin 11): Red LED (f1)
- BCM 27 (Pin 13): Yellow LED (f2)
- BCM 22 (Pin 15): Green LED (f3)
- BCM 9 (Pin 9): Ground

## Exercises Overview

### Exercise 5.1: Add Third Function
Add function `f3` to the device (starts in BLINK mode by default).

### Exercise 5.2: Device Status JSON
Implement REST GET response for `/dispositivo`:
```json
{
  "id": "ttmi051",
  "habilitado": true,
  "funciones": [
    {"id": "f1", "estado": "ON"},
    {"id": "f2", "estado": "OFF"},
    {"id": "f3", "estado": "BLINK"}
  ]
}
```

### Exercise 5.3: Function Status JSON
Implement REST GET response for `/dispositivo/funcion/{id}`:
```json
{"id": "f1", "estado": "ON"}
```

### Exercise 5.4: Enable/Disable Device
Add device enable/disable capability that blocks all function modifications when disabled.

### Exercise 5.5: Extend REST API
Add PUT support to `/dispositivo` for enabling/disabling:
```json
{"accion": "activar"}  // or "desactivar"
```

### Exercise 5.6: MQTT Topic Configuration
The `TOPIC_BASE` property defines the common prefix for all MQTT topics, ensuring consistent topic structure.

### Exercise 5.7: MQTT JSON Commands
Implement JSON format for MQTT function commands:
```json
{"accion": "encender"}  // "apagar" or "parpadear"
```
Topic: `dispositivo/{deviceId}/funcion/{funcionId}/comandos`

### Exercise 5.8: MQTT Device Enable/Disable
Extend MQTT API to enable/disable device via:
Topic: `dispositivo/{deviceId}/comandos`
```json
{"accion": "habilitar"}  // or "deshabilitar"
```

### Exercise 5.9: Function Status Publishing
Implement automatic status publishing to MQTT when function state changes:
Topic: `dispositivo/{deviceId}/funcion/{funcionId}/info`

### Exercise 5.10: Traffic Light Controller
Implement a traffic light intersection controller coordinating two devices.

**Running Exercise 10:**
```bash
# Make script executable
chmod +x task10-run.sh semaforo-controller.sh

# Run the traffic light controller
./task10-run.sh
```

The script will:
1. Prompt for broker URL and IP (defaults to localhost)
2. Start two semaphore devices (sem1, sem2)
3. Execute the traffic light sequence test

### Exercise 5.11: Master-Slave Controller
Implement a master-slave replication system where slave devices mirror the master's function states.

**Building and Running Exercise 11:**
```bash
cd ejercicio_11

# Build the project
mvn clean package

# Run the controller
java -jar target/maestro-esclavo-1.0.0.jar

# Alternative with classpath
mvn exec:java -Dexec.mainClass="ejercicio11.MaestroEsclavoController"
```

**Manual execution (if Maven build creates class files):**
```bash
# Compile (if needed)
javac -cp "lib/*" -d bin src/main/java/ejercicio11/MaestroEsclavoController.java

# Run
java -cp "bin;lib/*" ejercicio11.MaestroEsclavoController tcp://localhost:1883 ttmi050 ttmi051,ttmi052 f1
```

**Testing Exercise 11:**

Open 4 terminals:

Terminal A - Monitor master:
```bash
mosquitto_sub -h localhost -t "es/upv/inf/muiinf/ina/dispositivo/ttmi050/funcion/f1/#" -v
```

Terminal B - Monitor all slave commands:
```bash
mosquitto_sub -h localhost -t "es/upv/inf/muiinf/ina/dispositivo/+/funcion/f1/comandos" -v
```

Terminal C - Monitor slave 1:
```bash
mosquitto_sub -h localhost -t "es/upv/inf/muiinf/ina/dispositivo/ttmi051/funcion/f1/comandos" -v
```

Terminal D - Monitor slave 2:
```bash
mosquitto_sub -h localhost -t "es/upv/inf/muiinf/ina/dispositivo/ttmi052/funcion/f1/comandos" -v
```

Terminal E - Send commands to master:
```bash
mosquitto_pub -h localhost -t "es/upv/inf/muiinf/ina/dispositivo/ttmi050/funcion/f1/info" \
  -m '{"id":"f1","estado":"ON"}'
```

## Running Devices

### Virtual Device (Any Platform)
```bash
java -jar INA-Seminarios.jar <device-id> <ip> <rest-port> <mqtt-broker>

# Example
java -jar INA-Seminarios.jar ttmi051 localhost 8182 tcp://localhost:1883
```

### Physical Device (Raspberry Pi)
Requires Pi4J2 libraries and GPIO access:
```bash
# Must run with sudo for GPIO access
sudo java -jar dispositivo-pi4j2.jar ttmi051 ttmi051.iot.upv.es 8182 tcp://ttmi008.iot.upv.es:1883
```

## API Reference

### REST API

**Device Status** - `GET /dispositivo`
Returns device and all function states.

**Enable/Disable Device** - `PUT /dispositivo`
Payload: `{"accion": "activar"}` or `{"accion": "desactivar"}`

**Function Status** - `GET /dispositivo/funcion/{funcionId}`
Returns specific function state.

**Control Function** - `PUT /dispositivo/funcion/{funcionId}`
Payload: `{"accion": "encender"}`, `{"accion": "apagar"}`, or `{"accion": "parpadear"}`

### MQTT API

**Topic Structure:**
- Commands: `dispositivo/{deviceId}/funcion/{funcionId}/comandos`
- Status: `dispositivo/{deviceId}/funcion/{funcionId}/info`
- Device commands: `dispositivo/{deviceId}/comandos`

## Testing Tools

### MQTT Clients
- **MQTTX** - Modern cross-platform client
- **MQTT Explorer** - Visual topic browser
- **mosquitto_pub/sub** - Command-line tools

### REST Clients
- **Postman** - Full-featured API testing
- **curl** - Command-line testing
- **Advanced REST Client** - Browser-based

### Example curl Commands
```bash
# Get device status
curl http://localhost:8182/dispositivo

# Turn on function f2
curl -X PUT http://localhost:8182/dispositivo/funcion/f2 \
  -H "Content-Type: application/json" \
  -d '{"accion":"encender"}'
```

## Troubleshooting

### Java Version Issues
```bash
# Verify Java version
java -version  # Should show Java 11+

# Set JAVA_HOME (Linux/macOS)
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# Set JAVA_HOME (Windows)
set JAVA_HOME=C:\Program Files\Java\jdk-11
```

### MQTT Connection Issues
- Ensure Mosquitto broker is running: `mosquitto -v`
- Check firewall allows port 1883
- Verify broker URL format: `tcp://hostname:1883`

### Raspberry Pi GPIO Issues
- Run with sudo: `sudo java -jar ...`
- Verify Pi4J2 libraries are present
- Check GPIO pins are not in use by other processes

## Development Notes

- **Java Module System**: Project uses Java 9+ modules (requires Java 11+)
- **Pi4J Version**: Uses Pi4J v2 with PIGPIO drivers
- **MQTT Library**: Eclipse Paho MQTT v3
- **REST Framework**: Restlet framework

## License

Educational project for UPV Intelligent Environments (INA) course.