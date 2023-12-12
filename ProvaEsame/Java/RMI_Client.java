
/**
 * ClientCongresso.java
 *
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

class RMI_Client {
	private static int MAX = 100;

	// Avvio del Client RMI
	public static void main(String[] args) {
		int registryRemotoPort = 1099;
		String registryRemotoHost = null;
		String registryRemotoName = "RegistroRemoto";
		String serviceName = "Servitore";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Controllo dei parametri della riga di comando
		if (args.length != 1 && args.length != 2) {
			System.out.println("Sintassi: RMI_Client NomeHostRegistryRemoto [registryPort], registryPort intero");
			System.exit(1);
		}
		registryRemotoHost = args[0];
		if (args.length == 2) {
			try {
				registryRemotoPort = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out
						.println(
								"Sintassi: ClientCongresso NomeHostRegistryRemoto [registryPort], registryPort intero");
				System.exit(1);
			}
		}

		// Impostazione del SecurityManager
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());

		// Connessione al servizio RMI remoto
		try {
			String completeRemoteRegistryName = "//" + registryRemotoHost + ":"
					+ registryRemotoPort + "/" + registryRemotoName;
			RegistryRemotoClient registryRemoto = (RegistryRemotoClient) Naming.lookup(completeRemoteRegistryName);
			RMI_interfaceFile serverRMI = (RMI_interfaceFile) registryRemoto.cerca(serviceName);
			System.out.println("ClientRMI: Servizio \"" + serviceName + "\" connesso");

			System.out.println("\nRichieste di servizio fino a fine file");

			String service;
			System.out.println("Servizio (L=Lista nomi file, C=Conta numero linee): ");

			while ((service = stdIn.readLine()) != null) {

				if (service.equals("L")) {
					System.out.println("inserisci il nome della directory");
					String directory = stdIn.readLine();

					if(directory == null){
						System.err.println("Errore!");
						System.exit(1);
					}

					System.out.println("inserisci il nome della parola");
					String parola = stdIn.readLine();
					String[] lista = null;

					if(parola == null){
						System.err.println("Errore!");
						System.exit(1);
					}
					System.out.println("I parametri inseriti sono: "+directory+" "+parola);
					try{
						lista = serverRMI.lista_nomi_file_contenenti_parola_in_linea(directory, parola);

					}catch(RemoteException e){
						System.err.println("Elemento restituito dal server nullo!");
						System.out.println("Servizio (L=Lista nomi file, C=Conta numero linee): ");

						continue;

					}

					if(lista == null){
						System.err.println("Elemento restituito dal server nullo!");
						continue;
					}


					if(lista.length == 0){
						System.out.println("Non ci sono files contenenti quella parola");
					}

					for(String s : lista){
						if(s == null) break;
						System.out.println(s);
					}
				} // L

				else if (service.equals("C")) {

					System.out.println("inserisci il nome del file");
					String file = stdIn.readLine();
					int res = -1;

					if(file == null){
						System.err.println("Errore!");
						System.exit(1);
					}

					System.out.println("inserisci il nome della parola");
					String parola = stdIn.readLine();

					if(parola == null){
						System.err.println("Errore!");
						System.exit(1);
					}
					
					res = serverRMI.conta_numero_linee(file, parola);

					if(res != -1){
						System.out.println("Il numero di linee del file richiesto ("+file+") è: "+res);
					}else{
						System.out.println("Il file richiesto ("+file+") non esiste oppure non contiene la parola ("+parola+")");

					}
				} // C

				else
					System.out.println("Servizio non disponibile");

				System.out.println("Servizio (L=Lista nomi file, C=Conta numero linee): ");
			} // !EOF richieste utente

		} catch (Exception e) {
			System.err.println("ClientRMI: " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		}
	}
}