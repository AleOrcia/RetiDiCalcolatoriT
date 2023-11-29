#include "scan.h"
#include <rpc/rpc.h>
#include <stdio.h>
#include <dirent.h>
#include <sys/stat.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>

File_scan* file_scan_1_svc(char **nomefile, struct svc_req *rp){
    static File_scan ris;
    static char *nome;
    int lines = 0, chars = 0, words = 0;
    int fd, nread;
    char c;
    if ((fd = open(*nomefile, O_RDONLY, 00640)) < 0)
    {
        fprintf(stderr, "%s", "Impossibile aprire il file\n");
        exit(1);
    }

    while ((nread = read(fd, &c, sizeof(char))) > 0)
    {
        //printf("%c", c);
        if (c == '\n')
        {
            lines++;
            words++;
        }
        if (c == ' ' || c == '\t')
        {
            words++;
        }
        else    chars++;
    }

    close(fd);
    if (lines == 0 && chars == 0 && words == 0)
    {
        printf("File vuoto :(\n");
        exit(1);
    }
    
    ris.chars = chars;
    ris.words = words;
    ris.lines = lines;

    printf("Lettura avvenuta correttamente: %d, %d, %d\n", chars, words, lines);
    
    return (&ris);
}

Dir_scan* dir_scan_1_svc(Scan *req, struct svc_req *rp){
    char full_dir[300];
    static Dir_scan ris;
    struct Dir fileName;
    int numfile = 0, size;
    DIR *dir;
    struct dirent *dd;
    struct stat sstr;
    dir = opendir(req->nomeDir);
    while ((dd = readdir(dir)) != NULL)
    {
        if (strcmp(dd->d_name, ".") && strcmp(dd->d_name, ".."))
        {
            strcpy(full_dir,req->nomeDir);
            strcat(full_dir,"/");
            strcat(full_dir,dd->d_name);
            //printf("%s \n",full_dir);
            //printf("%s \n", dd->d_name);
            stat(full_dir, &sstr);
            //printf("%ld\n", sstr.st_size);
            size = sstr.st_size;
            if (size > req->soglia)
            {
                strcpy(fileName.Directories, dd->d_name);
                strcpy(ris.Directories[numfile].Directories, fileName.Directories);
                numfile++;
            }
        }
      
    }
    printf("Lettura avvenuta, numero file(s): %d\n", numfile);
    closedir(dir);
    ris.numFile = numfile;
    return (&ris);
}