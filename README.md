# Fuzzer-Swagger

## Introduction
Dans le cadre du projet de Validation et Vérification, nous avons réalisé un fuzzer sur les fichiers Swagger de description d'API. 

## Objectif
Notre projet vise à trouver des anomalies dans les descriptions d'API faites avec Swagger. Selon un fichier swagger.json donné (ici on utilise le petstore de swagger.io), on récupère les différents chemins (paths) que l'on requête avec des données "extrêmes" afin de faire apparaître des erreurs. Exemple : supprimer un pet avec un id inexistant ou un id négatif ...

## Contenu
Swagger.io nous a permis de génerer la partie Serveur du petstore en Node.js. Notre application Java effectue des requêtes HTTP (GET,POST,DELETE) sur ce serveur lancé en local sur le port 8080. Les tests sont réalisés et exécutés avec JUnit.

![Fuzzing Archi](/Fuzzing_Archi.png)

## Utilisation
Afin d’utiliser notre fuzzer, il faut tout d’abord lancer l’API PetStore en lançant la commande depuis le dossier "nodejs-server-server" : npm start

Puis, il faut lancer le test JUnit présent dans le fichier “/src/main/java/Fuzzer.java”.
