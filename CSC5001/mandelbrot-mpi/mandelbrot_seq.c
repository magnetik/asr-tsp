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
 *   mandelbrot maxiter [x0 y0 size]
 * where 
 *   maxiter denotes the maximum number of iterations at each point
 *   x0, y0, and size specify the range to examine (a square 
 *     centered at x0 + iy0 of size 2*size by 2*size -- by default, 
 *     a square of size 4 by 4 centered at the origin)
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
 * Reformatted and revised by B. Massingill and C. Parrot
 */ 
#include <stdio.h>  
#include <math.h>
#include <stdlib.h>
#include <unistd.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>
#include <omp.h>
#ifdef WITH_DISPLAY
#include "mandelbrot-gui.h"     /* has setup(), interact() */
#endif
/* Default values for things. */
#define N           2           /* size of problem space (x, y from -N to N) */
#define NPIXELS     800         /* size of display window in pixels */

/* Structure definition for complex numbers */
typedef struct {
	double r, i;
} complex ;

/* Shorthand for some commonly-used types */
typedef unsigned int uint;
typedef unsigned long ulong;

/* ---- Main program ---- */

int main(int argc, char *argv[]) {
	uint maxiter;
	double r_min = -N;
	double r_max = N;
	double i_min = -N;
	double i_max = N;
	uint width = NPIXELS;         /* dimensions of display window */
	uint height = NPIXELS;
#ifdef WITH_DISPLAY
	Display *display;
	Window win;
	GC gc;
	int setup_return;
#endif
	ulong min_color, max_color; 
	double scale_r, scale_i, scale_color;
	uint col, row, k;
	complex z, c;
	double lengthsq, temp;
	ulong couleur;
	omp_sched_t runtime_sched;
    int runtime_chunk;


	/* Check command-line arguments */
	if ((argc < 2) || ((argc > 2) && (argc < 5))) {
		fprintf(stderr, "usage:  %s maxiter [x0 y0 size]\n", argv[0]);
		return EXIT_FAILURE;
	}

	/* Process command-line arguments */
	maxiter = atoi(argv[1]);
	if (argc > 2) {
		double x0 = atof(argv[2]);
		double y0 = atof(argv[3]);
		double size = atof(argv[4]);
		r_min = x0 - size;
		r_max = x0 + size;
		i_min = y0 - size;
		i_max = y0 + size;
	}

#ifdef WITH_DISPLAY
	/* Initialize for graphical display */
	setup_return =
			setup(width, height, &display, &win, &gc, &min_color, &max_color);
	if (setup_return != EXIT_SUCCESS) {
		fprintf(stderr, "Unable to initialize display, continuing\n");
	}
	/* (if not successful, continue but don't display results) */
#else
	min_color=0;
	max_color=16777215;
#endif

	/* Calculate and draw points */

	/* Compute factors to scale computational region to window */
	scale_r = (double) (r_max - r_min) / (double) width;
	scale_i = (double) (i_max - i_min) / (double) height;

	/* Compute factor for color scaling */
	scale_color = (double) (max_color - min_color) / (double) (maxiter - 1);
	
	/* Print scheduler */


	int id=0;
	/* Calculate points and display */
	
	for (row = 0; row < height; ++row) {
		//printf("Ligne %i calculée par %i\n",row,id); 

		for (col = 0; col < width; ++col) {

			z.r = z.i = 0;

			/* Scale display coordinates to actual region  */
			c.r = r_min + ((double) col * scale_r);
			c.i = i_min + ((double) (height-1-row) * scale_i);
			/* height-1-row so y axis displays
			 * with larger values at top
			 */

			/* Calculate z0, z1, .... until divergence or maximum iterations */
			k = 0;
			do  {
				temp = z.r*z.r - z.i*z.i + c.r;
				z.i = 2*z.r*z.i + c.i;
				z.r = temp;
				lengthsq = z.r*z.r + z.i*z.i;
				++k;
			} while (lengthsq < (N*N) && k < maxiter);

			/* Scale color and display point  */
			couleur = (ulong) ((k-1) * scale_color) + min_color;
#ifdef WITH_DISPLAY
			if (setup_return == EXIT_SUCCESS) { 
                #pragma omp critical
                {               
				    XSetForeground (display, gc, couleur);
				    XDrawPoint (display, win, gc, col, row);
                }
			} 
#endif
		}
	}  

#ifdef WITH_DISPLAY
	/* Be sure all output is written */
	if (setup_return == EXIT_SUCCESS) {
		XFlush (display);
	}
#endif  
	/* Produce text output  */
	fprintf(stdout, "\n");
	fprintf(stdout, "center = (%g, %g), size = %g\n",
			(r_max + r_min)/2, (i_max + i_min)/2,
			(r_max - r_min)/2);
	fprintf(stdout, "maximum iterations = %d\n", maxiter);
	fprintf(stdout, "\n");

#ifdef WITH_DISPLAY
	/* Wait for user response, then exit program */
	if (setup_return == EXIT_SUCCESS) {
		interact(display, &win, width, height,
				r_min, r_max, i_min, i_max);
	}
#endif
	return EXIT_SUCCESS;
}

