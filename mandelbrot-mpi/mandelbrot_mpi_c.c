#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "mpi.h"
#include <unistd.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>
#include <omp.h>
#ifdef WITH_DISPLAY
#include "mandelbrot-gui.h"     /* has setup(), interact() */
#endif

#define NPIXELS     800         /* size of display window in pixels */
#define N           2

/* Structure definition for complex numbers */
typedef struct {
    double r, i;
} complex ;

/* Shorthand for some commonly-used types */
typedef unsigned int uint;
typedef unsigned long ulong;

void send_block(int* rows_per_worker_rem, int start_msg[], int *rows_per_worker, int last_row_per_worker[], int tag, int i, int rank);

int main(int argc, char *argv[]) {
    // Used for debug purpose :
    double calc_time=0;
    double start_calc,start_com;
    double com_time=0;
    
    double com_total,calc_total;


    Display *display;
    Window win;
    GC gc;
    int setup_return;

    double scale_r, scale_i, scale_color, temp, lengthsq;
    complex z, c;

    double r_min = -N;
    double r_max = N;
    double i_min = -N;
    double i_max = N;

    // usage mandelbrot_mpi <rows per worker> <max iteration>
    uint width = NPIXELS;        
    uint height = NPIXELS;
    
    int size, rank, ierr, i, j, col, row, k, maxiter;
    ulong color[2],couleur;
    int rows_per_worker,rows_per_worker_rem;
    int start_msg[2];
    int tag = 42;
    MPI_Status status;
    int data_msg[NPIXELS +1];
    int* last_row_per_worker;

    // Initialisation de MPI
    ierr = MPI_Init( &argc, &argv ) ; 
    ierr = MPI_Comm_size( MPI_COMM_WORLD, &size ) ; 
    ierr = MPI_Comm_rank( MPI_COMM_WORLD, &rank ) ; 
    
    // Set OMP threads num
    omp_set_num_threads(4);
    
    start_calc=MPI_Wtime();
    /* Compute factors to scale computational region to window */
    scale_r = (double) (r_max - r_min) / (double) width;
    scale_i = (double) (i_max - i_min) / (double) height;
    
    maxiter = atoi(argv[2]);
    calc_time+=MPI_Wtime()-start_calc;
    // Envoie des couleurs max et min
    if (rank == 0) {
#ifdef WITH_DISPLAY
      /* Initialize for graphical display */
      setup_return = setup(width, height, &display, &win, &gc, &color[0], &color[1]);
      if (setup_return != EXIT_SUCCESS) {
         fprintf(stderr, "Unable to initialize display, continuing\n");
      }
    /* (if not successful, continue but don't display results) */
#else
    color[0]=0;
    color[1]=16777215;
#endif
        // Calcul du nombre de worker travaillant sur TailleBloc+1
        rows_per_worker = atoi(argv[1]);
        rows_per_worker_rem = height % (rows_per_worker*size); 
        
        start_msg[0] = 1;
        start_msg[1] = rows_per_worker;
        
        last_row_per_worker = malloc(size*sizeof(int));  
        
        /* Compute factor for color scaling */
        scale_color = (double) (color[1] - color[0]) / (double) (maxiter - 1);
    }
    start_com=MPI_Wtime();
    // Broadcast des couleurs min et max
    ierr = MPI_Bcast( &color, 2, MPI_UNSIGNED_LONG, 0, MPI_COMM_WORLD ) ;
    com_time+=MPI_Wtime()-start_com;
    // Envoie des premières lignes à calculer par chaque worker
    if (rank == 0) {
        start_com=MPI_Wtime();
        for (i = 1; i <= size-1; i++) {
            send_block(&rows_per_worker_rem, start_msg, &rows_per_worker, last_row_per_worker, tag, i, rank);
        }
        // une fois les premiers blocs de calculs envoyés, on attend les réponses pour renvoyer du travail !
        for (i = 0; i <= height - 1; i++) {
            // Réception des messages de réponse
            ierr = MPI_Recv(&data_msg, width + 1, MPI_INT, MPI_ANY_SOURCE, tag, MPI_COMM_WORLD, &status ) ;
            com_time+=MPI_Wtime()-start_com;
            // Gestion de l'affichage d'une ligne
            start_calc=MPI_Wtime();
            for (j=1; j <= width; j++) {
                couleur = (ulong) ((data_msg[j]-1) * scale_color) + color[0];
#ifdef WITH_DISPLAY
                if (setup_return == EXIT_SUCCESS) { 
                    XSetForeground (display, gc, couleur);
                    XDrawPoint (display, win, gc, j, data_msg[0]); 
                }
#endif
            }
            calc_time+=MPI_Wtime()-start_calc;
            if (last_row_per_worker[status.MPI_SOURCE] <= NPIXELS) {
                //Si la ligne reçue est la dernière du paquet que nous envoie le processus, on lui réenvoie du travail !
                if (data_msg[0] == last_row_per_worker[status.MPI_SOURCE]) {
                    start_com=MPI_Wtime();
                    send_block(&rows_per_worker_rem, start_msg, &rows_per_worker, last_row_per_worker, tag, status.MPI_SOURCE, rank);
                    com_time+=MPI_Wtime()-start_com;
                }
            }
        }
        // Terminaison des esclaves
        start_msg[0]=0;
        start_msg[1]=0;
        start_com=MPI_Wtime();
        for (i = 1; i <= size-1; i++) {
            ierr = MPI_Send(start_msg, 2, MPI_INT, i, tag, MPI_COMM_WORLD ) ;
        }
        com_time+=MPI_Wtime()-start_com;
        
        // Tous les points ont été reçus
 #ifdef WITH_DISPLAY
        if (setup_return == EXIT_SUCCESS) {
            XFlush (display);
            interact(display, &win, width, height,r_min, r_max, i_min, i_max);
        }
#endif
    }
    else {
        while (1) {
            start_com=MPI_Wtime();
            // Réception des numéros de ligne à traiter
            ierr = MPI_Recv( &start_msg, 2, MPI_INT, MPI_ANY_SOURCE, tag, MPI_COMM_WORLD, &status ) ;
            com_time+=MPI_Wtime()-start_com;
            if ((start_msg[0] == 0) && (start_msg[1] == 0)) {
                break;
            }
            // --------------------------------------------------------------------
            // DEBUT CALCUL
            #pragma omp parallel for private(data_msg, row, z, col, c, k, temp, lengthsq,start_calc,start_com) firstprivate(start_msg) reduction(+:calc_time,com_time)
            for (i=0; i < start_msg[1]; i++) {
                start_calc=MPI_Wtime();
                // ligne à calculer
                row = start_msg[0] + i;
                data_msg[0] = row;

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
                    
                    // Data à envoyer : k
                    data_msg[col+1] = k;
                    
                 }
                 calc_time+=MPI_Wtime()-start_calc;
                 start_com=MPI_Wtime();
                 // Fin du calcul d'une ligne, on envoie le résultat
                 ierr = MPI_Send(&data_msg, width + 1, MPI_INT, 0, tag, MPI_COMM_WORLD ) ;
                 com_time+=MPI_Wtime()-start_com;
            }
        }
    }
    // Concatenate all results
    MPI_Reduce( &com_time, &com_total, 1, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD );
    MPI_Reduce( &calc_time, &calc_total, 1, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD );
    if (rank==0) {
        fprintf(stderr,"Com:%f  Calc:%f",com_total,calc_total);
        
    }
    return EXIT_SUCCESS;
}

void send_block(int *rows_per_worker_rem,int start_msg[], int *rows_per_worker, int last_row_per_worker[],int tag,int i, int rank) {
    int ierr;
    if (*rows_per_worker_rem > 0) {
        start_msg[1] = *rows_per_worker + 1;
        (*rows_per_worker_rem)--;
    }
    else {
        start_msg[1] = *rows_per_worker;
    }
    last_row_per_worker[i] = start_msg[0] + start_msg[1] - 1;
    
    // Empèche d'envoyer des lignes en trop
    if (start_msg[0] <= NPIXELS) {
        ierr = MPI_Send(start_msg, 2, MPI_INT, i, tag, MPI_COMM_WORLD ) ;
    }
    start_msg[0] += start_msg[1];
}
