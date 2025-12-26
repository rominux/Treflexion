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
    final String RED = "\u001B[31m";
    final String GREEN = "\u001B[32m";
    final String RESET = "\u001B[0m";

    // GLOBALES
    String seedActuelle;
    long seedNumber;
    Question[] baseDeQuestions;

    // Variables pour l'affichage persistant dans jeu.txt
    String derniereReponseJuste = "";
    String derniereReponseJoueur = "";
    String derniereExplication = "";
    String dernierCoup = "";

    // ========================================================================================================================
    // ALGORITHME PRINCIPAL
    // ========================================================================================================================
    
    // Point d'entrée du programme. Gère le menu principal et la boucle du jeu.
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

    // Gère le déroulement complet d'une partie (boucle des 25 tours, affichage, saisie joueur).
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
        dernierCoup = "Début de partie";
        String messageInfo = "";

        while (cartesPosees < 25 && idxPaquet < 52) {
            Carte cActuelle = paquet[idxPaquet];
            
            Carte cSuivante = null;
            if (idxPaquet < 51) {
                cSuivante = paquet[idxPaquet + 1];
            }

            // Calcul des scores en direct (Lignes et Colonnes)
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
                // Affichage de l'écran avec les infos à jour, incluant les scores
                AfficherEcranJeu(grille, cActuelle, cSuivante, messageInfo, jokers, ptsLignes, ptsCols);

                // On efface le message informatif après l'avoir affiché
                if (length(messageInfo) > 0) {
                    messageInfo = "";
                }

                println("\n=== TOUR " + (cartesPosees + 1) + "/25 ===");

                // --- DEMANDE DE LIGNE ---
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
                            dernierCoup = "Joker : Carte défaussée";
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
                        messageInfo = RED + ">> Joker impossible (épuisé ou déjà utilisé) !" + RESET;
                    }
                } else if (lig >= 0) { // COORDONNÉE VALIDE
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
                                dernierCoup = "Joker : Carte défaussée";
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
                            dernierCoup = "Posé " + NomCarte(cActuelle) + " en (" + (lig + 1) + "," + (col + 1) + ")";
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

    // ========================================================================================================================
    // OUTILS DE SAISIE
    // ========================================================================================================================

    // Demande à l'utilisateur de saisir une coordonnée ou d'activer un joker.
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

    // ========================================================================================================================
    // SYSTÈME DE QUESTIONS
    // ========================================================================================================================

    // Charge les questions depuis le fichier CSV dans le tableau global.
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

    // Sélectionne une question aléatoire, l'affiche, récupère la réponse et vérifie si elle est correcte.
    boolean PoserQuestion() {
        int idx = GenererNombrePseudoAleatoire(length(baseDeQuestions));
        Question q = baseDeQuestions[idx];

        String rep = "";
        boolean valid = false;
        String msgErreur = "";

        // BOUCLE DE SAISIE
        while (!valid) {
            AfficherEcranQuestion(q, msgErreur, false); // false = pas encore le résultat
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

        derniereReponseJoueur = toUpperCases(rep);
        derniereReponseJuste = q.bonneReponse;
        derniereExplication = q.explication;

        boolean correct = false;
        if (equals(derniereReponseJoueur, derniereReponseJuste)) {
            correct = true;
        }

        // AFFICHAGE DU RÉSULTAT SUR L'INTERFACE QUESTION
        String msgResultat = "";
        if (correct) {
            msgResultat = GREEN + "BONNE RÉPONSE ! +1 Joker" + RESET;
        } else {
            msgResultat = RED + "MAUVAISE RÉPONSE... (" + q.bonneReponse + ")" + RESET;
        }

        AfficherEcranQuestion(q, msgResultat, true); // true = afficher résultat
        println("\nAppuyez sur Entrée pour revenir au jeu...");
        readString();

        return correct;
    }

    // Convertit une chaîne de caractères en majuscules.
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

    // ========================================================================================================================
    // AFFICHAGE (Système de templates et buffers)
    // ========================================================================================================================

    // Affiche l'écran spécifique pour poser une question joker (utilise question.txt).
    void AfficherEcranQuestion(Question q, String message, boolean montrerSolution) {
        clear();
        String[] ecran = LireTemplate("visuel/question.txt");

        int ligEnonce = 15;
        int colEnonce = 10;
        int ligRepA = 23;
        int colRepG = 8;
        int ligRepB = 23;
        int colRepD = 90;
        int ligRepC = 29;
        int ligRepD = 29;
        int ligMessage = 18; // Où afficher les erreurs ou le bravo
        
        // Affichage des textes
        EcrireDansBuffer(ecran, ligEnonce, colEnonce, q.enonce);
        EcrireDansBuffer(ecran, ligRepA, colRepG, "A) " + q.repA);
        EcrireDansBuffer(ecran, ligRepB, colRepD, "B) " + q.repB);
        EcrireDansBuffer(ecran, ligRepC, colRepG, "C) " + q.repC);
        EcrireDansBuffer(ecran, ligRepD, colRepD, "D) " + q.repD);

        // Affichage du message (Erreur ou Succès)
        if (length(message) > 0) {
            EcrireDansBuffer(ecran, ligMessage, colRepG, message);
        }

        // Si on est à la phase résultat, on peut afficher l'explication
        if (montrerSolution) {
             EcrireDansBuffer(ecran, ligMessage, colRepD, "Explication : " + q.explication);
        }

        RendreBuffer(ecran);
    }

    // Affiche l'écran principal du jeu avec les cartes, les messages et les scores en temps réel.
    void AfficherEcranJeu(Carte[][] grille, Carte cActuelle, Carte cSuivante, String message, int nbJokers, int[] ptsLignes, int[] ptsCols) {
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

        // 2. Affichage des scores LIGNES (à droite du tableau)
        for (int i = 0; i < 5; i += 1) {
            if (EstLigneComplete(grille, i)) {
                int score = ptsLignes[i];
                int l = startLigne + (i * 7);
                EcrireDansBuffer(ecran, l + 3, 60, "=" + score);
            }
        }

        // 3. Affichage des scores COLONNES (en bas du tableau)
        for (int j = 0; j < 5; j += 1) {
            if (EstColonneComplete(grille, j)) {
                int score = ptsCols[j];
                int col = 8 + (j * 10);
                EcrireDansBuffer(ecran, 42, col, "=" + score);
            }
        }


        // 2. Affichage des cartes courantes
        if (cActuelle != null) {
            EcrireDansBuffer(ecran, 45, 21, NomCarte(cActuelle));
        }
        if (cSuivante != null) {
            EcrireDansBuffer(ecran, 45, 49, NomCarte(cSuivante));
        }

        // 3. Affichage du nombre de Jokers
        String jokerTxt = "(" + nbJokers + "/" + NB_JOKERS_MAX + ")";
        EcrireDansBuffer(ecran, 34, 117, jokerTxt);

        // 4. Affichage du CADRE "DERNIERE QUESTION" (Rappel sur l'écran de jeu)
        if (length(derniereReponseJuste) > 0) {
            EcrireDansBuffer(ecran, 39, 85, derniereReponseJuste);
        }
        if (length(derniereReponseJoueur) > 0) {
            EcrireDansBuffer(ecran, 40, 85, derniereReponseJoueur);
        }
        if (length(derniereExplication) > 0) {
            EcrireDansBuffer(ecran, 41, 66, derniereExplication);
        }

        // 5. Affichage du Dernier Coup Joué
        if (length(dernierCoup) > 0) {
            EcrireDansBuffer(ecran, 45, 67, ">> " + dernierCoup);
        }

        // 6. Affichage du message d'information
        if (length(message) > 0) {
            EcrireDansBuffer(ecran, 45, 67, message);
        }

        RendreBuffer(ecran);
    }

    // Calcule la longueur d'une chaîne en ignorant les codes couleurs ANSI invisibles.
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

    // Écrit une chaîne de caractères dans le tableau représentant l'écran à une position donnée.
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

    // Demande la saisie d'un entier compris entre min et max.
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

    // Vérifie si une chaîne de caractères est composée uniquement de chiffres.
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

    // Initialise une partie avec une graine (seed) spécifique pour la génération aléatoire.
    void LancerPartieAvecSeed(String seed) {
        seedActuelle = seed;
        seedNumber = StringToLong(seed);
        println("Initialisation avec Seed : " + seedActuelle);
        LancerJeu();
    }

    // Génère un nombre pseudo-aléatoire basé sur la seed actuelle.
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

    // Convertit une chaîne de caractères en un nombre long (pour la seed).
    long StringToLong(String s) {
        long h = 0;
        for (int i = 0; i < length(s); i += 1) {
            h = 31 * h + charAt(s, i);
        }
        return h;
    }

    // Génère une chaîne aléatoire de caractères alphanumériques.
    String GenererRandomSeedString(int longueur) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < longueur; i += 1) {
            int idx = (int) (random() * length(chars));
            res = res + charAt(chars, idx);
        }
        return res;
    }

    // Affiche l'écran final avec les scores calculés (utilise resultat.txt).
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

    // Dessine une carte spécifique à une position donnée dans le buffer d'écran.
    void DessinerCarteGrande(String[] ecran, int l, int c, Carte card) {
        String val = LISTE_VALEUR[card.num];
        String sym = LISTE_COULEUR[card.couleur];
        EcrireDansBuffer(ecran, l + 1, c + 2, val);
        EcrireDansBuffer(ecran, l + 3, c + 4, sym);
        
        int decalage = 6;
        if (length(val) == 2) {
            decalage = 5;
        }
        EcrireDansBuffer(ecran, l + 5, c + decalage, val);
    }

    // Vérifie si toutes les cases d'une ligne sont occupées par une carte.
    boolean EstLigneComplete(Carte[][] grille, int lig) {
        for (int j = 0; j < 5; j += 1) {
            if (grille[lig][j] == null) {
                return false;
            }
        }
        return true;
    }

    // Vérifie si toutes les cases d'une colonne sont occupées par une carte.
    boolean EstColonneComplete(Carte[][] grille, int col) {
        for (int i = 0; i < 5; i += 1) {
            if (grille[i][col] == null) {
                return false;
            }
        }
        return true;
    }

    // Calcule le score d'une ligne donnée de la grille.
    int CalculerScoreLigne(Carte[][] grille, int ligneIdx) {
        return CalculerPointsMain(grille[ligneIdx]);
    }

    // Calcule le score d'une colonne donnée de la grille.
    int CalculerScoreColonne(Carte[][] grille, int colIdx) {
        Carte[] col = new Carte[5];
        for (int i = 0; i < 5; i += 1) {
            col[i] = grille[i][colIdx];
        }
        return CalculerPointsMain(col);
    }

    // Analyse une main de 5 cartes et retourne les points correspondants au barème.
    int CalculerPointsMain(Carte[] main) {
        // Sécurité : Si une carte est null, la main ne vaut rien pour l'instant (incomplète)
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
        if (flush && straight) {
            if (triee[0].num == 10) {
                return PTS_ROYAL_FLUSH;
            } else {
                return PTS_STRAIGHT_FLUSH;
            }
        }
        if (carre) {
            return PTS_CARRE;
        }
        if (flush) {
            return PTS_FLUSH;
        }
        if (straight) {
            return PTS_STRAIGHT;
        }
        if (brelan && paires >= 1) {
            return PTS_FULL;
        }
        if (brelan) {
            return PTS_BRELAN;
        }
        if (paires == 2) {
            return PTS_DOUBLE_PAIRE;
        }
        if (paires == 1) {
            return PTS_PAIRE;
        }
        return 0;
    }

    // Vérifie si les 5 cartes sont de la même couleur (Flush).
    boolean EstCouleur(Carte[] m) {
        int c = m[0].couleur;
        for (int i = 1; i < 5; i += 1) {
            if (m[i].couleur != c) {
                return false;
            }
        }
        return true;
    }

    // Vérifie si les 5 cartes forment une suite (Straight).
    boolean EstSuite(Carte[] m) {
        // Cas particulier : Suite A-2-3-4-5
        // Comme le tableau est trié avant cette fonction, l'ordre est forcement : 2, 3, 4, 5, 14 (As)
        if (m[0].num == 2 && m[1].num == 3 && m[2].num == 4 && m[3].num == 5 && m[4].num == 14) {
            return true;
        }

        // Cas général (vérifie si chaque carte est égale à la précédente + 1)
        for (int i = 0; i < 4; i += 1) {
            if (m[i + 1].num != m[i].num + 1) {
                return false;
            }
        }
        return true;
    }

    // Compte les occurrences de chaque valeur de carte dans la main.
    int[] CompterValeurs(Carte[] m) {
        int[] c = new int[15];
        for (int i = 0; i < 5; i += 1) {
            c[m[i].num] += 1;
        }
        return c;
    }

    // Trie les cartes d'une main par ordre croissant de valeur (Tri à bulles).
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

    // Crée une copie indépendante d'un tableau de cartes.
    Carte[] CopierTableau(Carte[] src) {
        Carte[] dest = new Carte[length(src)];
        for (int i = 0; i < length(src); i += 1) {
            dest[i] = src[i];
        }
        return dest;
    }

    // Affiche le contenu du buffer (tableau de chaînes) ligne par ligne.
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

    // Lit un fichier texte et retourne son contenu sous forme de tableau de chaînes.
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

    // Affiche l'écran d'accueil du jeu.
    void AfficherPageAccueuil() {
        String[] ecran = LireTemplate("visuel/acceuil.txt");
        RendreBuffer(ecran);
    }

    // Affiche l'écran des règles du jeu.
    void AfficherRegles() {
        clear();
        String[] regles = LireTemplate("visuel/regle.md");
        RendreBuffer(regles);
        println("\nAppuyez sur Entrée pour revenir au menu...");
        readString();
    }

    // Crée un paquet neuf de 52 cartes ordonnées.
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

    // Mélange le paquet de cartes en utilisant l'algorithme de Fisher-Yates (adapté).
    void Melanger(Carte[] p) {
        for (int i = 0; i < length(p); i += 1) {
            int r = GenererNombrePseudoAleatoire(length(p));
            Carte t = p[i];
            p[i] = p[r];
            p[r] = t;
        }
    }

    // Retourne le nom lisible d'une carte (Ex: "10♠").
    String NomCarte(Carte c) {
        return LISTE_VALEUR[c.num] + LISTE_COULEUR[c.couleur];
    }

    // Efface le terminal en utilisant les codes ANSI.
    void clear() {
        print("\u001B[H\u001B[2J");
    }

    // ========================================================================================================================
    // TESTS
    // ========================================================================================================================
    Carte newCarte(int v, int c) {
        Carte a = new Carte();
        a.num = v;
        a.couleur = c;
        return a;
    }

    void testCalculerPointsMain() {
        // Test Royal Flush
        Carte[] royal = new Carte[]{newCarte(10, 0), newCarte(V, 0), newCarte(D, 0), newCarte(R, 0), newCarte(A, 0)};
        assertEquals(PTS_ROYAL_FLUSH, CalculerPointsMain(royal));
        
        // Test Main Incomplète (doit retourner 0)
        Carte[] incomplet = new Carte[]{newCarte(2, 0), null, newCarte(5, 2), newCarte(5, 3), newCarte(A, 0)};
        assertEquals(0, CalculerPointsMain(incomplet));
        
        // Test Rien (Carte haute complète)
        Carte[] rien = new Carte[]{newCarte(2, 0), newCarte(4, 1), newCarte(6, 2), newCarte(8, 3), newCarte(10, 0)};
        assertEquals(0, CalculerPointsMain(rien));
    }

    void testEstLigneComplete() {
        Carte[][] grille = new Carte[5][5];
        // Remplir une ligne
        for(int j=0; j<5; j+=1) grille[0][j] = newCarte(2,0);
        assertTrue(EstLigneComplete(grille, 0));
        
        // Ligne vide
        assertFalse(EstLigneComplete(grille, 1));
        
        // Ligne partielle
        grille[2][0] = newCarte(2,0);
        assertFalse(EstLigneComplete(grille, 2));
    }

    void testEstCouleur() {
        Carte[] oui = new Carte[]{newCarte(2, 0), newCarte(5, 0), newCarte(7, 0), newCarte(9, 0), newCarte(R, 0)};
        assertTrue(EstCouleur(oui));
        
        Carte[] non = new Carte[]{newCarte(2, 0), newCarte(5, 0), newCarte(7, 1), newCarte(9, 0), newCarte(R, 0)};
        assertFalse(EstCouleur(non));
    }

