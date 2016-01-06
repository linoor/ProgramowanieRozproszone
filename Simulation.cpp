#include"Simulation.h"
#include "Helper.h"
#include <iostream>
#include <limits>

using namespace std;

Simulation::Simulation() {}
Simulation::~Simulation() {}

void Simulation::setParticles( double *x, double *y, double *z, int numberOfParticles ) {
    // initialize the vectors
    this->x = x;
    this->y = y;
    this->z = z;
    this->numberOfParticles = numberOfParticles;
}
double Simulation::getAvgMinDistance( void ) { return 0.0; }
void Simulation::remove( int numberOfPairsToRemove ) {}
void Simulation::calcAvgMinDistance( void ) {}

int* Simulation::getTwoClosestsParticles() {
    int* results = new int[2];
    double closestDistanceSoFar = numeric_limits<double>::max();
    cout << numberOfParticles << endl;
    for (int i = 0; i < numberOfParticles; i++) {
        for (int j = 0; j < numberOfParticles; j++) {
            if (i == j) continue;

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
}
