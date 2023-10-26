// MultiplePutFileServer Sequenziale

import java.io.*;
import java.net.*;

public class MultiplePutFileServerSeq{
    public static final int PORT = 54321; // porta default per server

	public static void main(String[] args) throws IOException {
        // Porta sulla quale ascolta il server
		int port = -1, soglia = -1; long lengthFile = -1;

        /* controllo argomenti */
		try {
			if (args.length == 2) {
				port = Integer.parseInt(args[0]);
                soglia = Integer.parseInt(args[1]);
				// controllo che la porta sia nel range consentito 1024-65535
				if (port < 1024 || port > 65535) {
					System.out.println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
					System.exit(1);
				}
			} else if (args.length == 1) {
				port = PORT;
                soglia = Integer.parseInt(args[0]);
			} else {
				System.out
					.println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
				System.exit(1);
			}
		} //try
		catch (Exception e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.out
				.println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
			System.exit(1);
		}

        /* preparazione socket e in/out stream */
		ServerSocket serverSocket = null;
		try {
//			serverSocket = new ServerSocket(port,2);
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
			System.out.println("PutFileServerSeq: avviato ");
			System.out.println("Creata la server socket: " + serverSocket);
		}
		catch (Exception e) {
			System.err.println("Problemi nella creazione della server socket: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(2);
		}
        try {
			//ciclo infinito server
			while (true) {
				Socket clientSocket = null;
				DataInputStream inSock = null;
				DataOutputStream outSock = null;
				
				System.out.println("\nIn attesa di richieste...");
				try {
					clientSocket = serverSocket.accept();
					clientSocket.setSoTimeout(30000); //timeout altrimenti server sequenziale si sospende
					System.out.println("Connessione accettata: " + clientSocket + "\n");
				}
				catch (SocketTimeoutException te) {
					System.err
						.println("Non ho ricevuto nulla dal client per 30 sec., interrompo "
								+ "la comunicazione e accetto nuove richieste.");
					// il server continua a fornire il servizio ricominciando dall'inizio
					continue;
				}
				catch (Exception e) {
					System.err.println("Problemi nella accettazione della connessione: "
							+ e.getMessage());
					e.printStackTrace();
					// il server continua a fornire il servizio ricominciando dall'inizio
					// del ciclo, se ci sono stati problemi
					continue;
				}

				try{
					while(true){
						//stream I/O e ricezione nome file
						FileOutputStream outFile = null;
						String nomeFile;
						lengthFile = -1;
						try {
								inSock = new DataInputStream(clientSocket.getInputStream());
								outSock = new DataOutputStream(clientSocket.getOutputStream());
								nomeFile = inSock.readUTF();
								System.out.println("NOME FILEEEEEEEEEEEEEEEE "+nomeFile);
						}
						catch(SocketTimeoutException ste){
								System.out.println("Timeout scattato: ");
								ste.printStackTrace();
								clientSocket.close();
								System.out
									.print("\n^D(Unix)/^Z(Win)+invio per uscire, solo invio per continuare: ");
								// il client continua l'esecuzione riprendendo dall'inizio del ciclo
								continue;          
						}
						catch (IOException e) {
								System.out
									.println("Problemi nella creazione degli stream di input/output "
										+ "su socket: ");
								e.printStackTrace();
								// il server continua l'esecuzione riprendendo dall'inizio del ciclo
								continue;
						}
		
						String esito;
						if (nomeFile == null) {
							System.out.println("Problemi nella ricezione del nome del file: ");
							clientSocket.close();
							continue;
						}else{
							File curFile = new File(nomeFile);
							// controllo su file
							if (curFile.exists()) {
								esito = "salta";
							}
							else{
								esito = "attiva";
								outFile = new FileOutputStream(nomeFile);
							}
						}
		
						try{
							System.out.println("ESITOOOOOOOOOOOOOOOOOOO "+esito);

							outSock.writeUTF(esito);
							System.out.println("Inviato il nome dell'esito");
						}
						catch(Exception e){
								System.out.println("Problemi nell'invio del nome dell'esito");
								e.printStackTrace();
								System.out
									.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
								// il client continua l'esecuzione riprendendo dall'inizio del ciclo
								continue;
						}
		
						try{
							if(esito.equals("attiva")){
								lengthFile = inSock.readLong();
								if (lengthFile > soglia){
								int i = 0;
									while(i<lengthFile){
										outFile.write(inSock.read());
										i++;
									}
		
									outFile.close();
								}
							}
							
							System.out.println("LUNGHEZZA FILEEEEEEEEEEEE "+lengthFile);
		
							
						}
						catch (Exception e) {
							System.err.println("Problemi nella accettazione della connessione: "
									+ e.getMessage());
							e.printStackTrace();
							// il server continua a fornire il servizio ricominciando dall'inizio
							// del ciclo, se ci sono stati problemi
							continue;
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					// chiusura di stream e socket
					System.out.println("Errore irreversibile, PutFileServerSeq: termino...");
					System.exit(3);
				}
            }
        }
        catch (Exception e) {
			e.printStackTrace();
			// chiusura di stream e socket
			System.out.println("Errore irreversibile, PutFileServerSeq: termino...");
			System.exit(3);
		}
    }
}
