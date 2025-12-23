import extensions.File;

class Main extends Program {   
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
    String seedActuelle; 
    long seedNumber;     

    // ========================================================================================================================
    // FONCTION PRINCIPALE (ALGORITHME)
    // ========================================================================================================================
    
    /*
     * Fonction principale du programme.
     * Affiche le menu et dirige l'utilisateur vers les différentes sections (Jeu, Règles, Quitter).
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
                String seed = GenererRandomSeedString(10);
                LancerPartieAvecSeed(seed);
            } else if (choix == 3) {
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
    // GESTION DU HASARD
    // ========================================================================================================================

    /*
     * Initialise la seed (graine) et lance la boucle de jeu.
     */
    void LancerPartieAvecSeed(String seed) {
        seedActuelle = seed;
        seedNumber = StringToLong(seed);
        println("Initialisation de la partie avec la Seed : " + seedActuelle);
        LancerJeu();
    }

    /*
     * Génère un nombre pseudo-aléatoire basé sur la seed actuelle.
     * Assure que la séquence est reproductible.
     */
    int GenererNombrePseudoAleatoire(int max) {
        seedNumber = (seedNumber * 1664525 + 1013904223);
        long resultat = seedNumber;
        if (resultat < 0) {
            resultat = resultat * -1;
        }
        return (int) (resultat % max);
    }

    /*
     * Convertit la chaîne de caractères de la seed en un nombre (long) utilisable mathématiquement.
     */
    long StringToLong(String s) {
        long h = 0;
        for (int i = 0; i < length(s); i += 1) {
            h = 31 * h + charAt(s, i);
        }
        return h;
    }

    /*
     * Crée une seed aléatoire (chaine de caractères) si le joueur choisit "Nouvelle Partie".
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
     * Gère les 25 tours de jeu : pioche, affichage, placement, puis résultats finaux.
     */
    void LancerJeu() {
        Carte[] paquet = CreeNouveauJeu();
        Melanger(paquet);
        Carte[][] grille = new Carte[5][5];

        for (int tour = 0; tour < 25; tour += 1) {
            Carte cActuelle = paquet[tour];
            Carte cSuivante = (tour < 24) ? paquet[tour+1] : null;

            AfficherEcranJeu(grille, cActuelle, cSuivante);
            println("\n\n\n");
            PlacerCarte(grille, cActuelle);
        }

        AfficherEcranResultat(grille);
        
        println("\n=== FIN DE PARTIE ===");
        println("Seed de cette partie : " + seedActuelle);
        println("Appuyez sur Entrée pour revenir au menu...");
        readString();
    }

    // ========================================================================================================================
    // AFFICHAGE
    // ========================================================================================================================

    /*
     * Affiche l'écran d'accueil à partir du fichier texte correspondant.
     */
    void AfficherPageAccueuil() {
        String[] ecran = LireTemplate("visuel/acceuil.txt");
        RendreBuffer(ecran);
    }

    /*
     * Construit et affiche l'interface principale du jeu (Grille + HUD).
     */
    void AfficherEcranJeu(Carte[][] grille, Carte cActuelle, Carte cSuivante) {
        clear();
        String[] ecran = LireTemplate("visuel/jeu.txt");
        int startLigne = 7; 
        int startCol = 4;

        for (int i = 0; i < 5; i += 1) { 
            for (int j = 0; j < 5; j += 1) { 
                if (grille[i][j] != null) {
                    int l = startLigne + (i * 7); 
                    int c = startCol + (j * 10);
                    DessinerCarteGrande(ecran, l, c, grille[i][j]);
                }
            }
        }

        if (cActuelle != null) { EcrireDansBuffer(ecran, 45, 22, NomCarte(cActuelle)); }
        if (cSuivante != null) { EcrireDansBuffer(ecran, 45, 75, NomCarte(cSuivante)); }

        RendreBuffer(ecran);
    }

    /*
     * Construit et affiche l'écran de fin avec le détail des scores.
     */
    void AfficherEcranResultat(Carte[][] grille) {
        clear();
        String[] ecran = LireTemplate("visuel/resultat.txt");
        int startLigne = 7;
        int startCol = 4;
        
        // Affichage des cartes
        for (int i = 0; i < 5; i += 1) {
            for (int j = 0; j < 5; j += 1) {
                if (grille[i][j] != null) {
                    int l = startLigne + (i * 7); 
                    int c = startCol + (j * 10);
                    DessinerCarteGrande(ecran, l, c, grille[i][j]);
                }
            }
        }

        int scoreTotal = 0;

        // Scores Lignes
        for (int i = 0; i < 5; i += 1) {
            int score = CalculerScoreLigne(grille, i);
            scoreTotal = scoreTotal + score;
            int l = startLigne + (i * 7);
            EcrireDansBuffer(ecran, l + 3, 60, "" + score);
        }

        // Scores Colonnes
        for (int j = 0; j < 5; j += 1) {
            int score = CalculerScoreColonne(grille, j);
            scoreTotal = scoreTotal + score;
            int col = 8 + (j * 10);
            EcrireDansBuffer(ecran, 43, col, "" + score);
        }

        // Total
        EcrireDansBuffer(ecran, 43, 68, "" + scoreTotal);
        RendreBuffer(ecran);
    }

