#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>

#include <cutil.h>
#include <cuda.h>

#define A 3

__global__ void saxpy_gpu(float *tab, int nbintervals) {
   // Parametrer l'operation avec threadIdx.x;
    int k = threadIdx.x + blockDim.x * (blockIdx.x + blockIdx.y * gridDim.x);
    float delta = (float) 1.0/nbintervals;
    if (k<nbintervals) {
        tab[k] = (4.0/(1.0+(k*delta)*(k*delta))) * delta;
    }
}

/***    Affichage des vecteurs    ***/
void affitab(char * chaine,float * tab, int SIZE)
{
   int k;
   printf("\nLes 16 premiers de %s: \n",chaine);
   for (k=0; k<16; k++) printf("%.2f ",tab[k]);
   printf("\nLes 16 derniers: \n");
   for (k=SIZE-16; k<SIZE; k++) printf("%.2f ",tab[k]);
   printf("\n");
}

////////////////////////////////////////////////////////////////////////////////
// Program main
////////////////////////////////////////////////////////////////////////////////
int
main( int argc, char** argv) 
{ 

    float * tablo_d, * tablo_h;

    int nbintervals=1000;//valeur initiale
    int maxdim = 65535;
    int sizeblock = 512;
    
    float delta = (float) 1.0/nbintervals;
    
    int nblock = (nbintervals/sizeblock)+1;
    int nbligne = (nblock/maxdim)+1;
    
    int i;
    float pi = 0.;
      /** Initialisation de  nbthreadbyblock et nbblockbygrid**/

    dim3 nbthreadbyblock(sizeblock);
    
    dim3 nbblockbygrid(maxdim,nbligne);

      /** Allocation memoire sur le host(CPU) **/
    tablo_h=(float *) malloc(sizeof(float) * nbintervals);

      /** Allocation memoire sur le device(GPU) **/
    CUDA_SAFE_CALL(cudaMalloc((void**) &tablo_d, sizeof(float) * nbintervals));

      /** Lancement du kernel **/
    saxpy_gpu <<<nbblockbygrid, nbthreadbyblock>>> (tablo_d, nbintervals);

      /** Synchronisation **/
    CUDA_SAFE_CALL(cudaThreadSynchronize());

      /** Transfert memoire du device vers le host **/
    CUDA_SAFE_CALL(cudaMemcpy((void*)tablo_h, (void*) tablo_d, sizeof(float) * nbintervals, cudaMemcpyDeviceToHost));


    //Réduction cpu
    for (i=0; i<nbintervals; i++) {
        pi += tablo_h[i];
    }
    pi = pi - ( (4. + 2.) * delta / 2.);
      /** Affichage du resultat **/
    printf("Affichage du resultat ; pi= %f\n",pi);
    

      /** Liberation memoire **/
        cudaFree(tablo_d); 
        free(tablo_h);   

    printf("Fin du programme\n");
    return EXIT_SUCCESS;

}


