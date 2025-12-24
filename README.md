# SAÉ 1.01/1.02 - Tréflexion (Poker Solitaire)

**Tréflexion** est une implémentation du jeu de réflexion "Poker Solitaire" développée en Java (environnement iJava). Le but est de placer 25 cartes sur une grille de 5x5 pour former les meilleures combinaisons de poker possibles sur les 5 lignes et les 5 colonnes.

Ce projet intègre un système de questions "Joker" pédagogique et une interface graphique en ASCII Art.

## 📋 Prérequis

Pour compiler et exécuter ce projet, vous devez disposer de :
* L'environnement **iJava** (Université de Lille) installé et configuré dans votre terminal.
* La bibliothèque `extensions` (incluse dans l'environnement iJava) pour la gestion des fichiers et CSV.

## 🚀 Installation et Compilation

Le point d'entrée du programme est le fichier `Main.java`. Il ne contient pas de méthode `public static void main` standard mais utilise la structure `algorithm()` propre à iJava.

### 1. Compilation
Ouvrez votre terminal à la racine du dossier du projet et exécutez la commande suivante :

```bash
ijava compile Main.java

```

### 2. Exécution

Une fois la compilation terminée sans erreur, lancez le jeu avec la commande :

```bash
ijava execute Main

```

*(Note : Si vous souhaitez lancer les tests unitaires pour vérifier le bon fonctionnement des fonctions)* :

```bash
ijava test Main

```

## 🎮 Comment Jouer ?

### Le But du Jeu

Vous disposez d'un paquet de 52 cartes. Vous devez en placer 25, une par une, sur une grille de 5x5 cases.
Une fois une carte posée, **elle ne peut plus être déplacée**.

À la fin de la partie, des points sont attribués pour chaque ligne et chaque colonne selon les combinaisons de poker formées (Paire, Brelan, Suite, Couleur, etc.).

### Commandes en jeu

* **Saisie des coordonnées :** Entrez le numéro de la LIGNE (1-5) puis le numéro de la COLONNE (1-5).
* **Utiliser un Joker :** Au moment de saisir une ligne ou une colonne, tapez `J` (ou `j`).
* Une question de culture générale/mathématiques vous sera posée.
* **Bonne réponse :** La carte actuelle est défaussée (vous ne la jouez pas).
* **Mauvaise réponse :** Vous êtes obligé de jouer la carte.



### Système de Points (Américain)

Le barème favorise les combinaisons difficiles à obtenir géométriquement :

* **Quinte Flush Royale :** 100 pts
* **Quinte Flush :** 75 pts
* **Carré :** 50 pts
* **Couleur (Flush) :** 20 pts
* **Suite (Straight) :** 15 pts
* **Full :** 10 pts
* **Brelan :** 10 pts
* **Double Paire :** 5 pts
* **Paire :** 2 pts

## 📂 Structure du Projet

Voici l'organisation des fichiers source :

* `Main.java` : Cœur du programme. Contient l'algorithme principal, la gestion de l'affichage, la logique du jeu et les tests unitaires.
* `Carte.java` : Structure de données simple représentant une carte (valeur et couleur).
* `Question.java` : Structure de données représentant une question de quiz.
* `visuel/` : Dossier contenant les ressources externes.
* `acceuil.txt` : Template de l'écran d'accueil.
* `jeu.txt` : Template de l'interface principale de jeu.
* `question.txt` : Template de l'interface de quiz.
* `resultat.txt` : Template de l'écran de fin de partie.
* `regle.md` : Fichier texte contenant les règles affichées en jeu.
* `questions.csv` : Base de données des questions pour les Jokers.



## ✨ Fonctionnalités Clés

* **Interface ASCII Art avancée :** Utilisation de templates et de positionnement précis du curseur.
* **Système de Seed (Graine) :** Chaque partie possède un identifiant unique (Seed). Vous pouvez entrer une seed spécifique pour rejouer exactement la même distribution de cartes.
* **Jokers Pédagogiques :** Intégration d'un fichier CSV pour charger dynamiquement des questions.
* **Code Clean :** Respect des conventions de nommage, commentaires explicatifs et indentation rigoureuse.

## 👥 Auteurs

Projet réalisé dans le cadre de la SAÉ 1.01/1.02.
