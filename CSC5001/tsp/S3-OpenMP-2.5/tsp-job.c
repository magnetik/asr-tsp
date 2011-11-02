/* impl�mentation des queues de jobs, nul besoin de lire dans un premier temps */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "tsp-types.h"
#include "tsp-job.h"

void init_queue(TSPqueue *q)
{
	q->nbjobs = 0;
}

void add_job (TSPqueue *q, Path_t path, int hops, int len) 
{
	Job_t *job;

	q->nbjobs++;

	if (q->nbjobs == MAXNBJOBS)
	{
		fprintf(stderr,"trop de jobs: %d, augmentez MAXNBJOBS (%d) dans tsp-jobs.h\n", q->nbjobs, MAXNBJOBS);
		exit(1);
	}

	job = &(q->jobs[q->nbjobs - 1]);

	job->len = len ;
	job->hops = hops ;
	memcpy(job->path, path, hops * sizeof(int));
}


int get_nbjobs(TSPqueue *q)
{
	return q->nbjobs;
}

int get_job (TSPqueue *q, int numjob, Path_t p, int *hops, int *len) 
{
	Job_t *job;

	if (q->nbjobs == 0 || q->nbjobs <= numjob )
		return 0 ;

	job = &(q->jobs[numjob]);

	*len = job->len ;
	*hops = job->hops ;
	memcpy(p, job->path, *hops * sizeof(int));

	return 1 ;
} 












