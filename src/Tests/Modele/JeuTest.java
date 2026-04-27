package Tests.Modele;

import Modele.Coup;
import Modele.Jeu;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour Coup et Jeu (Jeu de Gaufre).
 *
 * Représentation :
 *   boolean[] grille de taille M+N — source unique de vérité.
 *   false (0) = pas → (droite),  true (1) = pas ↓ (bas).
 *   Grille pleine M×N : [false×N, true×M]
 *   Ex. 3×4 : [F,F,F,F,T,T,T]  →  g[0..3]=false, g[4..6]=true
 *
 *   hauteur(l) = largeur de la ligne l = nb de cases présentes depuis col 0.
 *   Coup (l,c) : hauteur(i) = min(hauteur(i), c) pour i dans [0..l].
 *   jeuTermine() ↔ hauteur(0)==0 ↔ grille[0]==true.
 */
public class JeuTest {

    // =========================================================================
    // Coup
    // =========================================================================

    @Test
    public void testCoupStockeCoordonnees() {
        // Segment bidon de taille 6 = hauteur(1) + 2 = 4 + 2
        boolean[] seg = {false, false, false, false, true, true};
        Coup c = new Coup(1, 2, seg);
        assertEquals("l doit être stocké correctement", 1, c.getL());
        assertEquals("c doit être stocké correctement", 2, c.getC());
    }

    @Test
    public void testCoupStockeSavedSegment() {
        // Avant joue(2,1) sur grille pleine 3×4 :
        // segment = grille[0..pos_2] = grille[0..6] = [F,F,F,F,T,T,T]
        boolean[] seg = {false, false, false, false, true, true, true};
        Coup c = new Coup(2, 1, seg);
        assertNotNull("savedSegment ne doit pas être null", c.getSavedSegment());
        assertEquals("savedSegment doit avoir 7 entrées", 7, c.getSavedSegment().length);
        assertFalse("savedSegment[0] doit être false", c.getSavedSegment()[0]);
        assertTrue("savedSegment[4] doit être true",   c.getSavedSegment()[4]);
    }

    @Test
    public void testCoupSurLigne0SegmentDeTaille1() {
        // Avant joue(0, c) : segment = grille[0..pos_0].
        // Sur grille pleine 3×4 : pos_0 = 4 (premier true à l'index 4),
        // segment = [F,F,F,F,T], taille = hauteur(0) + 1 = 4 + 1 = 5.
        boolean[] seg = {false, false, false, false, true};
        Coup c = new Coup(0, 2, seg);
        assertEquals("savedSegment doit avoir 5 entrées", 5, c.getSavedSegment().length);
    }

    // =========================================================================
    // Jeu — constructeurs et état initial
    // =========================================================================

    @Test
    public void testConstructeurDefautDimensions() {
        Jeu jeu = new Jeu(0);
        assertEquals("Lignes attendues",   5,  jeu.getLignes());
        assertEquals("Colonnes attendues", 7,  jeu.getColonnes());
        assertEquals("Taille grille = M+N = 12", 12, jeu.getGrille().length);
    }

    @Test
    public void testConstructeurDefautEtatInitial() {
        Jeu jeu = new Jeu(1);
        // Grille pleine 5×7 : [false×7, true×5]  →  g[0..6]=false, g[7..11]=true
        boolean[] g = jeu.getGrille();
        assertEquals("Joueur initial doit être 1", 1, jeu.getJoueur());
        for (int k = 0; k < 7;  k++) assertFalse("g[" + k + "] doit être false", g[k]);
        for (int k = 7; k < 12; k++) assertTrue("g["  + k + "] doit être true",  g[k]);
    }

    @Test
    public void testConstructeurParametresArbitraires() {
        Jeu jeu = new Jeu(2, 3, 1);
        // Grille pleine 2×3 : [F,F,F,T,T]  taille M+N = 5
        boolean[] g = jeu.getGrille();
        assertEquals("2 lignes",    2, jeu.getLignes());
        assertEquals("3 colonnes",  3, jeu.getColonnes());
        assertEquals("Taille = 5",  5, g.length);
        assertFalse("g[0]=false", g[0]);
        assertFalse("g[1]=false", g[1]);
        assertFalse("g[2]=false", g[2]);
        assertTrue("g[3]=true",   g[3]);
        assertTrue("g[4]=true",   g[4]);
    }

