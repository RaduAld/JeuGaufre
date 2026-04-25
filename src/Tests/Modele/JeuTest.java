package Tests.Modele;

import Modele.Coup;
import Modele.Jeu;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class JeuTest {

    // -------------------------------------------------------------------------
    // Coup
    // -------------------------------------------------------------------------

    @Test
    public void testCoupStockeCoordonnees() {
        ArrayList<int[]> liste = new ArrayList<>();
        liste.add(new int[]{1, 2});
        Coup c = new Coup(1, 2, liste);

        assertEquals("i doit être stocke correctement", 1, c.getI());
        assertEquals("j doit être stocke correctement", 2, c.getJ());
    }

    @Test
    public void testCoupStockeLaListeDesCasesModifiees() {
        ArrayList<int[]> liste = new ArrayList<>();
        liste.add(new int[]{0, 1});
        liste.add(new int[]{1, 1});
        Coup c = new Coup(0, 1, liste);

        assertEquals("La liste doit avoir 2 entrees", 2, c.getChangedToZero().size());
        assertArrayEquals("Première case incorrecte", new int[]{0, 1}, c.getChangedToZero().get(0));
        assertArrayEquals("Deuxième case incorrecte", new int[]{1, 1}, c.getChangedToZero().get(1));
    }

    @Test
    public void testCoupAvecListeVide() {
        ArrayList<int[]> liste = new ArrayList<>();
        Coup c = new Coup(2, 3, liste);

        assertNotNull("changedToZero ne doit pas être null", c.getChangedToZero());
        assertTrue("changedToZero doit être vide", c.getChangedToZero().isEmpty());
    }

    // -------------------------------------------------------------------------
    // Jeu — constructeurs
    // -------------------------------------------------------------------------

    @Test
    public void testConstructeurDefautDimensions() {
        Jeu jeu = new Jeu(0);
        int[][] grille = jeu.getGrille();

        assertEquals("Le constructeur par défaut doit créer 5 lignes", 5, grille.length);
        assertEquals("Le constructeur par défaut doit créer 7 colonnes", 7, grille[0].length);
    }

    @Test
    public void testConstructeurDefautEtatInitial() {
        Jeu jeu = new Jeu(1);
        int[][] grille = jeu.getGrille();

        assertEquals("Le joueur initial doit être 1", 1, jeu.getJoueur());
        assertEquals("La case poison doit valoir 2", 2, grille[0][0]);
        assertEquals("Une case normale doit valoir 1", 1, grille[4][6]);
    }

    @Test
    public void testConstructeurParametresArbitraires() {
        Jeu jeu = new Jeu(2, 3, 1);
        int[][] grille = jeu.getGrille();

        assertEquals("2 lignes attendues", 2, grille.length);
        assertEquals("3 colonnes attendues", 3, grille[0].length);
        assertEquals("Case poison en (0,0)", 2, grille[0][0]);
        assertEquals("Case (1,2) doit valoir 1", 1, grille[1][2]);
    }

    // -------------------------------------------------------------------------
    // Jeu — joueurSuivant
    // -------------------------------------------------------------------------

    @Test
    public void testJoueurSuivantAlterne() {
        Jeu jeu = new Jeu(3, 3, 0);

        jeu.joueurSuivant();
        assertEquals("Apres un appel, joueur doit être 1", 1, jeu.getJoueur());

        jeu.joueurSuivant();
        assertEquals("Apres deux appels, joueur doit revenir a 0", 0, jeu.getJoueur());
    }

    // -------------------------------------------------------------------------
    // Jeu — joue : cas invalides
    // -------------------------------------------------------------------------

    @Test
    public void testJouerSurCaseDejaVide() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 1); // met (1,1)...(2,3) à 0
        int joueurAvant = jeu.getJoueur();

        // (2,3) est maintenant à 0 — doit être refusé
        assertFalse("Jouer sur une case vide doit être refusé", jeu.joue(2, 3));
        assertEquals("Le joueur ne doit pas changer", joueurAvant, jeu.getJoueur());
    }

    @Test
    public void testJouerColonneNegative() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Colonne negative doit être refusée", jeu.joue(0, -1));
        assertEquals("Le joueur ne doit pas changer", 0, jeu.getJoueur());
    }

    @Test
    public void testJouerLigneNegative() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Ligne negative doit être refusée", jeu.joue(-1, 0));
        assertEquals("Le joueur ne doit pas changer", 0, jeu.getJoueur());
    }

    @Test
    public void testJouerSurLaCasePoisonEstAccepte() {
        // grille[0][0] vaut 2 (pas 0), donc joue() ne le refuse pas sur critère 0
        Jeu jeu = new Jeu(3, 4, 0);
        assertTrue("Jouer sur la case poison (valeur 2) doit être accepté", jeu.joue(0, 0));
        // Toute la grille passe à 0 sauf (0,0) qui vaut 2 — la boucle ne touche que les 1.
        int[][] grille = jeu.getGrille();
        assertEquals("La case poison doit rester à 2", 2, grille[0][0]);
    }

    @Test
    public void testJouerUnSeuleCaseCorner() {
        Jeu jeu = new Jeu(3, 4, 0);
        // Le coin bas-droit (2,3) ne touche qu'une seule case
        assertTrue("Le coup doit être validé", jeu.joue(2, 3));
        assertEquals("La case (2,3) doit passer à 0", 0, jeu.getGrille()[2][3]);
        assertEquals("La case (2,2) ne doit pas être affectee", 1, jeu.getGrille()[2][2]);
    }

    // -------------------------------------------------------------------------
    // Jeu — chaîne undo/redo multiple
    // -------------------------------------------------------------------------

    @Test
    public void testChaineAnnuleRefaisMultiple() {
        // joue(2,2) zero (2,2),(2,3),(3,2),(3,3)
        // joue(3,0) zero uniquement (3,0) — rectangles disjoints, le coup est valide
        Jeu jeu = new Jeu(4, 4, 0);
        assertTrue("coup 1 doit être valide", jeu.joue(2, 2));  // coup 1
        assertTrue("coup 2 doit être valide", jeu.joue(3, 0));  // coup 2

        // Annule coup 2
        assertTrue(jeu.annule());
        assertEquals("Apres undo coup 2 : joueur doit être 1", 1, jeu.getJoueur());
        assertEquals("(3,0) doit être restaurée à 1", 1, jeu.getGrille()[3][0]);

        // Annule coup 1
        assertTrue(jeu.annule());
        assertEquals("Apres undo coup 1 : joueur doit être 0", 0, jeu.getJoueur());
        assertEquals("(2,2) doit être restaurée à 1", 1, jeu.getGrille()[2][2]);
        assertEquals("(3,3) doit être restaurée à 1", 1, jeu.getGrille()[3][3]);

        // Refait coup 1
        assertTrue(jeu.refais());
        assertEquals("(2,2) doit repasser à 0", 0, jeu.getGrille()[2][2]);
        assertEquals("(3,3) doit repasser à 0", 0, jeu.getGrille()[3][3]);
        assertEquals("Joueur doit être 1 apres redo coup 1", 1, jeu.getJoueur());

        // Refait coup 2
        assertTrue(jeu.refais());
        assertEquals("(3,0) doit repasser a 0", 0, jeu.getGrille()[3][0]);
        assertEquals("Joueur doit être 0 apres redo coup 2", 0, jeu.getJoueur());

        // Plus rien à refaire
        assertFalse("Redo sur pile vide doit échouer", jeu.refais());
    }

    @Test
    public void testAnnuleSansHistoriqueNeCassePasLEtat() {
        Jeu jeu = new Jeu(3, 4, 0);

        assertFalse("Annuler sans coup joue doit échouer", jeu.annule());
        assertEquals("Le joueur ne doit pas changer", 0, jeu.getJoueur());
        assertEquals("La grille ne doit pas être modifiée", 2, jeu.getGrille()[0][0]);
        assertEquals("La grille ne doit pas être modifiée", 1, jeu.getGrille()[2][3]);
    }

    @Test
    public void testRefaisSansFuturNeCassePasLEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(2, 2);

        assertFalse("Refaire sans futur doit échouer", jeu.refais());
        assertEquals("Le joueur ne doit pas changer", 1, jeu.getJoueur());
        assertEquals("La case jouée doit rester a 0", 0, jeu.getGrille()[2][2]);
    }
}