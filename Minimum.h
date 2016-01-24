#ifndef MINIMUM_H
#define MINIMUM_H

#include"Function.h"

class Minimum {
	private:
		double bestX, bestY, bestZ, bestV; // najlepsza znaleziona pozycja i wartosc
		const Function *f;  // wskaznik do minimalizowanej funkcji
		const double min, max;
		
		double timeLimit; // czas zakonczenia obliczen
		
		double limit( double x ); // ograniczenie przestrzeni przeszukowania
		bool hasTimeToContinue();   // sprawdza czy nie minal czas na obliczenia
		void initializeTimeLimit( double msec );

	public:
		void find( double dr_ini, double dr_fin, int idleStepsLimit, double msec );
		
		double getBestX() {
		  return bestX;
		}
		double getBestY() {
		  return bestY;
		}
		double getBestZ() {
		  return bestZ;
		}
		double getBestValue() {
		  return bestV;
		}
		
		// Do konstruktora przekazywana jest funkcja do minimalizacji oraz obszar poszukiwania rozwiazania
		Minimum( Function *f, double min, double max );

		~Minimum();
};

#endif

