#!/bin/bash

cp src/*.java src_compiled/*.java tests/*.java tmp/
cd tmp
javac Start.java
javac Client.java

# starting Start
java -ea Start -ORBInitialPort 1050 -ORBInitialHost localhost&

sleep 1
# starting Client
java -ea Client -ORBInitialPort 1050 -ORBInitialHost localhost
