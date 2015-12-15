#!/bin/bash

cp src_compiled/* oramus_tests/
cd oramus_tests/
javac PMO_StartTest.java
javac Start.java

# starting Start
echo "Starting server"
java -ea Start -ORBInitialPort 1050 -ORBInitialHost localhost&
sleep 1

# running the tests
time java PMO_StartTest -ORBInitialPort 1050 -ORBInitialHost localhost

source ~/.bashrc
jobsdone;
