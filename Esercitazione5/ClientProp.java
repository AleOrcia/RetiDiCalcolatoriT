
/**
 * ClientProp.java
 *
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;

class ClientProp {

	public static void main(String[] args) {
		final int REGISTRYPORT = 1099;
		String registryHost = null; // host remoto con registry
		String serviceName = "";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Controllo dei parametri della riga di comando
		if (args.length != 2) {
			System.out.println("Sintassi: RMI_Registry_IP ServiceName");
			System.exit(1);
		}
		registryHost = args[0];
		serviceName = args[1];

		System.out.println("Invio richieste a " + registryHost + " per il servizio di nome " + serviceName);

		// Connessione al servizio RMI remoto
		try {
			String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/" + serviceName;
			RemOp serverRMI = (RemOp) Naming.lookup(completeName);
			System.out.println("ClientRMI: Servizio \"" + serviceName + "\" connesso");

			System.out.println("\nRichieste di servizio fino a fine file");

			String service;
			System.out.print("Servizio (C = Conta righe, E = Elimina riga): ");

			/* ciclo accettazione richieste utente */
			while ((service = stdIn.readLine()) != null) {

				if (service.equals("C")) {
					boolean ok = false; // stato [VALID|INVALID] della richiesta

                    System.out.print("Nome file? ");
					String nomeFile = stdIn.readLine(); // qualsiasi nome accettato, nessun check

					int num = 0;
					System.out.print("Quante parole devono contenere le righe? (>1) ");
					while (ok != true) {
						num = Integer.parseInt(stdIn.readLine());
						if (num < 1) {
							System.out.println("Numero non valido");
							System.out.print("Numero (>1)? ");
							continue;
						} else
							ok = true; // numero inserito valida
					}
					ok = false;
                    int result;

					if ((result = (serverRMI.conta_righe(nomeFile, num))) >= 0){
						System.out.println("Conteggio...");
                        System.out.println("Il numero delle righe che hanno un numero di parole maggiore di "+num+ " è: "+ result);

					}else{
                        System.out.println("Conteggio fallito");

                    }
				} // C=Conteggio righe

				else if (service.equals("E")) {
					System.out.print("Nome file? ");
					String nomeFile = stdIn.readLine(); // qualsiasi nome accettato, nessun check
                    boolean ok = false;

					int num = 0;
					System.out.print("Quale riga vuoi eliminare? (>1)");

					while (ok != true) {
						num = Integer.parseInt(stdIn.readLine());
						if (num < 1) {
							System.out.println("Numero non valido");
							System.out.print("Numero (>1)? ");
							continue;
						} else {
                            ok = true; // numero inserito valida

                        } 
					}
					ok = false;

                    int result;
					
					if((result = (serverRMI.elimina_riga(nomeFile, num))) > 0){
                        System.out.println("Riga numero "+ num + " eliminata!");
                        System.out.println("Righe del file "+ nomeFile + " rimanenti: "+ result);

                    }else{
                        System.out.println("Eliminazione fallita");
                    }
				} // E=Elimina righe

				else
					System.out.println("Servizio non disponibile");

				System.out.print("Servizio (C = Conta righe, E = Elimina riga): ");
			} // while (!EOF), fine richieste utente

		} catch (NotBoundException nbe) {
			System.err.println("ClientRMI: il nome fornito non risulta registrato; " + nbe.getMessage());
			nbe.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			System.err.println("ClientRMI: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}