#ifndef FUNCTION_H
#define FUNCTION_H

#include<omp.h>
#include<stdlib.h>
#include<sys/time.h>

class Function {
        private:
		static void wait( double repetitions );
	public:
		static double sleep;
                static long *counter;
		static double value( double x, double y, double z );
};

#endif

