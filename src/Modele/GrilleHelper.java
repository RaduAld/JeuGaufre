package Modele;

/**
 * Utilitaires purs sur le vecteur de bits grille (boolean[M+N])
 *
 * Encodage staircase (chemin en escalier) :
 *   false (0) → pas droite : avance le compteur de colonnes
 *   true  (1) ↓ pas bas    : enregistre la largeur de la ligne courante
 *
 * Décodage hauteur(l) :
 *   Scanner grille gauche→droite en cumulant col (incrémenté sur false)
 *   Au l-ième true, retourner col
 *
 * Grille pleine M×N : [false×N, true×M]
 *   Exemple 3×4 : [F,F,F,F,T,T,T] → largeurs [4,4,4]
 */
class GrilleHelper {

    private GrilleHelper() {}

    // -------------------------------------------------------------------------
    // Décodage
    // -------------------------------------------------------------------------

    /*
     * Largeur de la ligne l (nombre de cases présentes depuis col 0)
     * O(M+N)
     */
    static int hauteur(boolean[] grille, int l) {
        int col       = 0;
        int truesSeen = 0;
        for (boolean bit : grille) {
            if (!bit) {
                col++;
            } else {
                if (truesSeen == l) return col;
                truesSeen++;
            }
        }
        return 0;
    }

    /**
     * Toutes les largeurs [w0, w1, ..., w_{M-1}] d'un coup depuis grille
     * Plus efficace que d'appeler hauteur() M fois (un seul scan)
     *
     * @param lignes M
     */
    static int[] toutesLargeurs(boolean[] grille, int lignes) {
        int[] w       = new int[lignes];
        int col       = 0;
        int truesSeen = 0;
        for (boolean bit : grille) {
            if (!bit) {
                col++;
            } else {
                if (truesSeen < lignes) w[truesSeen] = col;
                truesSeen++;
                if (truesSeen == lignes) break;
            }
        }
        return w;
    }

    /**
     * Index dans grille du l-ième true (0-indexé)
     * Retourne -1 si introuvable
     */
    static int posOfLthTrue(boolean[] grille, int l) {
        int truesSeen = 0;
        for (int k = 0; k < grille.length; k++) {
            if (grille[k]) {
                if (truesSeen == l) return k;
                truesSeen++;
            }
        }
        return -1;
    }

    /** True si c < hauteur(l). */
    static boolean estPresente(boolean[] grille, int l, int c) {
        return c < hauteur(grille, l);
    }

    /**
     * True si le jeu est terminé : hauteur(0)==0 ↔ grille[0]==true
     * (le premier bit est immédiatement un pas ↓, donc la ligne 0 est vide)
     */
    static boolean jeuTermine(boolean[] grille) {
        return grille.length > 0 && grille[0];
    }

    // -------------------------------------------------------------------------
    // Sauvegarde de segment pour undo
    // -------------------------------------------------------------------------

    /**
     * Copie défensive de grille[0..pos_r] (inclus), où pos_r est l'index
     * du l-ième true Ce segment est stocké dans Coup pour l'undo
     *
     * Taille = hauteur(l) + (l+1)  (faux cumulés jusqu'à la ligne l + l+1 vrais)
     */
    static boolean[] saveSegment(boolean[] grille, int l) {
        int pos_r = posOfLthTrue(grille, l);
        boolean[] seg = new boolean[pos_r + 1];
        System.arraycopy(grille, 0, seg, 0, pos_r + 1);
        return seg;
    }

    // -------------------------------------------------------------------------
    // Application d'un coup
    // -------------------------------------------------------------------------

    /**
     * Applique le coup (l, c) sur grille en place
     *
     * Algorithme :
     *   1. Lire toutes les largeurs [w0..w_{M-1}] depuis grille (un seul scan)
     *   2. Appliquer le cap : w[i] = min(w[i], c) pour i dans [0..l]
     *   3. Ré-encoder toute la grille depuis zéro avec les nouvelles largeurs
     *
     * Cette approche — lire tout, modifier, réécrire tout — évite tout
     * problème de décalage de bits et reste O(M+N)
     *
     * @param grille      vecteur M+N bits, modifié en place
     * @param l           ligne du coup
     * @param c           colonne du coup
     * @param lignes      M
     * @param colonnes    N
     */
    static void applyMove(boolean[] grille, int l, int c,
                          int lignes, int colonnes) {
        // Étape 1 : lire toutes les largeurs
        int[] w = toutesLargeurs(grille, lignes);

        // Étape 2 : appliquer le cap sur les lignes 0..l uniquement
        for (int i = 0; i <= l; i++) {
            w[i] = Math.min(w[i], c);
        }

        // Étape 3 : réécrire toute la grille depuis zéro
        encode(grille, w, colonnes);
    }

    /**
     * Encode le tableau de largeurs w[] dans grille en place
     *
     * Algorithme staircase :
     *   Pour chaque ligne i (0..M-1) :
     *     Émettre (w[i] - w[i-1]) faux  (avec w[-1] = 0 par convention)
     *     Émettre 1 vrai
     *   Puis émettre (N - w[M-1]) faux  (colonnes non couvertes par la dernière ligne)
     *
     * Invariant : total faux = N, total vrais = M → taille écrite = M+N ✓
     *
     * @param grille   tableau de taille M+N, réécrit en entier
     * @param w        largeurs des M lignes (non-décroissantes)
     * @param colonnes N
     */
    static void encode(boolean[] grille, int[] w, int colonnes) {
        int pos  = 0;
        int prev = 0;
        for (int width : w) {
            int delta = width - prev;
            for (int k = 0; k < delta; k++) grille[pos++] = false;
            grille[pos++] = true;
            prev = width;
        }
        int remaining = colonnes - prev;
        for (int k = 0; k < remaining; k++) grille[pos++] = false;
    }
}