package Modele;

/**
 * Représente un coup joué dans le Jeu de Gaufre.
 *
 * Stocke :
 *   - (l, c)      : case cliquée en coordonnées de jeu (ligne l, colonne c)
 *   - savedGrille : copie complète du vecteur grille AVANT le coup (pour undo)
 *
 * Undo : System.arraycopy(savedGrille → grille)
 * Redo : GrilleHelper.applyMove(grille, l, c, lignes, colonnes)
 */
public class Coup {

    private final int l;                    // ligne de jeu (0 = haut, poison)
    private final int c;                    // colonne de jeu (0 = gauche)
    private final boolean[] savedGrille;    // copie complète du vecteur avant le coup

    public Coup(int l, int c, boolean[] savedGrille) {
        this.l            = l;
        this.c            = c;
        this.savedGrille  = savedGrille;
    }

    public int       getL()            { return l; }
    public int       getC()            { return c; }
    public boolean[] getSavedGrille()  { return savedGrille; }
}