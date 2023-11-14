import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemOp extends Remote {
    int elimina_riga(String nomeFile, int riga) throws RemoteException;
    int conta_righe(String nomeFile, int soglia) throws RemoteException;
}