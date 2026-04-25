package Modele;

import java.util.ArrayList;

/*
 Démarrer une nouvelle partie (en abandonnant l'état courant)
 fonctions annuler et rejouer (conservation de l'historique de tous les coups)
 sauvegarder et restaurer une partie et son historique complet
 */
public class Jeu {
    private int[][] grille;         // lignes * colonnes
    private int joueur;             // 0 ou 1
    private int lignes;             // 5 en standard
    private int colonnes;           // 7 en standard
    private Historique historique;  // Sauvegarde des coups
    int vainqueur;
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
    public int getJoueur() { return joueur; }
    public int getLignes() { return lignes; }
    public int getColonnes() { return colonnes; }
    public int[][] getGrille() { return grille; }
    public Historique getHistorique() { return historique; }

    public void joueurSuivant(){
        joueur = (joueur+1)%2;
    }

    //  True si le joueur a joué, false sinon
    public boolean joue(int l, int c){
        if (jeuTermine() || l>= lignes || c>= colonnes || l<0 ||c<0 || grille[l][c]==0){
            return false;
        }
        else{
            ArrayList<int[]> listeCases = new ArrayList<>();
            for (int i = l; i< lignes; i++){
                for (int j = c; j< colonnes; j++){
                    if(grille[i][j]>0) {
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

    public boolean jeuTermine(){
        return grille[0][0]== 0;
    }

    public void nouvellePartie(){
        historique = new Historique();
        grille = new int[lignes][colonnes];
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                grille[i][j] = 1;
            }
        }
        grille[0][0] = 2;
        joueur = 0;
    }

    // True si annuler est possible, false sinon
    public Coup annule(){
        Coup c = historique.annule(grille);
        if(c != null){
            joueurSuivant();
            return c;
        }
        else{
            return null;
        }
    }

    public boolean peutAnnuler(){
        return historique.peutAnnuler();
    }
    public boolean peutRefaire(){
        return historique.peutRefaire();
    }

    // True si refaire est possible, false sinon
    public Coup refais(){
        Coup c = historique.refais(grille);
        if(c != null){
            joueurSuivant();
            return c;
        }
        else{
            return null;
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
