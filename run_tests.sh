#!/bin/bash

cp src_compiled/* oramus_tests/
cd oramus_tests/
javac PMO_StartTest.java
time java PMO_StartTest
source ~/.bashrc
jobsdone;
