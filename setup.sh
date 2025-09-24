#!/bin/bash
set -e

# Config
BROKER="tcp://localhost:1883"
IP="localhost"

# Start the two devices in the background
java -jar seminari-2-vscode-alumno.jar sem1 $IP 8182 $BROKER &
PID1=$!
java -jar seminari-2-vscode-alumno.jar sem2 $IP 8183 $BROKER &
PID2=$!

# Give them time to connect
sleep 5

# Launch the semaforo controller loop
./semaforo-test.sh localhost

# If you want to clean up when semaforo-test exits:
kill $PID1 $PID2
