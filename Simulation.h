/*
 * Simulation.h
 *
 */

#ifndef SIMULATION_H_
#define SIMULATION_H_

class Simulation {
public:
	Simulation();
	virtual ~Simulation();

// te dwie metody wywolywane sa wylacznie dla procesu o rank==0
	void setParticles( double *x, double *y, double *z, int numberOfParticles );
	double getAvgMinDistance( void );

// te metody wywolywane sa dla _wszystkich_ procesow
	void remove( int numberOfPairsToRemove );
	void calcAvgMinDistance( void );
};

#endif /* SIMULATION_H_ */
