package Modele;
import java.util.ArrayList;
import java.util.Random;

public class IAAleatoire extends IA{
    @Override
    public Coup joue(){
        int k = 0;
        for (int i = 0; i < jeu.getLignes(); i++){
            for (int j = 0; j < jeu.getColonnes(); j++){
                if (jeu.estPresente(i,j))
                    k++;
            }
        }
        Random rnd = new Random();
        int val = rnd.nextInt(k);
        int numCaseAChoisir = 0;
        Coup coup = null;
        for (int i = 0; i < jeu.getLignes(); i++){
            for (int j = 0; j < jeu.getColonnes(); j++){
                if (jeu.estPresente(i,j)){
                    if(numCaseAChoisir == val){
                        //je passe quoi comme liste pour Coup ??? null pour l'instant
                        coup =new Coup(i, j, null);
                        return coup;
                    }
                    numCaseAChoisir++;
                }
            }
        }
        return coup;
    }
}
