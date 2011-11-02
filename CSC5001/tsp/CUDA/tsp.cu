/*
 * Sequential Mandelbrot program 
 * 
 * This program computes and displays all or part of the Mandelbrot 
 * set.  By default, it examines all points in the complex plane
 * that have both real and imaginary parts between -2 and 2.  
 * Command-line parameters allow zooming in on a specific part of
 * this range.
 * 
 * Usage:
 *   mandelbrot maxiter 
 * where 
 *   maxiter denotes the maximum number of iterations at each point
 * 
 * Input:  none, except the optional command-line arguments
 * Output: a graphical display as described in Wilkinson & Allen,
 *   displayed using the X Window system, plus text output to
 *   standard output showing the above parameters.
 * 
 * 
 * Code originally obtained from Web site for Wilkinson and Allen's
 * text on parallel programming:
 * http://www.cs.uncc.edu/~abw/parallel/par_prog/
 * 
 * Reformatted and revised by B. Massingill and C. Parrot and C.Schuller
 */
#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <unistd.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>

#include <cutil.h>
#include <cuda.h>

#include "tsp-types.h"

/* Shorthand for some commonly-used types */
typedef unsigned int uint;
typedef unsigned long ulong;

void genmap () 
{
    #define MAXX	100
    #define MAXY	100
    typedef struct
		    {
		     int x, y ;
		    } coor_t ;

    typedef coor_t coortab_t [MAXNBCITIES] ;
    coortab_t towns ;
    int i, j ;
    int dx, dy ;

     if (NbCities > MAXNBCITIES) {
      fprintf(stderr,"trop de villes, augmentez MAXNBCITIES dans tsp-types.h");
      exit(1);
     }

     srand (seed) ;

     for (i=0; i<NbCities; i++)
      {
       towns [i].x = rand () % MAXX ;
       towns [i].y = rand () % MAXY ;
      }

     for (i=0; i<NbCities; i++)
      {
       for (j=0; j<NbCities; j++)
        {
         /* Un peu r�aliste */
         dx = towns [i].x - towns [j].x ;
         dy = towns [i].y - towns [j].y ;
         distance [i][j] = (int) sqrt ((double) ((dx * dx) + (dy * dy))) ;
        }
      }
}


__global__ void tsp_gpu() {
   
}
////////////////////////////////////////////////////////////////////////////////
// Main program
/////////////////////////////////////////////////////////////////////////////

int main(int argc, char *argv[]) {
    uint32 ** Solution ;
    
    genmap(); 
    
    Solution = new uint32*[MAIN_NbIter] ;
    Solution[0] = new uint32[NbNode*MAIN_NbIter] ;

    return EXIT_SUCCESS;
}

