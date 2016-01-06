#include"Simulation.h"
#include "Helper.h"
#include <iostream>
#include <limits>

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
// TODO parallel
double Simulation::getAvgMinDistance(void) {
    this->avgMinDist;
}
// TODO parallel
void Simulation::remove(int numberOfPairsToRemove) {
    while (numberOfPairsToRemove > 0) {
        
    }
}
void Simulation::calcAvgMinDistance(void) {
    sumOfAvg = 0;
    for (int i = 0; i < numberOfParticles; i++) {
       sumOfAvg += getAvgMinDistance(i);
    }
    this->avgMinDist = sumOfAvg / numberOfParticles;
}

// TODO parallel
double Simulation::getMinDistance(int index) {
    double minDistance = numeric_limits<double>::max();
    int indexSoFar = -1;
    for (int i = 0; i < numberOfParticles; i++) {
        if (index == i) continue;

        double dist = Helper::getDistance(x, y, z, i, j);
        if (dist < minDistance) {
            minDistance = dist;
            indexSoFar = i;
        }
    }

    return minDistance;
}

// TODO make it parallel (divide the work)
// TODO refactor using getIndexOfClosest
int* Simulation::getTwoClosestsParticles() {
    int* results = new int[2];
    double closestDistanceSoFar = numeric_limits<double>::max();
    cout << numberOfParticles << endl;
    for (int i = 0; i < numberOfParticles; i++) {
        for (int j = 0; j < numberOfParticles; j++) {
            if (i == j) continue;

            // TODO use getDistanceSQ?
            double dist = Helper::getDistance(x, y, z, i, j);
            if (dist < closestDistanceSoFar) {
                cout << "new closes distance = " << dist << endl;
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
    // TODO parallel
    numberOfParticles--;
}
