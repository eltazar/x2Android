x2Android
=========

TODO LIST:
- Controllo dei crash quando si naviga tra le activity quando ci sono
di mezzo adapter ed oggetti http ( se si toglie un interfaccia prima
di far finire il caricamento ecc )
- immagini nella lista esercenti e nel dettaglio ( nel dettaglio manca
il calcolo della dim Dell immagine in base al display)
- mappa apribile nel dettaglio esercente

- immagini e mappa nel dettaglio esercente: gestire se non carica
l'immagine ( mostrare pulsante refresh o qlc del genere)

- scelta colori layout e decidere lo stile delle righe
- su android 2.x il dialog " scegli dove e quando" ha colori sballatissimi
- in android 4.x il dialog ha le righe esitabili :| 
- mettere le immagini per cell, mail, facebook, filtri ecc


* Clonare il repo
* Dare 'git submodule init'
* Dare 'git submodule update'
* Aprire Eclipse
* File > Import > Existing Android Code into Workspace
 * il progetto da importare si trova in ./external/ActionBarSherlock/library, 
 * **NON** copiare nel workspace
* (Opzionale, ma consigliato) Rinominare il progetto in Eclipse
* Andare nelle proprietà del progetto appena importato:
 * scheda "Java Compiler": Assicurarsi che Compiler Compliance Level sia impostato a 1.6 e che Use Default compliance Settings sia spuntato
 * scheda "Android": Assicurarsi che il progect build target sia all'api level 16
* Dare il rebuild dell'intero workspace (o quantomeno dei due progetti di interesse)
* Ripetere per ogni submodule (dal secondo punto se al repo è stato aggiunto un submodule nuovo, dal quarto se si stanno aggiungendo progressivamente i vari submodule dopo un clone del repo)
