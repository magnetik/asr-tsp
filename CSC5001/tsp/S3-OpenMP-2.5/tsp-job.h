#ifndef _TSP_JOB_H
#define _TSP_JOB_H
/* gestion de queues de jobs */

/* Structure pour la t�te de queue */
#define MAXNBJOBS 10000

typedef struct {
   Path_t path ;
   int hops ;
   int len ;
} Job_t ;

typedef struct {
  Job_t jobs[MAXNBJOBS];
  int nbjobs;
} Queue_t ;

typedef Queue_t TSPqueue;

/* initialise queue */
extern void init_queue(TSPqueue *q);

/* ajoute un job � la queue [q]: chemin [p], [hops] villes parcourues, longueur [len] parcourue */
extern void add_job (TSPqueue *q, Path_t p, int hops, int len) ;

/* stocke le job d'indice numjob dans [p], [hops] et [len]. Peut retourner 0 si la queue
 * est temporairement vide */
extern int get_job (TSPqueue *q, int numjob, Path_t p, int *hops, int *len) ;

/* retourne le nombre de jobs courants dans la file */
extern int get_nbjobs(TSPqueue *q);
#endif
