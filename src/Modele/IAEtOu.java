package Modele;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class IAEtOu extends IA{
    HashMap<boolean[], Boolean> hashmap = new HashMap<>();

    boolean[] initialConfig(){
        boolean[] init_config = new boolean[jeu.getColonnes() + jeu.getLignes()];
        for (int i=0; i<jeu.getColonnes(); i++){
            init_config[i] = true;
        }
        for (int i=jeu.getColonnes(); i<jeu.getLignes()+jeu.getColonnes(); i++){
            init_config[i] = false;
        }
        return init_config;
    }
    boolean[] losingConfig(){
        boolean[] losing_config = new boolean[jeu.getColonnes() + jeu.getLignes()];
        for (int i=0; i<jeu.getLignes(); i++){
            losing_config[i] = false;
        }
        for (int i=jeu.getLignes(); i<jeu.getColonnes()+jeu.getLignes(); i++){
            losing_config[i] = true;
        }
        return losing_config;
    }
    boolean compareConfig(boolean[] config1,boolean[] config2){
        if (config1.length != config2.length) return false;
        for(int i=0; i<config1.length; i++){
            if (config1[i] != config2[i]) return false;
        }
        return true;
    }

    boolean[] copy(boolean[] configuration){
        boolean[] result = new boolean[configuration.length];
        for(int i=0; i<configuration.length; i++){
            result[i] = false;
            if (configuration[i]) result[i] = true;
        }
        return result;
    }

    List<boolean[]> get_children(boolean[] configuration){
        List<boolean[]> children = new ArrayList<>();
        for(int i=0; i<configuration.length; i++) {
            if (configuration[i]) {
                for (int j = i + 1; j < configuration.length; j++) {
                    if (!configuration[j]) {
                        boolean[] configPermutation = copy(configuration);
                        configPermutation[i] = false;
                        configPermutation[j] = true;
                        children.add(configPermutation);
                    }
                }
            }
        }
        return children;
    }

    boolean evaluate(boolean[] configuration){
        // if configuration already exists
        for (boolean[] state : this.hashmap.keySet()) {
            if (compareConfig(configuration, state)){
                return this.hashmap.get(state);
            }
        }

        if (compareConfig(configuration, losingConfig())){
            hashmap.put(configuration, false);
            return false;
        }
        // generate all subtrees and evaluate them
        List<boolean[]> possible_coupes = get_children(configuration);
        for (boolean[] state : possible_coupes){
            boolean outcome = evaluate(state);
            // if exists winning outcome, we return true
            if (!outcome){
                hashmap.put(configuration, true);
                return true;
            }
        }
        // if all outcomes of subtrees losing, return false
        hashmap.put(configuration, false);
        return false;
    }

    Coup getCoup(boolean[] init, boolean[] fin){
        int c = 0;
        int l = jeu.getLignes();
        if (init.length != fin.length) return null;
        int n = init.length;
        int i = 0;
        while (i < n && init[i] == fin[i]){
            if (init[i]) c++;
            else l--;
            i++;
        }
        if (i < n){
            while(!fin[i]){
                l--;
                i++;
            }
            return new Coup(l, c, init);
        }
        return null;
    }

    List<boolean[]> get_winning_children(boolean[] init){
        List<boolean[]> children = get_children(init);
        List<boolean[]> winning_children = new ArrayList<>();
        for(boolean[] child : children){
            if (evaluate(child)) {
                winning_children.add(child);
            }
        }
        return winning_children;
    }

    boolean[] getFinalConfig(boolean[] init){
        List<boolean[]> children = get_winning_children(init);
        if (children.isEmpty()) return losingConfig();
        Random r = new Random();
        int n = r.nextInt(children.size());
        return children.get(n);
    }

    @Override
    public Coup joue(){
       // evaluate(initialConfig());
        boolean[] init = copy(jeu.getGrille());
        return getCoup(init, getFinalConfig(init));
    }
}
