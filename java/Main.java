import extensions.File;
import extensions.CSVFile;

class Main extends Program {   
    // ========================================================================================================================
    // CONSTANTES
    // ========================================================================================================================
    final int V = 11; 
    final int D = 12; 
    final int R = 13; 
    final int A = 14;
    final int NB_JOKERS_MAX = 15;

    final String[] LISTE_COULEUR = new String[]{"♦", "♥", "♣", "♠"};
    final String[] LISTE_VALEUR = new String[]{"", "", "2", "3", "4", "5", "6", "7", "8", "9", "10", "V", "D", "R", "A"};

    // BAREME POINTS
    final int PTS_ROYAL_FLUSH = 100;
    final int PTS_STRAIGHT_FLUSH = 75;
    final int PTS_CARRE = 50;
    final int PTS_FLUSH = 20;     
    final int PTS_STRAIGHT = 15;  
    final int PTS_FULL = 10;
    final int PTS_BRELAN = 10;
    final int PTS_DOUBLE_PAIRE = 5;
    final int PTS_PAIRE = 2;

    // CODES COULEURS
    final String ANSI_RED = "\u001B[31m";
    final String ANSI_GREEN = "\u001B[32m";
    final String ANSI_RESET = "\u001B[0m";

    // GLOBALES
    String seedActuelle; 
    long seedNumber; 
    Question[] baseDeQuestions;
    
    // Variables pour l'affichage persistant dans jeu.txt
    String derniereReponseJuste = "";
    String derniereReponseJoueur = "";
    String derniereExplication = ""; // Nouvelle variable pour l'explication

    // ========================================================================================================================
    // ALGORITHME PRINCIPAL
    // ========================================================================================================================
    void algorithm() {
        ChargerQuestions(); 
        boolean continuer = true;
        while(continuer) {
            clear();
            AfficherPageAccueuil(); 
            
            println("\n=== MENU PRINCIPAL ===");
            println("1. Lire les règles");
            println("2. Nouvelle Partie (Aléatoire)");
            println("3. Charger une Partie (Via Seed)");
            println("4. Quitter");
            
            int choix = lireEntier("Votre choix : ", 1, 5);

            if (choix == 1) {
                AfficherRegles();
            } else if (choix == 2) {
                String seed = GenererRandomSeedString(10);
                LancerPartieAvecSeed(seed);
            } else if (choix == 3) {
                print("Entrez la seed : ");
                String seed = readString();
                LancerPartieAvecSeed(seed);
            } else if (choix == 4) {
                continuer = false;
                println("Au revoir !");
            }
        }
    }

    // ========================================================================================================================
    // LOGIQUE JEU
    // ========================================================================================================================

