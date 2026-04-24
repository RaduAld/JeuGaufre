package Modele;
/*
démarrer une nouvelle partie (en abandonnant l'état courant)
 fonctions annuler et rejouer (conservation de l'historique de tous les coups)
 sauvegarder et restaurer une partie et son historique complet
 */
public class Jeu {
    int[][] grille;//a*b
    int joueur;//0 ou 1
    int a; //5 en standard
    int b; //7 en standard
    //historique
    Jeu(int a, int b, int joueur) {
        this.a = a;
        this.b = b;
        this.joueur = joueur;
        grille = new int[a][b];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; i++) {
                grille[i][j] = 1;
            }
        }
        grille[0][0] = 2;//Le poison
    }

    Jeu(int joueur) {//Avec dimensions par default
        this.a = 5;
        this.b = 7;
        this.joueur = joueur;
        grille = new int[a][b];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; i++) {
                grille[i][j] = 1;
            }
        }
        grille[0][0] = 2;//Le poison
    }

}
