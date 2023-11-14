Esercitazione sull'uso della Java Socket

Client(Filtro): chiede all’utente il nome del direttorio (assoluto o relativo al direttorio corrente dove viene lanciato il cliente), si connette al
server con una connessione TCP (java.net.Socket), usandola per tutto il direttorio: i due versi dello stream sono usati, in output per inviare tutte le
informazioni (nome, contenuto e dimensione), e in input per ricevere il comando di attivazione del trasferimento per ciascun file

Server: fornisce “attiva” nel caso un file con quel nome non sia già presente sul file system nel direttorio corrente del server, esito negativo altrimenti (ad esempio “salta file”). 
Il server salva nel direttorio corrente i file indicati e il protocollo deve evitare che vengano sovrascritti
file esistenti che abbiano lo stesso nome
