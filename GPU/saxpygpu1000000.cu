#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>

#include <cutil.h>
#include <cuda.h>

#define A 3

/***    Initialisation des vecteurs    ***/
void initab(float * tab, float val, int SIZE) {
   int k;
   for (k=0; k<SIZE; k++)   {
   tab[k]= val;
   }
}

__global__ void saxpy_gpu(float *tabX, float * tabY, int sizemax) {
   // Parametrer l'operation avec threadIdx.x;
    int k = threadIdx.x + blockDim.x * (blockIdx.x + blockIdx.y * gridDim.x);

    if (k<sizemax) {
    tabY[k] = tabX[k]*A + tabY[k];
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

    float * tablox_d, * tablox_h;
    float * tabloy_d, * tabloy_h;

    int nbelements=35000000;//valeur initiale
    int maxdim = 65535;
    int sizeblock = 512;
    
    int nblock = (nbelements/sizeblock)+1;
    int nbligne = (nblock/maxdim)+1;
      /** Initialisation de  nbthreadbyblock et nbblockbygrid**/

    dim3 nbthreadbyblock(sizeblock);
    
    dim3 nbblockbygrid(maxdim,nbligne);

    printf("Debut du programme avec %d nbelements \n",nbelements);

      /** Allocation memoire sur le host(CPU) **/
    tablox_h=(float *) malloc(sizeof(float) * nbelements);
    initab(tablox_h, 1., nbelements);
    tabloy_h=(float *) malloc(sizeof(float) * nbelements);
    initab(tabloy_h, 2., nbelements);


      /** Affichage initial **/
    printf("Affichage initial\n");
    affitab("tabloy_h",tabloy_h, nbelements);

      /** Allocation memoire sur le device(GPU) **/
    // Utilisation de cudaMalloc
    CUDA_SAFE_CALL(cudaMalloc((void**) &tablox_d, sizeof(float) * nbelements));
    CUDA_SAFE_CALL(cudaMalloc((void**) &tabloy_d, sizeof(float) * nbelements));

      /** Transfert memoire du host vers le device **/
    CUDA_SAFE_CALL(cudaMemcpy((void*)tablox_d, (void*) tablox_h, sizeof(float) * nbelements, cudaMemcpyHostToDevice));
    CUDA_SAFE_CALL(cudaMemcpy((void*)tabloy_d, (void*) tabloy_h, sizeof(float) * nbelements, cudaMemcpyHostToDevice));

      /** Lancement du kernel **/
    saxpy_gpu <<<nbblockbygrid, nbthreadbyblock>>> (tablox_d, tabloy_d, nbelements);

      /** Synchronisation **/
    CUDA_SAFE_CALL(cudaThreadSynchronize());

      /** Transfert memoire du device vers le host **/
    //CUDA_SAFE_CALL(cudaMemcpy((void*)tablox_h, (void*) tablox_d, sizeof(float) * nbelements, cudaMemcpyDeviceToHost));
    CUDA_SAFE_CALL(cudaMemcpy((void*)tabloy_h, (void*) tabloy_d, sizeof(float) * nbelements, cudaMemcpyDeviceToHost));

      /** Affichage du resultat **/
    printf("Affichage du resultat\n");
    affitab("tabloy_h",tabloy_h, nbelements);

      /** Liberation memoire **/
        cudaFree(tablox_d); cudaFree(tabloy_d);
        free(tablox_h); free(tabloy_h);  

    printf("Fin du programme\n");
    return EXIT_SUCCESS;

}


