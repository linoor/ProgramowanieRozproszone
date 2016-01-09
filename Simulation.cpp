#include"Simulation.h"
#include "Helper.h"
#include <iostream>
#include <limits>
#include <mpi.h>

using namespace std;

Simulation::Simulation() {
    avgMinDist = numeric_limits<double>::max();
    variables_set = false;
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

    if (!variables_set) {
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
        variables_set = true;
    }

    while (numberOfPairsToRemove > 0) {
        Simulation::getTwoClosestsParticles();
        if (rank == master) {
            fuseTwoParticles(closestPair[0], closestPair[1]);
        }
        numberOfPairsToRemove--;

        // update the other vectors and number of particles
        MPI_Bcast(&numberOfParticles, 1, MPI_INT, master, MPI_COMM_WORLD);
        MPI_Bcast(x, numberOfParticles, MPI_DOUBLE, master, MPI_COMM_WORLD);
        MPI_Bcast(y, numberOfParticles, MPI_DOUBLE, master, MPI_COMM_WORLD);
        MPI_Bcast(z, numberOfParticles, MPI_DOUBLE, master, MPI_COMM_WORLD);
    }
}
void Simulation::calcAvgMinDistance(void) {
    double sum = 0.0;
    for (int i = 0; i < numberOfParticles; i++) {
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
    this->avgMinDist = sum / numberOfParticles;
}

void Simulation::getTwoClosestsParticles() {
    // Get the rank of the process
    int rank;
    const int master = 0;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    int num_of_processes;
    MPI_Comm_size(MPI_COMM_WORLD, &num_of_processes);

    int *i_indexes;
    // create a buffer with i indexes
    if (rank == master) {
        i_indexes = new int[numberOfParticles];
        for (int i = 0; i < numberOfParticles; i++) {
            i_indexes[i] = i;
        }
        // creating a buffer telling each processes how many data it gets
    }

    int elements_per_proc = numberOfParticles / num_of_processes;

    // create a buffer that will hold a subset of i indexes
    int *sub_i_indexes = new int[elements_per_proc];

    // send the indexes to each of the process
    MPI_Scatter(i_indexes, elements_per_proc, MPI_INT,
                sub_i_indexes, elements_per_proc, MPI_INT,
                0, MPI_COMM_WORLD);

    int results[2];
    double closestDistanceSoFar = numeric_limits<double>::max();
    for (int k = 0; k < elements_per_proc; k++) {
        int i = sub_i_indexes[k];
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

    // create a buffer for the results
    int *pairs;
    if (rank == master) {
        pairs = new int[num_of_processes*2];
    }
    // receiving data from other processes into the sums buffer
    MPI_Gather(results, 2, MPI_INT,
               pairs, 2, MPI_INT,
               0, MPI_COMM_WORLD);

    // process all of the pairs that you got
    if (rank == master) {
        int closest[2];
        double closestDist = numeric_limits<double>::max();
        for (int k = 0; k < num_of_processes*2; k += 2) {
            int i = pairs[k];
            int j = pairs[k+1];
            double dist = Helper::getDistance(x, y, z, i, j);
            if (dist < closestDist) {
                closestDist = dist;
                closest[0] = i;
                closest[1] = j;
            }
        }
        this->closestPair[0] = closest[0];
        this->closestPair[1] = closest[1];
    }
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
