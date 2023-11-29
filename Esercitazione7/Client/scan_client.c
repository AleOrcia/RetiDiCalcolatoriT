/* scan_client.c
 *	+include scan.h
 */

#include "scan.h"
#include <rpc/rpc.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MAX_LEN 255

int main(int argc, char *argv[]) {

    CLIENT *cl;
    File_scan  *result_file; // struttura tre interi
    Dir_scan *result_dir;
    Scan struttura_dir;
    char   *server;
    char   command[10];
    char   *fileName;
    char   soglia[10];

    if (argc < 2) {
        fprintf(stderr, "uso: %s host\n", argv[0]);
        exit(1);
    }

    server = argv[1];

    cl = clnt_create(server, SCANPROG, SCANVERS, "udp");
    if (cl == NULL) {
        clnt_pcreateerror(server);
        exit(1);
    }

    /* CORPO DEL CLIENT:
    /* ciclo di accettazione di richieste da utente ------- */
    fileName = (char *)malloc(MAX_LEN+1);
    printf("Scegli tra le due procedure disponibili.\nfile_scan\tdir_scan\n");

    while (gets(command)) {
   
        if(strcmp(command,"file_scan")==0){
            printf("Inserisci il nome del file che vuoi scansionare\n");
            gets(fileName);

            result_file = file_scan_1(&fileName, cl);

            if (result_file == NULL) {
                fprintf(stderr, "%s: %s fallisce la rpc\n", argv[0], server);
                clnt_perror(cl, server);
                exit(1);
            }
            if (result_file->chars == -1){
                fprintf(stderr, "%s: errore con il numero dei caratteri\n", argv[0]);
                
            }
            if (result_file->words == -1){
                fprintf(stderr, "%s: errore con il numero delle parole\n", argv[0]);
               
            }
            if (result_file->lines == -1){
                fprintf(stderr, "%s: errore con il numero delle righe\n", argv[0]);
                
            }


            //stampa
            printf("Il risultato della chiamata per il file %s è:\nCaratteri: %d\nParole: %d\nRighe:%d\n", fileName, result_file->chars, result_file->words, result_file->lines);


        }else if(strcmp(command,"dir_scan")==0){

            printf("Inserisci il nome della directory che vuoi scansionare\n");
            gets(fileName);
            printf("Inserisci la soglia\n");
            gets(soglia);

            strcpy(struttura_dir.nomeDir, fileName);
            struttura_dir.soglia = atoi(soglia);

            result_dir = dir_scan_1(&struttura_dir, cl);
            if (result_dir == NULL) {
                fprintf(stderr, "%s: %s fallisce la rpc\n", argv[0], server);
                clnt_perror(cl, server);
                exit(1);
            }

            if (result_dir->numFile == -1){
                fprintf(stderr, "%s: errore con il numero dei caratteri\n", argv[0]);
                
            }
            

            //stampa
            printf("Il risultato della chiamata per la directory %s è:\nNumero Files: %d\n", fileName, result_dir->numFile);
            printf("\nI nomi delle directory sono:\n");

            for(int i = 0; i < result_dir->numFile; i++){
                printf("%s\n", result_dir->Directories[i].Directories);
            }


        }else{
            continue;
        }
            printf("\n\nScegli tra le due procedure disponibili.\nfile_scan\tdir_scan\n");

    } // while gets(msg)

    // Libero le risorse: memoria allocata con malloc e gestore di trasporto
    free(fileName);
    clnt_destroy(cl);
    printf("Termino...\n");
    exit(0);
}
