# Distributed and pervasive systems project

**KEEP IN MIND:**
1. CERCARE DI REALIZZARE UNA SINCRONIZZAZIONE A GRANA PIU' FINE POSSIBILE IN MODO TALE DA NON BLOCCARE IL SERVER.
        PER ESEMPIO, MENTRE PER OPERAZIONI DI INGRESSO/USCITA DAL SERVER OCCORRE SINCRONIZZARE L'INTERA ISTANZA DELLA SMARTCITY
        PER TUTTA LA DURATA DELL'OPERAZIONE, NEL CASO DEL CALCOLO DELLE STATISTICHE DOBBIAMO SOLO SINCRONIZZARE LA LETTURA DELL'ARRAY DELLE STATISTICHE
2. Attenti che se il drone si presenta al master ma non ha ancora il server grpc attivo, se il master prova ad assegnare una consegna fallisce e lo elimina, ma in realtà sarebbe appena entrato
   Controllate con sleep varie
   --> Questo dovrebbe essere gestito dal momento che prima il drone starta il server grpc e, solo dopo, comincia il processo di chattingp per comunicare la sua presenza.


**MOMENTI IN CUI VIENE CONTROLLATA PRESENZA DI SPEDIZIONI NON ANCORA GESTITE:**
1. Quando un drone normale finisce la propria consegna (quando il master riceve dati sulla spedizione controlla)
2. Quando il drone master finisce la propria consegna 
3. Quando il drone master gestisce ingresso nuovo drone (nel modulo di comunicazione in risposta al GreetingMessage)




**MOMENTI IN CUI UN MASTER PUO' ACCORGERSI CHE UN DRONE (NON MASTER) È CADUTO:**
1.  Quando gli assegna una spedizione e non risponde -> GESTITO 


    Assunzioni: 
        1. Non è valido il caso in cui il master si accorge che un drone è caduto nel momento in cui non riceve statistiche di consegna dopo un determinato time-out
            visto che è un assunzione del progetto il fatto che anche se vuole uscire un drone deve prima terminare la consegna, inviare le statistiche e, solo dopo, 
            uscire dalla smartcity.



**MOMENTI IN CUI UN NODO PUO' ACCORGERSI CHE IL MASTER È CADUTO:**
1.  Quando viene fatto il ping e non risponde.


    Assunzioni:
        1.  Non dovrebbe essere un occasione per accorgersi che il master è caduto il caso in cui un drone invia le statistiche di fine spedizione al master e lui non risponde. 
            Questo perché il master, una volta che ha assegnato l'ultima spedizione, ha un timeout di 5010 millisecondi (tempo utile alla fine della consegna e a ricevere le informazioni
            relative).  
            Questo time-out potrebbe essere non valido nel momento in cui si considera che in condizioni reali un tempo di 0010 millisecondi potrebbe essere non sufficiente per ricevere le statistiche.


**MOMENTI IN CUI UN DRONE SI ACCORGE CHE UN ALTRO DRONE E' CADUTO:**
1. Quando, durante un elezione, il next() non risponde
2. Momento in cui si fa il greeting iniziale (CommunicationThread) -> *GESTITO IL FATTO CHE IL DRONE DEVE ESSERE RIMOSSO DAL RING CORRENTE*



ELEZIONE:
1. Parte quando, durante il ping(), il master non risponde.
2. Potremmo far passare un messaggio contenente la lista dei droni presenti nella rete in modo tale che il nuovo master sappia quali sono i droni presenti e le loro informazioni 
3. Nella situazione in cui un drone si accorge che il master è caduto deve interrompere il ping. Allo stesso modo quando lo setta nuovamente deve farlo partire -> *GESTITO*

CASI GESTITI:
1. Caso in cui il drone è da solo dentro la smartcity durante l'elezione -> *GESTITO*
2. Caso in cui il prossimo drone nella rete è caduto




**_TEST DI USCITA DI UN DRONE:_**
    **MASTER**
   1. USCITA PER BATTERIA SCARICA
       1. CONDIZIONI NORMALI -> VERIFICATO
       2. DUE DRONI -> VERIFICATO
       3. PIU' DRONI -> VERIFICATO
       4. QUANDO HA DELLE CONSEGNE PENDENTI DA ASSEGNARE E NON PUO' ESEGUIRLE MA PUO' ASSEGNERARLE -> VERIFICATO
       5. QUANDO HA DELLE CONSEGNE PRENDENTI DA ASSEGNARE E NON PUO' ESEGUIRLE E NON PUO' NEMMENO ASSEGNARLE -> VERIFICATO

   2. USCITA TRAMITE QUIT
      1. CONDIZIONI NORMALI -> VERIFICATO
      2. DUE DRONI -> VERIFICATO
      3. PIU' DRONI -> VERIFICATO
      4. QUANDO E' DA SOLO E HA ANCORA CONSEGNE DA GESTIRE -> VERIFICATO
      5. QUANDO GLI SI CHIAMA UNA QUIT E SI METTE IN ATTESA DI ASSEGNARE TUTTE LE DELIVERY MA E' DA SOLO E FINISCE LA BATTERIA DURANTE QUESTA FASE DI EXIT -> VERIFICATO

   **NODO NORMALE** 
   1. USCITA TRAMITE QUIT
         1. CONDIZIONI NORMALI ----> VERIFICATO SIA CON DELIVERY IN CORSO CHE NON 
         
   2. USCITA BATTERIA SCARICA -> VERIFICATO









ESAME:
- Chi ha superato la prova scritta a Maggio non deve iscriversi all'appello ma solo consegnare il progetto in quella settimana e poi presentarsi il giorno dell'appello
- Ecco come è strutturato l’esame:
  1) Si espone lo schema preparato (non serve nulla di particolare)
  2) Ti chiedono come hai gestito un particolare caso limite
  3) chiedono di lanciare 4 droni, amministratore e dronazon, Killare il master e vedere come viene gestita l’elezione
  4) chiedono di vedere come hai gestito la concorrenza in un determinato punto, per esempio nel server
  5) una o due domande di teoria (es. qos, synchronized, testament)