    void LancerJeu() {
        Carte[] paquet = CreeNouveauJeu();
        Melanger(paquet); 
        Carte[][] grille = new Carte[5][5];

        int idxPaquet = 0;      
        int cartesPosees = 0;   
        int jokers = NB_JOKERS_MAX; 
        boolean peutPasser = true; 
        
        // Reset des affichages pour la nouvelle partie
        derniereReponseJuste = "";
        derniereReponseJoueur = "";
        derniereExplication = "";
        String messageInfo = "";

        while (cartesPosees < 25 && idxPaquet < 52) {
            Carte cActuelle = paquet[idxPaquet];
            Carte cSuivante = (idxPaquet < 51) ? paquet[idxPaquet+1] : null;

            boolean tourJoue = false;
            while (!tourJoue) {
                // Affichage de l'écran avec les infos à jour
                AfficherEcranJeu(grille, cActuelle, cSuivante, messageInfo, jokers);
                
                // On efface le message informatif après l'avoir affiché une fois pour ne pas qu'il reste si on ne fait rien
                if (length(messageInfo) > 0) messageInfo = "";

                println("\n=== TOUR " + (cartesPosees+1) + "/25 ===");
                
                // --- DEMANDE DE LIGNE ---
                int lig = lireCoordonneeOuJoker("Ligne (1-5) ou 'J' : ");

                if (lig == -3) {
                    // Erreur hors limites (ex: le joueur a tapé 6)
                    messageInfo = ANSI_RED + ">> ERREUR : Ligne hors limites (1-5) !" + ANSI_RESET;
                }
                else if (lig == -1) {
                    // Erreur de saisie (ex: lettres)
                    messageInfo = ANSI_RED + ">> ERREUR : Saisie invalide !" + ANSI_RESET;
                }
                else if (lig == -2) { // JOKER
                    if (jokers > 0 && peutPasser) {
                        boolean gagne = PoserQuestion(); 
                        if (gagne) {
                            messageInfo = ANSI_GREEN + ">> CORRECT ! Carte défaussée." + ANSI_RESET;
                            jokers--;
                            peutPasser = false;
                            idxPaquet++;
                            tourJoue = true; 
                            continue; 
                        } else {
                            messageInfo = ANSI_RED + ">> ERREUR ! Vous devez jouer la carte." + ANSI_RESET;
                            peutPasser = false; 
                        }
                    } else {
                        messageInfo = ANSI_RED + ">> Joker impossible (épuisé ou déjà utilisé) !" + ANSI_RESET;
                    }
                } 
                else if (lig >= 0) { // COORDONNÉE VALIDE
                    
                    // --- DEMANDE DE COLONNE ---
                    int col = lireCoordonneeOuJoker("Colonne (1-5) ou 'J' : ");
                    
                    if (col == -3) {
                         messageInfo = ANSI_RED + ">> ERREUR : Colonne hors limites (1-5) !" + ANSI_RESET;
                    }
                    else if (col == -1) {
                         messageInfo = ANSI_RED + ">> ERREUR : Saisie invalide !" + ANSI_RESET;
                    }
                    else if (col == -2) { // JOKER (Même logique)
                        if (jokers > 0 && peutPasser) {
                            boolean gagne = PoserQuestion();
                            if (gagne) {
                                messageInfo = ANSI_GREEN + ">> CORRECT ! Carte défaussée." + ANSI_RESET;
                                jokers--;
                                peutPasser = false;
                                idxPaquet++;
                                tourJoue = true;
                                continue;
                            } else {
                                messageInfo = ANSI_RED + ">> ERREUR ! Vous devez jouer la carte." + ANSI_RESET;
                                peutPasser = false;
                            }
                        } else {
                            messageInfo = ANSI_RED + ">> Joker impossible !" + ANSI_RESET;
                        }
                    } 
                    else if (col >= 0) { // COORDONNÉES COMPLÈTES (Ligne et Colonne valides)
                        if (grille[lig][col] == null) {
                            grille[lig][col] = cActuelle;
                            cartesPosees++;
                            idxPaquet++;
                            peutPasser = true; 
                            tourJoue = true;
                        } else {
                            messageInfo = ANSI_RED + ">> ERREUR : Case occupée !" + ANSI_RESET;
                        }
                    }
                }
            }
        }

        AfficherEcranResultat(grille);
        println("\n=== FIN DE PARTIE ===");
        println("Seed : " + seedActuelle);
        println("Appuyez sur Entrée...");
        readString();
    }

    // ========================================================================================================================
    // OUTILS DE SAISIE
    // ========================================================================================================================
    
    // Retourne 0-4 pour une coordonnée, -2 pour Joker, -1 pour invalide, -3 pour Hors Limites
    int lireCoordonneeOuJoker(String message) {
        print(message);
        String s = readString();
        
        // Détection Joker
        if (equals(s, "j") || equals(s, "J")) {
            return -2;
        }
        
        // Détection Chiffre
        if (estUnNombre(s)) {
            int val = stringToInt(s);
            if (val >= 1 && val <= 5) {
                return val - 1; // On renvoie l'index (0-4)
            } else {
                return -3; // Code spécial pour "Hors limites"
            }
        }
        return -1; // Invalide
    }

    // ========================================================================================================================
    // SYSTÈME DE QUESTIONS
    // ========================================================================================================================

    void ChargerQuestions() {
        CSVFile f = loadCSV("visuel/questions.csv");
        int nb = rowCount(f);
        baseDeQuestions = new Question[nb];
        for(int i=0; i<nb; i+=1) {
            Question q = new Question();
            q.enonce = getCell(f, i, 0);
            q.repA = getCell(f, i, 1);
            q.repB = getCell(f, i, 2);
            q.repC = getCell(f, i, 3);
            q.repD = getCell(f, i, 4);
            q.bonneReponse = getCell(f, i, 5);
            q.explication = getCell(f, i, 6);
            baseDeQuestions[i] = q;
        }
        println(nb + " questions chargées.");
    }

