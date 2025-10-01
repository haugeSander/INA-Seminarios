#!/bin/bash
set -e

read -p "Use other than localhost as broker and IP? (y/N): " -n 1 -r

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
  BROKER="tcp://localhost:1883"
  IP="localhost"
else
  echo "\nInput broker URL: "
  read BROKER
  
  echo "Input preferred IP: "
  read IP
fi

echo "Running with IP: $IP, and broker: $BROKER"

java -jar dispositivo-core/target/dispositivo-core-1.0.0.jar sem1 $IP 8182 $BROKER &
PID1=$!
java -jar dispositivo-core/target/dispositivo-core-1.0.0.jar sem2 $IP 8183 $BROKER &
PID2=$!

trap 'kill $PID1 $PID2' EXIT

sleep 5
./semaforo-controller.sh "$IP"

