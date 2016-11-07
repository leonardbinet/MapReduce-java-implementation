#!/bin/bash

# transfer jars
if [ $1 = "jar" ]
then
    echo "Transfert des jars: "
    scp -i ~/.ssh/telecom SLAVESHAVADOOP.jar MASTERSHAVADOOP.jar lbinet@ssh.enst.fr:/cal/homes/lbinet/workspace/Sys_distribue/

elif [ $1 = "git" ]
then
# git pull origin
    echo "Mise à jour: git pull origin: "
    ssh telecom "ssh C129-01 'cd workspace/Sys_distribue && git pull origin'"

elif [ $1 = "run" ]
then
    echo "Lancement du script: run"
    ssh telecom "ssh C129-01 'cd workspace/Sys_distribue && java -jar MASTERSHAVADOOP.jar Input.txt'"

else
    echo "Aucun argument passé: run, git ou jar"
fi
#
