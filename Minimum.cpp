#include"Minimum.h"
#include<stdlib.h>   // potrzebne dla random()
#include<math.h>     // bo sin i cos
#include<iostream>
#include <sys/time.h>

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
  
  while ( hasTimeToContinue() ) {
  // inicjujemy losowo polozenie startowe w obrebie kwadratu o bokach od min do max 
    x = (double)random() * ( max - min ) / RAND_MAX + min;
    y = (double)random() * ( max - min ) / RAND_MAX + min;
    z = (double)random() * ( max - min ) / RAND_MAX + min;
    
    v = f->value( x, y, z ); // wartosc funkcji w punkcie startowym
  
    idleSteps = 0;
    dr = dr_ini;        
        
    while ( dr > dr_fin ) { 
       xnew = x + ( random()%2 - 2 ) * dr;
       ynew = y + ( random()%2 - 2 ) * dr;
       znew = z + ( random()%2 - 2 ) * dr;
              
       // upewniamy sie, ze nie opuscilismy przestrzeni poszukiwania rozwiazania
       xnew = limit( xnew );
       ynew = limit( ynew );
       znew = limit( znew );
       
       // wartosc funkcji w nowym polozeniu
       vnew = f->value( xnew, ynew, znew );
       
       if ( vnew < v ) {
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
   
   if ( v < bestV ) {  // znalezlismy najlepsze polozenie globalnie
      bestV = v;
      bestX = x;
      bestY = y;
      bestZ = z;
      
      std::cout << "New better position: " << x << ", " << y << ", " << z << " value = " << v << std::endl;
   }   
  } // mamy czas na obliczenia
  
}

