package Modele;

import java.util.ArrayList;

/*
 Démarrer une nouvelle partie (en abandonnant l'état courant)
 fonctions annuler et rejouer (conservation de l'historique de tous les coups)
 sauvegarder et restaurer une partie et son historique complet
 */
public class Jeu {
    private boolean[] grille;         // lignes * colonnes
    private int joueur;             // 0 ou 1
    private int lignes;             // 5 en standard
    private int colonnes;           // 7 en standard
    private Historique historique;  // Sauvegarde des coups
    int vainqueur;
    // 0 - vide, 1 - gaufre, 2 - poison

    // -------------------------------------------------------------------------
    // Constructeurs
    // -------------------------------------------------------------------------

    public Jeu(int lignes, int colonnes, int joueur) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.joueur = joueur;
        historique = new Historique();
        grille = new boolean[lignes * colonnes];
        initialiserGrille();
    }

    public Jeu(int joueur) {    //  Avec dimensions par default
        this(5, 7, joueur);
    }

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    // Remplit toutes les cases à true (présentes) ; la case poison (0,0) reste true
    // car on distingue poison/gaufre uniquement par position, pas par valeur
    private void initialiserGrille() {
        for (int k = 0; k < grille.length; k++) {
            grille[k] = true;   // true = case présente (1 dans l'ancienne convention)
        }
        // La case (0,0) est le poison : elle reste true mais est identifiable via estPoison()
    }

    // -------------------------------------------------------------------------
    // Conversion coordonnées <-> index
    // -------------------------------------------------------------------------

    // Convertit (ligne, colonne) en index linéaire dans le vecteur
    private int index(int l, int c) {
        return l * colonnes + c;
    }

    // Convertit un index linéaire en ligne
    private int indexToLigne(int k) {
        return k / colonnes;
    }

    // Convertit un index linéaire en colonne
    private int indexToColonne(int k) {
        return k % colonnes;
    }

    // -------------------------------------------------------------------------
    // Accesseurs
    // -------------------------------------------------------------------------

    public int getJoueur() { return joueur; }
    public int getLignes() { return lignes; }
    public int getColonnes() { return colonnes; }

    // Retourne une copie du vecteur interne
    public boolean[] getGrille() {
        boolean[] copie = new boolean[grille.length];
        System.arraycopy(grille, 0, copie, 0, grille.length);
        return copie;
    }
    public Historique getHistorique() { return historique; }

    // -------------------------------------------------------------------------
    // Consultation de l'état d'une case
    // -------------------------------------------------------------------------

    // True si la case (l, c) est encore présente (gaufre ou poison)
    public boolean estPresente(int l, int c) {
        return grille[index(l, c)];
    }

    // True si la case est une gaufre (présente et pas le poison)
    public boolean estGauffre(int l, int c) {
        return estPresente(l, c) && !estPoison(l, c);
    }

    // True si la case est le poison (case (0,0) encore présente)
    public boolean estPoison(int l, int c) {
        return l == 0 && c == 0;
    }

    // True si la case a été mangée
    public boolean estVide(int l, int c) {
        return !grille[index(l, c)];
    }

    // -------------------------------------------------------------------------
    // Logique de jeu
    // -------------------------------------------------------------------------

    public void joueurSuivant(){
        joueur = (joueur + 1) % 2;
    }

    // True si le jeu est terminé (le poison (0,0) a été mangé)
    public boolean jeuTermine() {
        return !grille[0];  // index(0,0) == 0
    }

    //  True si le joueur a joué, false sinon
    public boolean joue(int l, int c) {
        if (jeuTermine() || l >= lignes || c >= colonnes || l < 0 || c < 0 || !grille[index(l, c)]) {
            return false;
        }
        else {
            ArrayList<Integer> listeCases = new ArrayList<>();
            // Mange toutes les cases dans le rectangle [l..lignes[ x [c..colonnes[
            for (int i = l; i < lignes; i++) {
                for (int j = c; j < colonnes; j++) {
                    int k = index(i, j);
                    if (grille[k]) {
                        grille[k] = false;  // false = mangée
                        listeCases.add(k);  // on stocke l'index linéaire
                    }
                }
            }
            historique.joue(l, c, listeCases);
            joueurSuivant();
            return true;
        }
    }

    // -------------------------------------------------------------------------
    // Gestion de la partie
    // -------------------------------------------------------------------------

    public void nouvellePartie() {
        historique = new Historique();
        grille = new boolean[lignes * colonnes];
        initialiserGrille();
        joueur = 0;
    }

    // -------------------------------------------------------------------------
    // Annuler / Refaire
    // -------------------------------------------------------------------------

    public boolean peutAnnuler() {
        return historique.peutAnnuler();
    }

    public boolean peutRefaire() {
        return historique.peutRefaire();
    }

    // Annule le dernier coup ; retourne le Coup annulé ou null si impossible
    public Coup annule() {
        Coup c = historique.annule(grille);
        if (c != null) {
            joueurSuivant();
            return c;
        }
        else {
            return null;
        }
    }

    // Rejoue le coup suivant ; retourne le Coup rejoué ou null si impossible
    public Coup refais() {
        Coup c = historique.refais(grille);
        if (c != null) {
            joueurSuivant();
            return c;
        }
        else {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Affichage debug
    // -------------------------------------------------------------------------

    public void afficheGrille() {
        System.out.println("-------------------");
        System.out.println("-----Grille -------");
        System.out.println("-------------------");
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                // '-' = présente, '|' = mangée  (convention du vecteur : true=1, false=0)
                System.out.print((grille[index(i, j)] ? "-" : "|") + " ");
            }
            System.out.println();
        }
        System.out.println("-------------------");
        System.out.println();
    }
}
