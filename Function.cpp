#include"Function.h"
#include<math.h>

void Function::wait( double repetitions ) {
  do {
    repetitions -= 1.0;
  } while ( repetitions > 0.0 );
}


double Function::value( double x, double y, double z ) {

   ((Function::counter)[ omp_get_thread_num() ])++;

   wait( Function::sleep );

   double dx = x - 9;
   double dy = y - 11;
   double dz = z + 6;
      
   return sin(x)+sin(y)+sin(z) + cos(x) + cos(y) + cos(z) - 2.0 * exp( - sqrt( dx * dx + dy * dy + dz * dz ) / 20.0 );
}