    boolean PoserQuestion() {
        clear();
        int idx = GenererNombrePseudoAleatoire(length(baseDeQuestions));
        Question q = baseDeQuestions[idx];

        println("================= QUESTION BONUS =================");
        println(q.enonce);
        println("--------------------------------------------------");
        println("A) " + q.repA);
        println("B) " + q.repB);
        println("C) " + q.repC);
        println("D) " + q.repD);
        println("--------------------------------------------------");
        
        String rep = "";
        boolean valid = false;
        while(!valid) {
            print("Votre réponse (A/B/C/D) : ");
            rep = readString();
            if (length(rep) == 1) valid = true;
        }
        
        // Mise à jour des variables globales pour l'affichage dans jeu.txt
        derniereReponseJoueur = toUpperCases(rep);
        derniereReponseJuste = q.bonneReponse;
        derniereExplication = q.explication; // On sauvegarde l'explication

        boolean correct = false;
        if (equals(derniereReponseJoueur, derniereReponseJuste)) correct = true;

        if (correct) {
            println(ANSI_GREEN + "BONNE RÉPONSE !" + ANSI_RESET);
        } else {
            println(ANSI_RED + "MAUVAISE RÉPONSE..." + ANSI_RESET);
            println("La bonne réponse était : " + q.bonneReponse);
            println("Explication : " + q.explication);
        }
        println("Appuyez sur Entrée pour revenir au jeu...");
        readString();
        return correct;
    }

    String toUpperCases(String s) {
        String res = "";
        for (int i=0; i<length(s); i++) {
            char c = charAt(s, i);
            if (c >= 'a' && c <= 'z') {
                res = res + (char)(c - 32);
            } else {
                res = res + c;
            }
        }
        return res;
    }

    // ========================================================================================================================
    // AFFICHAGE
    // ========================================================================================================================
    
    void AfficherEcranJeu(Carte[][] grille, Carte cActuelle, Carte cSuivante, String message, int nbJokers) {
        clear();
        String[] ecran = LireTemplate("visuel/jeu.txt");
        int startLigne = 7; 
        int startCol = 4;

        // 1. Dessin de la grille
        for (int i = 0; i < 5; i += 1) { 
            for (int j = 0; j < 5; j += 1) { 
                if (grille[i][j] != null) {
                    int l = startLigne + (i * 7); 
                    int c = startCol + (j * 10);
                    DessinerCarteGrande(ecran, l, c, grille[i][j]);
                }
            }
        }

        // 2. Affichage des cartes courantes
        if (cActuelle != null) { EcrireDansBuffer(ecran, 45, 21, NomCarte(cActuelle)); }
        if (cSuivante != null) { EcrireDansBuffer(ecran, 45, 49, NomCarte(cSuivante)); }
        
        // 3. Affichage du nombre de Jokers (Case COMMANDES)
        String jokerTxt = "(" + nbJokers + "/" + NB_JOKERS_MAX + ")";
        EcrireDansBuffer(ecran, 34, 117, jokerTxt);

        // 4. Affichage du CADRE "DERNIERE QUESTION"
        if (length(derniereReponseJuste) > 0) {
            EcrireDansBuffer(ecran, 39, 85, derniereReponseJuste);
        }
        if (length(derniereReponseJoueur) > 0) {
            EcrireDansBuffer(ecran, 40, 85, derniereReponseJoueur);
        }
        // Explication à la Ligne 42, Colonne 67 (comme demandé)
        if (length(derniereExplication) > 0) {
            EcrireDansBuffer(ecran, 41, 66, derniereExplication);
        }

        // 5. Affichage du message d'information/erreur en COULEUR
        // Ligne 46, Colonne 67 (comme demandé)
        if (length(message) > 0) {
            EcrireDansBuffer(ecran, 45, 67, message);
        }

        RendreBuffer(ecran);
    }
    
    // ... [Reste des fonctions utilitaires inchangées] ...

