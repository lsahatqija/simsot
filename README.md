# OT SIMS : Pac-Man sur Open Street Map
5IF Option Transversale Socio-Interactive MobileS

Ceci est une application Android où l'on pourra jouer à Pac-Man sur notre emplacement sur une carte.

Le but est de pouvoir y jouer soit seul avec les IA fantômes, soit en multijoueurs avec des joueurs faisant les fantômes.

## Architecture
Client Android Java

Serveur NodeJS

BDD Redis et MongoDB

Socket.io pour la communication client-serveur
 
## Utilisation
### Option Play Store
Télécharger l'application sur Play Store, qui a pour nom :

### Option téléphone physique
Récupérer le .apk, si besoin en compilant le code (sur Android Studio). Copier le .apk dans le téléphone par câble et lancer l'appli en utilisant ngrok pour se connecter au serveur.

### Option émulateur
##### Lancer le serveur 
Installer NodeJS et npm : https://nodejs.org/en/

Télécharger le code du serveur et le mettre dans un dossier

Installer le package express dans ce même dossier : écrire en ligne de commande

	npm install express

Lancer le serveur : dans ce même dossier, écrire en ligne de commande

	node index.js

##### Compiler le client
Installer Android Studio (de préférence) ou Eclipse, ou un éditeur de texte : http://developer.android.com/sdk/index.html

Récupérer le code, le mettre sur Android Studio, lancer l'app.

## Utile
### Récupérer le code
git clone


