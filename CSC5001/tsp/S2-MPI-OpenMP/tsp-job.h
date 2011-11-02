#ifndef _TSP_JOB_H
#define _TSP_JOB_H
#include "tsp-types.h"

/* gestion de queues de jobs */

/* Structure pour la t�te de queue */
typedef struct {
   struct Maillon *first ;
   struct Maillon *last ;
   int end;
} TSPqueue;

/* initialise la queue [q] */
extern void init_queue (TSPqueue *q) ;

/* ajoute un job � la queue [q]: chemin [p], [hops] villes parcourues, longueur [len] parcourue */
extern void add_job (TSPqueue *q, Path_t p, int hops, int len) ;

/* enl�ve un job de la queue [q], le stocke dans [p], [hops] et [len]. Peut retourner 0 si la queue
 * est temporairement vide */
extern int get_job (TSPqueue *q, Path_t p, int *hops, int *len) ;

/* indique qu'il n'y aura plus de jobs ajout�s � la queue */
extern void no_more_jobs (TSPqueue *q) ;

/* retourne 1 si la queue est finie (i.e. no_more_jobs() a �t� appel�, et tous
 * les jobs ont �t� enlev�s), 0 sinon */
extern int empty_queue (TSPqueue *q) ;
#endif
