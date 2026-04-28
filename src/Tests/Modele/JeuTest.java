package Tests.Modele;

import Modele.Coup;
import Modele.Jeu;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour Coup et Jeu (Jeu de Gaufre, poison haut-gauche).
 *
 * Convention d'encodage (nouvelle) :
 *   true  (1) → pas droite
 *   false (0) ↓ pas bas
 *
 * Grille pleine M×N : [true×N, false×M]
 *   Ex. 3×4 : [T,T,T,T,F,F,F]  →  g[0..3]=true, g[4..6]=false
 *
 * Sémantique du coup (l, c) :
 *   Supprime les cases (l', c') avec l' >= l ET c' >= c.
 *   → hauteur(i) = min(hauteur(i), c)  pour i dans [l .. M-1].
 *
 * jeuTermine() ↔ hauteur(0)==0 (ligne du haut vide, poison mangé).
 */
public class JeuTest {

    // =========================================================================
    // Coup
    // =========================================================================

    @Test
    public void testCoupStockeCoordonnees() {
        boolean[] grille = new boolean[7];
        Coup c = new Coup(1, 2, grille);
        assertEquals("l doit être stocké", 1, c.getL());
        assertEquals("c doit être stocké", 2, c.getC());
    }

    @Test
    public void testCoupStockeSavedGrille() {
        // Grille pleine 3×4 : [T,T,T,T,F,F,F]
        boolean[] grille = {true, true, true, true, false, false, false};
        Coup c = new Coup(0, 1, grille);
        assertNotNull(c.getSavedGrille());
        assertEquals(7, c.getSavedGrille().length);
        assertTrue("g[0] doit être true",   c.getSavedGrille()[0]);
        assertFalse("g[4] doit être false",  c.getSavedGrille()[4]);
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
        // Grille pleine 5×7 : [true×7, false×5]  →  g[0..6]=true, g[7..11]=false
        boolean[] g = jeu.getGrille();
        assertEquals(1, jeu.getJoueur());
        for (int k = 0; k < 7;  k++) assertTrue("g[" + k + "] doit être true",  g[k]);
        for (int k = 7; k < 12; k++) assertFalse("g[" + k + "] doit être false", g[k]);
    }

    @Test
    public void testConstructeurParametresArbitraires() {
        Jeu jeu = new Jeu(2, 3, 1);
        // Grille pleine 2×3 : [T,T,T,F,F]
        boolean[] g = jeu.getGrille();
        assertEquals(2, jeu.getLignes());
        assertEquals(3, jeu.getColonnes());
        assertEquals(5, g.length);
        assertTrue("g[0]=true",   g[0]);
        assertTrue("g[1]=true",   g[1]);
        assertTrue("g[2]=true",   g[2]);
        assertFalse("g[3]=false", g[3]);
        assertFalse("g[4]=false", g[4]);
    }

    @Test
    public void testConstructeur3x4GrillePleine() {
        Jeu jeu = new Jeu(3, 4, 0);
        // [T,T,T,T,F,F,F]
        boolean[] g = jeu.getGrille();
        assertEquals(7, g.length);
        assertTrue("g[0]=true",   g[0]);
        assertTrue("g[3]=true",   g[3]);
        assertFalse("g[4]=false", g[4]);
        assertFalse("g[5]=false", g[5]);
        assertFalse("g[6]=false", g[6]);
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
        jeu.joue(0, 2);  // toutes les lignes capées à 2
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
        assertEquals("joueur = 1",     1, jeu.getJoueur());
        assertEquals("hauteur(0) = 4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1) = 2", 2, jeu.hauteur(1));
        assertEquals("hauteur(2) = 2", 2, jeu.hauteur(2));
    }

