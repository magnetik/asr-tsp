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
 
#define TIME_DIFF(t1, t2) \
		((t2.tv_sec - t1.tv_sec) * 1000000 + (t2.tv_usec - t1.tv_usec))
		
#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <unistd.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>
//#ifdef WITH_DISPLAY
#include "mandelbrot-gui.h"     /* has setup(), interact() */
//#endif
/* Default values for things. */
#define N           2           /* size of problem space (x, y from -N to N) */
#define NPIXELS     600         /* size of display window in pixels */

/* Structure definition for complex numbers */
typedef struct {
  float r, i;
} complex ;

/* Shorthand for some commonly-used types */
typedef unsigned int uint;
typedef unsigned long ulong;

/* Description pixel */
typedef struct {
  uint col, row;
  ulong couleur; 
} Pixel ;

////////////////////////////////////////////////////////////////////////////////
// Main program
/////////////////////////////////////////////////////////////////////////////

int main(int argc, char *argv[]) {
  uint maxiter;
  float r_min = -N;
  float r_max = N;
  float i_min = -N;
  float i_max = N;
  uint width = NPIXELS;         /* dimensions of display window */
  uint height = NPIXELS;
  Display *display;
  Window win;
  GC gc;
  int setup_return;
  ulong min_color, max_color;
  float scale_r, scale_i, scale_color;
  uint col, row, k, indice;
  complex z, c;
  float lengthsq, temp;
  ulong couleur;
  ulong * vect_h;
  ulong temps;
  
  	struct timeval t1, t2;

  
  /* Check command-line arguments */
  if (argc < 2) {
    fprintf(stderr, "usage:  %s maxiter \n", argv[0]);
    return EXIT_FAILURE;
  }

  vect_h = (ulong *) malloc(sizeof(ulong) * NPIXELS * NPIXELS);

  //printf("Debut du programme\n");
  /* Process command-line arguments */
  maxiter = atoi(argv[1]);
#ifdef WITH_DISPLAY
  /* Initialize for graphical display */
  setup_return = 
    setup(width, height, &display, &win, &gc, &min_color, &max_color);
  if (setup_return != EXIT_SUCCESS) {
    fprintf(stderr, "Unable to initialize display, continuing\n");
  }
#else
    min_color=0;
    max_color=16777215;
#endif
  /* Calculate and draw points */
  
  /* Compute factors to scale computational region to window */
  scale_r = (float) (r_max - r_min) / (float) width;
  scale_i = (float) (i_max - i_min) / (float) height;
  
  /* Compute factor for color scaling */
  scale_color = (float) (max_color - min_color) / (float) (maxiter - 1);

gettimeofday(&t1,NULL);

  /* Calculate points and display */
  //printf("Debut du calcul des pixels CPU\n");
  for (row = 0; row < height; ++row)      
    for (col = 0; col < width; ++col) {
      
      z.r = z.i = 0;
      
      /* Scale display coordinates to actual region  */
      c.r = r_min + ((float) col * scale_r);
      c.i = i_min + ((float) (height-1-row) * scale_i);

      /* Calculate z0, z1, .... until divergence or maximum iterations */
      k = 0;
      do  {
	temp = z.r*z.r - z.i*z.i + c.r;
	z.i = 2*z.r*z.i + c.i;
	z.r = temp;
	lengthsq = z.r*z.r + z.i*z.i;
	++k;
      } while (lengthsq < (N*N) && k < maxiter);
      
      /* Scale color and display point */ 
      couleur = (ulong) ((k-1) * scale_color) + min_color;
      indice = NPIXELS * row + col;
      vect_h[indice]=couleur;
    }

    //printf("Fin du calcul des pixels CPU\n");
	gettimeofday(&t2,NULL);

	temps = TIME_DIFF(t1,t2);
	printf("%ld.%03ld\n", temps/1000, temps%1000);
#ifdef WITH_DISPLAY
     //printf("Debut affichage\n");
     for (k=0; k<(NPIXELS*NPIXELS); k++)

      if (setup_return == EXIT_SUCCESS) {
	XSetForeground (display, gc, vect_h[k]);
	XDrawPoint (display, win, gc, k%NPIXELS, k/NPIXELS);
      }
     //printf("Fin affichage\n");
#endif
     //printf("Fin attente\n");

  return EXIT_SUCCESS;
}

