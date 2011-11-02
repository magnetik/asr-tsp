#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <limits.h>
#include <sys/time.h>

#include "tsp-types.h"
#include "tsp-job.h"


/* macro de mesure de temps, retourne une valeur en �secondes */
#define TIME_DIFF(t1, t2) \
		((t2.tv_sec - t1.tv_sec) * 1000000 + (t2.tv_usec - t1.tv_usec))



/* variables globales */
/* dernier minimum trouv� */
int    minimum ;

/* liste des t�ches */
TSPqueue listeTaches ;

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
		fprintf(stderr,"trop de villes, augmentez MAXNBCITIES dans tsp-types.h\n");
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
		PrintPath("Too long path : ", hops, path);
#endif
		(*cuts)++ ;
		return;
	}

	if (hops == NbCities)
	{
		if (len < minimum)
		{
			minimum = len ;
			//printf ("found path len = %3d :", len) ;
			//for (i=0; i < NbCities; i++)
			//	printf ("%2d ", path[i]) ;
			//printf ("\n") ;
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
				tsp (hops+1, len+dist, path, cuts) ;
			}
		}

	}
}

void tsp_partiel (int hops, int len, Path_t path, int *cuts)
{
	int i ;
	int me, dist ;

	if (len >= minimum)
	{
		(*cuts)++ ;
		return;
	}

	if (hops == MINNBHOPS)
	{
#ifdef DEBUG
		PrintPath("Add job : ", hops, path);
#endif
		add_job (&listeTaches, path, hops, len);
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
				tsp_partiel (hops+1, len+dist, path, cuts) ;
			}
		}

	}
}

int main (int argc, char **argv)
{
	unsigned long temps;
	int cuts = 0;
	struct timeval t1, t2;

	if (argc != 3)
	{
		fprintf (stderr, "Usage: %s  <nbcities ( MAXNBCITIES = %d )> <seed> \n", argv[0], MAXNBCITIES) ;
		exit (1) ;
	}

	NbCities = atoi (argv[1]) ;
	seed = atoi(argv[2]);

	minimum = INT_MAX ;

	//printf ("NbCities = %3d\n", NbCities) ;

	init_queue (&listeTaches) ;
	genmap () ;

	gettimeofday(&t1,NULL);

	{
		Path_t path;
		int i;

		for(i = 0; i < MAXNBCITIES; i++)
			path[i] = -1 ;

		/* Ville de d�part : Ville 0 */
		path [0] = 0;

		tsp_partiel (1, 0, path, &cuts);
	}

	gettimeofday(&t2,NULL);

	temps = TIME_DIFF(t1,t2);
	//printf("time = %ld.%03ldms (%d coupures)\n", temps/1000, temps%1000, cuts);

	gettimeofday(&t1,NULL);

	{
		int j;
		Path_t path;
		int hops, len, nbjobs;
		cuts = 0;

		nbjobs = get_nbjobs(&listeTaches);

		for (j = 0; j < nbjobs; j++)
		{
			if (!get_job(&listeTaches, j, path, &hops, &len))
				break;

			tsp(hops, len, path, &cuts);
		}
	}

	gettimeofday(&t2,NULL);

	temps = TIME_DIFF(t1,t2);
	printf("minimum = %d time = %ld ms (%d coupures)\n", minimum, temps, cuts);
	return 0 ;
}
