package Modele;

import Global.Configuration;
import Patterns.Observable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle du Jeu de Gaufre.
 * <p>
 * ==========================================================================
 * REPRÉSENTATION — boolean[] grille  (taille M+N, source unique de vérité)
 * ==========================================================================
 * <p>
 * Encode le chemin en escalier (staircase path) :
 * true  (1) → pas droite : avance le compteur de colonnes
 * false (0) ↓ pas bas    : termine la ligne courante
 * <p>
 * Les lignes sont stockées en ordre INVERSE dans le vecteur :
 * index interne 0     = ligne de jeu M-1 (bas, la plus large)
 * index interne M-1   = ligne de jeu 0   (haut, contient le poison)
 * <p>
 * Grille pleine M×N : [true×N, false×M]   ex 3×4 : [T,T,T,T,F,F,F]
 * → w interne = [4,4,4], correspondant aux lignes de jeu 2,1,0
 * → hauteur(0)=4, hauteur(1)=4, hauteur(2)=4  ✓
 * <p>
 * Coup (l, c) : supprime les cases (l', c') avec l' >= l ET c' >= c
 * → hauteur(i) = min(hauteur(i), c) pour toutes les lignes de jeu i >= l
 * → en interne : w[j] = min(w[j], c) pour j in [0 .. M-1-l]
 * <p>
 * Poison = case (0,0).  jeuTermine() ↔ hauteur(0)==0 ↔ w[M-1]==0.
 * <p>
 * Undo : restaure grille entière depuis Coup.savedGrille (copie complète).
 * Redo : ré-applique applyMove.
 * <p>
 * Nombre de configurations : C(M+N, M).
 */
public class Jeu extends Observable implements Cloneable {

    private int lignes;
    private int colonnes;
    private int joueur;

    /**
     * Vecteur de M+N bits — source unique de vérité sur l'état de la grille.
     */
    private boolean[] grille;

    private Historique historique;

    // -------------------------------------------------------------------------
    // Constructeurs
    // -------------------------------------------------------------------------

