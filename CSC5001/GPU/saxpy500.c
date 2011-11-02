/*
 * Application saxpy: y = A.x + y
 * Calcul sur le CPU
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define A 3

/***    Fonction y=ax + y    ***/
void saxpy(float *tabX, float * tabY, int SIZE)
{
   int k;
   for (k=0; k<SIZE; k++)   tabY[k]= A * tabX[k] + tabY[k];
}
/***    Initialisation des vecteurs    ***/
void initab(float * tab, float val, int SIZE)
{
   int k;
   for (k=0; k<SIZE; k++)     tab[k]= val;
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
    int nbelements=500;//valeur initiale
    float * tablox;
    float * tabloy;
    printf("Debut du programme avec %d elements \n",nbelements);

    /***************************************/
        tablox=(float *) malloc(sizeof(float) * nbelements);
        initab(tablox, 1., nbelements);
        tabloy=(float *) malloc(sizeof(float) * nbelements);
        initab(tabloy, 2., nbelements);
        affitab("tabloy",tabloy, nbelements);

    /*****    Appel de la fonction    *****/
         saxpy(tablox, tabloy, nbelements);
    /**************************************/
        affitab("tabloy",tabloy, nbelements);  
    free(tablox);
    free(tabloy);
    printf("Fin du programme\n");
    return EXIT_SUCCESS;
}


