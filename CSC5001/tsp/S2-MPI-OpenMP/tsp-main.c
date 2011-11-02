#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <limits.h>
#include <sys/time.h>

#include "tsp-types.h"
#include "tsp-job.h"
#include "mpi.h"
#include "omp.h"

/* macro de mesure de temps, retourne une valeur en �secondes */
#define TIME_DIFF(t1, t2) \
		((t2.tv_sec - t1.tv_sec) * 1000000 + (t2.tv_usec - t1.tv_usec))



/* variables globales */
/* dernier minimum trouv� */
int    minimum ;
/* Nombre total de jobs calculés par tsp_partiel */
int    njobsTotal=0;

/* liste des t�ches */
TSPqueue     listeTaches ;

/* tableau des distances */
DTab_t    distance ;

int NbCities ;
int seed;

#define MINNBHOPS 3

/* initialisation du tableau des distances */
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


/* impression tableau des distances, pour v�rifier au besoin */
void PrintDistTab ()
{
	int i, j ;

	printf ("Nombre de villes = %d\n", NbCities) ;

	for (i=0; i<NbCities; i++)
	{
		for (j=0; j<NbCities; j++)
		{
			printf (" Ville %1d ---> Ville %1d: %2d \n", i, j, distance[i][j]) ;
		}
	}
	printf ("OK ...\n") ;

}

void PrintPath(char *msg, int hops, Path_t path)
{
	int i;

	printf("%s", msg);
	for (i = 0; i < hops; i ++)
	{
		for (i=0; i < hops; i++)
			printf ("%2d ", path[i]) ;
		printf ("\n") ;
	}
}
/* r�solution du probl�me du voyageur de commerce */

int present (int city, int hops, Path_t path)
{
	unsigned int i ;

	for (i=0; i<hops; i++)
		if (path [i] == city) return 1 ;
	return 0 ;
}

void tsp (int hops, int len, Path_t path, int *cuts)
{
	int i ;
	int me, dist ;

	if (len >= minimum)
	{
#ifdef DEBUG
		//PrintPath("Too long path : ", hops, path);
#endif
		(*cuts)++ ;
		return;
	}

	if (hops == NbCities)
	{
#pragma omp critical
		if (len < minimum)
		{
			minimum = len ;
			//fprintf (stderr,"found path len = %3d :", len) ;
			//for (i=0; i < NbCities; i++)
			//  fprintf (stderr,"%2d ", path[i]) ;
			//fprintf (stderr,"\n");
		}
	}
	else
	{
		me = path [hops-1] ;

		for (i=0; i < NbCities; i++)
		{
			if (!present (i, hops, path))
			{
				path [hops] = i ;
				dist = distance[me][i] ;
				if ((len+dist) < minimum)
				    tsp (hops+1, len+dist, path, cuts) ;
			}
		}

	}
}

void tsp_partiel (int hops, int len, Path_t path, int *cuts, int rank)
{
	int i ;
	int me, dist ;
	int maxhops;

	if (len >= minimum)
	{
		(*cuts)++ ;
		return;
	}

	maxhops = MINNBHOPS;
	if (rank != 0)
		maxhops++;

	if (hops == maxhops)
	{
#ifdef DEBUG
		//PrintPath("Add job : ", hops, path);
#endif
		add_job (&listeTaches, path, hops, len);
		if (rank == 0 )
			njobsTotal++;
	}
	else
	{
		me = path [hops-1] ;

		for (i=0; i < NbCities; i++)
		{
			if (!present (i, hops, path))
			{
				path [hops] = i ;
				dist = distance[me][i] ;
				tsp_partiel (hops+1, len+dist, path, cuts, rank) ;
			}
		}

	}
}

