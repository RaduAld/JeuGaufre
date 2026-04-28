package Tests.Modele;

import Modele.Jeu;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour l'Historique (undo/redo) du Jeu de Gaufre.
 *
 * Convention : true (1) → droite,  false (0) ↓ bas.
 * Grille pleine M×N : [true×N, false×M]
 *   Ex. 3×4 : [T,T,T,T,F,F,F]  →  g[0..3]=true, g[4..6]=false
 */
public class HistoriqueTest {

    @Test
    public void testInitialisationJeu() {
        Jeu jeu = new Jeu(3, 4, 0);
        boolean[] g = jeu.getGrille();
        assertEquals(3, jeu.getLignes());
        assertEquals(4, jeu.getColonnes());
        assertEquals(7, g.length);
        // Grille pleine : [T,T,T,T,F,F,F]
        assertTrue("g[0]=true",   g[0]);
        assertTrue("g[3]=true",   g[3]);
        assertFalse("g[4]=false", g[4]);
        assertFalse("g[6]=false", g[6]);
        assertEquals(0, jeu.getJoueur());
        assertEquals(4, jeu.hauteur(0));
        assertEquals(4, jeu.hauteur(2));
    }

    @Test
    public void testCoupValide_l1_c2() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(1,2) : lignes 1 et 2 capées à 2 ; ligne 0 inchangée
        assertTrue(jeu.joue(1, 2));
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=2", 2, jeu.hauteur(1));
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));
        assertTrue("(0,3) présente",  jeu.estPresente(0, 3));
        assertFalse("(1,2) vide",     jeu.estPresente(1, 2));
        assertFalse("(2,3) vide",     jeu.estPresente(2, 3));
    }

    @Test
    public void testCoupInvalide() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse(jeu.joue(4, 1));
        assertEquals(0, jeu.getJoueur());
        assertEquals(4, jeu.hauteur(0));
        assertEquals(4, jeu.hauteur(2));
        // Grille inchangée : [T,T,T,T,F,F,F]
        boolean[] g = jeu.getGrille();
        assertTrue("g[0]=true",   g[0]);
        assertTrue("g[3]=true",   g[3]);
        assertFalse("g[4]=false", g[4]);
        assertFalse("g[6]=false", g[6]);
    }

    @Test
    public void testAnnuleRestaureEtatPrecedent() {
        Jeu jeu = new Jeu(3, 4, 0);
        boolean[] avant = jeu.getGrille();
        jeu.joue(1, 2);
        assertTrue(jeu.peutAnnuler());
        jeu.annule();
        assertEquals("joueur=0",     0, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2)=4", 4, jeu.hauteur(2));
        assertArrayEquals("bits restaurés à l'identique", avant, jeu.getGrille());
        assertFalse(jeu.peutAnnuler());
    }

    @Test
    public void testAnnuleSansHistorique() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse(jeu.peutAnnuler());
        assertEquals(0, jeu.getJoueur());
        assertEquals(4, jeu.hauteur(0));
    }

    @Test
    public void testRefaisRejoueLeCoupAnnule() {
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
    public void testRefaisSansFutur() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(2, 2);
        assertFalse(jeu.peutRefaire());
        assertEquals(1, jeu.getJoueur());
        assertEquals(4, jeu.hauteur(0));
        assertEquals(2, jeu.hauteur(2));
    }

    @Test
    public void testNouveauCoupVideFutur() {
        Jeu jeu = new Jeu(4, 4, 0);
        jeu.joue(1, 1);
        jeu.annule();
        assertTrue(jeu.joue(2, 2));
        assertFalse("futur vide", jeu.peutRefaire());
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));
        assertEquals("hauteur(3)=2", 2, jeu.hauteur(3));
    }

    @Test
    public void testChaineUndoRedo() {
        Jeu jeu = new Jeu(3, 4, 0);

        // Coup 1 : (2,3) → seule ligne 2 capée à 3 → hauteur=[4,4,3]
        assertTrue(jeu.joue(2, 3));
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(2)=3", 3, jeu.hauteur(2));

        // Coup 2 : (1,1) → lignes 1 et 2 capées à 1 → hauteur=[4,1,1]
        assertTrue(jeu.joue(1, 1));
        assertEquals("joueur=0",     0, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(1)=1", 1, jeu.hauteur(1));
        assertEquals("hauteur(2)=1", 1, jeu.hauteur(2));

        // Undo coup 2 → hauteur=[4,4,3]
        jeu.annule();
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(1)=4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2)=3", 3, jeu.hauteur(2));

        // Undo coup 1 → hauteur=[4,4,4]
        jeu.annule();
        assertEquals("joueur=0",     0, jeu.getJoueur());
        assertEquals("hauteur(2)=4", 4, jeu.hauteur(2));
        assertFalse(jeu.peutAnnuler());

        // Redo coup 1 → hauteur=[4,4,3]
        jeu.refais();
        assertEquals("joueur=1",     1, jeu.getJoueur());
        assertEquals("hauteur(2)=3", 3, jeu.hauteur(2));

        // Redo coup 2 → hauteur=[4,1,1]
        jeu.refais();
        assertEquals("joueur=0",     0, jeu.getJoueur());
        assertEquals("hauteur(1)=1", 1, jeu.hauteur(1));
        assertEquals("hauteur(2)=1", 1, jeu.hauteur(2));
        assertFalse(jeu.peutRefaire());
    }
}