# Implémentation de Map Reduce en Java

## Fonctionnement:

### Principes
On cherche à distribuer une tâche sur plusieurs machines. Nous disposons d'une machine "Master" qui envoie des tâches aux machines "Slaves" via des commandes SSH.

On se base ici sur le fonctionnement de MapReduce, qui consiste à faire réaliser par la machine 'Master' des opérations de Map et de Reduce à nos machines 'Slaves'.

L'opération réalisée ici est un word count. On doit simplement donner en paramètre le chemin du fichier dont on cherche à compter les mots lors du lancement du programme.

![MapReduce](pictures/MapReduce.png)  

#### Configuration
Le code est en java, compilé en 1.7 (version machines de Telecom), et génère un jar MASTER et un jar SLAVE, respectivement sur le master et sur les slaves.

Pour les connections SSH il faut avoir créé en amont des clés RSA pour permettre au Master de se connecter aux Slaves sans mot de passe (ici c'est facile à réaliser car les machines partagent toutes le même file-system).

Par ailleurs, j'ai également créé des clés RSA entre mon ordinateur personnel 'client', et l'ordinateur de l'école 'master' afin de pouvoir lancer le MASTER.jar sur un ordinateur de l'école depuis mon ordinateur personnel.

Pour faciliter le lancement d'opérations depuis mon ordinateur personnel, j'ai créé un script shell contenant plusieurs commandes (transfert scp des jars, mise à jour via git, purger les fichiers créés, etc).

Pour clarifier le code, j'ai choisi de créer une classe configuration pour le Master, avec plusieurs paramètres dont (non exhaustif):
- le nombre maximum de threads lancés par machine
- le timeout lors du test de connexion SSH
- le timeout lors des requêtes du master vers les slaves
- le nombre de lignes par bloc lors de la phase de split.
- les mots à ne pas prendre en compte dans le word count.
- le chemin absolu du dossier où se trouve le jar Master sur la machine master
- les chemins des fichiers générés lors du job

De même il y a une config Slave avec les lieux des fichiers de job.


### Etapes

#### Côté Master:

 Prendre INPUT, faire l’INPUT splitting pour fabriquer les Sx.

 Pour chaque Sx, lancer le traitement Split Mapping en parallèle (Sx -> UMx) et construire le dictionnaire “UMx - machines”

 Recevoir les “clés-UMx” issus du traitement Split Mapping et construire le dictionnaire “clés -UMx”

 Attendre la fin des traitements Split Mapping

 Pour chaque clé du dictionnaire “clés-UMx”, lancer les traitements en parallèle shuffling maps  + reducing sorted maps  (UMx -> SMx + SMx -> RMx) et construire le dictionnaire “RMx - machines”

 Recevoir les RMx

 Attendre la fin des traitements

 Fusionner les RMx dans un fichier résultat final

#### Côté Slaves

Deux modes de réception de commandes:
 - modeSXUMX pour les opérations de mapping
 - modeUMXSMX pour les opérations de reducing

## Spécificités
S'il n'y a pas de réponse d'un slave (map ou reduce) au bout d'un certain temps, la commande est relancée sur la même machine ou sur une autre.

J'ai choisi de prendre un timout plus court, et en contrepartie de gérer les cas où une machine ne réponde pas dans le temps imparti. Cela permet d'aller plus vite.

J'ai remarqué que si l'on lançait plus de 5 jobs simultanément sur une machine, la connexion SSH était fermée par mesure de sécurité. Je l'ai laissé à 4 pour plus de stabilité.

## Lancement
Depuis le dossier contenant le MASTER.jar sur le master:
```
 - java -jar MASTER.jar fichier_input.txt
```
### Prérequis

Comme expliqué dans la configuration, il faudra que le SLAVE.jar soit situé à l'endroit spécifié dans la classe Config du Master (en effet le master doit savoir où est situé le SLAVE.jar pour pouvoir le lancer).

Par ailleurs il faut également que le master puisse accéder en ssh aux slaves sans mot de passe, c'est à dire avec un clé RSA générée en amont.

Il faut également créer en amont les dossiers qui vont recevoir les fichiers du job lancé (Sx Umx Smx Rmx Result).

Finalement, il faut rendre disponible dans le même dossier que le MASTER.jar un fichier texte contenant une liste de machines à tester (liste_machines.txt).

### Exemples

J'ai mis les logs résulant de lancement du programme avec divers fichier d'input.

Les paramètres étaient les suivants:
```
 - max_thread_per_machine = 3
 - timeout = 3
 - test_timeout = 3
 - lines_per_split = 50
```
Les étapes sont explicitées, et on peut observer quand des commandes n'ont pas abouti.

## TODO:

  - possibilité de trier les mots à ne pas prendre en compte en phase de shuffling
  - déclarer machine HS si elle répond mal plusieurs fois
  - rallonger timeout pour traitement de job s'il rate plus de 2 fois
  - rajouter les phases de transfert de fichiers
  - réaliser un vrai algo de shuffling pour minimiser les communications
  - factoriser code LaunchSlave et ConnectSSH

## Arborescence
```
├── Input
│   ├── Input.txt
│   ├── deontologie_police_nationale.txt
│   ├── domaine_public_fluvial.txt
│   └── forestier_mayotte.txt
├── Jobs
│   ├── Result
│   ├── Rmx
│   ├── Smx
│   ├── Sx
│   └── Umx
├── Logs
│   ├── log-domaine_public_fluvial.txt
│   ├── log-forestier_mayotte.txt
│   └── log-input.txt
├── MASTER
│   └── src
│       ├── AlgoMaster.java
│       ├── CheckMachinesUp.java
│       ├── Config.java
│       ├── ConnectSSH.java
│       ├── LaunchSlave.java
│       ├── LecteurFlux.java
│       └── Main.java
├── MASTER.jar
├── README.md
├── SLAVE
│   └── src
│       ├── Config.java
│       └── Main.java
├── SLAVE.jar
├── liste_machines.txt
└── utils.sh
```
