### 1. L'Objectif du Jeu
Le but est de marquer un maximum de points en constituant **10 mains de poker** simultanément sur une grille de 5x5 cartes.
* **5 mains horizontales** (les lignes).
* **5 mains verticales** (les colonnes).

### 2. Matériel et Mise en place
* Un jeu standard de 52 cartes (sans Jokers).
* Une grille vide de 5 cases sur 5 (soit 25 emplacements).


### 3. Déroulement d'un tour
Le jeu se déroule en **25 tours** exactement.

1.  **La Pioche :** Le joueur tire une carte du dessus du paquet.
2.  **Le Placement :** Le joueur doit placer cette carte sur n'importe quelle case **vide** de la grille.
3.  **La Règle d'Or :** Une fois qu'une carte est posée, elle **ne peut plus être déplacée**.
4.  **Répétition :** On répète l'opération jusqu'à ce que les 25 cases soient remplies.
5.  **Fin :** Il restera 27 cartes dans le paquet qui ne seront jamais utilisées.

### 4. Le Système de Points (Scoring)

C'est ici que votre projet de groupe prend tout son sens. Il existe deux systèmes. Pour votre projet, je recommande le **Système Américain**, car il respecte mieux les probabilités géométriques de la grille.

*Pourquoi ?* Dans une grille 5x5, il est mathématiquement plus difficile de faire une Couleur (Flush) qu'un Full. Le barème s'adapte donc à cette difficulté.

#### Tableau des scores (Système Américain)

| Main de Poker (Nom FR) | Description | Points |
| :--- | :--- | :--- |
| **Quinte Flush Royale** | 10, V, D, R, As de la même couleur | **100** |
| **Quinte Flush** | 5 cartes qui se suivent de la même couleur | **75** |
| **Carré** (4 of a Kind) | 4 cartes de même valeur (ex: 4 Rois) | **50** |
| **Couleur** (Flush) | 5 cartes de la même couleur (ex: 5 Cœurs) | **20** |
| **Full** (Full House) | 1 Brelan + 1 Paire (ex: 3 Rois, 2 As) | **10** |
| **Suite / Quinte** (Straight)| 5 cartes qui se suivent (couleurs mixtes) | **15** |
| **Brelan** (3 of a Kind) | 3 cartes de même valeur | **10** |
| **Double Paire** | 2 paires différentes | **5** |
| **Une Paire** | 2 cartes de même valeur | **2** |
| **Carte Haute** | Aucune combinaison | **0** |

*Notez que dans ce système, la Couleur vaut plus que le Full, et la Suite vaut plus que le Brelan.*

### 5. Détail des mains (Logique de validation)

Voici l'ordre de priorité strict (si une main est à la fois une Suite et une Couleur, c'est une Quinte Flush).

1.  **Quinte Flush Royale (Royal Flush) :**
    * *Condition :* `IsFlush` (Même couleur) AND `IsStraight` (Se suivent) AND `Contains(Ace)` AND `Contains(King)`.
2.  **Quinte Flush (Straight Flush) :**
    * *Condition :* `IsFlush` AND `IsStraight`.
3.  **Carré (Four of a Kind) :**
    * *Condition :* 4 cartes identiques en valeur.
4.  **Couleur (Flush) :**
    * *Condition :* 5 cartes du même symbole (Cœur, Pique, Trèfle, Carreau).
5.  **Full (Full House) :**
    * *Condition :* 3 cartes valeur X + 2 cartes valeur Y.
6.  **Suite (Straight) :**
    * *Condition :* 5 valeurs consécutives. *Attention : L'As peut être au début (A-2-3-4-5) ou à la fin (10-V-D-R-A).*
7.  **Brelan (Three of a Kind) :**
    * *Condition :* 3 cartes de même valeur.
8.  **Double Paire (Two Pair) :**
    * *Condition :* 2 cartes valeur X + 2 cartes valeur Y.
9.  **Paire (One Pair) :**
    * *Condition :* 2 cartes de même valeur.

### 6. Exemple de calcul de score final

Imaginez la ligne 1 : `As♥`, `As♦`, `As♣`, `Roi♠`, `Roi♥`
-> C'est un **Full** (3 As + 2 Rois) -> **10 points**.

Imaginez la colonne 1 : `As♥`, `2♥`, `5♥`, `Valet♥`, `9♥`
-> C'est une **Couleur** (Tous Cœurs) -> **20 points**.

Vous faites le calcul pour les 5 lignes + les 5 colonnes = **Score Total**.