    /*
     * Dessine les caractères ASCII d'une carte (Valeur et Symbole) à une position donnée.
     */
    void DessinerCarteGrande(String[] ecran, int l, int c, Carte card) {
        String val = LISTE_VALEUR[card.num];
        String sym = LISTE_COULEUR[card.couleur];
        
        EcrireDansBuffer(ecran, l + 1, c + 2, val);
        EcrireDansBuffer(ecran, l + 3, c + 4, sym);
        
        int decalage = (length(val) == 2) ? 5 : 6;
        EcrireDansBuffer(ecran, l + 5, c + decalage, val);
    }

    // ========================================================================================================================
    // LOGIQUE METIER (SCORES)
    // ========================================================================================================================
    
    /*
     * Extrait une ligne de la grille et calcule son score.
     */
    int CalculerScoreLigne(Carte[][] grille, int ligneIdx) {
        return CalculerPointsMain(grille[ligneIdx]);
    }

    /*
     * Extrait une colonne de la grille et calcule son score.
     */
    int CalculerScoreColonne(Carte[][] grille, int colIdx) {
        Carte[] col = new Carte[5];
        for(int i=0; i<5; i+=1) {
            col[i] = grille[i][colIdx];
        }
        return CalculerPointsMain(col);
    }

    /*
     * Fonction clé : Analyse une main de 5 cartes et retourne les points selon le barème américain.
     */
    int CalculerPointsMain(Carte[] main) {
        Carte[] triee = CopierTableau(main);
        TrierMain(triee);

        boolean flush = EstCouleur(triee);
        boolean straight = EstSuite(triee);
        int[] counts = CompterValeurs(triee); 

        boolean carre = false;
        boolean brelan = false;
        int paires = 0;

        for (int i = 0; i < length(counts); i+=1) {
            if (counts[i] == 4) { carre = true; }
            if (counts[i] == 3) { brelan = true; }
            if (counts[i] == 2) { paires += 1; }
        }

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
     * Vérifie si les 5 cartes sont de la même couleur (Flush).
     */
    boolean EstCouleur(Carte[] m) {
        int c = m[0].couleur;
        for(int i=1; i<5; i+=1) {
            if (m[i].couleur != c) { return false; }
        }
        return true;
    }

    /*
     * Vérifie si les 5 cartes se suivent numériquement (Suite).
     * Suppose que la main est déjà triée.
     */
    boolean EstSuite(Carte[] m) {
        for(int i=0; i<4; i+=1) {
            if (m[i+1].num != m[i].num + 1) { return false; }
        }
        return true;
    }

    /*
     * Compte le nombre d'occurrences de chaque valeur de carte (Histogramme).
     */
    int[] CompterValeurs(Carte[] m) {
        int[] c = new int[15];
        for(int i=0; i<5; i+=1) {
            c[m[i].num] += 1;
        }
        return c;
    }

    /*
     * Trie les cartes par valeur croissante (Bubble Sort).
     */
    void TrierMain(Carte[] t) {
        for(int i=0; i<length(t)-1; i+=1) {
            for(int j=0; j<length(t)-i-1; j+=1) {
                if (t[j].num > t[j+1].num) {
                    Carte temp = t[j]; t[j]=t[j+1]; t[j+1]=temp;
                }
            }
        }
    }

    /*
     * Crée une copie d'un tableau de cartes pour manipulation sans effet de bord.
     */
    Carte[] CopierTableau(Carte[] source) {
        Carte[] destination = new Carte[length(source)];
        for(int i=0; i<length(source); i+=1) {
            destination[i] = source[i];
        }
        return destination;
    }

    // ========================================================================================================================
    // OUTILS DE RENDU TEXTE
    // ========================================================================================================================
    
    /*
     * Modifie un buffer de texte (tableau de String) à des coordonnées précises.
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
     * Affiche le buffer final à l'écran en interprétant les codes couleurs.
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
     * Lit un fichier texte complet et le renvoie sous forme de tableau de lignes.
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
     * Affiche les règles du jeu.
     */
    void AfficherRegles() {
        clear();
        String[] regles = LireTemplate("visuel/regle.md");
        RendreBuffer(regles);
        println("\nAppuyez sur Entrée pour revenir au menu...");
        readString();
    }

    // ========================================================================================================================
    // SAISIE ET MANIPULATION JEU
    // ========================================================================================================================
    
    /*
     * Gère la saisie sécurisée des coordonnées par l'utilisateur.
     * Vérifie les limites et la disponibilité de la case.
     */
    void PlacerCarte(Carte[][] grille, Carte c) {
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
     * Génère un jeu de 52 cartes standard ordonné.
     */
    Carte[] CreeNouveauJeu() {
        Carte[] p = new Carte[52];
        int idx = 0;
        for (int c=0; c<4; c+=1) {
            for (int v=2; v<=14; v+=1) {
                Carte n = new Carte(); n.couleur=c; n.num=v; p[idx]=n; idx+=1;
            }
        }
        return p;
    }

    /*
     * Mélange le paquet de cartes en utilisant le générateur pseudo-aléatoire (Seed).
     */
    void Melanger(Carte[] p) {
        for (int i=0; i<length(p); i+=1) {
            int r = GenererNombrePseudoAleatoire(length(p)); 
            Carte t=p[i]; 
            p[i]=p[r]; 
            p[r]=t;
        }
    }

    /*
     * Renvoie le nom court d'une carte (ex: "10♥").
     */
    String NomCarte(Carte c) {
        return LISTE_VALEUR[c.num] + LISTE_COULEUR[c.couleur];
    }

    /*
     * Efface la console.
     */
    void clear() {
        print("\u001B[H\u001B[2J");
    }

    // ========================================================================================================================
    // TESTS UNITAIRES
    // ========================================================================================================================
    
    // pour créer des cartes rapidement dans les tests
    Carte newCarte(int valeur, int coul) {
        Carte c = new Carte(); c.num = valeur; c.couleur = coul; return c;
    }

    /*
     * Teste le calcul des points d'une main (return int).
     */
    void testCalculerPointsMain() {
        // Test 1: Quinte Flush Royale
        Carte[] royal = new Carte[]{ newCarte(10,0), newCarte(V,0), newCarte(D,0), newCarte(R,0), newCarte(A,0) };
        assertEquals(PTS_ROYAL_FLUSH, CalculerPointsMain(royal));
        
        // Test 2: Double Paire
        Carte[] dpaire = new Carte[]{ newCarte(2,0), newCarte(2,1), newCarte(5,2), newCarte(5,3), newCarte(A,0) };
        assertEquals(PTS_DOUBLE_PAIRE, CalculerPointsMain(dpaire));
    }

    /*
     * Teste la détection de couleur (return boolean).
     */
    void testEstCouleur() {
        // Test 1: Vrai
        Carte[] oui = new Carte[]{ newCarte(2,0), newCarte(5,0), newCarte(7,0), newCarte(9,0), newCarte(R,0) };
        assertTrue(EstCouleur(oui));

        // Test 2: Faux
        Carte[] non = new Carte[]{ newCarte(2,0), newCarte(5,0), newCarte(7,1), newCarte(9,0), newCarte(R,0) };
        assertFalse(EstCouleur(non));
    }

    /*
     * Teste la détection de suite (return boolean).
     */
    void testEstSuite() {
        // Test 1: Vrai (2,3,4,5,6)
        Carte[] oui = new Carte[]{ newCarte(2,0), newCarte(3,1), newCarte(4,0), newCarte(5,2), newCarte(6,0) };
        assertTrue(EstSuite(oui));
        
        // Test 2: Faux
        Carte[] non = new Carte[]{ newCarte(2,0), newCarte(3,1), newCarte(4,0), newCarte(5,2), newCarte(7,0) };
        assertFalse(EstSuite(non));
    }

    /*
     * Teste le générateur de nombres aléatoires (return int).
     * On vérifie le déterminisme (même seed = même résultat).
     */
    void testGenererNombrePseudoAleatoire() {
        // Test 1: Vérifie que le résultat est dans les bornes [0, max[
        seedNumber = 12345;
        int res = GenererNombrePseudoAleatoire(100);
        assertTrue(res >= 0 && res < 100);

        // Test 2: Déterminisme (reset seed -> même résultat)
        seedNumber = 12345;
        int res1 = GenererNombrePseudoAleatoire(50);
        seedNumber = 12345; // Reset
        int res2 = GenererNombrePseudoAleatoire(50);
        assertEquals(res1, res2);
    }

    /*
     * Teste la conversion String -> Long (return long).
     */
    void testStringToLong() {
        // Test 1: Chaine vide
        assertEquals(0, StringToLong(""));

        // Test 2: Chaine simple
        // 'A' = 65. Hash = 31*0 + 65 = 65.
        assertEquals(65, StringToLong("A"));
    }

    /*
     * Teste la génération de la seed texte (return String).
     */
    void testGenererRandomSeedString() {
        // Test 1: Longueur demandée respectée
        String s1 = GenererRandomSeedString(10);
        assertEquals(10, length(s1));

        // Test 2: Autre longueur
        String s2 = GenererRandomSeedString(5);
        assertEquals(5, length(s2));
    }

    /*
     * Teste le calcul du score d'une ligne (return int).
     */
    void testCalculerScoreLigne() {
        Carte[][] grille = new Carte[5][5];
        // Remplir ligne 0 avec un Brelan de 2
        grille[0][0] = newCarte(2,0); grille[0][1] = newCarte(2,1); grille[0][2] = newCarte(2,2);
        grille[0][3] = newCarte(5,0); grille[0][4] = newCarte(7,1);
        
        // Test 1: Score correct
        assertEquals(PTS_BRELAN, CalculerScoreLigne(grille, 0));

        // Test 2: Ligne vide
        // On teste une ligne "Carte Haute" (0 pts)
        grille[1][0] = newCarte(2,0); grille[1][1] = newCarte(4,1); grille[1][2] = newCarte(6,2);
        grille[1][3] = newCarte(8,0); grille[1][4] = newCarte(10,1);
        assertEquals(0, CalculerScoreLigne(grille, 1));
    }

    /*
     * Teste le calcul du score d'une colonne (return int).
     */
    void testCalculerScoreColonne() {
        Carte[][] grille = new Carte[5][5];
        // Remplir col 0 avec une Couleur
        for(int i=0; i<5; i+=1) grille[i][0] = newCarte(i+2, 0); // Tout couleur 0
        // Casser la suite potentielle (2,3,4,5,6 est une Straight Flush !)
        grille[4][0] = newCarte(10, 0); 
        
        // Test 1
        assertEquals(PTS_FLUSH, CalculerScoreColonne(grille, 0));

        // Test 2: Col 1 avec Paire
        grille[0][1] = newCarte(3,0); grille[1][1] = newCarte(3,1); 
        grille[2][1] = newCarte(5,2); grille[3][1] = newCarte(6,3); grille[4][1] = newCarte(7,0);
        assertEquals(PTS_PAIRE, CalculerScoreColonne(grille, 1));
    }

    /*
     * Teste le comptage des valeurs (return int[]).
     */
    void testCompterValeurs() {
        Carte[] main = new Carte[]{ newCarte(2,0), newCarte(2,1), newCarte(5,0), newCarte(5,1), newCarte(5,2) }; // Full
        int[] counts = CompterValeurs(main);
        
        // Test 1: Deux cartes de valeur 2
        assertEquals(2, counts[2]);
        // Test 2: Trois cartes de valeur 5
        assertEquals(3, counts[5]);
    }

    /*
     * Teste la copie de tableau (return Carte[]).
     */
    void testCopierTableau() {
        Carte[] source = new Carte[]{ newCarte(1,0), newCarte(2,0) };
        Carte[] destination = CopierTableau(source);
        
        // Test 1: Même taille
        assertEquals(length(source), length(destination));
        
        // Test 2: Indépendance (modifier copie n'affecte pas source)
        destination[0].num = 99;
        // Si on veut tester que le TABLEAU est nouveau :
        source[0] = newCarte(5,5); 
        // destination[0] pointe toujours vers l'ancien objet 1,0 ? Non, destination[0] pointe vers l'objet initial.
        // source[0] pointe vers un NOUVEL objet. Donc destination[0] ne doit pas changer.
        assertTrue(destination[0].num != 5);
    }

    /*
     * Teste la création d'un paquet (return Carte[]).
     */
    void testCreeNouveauJeu() {
        Carte[] p = CreeNouveauJeu();
        
        // Test 1: Taille 52
        assertEquals(52, length(p));
        
        // Test 2: Première carte est 2 (val=2) de couleur 0
        assertEquals(2, p[0].num);
        assertEquals(0, p[0].couleur);
    }

    /*
     * Teste le nommage des cartes (return String).
     */
    void testNomCarte() {
        // Test 1: Dame de Coeur (12, 1)
        Carte c1 = newCarte(12, 1);
        assertEquals("D♥", NomCarte(c1));

        // Test 2: 10 de Pique (10, 3)
        Carte c2 = newCarte(10, 3);
        assertEquals("10♠", NomCarte(c2));
    }
}