/* 
 * FattoreX.x
 *	+ definizione Input e struttura del Fattore.
 * 	+ definizione metodi e tipi richiesti/restituiti
 */

const NUMFILE=10;
const DIM=30;

struct Input{
    char candidato[DIM];
    char giudice[DIM];
    char categoria;
    char nomeFile[DIM];
    char fase;
    int voto;
};

struct Tabellone{
	Input riga[NUMFILE];
};

struct Voto{
   char candidato[DIM];
   char voto;
};

struct Giudice{
   char giudice[DIM];
   int totaleVoti;
};

struct Output{
   Giudice giudici[NUMFILE];
};

program FATTOREX {
	version FATTOREXVERS{
		Output CLASSIFICA_GIUDICI(void) = 1;
      int ESPRIMI_VOTO(Voto) = 2;
      Tabellone MOSTRA_TABELLONE(void) = 3;
	} = 1;
} = 0x20000013;