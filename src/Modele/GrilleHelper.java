package Modele;

/**
 * Utilitaires purs sur le vecteur de bits grille (boolean[M+N]).
 *
 * ==========================================================================
 * CONVENTION D'ENCODAGE
 * ==========================================================================
 *
 * Le poison est en haut-gauche (ligne 0, col 0).
 * Manger (l, c) supprime toutes les cases (l', c') avec l' >= l ET c' >= c.
 *
 * Encodage du chemin en escalier :
 *   true  (1) → pas droite  : avance le compteur de colonnes
 *   false (0) ↓ pas bas     : enregistre la largeur de la ligne courante
 *
 * Grille pleine M×N : [true×N, false×M]
 *   Exemple 3×4 : [T,T,T,T,F,F,F]  →  w interne [4,4,4]  →  hauteur = [4,4,4]
 *
 * Invariant staircase (interne) : w[] est NON-DÉCROISSANT.
 * Les lignes sont stockées en ordre INVERSE dans w[] :
 *   w[0]   = largeur ligne de jeu M-1 (bas, la plus grande)
 *   w[M-1] = largeur ligne de jeu 0   (haut, la plus petite, contient le poison)
 *
 * Correspondance : ligne de jeu l  <->  index interne  M-1-l
 *
 * jeuTermine() ↔ hauteur(0) == 0 ↔ w[M-1] == 0
 * ==========================================================================
 */
class GrilleHelper {

    private GrilleHelper() {}

    // -------------------------------------------------------------------------
    // Décodage vecteur → tableau w[] (ordre interne, bas en premier)
    // -------------------------------------------------------------------------

    /**
     * Décode le vecteur grille en tableau w[] de taille M.
     *   true  (1) → col++
     *   false (0) → w[row] = col ; row++
     * w[0] = largeur ligne de jeu M-1 (bas), w[M-1] = largeur ligne de jeu 0 (haut).
     * Un seul scan O(M+N).
     */
    static int[] toutesLargeurs(boolean[] grille, int lignes) {
        int[] w       = new int[lignes];
        int col       = 0;
        int row       = 0;
        for (boolean bit : grille) {
            if (bit) {
                col++;                          // true → pas droite
            } else {
                if (row < lignes) w[row] = col; // false → enregistre largeur
                row++;
                if (row == lignes) break;
            }
        }
        return w;
    }

    // -------------------------------------------------------------------------
    // API publique : indices en coordonnées de JEU (ligne 0 = haut)
    // -------------------------------------------------------------------------

    /**
     * Largeur de la ligne de jeu l.
     * Ligne de jeu l  →  index interne M-1-l.
     */
    static int hauteur(boolean[] grille, int l, int lignes) {
        int[] w = toutesLargeurs(grille, lignes);
        return w[lignes - 1 - l];
    }

    /** True si la case de jeu (l, c) est présente : c < hauteur(l). */
    static boolean estPresente(boolean[] grille, int l, int c, int lignes) {
        return c < hauteur(grille, l, lignes);
    }

    /**
     * True si le jeu est terminé : hauteur(0) == 0  ↔  w[M-1] == 0.
     */
    static boolean jeuTermine(boolean[] grille, int lignes) {
        int[] w = toutesLargeurs(grille, lignes);
        return w[lignes - 1] == 0;
    }

    // -------------------------------------------------------------------------
    // Sauvegarde complète pour undo
    // -------------------------------------------------------------------------

    /**
     * Copie défensive complète du vecteur grille (taille M+N).
     */
    static boolean[] saveGrille(boolean[] grille) {
        boolean[] copy = new boolean[grille.length];
        System.arraycopy(grille, 0, copy, 0, grille.length);
        return copy;
    }

    // -------------------------------------------------------------------------
    // Application d'un coup
    // -------------------------------------------------------------------------

    /**
     * Applique le coup (l, c) en coordonnées de jeu sur grille en place.
     *
     * Effet : hauteur(i) = min(hauteur(i), c)  pour toutes les lignes i >= l.
     * En interne : w[j] = min(w[j], c)  pour j in [0 .. M-1-l].
     *
     * Algorithme : lire tout / cap / réécrire tout. O(M+N).
     */
    static void applyMove(boolean[] grille, int l, int c,
                          int lignes, int colonnes) {
        int[] w = toutesLargeurs(grille, lignes);

        int maxInternal = lignes - 1 - l;
        for (int i = 0; i <= maxInternal; i++) {
            w[i] = Math.min(w[i], c);
        }

        encode(grille, w, colonnes);
    }

    // -------------------------------------------------------------------------
    // Encodage w[] → vecteur de bits
    // -------------------------------------------------------------------------

    /**
     * Encode le tableau de largeurs w[] (non-décroissant) dans grille en place.
     *
     * Staircase (nouvelle convention) :
     *   Pour chaque ligne interne i (0..M-1) :
     *     émettre (w[i] - w[i-1]) vrais   (w[-1] = 0 par convention)
     *     émettre 1 faux
     *   Puis émettre (N - w[M-1]) vrais   (colonnes non encore couvertes)
     *
     * Total : N vrais + M faux = M+N bits ✓
     */
    static void encode(boolean[] grille, int[] w, int colonnes) {
        int pos  = 0;
        int prev = 0;
        for (int width : w) {
            int delta = width - prev;
            for (int k = 0; k < delta; k++) grille[pos++] = true;  // vrais → pas droite
            grille[pos++] = false;                                   // faux  → pas bas
            prev = width;
        }
        int remaining = colonnes - prev;
        for (int k = 0; k < remaining; k++) grille[pos++] = true;   // vrais restants
    }
}