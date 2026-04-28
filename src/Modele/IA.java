package Modele;

import Global.Configuration;

import java.util.ArrayList;

public abstract class IA {
    //j'ai change de private a public, mauvaise pratique sans doute...
    public Jeu jeu;

    public static IA nouvelle(Jeu j) {
        IA resultat = null;
        // Méthode de fabrication pour l'IA, qui crée le bon objet selon la config
        String type = Configuration.lisChaine("IA");
        switch (type) {
            case "Aleatoire":
                resultat = new IAAleatoire();
                break;
            case "GagnantPerdant":
                resultat = new IAGagnantPerdant();
                break;
            case "EtOu":
                resultat = new IAEtOu();
                break;
            default:
                Configuration.erreur("IA de type " + type + " non supportée");
        }
        if (resultat != null) {
            resultat.jeu = j;
        }
        return resultat;
    }

    public Coup elaboreCoup() {
        //grille = jeu.getGrille().clone();
        return joue();
    }

    Coup joue() {
        return null;
    }
}

