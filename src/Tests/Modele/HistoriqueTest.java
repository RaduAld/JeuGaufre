package Tests.Modele;

import Modele.Jeu;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour l'Historique (undo/redo) du Jeu de Gaufre.
 *
 * La grille est boolean[M+N], source unique de vérité.
 * Undo : restaure les bits grille[0..savedSegment.length-1].
 * Redo : ré-applique le coup (min-cap) sur les bits.
 */
public class HistoriqueTest {

    @Test
    public void testInitialisationJeu() {
        Jeu jeu = new Jeu(3, 4, 0);
        boolean[] g = jeu.getGrille();
        assertEquals("Lignes = 3",        3, jeu.getLignes());
        assertEquals("Colonnes = 4",      4, jeu.getColonnes());
        assertEquals("Taille grille = 7", 7, g.length);
        // Grille pleine : [F,F,F,F,T,T,T]
        assertFalse("g[0]=false", g[0]);
        assertFalse("g[3]=false", g[3]);
        assertTrue("g[4]=true",   g[4]);
        assertTrue("g[6]=true",   g[6]);
        assertEquals("Joueur = 0",    0, jeu.getJoueur());
        assertEquals("hauteur(0) = 4", 4, jeu.hauteur(0));
        assertEquals("hauteur(2) = 4", 4, jeu.hauteur(2));
    }

