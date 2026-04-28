package Modele;

/**
 * Utilitaires purs sur le vecteur de bits grille (boolean[M+N]).
 *
 * ==========================================================================
 * CONVENTION D'ENCODAGE
 * ==========================================================================
 *
 * Le poison est en haut-gauche (ligne 0, col 0).
 * Manger (l, c) supprime toutes les cases (l', c') avec l' >= l ET c' >= c
 * (vers la droite ET vers le bas).
 *
 * L'invariant staircase est donc NON-CROISSANT de haut en bas :
 *   hauteur(0) >= hauteur(1) >= ... >= hauteur(M-1)
 * (la ligne du haut peut avoir plus de cases que la ligne du bas)
 *
 * Pour conserver un vecteur de bits avec un chemin monotone standard,
 * on stocke les lignes en ORDRE INVERSE dans le tableau w[] :
 *   w[0]   = largeur de la ligne M-1 (bas)   — la plus grande
 *   w[M-1] = largeur de la ligne 0   (haut)  — la plus petite (contient poison)
 *
 * Ce tableau w[] est NON-DÉCROISSANT : w[0] <= w[1] <= ... <= w[M-1]
 * et le vecteur de bits l'encode exactement comme avant :
 *   false (0) → pas droite  (→)
 *   true  (1) ↓ pas bas     (↓)
 * Grille pleine M×N : [false×N, true×M]   ex 3×4 : [F,F,F,F,T,T,T]
 *
 * Correspondance : ligne de jeu l  <->  index tableau  M-1-l
 *
 * Poison (ligne 0, col 0) : présent ssi w[M-1] >= 1 ssi grille[M+N-1] n'est
 * pas le seul true restant au dernier rang — plus simplement :
 *   jeuTermine() <=> w[M-1] == 0 <=> le dernier true dans grille est précédé
 *   immédiatement par aucun false, i.e. grille[M+N-M] == true ...
 *   En pratique : hauteur(0) == 0, ce qu'on calcule via w[M-1].
 *
 * ==========================================================================
 */
class GrilleHelper {

    private GrilleHelper() {}

    // -------------------------------------------------------------------------
    // Décodage brut du vecteur → tableau w[] (ordre interne, bas en premier)
    // -------------------------------------------------------------------------

    /**
     * Décode le vecteur grille en tableau w[] de taille M.
     * w[0] = largeur ligne de jeu M-1 (bas), w[M-1] = largeur ligne de jeu 0 (haut).
     * Un seul scan O(M+N).
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

    // -------------------------------------------------------------------------
    // API publique : indices en coordonnées de JEU (ligne 0 = haut)
    // -------------------------------------------------------------------------

    /**
     * Largeur de la ligne de JEU l (cases présentes depuis col 0).
     * Ligne de jeu l  →  index interne  M-1-l.
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
     * True si le jeu est terminé : hauteur de la ligne 0 (haut) == 0.
     * Ligne 0 → index interne M-1 → w[M-1] == 0.
     * Dans le vecteur, w[M-1] est la DERNIÈRE largeur encodée :
     * le dernier true du vecteur est immédiatement précédé de zéro faux.
     * Plus simplement : on décode et on regarde w[M-1].
     */
    static boolean jeuTermine(boolean[] grille, int lignes) {
        int[] w = toutesLargeurs(grille, lignes);
        return w[lignes - 1] == 0;
    }

    // -------------------------------------------------------------------------
    // Sauvegarde du segment pour undo
    // -------------------------------------------------------------------------

    /**
     * Copie défensive complète du vecteur grille, sauvegardée dans Coup pour undo.
     * Taille = M+N bits (l'intégralité de l'état courant).
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
     * Applique le coup (l, c) en coordonnées de JEU sur grille en place.
     *
     * Effet logique : pour toutes les lignes de jeu l' >= l,
     *   hauteur(l') = min(hauteur(l'), c)
     *
     * En termes d'index interne (i = M-1-l') :
     *   lignes de jeu l' in [l .. M-1]  ↔  index internes i in [0 .. M-1-l]
     *   → w[i] = min(w[i], c)  pour i in [0 .. M-1-l]
     *
     * Algorithme : lire tout / cap / réécrire tout. O(M+N).
     */
    static void applyMove(boolean[] grille, int l, int c,
                          int lignes, int colonnes) {
        int[] w = toutesLargeurs(grille, lignes);

        // Index interne maximal affecté par le coup sur la ligne de jeu l
        int maxInternal = lignes - 1 - l;   // = M-1-l

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
     * Staircase :
     *   Pour chaque i (0..M-1) :
     *     émettre (w[i] - w[i-1]) faux  (w[-1] = 0 par convention)
     *     émettre 1 vrai
     *   Puis émettre (N - w[M-1]) faux
     *
     * Total : N faux + M vrais = M+N bits ✓
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