x2Android
=========

TODO LIST:
- Controllo dei crash quando si naviga tra le activity quando ci sono
di mezzo adapter ed oggetti http ( se si toglie un interfaccia prima
di far finire il caricamento ecc )
- crash dovuti a out of memory (dovuto al cashing dell imagine)
____________________

- immagini nel dettaglio esercente, settare il layout
- immagine della mappa in dettaglio esercente, settare la dim in base lo screen size
- mappa apribile nel dettaglio esercente
- scelta colori layout e decidere lo stile delle righe
- su android 2.x il dialog " scegli dove e quando" ha colori sballatissimi
- mettere le immagini per cell, mail, facebook, filtri ecc

--------------------

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
