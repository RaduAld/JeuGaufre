package Modele;

import java.util.ArrayList;

/*
 Démarrer une nouvelle partie (en abandonnant l'état courant)
 fonctions annuler et rejouer (conservation de l'historique de tous les coups)
 sauvegarder et restaurer une partie et son historique complet
 */
public class Jeu {
    int[][] grille;         // lignes * colonnes
    int joueur;             // 0 ou 1
    int lignes;             // 5 en standard
    int colonnes;           // 7 en standard
    Historique historique;  // Sauvegarde des coups
    // 0 - vide, 1 - gaufre, 2 - poison

    public Jeu(int lignes, int colonnes, int joueur) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.joueur = joueur;
        historique = new Historique();
        grille = new int[lignes][colonnes];
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                grille[i][j] = 1;
            }
        }
        grille[0][0] = 2;   // Le poison
    }

    public Jeu(int joueur) {    //  Avec dimensions par default
        this(5, 7, joueur);
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

    //  True si le joueur a joué, false sinon
    public boolean joue(int l, int c){
        if (l>= lignes || c>= colonnes || l<0 ||c<0 || grille[l][c]==0){
            return false;
        }
        else{
            ArrayList<int[]> listeCases = new ArrayList<>();
            for (int i = l; i< lignes; i++){
                for (int j = c; j< colonnes; j++){
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

    // True si annuler est possible, false sinon
    public boolean annule(){
        if( historique.annule(grille)){
            joueurSuivant();
            return true;
        }
        else{
            return false;
        }
    }

    // True si refaire est possible, false sinon
    public boolean refais(){
        if( historique.refais(grille)){
            joueurSuivant();
            return true;
        }
        else{
            return false;
        }
    }

    public void afficheGrille(){
        System.out.println("-------------------");
        System.out.println("-----Grille -------");
        System.out.println("-------------------");
        for(int i = 0; i< lignes; i++){
            for (int j = 0; j< colonnes; j++){
                System.out.print(grille[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println("-------------------");
        System.out.println();
    }
}
