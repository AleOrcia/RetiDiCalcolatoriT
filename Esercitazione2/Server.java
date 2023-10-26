/*
  dimensione del file risulti maggiore di una soglia specificata dall’utente
  
   server fornisce “attiva” nel caso un file con quel nome non sia già 
   presente sul file system nel direttorio corrente del server, esito negativo
   altrimenti (ad esempio “salta file”)
   
  Il server salva nel direttorio corrente i file indicati e il protocollo deve evitare 
  che vengano sovrascritti file esistenti che abbiano lo stesso nome
 
  Attesa richiesta di connessione, usa la socket connessa del client per creare uno
  stream di input da cui ricevere o nomi dei file e il contenuto e uno stream di output
  su cui inviare il comando di attivazione trasferimento
 
  SI DEVE UTILIZZARE LA STESSA CONNESSIONE E LA STESSA SOCKET PER IL TRASFERIMENTO DI TUTTI
  I FILE DEL DIRETTORIO (per ogni operazione cliente)
  
  Il processo padre attiva un processo figlio per ogni richiesta accettata
  
  Il server quindi si aspetta, per ogni file, il
  nome e il numero di byte, utilizzando la medesima socket per gestire
  tutti i trasferimenti
  
  chiusura socket=chiusura operazioni
  
 */

import java.io.*;
import java.net.*;

class ServerThread extends Thread{
    private Socket clientSocket;
    private static long MIN=5;

    public ServerThread(Socket clientSocket){
      this.clientSocket=clientSocket;
    }

    public void run(){
      DataInputStream inSock = null;
      DataOutputStream outSock = null;
      //byte[] data = null;
      long lengthFile=0;
      int i=0;
      String nomeFile = null;


      try {
        try {
          // creazione stream di input e out da socket
          System.out.println("DEBUG: SUS1");

          inSock = new DataInputStream(clientSocket.getInputStream());
          System.out.println("DEBUG: SUS2" + inSock.toString());
          outSock = new DataOutputStream(clientSocket.getOutputStream());
          System.out.println("DEBUG: SUS3"+ outSock.toString());
          nomeFile = inSock.readUTF();
          System.out.println("DEBUG: NOMEFILE "+nomeFile);

        }
        catch(SocketTimeoutException ste){
          System.out.println("Timeout scattato: ");
          ste.printStackTrace();
          clientSocket.close();
          System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, solo invio per continuare: ");
          return;          
        }        
        catch (IOException ioe) {

          System.out.println("Problemi nella creazione degli stream di input/output su socket: ");
          ioe.printStackTrace();
          // il server continua l'esecuzione riprendendo dall'inizio del ciclo
          return;
        }
        catch (Exception e) {
          System.out
            .println("Problemi nella creazione degli stream di input/output su socket: ");
          e.printStackTrace();
          return;
        }

        FileOutputStream outFile = null;
        String esito = null;
        // file check
        if (nomeFile == null) {
          System.out.println("Problemi nella ricezione del nome del file: ");
          clientSocket.close();
          return;
        } else {
          File curFile = new File(nomeFile);
          if (curFile.exists()) {
            try {
              esito = "Salta";
            }
            catch (Exception e) {
              System.out.println("Problemi nella notifica di file esistente: ");
              e.printStackTrace();
              return;
            }
          } else{ 
            esito = "Attiva";
            outFile = new FileOutputStream(nomeFile);
          }
        }
        
        //invio esito
        try{
          outSock.writeUTF(esito);
          System.out.println("Esito inviato");
        }catch(IOException e) {
					System.err.println("Problemi nell'invio della risposta: "+ e.getMessage());
					e.printStackTrace();
        }

        if(esito.equalsIgnoreCase("Attiva")){
            try{
            lengthFile=inSock.readLong();
            if(lengthFile<MIN){
              System.out.println("Problemi nella lunghezza del file: ");
              clientSocket.close();
              return;
            }
            System.out.println("Ricevuta lunghezza");
          }catch(IOException e) {
            System.err.println("Problemi nella ricezione di lenghtFile: "+ e.getMessage());
            e.printStackTrace();
            // il server continua a fornire il servizio ricominciando dall'inizio
            // del ciclo
          }

          try{
            while(i<lengthFile){
              i++;
              outFile.write(inSock.read());
            }
            System.out.println("Ricevuto file");


            outFile.close();
            clientSocket.shutdownInput(); //chiusura socket (downstream)
            outSock.writeUTF("File salvato lato server");
            outSock.flush();
            clientSocket.shutdownOutput(); //chiusura socket (dupstream)
            System.out.println("\nTerminata connessione con " + clientSocket);
            clientSocket.close();
          }
          catch (SocketTimeoutException ste){
            System.out.println("Timeout scattato: ");
            ste.printStackTrace();
            clientSocket.close();
            System.out
              .print("\n^D(Unix)/^Z(Win)+invio per uscire, solo invio per continuare: ");
            return;          
          }              
          catch (Exception e) {
            System.err.println("\nProblemi durante la ricezione e scrittura del file: "+ e.getMessage());
            e.printStackTrace();
            clientSocket.close();
            System.out.println("Terminata connessione con " + clientSocket);
            return;
          }
        }
        else{
          clientSocket.close();
          return;
        }
        

      } 
	    // qui catturo le eccezioni non catturate all'interno del while
	    // in seguito alle quali il server termina l'esecuzione
	    catch (Exception e) {
	    	e.printStackTrace();
	    	System.out
	          .println("Errore irreversibile, PutFileServerThread: termino...");
	    	System.exit(3);
	    }
  }
}

