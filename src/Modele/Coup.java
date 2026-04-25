package Modele;

import java.util.ArrayList;

public class Coup {
    //Coordonnées de la case cliquée
    private int i;
    private int j;
    //Liste des cases passées à 0 lors de ce coup (pour l'historique)
    ArrayList<int[]> changedToZero;

    public Coup (int i, int j, ArrayList<int[]> changedToZero){
        this.i = i;
        this.j = j;
        this.changedToZero = changedToZero;
    }

    //Getters
    public int getI(){ return i; }

    public int getJ(){ return j; }

    public ArrayList<int[]> getChangedToZero(){ return changedToZero; }

}
