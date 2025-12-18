import extensions.File;

class main extends Program {   
    // ========================================================================================================================
    // CONSTANTES DU JEU DE CARTES
    // ========================================================================================================================
    final int V = 11; 
    final int D = 12; 
    final int R = 13; 
    final int A = 14;

    final String[] LISTE_COULEUR = new String[]{"♦", "♥", "♣", "♠"};
    final String[] LISTE_VALEUR = new String[]{"", "", "2", "3", "4", "5", "6", "7", "8", "9", "10", "V", "D", "R", "A"};

    // ========================================================================================================================
    // BAREME DES POINTS
    // ========================================================================================================================
    final int PTS_ROYAL_FLUSH = 100;
    final int PTS_STRAIGHT_FLUSH = 75;
    final int PTS_CARRE = 50;
    final int PTS_FLUSH = 20;     
    final int PTS_STRAIGHT = 15;  
    final int PTS_FULL = 10;
    final int PTS_BRELAN = 10;
    final int PTS_DOUBLE_PAIRE = 5;
    final int PTS_PAIRE = 2;

    // ========================================================================================================================
    // VARIABLES GLOBALES
    // ========================================================================================================================
    // Stocke la version texte de la seed pour l'affichage
    String seedActuelle; 
    // Stocke la version numérique pour les calculs
    long seedNumber;     

    // ========================================================================================================================
    // FONCTION PRINCIPALE (ALGORITHME)
    // ========================================================================================================================
    
    /*
     * Fonction principale du programme.
     * Elle gère la boucle du menu principal, la navigation entre les écrans
     * et l'arrêt du programme.
     */
    void algorithm() {
        boolean continuer = true;
        while(continuer) {
            clear();
            AfficherPageAccueuil(); 
            
            println("\n=== MENU PRINCIPAL ===");
            println("1. Lire les règles");
            println("2. Nouvelle Partie (Aléatoire)");
            println("3. Charger une Partie (Via Seed)");
            println("4. Quitter");
            print("Votre choix : ");
            
            int choix = readInt();

            if (choix == 1) {
                AfficherRegles();
            } else if (choix == 2) {
                // Création d'une seed aléatoire puis lancement
                String seed = GenererRandomSeedString(10);
                LancerPartieAvecSeed(seed);
            } else if (choix == 3) {
                // Demande de seed manuelle pour rejouer une partie
                print("Entrez la seed (ex: A1B2C3D4E5) : ");
                String seed = readString();
                LancerPartieAvecSeed(seed);
            } else if (choix == 4) {
                continuer = false;
                println("Au revoir !");
            }
        }
    }

    // ========================================================================================================================
    // GESTION DU HASARD (SEED) SANS LIBRAIRIE
    // ========================================================================================================================

    /*
     * Initialise la partie avec une graine spécifique.
     * Convertit la graine texte en nombre pour notre générateur maison.
     */
    void LancerPartieAvecSeed(String seed) {
        seedActuelle = seed;
        seedNumber = StringToLong(seed); // Initialisation de la variable de hasard
        
        println("Initialisation de la partie avec la Seed : " + seedActuelle);
        LancerJeu();
    }

    /*
     * Génère un nombre entier "aléatoire" en fonction de la Seed actuelle.
     * Permet de rejouer exactement la même partie si on a la même Seed.
     */
    int GenererNombrePseudoAleatoire(int max) {
        // Fait un grand nombre puis le reduit
        seedNumber = (seedNumber * 1664525 + 1013904223);
        long resultat = seedNumber;
        if (resultat < 0) {
            resultat = resultat * -1;//si limite atteinte (Z**63 + 1 = -2**63)
        }
        
        // On renvoie un nombre entre 0 et max
        return (int) (resultat % max);
    }

    /*
     * Convertit une chaine de caractères en un nombre long unique.
     * Sert à transformer le texte de la seed en nombre utilisable pour les calculs.
     */
    long StringToLong(String s) {
        long h = 0;
        for (int i = 0; i < length(s); i += 1) {
            h = 31 * h + charAt(s, i);
        }
        return h;
    }

