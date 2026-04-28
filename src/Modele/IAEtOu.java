package Modele;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class IAEtOu extends IA{
    HashMap<String, Boolean> hashmap = new HashMap<>();

    String toChaine(boolean[] configuration){
        String res = "";
        for(int i=0; i<configuration.length; i++){
            if(configuration[i]) res += "1";
            else res += "0";
        }
        return res;
    }
    boolean[] toVecteur(String configuration){
        boolean[] res = new boolean[configuration.length()];
        for(int i=0; i<configuration.length(); i++) {
            if (configuration.charAt(i) == '1') res[i] = true;
            else res[i] = false;
        }
        return res;
    }

    boolean evaluate(boolean[] configuration){
        // if configuration already exists
        for (String state : this.hashmap.keySet()) {
            if (Jeu.compareGrille(configuration, toVecteur(state))){
                return this.hashmap.get(state);
            }
        }
        //if we are in a losing config, that means that the previous player played that move (manger 0,0), so it's actually a winning move from the ai's perspective
        if (Jeu.compareGrille(configuration, Jeu.losingConfig(jeu.getColonnes(), jeu.getLignes()))){
            //changing from false to true
            hashmap.put(toChaine(configuration), true);
            return true;
        }
        // generate all subtrees and evaluate them
        List<boolean[]> possible_coupes = Jeu.get_children(configuration,  jeu.getLignes(), jeu.getColonnes());
        for (boolean[] state : possible_coupes){
            boolean outcome = evaluate(state);
            // if exists winning outcome, we return true
            if (!outcome){
                hashmap.put(toChaine(configuration), true);
                return true;
            }
        }
        // if all outcomes of subtrees losing, return false
        hashmap.put(toChaine(configuration), false);
        return false;
    }

    boolean[] getFinalConfig(boolean[] init){
        List<boolean[]> children = Jeu.get_children(init,  jeu.getLignes(), jeu.getColonnes());
        List<boolean[]> winning_children = new ArrayList<>();
        for(boolean[] child : children){
            if (!evaluate(child)) {
                winning_children.add(child);
            }
        }
        if (!winning_children.isEmpty()) {
            Random r = new Random();
            return winning_children.get(r.nextInt(winning_children.size()));
        }
        Random r = new Random();
        return children.get(r.nextInt(children.size()));
    }

    @Override
    public Coup joue(){
        boolean[] init = Jeu.copy(jeu.getGrille());
        return Jeu.getCoup(init, getFinalConfig(init), jeu.getLignes());
    }
}
