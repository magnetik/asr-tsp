/* implémentation des queues de jobs, nul besoin de lire dans un premier temps */

#include "tsp-types.h"
#include "tsp-job.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct {
   Path_t path ;
   int hops ;
   int len ;
} Job_t ;

typedef struct Maillon 
{
   Job_t tsp_job ;
   struct Maillon *next ;
} Maillon;

void init_queue (TSPqueue *q)
{
   q->first = 0 ;
   q->last = 0 ;
   q->end = 0 ;
}


int empty_queue (TSPqueue *q)
{
   int b  ;

   b = ((q->first == 0) && (q->end == 1)) ;
   
   return b ;
}


void add_job (TSPqueue *q, Path_t p, int hops, int len) 
{
   Maillon *ptr ;
   
   ptr = malloc (sizeof (*ptr)) ;
   ptr->next = 0 ;
   ptr->tsp_job.len = len ;
   ptr->tsp_job.hops = hops ;
   memcpy(ptr->tsp_job.path, p, hops * sizeof(p[0]));
   
   if (q->first   == 0)
     q->first = q->last = ptr ;
   else
     {
	q->last->next = ptr ;
	q->last = ptr ;
     }
}


int get_job (TSPqueue *q, Path_t p, int *hops, int *len) 
{
   Maillon *ptr ;
   
   if (q->first == 0)
        return 0 ;
   
   ptr = q->first ;
   
   q->first = ptr->next ;
   if (q->first == 0)
     q->last = 0 ;

   *len = ptr->tsp_job.len ;
   *hops = ptr->tsp_job.hops ;
   memcpy(p, ptr->tsp_job.path, *hops * sizeof(p[0]));

   free (ptr) ;
   return 1 ;
} 

void no_more_jobs (TSPqueue *q)
{
   q->end = 1 ;
}











