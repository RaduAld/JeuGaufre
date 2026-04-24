package Modele;

import java.util.ArrayList;

public class Historique {
    Pile<Coup> passe;
    Pile<Coup> futur;
    Historique(){
        passe = new Pile<>();
        futur = new Pile<>();
    }

    void joue (int i, int j, ArrayList<int[]> changedToZero){
        Coup c = new Coup(i, j, changedToZero);
        passe.empiler(c);
        futur.videPile();
    }

    boolean annule(int[][] grille){
        if(passe.estVide()){
            System.out.println("Impossible d'annuler");
            return false;
        }
        Coup c = passe.depiler();
        for (int[] coord : c.changedToZero){
            grille[coord[0]][coord[1]]= 1;
        }
        futur.empiler(c);
        return true;
    }

    boolean refais (int[][] grille){
        if(futur.estVide()){
            System.out.println("Impossible de refaire");
            return false;
        }
        Coup c = futur.depiler();
        for (int[] coord : c.changedToZero){
            grille[coord[0]][coord[1]]= 0;
        }
        passe.empiler(c);
        return true;

    }

}
