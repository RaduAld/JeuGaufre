package Modele;

import java.util.ArrayList;

public class Coup {
    //Coordonnées de la case cliquée
    private int i;
    private int j;
    //Liste des index linéaires (i*colonnes+j) passés à false lors de ce coup (pour l'historique)
    ArrayList<Integer> changedToFalse;

    public Coup(int i, int j, ArrayList<Integer> changedToFalse) {
        this.i = i;
        this.j = j;
        this.changedToFalse = changedToFalse;
    }

    //Getters
    public int getI() { return i; }

    public int getJ() { return j; }

    public ArrayList<Integer> getChangedToFalse() { return changedToFalse; }
}