    // Outils de base
    int lireEntier(String message, int min, int max) {
        int val = -1; boolean valide = false;
        while (!valide) {
            print(message); String s = readString();
            if (estUnNombre(s)) {
                val = stringToInt(s);
                if (val >= min && val <= max) valide = true; 
                else println(">> Erreur : Hors limites");
            } else println(">> Erreur : Nombre attendu");
        }
        return val;
    }

    boolean estUnNombre(String s) {
        if (length(s) == 0) return false;
        for(int i=0; i<length(s); i+=1) {
            char c = charAt(s, i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    void LancerPartieAvecSeed(String seed) {
        seedActuelle = seed;
        seedNumber = StringToLong(seed);
        println("Initialisation avec Seed : " + seedActuelle);
        LancerJeu();
    }

    int GenererNombrePseudoAleatoire(int max) {
        if (max <= 0) return 0;
        seedNumber = (seedNumber * 1664525 + 1013904223);
        long resultat = seedNumber;
        if (resultat < 0) { resultat = resultat * -1; }
        return (int) (resultat % max);
    }

    long StringToLong(String s) {
        long h = 0;
        for (int i = 0; i < length(s); i += 1) { h = 31 * h + charAt(s, i); }
        return h;
    }

    String GenererRandomSeedString(int longueur) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for(int i=0; i < longueur; i += 1) {
            int idx = (int)(random() * length(chars)); 
            res = res + charAt(chars, idx);
        }
        return res;
    }

    void AfficherEcranResultat(Carte[][] grille) {
        clear();
        String[] ecran = LireTemplate("visuel/resultat.txt");
        int startLigne = 7; int startCol = 4;
        for (int i = 0; i < 5; i += 1) {
            for (int j = 0; j < 5; j += 1) {
                if (grille[i][j] != null) {
                    int l = startLigne + (i * 7); int c = startCol + (j * 10);
                    DessinerCarteGrande(ecran, l, c, grille[i][j]);
                }
            }
        }
        int scoreTotal = 0;
        for (int i = 0; i < 5; i += 1) {
            int score = CalculerScoreLigne(grille, i);
            scoreTotal += score;
            int l = startLigne + (i * 7);
            EcrireDansBuffer(ecran, l + 3, 60, "" + score);
        }
        for (int j = 0; j < 5; j += 1) {
            int score = CalculerScoreColonne(grille, j);
            scoreTotal += score;
            int col = 8 + (j * 10);
            EcrireDansBuffer(ecran, 43, col, "" + score);
        }
        EcrireDansBuffer(ecran, 43, 68, "" + scoreTotal);
        RendreBuffer(ecran);
    }

    void DessinerCarteGrande(String[] ecran, int l, int c, Carte card) {
        String val = LISTE_VALEUR[card.num];
        String sym = LISTE_COULEUR[card.couleur];
        EcrireDansBuffer(ecran, l + 1, c + 2, val);
        EcrireDansBuffer(ecran, l + 3, c + 4, sym);
        int decalage = (length(val) == 2) ? 5 : 6;
        EcrireDansBuffer(ecran, l + 5, c + decalage, val);
    }

    int CalculerScoreLigne(Carte[][] grille, int ligneIdx) { return CalculerPointsMain(grille[ligneIdx]); }
    int CalculerScoreColonne(Carte[][] grille, int colIdx) {
        Carte[] col = new Carte[5];
        for(int i=0; i<5; i+=1) { col[i] = grille[i][colIdx]; }
        return CalculerPointsMain(col);
    }

    int CalculerPointsMain(Carte[] main) {
        Carte[] triee = CopierTableau(main);
        TrierMain(triee);
        boolean flush = EstCouleur(triee);
        boolean straight = EstSuite(triee);
        int[] counts = CompterValeurs(triee); 
        boolean carre = false; boolean brelan = false; int paires = 0;
        for (int i = 0; i < length(counts); i+=1) {
            if (counts[i] == 4) { carre = true; }
            if (counts[i] == 3) { brelan = true; }
            if (counts[i] == 2) { paires += 1; }
        }
        if (flush && straight) { return (triee[0].num == 10) ? PTS_ROYAL_FLUSH : PTS_STRAIGHT_FLUSH; }
        if (carre) return PTS_CARRE;
        if (flush) return PTS_FLUSH;
        if (straight) return PTS_STRAIGHT;
        if (brelan && paires >= 1) return PTS_FULL;
        if (brelan) return PTS_BRELAN;
        if (paires == 2) return PTS_DOUBLE_PAIRE;
        if (paires == 1) return PTS_PAIRE;
        return 0;
    }

    boolean EstCouleur(Carte[] m) {
        int c = m[0].couleur;
        for(int i=1; i<5; i+=1) if (m[i].couleur != c) return false;
        return true;
    }

    boolean EstSuite(Carte[] m) {
        for(int i=0; i<4; i+=1) if (m[i+1].num != m[i].num + 1) return false;
        return true;
    }

    int[] CompterValeurs(Carte[] m) {
        int[] c = new int[15];
        for(int i=0; i<5; i+=1) c[m[i].num] += 1;
        return c;
    }

    void TrierMain(Carte[] t) {
        for(int i=0; i<length(t)-1; i+=1)
            for(int j=0; j<length(t)-i-1; j+=1)
                if (t[j].num > t[j+1].num) { Carte temp = t[j]; t[j]=t[j+1]; t[j+1]=temp; }
    }

    Carte[] CopierTableau(Carte[] src) {
        Carte[] dest = new Carte[length(src)];
        for(int i=0; i<length(src); i+=1) dest[i] = src[i];
        return dest;
    }

    void EcrireDansBuffer(String[] ecran, int l, int c, String texte) {
        if (l >= length(ecran)) return;
        String ligne = ecran[l];
        if (c + length(texte) > length(ligne)) return;
        ecran[l] = substring(ligne, 0, c) + texte + substring(ligne, c + length(texte), length(ligne));
    }

    void RendreBuffer(String[] ecran) {
        print(WHITE + BG_BLACK);
        for (int i = 0; i < length(ecran); i += 1) {
            String ligne = ecran[i];
            for (int j = 0; j < length(ligne); j += 1) {
                char c = charAt(ligne, j);
                if (c == '¤') print(BG_WHITE + " " + BG_BLACK);
                else print(c);
            }
            println(); 
        }
        print(RESET);
    }

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

    void AfficherPageAccueuil() {
        String[] ecran = LireTemplate("visuel/acceuil.txt");
        RendreBuffer(ecran);
    }

    void AfficherRegles() {
        clear();
        String[] regles = LireTemplate("visuel/regle.md");
        RendreBuffer(regles);
        println("\nAppuyez sur Entrée pour revenir au menu...");
        readString();
    }

    void PlacerCarte(Carte[][] grille, Carte c) {
        // Fonction gardée pour compatibilité mais non utilisée dans la nouvelle logique
    }

    Carte[] CreeNouveauJeu() {
        Carte[] p = new Carte[52];
        int idx = 0;
        for (int c=0; c<4; c+=1)
            for (int v=2; v<=14; v+=1) {
                Carte n = new Carte(); n.couleur=c; n.num=v; p[idx]=n; idx+=1;
            }
        return p;
    }

    void Melanger(Carte[] p) {
        for (int i=0; i<length(p); i+=1) {
            int r = GenererNombrePseudoAleatoire(length(p)); 
            Carte t=p[i]; p[i]=p[r]; p[r]=t;
        }
    }

    String NomCarte(Carte c) { return LISTE_VALEUR[c.num] + LISTE_COULEUR[c.couleur]; }
    void clear() { print("\u001B[H\u001B[2J"); }

    // ========================================================================================================================
    // TESTS UNITAIRES
    // ========================================================================================================================
    Carte newCarte(int v, int c) { Carte a=new Carte(); a.num=v; a.couleur=c; return a; }

    void testCalculerPointsMain() {
        Carte[] royal = new Carte[]{ newCarte(10,0), newCarte(V,0), newCarte(D,0), newCarte(R,0), newCarte(A,0) };
        assertEquals(PTS_ROYAL_FLUSH, CalculerPointsMain(royal));
        Carte[] dpaire = new Carte[]{ newCarte(2,0), newCarte(2,1), newCarte(5,2), newCarte(5,3), newCarte(A,0) };
        assertEquals(PTS_DOUBLE_PAIRE, CalculerPointsMain(dpaire));
    }

    void testEstCouleur() {
        Carte[] oui = new Carte[]{ newCarte(2,0), newCarte(5,0), newCarte(7,0), newCarte(9,0), newCarte(R,0) };
        assertTrue(EstCouleur(oui));
        Carte[] non = new Carte[]{ newCarte(2,0), newCarte(5,0), newCarte(7,1), newCarte(9,0), newCarte(R,0) };
        assertFalse(EstCouleur(non));
    }

    void testEstSuite() {
        Carte[] oui = new Carte[]{ newCarte(2,0), newCarte(3,1), newCarte(4,0), newCarte(5,2), newCarte(6,0) };
        assertTrue(EstSuite(oui));
        Carte[] non = new Carte[]{ newCarte(2,0), newCarte(3,1), newCarte(4,0), newCarte(5,2), newCarte(7,0) };
        assertFalse(EstSuite(non));
    }

    void testGenererNombrePseudoAleatoire() {
        seedNumber = 12345;
        int res1 = GenererNombrePseudoAleatoire(100);
        assertTrue(res1 >= 0 && res1 < 100);
        seedNumber = 12345;
        int res2 = GenererNombrePseudoAleatoire(100);
        assertEquals(res1, res2);
    }

    void testStringToLong() {
        assertEquals(0, StringToLong(""));
        assertEquals(65, StringToLong("A"));
    }

    void testGenererRandomSeedString() {
        String s1 = GenererRandomSeedString(10);
        assertEquals(10, length(s1));
        String s2 = GenererRandomSeedString(5);
        assertEquals(5, length(s2));
    }

    void testCalculerScoreLigne() {
        Carte[][] grille = new Carte[5][5];
        grille[0][0] = newCarte(2,0); grille[0][1] = newCarte(2,1); grille[0][2] = newCarte(2,2);
        grille[0][3] = newCarte(5,0); grille[0][4] = newCarte(7,1);
        assertEquals(PTS_BRELAN, CalculerScoreLigne(grille, 0));
        grille[1][0] = newCarte(2,0); grille[1][1] = newCarte(4,1); grille[1][2] = newCarte(6,2);
        grille[1][3] = newCarte(8,0); grille[1][4] = newCarte(10,1);
        assertEquals(0, CalculerScoreLigne(grille, 1));
    }

    void testCalculerScoreColonne() {
        Carte[][] grille = new Carte[5][5];
        for(int i=0; i<5; i+=1) grille[i][0] = newCarte(i+2, 0);
        grille[4][0] = newCarte(10, 0); 
        assertEquals(PTS_FLUSH, CalculerScoreColonne(grille, 0));
        grille[0][1] = newCarte(3,0); grille[1][1] = newCarte(3,1); 
        grille[2][1] = newCarte(5,2); grille[3][1] = newCarte(6,3); grille[4][1] = newCarte(7,0);
        assertEquals(PTS_PAIRE, CalculerScoreColonne(grille, 1));
    }

    void testCompterValeurs() {
        Carte[] main = new Carte[]{ newCarte(2,0), newCarte(2,1), newCarte(5,0), newCarte(5,1), newCarte(5,2) };
        int[] counts = CompterValeurs(main);
        assertEquals(2, counts[2]);
        assertEquals(3, counts[5]);
    }

    void testCopierTableau() {
        Carte[] src = new Carte[]{ newCarte(1,0), newCarte(2,0) };
        Carte[] dest = CopierTableau(src);
        assertEquals(length(src), length(dest));
        dest[0].num = 99;
        src[0] = newCarte(5,5); 
        assertTrue(dest[0].num != 5);
    }

    void testCreeNouveauJeu() {
        Carte[] p = CreeNouveauJeu();
        assertEquals(52, length(p));
        assertEquals(2, p[0].num);
        assertEquals(0, p[0].couleur);
    }

    void testNomCarte() {
        Carte c1 = newCarte(12, 1);
        assertEquals("D♥", NomCarte(c1));
        Carte c2 = newCarte(10, 3);
        assertEquals("10♠", NomCarte(c2));
    }
}