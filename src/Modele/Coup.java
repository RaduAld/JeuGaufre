package Modele;

/**
 * Représente un coup joué dans le Jeu de Gaufre
 *
 * La grille est un vecteur de M+N bits (boolean[] grille) qui encode
 * le chemin en escalier. Un coup (l, c) modifie les bits correspondant
 * aux lignes 0..l dans ce vecteur
 *
 * Pour permettre l'annulation, on sauvegarde la tranche de grille
 * allant de l'index 0 jusqu'à la position du l-ième true (inclus),
 * telle qu'elle était AVANT le coup
 *
 * Undo : recopier savedSegment dans grille[0..savedSegment.length-1]
 * Redo : ré-appliquer le coup (l, c) sur la grille courante
 */
public class Coup {

    // Ligne de la case jouée (0 ≤ l < M)
    private final int l;

    // Colonne de la case jouée (0 ≤ c < N)
    private final int c;

    /**
     * Tranche de grille sauvegardée avant ce coup
     * Contient grille[0..pos_r] où pos_r est l'index du l-ième true dans grille
     * Longueur = hauteur(l) + (l+1)
     *   (hauteur(l) faux accumulés + l+1 vrais pour les lignes 0..l)
     */
    private final boolean[] savedSegment;

    /**
     * @param l            ligne de la case jouée
     * @param c            colonne de la case jouée
     * @param savedSegment copie de grille[0..pos_r] avant le coup
     */
    public Coup(int l, int c, boolean[] savedSegment) {
        this.l = l;
        this.c            = c;
        this.savedSegment = savedSegment;
    }

    public int getL()            { return l; }
    public int getC()            { return c; }
    public boolean[] getSavedSegment() { return savedSegment; }
}