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

## Exercise Solutions - Implementation Details

This section documents where each exercise solution has been implemented in the codebase:

### Exercise 5.1: Third Function (f3)
**Location:** `dispositivo-core/src/main/java/dispositivo/iniciador/DispositivoIniciador.java`
- Added `f3` function initialization in constructor
- Default state set to BLINK mode

### Exercise 5.2: Device Status JSON
**Location:** `dispositivo-core/src/main/java/dispositivo/api/rest/Dispositivo_Recurso.java`
- Expanded `serialize()` method to return device status with all functions
- JSON format includes device ID, enabled status, and function states

### Exercise 5.3: Function Status JSON  
**Location:** `dispositivo-core/src/main/java/dispositivo/api/rest/Funcion_Recurso.java`
- Added `jsonResult.put("estado", f.getStatus());` into `serialize()` method for individual function status
- Returns JSON with function ID and current state

### Exercise 5.4: Device Enable/Disable
**Location:** `dispositivo-core/src/main/java/dispositivo/componentes/Dispositivo.java`
- Added `habilitado` boolean field and getter/setter methods
- Function modifications blocked when device is disabled

### Exercise 5.5: REST Device Control
**Location:** `dispositivo-core/src/main/java/dispositivo/api/rest/Dispositivo_Recurso.java`
- Added status return for `serialize` method: `jsonResult.put("habilitado", dispositivo.isHabilitado());`.
- Accepts JSON payload with "activar"/"desactivar" actions

### Exercise 5.6: MQTT Topic Configuration
**Location:** `dispositivo-core/src/main/java/dispositivo/api/mqtt/Dispositivo_APIMQTT.java`
- `TOPIC_BASE` constant defines common topic prefix
- Ensures consistent topic structure across all MQTT operations

### Exercise 5.7: MQTT JSON Commands
**Location:** `dispositivo-core/src/main/java/dispositivo/api/mqtt/Dispositivo_APIMQTT.java`
- Enhanced `messageArrived()` method to parse JSON commands
- Supports "encender", "apagar", "parpadear" actions

### Exercise 5.8: MQTT Device Commands
**Location:** `dispositivo-core/src/main/java/dispositivo/api/mqtt/Dispositivo_APIMQTT.java`
- Added device-level MQTT command handling
- Topic: `dispositivo/{deviceId}/comandos` for enable/disable

### Exercise 5.9: Function Status Publishing
**Location:** `dispositivo-core/src/main/java/dispositivo/componentes/Funcion.java`
- Automatic MQTT publishing when function state changes
- Publishes to: `dispositivo/{deviceId}/funcion/{funcionId}/info`

### Exercise 5.10: Traffic Light Controller
**Location:** `task10-run.sh` and `semaforo-controller.sh`
- Bash scripts coordinate two traffic light devices
- Implements intersection control logic with timing sequences
- Shell script instead of semaforo controller which most people would make, see how to run it furhter down

### Exercise 5.11: Master-Slave Controller
**Location:** `ejercicio_11/src/main/java/ejercicio11/MaestroEsclavoController.java`
- Standalone application implementing master-slave replication
- Monitors master device and replicates state changes to slaves
- Separate Maven module with its own build configuration

## Exercises Extra Details

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

## Building the Project

### Quick Build (All Platforms)

**Windows:**
```cmd
build.bat
```

**macOS/Linux:**
```bash
./build.sh
```

### Manual Build with Maven
```bash
# Clean and compile all modules
mvn clean compile

# Run tests
mvn test

# Create executable JARs
mvn package
```

## Running the Applications

### Core Device Application (Virtual Device)
The core device requires 4 arguments: deviceId, deviceIP, REST port, and MQTT broker URL.

```bash
# Build first
mvn clean package

# Run with required arguments
java -jar dispositivo-core/target/dispositivo-core-1.0.0.jar <deviceId> <deviceIP> <rest-port> <mqttBroker>

# Example:
java -jar dispositivo-core/target/dispositivo-core-1.0.0.jar ttmi051 localhost 8182 tcp://localhost:1883
```

### Pi4J Device Application (Raspberry Pi)
Same arguments as core device, but with Pi4J GPIO support for physical LEDs:

```bash
# On Raspberry Pi (requires sudo for GPIO access)
sudo java -jar dispositivo-pi4j/target/dispositivo-pi4j-1.0.0.jar <deviceId> <deviceIP> <rest-port> <mqttBroker>

# Example:
sudo java -jar dispositivo-pi4j/target/dispositivo-pi4j-1.0.0.jar ttmi051 192.168.1.100 8182 tcp://localhost:1883
```

### Ejercicio 11 - Master-Slave MQTT Controller
This exercise implements a master-slave pattern using MQTT communication:

```bash
# Build the exercise
cd ejercicio_11
mvn clean package

# Run with required arguments
java -jar target/maestro-esclavo-1.0.0.jar <broker> <maestroId> <esclavo1,esclavo2,...> [funcion]

# Example:
java -jar target/maestro-esclavo-1.0.0.jar tcp://localhost:1883 ttmi050 ttmi051,ttmi052 f1
```

**Arguments:**
- `broker`: MQTT broker URL (e.g., tcp://localhost:1883)
- `maestroId`: ID of the master device
- `esclavos`: Comma-separated list of slave device IDs
- `funcion`: Function ID to control (optional, defaults to "f1")

## API Reference

### REST API

**Device Status** - `GET /dispositivo`
Returns device and all function states.

**Enable/Disable Device** - `PUT /dispositivo`
Payload: `{"accion": "habilitar"}` or `{"accion": "deshabilitar"}`

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
- **MQTTX** - Modern cross-platform client (We used)
- **MQTT Explorer** - Visual topic browser
- **mosquitto_pub/sub** - Command-line tools

### REST Clients
- **Postman/Insomnia** - Full-featured API testing
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
