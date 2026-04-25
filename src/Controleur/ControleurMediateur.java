package Controleur;
import Global.Configuration;
import Modele.Coup;
import Modele.IA;
import Modele.Jeu;
//import Modele.Mouvement;
//import Structures.Iterateur;
//import Structures.Sequence;
import Vue.CollecteurEvenements;
// import Vue.InterfaceUtilisateur;

public class ControleurMediateur implements CollecteurEvenements {
    Jeu jeu;
    // InterfaceUtilisateur vue;
    int lenteurPas;
    Animation mouvement;
    boolean animationsSupportees, animationsActives;
    int lenteurJeuAutomatique;
    // IA joueurAutomatique;
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
            jeu.joue(cp.getI(), cp.getJ());
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

//    void deplace(int dL, int dC) {
//        if (mouvement == null) {
//            Coup cp = jeu.elaboreCoup(dL, dC);
//            if (cp != null)
//                joue(cp);
//        }
//    }

    private void testFin() {
            if (jeu.jeuTermine()){
                Configuration.info("Partie finie ! Le vainqueur est: : "+ jeu.getJoueur());
                System.exit(0);
            }
    }

    public void nouvellePartie(){
        jeu.nouvellePartie();
    }

    public void basculeModeJoueur(){
        //flip entre joueur et IA dans le player
        //car quand on lance la partie, on est par défaut en player vs player, donc on voudra activer l'ia pour le second joeururuerufhuerih JOUEUR
        if(typeJoueur[1] == 0){
            typeJoueur[1] = 1;
            if(joueursAutomatiques[1] == null){
                joueursAutomatiques[1] = IA.nouvelle(jeu);
            }
            Configuration.info("Joueur vs IA");
        }
        else{
            //on est deja dans le cas où c'est une IA, on la FLIP vers un joueur
            typeJoueur[1] = 0;
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
            case "Full":
                // a implementer plus tard
                //vue.toggleFullscreen();
                break;
            default:
                System.out.println("Touche inconnue : " + touche);
        }
    }


//    public void ajouteInterfaceUtilisateur(InterfaceUtilisateur v) {
//        vue = v;
//    }

    @Override
    public void tictac() {

        if (!jeu.jeuTermine() && typeJoueur[jeu.getJoueur()] == 1)  {
            //c le tour de l'ia
            //MAIS on n'est pas sur d'avoir initialise ses parametres a l'instant t
            if(animationIA == null || animationIA.joueur != joueursAutomatiques[jeu.getJoueur()]){
                    lenteurJeuAutomatique = Configuration.lisInt("LenteurJeuAutomatique");
                    animationIA = new AnimationJeuAutomatique(lenteurJeuAutomatique, joueursAutomatiques[jeu.getJoueur()], this);
            }
            animationIA.tictac();
            //test de fin à implementer ?
        }
    }

//    public void changeEtape() {
//        vue.changeEtape();
//    }

//    public void decale(int versL, int versC, double dL, double dC) {
//        vue.decale(versL, versC, dL, dC);
//    }

//    public void basculeAnimations() {
//        if (animationsSupportees && (mouvement == null)) {
//            animationsActives = !animationsActives;
//            vue.changeEtatAnimations(animationsActives);
//        }
//    }

}
