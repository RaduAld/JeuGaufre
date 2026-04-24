package Modele;

import java.util.ArrayList;

class Pile<T> {

    private ArrayList<T> pile;

    public Pile(){
        pile = new ArrayList<>();
    }

    public boolean estVide(){
        return pile.isEmpty();
    }

    public void empiler(T e){
        pile.add(0, e);
    }

    public T depiler(){
        T e = pile.get(0);
        pile.remove(0);
        return e;
    }

    public void videPile(){
        pile.clear();
    }
}
