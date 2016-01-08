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
            cout << "getting two closest particles" << endl;
            int* closestParticles = Simulation::getTwoClosestsParticles();
            fuseTwoParticles(closestParticles[0], closestParticles[1]);

            numberOfPairsToRemove--;
        }
    }
}
void Simulation::calcAvgMinDistance(void) {
    // Get the rank of the process
    int rank;
    int master = 0;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    double sumOfAvg = 0.0;
    for (int i = 0; i < numberOfParticles; i++) {
        double minDistance = numeric_limits<double>::max();
        int indexSoFar = -1;
        for (int j = 0; j < numberOfParticles; j++) {
            if (i == j) continue;

            double dist = Helper::getDistance(x, y, z, i, j);
            if (dist < minDistance) {
                minDistance = dist;
                indexSoFar = j;
            }
        }
        sumOfAvg += minDistance;
    }
    this->avgMinDist = sumOfAvg / numberOfParticles;
}

// TODO make it parallel (divide the work)
// TODO refactor using getIndexOfClosest
int* Simulation::getTwoClosestsParticles() {
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
    cout << "fusing particles" << endl;
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
    cout << "fused particles" << endl;
}
