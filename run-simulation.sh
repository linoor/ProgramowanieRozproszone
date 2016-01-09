#!/bin/bash

# run the simulation
mpicxx -O3 Helper.cpp Simulation.cpp main_01.cpp -o simulation || exit 1; # exit on failure
mpirun -np $1 ./simulation

source ~/.bashrc
jobsdone