int main (int argc, char **argv)
{
	int rank, size, ierr ;
	MPI_Status status;
	int aTraiter[MAXNBCITIES+2];
	int min;
	int tag = 42;
	Path_t path;
	int hops, len;
	int j;

	int nbjobsr=0;

	unsigned long temps;
	int cuts = 0;
	struct timeval t1, t2;

	// Initialisation de MPI
	ierr = MPI_Init( &argc, &argv ) ;
	ierr = MPI_Comm_size( MPI_COMM_WORLD, &size ) ;
	ierr = MPI_Comm_rank( MPI_COMM_WORLD, &rank ) ;


	// master initialise le path et la liste de tache

	if (argc != 3)
	{
		fprintf (stderr, "Usage: %s  <nbcities ( MAXNBCITIES = %d )> <seed> \n", argv[0], MAXNBCITIES) ;
		exit (1) ;
	}

	NbCities = atoi (argv[1]) ;
	seed = atoi(argv[2]);

	minimum = INT_MAX ;
	//fprintf (stderr,"NbCities = %3d\n", NbCities) ;

	init_queue (&listeTaches) ;
	genmap () ;

	gettimeofday(&t1,NULL);

	int i;

	for(i = 0; i < MAXNBCITIES; i++)
		path[i] = -1 ;

	/* Ville de d�part : Ville 0 */
	path [0] = 0;


	if (rank == 0) {
		tsp_partiel (1, 0, path, &cuts, rank);
		no_more_jobs (&listeTaches);

		gettimeofday(&t2,NULL);

		temps = TIME_DIFF(t1,t2);
		//printf("time = %ld.%03ldms (%d coupures)\n", temps/1000, temps%1000, cuts);

		gettimeofday(&t1,NULL);


		while (nbjobsr != (njobsTotal+size-1)) {
			//bloque en réception du résultat
			ierr = MPI_Recv(&min, 1, MPI_INT, MPI_ANY_SOURCE, tag, MPI_COMM_WORLD, &status ) ;
#ifdef DEBUG
			fprintf(stderr,"O: Recv min: %d de source: %d\n", min, status.MPI_SOURCE);
#endif
			nbjobsr++;
			// calcul du min
			if (min < minimum)
				minimum = min;

			if (get_job(&listeTaches, path, &hops, &len)) {
				//préparation du nouveau job à envoyer
				for (j=0; j<MAXNBCITIES; j++)
					aTraiter[j] = path[j];
				aTraiter[MAXNBCITIES] = hops;
				aTraiter[MAXNBCITIES+1] = len;

				//envoie du job aTraiter
				ierr = MPI_Send(&aTraiter, MAXNBCITIES+2, MPI_INT, status.MPI_SOURCE, tag+1, MPI_COMM_WORLD);
#ifdef DEBUG
				fprintf(stderr,"0: Send aTraiter to: %d\n", status.MPI_SOURCE);
#endif

			}
		}
		// Envoit de la terminaison
		for (i=1; i < size;i++) {
			aTraiter[0] = -1;
			ierr = MPI_Send(&aTraiter, MAXNBCITIES+2, MPI_INT, i, tag+1, MPI_COMM_WORLD);
		}
		gettimeofday(&t2,NULL);

		temps = TIME_DIFF(t1,t2);
		printf("***********************minimum = %d time = %ld ms (%d coupures)\n",minimum, temps, cuts);

	}

	else {
		//Envoie d'une demande de traitement
		min = INT_MAX;
		ierr = MPI_Send(&min, 1, MPI_INT, 0, tag, MPI_COMM_WORLD);
#ifdef DEBUG
		fprintf(stderr, "%d: Send min: %d to: 0\n", rank, min);
#endif

		while (1) {
			//Attend de recevoir le job aTraiter
			ierr = MPI_Recv(&aTraiter, MAXNBCITIES+2, MPI_INT, MPI_ANY_SOURCE, tag+1, MPI_COMM_WORLD, &status ) ;

			//test la terminaison
			if (aTraiter[0] == -1) {
				break;
			}

			for (j=0; j<MAXNBCITIES; j++)
				path[j] = aTraiter[j];

			hops = aTraiter[MAXNBCITIES];
			len = aTraiter[MAXNBCITIES+1];
#ifdef DEBUG
			fprintf(stderr, "%d: Recv de source: 0 hops: %d len: %d path:", rank, hops, len);
			for (i=0; i < NbCities; i++)
				fprintf (stderr,"%2d ", path[i]) ;
			fprintf (stderr, "\n") ;
#endif

			//Calcul
			tsp_partiel(hops, len, path, &cuts, rank);
			no_more_jobs (&listeTaches);

			omp_set_num_threads(4);

#pragma omp parallel
#pragma omp single
			{
				while (get_job(&listeTaches, path, &hops, &len))
				{
;
#pragma omp task untied firstprivate(hops, len, path)
					{
						tsp(hops, len, path, &cuts);
					}
				}
			}


			//Envoie du min
			min = minimum;
			ierr = MPI_Send(&min, 1, MPI_INT, 0, tag, MPI_COMM_WORLD);
#ifdef DEBUG
			fprintf(stderr, "%d: Send min: %d to: 0\n", rank, min);
#endif
		}

	}
	ierr = MPI_Finalize();
	return 0 ;
}

