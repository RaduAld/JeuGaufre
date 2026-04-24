package Modele;

import java.util.ArrayList;

public class Coup {
    int i;
    int j;
    ArrayList<int[]> changedToZero;
    public Coup (int i, int j, ArrayList<int[]> changedToZero){
        this.i = i;
        this.j = j;
        this.changedToZero = changedToZero;
    }
}
