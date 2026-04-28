package Modele;

/**
 * Représente un coup joué dans le Jeu de Gaufre.
 *
 * Stocke :
 *   - (l, c) : la case cliquée en coordonnées de JEU (ligne l, colonne c)
 *   - savedSegment : copie complète du vecteur grille AVANT le coup, pour undo
 *
 * Undo : System.arraycopy(savedSegment → grille)
 * Redo : GrilleHelper.applyMove(grille, l, c, lignes, colonnes)
 */
public class Coup {

    private final int l;            // ligne de jeu (0 = haut, poison)
    private final int c;            // colonne de jeu (0 = gauche)
    private final boolean[] savedSegment;   // copie complète du vecteur avant le coup

    public Coup(int l, int c, boolean[] savedSegment) {
        this.l             = l;
        this.c             = c;
        this.savedSegment  = savedSegment;
    }

    public int       getL()            { return l; }
    public int       getC()            { return c; }
    public boolean[] getSavedSegment() { return savedSegment; }
}