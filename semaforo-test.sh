#!/bin/bash
addr=$1

while true; do
  # Step 1: both red
  mosquitto_pub -h $addr -t "es/upv/inf/muiinf/ina/dispositivo/sem1/funcion/f1/comandos" -m '{"accion":"apagar"}'
  mosquitto_pub -h $addr -t "es/upv/inf/muiinf/ina/dispositivo/sem2/funcion/f1/comandos" -m '{"accion":"apagar"}'
  sleep 1

  # Step 2: green sem1
  mosquitto_pub -h $addr -t "es/upv/inf/muiinf/ina/dispositivo/sem1/funcion/f3/comandos" -m '{"accion":"encender"}'
  sleep 5

  # Step 3: yellow sem1
  mosquitto_pub -h $addr -t "es/upv/inf/muiinf/ina/dispositivo/sem1/funcion/f2/comandos" -m '{"accion":"parpadear"}'
  sleep 2

  # Step 4: red sem1
  mosquitto_pub -h $addr -t "es/upv/inf/muiinf/ina/dispositivo/sem1/funcion/f1/comandos" -m '{"accion":"apagar"}'
  sleep 1

  # Step 5: green sem2
  mosquitto_pub -h $addr -t "es/upv/inf/muiinf/ina/dispositivo/sem2/funcion/f3/comandos" -m '{"accion":"encender"}'
  sleep 5

  # Step 6: yellow sem2
  mosquitto_pub -h $addr -t "es/upv/inf/muiinf/ina/dispositivo/sem2/funcion/f2/comandos" -m '{"accion":"parpadear"}'
  sleep 2

  # Step 7: red sem2
  mosquitto_pub -h $addr -t "es/upv/inf/muiinf/ina/dispositivo/sem2/funcion/f1/comandos" -m '{"accion":"apagar"}'
  sleep 1
done
