/* FattoreX_s.c
 * 	+implementazione delle procedure remote: "prenota" e "visualizza".
 *	+inizializzazione struttura.
 */

#include "FattoreX.h"
#include <fcntl.h>
#include <rpc/rpc.h>
#include <stdio.h>
#include <sys/stat.h>
#include <sys/types.h>

/* STATO SERVER */
static Tabellone tabellone;
static int  inizializzato = 0;

void inizializza() {
    int i;

    if (inizializzato == 1) {
        return;
    }

    // inizializzazione struttura dati
    for (i = 0; i < NUMFILE; i++) {
        strcpy(tabellone.riga[i].candidato, "L");
        strcpy(tabellone.riga[i].giuduce, "L");
        tabellone.riga[i].categoria = 'L';
        strcpy(tabellone.riga[i].nomeFile, "L");
        tabellone.riga[i].fase = 'L';
        tabellone.riga[i].voto = -1;
    }

    
    i = 1;
    strcpy(tabellone.riga[i].candidato, "Nico");;
    strcpy(tabellone.riga[i].giuduce, "Caini");
    tabellone.riga[i].categoria = 'U';
    strcpy(tabellone.riga[i].nomeFile, "nico.txt");
    tabellone.riga[i].fase = 'A';
    tabellone.riga[i].voto = 1;
    i+=2;
    strcpy(tabellone.riga[i].candidato, "Filo");
    strcpy(tabellone.riga[i].giuduce, "Guidetti");
    tabellone.riga[i].categoria = 'D';
    strcpy(tabellone.riga[i].nomeFile, "filo.txt");;
    tabellone.riga[i].fase = 'A';
    tabellone.riga[i].voto = 0;
    i++;
    strcpy(tabellone.riga[i].candidato, "Ale");
    strcpy(tabellone.riga[i].giuduce, "Guidetti");
    tabellone.riga[i].categoria = 'O';
    strcpy(tabellone.riga[i].nomeFile, "ale.txt");
    tabellone.riga[i].fase = 'S';
    tabellone.riga[i].voto = 5;
    
    inizializzato = 1;
    printf("Terminata inizializzazione struttura dati!\n");
}

Output *classifica_giudici_1_svc(void *in, struct svc_req *rqstp) {
    if (inizializzato == 0) { //check dello stato del server
        inizializza();
    }

    //DA FIXARE
    printf("Ricevuta richiesta di visualizzazione della classifica dei giudici\n"); 
    Giudice g[NUMFILE];
    for (int i = 0; i < NUMFILE; i++){
        for (int j = i; j < NUMFILE; j++){
            if (strcmp(tabellone.riga[i].giuduce, g[j].giudice))
            {
                strcpy(g[j].giudice, tabellone.riga[i].giuduce);
                g[j].totaleVoti = tabellone.riga[i].voto;
            }
            else
                g[j].totaleVoti += tabellone.riga[i].voto;
        }
        
    }

    for (int i = 0; i < NUMFILE; i++){
        for (int j = i; j < NUMFILE; j++)
        {
            if (g[j].totaleVoti > g[i].totaleVoti)
            {
                Giudice temp = g[i];
                g[i] = g[j];
                g[j] = temp;
            }
        }
    }

    static Output result;
    for (int i = 0; i < NUMFILE; i++)
    {
        result.giudici[i] = g[i];
    }
    return (&result);

} // classifica

int *esprimi_voto_1_svc(Voto *voto, struct svc_req *rqstp) {
    if (inizializzato == 0) { //check dello stato del server
        inizializza();
    }
    static int result = 1;
    printf("Ricevuta richiesta di modifica voto per il candidato %s\n", voto->candidato);
    for (int i = 0; i < NUMFILE; i++)
    {
        if (!strcmp(voto->candidato, tabellone.riga[i].candidato))
        {
            if (voto->voto == '+')  {       //incremento voto
                tabellone.riga[i].voto += 1;
                printf("Voto assegnato\n");
            }
            else if (voto->voto == '-' && tabellone.riga[i].voto != 0) { //decremento voto
                tabellone.riga[i].voto--;
                printf("Voto assegnato\n");
            }
            else    {
                printf("Impossibile dare un voto\n"); 
                result = -1;
            }
        }
    }

    return (&result);
    
} // vota

Tabellone *mostra_tabellone_1_svc(void *in, struct svc_req *rqstp) { //restituisco il tabellone
    if (inizializzato == 0) {
        inizializza();
    }
    return (&tabellone);
} // visualizza
