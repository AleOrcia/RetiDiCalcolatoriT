
/**
 * ServerCongressoImpl.java
 * 		Implementazione del server
 * */

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;


public class RMI_Server extends UnicastRemoteObject implements
RMI_interfaceFile {

  private static int MAX = 100;

  // Costruttore
  public RMI_Server() throws RemoteException {
    super();
  }

  // Richiede una prenotazione
  public String[] lista_nomi_file_contenenti_parola_in_linea(String dir, String word) throws RemoteException{
    String[] res = new String[MAX];
    File directory = new File(dir);
    if(!directory.isDirectory()){
      System.err.println("La directory richiesta non è corretta");
      throw new RemoteException();
    }

    File[] files = directory.listFiles();
    BufferedReader br = null;
    String line = null;
    int i = 0;
    boolean check = false;

    for(File f : files){
      System.out.println(f.getName());
      try{
        if(f.getName().endsWith(".txt")){
          br = new BufferedReader(new FileReader(f));
          while((line = br.readLine())!= null){
            System.out.println(line);
              if(line.contains(word)){
                check = true;
            }
          }
          if(check){
            res[i]= f.getName();
            i++;
          }
          
            
        }else{
          continue;
        }
        

      }catch(IOException e){
        try {
          br.close();
          
        } catch (IOException e1) {
          throw new RemoteException();
        }
        throw new RemoteException();
      }
    }
    
    try {
      br.close();
    } catch (IOException e) {
      throw new RemoteException();
    }

    return res;
  } 
    
  public int conta_numero_linee(String file, String word) throws RemoteException{
    int res = -1;
    File f = new File(file);
    BufferedReader br = null;
    int i = 0;
    String line = null;
    boolean check = false;
    try {
      br = new BufferedReader(new FileReader(f));
    } catch (FileNotFoundException e) {
      throw new RemoteException();
    }

    if(f.exists() && f.getName().endsWith(".txt")){
      try {
        while((line=br.readLine())!= null){
          i++;
          if(line.contains(word)) check = true;
        }
      } catch (IOException e) {
        throw new RemoteException();
      }
    }


    try {
      br.close();
    } catch (IOException e) {
      throw new RemoteException();
    }
    
    if(check){
      return i;
    }
    return res;
  }

  // Avvio del Server RMI
  public static void main(String[] args) {

    // creazione programma
    
    int registryRemotoPort = 1099;
    String registryRemotoName = "RegistroRemoto";
    String serviceName = "Servitore";

    // Controllo dei parametri della riga di comando
    if (args.length != 1 && args.length != 2) {
      System.out
          .println("Sintassi: RMI_Server NomeHostRegistryRemoto [registryPort], registryPort intero");
      System.exit(1);
    }
    String registryRemotoHost = args[0];
    if (args.length == 2) {
      try {
        registryRemotoPort = Integer.parseInt(args[1]);
      } catch (Exception e) {
        System.out
            .println("Sintassi: RMI_Server NomeHostRegistryRemoto [registryPort], registryPort intero");
        System.exit(2);
      }
    }

    // Impostazione del SecurityManager
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    // Registrazione del servizio RMI
    String completeRemoteRegistryName = "//" + registryRemotoHost + ":"
        + registryRemotoPort + "/" + registryRemotoName;

    try {
      RegistryRemotoServer registryRemoto = (RegistryRemotoServer) Naming
          .lookup(completeRemoteRegistryName);
      RMI_Server serverRMI = new RMI_Server();
      registryRemoto.aggiungi(serviceName, serverRMI);
      System.out.println("Server RMI: Servizio \"" + serviceName
          + "\" registrato");
    } catch (Exception e) {
      System.err.println("Server RMI \"" + serviceName + "\": "
          + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }
}