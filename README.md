# OT SIMS : Pac-Man sur Open Street Map
5IF Option Transversale Socio-Interactive MobileS

Ceci est une application Android où l'on pourra jouer à Pac-Man sur notre emplacement sur une carte.

Le but est de pouvoir y jouer soit seul avec les IA fantômes, soit en multijoueurs avec des joueurs faisant les fantômes.

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

