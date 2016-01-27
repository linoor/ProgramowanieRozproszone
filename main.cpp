#include"Function.h"
#include"Minimum.h"
#include<sys/times.h>
#include<sys/time.h>
#include<omp.h>
#include<iostream>

const double CZAS_PRACY = 10000; // w msec
const int MAX_THREADS = 12;

long *Function::counter = new long[ MAX_THREADS ];
double Function::sleep = 100;

void cleanCounter() {
   for ( int i = 0; i < MAX_THREADS; i++ ) {
      Function::counter[ i ] = 0;
   }
}

long sumCounter() {
   long sum = 0;
   for ( int i = 0; i < MAX_THREADS; i++ ) {
      sum += Function::counter[ i ];
   } 
   
   return sum;
}

double getMsecTime() {
  struct timeval tf;
  gettimeofday( &tf, NULL );
  return tf.tv_sec * 1000 + tf.tv_usec * 0.001;
}

tms getTimes() {
  struct tms current;
  times( &current );
  return current;
}

int main( int ac, char **av ) {

   for ( int i = 1; i < MAX_THREADS; i++ ) {
     cleanCounter();
     std::cout << "PMO: Dla watkow = " << i << std::endl;
     omp_set_num_threads( i );

     Minimum *m = new Minimum( new Function(), -50.0, 50.0 );

     double start = getMsecTime();
     struct tms startTMS = getTimes();
     m->find( 0.1, 0.0001, 20, CZAS_PRACY );
     struct tms stopTMS = getTimes();
     double stop = getMsecTime();

     std::cout << "PMO: Dla watkow = " << i << std::endl;
     std::cout << "PMO: Best x     = " << m->getBestX() << std::endl;
     std::cout << "PMO: Best y     = " << m->getBestY() << std::endl;
     std::cout << "PMO: Best z     = " << m->getBestZ() << std::endl;
     std::cout << "PMO: Best value = " << m->getBestValue() << std::endl;
     std::cout << "PMO: Funkcje policzono   = " << sumCounter()/1000000 << " milionow razy" << std::endl;
     std::cout << "PMO: Roznica czasu pracy = " << ( stop - start - CZAS_PRACY ) << " msec" << std::endl;
     std::cout << "PMO: User time           = " << 10*( stopTMS.tms_utime - startTMS.tms_utime ) << " msec" << std::endl;
     std::cout << "PMO: User / real time    = " << ( stopTMS.tms_utime - startTMS.tms_utime ) * 1000.0 / ( stop - start ) << "%" << std::endl;
     std::cout << "PMO: System time         = " << ( stopTMS.tms_stime - startTMS.tms_stime ) * 10 << " msec" << std::endl;
     
     delete m;
   } 

   return 0;
}
