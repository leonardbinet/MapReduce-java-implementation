# Implémentation de Map Reduce en Java

## Fonctionnement:

### Principes
On cherche à distribuer une tâche sur plusieurs machines. Nous disposons d'une machine "Master" qui envoie des tâches aux machines "Slaves" via des commandes SSH.

On se base ici sur le fonctionnement de MapReduce, qui consiste à faire réaliser des opérations de Map et de Reduce à nos machines 'Slaves'.

#### Configuration
Le code est en java, nous avons constitué un jar MASTER et un jar SLAVE.

Pour les connections SSH il faut avoir créé en amont des clés RSA pour permettre au Master de se connecter aux Slaves sans mot de passe.

### Etapes

#### Côté Master:

 Prendre INPUT, faire l’INPUT splitting pour fabriquer les Sx (attention à la taille des splits pour éviter qu’un traitement ne dépasse les capacités d’une machine).

 Pour chaque Sx, lancer le traitement Split Mapping en parallèle (Sx -> UMx) et construire le dictionnaire “UMx - machines”

 Recevoir les “clés-UMx” issus du traitement Split Mapping et construire le dictionnaire “clés -UMx”

 Attendre la fin des traitements Split Mapping

 Pour chaque clé du dictionnaire “clés-UMx”, lancer les traitements en parallèle shuffling maps  + reducing sorted maps  (UMx -> SMx + SMx -> RMx) et construire le dictionnaire “RMx - machines”

 Recevoir les RMx

 Attendre la fin des traitements

 Fusionner les RMx dans un fichier résultat final

 #### Côté Slaves

Deux modes de réception de commandes:

 ## TODO:
  - faire des splits de plusieurs lignes en phase de split.
  - gérer les cas de pannes (si une machine ne répond plus).
  - fichier final
