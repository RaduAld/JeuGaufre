package Tests.Modele;

import Modele.Coup;
import Modele.Jeu;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour Coup et Jeu (Jeu de Gaufre, poison haut-gauche).
 *
 * Sémantique du coup (l, c) :
 *   Supprime toutes les cases (l', c') avec l' >= l ET c' >= c
 *   → hauteur(i) = min(hauteur(i), c)  pour i dans [l .. M-1]
 *   (lignes SOUS et INCLUANT la ligne cliquée sont capées)
 *
 * Encodage vecteur (taille M+N) :
 *   Les lignes sont stockées en ordre inverse : index 0 = ligne M-1 (bas).
 *   Grille pleine M×N : [false×N, true×M]
 *   Ex. 3×4 pleine : [F,F,F,F,T,T,T]
 *     → w interne [4,4,4] = lignes de jeu 2,1,0 → hauteur(0)=hauteur(1)=hauteur(2)=4
 *
 * jeuTermine() ↔ hauteur(0)==0 (ligne du haut vide, poison mangé).
 */
public class JeuTest {

    // =========================================================================
    // Coup
    // =========================================================================

    @Test
    public void testCoupStockeCoordonnees() {
        boolean[] seg = new boolean[7]; // taille quelconque pour le test
        Coup c = new Coup(1, 2, seg);
        assertEquals("l doit être stocké", 1, c.getL());
        assertEquals("c doit être stocké", 2, c.getC());
    }

    @Test
    public void testCoupStockeSavedSegment() {
        boolean[] seg = {false, false, false, false, true, true, true};
        Coup c = new Coup(0, 1, seg);
        assertNotNull(c.getSavedSegment());
        assertEquals(7, c.getSavedSegment().length);
        assertFalse(c.getSavedSegment()[0]);
        assertTrue(c.getSavedSegment()[4]);
    }

    // =========================================================================
    // Constructeurs et état initial
    // =========================================================================

    @Test
    public void testConstructeurDefautDimensions() {
        Jeu jeu = new Jeu(0);
        assertEquals(5,  jeu.getLignes());
        assertEquals(7,  jeu.getColonnes());
        assertEquals(12, jeu.getGrille().length);  // M+N = 12
    }

    @Test
    public void testConstructeurDefautGrillePleine() {
        Jeu jeu = new Jeu(1);
        // Grille pleine 5×7 : [false×7, true×5]
        boolean[] g = jeu.getGrille();
        assertEquals(1, jeu.getJoueur());
        for (int k = 0; k < 7;  k++) assertFalse("g[" + k + "] doit être false", g[k]);
        for (int k = 7; k < 12; k++) assertTrue("g["  + k + "] doit être true",  g[k]);
    }

    @Test
    public void testConstructeur3x4GrillePleine() {
        Jeu jeu = new Jeu(3, 4, 0);
        // [F,F,F,F,T,T,T]
        boolean[] g = jeu.getGrille();
        assertEquals(7, g.length);
        assertFalse("g[0]=false", g[0]);
        assertFalse("g[3]=false", g[3]);
        assertTrue("g[4]=true",   g[4]);
        assertTrue("g[6]=true",   g[6]);
    }

