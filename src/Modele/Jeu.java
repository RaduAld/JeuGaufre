package Modele;

import Global.Configuration;
import Patterns.Observable;

import java.io.*;
import java.util.ArrayList;

/**
 * Modèle du Jeu de Gaufre
 *
 * ==========================================================================
 * REPRÉSENTATION — boolean[] grille  (taille M+N, source unique de vérité)
 * ==========================================================================
 *
 * Encode le chemin en escalier (staircase path) :
 *   false (0) → pas droite : avance le compteur de colonnes
 *   true  (1) ↓ pas bas    : termine la ligne courante
 *
 * Décodage : scan gauche→droite, col++ sur false, hauteur[row]=col sur true
 * Grille pleine M×N : [false×N, true×M]   ex 3×4 : [F,F,F,F,T,T,T]
 *
 * Coup (l,c) :  hauteur(i) = min(hauteur(i), c)  pour i dans [0..l]
 *   → implémenté par GrilleHelper.applyMove (lire tout / cap / réécrire tout)
 *
 * Undo : restaure grille[0..savedSegment.length-1] depuis Coup.savedSegment
 * Redo : ré-applique applyMove
 *
 * Poison = case (0,0)  jeuTermine() ↔ hauteur(0)==0 ↔ grille[0]==true
 *
 * Nombre de configurations : C(M+N, M)
 */
public class Jeu extends Observable {

    private int lignes;
    private int colonnes;
    private int joueur;
    private boolean[] grille;

    private Historique historique;

    // -------------------------------------------------------------------------
    // Constructeurs
    // -------------------------------------------------------------------------

    public Jeu(int lignes, int colonnes, int joueur) {
        this.lignes   = lignes;
        this.colonnes = colonnes;
        this.joueur   = joueur;
        historique    = new Historique();
        grille        = new boolean[lignes + colonnes];
        initialiserGrille();
    }

    public Jeu(int joueur) { this(5, 7, joueur); }

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    // Grille pleine : [false×N, true×M]    Ex 3×4 → [F,F,F,F,T,T,T]
    private void initialiserGrille() {
        for (int k = 0; k < colonnes; k++)             grille[k] = false;
        for (int k = colonnes; k < lignes + colonnes; k++) grille[k] = true;
    }

    // -------------------------------------------------------------------------
    // Accesseurs
    // -------------------------------------------------------------------------

    public int getJoueur()   { return joueur;   }
    public int getLignes()   { return lignes;   }
    public int getColonnes() { return colonnes; }
    public Historique getHistorique() { return historique; }

    // Copie défensive du vecteur grille (taille M+N)
    public boolean[] getGrille() {
        boolean[] copie = new boolean[grille.length];
        System.arraycopy(grille, 0, copie, 0, grille.length);
        return copie;
    }

    // Nombre de cases présentes dans la ligne l Scan O(M+N)
    public int hauteur(int l) {
        if (l < 0 || l >= lignes) return 0;
        return GrilleHelper.hauteur(grille, l);
    }

    // -------------------------------------------------------------------------
    // Consultation de l'état d'une case
    // -------------------------------------------------------------------------

    // True si la case (l,c) est présente : c < hauteur(l)
    public boolean estPresente(int l, int c) {
        if (l < 0 || l >= lignes || c < 0 || c >= colonnes) return false;
        return GrilleHelper.estPresente(grille, l, c);
    }

    // True si (l,c) est le poison (coin haut-gauche, toujours (0,0))
    public boolean estPoison(int l, int c) { return l == 0 && c == 0; }

    // True si (l,c) est une gaufre (présente et pas le poison)
    public boolean estGaufre(int l, int c) { return estPresente(l, c) && !estPoison(l, c); }

    // True si (l,c) a été mangée
    public boolean estVide(int l, int c) { return !estPresente(l, c); }

    // -------------------------------------------------------------------------
    // Logique de jeu
    // -------------------------------------------------------------------------

    // True si le jeu est terminé : hauteur(0)==0, ex grille[0]==true
    public boolean jeuTermine() { return GrilleHelper.jeuTermine(grille); }

    //Alterne le joueur courant
    public void joueurSuivant() { joueur = (joueur + 1) % 2; }

    /**
     * Joue le coup (l,c) si valide
     *
     * Validité : jeu non terminé, 0≤l<M, 0≤c<N, case (l,c) présente
     * Effet    : hauteur(i) = min(hauteur(i), c) pour i dans [0..l]
     *
     * @return true si le coup a été accepté et joué
     */
    public boolean joue(int l, int c) {
        if (jeuTermine())               return false;
        if (l < 0 || l >= lignes)       return false;
        if (c < 0 || c >= colonnes)     return false;
        if (!estPresente(l, c))         return false;

        // Sauvegarder grille[0..pos_r] avant modification (pour undo)
        boolean[] savedSegment = GrilleHelper.saveSegment(grille, l);

        // Appliquer le coup : lire toutes les largeurs, cap, réécrire
        GrilleHelper.applyMove(grille, l, c, lignes, colonnes);

        historique.joue(new Coup(l, c, savedSegment));
        joueurSuivant();
        metAJour();
        return true;
    }

    // -------------------------------------------------------------------------
    // Gestion de la partie
    // -------------------------------------------------------------------------

    public void nouvellePartie() {
        historique = new Historique();
        grille     = new boolean[lignes + colonnes];
        joueur     = 0;
        initialiserGrille();
        metAJour();
    }

    // -------------------------------------------------------------------------
    // Annuler / Refaire
    // -------------------------------------------------------------------------

