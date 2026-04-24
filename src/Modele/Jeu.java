package Modele;

import java.util.ArrayList;

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
    Historique historique; //Sauvegarde des coups

    public Jeu(int a, int b, int joueur) {
        this.a = a;
        this.b = b;
        this.joueur = joueur;
        historique = new Historique();
        grille = new int[a][b];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                grille[i][j] = 1;
            }
        }
        grille[0][0] = 2;//Le poison
    }

    public Jeu(int joueur) {//Avec dimensions par default
        this.a = 5;
        this.b = 7;
        this.joueur = joueur;
        historique = new Historique();
        grille = new int[a][b];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                grille[i][j] = 1;
            }
        }
        grille[0][0] = 2;//Le poison
    }

    public int getJoueur(){
        return joueur;
    }

    public int[][] getGrille(){
        return grille;
    }

    public void joueurSuivant(){
        joueur = (joueur+1)%2;
    }

    public boolean joue(int l, int c){//True si le joueur a jouéé, false sinon
        if (l>=a || c>= b || l<0 ||c<0 || grille[l][c]==0){
            return false;
        }
        else{
            ArrayList<int[]> listeCases = new ArrayList<>();
            for (int i = l; i<a; i++){
                for (int j = c; j<b; j++){
                    if(grille[i][j]==1) {
                        grille[i][j]=0;
                        listeCases.add(new int[] {i,j});
                    }
                }
            }
            historique.joue(l, c, listeCases);
            joueurSuivant();
            return true;
        }
    }

    public boolean annule(){//True si annuler est possible, false sinon
        if( historique.annule(grille)){
            joueurSuivant();
            return true;
        }
        else{
            return false;
        }
    }

    public boolean refais(){//True si refaire est possible, false sinon
        if( historique.refais(grille)){
            joueurSuivant();
            return true;
        }
        else{
            return false;
        }
    }

    public void afficheGrille(){
        System.out.println("Grille :");
        for(int i=0; i<a; i++){
            for (int j=0; j<b; j++){
                System.out.print(grille[i][j]+" ");
            }
            System.out.println();
        }
    }
}
