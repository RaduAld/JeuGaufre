package Modele;

import java.util.ArrayList;

/**
 * Historique des coups du Jeu de Gaufre
 *
 * Deux piles :
 *   passe  — coups joués (sommet = dernier coup)
 *   futur  — coups annulés prêts à être refaits (sommet = prochain redo)
 *
 * Undo : restaure grille[0..savedSegment.length-1] depuis la sauvegarde
 * Redo : ré-applique le coup via GrilleHelper.applyMove (lire/modifier/réécrire)
 */
public class Historique {

    private final Pile<Coup> passe;
    private final Pile<Coup> futur;

    Historique() {
        passe = new Pile<>();
        futur = new Pile<>();
    }

    void joue(Coup coup) {
        passe.empiler(coup);
        futur.videPile();
    }

    /**
     * Annule le dernier coup : restaure grille[0..savedSegment.length-1]
     */
    Coup annule(boolean[] grille) {
        if (passe.estVide()) {
            System.out.println("Impossible d'annuler : aucun coup dans le passé.");
            return null;
        }
        Coup c = passe.depiler();
        boolean[] seg = c.getSavedSegment();
        System.arraycopy(seg, 0, grille, 0, seg.length);
        futur.empiler(c);
        return c;
    }

    /**
     * Rejoue le coup suivant : ré-applique son effet via GrilleHelper.applyMove
     *
     * @param grille   vecteur de bits de Jeu, modifié en place
     * @param lignes   M — nécessaire pour lire toutes les largeurs
     * @param colonnes N — nécessaire pour réécrire toute la grille
     */
    Coup refais(boolean[] grille, int lignes, int colonnes) {
        if (futur.estVide()) {
            System.out.println("Impossible de refaire : aucun coup dans le futur.");
            return null;
        }
        Coup c = futur.depiler();
        GrilleHelper.applyMove(grille, c.getL(), c.getC(), lignes, colonnes);
        passe.empiler(c);
        return c;
    }

    // -------------------------------------------------------------------------
    // Sauvegarde / Chargement
    // -------------------------------------------------------------------------

    public ArrayList<Coup> getCoupsPasse() { return extraireOrdreChronologique(passe); }
    public ArrayList<Coup> getCoupsFutur() { return extraireOrdreChronologique(futur); }

    private ArrayList<Coup> extraireOrdreChronologique(Pile<Coup> pile) {
        ArrayList<Coup> inversee = new ArrayList<>();
        while (!pile.estVide()) inversee.add(pile.depiler());
        for (int i = inversee.size() - 1; i >= 0; i--) pile.empiler(inversee.get(i));
        ArrayList<Coup> chrono = new ArrayList<>(inversee.size());
        for (int i = inversee.size() - 1; i >= 0; i--) chrono.add(inversee.get(i));
        return chrono;
    }

    public void restaurer(ArrayList<Coup> passeChrono, ArrayList<Coup> futurChrono) {
        passe.videPile();
        futur.videPile();
        for (Coup c : passeChrono) passe.empiler(c);
        for (Coup c : futurChrono) futur.empiler(c);
    }

    public boolean peutAnnuler() { return !passe.estVide(); }
    public boolean peutRefaire() { return !futur.estVide(); }
}