public class Server {
    public static final int PORT=1024;

    public static void main(String[] args) throws IOException {
    
      int port = -1;

		/* controllo argomenti */
	    try {
	    	if (args.length == 1) {
	    		port = Integer.parseInt(args[0]);
	    		if (port < 1024 || port > 65535) {
	    			System.out.println("Usage: java LineServer [serverPort>1024]");
	    			System.exit(1);
	    		}
	    	} else if (args.length == 0) {
	    		port = PORT;
	    	} else {
	    		System.out
	    			.println("Usage: java PutFileServerThread or java PutFileServerThread port");
	    		System.exit(1);
	    	}
	    } //try
	    catch (Exception e) {
	    	System.out.println("Problemi, i seguenti: ");
	    	e.printStackTrace();
	    	System.out
	          	.println("Usage: java PutFileServerThread or java PutFileServerThread port");
	    	System.exit(1);
	    }

	    ServerSocket serverSocket = null;
	    Socket clientSocket = null;

	    try {
	    	serverSocket = new ServerSocket(port);
	    	serverSocket.setReuseAddress(true);
	    	System.out.println("PutFileServerCon: avviato ");
	    	System.out.println("Server: creata la server socket: " + serverSocket);
	    }
	    catch (Exception e) {
	    	System.err
	    		.println("Server: problemi nella creazione della server socket: "
	    				+ e.getMessage());
	    	e.printStackTrace();
	    	System.exit(1);
	    }

	    try {

	    	while (true) {
	    		System.out.println("Server: in attesa di richieste...\n");

	    		try {
	    			// bloccante fino ad una pervenuta connessione
	    			clientSocket = serverSocket.accept();
	    			clientSocket.setSoTimeout(30000);
	    			System.out.println("Server: connessione accettata: " + clientSocket);
	    		}
	    		catch (Exception e) {
	    			System.err
	    				.println("Server: problemi nella accettazione della connessione: "
	    						+ e.getMessage());
	    			e.printStackTrace();
	    			continue;
	    		}

	    		// servizio delegato ad un nuovo thread
	    		try {
	    			new ServerThread(clientSocket).start();
	    		}
	    		catch (Exception e) {
	    			System.err.println("Server: problemi nel server thread: "
	    					+ e.getMessage());
	    			e.printStackTrace();
	    			continue;
	    		}

	    	} // while
	    }
	    // qui catturo le eccezioni non catturate all'interno del while
	    // in seguito alle quali il server termina l'esecuzione
	    catch (Exception e) {
	    	e.printStackTrace();
	    	// chiusura di stream e socket
        serverSocket.close();
	    	System.out.println("PutFileServerCon: termino...");
	    	System.exit(2);
	    }
    
    }
}

