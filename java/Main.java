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

    // PARAMETRES HISTORIQUE
    final int COL_HISTORIQUE = 162; 
    final int LIG_HISTORIQUE_START = 6;
    final int NB_LIGNES_HISTO = 35; 

    // GLOBALES
    String seedActuelle;
    long seedNumber;
    Question[] baseDeQuestions;
    String[] historiqueJeu; 

    // Variables pour l'affichage persistant
    String derniereQuestionEnonce = "";
    String derniereReponseJusteLettre = "";
    String derniereReponseJusteTexte = ""; 
    String derniereReponseJoueurLettre = "";
    String derniereReponseJoueurTexte = "";
    String derniereExplication = "";

    // ========================================================================================================================
    // ALGORITHME PRINCIPAL
    // ========================================================================================================================
    
    // Fonction principale qui gère le cycle de vie de l'application (Menu, Boucle de jeu, Quitter).
    void algorithm() {
        ChargerQuestions();
        boolean continuer = true;
        while (continuer) {
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

    // Initialise et lance une partie complète avec la logique de tour par tour.
    void LancerJeu() {
        Carte[] paquet = CreeNouveauJeu();
        Melanger(paquet);
        Carte[][] grille = new Carte[5][5];

        // Initialisation de l'historique vide
        historiqueJeu = new String[NB_LIGNES_HISTO];
        for (int i = 0; i < length(historiqueJeu); i += 1) {
            historiqueJeu[i] = "";
        }
        AjouterHistorique("Nouvelle partie lancée.");
        AjouterHistorique("Seed : " + seedActuelle);
        AjouterHistorique("-----------------------");

        int idxPaquet = 0;
        int cartesPosees = 0;
        int jokers = NB_JOKERS_MAX;
        boolean peutPasser = true;

        // Reset variables questions
        derniereQuestionEnonce = "";
        derniereReponseJusteLettre = "";
        derniereReponseJoueurLettre = "";
        derniereExplication = "";
        
        String messageInfo = ""; 

        while (cartesPosees < 25 && idxPaquet < 52) {
            Carte cActuelle = paquet[idxPaquet];
            Carte cSuivante = null;
            if (idxPaquet < 51) {
                cSuivante = paquet[idxPaquet + 1];
            }

            // Calcul des scores
            int[] ptsLignes = new int[5];
            int[] ptsCols = new int[5];
            for (int i = 0; i < 5; i += 1) {
                ptsLignes[i] = CalculerScoreLigne(grille, i);
            }
            for (int j = 0; j < 5; j += 1) {
                ptsCols[j] = CalculerScoreColonne(grille, j);
            }

            boolean tourJoue = false;
            while (!tourJoue) {
                AfficherEcranJeu(grille, cActuelle, cSuivante, messageInfo, jokers, ptsLignes, ptsCols);

                if (length(messageInfo) > 0) {
                    messageInfo = "";
                }

                println("\n=== TOUR " + (cartesPosees + 1) + "/25 ===");
                int lig = lireCoordonneeOuJoker("Ligne (1-5) ou 'J' : ");

                if (lig == -3) {
                    messageInfo = RED + ">> ERREUR : Ligne hors limites (1-5) !" + RESET;
                } else if (lig == -1) {
                    messageInfo = RED + ">> ERREUR : Saisie invalide !" + RESET;
                } else if (lig == -2) { // JOKER
                    if (jokers > 0 && peutPasser) {
                        boolean gagne = PoserQuestion();
                        if (gagne) {
                            messageInfo = GREEN + ">> CORRECT ! Carte défaussée." + RESET;
                            AjouterHistorique(GREEN + "Joker : " + NomCarte(cActuelle) + " défaussée." + RESET);
                            jokers -= 1;
                            peutPasser = false;
                            idxPaquet += 1;
                            tourJoue = true;
                            continue;
                        } else {
                            messageInfo = RED + ">> ERREUR ! Vous devez jouer la carte." + RESET;
                            peutPasser = false;
                        }
                    } else {
                        messageInfo = RED + ">> Joker impossible !" + RESET;
                    }
                } else if (lig >= 0) {
                    int col = lireCoordonneeOuJoker("Colonne (1-5) ou 'J' : ");

                    if (col == -3) {
                        messageInfo = RED + ">> ERREUR : Colonne hors limites (1-5) !" + RESET;
                    } else if (col == -1) {
                        messageInfo = RED + ">> ERREUR : Saisie invalide !" + RESET;
                    } else if (col == -2) {
                        if (jokers > 0 && peutPasser) {
                            boolean gagne = PoserQuestion();
                            if (gagne) {
                                messageInfo = GREEN + ">> CORRECT ! Carte défaussée." + RESET;
                                AjouterHistorique(GREEN + "Joker : " + NomCarte(cActuelle) + " défaussée." + RESET);
                                jokers -= 1;
                                peutPasser = false;
                                idxPaquet += 1;
                                tourJoue = true;
                                continue;
                            } else {
                                messageInfo = RED + ">> ERREUR ! Vous devez jouer la carte." + RESET;
                                peutPasser = false;
                            }
                        } else {
                            messageInfo = RED + ">> Joker impossible !" + RESET;
                        }
                    } else if (col >= 0) {
                        if (grille[lig][col] == null) {
                            grille[lig][col] = cActuelle;
                            
                            AjouterHistorique("Posé " + NomCarte(cActuelle) + " en (" + (lig + 1) + "," + (col + 1) + ")");
                            
                            if (EstLigneComplete(grille, lig)) {
                                AjouterHistorique(YELLOW + "Ligne " + (lig + 1) + " complétée !" + RESET);
                            }
                            if (EstColonneComplete(grille, col)) {
                                AjouterHistorique(YELLOW + "Colonne " + (col + 1) + " complétée !" + RESET);
                            }

                            cartesPosees += 1;
                            idxPaquet += 1;
                            peutPasser = true;
                            tourJoue = true;
                        } else {
                            messageInfo = RED + ">> ERREUR : Case occupée !" + RESET;
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

    // Ajoute un message dans la pile de l'historique et décale les anciens messages.
    void AjouterHistorique(String msg) {
        for (int i = 0; i < length(historiqueJeu) - 1; i += 1) {
            historiqueJeu[i] = historiqueJeu[i + 1];
        }
        historiqueJeu[length(historiqueJeu) - 1] = msg;
    }

    // ========================================================================================================================
    // CALCUL SCORE BALATRO
    // ========================================================================================================================

    // Détermine la valeur en jetons (Chips) d'une carte selon son rang (2-9 = valeur, 10-R = 10, A = 11).
    int JetonsPourValeur(int valeurNum) {
        if (valeurNum >= 2 && valeurNum <= 9) {
            return valeurNum;
        }
        if (valeurNum >= 10 && valeurNum <= 13) {
            return 10;
        }
        if (valeurNum == 14) {
            return 11;
        }
        return 0;
    }

    // Calcule le score Balatro d'une main de 5 cartes (Chips * Mult).
    int CalculerPointsMain(Carte[] main) {
        for (int i = 0; i < length(main); i += 1) {
            if (main[i] == null) {
                return 0;
            }
        }

        Carte[] triee = CopierTableau(main);
        TrierMain(triee);

        boolean flush = EstCouleur(triee);
        boolean straight = EstSuite(triee);
        int[] counts = CompterValeurs(triee);
        
        boolean carre = false;
        boolean brelan = false;
        int paires = 0;
        
        for (int i = 0; i < length(counts); i += 1) {
            if (counts[i] == 4) {
                carre = true;
            }
            if (counts[i] == 3) {
                brelan = true;
            }
            if (counts[i] == 2) {
                paires += 1;
            }
        }

        int baseChips = 0;
        int mult = 0;
        int sommeJetonsCartes = 0;

        if (flush && straight) {
            for (int i = 0; i < 5; i += 1) {
                sommeJetonsCartes += JetonsPourValeur(triee[i].num);
            }
            if (triee[0].num == 10) { 
                baseChips = 100; mult = 8; 
            } else { 
                baseChips = 100; mult = 8; 
            }
        } else if (carre) {
            baseChips = 60; mult = 7;
            for (int v = 2; v <= 14; v += 1) {
                if (counts[v] == 4) {
                    sommeJetonsCartes += (JetonsPourValeur(v) * 4);
                }
            }
        } else if (brelan && paires >= 1) {
            baseChips = 40; mult = 4;
            for (int i = 0; i < 5; i += 1) {
                sommeJetonsCartes += JetonsPourValeur(triee[i].num);
            }
        } else if (flush) {
            baseChips = 35; mult = 4;
            for (int i = 0; i < 5; i += 1) {
                sommeJetonsCartes += JetonsPourValeur(triee[i].num);
            }
        } else if (straight) {
            baseChips = 30; mult = 4;
            for (int i = 0; i < 5; i += 1) {
                sommeJetonsCartes += JetonsPourValeur(triee[i].num);
            }
        } else if (brelan) {
            baseChips = 30; mult = 3;
            for (int v = 2; v <= 14; v += 1) {
                if (counts[v] == 3) {
                    sommeJetonsCartes += (JetonsPourValeur(v) * 3);
                }
            }
        } else if (paires == 2) {
            baseChips = 20; mult = 2;
            for (int v = 2; v <= 14; v += 1) {
                if (counts[v] == 2) {
                    sommeJetonsCartes += (JetonsPourValeur(v) * 2);
                }
            }
        } else if (paires == 1) {
            baseChips = 10; mult = 2;
            for (int v = 2; v <= 14; v += 1) {
                if (counts[v] == 2) {
                    sommeJetonsCartes += (JetonsPourValeur(v) * 2);
                }
            }
        } else {
            baseChips = 5; mult = 1;
            sommeJetonsCartes = JetonsPourValeur(triee[4].num);
        }

        return (sommeJetonsCartes + baseChips) * mult;
    }

    // ========================================================================================================================
    // OUTILS ET AFFICHAGE
    // ========================================================================================================================

    // Récupère l'entrée utilisateur pour jouer une carte ou activer un joker.
    int lireCoordonneeOuJoker(String message) {
        print(message);
        String s = readString();
        if (equals(s, "j") || equals(s, "J")) {
            return -2;
        }
        if (estUnNombre(s)) {
            int val = stringToInt(s);
            if (val >= 1 && val <= 5) {
                return val - 1;
            } else {
                return -3;
            }
        }
        return -1;
    }

    // Charge la base de données de questions depuis le fichier CSV.
    void ChargerQuestions() {
        CSVFile f = loadCSV("visuel/questions.csv");
        int nb = rowCount(f);
        baseDeQuestions = new Question[nb];
        for (int i = 0; i < nb; i += 1) {
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

    // Retourne le texte complet de la réponse correspondant à une lettre (A, B, C, D).
    String GetTexteReponse(Question q, String lettre) {
        if (equals(lettre, "A")) {
            return q.repA;
        }
        if (equals(lettre, "B")) {
            return q.repB;
        }
        if (equals(lettre, "C")) {
            return q.repC;
        }
        if (equals(lettre, "D")) {
            return q.repD;
        }
        return "Inconnu";
    }

    // Gère le processus de question Joker (Affichage, Saisie, Vérification).
    boolean PoserQuestion() {
        int idx = GenererNombrePseudoAleatoire(length(baseDeQuestions));
        Question q = baseDeQuestions[idx];
        String rep = "";
        boolean valid = false;
        String msgErreur = "";

        derniereQuestionEnonce = q.enonce;

        while (!valid) {
            AfficherEcranQuestion(q, msgErreur, false);
            print("Votre réponse (A/B/C/D) : ");
            rep = readString();
            if (length(rep) == 1) {
                char c = charAt(rep, 0);
                if (c >= 'a' && c <= 'd') {
                    valid = true;
                }
                if (c >= 'A' && c <= 'D') {
                    valid = true;
                }
            }
            if (!valid) {
                msgErreur = RED + "Réponse invalide (A, B, C ou D attendu)" + RESET;
            }
        }

        derniereReponseJoueurLettre = toUpperCases(rep);
        derniereReponseJoueurTexte = GetTexteReponse(q, derniereReponseJoueurLettre);
        
        derniereReponseJusteLettre = q.bonneReponse;
        derniereReponseJusteTexte = GetTexteReponse(q, derniereReponseJusteLettre);
        
        derniereExplication = q.explication;
        
        boolean correct = equals(derniereReponseJoueurLettre, derniereReponseJusteLettre);

        String msgResultat = "";
        if (correct) {
            msgResultat = GREEN + "BONNE RÉPONSE ! +1 Joker" + RESET;
        } else {
            msgResultat = RED + "MAUVAISE RÉPONSE... (" + q.bonneReponse + ")" + RESET;
        }
        
        AfficherEcranQuestion(q, msgResultat, true);
        println("\nAppuyez sur Entrée pour revenir au jeu...");
        readString();
        return correct;
    }

    // Convertit une chaîne minuscule en majuscule.
    String toUpperCases(String s) {
        String res = "";
        for (int i = 0; i < length(s); i += 1) {
            char c = charAt(s, i);
            if (c >= 'a' && c <= 'z') {
                res = res + (char) (c - 32);
            } else {
                res = res + c;
            }
        }
        return res;
    }

    // Affiche l'interface de la question joker.
    void AfficherEcranQuestion(Question q, String message, boolean montrerSolution) {
        clear();
        String[] ecran = LireTemplate("visuel/question.txt");
        EcrireDansBuffer(ecran, 15, 10, q.enonce);
        EcrireDansBuffer(ecran, 23, 8, "A) " + q.repA);
        EcrireDansBuffer(ecran, 23, 90, "B) " + q.repB);
        EcrireDansBuffer(ecran, 29, 8, "C) " + q.repC);
        EcrireDansBuffer(ecran, 29, 90, "D) " + q.repD);
        if (length(message) > 0) {
            EcrireDansBuffer(ecran, 18, 8, message);
        }
        if (montrerSolution) {
            EcrireDansBuffer(ecran, 18, 90, "Explication : " + q.explication);
        }
        RendreBuffer(ecran);
    }

    // Affiche l'écran principal du jeu.
    void AfficherEcranJeu(Carte[][] grille, Carte cActuelle, Carte cSuivante, String message, int nbJokers, int[] ptsLignes, int[] ptsCols) {
        clear();
        String[] ecran = LireTemplate("visuel/jeu.txt");
        int startLigne = 7;
        int startCol = 4;

        // 1. Historique (Droite) - AFFICHE EN PREMIER pour éviter les bugs ANSI
        for (int i = 0; i < length(historiqueJeu); i += 1) {
            EcrireDansBuffer(ecran, LIG_HISTORIQUE_START + i, COL_HISTORIQUE, historiqueJeu[i]);
        }

        // 2. Message Erreur (Droite / Bas) - Remonté à 45
        if (length(message) > 0) {
            EcrireDansBuffer(ecran, 45, 162, message);
        }

        // 3. Panneau Question (Milieu)
        if (length(derniereReponseJusteLettre) > 0) {
            EcrireDansBuffer(ecran, 39, 66, CYAN + "DERNIERE QUESTION :" + RESET);
            
            if (length(derniereQuestionEnonce) > 90) {
                String l1 = substring(derniereQuestionEnonce, 0, 90);
                String l2 = substring(derniereQuestionEnonce, 90, length(derniereQuestionEnonce));
                EcrireDansBuffer(ecran, 40, 66, l1);
                EcrireDansBuffer(ecran, 41, 66, l2);
            } else {
                EcrireDansBuffer(ecran, 40, 66, derniereQuestionEnonce);
            }

            String texteBonne = GREEN + "Reponse Correcte : " + derniereReponseJusteLettre + " - " + derniereReponseJusteTexte + RESET;
            EcrireDansBuffer(ecran, 42, 66, texteBonne);

            String coulJoueur = "";
            if (equals(derniereReponseJusteLettre, derniereReponseJoueurLettre)) {
                coulJoueur = GREEN;
            } else {
                coulJoueur = RED;
            }
            String texteJoueur = coulJoueur + "Votre Reponse    : " + derniereReponseJoueurLettre + " - " + derniereReponseJoueurTexte + RESET;
            EcrireDansBuffer(ecran, 43, 66, texteJoueur);

            EcrireDansBuffer(ecran, 44, 66, "Explication : " + derniereExplication);
        }

        // 4. Grille (Gauche)
        for (int i = 0; i < 5; i += 1) {
            for (int j = 0; j < 5; j += 1) {
                if (grille[i][j] != null) {
                    int l = startLigne + (i * 7);
                    int c = startCol + (j * 10);
                    DessinerCarteGrande(ecran, l, c, grille[i][j]);
                }
            }
        }

        // 5. Scores (Gauche / Milieu) - Avec correction décalage > 99
        for (int i = 0; i < 5; i += 1) {
            if (EstLigneComplete(grille, i)) {
                int colScore = 58;
                if (ptsLignes[i] > 99) {
                    colScore -= 1;
                }
                EcrireDansBuffer(ecran, startLigne + (i * 7) + 3, colScore, "=" + ptsLignes[i]);
            }
        }
        for (int j = 0; j < 5; j += 1) {
            if (EstColonneComplete(grille, j)) {
                EcrireDansBuffer(ecran, 42, 4 + (j * 10), "=" + ptsCols[j]);
            }
        }

        // 6. Infos Cartes (Gauche Bas)
        if (cActuelle != null) {
            EcrireDansBuffer(ecran, 45, 21, NomCarte(cActuelle));
        }
        if (cSuivante != null) {
            EcrireDansBuffer(ecran, 45, 49, NomCarte(cSuivante));
        }
        EcrireDansBuffer(ecran, 35, 117, "(" + nbJokers + "/" + NB_JOKERS_MAX + ")");
        
        RendreBuffer(ecran);
    }

    // Calcule la longueur visuelle d'une chaîne (sans les codes ANSI).
    int longueurVisuelle(String s) {
        int len = 0;
        boolean inAnsi = false;
        for (int i = 0; i < length(s); i += 1) {
            char c = charAt(s, i);
            if (c == '\u001B') {
                inAnsi = true;
            } else if (inAnsi && c == 'm') {
                inAnsi = false;
            } else if (!inAnsi) {
                len += 1;
            }
        }
        return len;
    }

    // Ecrit du texte dans le buffer d'affichage à une position donnée.
    void EcrireDansBuffer(String[] ecran, int l, int c, String texte) {
        if (l >= length(ecran)) {
            return;
        }
        String ligne = ecran[l];
        int lenV = longueurVisuelle(texte);
        if (c + lenV > length(ligne)) {
            return;
        }
        ecran[l] = substring(ligne, 0, c) + texte + substring(ligne, c + lenV, length(ligne));
    }

    // Lit un entier depuis la console avec vérification des bornes.
    int lireEntier(String message, int min, int max) {
        int val = -1;
        boolean valide = false;
        while (!valide) {
            print(message);
            String s = readString();
            if (estUnNombre(s)) {
                val = stringToInt(s);
                if (val >= min && val <= max) {
                    valide = true;
                } else {
                    println(">> Erreur : Hors limites");
                }
            } else {
                println(">> Erreur : Nombre attendu");
            }
        }
        return val;
    }

    // Vérifie si une chaîne ne contient que des chiffres.
    boolean estUnNombre(String s) {
        if (length(s) == 0) {
            return false;
        }
        for (int i = 0; i < length(s); i += 1) {
            char c = charAt(s, i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    // Lance le jeu avec une seed prédéfinie.
    void LancerPartieAvecSeed(String seed) {
        seedActuelle = seed;
        seedNumber = StringToLong(seed);
        println("Initialisation avec Seed : " + seedActuelle);
        LancerJeu();
    }

    // Générateur de nombres pseudo-aléatoires.
    int GenererNombrePseudoAleatoire(int max) {
        if (max <= 0) {
            return 0;
        }
        seedNumber = (seedNumber * 1664525 + 1013904223);
        long resultat = seedNumber;
        if (resultat < 0) {
            resultat = resultat * -1;
        }
        return (int) (resultat % max);
    }

    // Convertit la chaîne seed en un long.
    long StringToLong(String s) {
        long h = 0;
        for (int i = 0; i < length(s); i += 1) {
            h = 31 * h + charAt(s, i);
        }
        return h;
    }

    // Génère une seed aléatoire.
    String GenererRandomSeedString(int longueur) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < longueur; i += 1) {
            int idx = (int) (random() * length(chars));
            res = res + charAt(chars, idx);
        }
        return res;
    }

    // Affiche l'écran de fin de partie.
    void AfficherEcranResultat(Carte[][] grille) {
        clear();
        String[] ecran = LireTemplate("visuel/resultat.txt");
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
        int scoreTotal = 0;
        for (int i = 0; i < 5; i += 1) {
            int score = CalculerScoreLigne(grille, i);
            scoreTotal += score;
            EcrireDansBuffer(ecran, startLigne + (i * 7) + 3, 60, "" + score);
        }
        for (int j = 0; j < 5; j += 1) {
            int score = CalculerScoreColonne(grille, j);
            scoreTotal += score;
            EcrireDansBuffer(ecran, 43, 8 + (j * 10), "" + score);
        }
        EcrireDansBuffer(ecran, 43, 68, "" + scoreTotal);
        RendreBuffer(ecran);
    }

    // Dessine une carte dans le buffer d'affichage.
    void DessinerCarteGrande(String[] ecran, int l, int c, Carte card) {
        String val = LISTE_VALEUR[card.num];
        String sym = LISTE_COULEUR[card.couleur];
        EcrireDansBuffer(ecran, l + 1, c + 2, val);
        EcrireDansBuffer(ecran, l + 3, c + 4, sym);
        int decalage = 0;
        if (length(val) == 2) {
            decalage = 5;
        } else {
            decalage = 6;
        }
        EcrireDansBuffer(ecran, l + 5, c + decalage, val);
    }

    // Vérifie si une ligne est complète.
    boolean EstLigneComplete(Carte[][] grille, int lig) {
        for (int j = 0; j < 5; j += 1) {
            if (grille[lig][j] == null) {
                return false;
            }
        }
        return true;
    }

    // Vérifie si une colonne est complète.
    boolean EstColonneComplete(Carte[][] grille, int col) {
        for (int i = 0; i < 5; i += 1) {
            if (grille[i][col] == null) {
                return false;
            }
        }
        return true;
    }

    // Wrapper pour le score d'une ligne.
    int CalculerScoreLigne(Carte[][] grille, int ligneIdx) {
        return CalculerPointsMain(grille[ligneIdx]);
    }

    // Wrapper pour le score d'une colonne.
    int CalculerScoreColonne(Carte[][] grille, int colIdx) {
        Carte[] col = new Carte[5];
        for (int i = 0; i < 5; i += 1) {
            col[i] = grille[i][colIdx];
        }
        return CalculerPointsMain(col);
    }

    // Vérifie la combinaison Couleur (Flush).
    boolean EstCouleur(Carte[] m) {
        int c = m[0].couleur;
        for (int i = 1; i < 5; i += 1) {
            if (m[i].couleur != c) {
                return false;
            }
        }
        return true;
    }

    // Vérifie la combinaison Suite (Straight).
    boolean EstSuite(Carte[] m) {
        if (m[0].num == 2 && m[1].num == 3 && m[2].num == 4 && m[3].num == 5 && m[4].num == 14) {
            return true;
        }
        for (int i = 0; i < 4; i += 1) {
            if (m[i + 1].num != m[i].num + 1) {
                return false;
            }
        }
        return true;
    }

    // Compte les occurrences des valeurs des cartes.
    int[] CompterValeurs(Carte[] m) {
        int[] c = new int[15];
        for (int i = 0; i < 5; i += 1) {
            c[m[i].num] += 1;
        }
        return c;
    }

    // Trie une main de cartes par valeur (Tri à bulles).
    void TrierMain(Carte[] t) {
        for (int i = 0; i < length(t) - 1; i += 1) {
            for (int j = 0; j < length(t) - i - 1; j += 1) {
                if (t[j].num > t[j + 1].num) {
                    Carte temp = t[j];
                    t[j] = t[j + 1];
                    t[j + 1] = temp;
                }
            }
        }
    }

    // Copie un tableau de cartes (copie superficielle des références).
    Carte[] CopierTableau(Carte[] src) {
        Carte[] dest = new Carte[length(src)];
        for (int i = 0; i < length(src); i += 1) {
            dest[i] = src[i];
        }
        return dest;
    }

    // Affiche le tableau de chaînes à l'écran.
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

    // Lit un fichier template ligne par ligne.
    String[] LireTemplate(String chemin) {
        File f = newFile(chemin);
        int nb = 0;
        while (ready(f)) {
            readLine(f);
            nb += 1;
        }
        f = newFile(chemin);
        String[] tab = new String[nb];
        int idx = 0;
        while (ready(f)) {
            tab[idx] = readLine(f);
            idx += 1;
        }
        return tab;
    }

    // Affiche la page d'accueil.
    void AfficherPageAccueuil() {
        String[] ecran = LireTemplate("visuel/acceuil.txt");
        RendreBuffer(ecran);
    }

    // Affiche les règles.
    void AfficherRegles() {
        clear();
        String[] regles = LireTemplate("visuel/regle.md");
        RendreBuffer(regles);
        println("\nAppuyez sur Entrée pour revenir au menu...");
        readString();
    }

    // Crée un paquet de 52 cartes.
    Carte[] CreeNouveauJeu() {
        Carte[] p = new Carte[52];
        int idx = 0;
        for (int c = 0; c < 4; c += 1) {
            for (int v = 2; v <= 14; v += 1) {
                Carte n = new Carte();
                n.couleur = c;
                n.num = v;
                p[idx] = n;
                idx += 1;
            }
        }
        return p;
    }

    // Mélange le paquet.
    void Melanger(Carte[] p) {
        for (int i = 0; i < length(p); i += 1) {
            int r = GenererNombrePseudoAleatoire(length(p));
            Carte t = p[i];
            p[i] = p[r];
            p[r] = t;
        }
    }

    // Retourne la représentation textuelle d'une carte.
    String NomCarte(Carte c) {
        return LISTE_VALEUR[c.num] + LISTE_COULEUR[c.couleur];
    }

    // Efface le terminal.
    void clear() {
        print("\u001B[H\u001B[2J");
    }

    // ========================================================================================================================
    // TESTS UNITAIRES COMPLETS
    // ========================================================================================================================
    
    // Utilitaire pour créer une carte rapidement dans les tests
    Carte newCarte(int v, int c) {
        Carte a = new Carte();
        a.num = v;
        a.couleur = c;
        return a;
    }

    void testCalculerPointsMain() {
        // Royal Flush
        Carte[] royal = new Carte[]{newCarte(10, 0), newCarte(V, 0), newCarte(D, 0), newCarte(R, 0), newCarte(A, 0)};
        assertEquals(1208, CalculerPointsMain(royal)); // (51 + 100) * 8
        
        // Carte Haute (Rien)
        Carte[] rien = new Carte[]{newCarte(2, 0), newCarte(4, 1), newCarte(6, 2), newCarte(8, 3), newCarte(10, 0)};
        assertEquals(15, CalculerPointsMain(rien)); // (10 + 5) * 1

        // Full House
        Carte[] full = new Carte[]{newCarte(R, 0), newCarte(R, 1), newCarte(R, 2), newCarte(A, 0), newCarte(A, 1)};
        assertEquals(368, CalculerPointsMain(full));

        // Paire
        Carte[] paire = new Carte[]{newCarte(5, 0), newCarte(5, 1), newCarte(2, 2), newCarte(3, 3), newCarte(4, 0)};
        assertEquals(40, CalculerPointsMain(paire));
    }

    void testJetonsPourValeur() {
        assertEquals(5, JetonsPourValeur(5));
        assertEquals(10, JetonsPourValeur(10));
        assertEquals(11, JetonsPourValeur(14));
        assertEquals(0, JetonsPourValeur(99)); // Cas invalide
    }
    
    void testEstLigneComplete() {
        Carte[][] grille = new Carte[5][5];
        for (int j = 0; j < 5; j += 1) {
            grille[0][j] = newCarte(2, 0);
        }
        assertTrue(EstLigneComplete(grille, 0));
        assertFalse(EstLigneComplete(grille, 1));
    }

    void testEstColonneComplete() {
        Carte[][] grille = new Carte[5][5];
        for (int i = 0; i < 5; i += 1) {
            grille[i][0] = newCarte(2, 0);
        }
        assertTrue(EstColonneComplete(grille, 0));
        assertFalse(EstColonneComplete(grille, 1));
    }

    void testCalculerScoreLigne() {
        Carte[][] grille = new Carte[5][5];
        // Remplir ligne 0 avec un Flush (2, 3, 4, 5, 6 de couleur 0)
        for (int j = 0; j < 5; j += 1) {
            grille[0][j] = newCarte(j + 2, 0);
        }
        // Score: Straight Flush (30 + 35) = 66 non, (20 + 100) * 8 = 960 (Straight Flush Balatro : (2+3+4+5+6 + 100) * 8 = 960)
        assertTrue(CalculerScoreLigne(grille, 0) > 0);
        assertEquals(0, CalculerScoreLigne(grille, 1)); // Ligne vide
    }

    void testCalculerScoreColonne() {
        Carte[][] grille = new Carte[5][5];
        // Remplir col 0 avec des As (Carré possible si 4 As, ici 5 As = Carré)
        for (int i = 0; i < 5; i += 1) {
            grille[i][0] = newCarte(A, 0);
        }
        assertTrue(CalculerScoreColonne(grille, 0) > 0);
        assertEquals(0, CalculerScoreColonne(grille, 1));
    }

    void testEstCouleur() {
        Carte[] oui = new Carte[]{newCarte(2, 0), newCarte(5, 0), newCarte(7, 0), newCarte(9, 0), newCarte(R, 0)};
        assertTrue(EstCouleur(oui));
        Carte[] non = new Carte[]{newCarte(2, 0), newCarte(5, 1), newCarte(7, 0), newCarte(9, 0), newCarte(R, 0)};
        assertFalse(EstCouleur(non));
    }

    void testEstSuite() {
        Carte[] oui = new Carte[]{newCarte(2, 0), newCarte(3, 1), newCarte(4, 0), newCarte(5, 2), newCarte(6, 0)};
        assertTrue(EstSuite(oui));
        Carte[] non = new Carte[]{newCarte(2, 0), newCarte(3, 1), newCarte(4, 0), newCarte(5, 2), newCarte(7, 0)};
        assertFalse(EstSuite(non));
    }

    void testCompterValeurs() {
        Carte[] main = new Carte[]{newCarte(5, 0), newCarte(5, 1), newCarte(5, 2), newCarte(2, 3), newCarte(9, 0)};
        int[] counts = CompterValeurs(main);
        assertEquals(3, counts[5]);
        assertEquals(1, counts[2]);
        assertEquals(1, counts[9]);
        assertEquals(0, counts[10]);
    }

    void testCopierTableau() {
        Carte[] src = new Carte[]{newCarte(1, 1)};
        Carte[] dest = CopierTableau(src);
        assertEquals(1, length(dest));
        assertFalse(src == dest); // Vérifie que c'est une nouvelle instance de tableau
    }

    void testCreeNouveauJeu() {
        Carte[] p = CreeNouveauJeu();
        assertEquals(52, length(p));
        assertFalse(p[0] == null);
    }

    void testNomCarte() {
        Carte c = newCarte(14, 3); // As de Pique
        assertEquals("A♠", NomCarte(c));
        Carte c2 = newCarte(10, 1); // 10 de Coeur
        assertEquals("10♥", NomCarte(c2));
    }

    void testToUpperCases() {
        assertEquals("ABC", toUpperCases("abc"));
        assertEquals("TEST", toUpperCases("TeSt"));
    }

    void testLongueurVisuelle() {
        assertEquals(3, longueurVisuelle("abc"));
        assertEquals(0, longueurVisuelle(""));
        assertEquals(4, longueurVisuelle("test" + RED));
    }

    void testEstUnNombre() {
        assertTrue(estUnNombre("123"));
        assertFalse(estUnNombre("12a"));
        assertFalse(estUnNombre(""));
    }

    void testGenererNombrePseudoAleatoire() {
        seedNumber = 12345;
        int n = GenererNombrePseudoAleatoire(10);
        assertTrue(n >= 0);
        assertTrue(n < 10);
    }

    void testStringToLong() {
        assertTrue(StringToLong("A") > 0);
        assertEquals(0, StringToLong(""));
    }

    void testGenererRandomSeedString() {
        assertEquals(5, length(GenererRandomSeedString(5)));
        assertEquals(10, length(GenererRandomSeedString(10)));
    }

    void testGetTexteReponse() {
        Question q = new Question();
        q.repA = "Reponse A";
        q.repB = "Reponse B";
        assertEquals("Reponse A", GetTexteReponse(q, "A"));
        assertEquals("Reponse B", GetTexteReponse(q, "B"));
        assertEquals("Inconnu", GetTexteReponse(q, "Z"));
    }
}
