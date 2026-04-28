package Controleur;

import Global.Configuration;
import Modele.Coup;
import Modele.IA;

import java.util.ArrayList;
import Modele.Pile;

class AnimationJeuAutomatique extends Animation {
    IA joueur;
    Coup enAttente = null;

    AnimationJeuAutomatique(int lenteur, IA j, ControleurMediateur c) {
        super(lenteur, c);
        joueur = j;
        control = c;
    }

    @Override
    public void miseAJour() {
        if ((enAttente == null))
            enAttente = joueur.elaboreCoup();
        if ((enAttente == null))
            Configuration.erreur("Bug : l'IA n'a joué aucun coup");
        else
        {
            Coup cp = enAttente;
            enAttente = null; 
            control.joue(cp);
        }
    }
}
