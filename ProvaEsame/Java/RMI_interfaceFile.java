
/**
 * Interfaccia remota di servizio
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI_interfaceFile extends Remote {
	String[] lista_nomi_file_contenenti_parola_in_linea(String dir, String word) throws RemoteException;

	int conta_numero_linee(String file, String word) throws RemoteException;
}