    public boolean peutAnnuler() { return historique.peutAnnuler(); }
    public boolean peutRefaire() { return historique.peutRefaire(); }

    /*
     * Annule le dernier coup
     * Restaure grille[0..savedSegment.length-1] depuis la sauvegarde
     * Les bits au-delà du segment (lignes l+1..M-1) n'ont jamais changé
     */
    public Coup annule() {
        Coup c = historique.annule(grille);
        if (c != null) { joueurSuivant(); metAJour(); }
        return c;
    }

    /*
     * Rejoue le coup suivant
     * Ré-applique le coup via GrilleHelper.applyMove
     */
    public Coup refais() {
        Coup c = historique.refais(grille, lignes, colonnes);
        if (c != null) { joueurSuivant(); metAJour(); }
        return c;
    }

    // -------------------------------------------------------------------------
    // Sauvegarde / Chargement
    // -------------------------------------------------------------------------

    /*
     * Format fichier :
     *   <lignes> <colonnes> <joueur>
     *   <grille : chaîne de '1'/'0' de longueur M+N>
     *   <nb_coups_passés>
     *   <l> <c> <seg[0]> <seg[1]> ...    (un coup par ligne, bits en 0/1)
     *   ...
     *   <nb_coups_futurs>
     *   <l> <c> <seg[0]> <seg[1]> ...
     *   ...
     */

    public void sauvegarder(String nomFichier) throws IOException {
        File fichier = new File("res" + File.separator + "Jeux" + File.separator + nomFichier);
        fichier.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichier))) {
            bw.write(lignes + " " + colonnes + " " + joueur); bw.newLine();
            StringBuilder sb = new StringBuilder(grille.length);
            for (boolean b : grille) sb.append(b ? '1' : '0');
            bw.write(sb.toString()); bw.newLine();
            ArrayList<Coup> passe = historique.getCoupsPasse();
            bw.write(String.valueOf(passe.size())); bw.newLine();
            for (Coup c : passe) { bw.write(encoderCoup(c)); bw.newLine(); }
            ArrayList<Coup> futur = historique.getCoupsFutur();
            bw.write(String.valueOf(futur.size())); bw.newLine();
            for (Coup c : futur) { bw.write(encoderCoup(c)); bw.newLine(); }
        }
        metAJour();
    }

    private String encoderCoup(Coup c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c.getL()).append(' ').append(c.getC());
        for (boolean b : c.getSavedSegment()) sb.append(' ').append(b ? '1' : '0');
        return sb.toString();
    }

    public void charger(String nomFichier) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Configuration.ouvre("Jeux/" + nomFichier)))) {
            String[] entete  = br.readLine().trim().split(" ");
            int nouvLignes   = Integer.parseInt(entete[0]);
            int nouvColonnes = Integer.parseInt(entete[1]);
            int nouvJoueur   = Integer.parseInt(entete[2]);
            String grilleStr = br.readLine().trim();
            int tailleAttendue = nouvLignes + nouvColonnes;
            if (grilleStr.length() != tailleAttendue)
                throw new IOException("Taille de grille incorrecte.");
            boolean[] nouvGrille = new boolean[tailleAttendue];
            for (int k = 0; k < tailleAttendue; k++) {
                char ch = grilleStr.charAt(k);
                if      (ch == '1') nouvGrille[k] = true;
                else if (ch == '0') nouvGrille[k] = false;
                else throw new IOException("Caractère invalide : '" + ch + "'");
            }
            int nbPasse = Integer.parseInt(br.readLine().trim());
            ArrayList<Coup> passeChrono = new ArrayList<>(nbPasse);
            for (int n = 0; n < nbPasse; n++) passeChrono.add(decoderCoup(br.readLine()));
            int nbFutur = Integer.parseInt(br.readLine().trim());
            ArrayList<Coup> futurChrono = new ArrayList<>(nbFutur);
            for (int n = 0; n < nbFutur; n++) futurChrono.add(decoderCoup(br.readLine()));
            this.lignes     = nouvLignes;
            this.colonnes   = nouvColonnes;
            this.joueur     = nouvJoueur;
            this.grille     = nouvGrille;
            this.historique = new Historique();
            this.historique.restaurer(passeChrono, futurChrono);
            metAJour();
        }
    }

    private Coup decoderCoup(String ligne) throws IOException {
        if (ligne == null) throw new IOException("Ligne de coup manquante.");
        String[] parts = ligne.trim().split(" ");
        if (parts.length < 2) throw new IOException("Ligne de coup malformée.");
        int l = Integer.parseInt(parts[0]);
        int c = Integer.parseInt(parts[1]);
        boolean[] seg = new boolean[parts.length - 2];
        for (int i = 0; i < seg.length; i++) seg[i] = parts[2 + i].equals("1");
        return new Coup(l, c, seg);
    }

    // -------------------------------------------------------------------------
    // Affichage debug
    // -------------------------------------------------------------------------

    public void afficheGrille() {
        System.out.println("Grille " + lignes + "×" + colonnes + "  (joueur " + joueur + ")");
        System.out.print("Bits : [");
        for (int k = 0; k < grille.length; k++)
            System.out.print((grille[k] ? "1" : "0") + (k < grille.length - 1 ? "," : ""));
        System.out.println("]");
        for (int l = 0; l < lignes; l++) {
            int h = hauteur(l);
            System.out.print("  ligne " + l + " [w=" + h + "] : ");
            for (int c = 0; c < colonnes; c++) {
                if      (l == 0 && c == 0) System.out.print("☠ ");
                else if (c < h)            System.out.print("■ ");
                else                       System.out.print("· ");
            }
            System.out.println();
        }
        System.out.println();
    }
}