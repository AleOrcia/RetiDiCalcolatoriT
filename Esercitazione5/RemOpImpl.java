
/**
 * RemOpImpl.java
 * 		Implementazione del server
 * */

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class RemOpImpl extends UnicastRemoteObject implements RemOp {

	// Costruttore
	public RemOpImpl() throws RemoteException {
		super();
	}

	// Conta le righe
	public int conta_righe(String nomeFile, int num) throws RemoteException {
		System.out.println("Server RMI: richiesta conteggio righe con parametri");
        int result = 0;

        
        File fd = new File(nomeFile);
        if(!fd.exists() || !fd.isFile()){
            System.out.println("File non esistente");
            throw new RemoteException();
        }

        
        try{
            BufferedReader reader = new BufferedReader(new FileReader(nomeFile));
            String line = null;

        while((line = reader.readLine()) != null){
            String[] words = line.split(" ");

            if(words.length >= num){
                result++;
                }
            }

        
        }catch(IOException e2){
            throw new RemoteException();
        }
		return result;
	}

    // Elimina una riga
	public int elimina_riga(String nomeFile, int num) throws RemoteException {
		System.out.println("Server RMI: richiesta eliminazione riga con parametri");
        int numRiga = -1;
        File file = new File(nomeFile);
        if (!file.exists()){
            System.out.println("File non esistente");
            throw new RemoteException();
        }
        
        try{
            numRiga = 1;
            FileReader reader = new FileReader(nomeFile);
            BufferedReader buf = new BufferedReader(reader);

            String rigaAtt = null;

            File temp = new File("Temp.txt");
            FileWriter writer = new FileWriter("Temp.txt");
            BufferedWriter bufW = new BufferedWriter(writer);

            while((rigaAtt = buf.readLine()) != null){
                if (numRiga != riga){
                    bufW.write(rigaAtt);
                }
                numRiga++;
            }

            if (numRiga < riga){
                throw new RemoteException();
            }
            
            temp.renameTo(file);
            file.delete();
        }
        catch (IOException e){
            throw new RemoteException();
        }

        return(numRiga -1);
	}


	// Avvio del Server RMI
	public static void main(String[] args) {

		final int REGISTRYPORT = 1099;
		String registryHost = "localhost";
		String serviceName = "ServerProp"; // lookup name...

		// Registrazione del servizio RMI
		String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/" + serviceName;
		try {
			RemOpImpl serverRMI = new RemOpImpl();
			Naming.rebind(completeName, serverRMI);
			System.out.println("Server RMI: Servizio \"" + serviceName + "\" registrato");
		} catch (Exception e) {
			System.err.println("Server RMI \"" + serviceName + "\": " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
