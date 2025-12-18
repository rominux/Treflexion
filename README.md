# ♣️ Tréflexion - Le Poker Solitaire

> **SAÉ 1.01-1.02 : Création d'un logiciel ludo-pédagogique** > *Université de Lille - IUT A*

**Tréflexion** est un jeu de stratégie et de probabilités codé en Java (iJava). Il revisite le concept du "Poker Solitaire" sur une grille de 5x5 cartes, avec pour objectif d'initier les joueurs aux notions de combinatoire et d'optimisation spatiale.

---

## 👥 Auteurs
**Groupe B**
* **Romain LEFEBVRE**
* **Baptiste MORIN**

---

## 🎮 Concept du Jeu
Le but est de remplir une grille de **25 cases** avec des cartes piochées une par une. Chaque ligne et chaque colonne constitue une "main" de poker de 5 cartes.

L'objectif est de maximiser son score total en formant les meilleures combinaisons possibles simultanément horizontalement et verticalement.

### Pourquoi "Tréflexion" ?
Contrairement au hasard pur, ce jeu demande de :
1. **Anticiper** : Calculer les probabilités qu'une carte utile sorte (les "Outs").
2. **Optimiser** : Gérer les intersections (une carte posée impacte à la fois une ligne et une colonne).
3. **Mémoriser** : Se souvenir des cartes déjà passées ("Memory" implicite).

---

## 🚀 Fonctionnalités Techniques
Ce projet a été développé en mettant l'accent sur la modularité et l'expérience utilisateur en mode texte (Console).

* **🎨 Moteur Graphique ASCII :** Le jeu ne se contente pas d'afficher du texte brut. Il charge dynamiquement des fichiers "templates" (`visu/carte.txt`, `visu/acceuil.txt`) pour afficher une interface immersive avec décors et cadres.
* **💾 Système de Sauvegarde Procédurale (Seed) :**
    * Pas de sauvegarde binaire lourde.
    * Chaque partie est générée par une clé alphanumérique unique (la **Seed**), ex : `F8K9L2`.
    * En entrant la même Seed au démarrage, deux joueurs auront **exactement** le même tirage de cartes, permettant des compétitions équitables.
* **🧮 Système de Score "Américain" :**
    * Implémentation d'un barème spécifique aux grilles 5x5 (où une *Couleur* est mathématiquement plus rare et donc plus rémunératrice qu'un *Full*, contrairement au poker classique).
* **🛡️ Robustesse :** Gestion complète des erreurs de saisie (coordonnées hors limites, placement sur une case déjà occupée).
