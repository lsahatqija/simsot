# OT SIMS : Pac-Man sur Open Street Map
5IF Option Transversale Socio-Interactive MobileS

Ce projet est une application Android grâce à laquelle il est possible de jouer à Pac-Man en multijoueurs sur la carte GPS centrée sur notre position.

On peut y jouer soit un niveau normal soit sur une carte GPS, on peut choisir PacMan ou un fantôme, on peut y jouer avec d'autres joueurs et/ou avec des IA.

Le serveur associé à cette application Android se trouve à ce lien : https://github.com/remyrd/simsot-server

## Architecture
Client : Android Java

Communication client-serveur : Socket.io

Serveur : NodeJS

BDD : Redis et MongoDB

## Utilisation
### Option Play Store
Pas encore prêt.

### Serveur
##### Option serveur local

Installer le serveur local puis le lancer : dans le dossier du serveur, écrire en ligne de commande node puis le nom du serveur. Exemple

	node index.js
	
##### Option serveur Heroku

Installer le client Heroku puis lancer le serveur Heroku : entrer la commande

	heroku run bash --app simsot-server

### Client
##### Option téléphone physique
Récupérer le .apk, si besoin en compilant le code (sur Android Studio). Copier le .apk dans le téléphone par câble et lancer l'appli (en utilisant ngrok pour se connecter au serveur, dans le cas du serveur local).

##### Option émulateur
Récupérer le code, le mettre sur Android Studio, lancer l'app.

## Installation
##### Serveur local
Installer NodeJS et npm : https://nodejs.org/en/

Prendre le code du serveur et le mettre dans un dossier

Installer les packages dans ce même dossier : écrire en ligne de commande

	npm install express
	
	npm install socket.io
	
	npm install ioredis
	
	npm install mongodb

##### Client Heroku
https://toolbelt.heroku.com/

##### Client

Installer Android Studio : http://developer.android.com/sdk/index.html , sinon Eclipse ou un éditeur de texte

## Autre
### Checker les logs du serveur Heroku

Installer le client Heroku puis entrer la commande

    heroku logs --app simsot-server

### Preview du ReadMe

http://tmpvar.com/markdown.html

