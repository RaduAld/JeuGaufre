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

    // Getters
    public boolean peutAnnuler() { return !passe.estVide(); }
    public boolean peutRefaire() { return !futur.estVide(); }
}