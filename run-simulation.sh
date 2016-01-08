#!/bin/bash

# run the simulation
mpicxx -O0 Helper.cpp Simulation.cpp main_01.cpp -o simulation || exit 1; # exit on failure
mpirun -n $1 ./simulation

source ~/.bashrc
jobsdone
