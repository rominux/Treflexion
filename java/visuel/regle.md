# RÈGLES DU JEU - TRÉFLEXION (Édition Balatro)

### 1. BUT DU JEU
L'objectif est de remplir une grille de 5x5 cases (25 cartes) pour former les meilleures mains de poker possibles sur les **5 lignes** et les **5 colonnes**.

Le score total est la somme des scores des 10 mains formées.

### 2. DÉROULEMENT D'UN TOUR
1.  **La Pioche :** Une carte vous est proposée.
2.  **La Décision :**
    * Soit vous la **placez** sur une case vide (Coordonnées Ligne/Colonne).
    * Soit vous la **défaussez** en utilisant un Joker (touche 'J').
3.  **Irréversible :** Une fois posée, une carte ne peut plus être déplacée.

### 3. LES JOKERS (QUIZ)
Vous disposez d'un stock limité de **15 Jokers**.
* Pour activer un Joker, entrez **'J'** (ou 'j') au moment de choisir la ligne ou la colonne.
* Une question de culture générale (Maths/Logique/Casino) vous sera posée.
    * **Bonne réponse :** La carte est défaussée sans pénalité. On passe à la suivante.
    * **Mauvaise réponse :** Le Joker est perdu et vous êtes **obligé** de jouer la carte.

### 4. SYSTÈME DE SCORE (BALATRO)
Le calcul des points suit la formule :
**SCORE = (Somme des Jetons + Base) x Multiplicateur**

#### A. Valeur des Cartes (Jetons)
Seules les cartes qui *contribuent* à la combinaison (Cartes Actives) donnent des jetons.
* **2 à 9** : Valeur faciale (ex: 7 vaut 7 jetons).
* **10, Valet, Dame, Roi** : 10 jetons.
* **As** : 11 jetons.

#### B. Barème des Mains (Base Chips x Mult)
| Main de Poker | Base | Mult | Cartes Actives (qui comptent) |
| :--- | :--- | :--- | :--- |
| **Quinte Flush Royale** | **100** | **x8** | Les 5 cartes |
| **Quinte Flush** | **100** | **x8** | Les 5 cartes |
| **Carré** (4 of a Kind) | **60** | **x7** | Seulement les 4 cartes identiques |
| **Full House** | **40** | **x4** | Les 5 cartes (Brelan + Paire) |
| **Couleur** (Flush) | **35** | **x4** | Les 5 cartes |
| **Suite** (Straight) | **30** | **x4** | Les 5 cartes |
| **Brelan** (3 of a Kind) | **30** | **x3** | Seulement les 3 cartes identiques |
| **Double Paire** | **20** | **x2** | Seulement les 4 cartes (2 Paires) |
| **Paire** | **10** | **x2** | Seulement les 2 cartes de la paire |
| **Carte Haute** | **5** | **x1** | Seulement la carte la plus forte |

### 5. COMMANDES
* **Ligne :** Entrez un chiffre de 1 à 5.
* **Colonne :** Entrez un chiffre de 1 à 5.
* **Joker :** Entrez 'J' ou 'j' pour tenter de défausser.

### 6. SEED (Graine de génération)
Chaque partie possède un code unique appelé **Seed** (ex: `A1B2C3D4`).
Vous pouvez noter ce code à la fin d'une partie et le saisir dans le menu principal pour rejouer exactement la même distribution de cartes et comparer votre score avec un ami !