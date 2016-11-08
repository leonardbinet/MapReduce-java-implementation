#!/bin/bash

# paramètre $2 pour la machine à choisir, par défaut C133-22
if [ -z "$2" ]
then
    machine="C133-22"
else
    machine=$2
fi
echo "Connexion sur ssh.enst.fr puis sur "$machine


# transfer jars
if [ $1 = "jar" ]
then
    echo "Transfert des jars: "
    scp -i ~/.ssh/telecom SLAVESHAVADOOP.jar MASTERSHAVADOOP.jar lbinet@ssh.enst.fr:/cal/homes/lbinet/workspace/Sys_distribue/

elif [ $1 = "git" ]
then
# git pull origin
    echo "Mise à jour: git pull origin: "
    ssh telecom "ssh -o StrictHostKeyChecking=no "$machine" 'cd workspace/Sys_distribue && git pull origin'"

elif [ $1 = "run" ]
then
    echo "Lancement du script: run"
    ssh telecom "ssh -o StrictHostKeyChecking=no "$machine" 'cd workspace/Sys_distribue && java -jar MASTERSHAVADOOP.jar Input.txt'"
elif [ $1 = "runslave" ]
then
    echo "Lancement du script: "
    ssh telecom "ssh -o StrictHostKeyChecking=no "$machine" 'cd workspace/Sys_distribue && java -jar SLAVESHAVADOOP.jar modeUMXSMX car Sm1 Um1 Um2'"

elif [ $1 = "purge" ]
then
    echo "Lancement du script: purge (suppression des Sx et Umx)"
    ssh telecom "cd workspace/Sys_distribue/Sx && echo Sx && rm * && cd ../Umx && echo Umx &&rm *"
else
    echo "Aucun argument passé: run, git ou jar"
fi
#
