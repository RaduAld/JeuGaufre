import Controleur.ControleurMediateur;
import Modele.Jeu;
import Vue.CollecteurEvenements;
import Vue.InterfaceGraphique;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int firstPlayer = 0;
        Jeu monJeu = new Jeu(firstPlayer);
        

      /*  try {
            // loads the game from the given nomFichier file
            monJeu.charger("game0.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            // saves the game in the given nomFichier file
            monJeu.sauvegarder("game1.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
        CollecteurEvenements c = new ControleurMediateur(monJeu);
        InterfaceGraphique.demarrer(monJeu,c);
    }
}
