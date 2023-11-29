Esercitazione su RPC SUN.

	Server: espone un metodo per scansionare un file in locale e un metodo per scansionare una cartella in locale

	Client: tramite chiamate remote può controllare files/cartelle del server

ISTRUZIONI PER LA COMPILAZIONE DEI BINARI:

	Per generare i files necessari occorre utilizzare il comando rpcgen sul file con direttive XDL

	rpcgen file.x



	Utilizzare queste direttive per il client (sostituire i nomi dei files)

	gcc -I /usr/include/tirpc file_client.c file_clnt.c file_xdr.c -o file_client -ltirpc




	Utilizzare queste direttive per il server (sostituire i nomi dei files)

	gcc -I /usr/include/tirpc file_server.c file_svc.c file_xdr.c -o file_server -ltirpc
