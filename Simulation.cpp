#include"Simulation.h"
#include "Helper.h"
#include <iostream>
#include <limits>
#include <mpi.h>

using namespace std;

Simulation::Simulation() {}
Simulation::~Simulation() {}

void Simulation::setParticles(double *x, double *y, double *z, int numberOfParticles) {
    // initialize the vectors
    this->x = x;
    this->y = y;
    this->z = z;
    this->numberOfParticles = numberOfParticles;
}
double Simulation::getAvgMinDistance(void) {
    this->avgMinDist;
}

void Simulation::remove(int numberOfPairsToRemove) {
    int rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    if (rank == 0) {
        while (numberOfPairsToRemove > 0) {
            int *twoClosest = Simulation::getTwoClosestsParticles();
            fuseTwoParticles(twoClosest[0], twoClosest[1]);
            numberOfPairsToRemove--;
        }
    }
    Simulation::calcAvgMinDistance();
}
void Simulation::calcAvgMinDistance(void) {
    MPI_Status status;
    int numprocs;
    MPI_Comm_size(MPI_COMM_WORLD, &numprocs);
    int master = 0;
    // getting rank of this process
    int rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int chunksize = numberOfParticles / numprocs;
    int lower = rank * chunksize;
    int upper = lower + chunksize;

    // each process adds up its entries
    double sum = 0.0;
    for (int i = lower; i < upper; i++) {
       sum += getMinDistance(i);
    }

    // sending back the results to the main process
    cout << "the rank: " << rank << endl;
    if (rank != master) {
        cout << "test" << endl;
        MPI_Send(&sum, 1, MPI_DOUBLE, master, 1, MPI_COMM_WORLD);
    } else {
        // summing all of the received data
        cout << "test2" << endl;
        double sum_all = sum;
        for (int i = 1; i < numprocs; i++) {
            MPI_Recv(&sum, 1, MPI_DOUBLE, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &status);
            sum_all += sum;
        }
        this->avgMinDist = sum_all / numberOfParticles;
    }
}

// TODO parallel
double Simulation::getMinDistance(int i) {
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

    return minDistance;
}

// TODO make it parallel (divide the work)
// TODO refactor using getIndexOfClosest
int* Simulation::getTwoClosestsParticles() {
    int rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int* results = new int[2];

    double closestDistanceSoFar = numeric_limits<double>::max();
    for (int i = 0; i < numberOfParticles; i++) {
        for (int j = 0; j < numberOfParticles; j++) {
            if (i == j) continue;

            // TODO use getDistanceSQ?
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
    // TODO parallel
    numberOfParticles--;
    cout << "fused particles" << endl;
}
