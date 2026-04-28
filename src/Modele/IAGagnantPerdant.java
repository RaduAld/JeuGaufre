package Modele;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IAGagnantPerdant extends IA{


    @Override
    public Coup joue(){
        List<boolean[]> possible_coupes = Jeu.get_children(jeu.getGrille(),  jeu.getLignes(), jeu.getColonnes());
        if (possible_coupes.size() > 1){
            for(boolean[] state : possible_coupes){
                if (Jeu.compteCasesRestantes(state, jeu.getLignes()) == 1){
                    return Jeu.getCoup(jeu.getGrille(), state, jeu.getLignes());
                }
            }

            Random r = new Random();
            boolean[] state = possible_coupes.get(r.nextInt(possible_coupes.size()));
            while(Jeu.compareGrille(state, Jeu.losingConfig(jeu.getColonnes(), jeu.getLignes()))){
                state = possible_coupes.get(r.nextInt(possible_coupes.size()));
            }
            return Jeu.getCoup(jeu.getGrille(), state, jeu.getLignes());
        }else{
            return Jeu.getCoup(jeu.getGrille(), Jeu.losingConfig(jeu.getColonnes(), jeu.getLignes()), jeu.getLignes());
        }
    }
}
