Esercitazione su RPC SUN.

	Server: espone metodi per visualizzare la classifica di un talent show, visualizzare il tabellone dei punteggi e permette ai clients di votare.
	Inoltre gestisce la struttura dati

	Client: tramite chiamate remote può richiedere di visualizzare la classifica, visualizzare il tabellone completo e votare

ISTRUZIONI PER LA COMPILAZIONE DEI BINARI:

	Per generare i files necessari occorre utilizzare il comando rpcgen sul file con direttive XDL

	rpcgen file.x



	Utilizzare queste direttive per il client (sostituire i nomi dei files)

	gcc -I /usr/include/tirpc file_client.c file_clnt.c file_xdr.c -o file_client -ltirpc




	Utilizzare queste direttive per il server (sostituire i nomi dei files)

	gcc -I /usr/include/tirpc file_server.c file_svc.c file_xdr.c -o file_server -ltirpc

