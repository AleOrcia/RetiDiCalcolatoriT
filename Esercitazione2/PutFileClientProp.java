// PutFileClient.java

import java.net.*;
import java.io.*;
import java.nio.*;

public class PutFileClientProp {

    public static void main(String[] args) throws IOException {

        InetAddress addr = null;
        int port = -1;

        try { // check args
            if (args.length == 2) {
                addr = InetAddress.getByName(args[0]);
                port = Integer.parseInt(args[1]);
            } else {
                System.out.println("Usage: java PutFileClient serverAddr serverPort");
                System.exit(1);
            }
        } // try
          // Per esercizio si possono dividere le diverse eccezioni
        catch (Exception e) {
            System.out.println("Problemi, i seguenti: ");
            e.printStackTrace();
            System.out.println("Usage: java PutFileClient serverAddr serverPort");
            System.exit(2);
        }

        // oggetti utilizzati dal client per la comunicazione e la lettura del file
        // locale
        Socket socket = null;
        DataOutputStream outSock = null;
        DataInputStream inSock = null;
        String directory = null;
        String comando = null;
        File dir = null;
        File tmpFile = null;
        String[] files = null;
        DataInputStream str = null;

        // creazione stream di input da tastiera
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("PutFileClient Started.\n\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome direttorio: ");

            try {
                socket = new Socket(addr, port);
                socket.setSoTimeout(30000);
                System.out.println("Creata la socket: " + socket);
            } catch (Exception e) {
                System.out.println("Problemi nella creazione della socket: ");
                e.printStackTrace();
                System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome direttorio: ");
                System.exit(1);
                //continue;
                // il client continua l'esecuzione riprendendo dall'inizio del ciclo
            }

        try {
                directory = stdIn.readLine();
                dir = new File(directory);
                if (dir.exists()) {
                    // creazione socket
                 

                    // creazione stream di input/output su socket
                    try {
                        inSock = new DataInputStream(socket.getInputStream());
                        outSock = new DataOutputStream(socket.getOutputStream());
                    } catch (IOException e) {
                        System.out.println("Problemi nella creazione degli stream su socket: ");
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                // se la richiesta non � corretta non proseguo
                else {
                    System.out.println("File non presente nel direttorio corrente");
                    System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome direttorio: ");
                    // il client continua l'esecuzione riprendendo dall'inizio del ciclo
                    System.exit(1);
                }

                files = dir.list();

                for (String x : files) {
                    tmpFile = new File(x);
                    System.out.println("NOME DEL FILE TRASMESSO " + x + "\nLA DIMENSIONE " + tmpFile.length());

                    if (!tmpFile.isDirectory()) {
                        //SEND

                        /* Invio file richiesto e attesa esito dal server */
                        // creazione stream di input da file
                        try {
                            str = new DataInputStream(new FileInputStream(x));
                        }
                        /*
                         * abbiamo gia' verificato che esiste, a meno di inconvenienti, es.
                         * cancellazione concorrente del file da parte di un altro processo, non
                         * dovremmo mai incorrere in questa eccezione.
                         */
                        catch (FileNotFoundException e) {
                            System.out.println("Problemi nella creazione dello stream di input da "
                                    + x + ": ");
                            e.printStackTrace();
                            System.out.println("Procedo con il prossimo file");
                            // il client continua l'esecuzione riprendendo dall'inizio del ciclo
                            continue;
                        }

                        // trasmissione del nome
                        try {

                            outSock.writeUTF(x);
                            System.out.println("Inviato il nome del file " + x);
                        } catch (Exception e) {
                            System.out.println("Problemi nell'invio del nome di " + x
                                    + ": ");
                            e.printStackTrace();
                            System.out
                                    .println("Procedo con il prossimo file");
                            // il client continua l'esecuzione riprendendo dall'inizio del ciclo
                            continue;
                        }
                        try {
                            comando = inSock.readUTF();
                            System.out.println("Esito trasmissione: " + comando);

                        } catch (Exception e) {
                            System.out.println("Problemi con il ricevimento del comando da parte del server");
                            System.out.println("Procedo con il prossimo file");
                            continue;
                        }
                        if (comando.equalsIgnoreCase("attiva")) {
                            System.out.println("Inizio la trasmissione della dimensione di " + x.toString());

                            try {
                                outSock.writeLong(tmpFile.length());

                            } catch (Exception e) {
                                System.out.println("Problemi con la scrittura della dimensione file");
                                System.out.println("Procedo con il prossimo file");
                                continue;
                            }

                            System.out.println("Inizio la trasmissione di " + x);

                            // trasferimento file
                            try {
                                // FileUtility.trasferisci_a_linee_UTF_e_stampa_a_video(new
                                // DataInputStream(inFile), outSock);
                                FileUtility.trasferisci_a_byte_file_binario(str, outSock);
                                // chiusura file
                                //socket.shutdownOutput(); // chiusura socket in upstream, invio l'EOF al server
                                System.out.println("Trasmissione di " + x + " terminata ");
                            } catch (SocketTimeoutException ste) {
                                System.out.println("Timeout scattato: ");
                                ste.printStackTrace();
                                socket.close();

                                System.out.print("\nImmetti nome nuovo direttorio");
                                // il client continua l'esecuzione riprendendo dall'inizio del ciclo
                                break;
                            } catch (Exception e) {
                                System.out.println("Problemi nell'invio di " + x + ": ");
                                e.printStackTrace();
                                socket.close();

                                System.out
                                        .print("\nImmetti nome nuovo direttorio ");
                                // il client continua l'esecuzione riprendendo dall'inizio del ciclo
                                break;
                            }

                        } else if (comando.equalsIgnoreCase("salta")) {
                            System.out.println("Salto al prossimo file");

                            continue;

                        } else {
                            System.out.println("COMANDO ERRATO!!!");

                            socket.close();
                            System.exit(1);
                        }

                    }

                }
            
            socket.close();
            System.out.println("PutFileClient: termino...");
        }
        

        

        catch (Exception e) {
            System.err.println("Errore irreversibile, il seguente: ");
            e.printStackTrace();
            System.err.println("Chiudo!");
            System.exit(3);
        }
    } // main
} // PutFileClient