x2Android
=========


* Clonare il repo
* Dare 'git submodule init'
* Dare 'git submodule update'
* Aprire Eclipse
* File > Import > Existing Android Code into Workspace
 * il progetto da importare si trova in ./external/ActionBarSherlock/library, 
 * **NON** copiare nel workspace
* Andare nelle proprietà del progetto appena importato:
 * scheda "Java Compiler": Assicurarsi che Compiler Compliance Level sia impostato a 1.6 e che Use Default compliance Settings sia spuntato
 * scheda "Android": Assicurarsi che il progect build target sia all'api level 16
* Dare il rebuild dell'intero workspace (o quantomeno dei due progetti di interesse)
* Ripetere per ogni submodule (dal secondo punto se al repo è stato aggiunto un submodule nuovo, dal quarto se si stanno aggiungendo progressivamente i vari submodule dopo un clone del repo)
