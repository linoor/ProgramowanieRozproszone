#include"Simulation.h"

#include <iostream>
#include <mpi.h>
#include <time.h>
#include <stdlib.h>
#include <sys/time.h>

using namespace std;

const int LEN = 20;
const int SIZE = LEN * LEN * LEN;
const int REPETITIONS = 5;
const int NUMBER_OF_PARTICLES_TO_REMOVE_ONCE = 2;
const int SHUFFLE_TIMES = 2*SIZE;
const double BOX_SIZE = 10.0;
const int ALL_PARTICLES = SIZE + REPETITIONS * NUMBER_OF_PARTICLES_TO_REMOVE_ONCE;

void generate( double *x, double *y, double *z ) {
	  int l = 0;
  	  for ( int k = 0; k < LEN; k++ )
  	   for ( int j = 0; j < LEN; j++ )
  	     for ( int i = 0; i < LEN; i++ ) {
//		cout << " l[ " << i << ", " << j << ", " << k << " ] = " << l << endl;
		x[l] = i * BOX_SIZE;
		y[l] = j * BOX_SIZE;
		z[l] = k * BOX_SIZE;
		l++;
  	     }
  	     
  	  // dodajemy troche czastek w poblizu [0,0,0]
  	  double size = BOX_SIZE * 0.9;
  	  double pos = size;
  	  for ( int i = SIZE; i < ALL_PARTICLES; i++ ) {
  	  	size *= 0.5;
  	  	pos -= size;
  	  	x[i] = 0.0;
  	  	y[i] = 0.0;
  	  	z[i] = pos;
  	  	cout << "z [ " << i << " ] = " << pos << endl;
  	  }  	  	     

          // losowo przestawiamy polozenia czastek
	  srandom( time( NULL ) );	  
	  int i, j, k;
	  double xtmp, ytmp, ztmp;
	  for ( int k = 0; k < SHUFFLE_TIMES; k++ ) {
	     i = random() % ALL_PARTICLES;
	     j = random() % ALL_PARTICLES;
	  
	     xtmp = x[ i ];
	     ytmp = y[ i ];
	     ztmp = z[ i ];
	     
	     x[ i ] = x[ j ];
	     y[ i ] = y[ j ];
	     z[ i ] = z[ j ];
	     
	     x[ j ] = xtmp;
	     y[ j ] = ytmp;
	     z[ j ] = ztmp;
	  }
}

int main(int ac, char **av) {

	MPI_Init(&ac, &av);

	int rank;

 	MPI_Comm_rank ( MPI_COMM_WORLD, &rank );

	Simulation *sim = new Simulation();

	if ( ! rank ) {
  	  double *x = new double[ ALL_PARTICLES ];
	  double *y = new double[ ALL_PARTICLES ];
	  double *z = new double[ ALL_PARTICLES ];
	
	  // dane generowane sa tylko w procesie z rank == 0
   	  generate( x, y, z );
   	  
	  // udostepniam czastki procesowi z rank==0
	  sim->setParticles( x, y, z, ALL_PARTICLES );


      // stuff i added
//      int* closestsTwo = sim->getTwoClosestsParticles();
//      cout << x[closestsTwo[0]] << " " << y[closestsTwo[0]] << " " << z[closestsTwo[0]] << endl;
//      cout << y[closestsTwo[1]] << " " << y[closestsTwo[1]] << " " << z[closestsTwo[1]] << endl;

//        int numberOfParticlesBefore = sim->numberOfParticles;
//        int numberOfParticles = sim->numberOfParticles;
//        cout << "particle 1: " << x[0] << " " << y[0] << " " << z[0] << endl;
//        cout << "particle 2: " << x[1] << " " << y[1] << " " << z[1] << endl;
//        cout << "last particle: " << x[numberOfParticles-1] << " " << y[numberOfParticles-1] << " " << z[numberOfParticles-1] << endl;
//        sim->fuseTwoParticles(0, 1);
//        numberOfParticles = sim->numberOfParticles;
//        cout << "number of particles before: " << numberOfParticlesBefore << endl;
//        cout << "number of particles now: " << numberOfParticles << endl;
//        cout << "particle 1: " << x[0] << " " << y[0] << " " << z[0] << endl;
//        cout << "last particle: " << x[numberOfParticles-1] << " " << y[numberOfParticles-1] << " " << z[numberOfParticles-1] << endl;
	}

	// tu sa obliczenia - start pomiaru czasu
	struct timeval tf;
	gettimeofday( &tf, NULL );
	double t0 = tf.tv_sec + tf.tv_usec * 0.000001;
	
	for (int i = 0; i < REPETITIONS; i++) {
		sim->remove( NUMBER_OF_PARTICLES_TO_REMOVE_ONCE );
		sim->calcAvgMinDistance();

		if (!rank) {
		   cout << "Krok " << ( i + 1 ) << " -> " <<
		        sim->getAvgMinDistance() << endl;
		}
	} // REPETITIONS

	gettimeofday( &tf, NULL );
	double tk = tf.tv_sec + tf.tv_usec * 0.000001;

        cout << "Pomiar czasu " << ( tk - t0 ) << " sekund" <<  endl;

	MPI_Finalize();
	return 0;
}
