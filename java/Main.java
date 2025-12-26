void AfficherEcranResultat(Carte[][] grille, int[] ptsLignes, int[] ptsCols, int scoreTotal, String messageInfo, String pseudoJoueur, String seedJoueur) {
        clear();
        String[] ecran = LireTemplate("visuel/resultat.txt");
        int startLigne = 7;
        int startCol = 4;

        // 1. Grille et Cartes
        for (int i = 0; i < 5; i += 1) {
            for (int j = 0; j < 5; j += 1) {
                if (grille[i][j] != null) {
                    int l = startLigne + (i * 7);
                    int c = startCol + (j * 10);
                    DessinerCarteGrande(ecran, l, c, grille[i][j]);
                }
            }
        }

        // 2. Scores Lignes
        for (int i = 0; i < 5; i += 1) {
            int colScore = 58;
            if (ptsLignes[i] > 999) {
                colScore -= 2; 
            } else if (ptsLignes[i] > 99) {
                colScore -= 1; 
            }
            EcrireDansBuffer(ecran, startLigne + (i * 7) + 3, colScore, "=" + ptsLignes[i]);
        }

        // 3. Scores Colonnes
        for (int j = 0; j < 5; j += 1) {
            EcrireDansBuffer(ecran, 42, 4 + (j * 10), "=" + ptsCols[j]);
        }
        
        // 4. Score Total (MODIFIÉ)
        EcrireDansBuffer(ecran, 45, 23, "TOTAL = " + scoreTotal);

        // 5. Historique
        for (int i = 0; i < length(historiqueJeu); i += 1) {
            EcrireDansBuffer(ecran, LIG_HISTORIQUE_START + i, COL_HISTORIQUE, historiqueJeu[i]);
        }

        // 6. Message d'info (Erreur saisie ou Succès)
        if (length(messageInfo) > 0) {
            EcrireDansBuffer(ecran, 45, 162, messageInfo);
        }

        // 7. Leaderboard (Ordre Inversé)
        ScoreRecord[] recs = ChargerLeaderboard();
        int ligStart = 25; 
        int maxLignes = 20; 
        boolean joueurAffiche = false; 

        for (int i = 0; i < length(recs) && i < maxLignes; i += 1) {
            ScoreRecord r = recs[i];
            
            String sRang = "" + (i + 1);
            String sPseudo = r.pseudo;
            if (length(sPseudo) > 20) {
                sPseudo = substring(sPseudo, 0, 20);
            }
            String sScore = "" + r.score;
            String sSeed = r.seed;

            String color = "";
            boolean estLeJoueur = length(pseudoJoueur) > 0 && equals(r.pseudo, pseudoJoueur) && equals(sSeed, seedJoueur);
            if (estLeJoueur) {
                color = YELLOW;
                joueurAffiche = true;
            }
            
            EcrireDansBuffer(ecran, ligStart + i, 140, color + sSeed + RESET + WHITE + BG_BLACK);
            EcrireDansBuffer(ecran, ligStart + i, 116, color + sScore + RESET + WHITE + BG_BLACK);
            EcrireDansBuffer(ecran, ligStart + i, 93, color + sPseudo + RESET + WHITE + BG_BLACK);
            EcrireDansBuffer(ecran, ligStart + i, 68, color + sRang + RESET + WHITE + BG_BLACK);
        }

        if (length(pseudoJoueur) > 0 && !joueurAffiche) {
            int rangReel = -1;
            for (int k = maxLignes; k < length(recs); k += 1) {
                ScoreRecord r = recs[k];
                if (equals(r.pseudo, pseudoJoueur) && equals(r.seed, seedJoueur)) {
                    rangReel = k + 1;
                }
            }

            if (rangReel != -1) {
                String color = YELLOW;
                String sPseudo = pseudoJoueur;
                 if (length(sPseudo) > 20) {
                    sPseudo = substring(sPseudo, 0, 20);
                }
                EcrireDansBuffer(ecran, 45, 140, color + seedJoueur + RESET + WHITE + BG_BLACK);
                EcrireDansBuffer(ecran, 45, 116, color + scoreTotal + RESET + WHITE + BG_BLACK);
                EcrireDansBuffer(ecran, 45, 93, color + sPseudo + RESET + WHITE + BG_BLACK);
                EcrireDansBuffer(ecran, 45, 68, color + rangReel + RESET + WHITE + BG_BLACK);
            }
        }
        RendreBuffer(ecran);
    }
