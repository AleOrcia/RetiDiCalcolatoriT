/* FattoreX.c
 *
 */
#include "FattoreX.h"
#include <rpc/rpc.h>
#include <stdio.h>
#include <string.h>

#define LUNGHFILA 7
#define NUMFILE   10

int main(int argc, char *argv[]) {
    char   *host;
    CLIENT *cl;
    int    *ris;
    void   *in;
    Tabellone   *tabellone;
    Input   input;

    Output *output;
    Voto v;
    char    ok[29], voto;

    if (argc != 2) {
        printf("usage: %s server_host\n", argv[0]);
        exit(1);
    }
    host = argv[1];

    cl = clnt_create(host, FATTOREX, FATTOREXVERS, "udp");
    if (cl == NULL) {
        clnt_pcreateerror(host);
        exit(1);
    }

    printf("Inserire:\nC) per vedere la classifica\tV) per votare\tM) mostra tabellone\t^D per terminare: ");

    while (gets(ok)) {

        if (strcmp(ok, "V") == 0) {//if V
            printf("Inserisci nome candidato \n");
            gets(ok); 
            strcpy(v.candidato,ok);

            // Chiedo il voto

            voto = '0';
            while (voto != '+' && voto != '-') {
                printf("Voto positivo (+) o voto negativo (-)? \n");
                voto = getchar();
            }
            v.voto = voto;
            printf("VOTO UTENTE %c \t VOTO COPIATO %c", voto, v.voto);
            // Invocazione remota
            ris = esprimi_voto_1(&v, cl);
            if (ris == NULL) {
                clnt_perror(cl, host);
                exit(1);
            }
            if (*ris < 0) {
                printf("Votazione fallita\n");

            } else {
                printf("Votazione eseguita con successo\n");
            }
         // if C
        }else if (strcmp(ok, "C") == 0){
        
            output = classifica_giudici_1(in, cl); //chiedo la classifica
            if (output == NULL) {
                clnt_perror(cl, host);
                exit(1);
            }
            printf("Stato di occupazione della sala:\n");
            for (int i = 0; i < NUMFILE; i++) {
                
                printf("%s", output->giudici[i].giudice);
                printf("\n");
            }
         // if M
        }else if (strcmp(ok, "M") == 0){
            tabellone = mostra_tabellone_1(in,cl); //chiedo il tabellone completo

            if (tabellone == NULL) {
                clnt_perror(cl, host);
                exit(1);
            }

            for(int i = 0; i < NUMFILE; i++){ //stampa del tabellone a terminale
                printf("Candidato: %s\t\tGiudice: %s\t\tCategoria: %c\t\tFase: %c\t\tVoto: %d\t\tNome File: %s\n\n", 
                tabellone->riga[i].candidato, tabellone->riga[i].giudice, tabellone->riga[i].categoria,
                tabellone->riga[i].fase,tabellone->riga[i].voto,tabellone->riga[i].nomeFile);
            }

        }else{
        
            printf("Argomento di ingresso errato!!\n");
        }
        printf("Inserire:\nC) per vedere la classifica\tV) per votare\tM) mostra tabellone\t^D per terminare: ");
    } // while

    // Libero le risorse, distruggendo il gestore di trasporto
    clnt_destroy(cl);
    exit(0);
}
