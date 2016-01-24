#include"Minimum.h"
#include<stdlib.h>   // potrzebne dla random()
#include<math.h>     // bo sin i cos
#include<iostream>
#include <sys/time.h>

#include <omp.h>

using namespace std;

Minimum::Minimum( Function *f, double min, double max ) : min( min ), max( max ), f(f) {
   bestX = bestY = bestZ = ( min + max ) * 0.5;
   bestV = f->value( bestX, bestY, bestZ );
   
   srandom( (unsigned)time(NULL) );
}

Minimum::~Minimum(){
}

// metoda zwraca pozycje z wnetrza obszaru poszukiwania
double Minimum::limit( double x ) {
   if ( x < min ) return min; // za male
   if ( x > max ) return max; // za duze
   return x; // x jest pomiedzy min a max, wiec zwracamy x
}

bool Minimum::hasTimeToContinue() {
  struct timeval tf;
  gettimeofday( &tf, NULL );
  double now = tf.tv_sec * 1000 + tf.tv_usec * 0.001;
  
  if ( now < timeLimit ) return true;  // limit czasu nie osiagniety
  return false; // juz po czasie, pora konczyc obliczenia
} 

void Minimum::initializeTimeLimit( double msec ) {
  struct timeval tf;
  gettimeofday( &tf, NULL );
  timeLimit = tf.tv_sec * 1000 + tf.tv_usec * 0.001 + msec; // ustawiamy czas zakonczenia obliczen    
}

void Minimum::find( double dr_ini, double dr_fin, int idleStepsLimit, double msec ) {

// ustalamy czas zakonczenia obliczen na msec od teraz
  initializeTimeLimit( msec );
  
  double x, y, z, v, r, xnew, ynew, znew, vnew, dr;
  int idleSteps = 0;  // liczba krokow, ktore nie poprawily lokalizacji

  struct drand48_data drand_buff;
  int seed;
  double random_1 = 0.0;
  double random_2 = 0.0;
  double random_3 = 0.0;
  double random_4 = 0.0;
  double random_5 = 0.0;
  double random_6 = 0.0;

  #pragma omp parallel private(x, y, z, seed, random_1, random_2, random_3, random_4, random_5, random_6, drand_buff, xnew, ynew, znew)
  {
    seed = time(NULL) + omp_get_thread_num();
    srand48_r(seed, &drand_buff);
    cout << "drandbuff " << drand_buff << endl;

    while (hasTimeToContinue()) {
        // inicjujemy losowo polozenie startowe w obrebie kwadratu o bokach od min do max
        drand48_r(&drand_buff, &random_1);
        drand48_r(&drand_buff, &random_2);
        drand48_r(&drand_buff, &random_3);
        x = random_1 * ( max - min ) + min;
        y = random_2 * ( max - min ) + min;
        z = random_3 * ( max - min ) + min;

        v = f->value(x, y, z); // wartosc funkcji w punkcie startowym

        idleSteps = 0;
        dr = dr_ini;

        while (dr > dr_fin) {
            drand48_r(&drand_buff, &random_4);
            drand48_r(&drand_buff, &random_5);
            drand48_r(&drand_buff, &random_6);
            xnew = x + ((int)random_4 % 2 - 2) * dr;
            ynew = y + ((int)random_5 % 2 - 2) * dr;
            znew = z + ((int)random_6 % 2 - 2) * dr;

            // upewniamy sie, ze nie opuscilismy przestrzeni poszukiwania rozwiazania
            xnew = limit(xnew);
            ynew = limit(ynew);
            znew = limit(znew);

            // wartosc funkcji w nowym polozeniu
            vnew = f->value(xnew, ynew, znew);

            if (vnew < v) {
                x = xnew;  // przenosimy sie do nowej, lepszej lokalizacji
                y = ynew;
                z = znew;
                v = vnew;
                idleSteps = 0; // resetujemy licznik krokow, bez poprawy polozenia
            } else {
                idleSteps++; // nic sie nie stalo
                dr *= 0.5; // zmniejszamy 2x dr
            }
        } // dr wciaz za duze

        #pragma omp critical
        {
            if (v < bestV) {  // znalezlismy najlepsze polozenie globalnie
                bestV = v;
                bestX = x;
                bestY = y;
                bestZ = z;

                std::cout << "New better position: " << x << ", " << y << ", " << z << " value = " << v << std::endl;
            }
        }
    } // mamy czas na obliczenia
  }
}