    @Test
    public void testHauteurInitiale3x4() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2)=4", 4, jeu.hauteur(2));
    }

    @Test
    public void testEstPresenteInitiale() {
        Jeu jeu = new Jeu(3, 4, 0);
        for (int l = 0; l < 3; l++)
            for (int c = 0; c < 4; c++)
                assertTrue("(" + l + "," + c + ") présente", jeu.estPresente(l, c));
    }

    // =========================================================================
    // joueurSuivant
    // =========================================================================

    @Test
    public void testJoueurSuivantAlterne() {
        Jeu jeu = new Jeu(3, 3, 0);
        jeu.joueurSuivant();
        assertEquals(1, jeu.getJoueur());
        jeu.joueurSuivant();
        assertEquals(0, jeu.getJoueur());
    }

    // =========================================================================
    // Cas invalides
    // =========================================================================

    @Test
    public void testJouerLigneHorsGrille() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse(jeu.joue(4, 1));
        assertEquals(0, jeu.getJoueur());
    }

    @Test
    public void testJouerColonneNegative() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse(jeu.joue(0, -1));
        assertEquals(0, jeu.getJoueur());
    }

    @Test
    public void testJouerLigneNegative() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse(jeu.joue(-1, 0));
        assertEquals(0, jeu.getJoueur());
    }

    @Test
    public void testJouerSurCaseVide() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(0, 2);   // ligne 0,1,2 → hauteur = min(4,2)=2 pour l>=0 → toutes à 2
        int avant = jeu.getJoueur();
        assertFalse("case vide refusée", jeu.joue(0, 3));
        assertEquals(avant, jeu.getJoueur());
    }

    @Test
    public void testJouerApresJeuTermine() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(0, 0);
        assertTrue(jeu.jeuTermine());
        assertFalse(jeu.joue(1, 1));
    }

    // =========================================================================
    // Sémantique du coup : lignes >= l sont capées
    // =========================================================================

    @Test
    public void testCoup_l1_c2_sur3x4() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(1,2) : lignes 1 et 2 capées à 2 ; ligne 0 (haut) inchangée
        assertTrue(jeu.joue(1, 2));
        assertEquals("joueur = 1",        1, jeu.getJoueur());
        assertEquals("hauteur(0) = 4",    4, jeu.hauteur(0));  // ligne haut inchangée
        assertEquals("hauteur(1) = 2",    2, jeu.hauteur(1));
        assertEquals("hauteur(2) = 2",    2, jeu.hauteur(2));
    }

    @Test
    public void testCoup_l0_c2_capeToutes() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(0,2) : toutes les lignes (0,1,2) capées à 2
        assertTrue(jeu.joue(0, 2));
        assertEquals("hauteur(0) = 2", 2, jeu.hauteur(0));
        assertEquals("hauteur(1) = 2", 2, jeu.hauteur(1));
        assertEquals("hauteur(2) = 2", 2, jeu.hauteur(2));
    }

    @Test
    public void testCoup_l2_c3_seulementLigne2() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(2,3) : seule la ligne 2 (bas) est capée à 3 ; lignes 0 et 1 inchangées
        assertTrue(jeu.joue(2, 3));
        assertEquals("hauteur(0) = 4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1) = 4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2) = 3", 3, jeu.hauteur(2));
    }

    @Test
    public void testPresenceApresUnCoup() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);   // hauteur = [0→4, 1→2, 2→2]
        // Ligne 0 (haut) inchangée
        assertTrue("(0,3) présente",  jeu.estPresente(0, 3));
        // Lignes 1 et 2 capées à 2
        assertTrue("(1,0) présente",  jeu.estPresente(1, 0));
        assertTrue("(1,1) présente",  jeu.estPresente(1, 1));
        assertFalse("(1,2) vide",     jeu.estPresente(1, 2));
        assertFalse("(1,3) vide",     jeu.estPresente(1, 3));
        assertFalse("(2,2) vide",     jeu.estPresente(2, 2));
    }

    @Test
    public void testCoupPoisonTermineLeJeu() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(0,0) : toutes les lignes capées à 0
        assertTrue(jeu.joue(0, 0));
        assertTrue("jeu terminé", jeu.jeuTermine());
        assertEquals("hauteur(0)=0", 0, jeu.hauteur(0));
        assertEquals("hauteur(1)=0", 0, jeu.hauteur(1));
        assertEquals("hauteur(2)=0", 0, jeu.hauteur(2));
    }

    @Test
    public void testDeuxCoupsSuccessifs() {
        Jeu jeu = new Jeu(3, 4, 0);
        // Coup 1 : (2,2) → seule ligne 2 capée à 2 → hauteur = [4,4,2]
        assertTrue(jeu.joue(2, 2));
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));

        // Coup 2 : (1,1) → lignes 1 et 2 capées à 1 → hauteur = [4,1,1]
        assertTrue(jeu.joue(1, 1));
        assertEquals("joueur=0",     0, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=1", 1, jeu.hauteur(1));
        assertEquals("hauteur(2)=1", 1, jeu.hauteur(2));
    }

    // =========================================================================
    // Cohérence des bits après un coup
    // =========================================================================

    @Test
    public void testBitsApresJoue_l2_c3_sur3x4() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(2,3) : seule ligne 2 (bas) capée à 3
        // w interne : [w_ligne2, w_ligne1, w_ligne0] = [3, 4, 4]
        // Encodage : delta(3-0)=3 faux, vrai, delta(4-3)=1 faux, vrai, delta(4-4)=0 faux, vrai
        // → [F,F,F,T,F,T,T]
        jeu.joue(2, 3);
        boolean[] g = jeu.getGrille();
        assertEquals(7, g.length);
        assertFalse("g[0]=false", g[0]);
        assertFalse("g[1]=false", g[1]);
        assertFalse("g[2]=false", g[2]);
        assertTrue("g[3]=true",   g[3]);
        assertFalse("g[4]=false", g[4]);
        assertTrue("g[5]=true",   g[5]);
        assertTrue("g[6]=true",   g[6]);
    }

    @Test
    public void testBitsApresJoue_l1_c2_sur3x4() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(1,2) : lignes 1 et 2 capées à 2 → hauteur = [4,2,2]
        // w interne : [w_ligne2, w_ligne1, w_ligne0] = [2, 2, 4]
        // Encodage : 2 faux, vrai, 0 faux, vrai, 2 faux, vrai, 0 faux restants
        // → [F,F,T,T,F,F,T]
        jeu.joue(1, 2);
        boolean[] g = jeu.getGrille();
        assertFalse("g[0]=false", g[0]);
        assertFalse("g[1]=false", g[1]);
        assertTrue("g[2]=true",   g[2]);
        assertTrue("g[3]=true",   g[3]);
        assertFalse("g[4]=false", g[4]);
        assertFalse("g[5]=false", g[5]);
        assertTrue("g[6]=true",   g[6]);
    }

    // =========================================================================
    // Annuler / Refaire
    // =========================================================================

    @Test
    public void testAnnuleRestaureEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        boolean[] avant = jeu.getGrille();
        jeu.joue(1, 2);
        assertTrue(jeu.peutAnnuler());
        jeu.annule();
        assertEquals("joueur=0",     0, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2)=4", 4, jeu.hauteur(2));
        assertArrayEquals("bits restaurés", avant, jeu.getGrille());
        assertFalse(jeu.peutAnnuler());
    }

    @Test
    public void testRefaisRejoue() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);
        boolean[] apres = jeu.getGrille();
        jeu.annule();
        assertTrue(jeu.peutRefaire());
        jeu.refais();
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=2", 2, jeu.hauteur(1));
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));
        assertArrayEquals("bits identiques après redo", apres, jeu.getGrille());
        assertFalse(jeu.peutRefaire());
    }

    @Test
    public void testNouveauCoupVideFutur() {
        Jeu jeu = new Jeu(4, 4, 0);
        jeu.joue(1, 1);
        jeu.annule();
        assertTrue(jeu.joue(2, 2));
        assertFalse("futur vide", jeu.peutRefaire());
        // joue(2,2) : lignes 2 et 3 capées à 2 → hauteur = [4,4,2,2]
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));
        assertEquals("hauteur(3)=2", 2, jeu.hauteur(3));
    }

    @Test
    public void testChaineAnnuleRefaisMultiple() {
        Jeu jeu = new Jeu(4, 4, 0);
        // Coup 1 : (2,2) → lignes 2,3 capées à 2 → hauteur=[4,4,2,2]
        assertTrue(jeu.joue(2, 2));
        // Coup 2 : (0,0) → toutes capées à 0 → jeu terminé
        assertTrue(jeu.joue(0, 0));
        assertTrue(jeu.jeuTermine());

        // Undo coup 2 → hauteur=[4,4,2,2]
        jeu.annule();
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));
        assertFalse(jeu.jeuTermine());

        // Undo coup 1 → hauteur=[4,4,4,4]
        jeu.annule();
        assertEquals("joueur=0",     0, jeu.getJoueur());
        assertEquals("hauteur(2)=4", 4, jeu.hauteur(2));
        assertEquals("hauteur(3)=4", 4, jeu.hauteur(3));
        assertFalse(jeu.peutAnnuler());

        // Redo coup 1 → hauteur=[4,4,2,2]
        jeu.refais();
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));
        assertEquals("hauteur(3)=2", 2, jeu.hauteur(3));

        // Redo coup 2 → jeu terminé
        jeu.refais();
        assertEquals("joueur=0",     0, jeu.getJoueur());
        assertTrue(jeu.jeuTermine());
        assertFalse(jeu.peutRefaire());
    }

    @Test
    public void testAnnuleSansHistorique() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse(jeu.peutAnnuler());
        assertEquals(0, jeu.getJoueur());
        assertEquals(4, jeu.hauteur(0));
    }

    @Test
    public void testRefaisSansFutur() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(2, 2);
        assertFalse(jeu.peutRefaire());
        assertEquals(1, jeu.getJoueur());
        assertEquals(2, jeu.hauteur(2));
    }
}