    public Jeu(int lignes, int colonnes, int joueur) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.joueur = joueur;
        historique = new Historique();
        grille = new boolean[lignes + colonnes];
        initialiserGrille();
    }

    public List<boolean[]> get_children() {
        List<boolean[]> children = new ArrayList<>();
        for (int i = 0; i < grille.length; i++) {
            if (grille[i]) {
                for (int j = i + 1; j < grille.length; j++) {
                    if (!grille[j]) {
                        boolean[] configPermutation = copy();
                        configPermutation[i] = false;
                        configPermutation[j] = true;
                        children.add(configPermutation);
                    }
                }
            }
        }
        return children;
    }

    public boolean[] copy() {
        boolean[] result = new boolean[grille.length];
        for (int i = 0; i < grille.length; i++) {
            result[i] = false;
            if (grille[i]) result[i] = true;
        }
        return result;
    }

    public Jeu(int joueur) {
        this(5, 7, joueur);
    }

    @Override
    public Jeu clone() {
        try {
            Jeu clone = (Jeu) super.clone();
            clone.grille = grille.clone();      // copie du tableau de bits
            clone.historique = new Historique();    // historique vide
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    /**
     * Grille pleine : [true×N, false×M].  Ex 3×4 → [T,T,T,T,F,F,F].
     */
    private void initialiserGrille() {
        for (int k = 0; k < colonnes; k++) grille[k] = true;
        for (int k = colonnes; k < lignes + colonnes; k++) grille[k] = false;
    }

    // -------------------------------------------------------------------------
    // Accesseurs
    // -------------------------------------------------------------------------

    public int getJoueur() {
        return joueur;
    }

    public int getLignes() {
        return lignes;
    }

    public int getColonnes() {
        return colonnes;
    }

    public Historique getHistorique() {
        return historique;
    }

    /**
     * Copie défensive du vecteur grille (taille M+N).
     */
    public boolean[] getGrille() {
        boolean[] copie = new boolean[grille.length];
        System.arraycopy(grille, 0, copie, 0, grille.length);
        return copie;
    }

    /**
     * Nombre de cases présentes dans la ligne de jeu l.
     */
    public int hauteur(int l) {
        if (l < 0 || l >= lignes) return 0;
        return GrilleHelper.hauteur(grille, l, lignes);
    }

    // -------------------------------------------------------------------------
    // Consultation de l'état d'une case
    // -------------------------------------------------------------------------

    /**
     * True si la case (l, c) est présente : c < hauteur(l).
     */
    public boolean estPresente(int l, int c) {
        if (l < 0 || l >= lignes || c < 0 || c >= colonnes) return false;
        return GrilleHelper.estPresente(grille, l, c, lignes);
    }

    /**
     * True si (l, c) est le poison (coin haut-gauche, toujours (0,0)).
     */
    public boolean estPoison(int l, int c) {
        return l == 0 && c == 0;
    }

    /**
     * True si (l, c) est une gaufre (présente et pas le poison).
     */
    public boolean estGaufre(int l, int c) {
        return estPresente(l, c) && !estPoison(l, c);
    }

    /**
     * True si (l, c) a été mangée.
     */
    public boolean estVide(int l, int c) {
        return !estPresente(l, c);
    }

    // -------------------------------------------------------------------------
    // Logique de jeu
    // -------------------------------------------------------------------------

    /**
     * True si le jeu est terminé : hauteur(0)==0
     * (la ligne du haut, qui contient le poison, est vide).
     */
    public boolean jeuTermine() {
        return GrilleHelper.jeuTermine(grille, lignes);
    }

    /**
     * Alterne le joueur courant.
     */
    public void joueurSuivant() {
        joueur = (joueur + 1) % 2;
    }

    /**
     * Joue le coup (l, c) si valide.
     * <p>
     * Validité : jeu non terminé, 0≤l<M, 0≤c<N, case (l,c) présente.
     * Effet    : supprime toutes les cases (l', c') avec l'>=l ET c'>=c.
     * → hauteur(i) = min(hauteur(i), c) pour i in [l..M-1].
     *
     * @return true si le coup a été accepté et joué
     */
    public boolean joue(int l, int c) {
        if (jeuTermine()) return false;
        if (l < 0 || l >= lignes) return false;
        if (c < 0 || c >= colonnes) return false;
        if (!estPresente(l, c)) return false;

        // Copie complète du vecteur grille avant modification (pour undo)
        boolean[] savedGrille = GrilleHelper.saveGrille(grille);

        // Appliquer le coup : lire toutes les largeurs, cap, réécrire
        GrilleHelper.applyMove(grille, l, c, lignes, colonnes);

        historique.joue(new Coup(l, c, savedGrille));
        joueurSuivant();
        metAJour();
        return true;
    }

    // -------------------------------------------------------------------------
    // Gestion de la partie
    // -------------------------------------------------------------------------

    public void nouvellePartie() {
        historique = new Historique();
        grille = new boolean[lignes + colonnes];
        joueur = 0;
        initialiserGrille();
        metAJour();
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

    /**
     * Annule le dernier coup : restaure l'intégralité du vecteur grille
     * depuis la copie stockée dans Coup.savedGrille.
     */
    public Coup annule() {
        Coup c = historique.annule(grille);
        if (c != null) {
            joueurSuivant();
            metAJour();
        }
        return c;
    }

    /**
     * Rejoue le coup suivant : ré-applique son effet via GrilleHelper.applyMove.
     */
    public Coup refais() {
        Coup c = historique.refais(grille, lignes, colonnes);
        if (c != null) {
            joueurSuivant();
            metAJour();
        }
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
            bw.write(lignes + " " + colonnes + " " + joueur);
            bw.newLine();
            StringBuilder sb = new StringBuilder(grille.length);
            for (boolean b : grille) sb.append(b ? '1' : '0');
            bw.write(sb.toString());
            bw.newLine();
            ArrayList<Coup> passe = historique.getCoupsPasse();
            bw.write(String.valueOf(passe.size()));
            bw.newLine();
            for (Coup cp : passe) {
                bw.write(encoderCoup(cp));
                bw.newLine();
            }
            ArrayList<Coup> futur = historique.getCoupsFutur();
            bw.write(String.valueOf(futur.size()));
            bw.newLine();
            for (Coup cp : futur) {
                bw.write(encoderCoup(cp));
                bw.newLine();
            }
        }
        metAJour();
    }

    private String encoderCoup(Coup cp) {
        StringBuilder sb = new StringBuilder();
        sb.append(cp.getL()).append(' ').append(cp.getC());
        for (boolean b : cp.getSavedGrille()) sb.append(' ').append(b ? '1' : '0');
        return sb.toString();
    }

    public void charger(String nomFichier) throws IOException {
        File fichier = new File("res" + File.separator + "Jeux" + File.separator + nomFichier);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fichier)))) {
            String[] entete = br.readLine().trim().split(" ");
            int nouvLignes = Integer.parseInt(entete[0]);
            int nouvColonnes = Integer.parseInt(entete[1]);
            int nouvJoueur = Integer.parseInt(entete[2]);
            String grilleStr = br.readLine().trim();
            int tailleAttendue = nouvLignes + nouvColonnes;
            if (grilleStr.length() != tailleAttendue)
                throw new IOException("Taille de grille incorrecte.");
            boolean[] nouvGrille = new boolean[tailleAttendue];
            for (int k = 0; k < tailleAttendue; k++) {
                char ch = grilleStr.charAt(k);
                if (ch == '1') nouvGrille[k] = true;
                else if (ch == '0') nouvGrille[k] = false;
                else throw new IOException("Caractère invalide : '" + ch + "'");
            }
            int nbPasse = Integer.parseInt(br.readLine().trim());
            ArrayList<Coup> passeChrono = new ArrayList<>(nbPasse);
            for (int n = 0; n < nbPasse; n++) passeChrono.add(decoderCoup(br.readLine()));
            int nbFutur = Integer.parseInt(br.readLine().trim());
            ArrayList<Coup> futurChrono = new ArrayList<>(nbFutur);
            for (int n = 0; n < nbFutur; n++) futurChrono.add(decoderCoup(br.readLine()));
            this.lignes = nouvLignes;
            this.colonnes = nouvColonnes;
            this.joueur = nouvJoueur;
            this.grille = nouvGrille;
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
                if (l == 0 && c == 0) System.out.print("☠ ");
                else if (c < h) System.out.print("■ ");
                else System.out.print("· ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Fonctions de comparaison (pour IA)
    // -------------------------------------------------------------------------

    /**
     * True si les deux jeux ont exactement le même vecteur grille bit à bit.
     * Utilisé par l'IA pour détecter des configurations identiques.
     */
    public static boolean compareGrille(boolean[] g1, boolean[] g2) {
        if (g1.length != g2.length) return false;
        for (int k = 0; k < g1.length; k++) {
            if (g1[k] != g2[k]) return false;
        }
        return true;
    }

    /**
     * Retourne le nombre de cases de gaufre encore présentes,
     * c'est-à-dire toutes les cases présentes SAUF le poison (0,0).
     */
    public static int compteCasesRestantes(boolean[] configuration, int lignes) {
        int l = lignes;
        int cases = 0;
        for (int i = 0; i < configuration.length; i++) {
            if (configuration[i]) {
                cases += l;
            } else {
                l--;
            }
        }
        return cases;
    }

    public static boolean[] copy(boolean[] configuration) {
        boolean[] result = new boolean[configuration.length];
        for (int i = 0; i < configuration.length; i++) {
            result[i] = false;
            if (configuration[i]) result[i] = true;
        }
        return result;
    }

    public static List<boolean[]> get_children(boolean[] configuration) {
        List<boolean[]> children = new ArrayList<>();
        for (int i = 0; i < configuration.length; i++) {
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

    public static Coup getCoup(boolean[] init, boolean[] fin, int lignes) {
        int c = 0;
        int l = lignes;
        if (init.length != fin.length) return null;
        int n = init.length;
        int i = 0;
        while (i < n && init[i] == fin[i]) {
            if (init[i]) c++;
            else l--;
            i++;
        }
        if (i < n) {
            while (!fin[i]) {
                l--;
                i++;
            }
            return new Coup(l, c, init);
        }
        return null;
    }

    public static boolean[] initialConfig(int colonnes, int lignes) {
        boolean[] init_config = new boolean[colonnes + lignes];
        for (int i = 0; i < colonnes; i++) {
            init_config[i] = true;
        }
        for (int i = colonnes; i < lignes + colonnes; i++) {
            init_config[i] = false;
        }
        return init_config;
    }

    public static boolean[] losingConfig(int colonnes, int lignes) {
        boolean[] losing_config = new boolean[colonnes + lignes];
        for (int i = 0; i < lignes; i++) {
            losing_config[i] = false;
        }
        for (int i = lignes; i < colonnes + lignes; i++) {
            losing_config[i] = true;
        }
        return losing_config;
    }
}