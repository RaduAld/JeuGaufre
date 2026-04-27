package Modele;

import java.util.ArrayList;

public class Historique {
    private Pile<Coup> passe;
    private Pile<Coup> futur;

    Historique() {
        passe = new Pile<>();
        futur = new Pile<>();
    }

    // Enregistre un coup : stocke les index linéaires des cases passées à false
    void joue(int i, int j, ArrayList<Integer> changedToFalse) {
        Coup c = new Coup(i, j, changedToFalse);
        passe.empiler(c);
        futur.videPile();
    }

    // Annule le dernier coup : remet à true toutes les cases du coup dépilé
    Coup annule(boolean[] grille) {
        if (passe.estVide()) {
            System.out.println("Impossible d'annuler");
            return null;
        }
        Coup c = passe.depiler();
        for (int k : c.changedToFalse) {
            grille[k] = true;
        }
        futur.empiler(c);
        return c;
    }

    // Rejoue le coup suivant : remet à false toutes les cases du coup dépilé
    Coup refais(boolean[] grille) {
        if (futur.estVide()) {
            System.out.println("Impossible de refaire");
            return null;
        }
        Coup c = futur.depiler();
        for (int k : c.changedToFalse) {
            grille[k] = false;
        }
        passe.empiler(c);
        return c;
    }

    // -------------------------------------------------------------------------
    // Sauvegarde / Chargement
    // -------------------------------------------------------------------------

    // Retourne les coups du passé du plus ancien au plus récent (ordre de jeu).
    // Draine la pile puis la reconstitue à l'identique — ne modifie pas l'état logique.
    public ArrayList<Coup> getCoupsPasse() {
        return extraireOrdreChronologique(passe);
    }

    // Retourne les coups du futur du prochain redo au plus lointain.
    // Draine la pile puis la reconstitue à l'identique — ne modifie pas l'état logique.
    public ArrayList<Coup> getCoupsFutur() {
        return extraireOrdreChronologique(futur);
    }

    // Draine la pile dans une liste (sommet en tête), puis la recharge à l'identique.
    // Retourne la liste dans l'ordre chronologique (base de pile = index 0).
    private ArrayList<Coup> extraireOrdreChronologique(Pile<Coup> pile) {
        // Étape 1 : vider la pile dans une liste temporaire (sommet d'abord)
        ArrayList<Coup> inversee = new ArrayList<>();
        while (!pile.estVide()) {
            inversee.add(pile.depiler());
        }
        // Étape 2 : remettre dans la pile (restitue l'ordre original)
        for (int i = inversee.size() - 1; i >= 0; i--) {
            pile.empiler(inversee.get(i));
        }
        // Étape 3 : inverser la liste pour avoir l'ordre chronologique (base = index 0)
        ArrayList<Coup> chrono = new ArrayList<>(inversee.size());
        for (int i = inversee.size() - 1; i >= 0; i--) {
            chrono.add(inversee.get(i));
        }
        return chrono;
    }

    // Reconstruit les piles à partir de listes ordonnées (utilisé au chargement).
    // passeChrono : du plus ancien au plus récent.
    // futurChrono : du prochain redo au plus lointain.
    public void restaurer(ArrayList<Coup> passeChrono, ArrayList<Coup> futurChrono) {
        passe.videPile();
        futur.videPile();
        for (Coup c : passeChrono) {
            passe.empiler(c);
        }
        for (Coup c : futurChrono) {
            futur.empiler(c);
        }
    }

    // Getters
    public boolean peutAnnuler() { return !passe.estVide(); }
    public boolean peutRefaire() { return !futur.estVide(); }
}