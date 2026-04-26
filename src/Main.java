import Modele.Jeu;
import Vue.InterfaceGraphique;
public class Main {
    public static void main(String[] args) {
        int firstPlayer = 0;
        Jeu monJeu = new Jeu(firstPlayer);
        InterfaceGraphique.demarrer(monJeu);
        monJeu.afficheGrille();
        monJeu.joue(2, 2);
        monJeu.joue(1, 4);
        monJeu.afficheGrille();

        monJeu.annule();
        monJeu.afficheGrille();

        monJeu.annule();
        monJeu.afficheGrille();

        monJeu.refais();
        monJeu.afficheGrille();
    }
}
