package Controleur;
import java.io.IOException;

import Global.Configuration;
import Modele.Coup;
import Modele.IA;
import Modele.Jeu;
//import Modele.Mouvement;
//import Structures.Iterateur;
//import Structures.Sequence;
import Vue.CollecteurEvenements;
import Vue.InterfaceUtilisateur;

public class ControleurMediateur implements CollecteurEvenements {
    Jeu jeu;
    InterfaceUtilisateur vue;
    int lenteurPas;
    Animation mouvement;
    boolean animationsSupportees, animationsActives;
    int lenteurJeuAutomatique;
    IA joueurAutomatique;
    boolean IAActive;
    AnimationJeuAutomatique animationIA;
    IA[] joueursAutomatiques; // on va faire jouer des IA l'une contre l'autre, ça remplace joueurAutomatique
    int[] typeJoueur; // humain == 0, IA == 1

    public ControleurMediateur(Jeu j) {
        jeu = j;
        mouvement = null;
        // Tant qu'on ne reçoit pas d'évènement temporel, on n'est pas sur que les
        // animations soient supportées (ex. interface textuelle)
        animationsActives = false;
        animationsSupportees = false;
        typeJoueur = new int[2];
        //on initialise les joueurs à humain par défaut
        typeJoueur[0] = 0;
        typeJoueur[1] = 0;
        joueursAutomatiques = new IA[2];
    }

    void joue(Coup cp) {
        if (cp != null) {
            jeu.joue(cp.getL(), cp.getC());
            animationIA = null;
            testFin();
        } else {
            Configuration.alerte("Coup null fourni, probablement un bug dans l'IA");
        }
    }

    void annule() {
        if (jeu.peutAnnuler()) {
            Coup cp = jeu.annule();
        }
    }

    void refait() {
        if (jeu.peutRefaire()) {
            Coup cp = jeu.refais();
        }
    }

void sauvegarder() {
    try {
        jeu.sauvegarder("game1.txt");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

void restaurer() {
    try {
        jeu.charger("game1.txt");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    private void testFin() {
            if (jeu.jeuTermine()){
                int gagnant = (jeu.getJoueur() + 1) % 2;
                Configuration.info("Partie finie ! Le vainqueur est: : "+ gagnant);
            }
    }

    public void nouvellePartie(){
        jeu.nouvellePartie();
    }

    public void basculeModeJoueur(){
        //flip entre joueur et IA dans le player
        //car quand on lance la partie, on est par défaut en player vs player, donc on voudra activer l'ia pour le second JOUEUR
        if(typeJoueur[1] == 0){
            typeJoueur[1] = 1;
            if(joueursAutomatiques[1] == null){
                joueursAutomatiques[1] = IA.nouvelle(jeu);
            }
             animationIA = null;
             Configuration.info("Joueur vs IA");
        }
        else{
            //on est deja dans le cas où c'est une IA, on la FLIP vers un joueur
             typeJoueur[1] = 0;
             animationIA = null; 
             Configuration.info("Joueur vs Joueur");
        }
    }

    // anticipation du clic souris
    @Override
    public void clicSouris(int l, int c){
        if(!jeu.jeuTermine() && typeJoueur[jeu.getJoueur()] == 0){
            //partie pas encore finie + c un humain qui joue actuellement
            boolean coupValide = jeu.joue(l, c);
            if(coupValide)
                testFin();
        }
    }

    @Override
    public void toucheClavier(String touche) {
        switch (touche) {
            case "Undo":
                annule();
                break;
            case "Restore":
                restaurer();
                break;
            case "Save":
                sauvegarder();
                break;
            case "Redo":
                refait();
                break;
            case "Quit":
                System.exit(0);
                break;
            case "IA":
                basculeModeJoueur();
                break;
            case "Nouvelle":
                nouvellePartie();
                break;
           case "JoueurVsJoueur":
                typeJoueur[0] = 0;
                typeJoueur[1] = 0;
                animationIA = null;
                joueursAutomatiques[1] = null;
                Configuration.info("Joueur vs Joueur");
                break;
            case "Full":
                // a implementer plus tard
                //vue.toggleFullscreen();
                break;
            default:
                System.out.println("Touche inconnue : " + touche);
        }
    }

        @Override
    public void ajouteInterfaceUtilisateur(InterfaceUtilisateur v) {
        vue = v;
    }
@Override
public void tictac() {
    if (!jeu.jeuTermine() && typeJoueur[jeu.getJoueur()] == 1) {
        if (animationIA == null) {
            lenteurJeuAutomatique = Configuration.lisInt("LenteurJeuAutomatique");
            animationIA = new AnimationJeuAutomatique(
                lenteurJeuAutomatique,
                joueursAutomatiques[jeu.getJoueur()],
                this
            );
        }
        animationIA.tictac();
    }
}
}