    /*
     * Utilise la fonction random() native d'iJava pour créer une nouvelle Seed aléatoire.
     */
    String GenererRandomSeedString(int longueur) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for(int i=0; i < longueur; i += 1) {
            int idx = (int)(random() * length(chars)); 
            res = res + charAt(chars, idx);
        }
        return res;
    }

    // ========================================================================================================================
    // BOUCLE DE JEU
    // ========================================================================================================================

    /*
     * Gère le déroulement complet d'une partie (les 25 tours).
     * Crée le paquet, gère la pioche, l'affichage et le placement.
     */
    void LancerJeu() {
        carte[] paquet = CreeNouveauJeu();
        Melanger(paquet); // Mélange contrôlé par la seedNumber
        carte[][] grille = new carte[5][5];

        // Boucle pour placer les 25 cartes
        for (int tour = 0; tour < 25; tour += 1) {
            carte cActuelle = paquet[tour];
            // On prévisualise la carte suivante (sauf au dernier tour)
            carte cSuivante = (tour < 24) ? paquet[tour+1] : null;

            AfficherEcranJeu(grille, cActuelle, cSuivante);
            println("\n\n\n"); // Espace pour ne pas écrire sur le dessin
            PlacerCarte(grille, cActuelle);
        }

        // Une fois les 25 cartes placées, on affiche les résultats
        AfficherEcranResultat(grille);
        
        println("\n=== FIN DE PARTIE ===");
        println("Seed de cette partie : " + seedActuelle);
        println("Appuyez sur Entrée pour revenir au menu...");
        readString(); // Pause en attendant Entrée
    }

    // ========================================================================================================================
    // AFFICHAGE : ACCUEIL
    // ========================================================================================================================

    /*
     * Charge et affiche le fichier visu/acceuil.txt.
     * Utilise RendreBuffer pour gérer les couleurs.
     */
    void AfficherPageAccueuil() {
        String[] ecran = LireTemplate("visu/acceuil.txt");
        RendreBuffer(ecran);
    }

    // ========================================================================================================================
    // AFFICHAGE : JEU EN COURS
    // ========================================================================================================================

    /*
     * Affiche la grille de jeu, les cartes placées et les informations.
     * Utilise visu/carte.txt comme fond d'écran.
     */
    void AfficherEcranJeu(carte[][] grille, carte cActuelle, carte cSuivante) {
        clear();
        String[] ecran = LireTemplate("visu/carte.txt");

        // Coordonnées de départ de la grille dans visu/carte.txt
        int startLigne = 7; 
        int startCol = 4;

        // On parcourt la grille pour dessiner chaque carte placée
        for (int i = 0; i < 5; i += 1) { 
            for (int j = 0; j < 5; j += 1) { 
                if (grille[i][j] != null) {
                    int l = startLigne + (i * 7); 
                    int c = startCol + (j * 10);
                    DessinerCarteGrande(ecran, l, c, grille[i][j]);
                }
            }
        }

        // On écrit le nom de la carte actuelle et suivante dans les crochets en bas
        if (cActuelle != null) { EcrireDansBuffer(ecran, 45, 22, NomCarte(cActuelle)); }
        if (cSuivante != null) { EcrireDansBuffer(ecran, 45, 75, NomCarte(cSuivante)); }

        RendreBuffer(ecran);
    }

    // ========================================================================================================================
    // AFFICHAGE : RÉSULTATS
    // ========================================================================================================================

    /*
     * Affiche l'écran de fin avec les scores calculés.
     * Utilise visu/resultat.txt comme fond d'écran.
     */
    void AfficherEcranResultat(carte[][] grille) {
        clear();
        String[] ecran = LireTemplate("visu/resultat.txt");

        // 1. Dessiner les cartes (Grand Format)
        int startLigne = 7;
        int startCol = 4;
        
        for (int i = 0; i < 5; i += 1) {
            for (int j = 0; j < 5; j += 1) {
                if (grille[i][j] != null) {
                    // Espacement vertical = 7 lignes (hauteur carte)
                    int l = startLigne + (i * 7); 
                    int c = startCol + (j * 10);
                    DessinerCarteGrande(ecran, l, c, grille[i][j]);
                }
            }
        }

        int scoreTotal = 0;

        // 2. Calcul et Affichage des scores des LIGNES
        for (int i = 0; i < 5; i += 1) {
            int score = CalculerScoreLigne(grille, i);
            scoreTotal = scoreTotal + score;
            
            int l = startLigne + (i * 7);
            int ligTexte = l + 3; // Milieu de la carte
            
            EcrireDansBuffer(ecran, ligTexte, 60, "" + score);
        }

        // 3. Calcul et Affichage des scores des COLONNES
        for (int j = 0; j < 5; j += 1) {
            int score = CalculerScoreColonne(grille, j);
            scoreTotal = scoreTotal + score;
            
            int col = 8 + (j * 10); // Alignement sous les cartes
            EcrireDansBuffer(ecran, 43, col, "" + score);
        }

        // 4. Afficher le Total global
        EcrireDansBuffer(ecran, 43, 68, "" + scoreTotal);

        RendreBuffer(ecran);
    }

    /*
     * Dessine une carte dans le buffer texte.
     * Place la valeur dans les coins et le symbole au centre.
     */
    void DessinerCarteGrande(String[] ecran, int l, int c, carte card) {
        String val = LISTE_VALEUR[card.num];
        String sym = LISTE_COULEUR[card.couleur];
        
        EcrireDansBuffer(ecran, l + 1, c + 2, val); // Valeur haut gauche
        EcrireDansBuffer(ecran, l + 3, c + 4, sym); // Symbole centre
        
        int decalage = (length(val) == 2) ? 5 : 6;  // Ajustement pour "10"
        EcrireDansBuffer(ecran, l + 5, c + decalage, val); // Valeur bas droite
    }

    // ========================================================================================================================
    // LOGIQUE SCORE (POKER AMÉRICAIN)
    // ========================================================================================================================
    
    /*
     * Récupère une ligne complète et calcule son score.
     */
    int CalculerScoreLigne(carte[][] grille, int ligneIdx) {
        return CalculerPointsMain(grille[ligneIdx]);
    }

    /*
     * Récupère une colonne complète et calcule son score.
     */
    int CalculerScoreColonne(carte[][] grille, int colIdx) {
        carte[] col = new carte[5];
        for(int i=0; i<5; i+=1) {
            col[i] = grille[i][colIdx];
        }
        return CalculerPointsMain(col);
    }

    /*
     * Cœur du système de score : analyse 5 cartes et renvoie les points.
     * Vérifie les combinaisons (Suite, Couleur, Carré, etc.).
     */
    int CalculerPointsMain(carte[] main) {
        // Copie et tri pour faciliter l'analyse
        carte[] triee = CopierTableau(main);
        TrierMain(triee);

        boolean flush = EstCouleur(triee);
        boolean straight = EstSuite(triee);
        int[] counts = CompterValeurs(triee); 

        boolean carre = false;
        boolean brelan = false;
        int paires = 0;

        // Analyse des occurrences (Paires, Brelans...)
        for (int i = 0; i < length(counts); i+=1) {
            if (counts[i] == 4) { carre = true; }
            if (counts[i] == 3) { brelan = true; }
            if (counts[i] == 2) { paires += 1; }
        }

        // Vérification des combinaisons par ordre décroissant de valeur
        if (flush && straight) {
            if (triee[0].num == 10) { return PTS_ROYAL_FLUSH; }
            return PTS_STRAIGHT_FLUSH;
        }
        if (carre) { return PTS_CARRE; }
        if (flush) { return PTS_FLUSH; }
        if (straight) { return PTS_STRAIGHT; }
        if (brelan && paires >= 1) { return PTS_FULL; }
        if (brelan) { return PTS_BRELAN; }
        if (paires == 2) { return PTS_DOUBLE_PAIRE; }
        if (paires == 1) { return PTS_PAIRE; }

        return 0;
    }

    /*
     * Vérifie si toutes les cartes ont la même couleur.
     */
    boolean EstCouleur(carte[] m) {
        int c = m[0].couleur;
        for(int i=1; i<5; i+=1) {
            if (m[i].couleur != c) { return false; }
        }
        return true;
    }

    /*
     * Vérifie si les cartes se suivent numériquement.
     */
    boolean EstSuite(carte[] m) {
        for(int i=0; i<4; i+=1) {
            if (m[i+1].num != m[i].num + 1) { return false; }
        }
        return true;
    }

    /*
     * Compte combien de fois chaque valeur apparait (Histogramme).
     */
    int[] CompterValeurs(carte[] m) {
        int[] c = new int[15];
        for(int i=0; i<5; i+=1) {
            c[m[i].num] += 1;
        }
        return c;
    }

    /*
     * Trie les cartes par valeur croissante pour faciliter l'analyse.
     */
    void TrierMain(carte[] t) {
        for(int i=0; i<length(t)-1; i+=1) {
            for(int j=0; j<length(t)-i-1; j+=1) {
                if (t[j].num > t[j+1].num) {
                    carte temp = t[j]; t[j]=t[j+1]; t[j+1]=temp;
                }
            }
        }
    }

    /*
     * Crée une copie d'un tableau de cartes pour ne pas modifier l'original.
     */
    carte[] CopierTableau(carte[] src) {
        carte[] dest = new carte[length(src)];
        for(int i=0; i<length(src); i+=1) {
            dest[i] = src[i];
        }
        return dest;
    }

    // ========================================================================================================================
    // RENDU & FICHIERS
    // ========================================================================================================================
    
    /*
     * Ecrit un texte à des coordonnées précises dans le tableau représentant l'écran.
     */
    void EcrireDansBuffer(String[] ecran, int l, int c, String texte) {
        if (l >= length(ecran)) { return; }
        String ligne = ecran[l];
        if (c + length(texte) > length(ligne)) { return; }
        
        String debut = substring(ligne, 0, c);
        String fin = substring(ligne, c + length(texte), length(ligne));
        ecran[l] = debut + texte + fin;
    }

    /*
     * Affiche le tableau de chaines ligne par ligne.
     * Remplace le caractère spécial '¤' par un bloc blanc.
     */
    void RendreBuffer(String[] ecran) {
        print(WHITE + BG_BLACK);
        for (int i = 0; i < length(ecran); i += 1) {
            String ligne = ecran[i];
            for (int j = 0; j < length(ligne); j += 1) {
                char c = charAt(ligne, j);
                if (c == '¤') {
                    print(BG_WHITE + " " + BG_BLACK);
                } else {
                    print(c);
                }
            }
            println(); 
        }
        print(RESET);
    }

    /*
     * Lit un fichier texte et le stocke dans un tableau de String.
     * Utile pour charger les templates graphiques.
     */
    String[] LireTemplate(String chemin) {
        File f = newFile(chemin);
        int nb = 0;
        while(ready(f)) { readLine(f); nb += 1; }
        
        f = newFile(chemin);
        String[] tab = new String[nb];
        int idx = 0;
        while(ready(f)) { tab[idx] = readLine(f); idx += 1; }
        return tab;
    }

    /*
     * Charge et affiche le fichier des règles.
     */
    void AfficherRegles() {
        clear();
        String[] regles = LireTemplate("visu/regle.md");
        RendreBuffer(regles);
        println("\nAppuyez sur Entrée pour revenir au menu...");
        readString();
    }

    // ========================================================================================================================
    // SAISIE & CARTES
    // ========================================================================================================================
    
    /*
     * Demande au joueur de saisir les coordonnées pour placer une carte.
     * Vérifie que la case est valide et vide.
     */
    void PlacerCarte(carte[][] grille, carte c) {
        boolean valide = false;
        while(!valide) {
            println("SEED: " + seedActuelle + " | Placement de : " + NomCarte(c));
            print("Choissisez une ligne (1-5) : ");
            int lig = readInt() - 1; 
            print("Choissisez une colonne (1-5) : ");
            int col = readInt() - 1;

            if (lig >= 0 && lig < 5 && col >= 0 && col < 5) {
                if (grille[lig][col] == null) {
                    grille[lig][col] = c;
                    valide = true;
                } else {
                    println(">> ERREUR : Case occupée !");
                }
            } else {
                println(">> ERREUR : Hors limites !");
            }
        }
    }

    /*
     * Crée un paquet neuf de 52 cartes ordonnées.
     */
    carte[] CreeNouveauJeu() {
        carte[] p = new carte[52];
        int idx = 0;
        for (int c=0; c<4; c+=1) {
            for (int v=2; v<=14; v+=1) {
                carte n = new carte(); n.couleur=c; n.num=v; p[idx]=n; idx+=1;
            }
        }
        return p;
    }

    /*
     * Mélange le paquet en utilisant le générateur contrôlé par la Seed.
     */
    void Melanger(carte[] p) {
        for (int i=0; i<length(p); i+=1) {
            // On utilise notre fonction pour respecter la Seed choisie
            int r = GenererNombrePseudoAleatoire(length(p)); 
            carte t=p[i]; 
            p[i]=p[r]; 
            p[r]=t;
        }
    }

    /*
     * Renvoie la représentation texte courte d'une carte (ex: V♥).
     */
    String NomCarte(carte c) {
        return LISTE_VALEUR[c.num] + LISTE_COULEUR[c.couleur];
    }

    /*
     * Efface le terminal. (home + clear)
     */
    void clear() {
        print("\u001B[H\u001B[2J");
    }
}