    @Test
    public void testConstructeur3x4GrilleInitiale() {
        Jeu jeu = new Jeu(3, 4, 0);
        // [F,F,F,F,T,T,T]
        boolean[] g = jeu.getGrille();
        assertEquals("Taille = 7", 7, g.length);
        assertFalse("g[0]=false", g[0]);
        assertFalse("g[3]=false", g[3]);
        assertTrue("g[4]=true",   g[4]);
        assertTrue("g[5]=true",   g[5]);
        assertTrue("g[6]=true",   g[6]);
    }

    @Test
    public void testHauteurInitiale() {
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
                assertTrue("(" + l + "," + c + ") doit être présente",
                        jeu.estPresente(l, c));
    }

    // =========================================================================
    // Jeu — joueurSuivant
    // =========================================================================

    @Test
    public void testJoueurSuivantAlterne() {
        Jeu jeu = new Jeu(3, 3, 0);
        jeu.joueurSuivant();
        assertEquals("Doit être 1", 1, jeu.getJoueur());
        jeu.joueurSuivant();
        assertEquals("Doit revenir à 0", 0, jeu.getJoueur());
    }

    // =========================================================================
    // Jeu — joue : cas invalides
    // =========================================================================

    @Test
    public void testJouerHorsGrilleLigne() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Ligne hors grille refusée", jeu.joue(4, 1));
        assertEquals("Joueur inchangé", 0, jeu.getJoueur());
    }

    @Test
    public void testJouerColonneNegative() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Colonne négative refusée", jeu.joue(0, -1));
        assertEquals("Joueur inchangé", 0, jeu.getJoueur());
    }

    @Test
    public void testJouerLigneNegative() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Ligne négative refusée", jeu.joue(-1, 0));
        assertEquals("Joueur inchangé", 0, jeu.getJoueur());
    }

    @Test
    public void testJouerSurCaseDejaVide() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(0,2) : hauteur(0) → 2 ; col 2 et 3 de la ligne 0 mangées
        jeu.joue(0, 2);
        int joueurAvant = jeu.getJoueur();
        assertFalse("Case vide refusée", jeu.joue(0, 3));
        assertEquals("Joueur inchangé", joueurAvant, jeu.getJoueur());
    }

    @Test
    public void testJouerApresJeuTermineEstRefuse() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(0, 0);
        assertTrue("Jeu terminé", jeu.jeuTermine());
        assertFalse("Coup après fin refusé", jeu.joue(1, 1));
    }

    // =========================================================================
    // Jeu — joue : vérification des hauteurs
    // =========================================================================

    @Test
    public void testCoupValideMetAJourHauteurs() {
        Jeu jeu = new Jeu(3, 4, 0);
        // Coup (1,2) : lignes 0 et 1 capées à 2 ; ligne 2 inchangée
        assertTrue("Coup (1,2) valide", jeu.joue(1, 2));
        assertEquals("Joueur = 1",       1, jeu.getJoueur());
        assertEquals("hauteur(0) = 2",   2, jeu.hauteur(0));
        assertEquals("hauteur(1) = 2",   2, jeu.hauteur(1));
        assertEquals("hauteur(2) = 4",   4, jeu.hauteur(2));
    }

    @Test
    public void testCoupValideMetAJourPresence() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);
        assertTrue("(0,0) présente",  jeu.estPresente(0, 0));
        assertTrue("(0,1) présente",  jeu.estPresente(0, 1));
        assertTrue("(1,0) présente",  jeu.estPresente(1, 0));
        assertTrue("(1,1) présente",  jeu.estPresente(1, 1));
        assertTrue("(2,3) présente",  jeu.estPresente(2, 3));
        assertFalse("(0,2) vide",     jeu.estPresente(0, 2));
        assertFalse("(0,3) vide",     jeu.estPresente(0, 3));
        assertFalse("(1,2) vide",     jeu.estPresente(1, 2));
        assertFalse("(1,3) vide",     jeu.estPresente(1, 3));
    }

    @Test
    public void testCoupUneCaseCornerBasDroit() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(2,3) : lignes 0,1,2 capées à 3
        assertTrue("Coup (2,3) valide", jeu.joue(2, 3));
        assertEquals("hauteur(0) = 3", 3, jeu.hauteur(0));
        assertEquals("hauteur(1) = 3", 3, jeu.hauteur(1));
        assertEquals("hauteur(2) = 3", 3, jeu.hauteur(2));
    }

    @Test
    public void testCoupSurLigne0SeulementLigne0() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(0,2) : seule la ligne 0 est capée à 2
        assertTrue("Coup (0,2) valide", jeu.joue(0, 2));
        assertEquals("hauteur(0) = 2", 2, jeu.hauteur(0));
        assertEquals("hauteur(1) = 4", 4, jeu.hauteur(1));
        assertEquals("hauteur(2) = 4", 4, jeu.hauteur(2));
    }

    @Test
    public void testCoupInvalideNeModifiePasEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Coup hors grille refusé", jeu.joue(5, 0));
        assertEquals("Joueur = 0",       0, jeu.getJoueur());
        assertEquals("hauteur(0) = 4",   4, jeu.hauteur(0));
        assertEquals("hauteur(1) = 4",   4, jeu.hauteur(1));
        assertEquals("hauteur(2) = 4",   4, jeu.hauteur(2));
    }

    @Test
    public void testJouerSurLaCasePoisonTermineLeJeu() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(0,0) : seule la ligne 0 est capée à 0 → hauteur = [0, 4, 4]
        assertTrue("Coup (0,0) accepté",        jeu.joue(0, 0));
        assertTrue("Jeu terminé",               jeu.jeuTermine());
        assertEquals("hauteur(0) = 0",      0,  jeu.hauteur(0));
        assertEquals("hauteur(1) = 4",      4,  jeu.hauteur(1));
        assertEquals("hauteur(2) = 4",      4,  jeu.hauteur(2));
        // Grille avec hauteur=[0,4,4] :
        //   ligne 0 : 0 faux, 1 vrai   → g[0]=true
        //   ligne 1 : 4 faux, 1 vrai   → g[1..4]=false, g[5]=true
        //   ligne 2 : 0 faux, 1 vrai   → g[6]=true
        boolean[] g = jeu.getGrille();
        assertTrue("g[0]=true  (↓ immédiat)", g[0]);
        assertFalse("g[1]=false", g[1]);
        assertFalse("g[2]=false", g[2]);
        assertFalse("g[3]=false", g[3]);
        assertFalse("g[4]=false", g[4]);
        assertTrue("g[5]=true",   g[5]);
        assertTrue("g[6]=true",   g[6]);
    }

    @Test
    public void testDeuxCoupsSuccessifs() {
        Jeu jeu = new Jeu(3, 4, 0);
        // Coup 1 : (2,2) → hauteur = [2,2,2]
        assertTrue(jeu.joue(2, 2));
        assertEquals("Joueur = 1",   1, jeu.getJoueur());
        assertEquals("hauteur(0)=2", 2, jeu.hauteur(0));
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));

        // Coup 2 : (1,1) → hauteur[0..1] = min(2,1)=1 → hauteur = [1,1,2]
        assertTrue(jeu.joue(1, 1));
        assertEquals("Joueur = 0",   0, jeu.getJoueur());
        assertEquals("hauteur(0)=1", 1, jeu.hauteur(0));
        assertEquals("hauteur(1)=1", 1, jeu.hauteur(1));
        assertEquals("hauteur(2)=2", 2, jeu.hauteur(2));
    }

    // =========================================================================
    // Jeu — cohérence des bits après un coup
    // =========================================================================

    @Test
    public void testBitsCoherentsApresUnCoup() {
        Jeu jeu = new Jeu(3, 4, 0);
        // joue(1,2) → hauteur = [2,2,4]
        // Encodage staircase :
        //   ligne 0 : 2 faux, 1 vrai
        //   ligne 1 : 0 faux (delta=0), 1 vrai
        //   ligne 2 : 2 faux (delta=2), 1 vrai
        //   reste   : 0 faux
        // → [F,F,T,T,F,F,T]
        jeu.joue(1, 2);
        boolean[] g = jeu.getGrille();
        assertEquals("Taille = 7", 7, g.length);
        assertFalse("g[0]=false", g[0]);
        assertFalse("g[1]=false", g[1]);
        assertTrue("g[2]=true",   g[2]);
        assertTrue("g[3]=true",   g[3]);
        assertFalse("g[4]=false", g[4]);
        assertFalse("g[5]=false", g[5]);
        assertTrue("g[6]=true",   g[6]);
    }

    // =========================================================================
    // Jeu — annuler / refaire
    // =========================================================================

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
        boolean[] grilleAvant = jeu.getGrille();
        jeu.joue(1, 2);
        jeu.annule();
        boolean[] grilleApres = jeu.getGrille();
        assertArrayEquals("Les bits doivent être restaurés à l'identique",
                grilleAvant, grilleApres);
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
    public void testNouveauCoupVideLeFutur() {
        Jeu jeu = new Jeu(4, 4, 0);
        jeu.joue(1, 1);
        jeu.annule();
        assertTrue("Nouveau coup valide", jeu.joue(2, 2));
        assertFalse("Redo impossible après nouveau coup", jeu.peutRefaire());
        assertEquals("Joueur = 1",       1, jeu.getJoueur());
        // joue(2,2) : hauteur[0..2] = min(4,2) = 2 → [2,2,2,4]
        assertEquals("hauteur(0) = 2",   2, jeu.hauteur(0));
        assertEquals("hauteur(1) = 2",   2, jeu.hauteur(1));
        assertEquals("hauteur(2) = 2",   2, jeu.hauteur(2));
        assertEquals("hauteur(3) = 4",   4, jeu.hauteur(3));
    }

    @Test
    public void testChaineAnnuleRefaisMultiple() {
        Jeu jeu = new Jeu(4, 4, 0);
        // Coup 1 : (2,2) → hauteur = [2,2,2,4]
        assertTrue(jeu.joue(2, 2));
        // Coup 2 : (3,0) → hauteur[0..3] = min(·,0) → [0,0,0,0]
        assertTrue(jeu.joue(3, 0));
        assertEquals("hauteur(3)=0", 0, jeu.hauteur(3));
        assertTrue("Jeu terminé", jeu.jeuTermine());

        // Undo coup 2 → [2,2,2,4]
        jeu.annule();
        assertEquals("Joueur = 1",       1, jeu.getJoueur());
        assertEquals("hauteur(0) = 2",   2, jeu.hauteur(0));
        assertEquals("hauteur(3) = 4",   4, jeu.hauteur(3));
        assertFalse("Jeu non terminé", jeu.jeuTermine());

        // Undo coup 1 → [4,4,4,4]
        jeu.annule();
        assertEquals("Joueur = 0",       0, jeu.getJoueur());
        assertEquals("hauteur(0) = 4",   4, jeu.hauteur(0));
        assertEquals("hauteur(2) = 4",   4, jeu.hauteur(2));
        assertEquals("hauteur(3) = 4",   4, jeu.hauteur(3));

        // Redo coup 1 → [2,2,2,4]
        jeu.refais();
        assertEquals("Joueur = 1",       1, jeu.getJoueur());
        assertEquals("hauteur(0) = 2",   2, jeu.hauteur(0));
        assertEquals("hauteur(2) = 2",   2, jeu.hauteur(2));
        assertEquals("hauteur(3) = 4",   4, jeu.hauteur(3));

        // Redo coup 2 → [0,0,0,0]
        jeu.refais();
        assertEquals("Joueur = 0",       0, jeu.getJoueur());
        assertEquals("hauteur(0) = 0",   0, jeu.hauteur(0));
        assertEquals("hauteur(3) = 0",   0, jeu.hauteur(3));
        assertTrue("Jeu terminé à nouveau", jeu.jeuTermine());
        assertFalse("Redo impossible", jeu.peutRefaire());
    }

    @Test
    public void testAnnuleSansHistoriqueNeCassePasLEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Undo sans coup impossible", jeu.peutAnnuler());
        assertEquals("Joueur = 0",     0, jeu.getJoueur());
        assertEquals("hauteur(0) = 4", 4, jeu.hauteur(0));
    }

    @Test
    public void testRefaisSansFuturNeCassePasLEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(2, 2);
        assertFalse("Redo sans futur impossible", jeu.peutRefaire());
        assertEquals("Joueur = 1",     1, jeu.getJoueur());
        assertEquals("hauteur(2) = 2", 2, jeu.hauteur(2));
    }
}