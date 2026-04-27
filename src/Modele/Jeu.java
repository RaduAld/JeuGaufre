package Modele;

import Global.Configuration;
import Patterns.Observable;

import java.io.*;
import java.util.ArrayList;

/*
 Démarrer une nouvelle partie (en abandonnant l'état courant)
 fonctions annuler et rejouer (conservation de l'historique de tous les coups)
 sauvegarder et restaurer une partie et son historique complet
 */
public class Jeu extends Observable {
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
            metAJour();
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

    // Annule le dernier coup ; retourne le Coup annulé ou null si impossible
    public Coup annule() {
        Coup c = historique.annule(grille);
        if (c != null) {
            joueurSuivant();
            metAJour();
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
            metAJour();
            return c;
        }
        else {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Sauvegarde / Chargement du jeu
    // -------------------------------------------------------------------------

    /*
     * Format du fichier de sauvegarde (texte, une donnée par ligne) :
     *
     *   lignes colonnes joueur
     *   <grille : suite de '1' (présente) et '0' (mangée), sans séparateur>
     *   <nb_coups_passés>
     *   l c k1 k2 k3 ...     un coup par ligne, du plus ancien au plus récent
     *   ...
     *   <nb_coups_futurs>
     *   l c k1 k2 k3 ...     un coup par ligne, du prochain redo au plus lointain
     *   ...
     *
     * Exemple minimal (3×4, joueur 1, un coup joué en (1,2)) :
     *   3 4 1
     *   111111100000
     *   1
     *   1 2 6 7 10 11
     *   0
     */

    // Sauvegarde la partie courante (état + historique complet) dans res/<nomFichier>.
    // Lève une IOException si l'écriture échoue.
    public void sauvegarder(String nomFichier) throws IOException {
        // On écrit dans res/ : c'est le dossier que Configuration.ouvre() consulte
        // en fallback, ce qui garantit que charger() retrouvera le fichier.
        File fichier = new File("res" + File.separator + "Jeux" + File.separator + nomFichier);
        fichier.getParentFile().mkdirs(); // crée res/ si absent

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichier))) {

            // Ligne 1 : dimensions et joueur courant
            bw.write(lignes + " " + colonnes + " " + joueur);
            bw.newLine();

            // Ligne 2 : grille encodée comme une chaîne de '1'/'0'
            StringBuilder sb = new StringBuilder(grille.length);
            for (boolean b : grille) {
                sb.append(b ? '1' : '0');
            }
            bw.write(sb.toString());
            bw.newLine();

            // Historique : coups du passé (du plus ancien au plus récent)
            ArrayList<Coup> passe = historique.getCoupsPasse();
            bw.write(String.valueOf(passe.size()));
            bw.newLine();
            for (Coup c : passe) {
                bw.write(encoderCoup(c));
                bw.newLine();
            }

            // Historique : coups du futur (du prochain redo au plus lointain)
            ArrayList<Coup> futur = historique.getCoupsFutur();
            bw.write(String.valueOf(futur.size()));
            bw.newLine();
            for (Coup c : futur) {
                bw.write(encoderCoup(c));
                bw.newLine();
            }
        }
        metAJour();
    }

    // Encode un Coup en une ligne : "l c k1 k2 k3 ..."
    private String encoderCoup(Coup c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c.getI()).append(' ').append(c.getJ());
        for (int k : c.getChangedToFalse()) {
            sb.append(' ').append(k);
        }
        return sb.toString();
    }

    // Charge une partie depuis nomFichier via Configuration.ouvre() et remplace l'état courant.
    // Configuration.ouvre() cherche d'abord dans le classpath, puis dans res/.
    // Lève une IOException si la lecture échoue ou si le fichier est malformé.
    public void charger(String nomFichier) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Configuration.ouvre("Jeux/" + nomFichier)))) {

            // Ligne 1 : dimensions et joueur
            String[] entete = br.readLine().trim().split(" ");
            int nouvLignes   = Integer.parseInt(entete[0]);
            int nouvColonnes = Integer.parseInt(entete[1]);
            int nouvJoueur   = Integer.parseInt(entete[2]);

            // Ligne 2 : grille
            String grilleStr = br.readLine().trim();
            if (grilleStr.length() != nouvLignes * nouvColonnes) {
                throw new IOException("Taille de grille incorrecte dans le fichier de sauvegarde");
            }
            boolean[] nouvGrille = new boolean[nouvLignes * nouvColonnes];
            for (int k = 0; k < nouvGrille.length; k++) {
                char ch = grilleStr.charAt(k);
                if (ch == '1')      nouvGrille[k] = true;
                else if (ch == '0') nouvGrille[k] = false;
                else throw new IOException("Caractère de grille invalide : '" + ch + "'");
            }

            // Coups du passé
            int nbPasse = Integer.parseInt(br.readLine().trim());
            ArrayList<Coup> passeChrono = new ArrayList<>(nbPasse);
            for (int n = 0; n < nbPasse; n++) {
                passeChrono.add(decoderCoup(br.readLine()));
            }

            // Coups du futur
            int nbFutur = Integer.parseInt(br.readLine().trim());
            ArrayList<Coup> futurChrono = new ArrayList<>(nbFutur);
            for (int n = 0; n < nbFutur; n++) {
                futurChrono.add(decoderCoup(br.readLine()));
            }

            // Tout est lu sans erreur : on applique le nouvel état
            this.lignes     = nouvLignes;
            this.colonnes   = nouvColonnes;
            this.joueur     = nouvJoueur;
            this.grille     = nouvGrille;
            this.historique = new Historique();
            this.historique.restaurer(passeChrono, futurChrono);
            metAJour();
        }
    }

    // Décode une ligne "l c k1 k2 ..." en un Coup
    private Coup decoderCoup(String ligne) throws IOException {
        if (ligne == null) {
            throw new IOException("Ligne de coup manquante dans le fichier de sauvegarde");
        }
        String[] parts = ligne.trim().split(" ");
        if (parts.length < 2) {
            throw new IOException("Ligne de coup malformée : \"" + ligne + "\"");
        }
        int l = Integer.parseInt(parts[0]);
        int c = Integer.parseInt(parts[1]);
        ArrayList<Integer> indices = new ArrayList<>(parts.length - 2);
        for (int i = 2; i < parts.length; i++) {
            indices.add(Integer.parseInt(parts[i]));
        }
        return new Coup(l, c, indices);
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
