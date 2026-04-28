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
//        */
        CollecteurEvenements c = new ControleurMediateur(monJeu);
        InterfaceGraphique.demarrer(monJeu,c);

        //Game test
        // System.out.println("");
        // System.out.println("       JEU DE GAUFRE  3×4        ");
        // System.out.println("");

        // Jeu jeu = new Jeu(3, 4, 0);

        // System.out.println("=== État initial ===");
        // jeu.afficheGrille();

        // // --- Coup 1 : joueur 0 joue (2, 3) ---
        // System.out.println(">>> Joueur 0 joue (2, 3)");
        // jeu.joue(2, 3);
        // jeu.afficheGrille();

        // // --- Coup 2 : joueur 1 joue (1, 1) ---
        // System.out.println(">>> Joueur 1 joue (1, 1)");
        // jeu.joue(1, 1);
        // jeu.afficheGrille();

        // // --- Coup 3 : joueur 0 joue (2, 2) ---
        // System.out.println(">>> Joueur 0 joue (2, 2)");
        // jeu.joue(2, 2);
        // jeu.afficheGrille();

        // // --- Undo : annule le coup 3 ---
        // System.out.println("<<< Annulation du dernier coup");
        // jeu.annule();
        // jeu.afficheGrille();

        // // --- Redo : rejoue le coup 3 ---
        // System.out.println(">>> Refaire le coup annulé");
        // jeu.refais();
        // jeu.afficheGrille();

        // // --- Coup 4 : joueur 1 joue (0, 0) — mange le poison ---
        // System.out.println(">>> Joueur 1 joue (0, 0) — mange le poison !");
        // jeu.joue(0, 0);
        // jeu.afficheGrille();

        // if (jeu.jeuTermine()) {
        //     int perdant = jeu.getJoueur();          // joueurSuivant() a déjà tourné
        //     int gagnant = (perdant + 1) % 2;
        //     System.out.println("Partie terminée — joueur " + perdant
        //             + " a mangé le poison.");
        //     System.out.println("Joueur " + gagnant + " gagne !");
        // }

    }
}