void testEstSuite() {
        // Test suite classique
        Carte[] oui = new Carte[]{newCarte(2, 0), newCarte(3, 1), newCarte(4, 0), newCarte(5, 2), newCarte(6, 0)};
        assertTrue(EstSuite(oui));
        
        // Test suite As faible (A, 2, 3, 4, 5) -> Trié en (2, 3, 4, 5, 14)
        Carte[] asFaible = new Carte[]{newCarte(2, 0), newCarte(3, 1), newCarte(4, 0), newCarte(5, 2), newCarte(14, 0)};
        assertTrue(EstSuite(asFaible));

        // Test pas de suite
        Carte[] non = new Carte[]{newCarte(2, 0), newCarte(3, 1), newCarte(4, 0), newCarte(5, 2), newCarte(7, 0)};
        assertFalse(EstSuite(non));
    }

    void testGenererNombrePseudoAleatoire() {
        seedNumber = 12345;
        int res1 = GenererNombrePseudoAleatoire(100);
        assertTrue(res1 >= 0 && res1 < 100);
        
        // Vérification de la reproductibilité
        seedNumber = 12345;
        int res2 = GenererNombrePseudoAleatoire(100);
        assertEquals(res1, res2);
    }

    void testStringToLong() {
        assertEquals(0, StringToLong(""));
        assertEquals(65, StringToLong("A"));
        assertEquals(2001, StringToLong("AB")); // Exemple arbitraire de hash simple
    }

    void testGenererRandomSeedString() {
        String s1 = GenererRandomSeedString(10);
        assertEquals(10, length(s1));
        
        String s2 = GenererRandomSeedString(5);
        assertEquals(5, length(s2));
    }

    void testCalculerScoreLigne() {
        Carte[][] grille = new Carte[5][5];
        // Brelan
        grille[0][0] = newCarte(2, 0);
        grille[0][1] = newCarte(2, 1);
        grille[0][2] = newCarte(2, 2);
        grille[0][3] = newCarte(5, 0);
        grille[0][4] = newCarte(7, 1);
        assertEquals(PTS_BRELAN, CalculerScoreLigne(grille, 0));
        
        // Rien
        grille[1][0] = newCarte(2, 0);
        grille[1][1] = newCarte(4, 1);
        grille[1][2] = newCarte(6, 2);
        grille[1][3] = newCarte(8, 0);
        grille[1][4] = newCarte(10, 1);
        assertEquals(0, CalculerScoreLigne(grille, 1));
    }

    void testCalculerScoreColonne() {
        Carte[][] grille = new Carte[5][5];
        // Flush vertical
        for (int i = 0; i < 5; i += 1) {
            grille[i][0] = newCarte(i + 2, 0);
        }
        grille[4][0] = newCarte(10, 0);
        assertEquals(PTS_FLUSH, CalculerScoreColonne(grille, 0));
        
        // Paire verticale
        grille[0][1] = newCarte(3, 0);
        grille[1][1] = newCarte(3, 1);
        grille[2][1] = newCarte(5, 2);
        grille[3][1] = newCarte(6, 3);
        grille[4][1] = newCarte(7, 0);
        assertEquals(PTS_PAIRE, CalculerScoreColonne(grille, 1));
    }

    void testCompterValeurs() {
        Carte[] main = new Carte[]{newCarte(2, 0), newCarte(2, 1), newCarte(5, 0), newCarte(5, 1), newCarte(5, 2)};
        int[] counts = CompterValeurs(main);
        assertEquals(2, counts[2]);
        assertEquals(3, counts[5]);
        assertEquals(0, counts[10]);
    }

    void testCopierTableau() {
        Carte[] src = new Carte[]{newCarte(1, 0), newCarte(2, 0)};
        Carte[] dest = CopierTableau(src);
        assertEquals(length(src), length(dest));
        
        // Vérification copie profonde (les objets sont les mêmes références ici, mais le tableau est nouveau)
        dest[0] = newCarte(99, 99); // On change la référence dans le nouveau tableau
        assertTrue(src[0].num != 99); // L'ancien ne doit pas changer
    }

    void testCreeNouveauJeu() {
        Carte[] p = CreeNouveauJeu();
        assertEquals(52, length(p));
        assertEquals(2, p[0].num);
        assertEquals(0, p[0].couleur);
        assertEquals(14, p[51].num); // As de Pique (dernier)
    }

    void testNomCarte() {
        Carte c1 = newCarte(12, 1);
        assertEquals("D♥", NomCarte(c1));
        Carte c2 = newCarte(10, 3);
        assertEquals("10♠", NomCarte(c2));
    }

    void testToUpperCases() {
        assertEquals("ABC", toUpperCases("abc"));
        assertEquals("TEST", toUpperCases("TeSt"));
        assertEquals("123", toUpperCases("123"));
    }

    void testLongueurVisuelle() {
        assertEquals(3, longueurVisuelle("abc"));
        assertEquals(5, longueurVisuelle("\u001B[31mHELLO\u001B[0m"));
        assertEquals(0, longueurVisuelle(""));
    }

    void testEstUnNombre() {
        assertTrue(estUnNombre("123"));
        assertTrue(estUnNombre("0"));
        assertFalse(estUnNombre("12a"));
        assertFalse(estUnNombre(""));
        assertFalse(estUnNombre("-5")); // Gère uniquement les entiers positifs simples ici
    }
}