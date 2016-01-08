#include"Simulation.h"
#include "Helper.h"
#include <iostream>
#include <limits>
#include <mpi.h>

using namespace std;

Simulation::Simulation() {
    avgMinDist = numeric_limits<double>::max();
}
Simulation::~Simulation() {}

void Simulation::setParticles(double *x, double *y, double *z, int numberOfParticles) {
    // initialize the vectors
    this->x = x;
    this->y = y;
    this->z = z;
    this->numberOfParticles = numberOfParticles;
}

double Simulation::getAvgMinDistance(void) {
    return this->avgMinDist;
}

void Simulation::remove(int numberOfPairsToRemove) {
    // Get the rank of the process
    int rank;
    int master = 0;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    if (rank == master) {
        while (numberOfPairsToRemove > 0) {
            int* closestParticles = Simulation::getTwoClosestsParticles();
            fuseTwoParticles(closestParticles[0], closestParticles[1]);

            numberOfPairsToRemove--;
        }
    }
}
void Simulation::calcAvgMinDistance(void) {
    // Get the rank of the process
    int rank;
    const int master = 0;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    int num_of_processes;
    MPI_Comm_size(MPI_COMM_WORLD, &num_of_processes);

    // send the data about the number of particles
    MPI_Bcast(&numberOfParticles, 1, MPI_INT, master, MPI_COMM_WORLD);
    // initialize the arrays
    if (rank != 0) {
        x = new double[numberOfParticles];
        y = new double[numberOfParticles];
        z = new double[numberOfParticles];
    }
    // send the data about the three vectors of particles
    MPI_Bcast(x, numberOfParticles, MPI_DOUBLE, master, MPI_COMM_WORLD);
    MPI_Bcast(y, numberOfParticles, MPI_DOUBLE, master, MPI_COMM_WORLD);
    MPI_Bcast(z, numberOfParticles, MPI_DOUBLE, master, MPI_COMM_WORLD);

    int chunksize = numberOfParticles / num_of_processes;
    int avg = 0;

    int lower = rank * chunksize;
    int upper = lower + chunksize;

    double sum = 0.0;
    for (int i = lower; i < upper; i++) {
        double minDistance = numeric_limits<double>::max();
        for (int j = 0; j < numberOfParticles; j++) {
            if (i == j) continue;

            double dist = Helper::getDistance(x, y, z, i, j);
            if (dist < minDistance) {
                minDistance = dist;
            }
        }
        sum += minDistance;
    }

    // create a buffer for the results
    double *sums;
    if (rank == master) {
       sums = new double[num_of_processes];
    }
    // receiving data from other processes into the sums buffer
    MPI_Gather(&sum, 1, MPI_DOUBLE,
               sums, 1, MPI_DOUBLE,
               0, MPI_COMM_WORLD);

    // take the average from all of the results
    if (rank == master) {
        double allsums = 0;
        for (int i = 0; i < num_of_processes; i++) {
            allsums += sums[i];
        }
        this->avgMinDist = allsums / numberOfParticles;
    }
}

int* Simulation::getTwoClosestsParticles() {
    // Get the rank of the process
    int rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    int size ; // liczba procesow
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    int* results = new int[2];
    double closestDistanceSoFar = numeric_limits<double>::max();
    for (int i = 0; i < numberOfParticles; i++) {
        for (int j = 0; j < numberOfParticles; j++) {
            if (i == j) continue;

            double dist = Helper::getDistance(x, y, z, i, j);
            if (dist < closestDistanceSoFar) {
                closestDistanceSoFar = dist;
                results[0] = i;
                results[1] = j;
            }
        }
    }
    return results;
}

void Simulation::fuseTwoParticles(int i, int j) {
    // insert the new particle in the place of the first particle
    x[i] = Helper::middle(x, i, j);
    y[i] = Helper::middle(y, i, j);
    z[i] = Helper::middle(z, i, j);
    // get the last particle in the list and place it in the place of the second particle
    x[j] = x[numberOfParticles-1];
    y[j] = y[numberOfParticles-1];
    z[j] = z[numberOfParticles-1];
    // decrement the number of the particles
    numberOfParticles--;
}
