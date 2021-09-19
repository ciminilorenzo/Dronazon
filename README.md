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

CARATTERISTICHE:
    - Se sta consegnando nel momento in cui arriva il messaggio di elezione allora sarà considerata batteria attuale - 10%
    - Viene fatto passare nell'anello un messaggio di elezione contenente la lista dei droni presenti nella rete in modo tale che il nuovo master sappia quali sono i droni presenti e le loro informazioni



**CASI GESTITI:**
*Caso in cui il drone è da solo dentro la smartcity durante l'elezione*:
    Se il metodo smartcity.next() restituisce false allora significa che non c'è nessun'altro nella smartcity. Di conseguenza
    l'elezione termina e il drone diventa master.
    
*Caso in cui il prossimo drone nella rete è caduto*
    I droni sono in grado di gestirsi autononamente in questa situazione

*Caso in cui cade un drone durante un elezione*
    Il metodo smartcity.next() restituirà null per quel dato drone e, a seguito della presenza di un while(true) nel metodo
    forward() la chiamata verrà tentata nuovamente con il nuovo drone successivo.
    Tutto questo finchè il metodo smartcity.next() restituirà false dal momento che in cui caso il metodo forward() restituirà
    a sua volta false in modo tale da settare il drone corrente come master visto che è da solo all'interno della smartcity.

*Nuovo drone entra durante l'elezione:*
    I droni quando ricevono un messaggio di greeting controllano il valore del flag relativo all'attuale partecipazione 
    ad una elezione. Nel caso il flag fosse true allora il thread si mette in attesa fino al momento in cui il flag viene 
    settato a false

*Drone vincitore cade durante l'elezione*
    In questo caso se il drone ha inviato ALMENO un messaggio di elected non c'è problema dal momento che il drone successivo
    inoltrerà il messaggio nell'anello e, di conseguenza, i PingModule verranno inizializzati con la porta del master drone appena
    caduto. Questo genererà ovviamente dopo 10 secondi da questo momento un eccezione che farà capire ai droni che il drone master è caduto
    e, di conseguenza, una nuova lezione ricomincerà.

    Per evitare la situazione nelle quale il vincitore non sia riuscito ad inviare nell'anello ALMENO un messaggio di ELECTED, è stato inserito
    un flag che non permette l'uscita (attraverso classico meccanismo con wait() e notify()) dell'uscita dell'attuale drone master fino al momento in cui
    non ha inviato ALMENO un messaggio.

*Drone attualmente contenuto nel messaggio di elezione come master cade*
    Per lo stesso flag appena descritto, un drone attualmente reputato come master non può uscire finchè:
    - Ha inviato almeno un messaggio di ELECTED nell'anello
    - Perde il primato di importanza



RICARICA:
    - CARATTERISTICHE: 
            1. Quando un drone ha iniziato il processo di ricarica non può uscire
            2. Quando si trova in una situazione di concorrenza controlla la richiesta ricevuta e in base al timestamp()
                    Se timestamp viene prima allora ha priorità e mette in wait la risposta
                    Se timestamp viene dopo da il consenso e verrà salvato nella coda del ricevente
                    Se timestamp è uguale:
                        Se id maggiore allora ha precedenza
                        Se id minore da il consenso
            3. Se non si trova in una situazione di concorrenza da il permesso
            4. Se si è trovato in una situazione concorrenziale, alla fine della ricarica, farà un broadcast parallelo dei permessi ai vari droni


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




Unico consiglio: non preparate schemi sul progetto in generale, tenetevi appuntato quali casi limite trovate o studiate e fate uno schema su come si generano e su come li avete risolti. Risparmiate tempo a loro ed è più chiaro a voi da spiegare (e ricordare)