    @Test
    public void testCoupValideMetAJourGrilleEtJoueur() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(1,2) : hauteur[0..1] = min(4,2) = 2 ; ligne 2 inchangée
        assertTrue("Coup (1,2) valide", jeu.joue(1, 2));
        assertEquals("Joueur = 1",        1, jeu.getJoueur());
        assertEquals("hauteur(0) = 2",    2, jeu.hauteur(0));
        assertEquals("hauteur(1) = 2",    2, jeu.hauteur(1));
        assertEquals("hauteur(2) = 4",    4, jeu.hauteur(2));
        assertTrue("(1,0) présente",  jeu.estPresente(1, 0));
        assertTrue("(1,1) présente",  jeu.estPresente(1, 1));
        assertTrue("(2,3) présente",  jeu.estPresente(2, 3));
        assertFalse("(1,2) vide",     jeu.estPresente(1, 2));
        assertFalse("(0,3) vide",     jeu.estPresente(0, 3));
    }

    @Test
    public void testCoupInvalideNeModifiePasEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Coup hors grille refusé", jeu.joue(4, 1));
        assertEquals("Joueur = 0",        0, jeu.getJoueur());
        assertEquals("hauteur(0) = 4",    4, jeu.hauteur(0));
        assertEquals("hauteur(2) = 4",    4, jeu.hauteur(2));
        boolean[] g = jeu.getGrille();
        assertFalse("g[0]=false", g[0]);
        assertFalse("g[3]=false", g[3]);
        assertTrue("g[4]=true",   g[4]);
        assertTrue("g[6]=true",   g[6]);
    }

    @Test
    public void testAnnuleRestaureEtatPrecedent() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);
        assertTrue("Annulation possible", jeu.peutAnnuler());
        jeu.annule();
        assertEquals("Joueur = 0",       0, jeu.getJoueur());
        assertEquals("hauteur(0) = 4",   4, jeu.hauteur(0));
        assertEquals("hauteur(1) = 4",   4, jeu.hauteur(1));
        assertEquals("hauteur(2) = 4",   4, jeu.hauteur(2));
        assertFalse("Second undo impossible", jeu.peutAnnuler());
    }

    @Test
    public void testAnnuleRestaureLesExactsBits() {
        Jeu jeu = new Jeu(3, 4, 0);
        boolean[] grilleInitiale = jeu.getGrille();
        jeu.joue(1, 2);
        jeu.annule();
        assertArrayEquals("Bits restaurés à l'identique",
                grilleInitiale, jeu.getGrille());
    }

    @Test
    public void testAnnuleSansHistoriqueNeCassePasLEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Undo sans coup impossible", jeu.peutAnnuler());
        assertEquals("Joueur = 0",     0, jeu.getJoueur());
        assertEquals("hauteur(0) = 4", 4, jeu.hauteur(0));
        assertEquals("hauteur(2) = 4", 4, jeu.hauteur(2));
    }

    @Test
    public void testRefaisRejoueLeCoupAnnule() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);
        jeu.annule();
        assertTrue("Redo possible", jeu.peutRefaire());
        jeu.refais();
        assertEquals("Joueur = 1",       1, jeu.getJoueur());
        assertEquals("hauteur(0) = 2",   2, jeu.hauteur(0));
        assertEquals("hauteur(1) = 2",   2, jeu.hauteur(1));
        assertEquals("hauteur(2) = 4",   4, jeu.hauteur(2));
        assertFalse("Second redo impossible", jeu.peutRefaire());
    }

    @Test
    public void testRefaisRestaureLesExactsBits() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);
        boolean[] grilleApresCoup = jeu.getGrille();
        jeu.annule();
        jeu.refais();
        assertArrayEquals("Bits restaurés après redo",
                grilleApresCoup, jeu.getGrille());
    }

    @Test
    public void testRefaisSansFuturNeCassePasLEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(2, 2);
        assertFalse("Redo sans futur impossible", jeu.peutRefaire());
        assertEquals("Joueur = 1",     1, jeu.getJoueur());
        assertEquals("hauteur(0) = 2", 2, jeu.hauteur(0));
        assertEquals("hauteur(2) = 2", 2, jeu.hauteur(2));
    }

    @Test
    public void testNouveauCoupVideLeFutur() {
        Jeu jeu = new Jeu(4, 4, 0);
        jeu.joue(1, 1);
        jeu.annule();
        assertTrue("Nouveau coup valide", jeu.joue(2, 2));
        assertFalse("Redo impossible après nouveau coup", jeu.peutRefaire());
        assertEquals("Joueur = 1",       1, jeu.getJoueur());
        assertEquals("hauteur(0) = 2",   2, jeu.hauteur(0));
        assertEquals("hauteur(1) = 2",   2, jeu.hauteur(1));
        assertEquals("hauteur(2) = 2",   2, jeu.hauteur(2));
        assertEquals("hauteur(3) = 4",   4, jeu.hauteur(3));
    }

    @Test
    public void testChaineUndoRedoAvecDeuxCoups() {
        Jeu jeu = new Jeu(3, 4, 0);

        // Coup 1 : (2,3) → hauteur = [3,3,3]
        assertTrue(jeu.joue(2, 3));
        assertEquals("Joueur = 1",   1, jeu.getJoueur());
        assertEquals("hauteur(0)=3", 3, jeu.hauteur(0));

        // Coup 2 : (1,1) → hauteur[0..1] = min(3,1)=1 → [1,1,3]
        assertTrue(jeu.joue(1, 1));
        assertEquals("Joueur = 0",   0, jeu.getJoueur());
        assertEquals("hauteur(0)=1", 1, jeu.hauteur(0));
        assertEquals("hauteur(1)=1", 1, jeu.hauteur(1));
        assertEquals("hauteur(2)=3", 3, jeu.hauteur(2));

        // Undo coup 2 → [3,3,3]
        jeu.annule();
        assertEquals("Joueur = 1",   1, jeu.getJoueur());
        assertEquals("hauteur(0)=3", 3, jeu.hauteur(0));
        assertEquals("hauteur(1)=3", 3, jeu.hauteur(1));
        assertEquals("hauteur(2)=3", 3, jeu.hauteur(2));

        // Undo coup 1 → [4,4,4]
        jeu.annule();
        assertEquals("Joueur = 0",   0, jeu.getJoueur());
        assertEquals("hauteur(0)=4", 4, jeu.hauteur(0));
        assertEquals("hauteur(2)=4", 4, jeu.hauteur(2));
        assertFalse("Plus d'undo", jeu.peutAnnuler());

        // Redo coup 1 → [3,3,3]
        jeu.refais();
        assertEquals("Joueur = 1",   1, jeu.getJoueur());
        assertEquals("hauteur(0)=3", 3, jeu.hauteur(0));
        assertEquals("hauteur(2)=3", 3, jeu.hauteur(2));

        // Redo coup 2 → [1,1,3]
        jeu.refais();
        assertEquals("Joueur = 0",   0, jeu.getJoueur());
        assertEquals("hauteur(0)=1", 1, jeu.hauteur(0));
        assertEquals("hauteur(1)=1", 1, jeu.hauteur(1));
        assertEquals("hauteur(2)=3", 3, jeu.hauteur(2));
        assertFalse("Plus de redo", jeu.peutRefaire());
    }
}