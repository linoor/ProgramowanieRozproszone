#!/bin/bash

cp src/*.java src_compiled/*.java tests/*.java tmp/
cd tmp
javac Server.java
javac Client.java

# starting Server
java Server -ORBInitialPort 1050 -ORBInitialHost localhost&

sleep 1
# starting Client
java Client -ORBInitialPort 1050 -ORBInitialHost localhost
