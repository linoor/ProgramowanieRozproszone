#!/bin/bash

c++ -O -fopenmp Function.cpp Minimum.cpp main.cpp -o openmp_out || exit 1;
./openmp_out
