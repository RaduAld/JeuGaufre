package Modele;

import java.util.ArrayList;

public class Coup {
    //Coordonnées de la case cliquée
    int i;
    int j;
    //Liste des cases passées à 0 lors de ce coup (pour l'historique)
    ArrayList<int[]> changedToZero;
    public Coup (int i, int j, ArrayList<int[]> changedToZero){
        this.i = i;
        this.j = j;
        this.changedToZero = changedToZero;
    }
}