    @Test
    public void testCoup_l0_c2_capeToutes() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(0,2) : toutes les lignes capées à 2
        assertTrue(jeu.joue(0, 2));
        assertEquals("hauteur(0) = 2", 2, jeu.hauteur(0));
        assertEquals("hauteur(1) = 2", 2, jeu.hauteur(1));
        assertEquals("hauteur(2) = 2", 2, jeu.hauteur(2));
    }

    @Test
    public void testCoup_l2_c3_seulementLigne2() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(2,3) : seule la ligne 2 (bas) est capée à 3
        assertTrue(jeu.joue(2, 3));
        assertEquals("hauteur(0) = 4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1) = 4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2) = 3", 3, jeu.hauteur(2));
    }

    @Test
    public void testPresenceApresUnCoup() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);   // hauteur = [0→4, 1→2, 2→2]
        // Ligne 0 inchangée
        assertTrue("(0,3) présente",  jeu.estPresente(0, 3));
        // Lignes 1 et 2 capées à 2
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
        assertTrue("jeu terminé",       jeu.jeuTermine());
        assertEquals("hauteur(0)=0", 0, jeu.hauteur(0));
        assertEquals("hauteur(1)=0", 0, jeu.hauteur(1));
        assertEquals("hauteur(2)=0", 0, jeu.hauteur(2));
    }

    @Test
    public void testDeuxCoupsSuccessifs() {
        Jeu jeu = new Jeu(3, 4, 0);
        // Coup 1 : (2,2) → seule ligne 2 capée → hauteur = [4,4,2]
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
        // w interne : [w_l2, w_l1, w_l0] = [3, 4, 4]
        // Encodage :
        //   i=0 (l=2): 3 vrais, 1 faux
        //   i=1 (l=1): 1 vrai  (delta=4-3), 1 faux
        //   i=2 (l=0): 0 vrai  (delta=4-4), 1 faux
        //   reste    : 0 vrais
        // → [T,T,T,F,T,F,F]
        jeu.joue(2, 3);
        boolean[] g = jeu.getGrille();
        assertEquals(7, g.length);
        assertTrue("g[0]=true",   g[0]);
        assertTrue("g[1]=true",   g[1]);
        assertTrue("g[2]=true",   g[2]);
        assertFalse("g[3]=false", g[3]);
        assertTrue("g[4]=true",   g[4]);
        assertFalse("g[5]=false", g[5]);
        assertFalse("g[6]=false", g[6]);
    }

    @Test
    public void testBitsApresJoue_l1_c2_sur3x4() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(1,2) : lignes 1 et 2 capées à 2 → hauteur = [4,2,2]
        // w interne : [w_l2, w_l1, w_l0] = [2, 2, 4]
        // Encodage :
        //   i=0 (l=2): 2 vrais, 1 faux
        //   i=1 (l=1): 0 vrais (delta=0), 1 faux
        //   i=2 (l=0): 2 vrais (delta=2), 1 faux
        //   reste    : 0 vrais
        // → [T,T,F,F,T,T,F]
        jeu.joue(1, 2);
        boolean[] g = jeu.getGrille();
        assertTrue("g[0]=true",   g[0]);
        assertTrue("g[1]=true",   g[1]);
        assertFalse("g[2]=false", g[2]);
        assertFalse("g[3]=false", g[3]);
        assertTrue("g[4]=true",   g[4]);
        assertTrue("g[5]=true",   g[5]);
        assertFalse("g[6]=false", g[6]);
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

        jeu.annule();   // → hauteur=[4,4,2,2]
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));
        assertFalse(jeu.jeuTermine());

        jeu.annule();   // → hauteur=[4,4,4,4]
        assertEquals("joueur=0",     0, jeu.getJoueur());
        assertEquals("hauteur(2)=4", 4, jeu.hauteur(2));
        assertEquals("hauteur(3)=4", 4, jeu.hauteur(3));
        assertFalse(jeu.peutAnnuler());

        jeu.refais();   // → hauteur=[4,4,2,2]
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));
        assertEquals("hauteur(3)=2", 2, jeu.hauteur(3));

        jeu.refais();   // → jeu terminé
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