#!/bin/bash

# CONFIG
racine="/cal/homes/lbinet/workspace/Sys_distribue/"

# CHOIX DE L'INPUT
input="Input/domaine_public_fluvial.txt"
#input="Input/Input.txt"
#input="Input/deontologie_police_nationale.txt"
#input="Input/forestier_mayotte.txt"

# paramètre $2 pour la machine à choisir, par défaut C133-01
if [ -z "$2" ]
then
    machine="C133-01"
else
    machine=$2
fi
echo "Connexion sur ssh.enst.fr puis sur "$machine" si besoin"

if [ -z "$1" ]
then
    echo "Aucun argument passé: run, git, jar, purge_files, purge_folders, mkdir, input."
    exit 1

# transfer jars
elif [ $1 = "jar" ]
then
    echo "Transfert des jars: "
    scp -i ~/.ssh/telecom SLAVE.jar MASTER.jar lbinet@ssh.enst.fr:$racine

elif [ $1 = "input" ]
then
    echo "Transfert des fichiers d'Input: "
    scp -i ~/.ssh/telecom -r Input lbinet@ssh.enst.fr:$racine

elif [ $1 = "git" ]
then
# git pull origin
    echo "Mise à jour: git pull origin: "
    ssh telecom "ssh -o StrictHostKeyChecking=no "$machine" 'cd "$racine" && git pull origin'"

elif [ $1 = "run" ]
then
    echo "Lancement du script: run"
    ssh telecom "ssh -i /cal/homes/lbinet/.ssh/intra_telecom -o StrictHostKeyChecking=no "$machine" 'cd "$racine" && java -jar MASTER.jar "$input"'"
elif [ $1 = "runslave" ]
then
    echo "Lancement du script: "
    ssh telecom "ssh -o StrictHostKeyChecking=no "$machine" 'cd "$racine" && java -jar SLAVE.jar modeUMXSMX car Sm1 Um1 Um2'"

elif [ $1 = "purge_files" ]
then
    echo "Lancement du script: purge (suppression des Sx Umx Smx Rmx Result)"
    ssh telecom "cd "$racine"Jobs && rm Sx/* Umx/* Smx/* Rmx/* Result/* && echo 'Fichiers purgés.'"
elif [ $1 = "purge_folders" ]
then
    echo "Lancement du script: purge (suppression des Sx Umx Smx Rmx Result)"
    ssh telecom "cd "$racine"Jobs && rm -r Sx Umx Smx Rmx Result && echo 'Fichiers purgés.' && ls"
elif [ $1 = "mkdir" ]
then
    echo "Creation arborescence :"
    ssh telecom "cd "$racine"Jobs && mkdir Sx Umx Smx Rmx Result && echo 'Création des dossiers réalisée' && ls"
else
    echo "Argument invalide: run, git, jar, purge_files, purge_folders, mkdir, input."
    exit 1
fi
#
