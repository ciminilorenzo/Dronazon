# Distributed and pervasive systems project

DA FARE: 

    - TESTARE SINCRONIZZAZIONE IN FASE DI INSERIMENTO NELLA SMARTCITY 





MOMENTI IN CUI VIENE CONTROLLATA PRESENZA DI SPEDIZIONI NON ANCORA GESTITE:

    - Quando un drone normale finisce la propria consegna (quando il master riceve dati sulla spedizione controlla)
    - Quando il drone master finisce la propria consegna
    - Quando il drone master gestisce ingresso nuovo drone (nel modulo di comunicazione in risposta al GreetingMessage)


COSE DA FARE:
    - RIMOZIONE DI UN DRONE
    - ELEZIONE NUOVO MASTER
    - DEVE ESSERCI UN TIMEOUT AD OGNI CHIAMATA gPRC DAL MOMENTO CHE I DRONI POSSONO USCIRE DALLA RETE SENZA COMUNICARE NULLA


MOMENTI IN CUI UN DRONE PUO' ACCORGERSI CHE UN DRONE E' CADUTO:
    - Momento in cui si fa il greeting iniziale (CommunicationThread)
    - Momento in cui si chiede di fare una spedizione (CommunicationModule)



ESAME:
    - Chi ha superato la prova scritta a Maggio non deve iscriversi all'appello ma solo consegnare il progetto in quella settimana e poi presentarsi il giorno dell'appello
