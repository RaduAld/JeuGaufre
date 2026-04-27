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
        ArrayList<Integer> liste = new ArrayList<>();
        liste.add(6);   // index linéaire quelconque
        Coup c = new Coup(1, 2, liste);

        assertEquals("i doit être stocke correctement", 1, c.getI());
        assertEquals("j doit être stocke correctement", 2, c.getJ());
    }

    @Test
    public void testCoupStockeLaListeDesCasesModifiees() {
        // Dans une grille à 4 colonnes : (0,1)->1 , (1,1)->5
        ArrayList<Integer> liste = new ArrayList<>();
        liste.add(1);
        liste.add(5);
        Coup c = new Coup(0, 1, liste);

        assertEquals("La liste doit avoir 2 entrees", 2, c.getChangedToFalse().size());
        assertEquals("Premier index incorrect",  Integer.valueOf(1), c.getChangedToFalse().get(0));
        assertEquals("Deuxieme index incorrect", Integer.valueOf(5), c.getChangedToFalse().get(1));
    }

    @Test
    public void testCoupAvecListeVide() {
        ArrayList<Integer> liste = new ArrayList<>();
        Coup c = new Coup(2, 3, liste);

        assertNotNull("changedToFalse ne doit pas être null", c.getChangedToFalse());
        assertTrue("changedToFalse doit être vide", c.getChangedToFalse().isEmpty());
    }

    // -------------------------------------------------------------------------
    // Jeu — constructeurs
    // -------------------------------------------------------------------------

    @Test
    public void testConstructeurDefautDimensions() {
        Jeu jeu = new Jeu(0);

        assertEquals("Le constructeur par défaut doit créer 5 lignes",   5, jeu.getLignes());
        assertEquals("Le constructeur par défaut doit créer 7 colonnes", 7, jeu.getColonnes());
        assertEquals("La taille du vecteur doit être lignes*colonnes", 5 * 7, jeu.getGrille().length);
    }

    @Test
    public void testConstructeurDefautEtatInitial() {
        Jeu jeu = new Jeu(1);
        boolean[] grille = jeu.getGrille();

        assertEquals("Le joueur initial doit être 1", 1, jeu.getJoueur());
        // Case poison (0,0) -> index 0 : présente
        assertTrue("La case poison (0,0) doit être présente", grille[0]);
        // Case (4,6) -> index 4*7+6 = 34 : présente
        assertTrue("Une case normale doit être présente", grille[34]);
    }

    @Test
    public void testConstructeurParametresArbitraires() {
        Jeu jeu = new Jeu(2, 3, 1);
        boolean[] grille = jeu.getGrille();

        assertEquals("2 lignes attendues",   2, jeu.getLignes());
        assertEquals("3 colonnes attendues", 3, jeu.getColonnes());
        assertEquals("Taille vecteur = 2*3", 6, grille.length);
        // (0,0) -> index 0 : poison, présent
        assertTrue("Case poison en (0,0) doit être présente", grille[0]);
        // (1,2) -> index 1*3+2 = 5 : présente
        assertTrue("Case (1,2) doit être présente", grille[5]);
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
        jeu.joue(1, 1); // mange tout le rectangle [1..2][1..3]
        int joueurAvant = jeu.getJoueur();

        // (2,3) -> index 2*4+3 = 11 : maintenant false (mangée)
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
    public void testJouerSurLaCasePoisonTermineLeJeu() {
        // Jouer (0,0) mange toute la grille -> jeuTermine() devient true
        Jeu jeu = new Jeu(3, 4, 0);
        assertTrue("Jouer sur la case poison doit être accepté", jeu.joue(0, 0));
        assertTrue("Le jeu doit être terminé après avoir mangé le poison", jeu.jeuTermine());
        // index 0 (0,0) doit être false
        assertFalse("La case poison doit être mangée (false)", jeu.getGrille()[0]);
    }

    @Test
    public void testJouerUnSeuleCaseCorner() {
        Jeu jeu = new Jeu(3, 4, 0);
        // Coin bas-droit (2,3) -> index 2*4+3 = 11 : ne touche qu'une seule case
        assertTrue("Le coup doit être validé", jeu.joue(2, 3));
        assertFalse("La case (2,3) doit être mangée (false)", jeu.getGrille()[11]);
        // (2,2) -> index 10 : non touchée
        assertTrue("La case (2,2) ne doit pas être affectee", jeu.getGrille()[10]);
    }

    @Test
    public void testJouerApresJeuTermineEstRefuse() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(0, 0); // termine le jeu
        assertFalse("Jouer après la fin du jeu doit être refusé", jeu.joue(1, 1));
    }

    // -------------------------------------------------------------------------
    // Jeu — joue : vérification grille
    // -------------------------------------------------------------------------

    @Test
    public void testCoupValideMetAJourGrilleEtJoueur() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertTrue("Le coup (1,2) devrait être valide", jeu.joue(1, 2));

        boolean[] grille = jeu.getGrille();
        assertEquals("Le joueur doit changer après un coup valide", 1, jeu.getJoueur());

        // Cases hors du rectangle [1..2][2..3] : non touchées
        assertTrue("(0,3) -> index 3 : non touchée",  grille[3]);   // 0*4+3
        assertTrue("(1,1) -> index 5 : non touchée",  grille[5]);   // 1*4+1  (à gauche du coup)
        // Cases dans le rectangle : mangées
        assertFalse("(1,2) -> index 6 : doit être mangée",  grille[6]);   // 1*4+2
        assertFalse("(2,3) -> index 11 : doit être mangée", grille[11]);  // 2*4+3
    }

    @Test
    public void testCoupInvalideNeModifiePasEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Un coup hors grille devrait être refusé", jeu.joue(4, 1));

        boolean[] grille = jeu.getGrille();
        assertEquals("Le joueur ne doit pas changer après un coup invalide", 0, jeu.getJoueur());
        // (0,0) -> index 0 : poison, toujours présent
        assertTrue("La case poison doit rester présente", grille[0]);
        // (2,3) -> index 11 : toujours présent
        assertTrue("La grille ne doit pas être modifiée", grille[11]);
    }

    // -------------------------------------------------------------------------
    // Jeu — chaîne undo/redo multiple
    // -------------------------------------------------------------------------

    @Test
    public void testAnnuleRestaureEtatPrecedent() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);

        assertTrue("L'annulation du dernier coup devrait réussir", jeu.peutAnnuler());
        jeu.annule();

        boolean[] grille = jeu.getGrille();
        assertEquals("Le joueur doit revenir au joueur précédent", 0, jeu.getJoueur());
        // (1,2) -> index 6 et (2,3) -> index 11 : restaurées
        assertTrue("La case (1,2) doit être restaurée", grille[6]);
        assertTrue("La case (2,3) doit être restaurée", grille[11]);
        assertFalse("Un second undo immédiat doit échouer sans historique", jeu.peutAnnuler());
    }

    @Test
    public void testRefaisRejoueLeCoupAnnule() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);
        jeu.annule();

        assertTrue("Le redo devrait réussir après un undo", jeu.peutRefaire());
        jeu.refais();

        boolean[] grille = jeu.getGrille();
        assertEquals("Le joueur doit rechanger après redo", 1, jeu.getJoueur());
        assertFalse("La case (1,2) -> index 6 doit être mangée",  grille[6]);
        assertFalse("La case (2,3) -> index 11 doit être mangée", grille[11]);
        assertFalse("Un second redo doit échouer si le futur est vide", jeu.peutRefaire());
    }

    @Test
    public void testNouveauCoupVideLeFutur() {
        Jeu jeu = new Jeu(4, 4, 0);
        jeu.joue(1, 1);
        jeu.annule();

        assertTrue("Le nouveau coup doit être valide", jeu.joue(2, 2));
        assertFalse("Le redo doit être impossible après un nouveau coup", jeu.peutRefaire());

        boolean[] grille = jeu.getGrille();
        assertEquals("Le joueur doit correspondre au nouveau coup joué", 1, jeu.getJoueur());
        // (2,2) -> index 2*4+2 = 10 : mangée
        assertFalse("La nouvelle zone jouée doit être supprimée", grille[10]);
        // (1,1) -> index 1*4+1 = 5 : l'ancien coup a été annulé et non rejoué
        assertTrue("L'ancienne zone annulée ne doit pas être rejouée", grille[5]);
    }

    @Test
    public void testChaineAnnuleRefaisMultiple() {
        Jeu jeu = new Jeu(4, 4, 0);
        // coup 1 : (2,2) -> mange (2,2),(2,3),(3,2),(3,3) = index 10,11,14,15
        assertTrue("coup 1 doit être valide", jeu.joue(2, 2));
        // coup 2 : (3,0) -> mange (3,0) = index 12  (non touché par coup 1)
        assertTrue("coup 2 doit être valide", jeu.joue(3, 0));

        // Annule coup 2
        assertTrue(jeu.peutAnnuler());
        jeu.annule();
        assertEquals("Après undo coup 2 : joueur doit être 1", 1, jeu.getJoueur());
        assertTrue("(3,0) -> index 12 doit être restaurée", jeu.getGrille()[12]);

        // Annule coup 1
        assertTrue(jeu.peutAnnuler());
        jeu.annule();
        assertEquals("Après undo coup 1 : joueur doit être 0", 0, jeu.getJoueur());
        assertTrue("(2,2) -> index 10 doit être restaurée", jeu.getGrille()[10]);
        assertTrue("(3,3) -> index 15 doit être restaurée", jeu.getGrille()[15]);

        // Refait coup 1
        assertTrue(jeu.peutRefaire());
        jeu.refais();
        assertFalse("(2,2) -> index 10 doit repasser à false", jeu.getGrille()[10]);
        assertFalse("(3,3) -> index 15 doit repasser à false", jeu.getGrille()[15]);
        assertEquals("Joueur doit être 1 après redo coup 1", 1, jeu.getJoueur());

        // Refait coup 2
        assertTrue(jeu.peutRefaire());
        jeu.refais();
        assertFalse("(3,0) -> index 12 doit repasser à false", jeu.getGrille()[12]);
        assertEquals("Joueur doit être 0 après redo coup 2", 0, jeu.getJoueur());

        // Plus rien à refaire
        assertFalse("Redo sur pile vide doit échouer", jeu.peutRefaire());
    }

    @Test
    public void testAnnuleSansHistoriqueNeCassePasLEtat() {
        Jeu jeu = new Jeu(3, 4, 0);

        assertFalse("Annuler sans coup joué doit échouer", jeu.peutAnnuler());
        assertEquals("Le joueur ne doit pas changer", 0, jeu.getJoueur());
        // (0,0) -> index 0 : poison, présent
        assertTrue("La case poison doit rester présente", jeu.getGrille()[0]);
        // (2,3) -> index 11 : présente
        assertTrue("La grille ne doit pas être modifiée", jeu.getGrille()[11]);
    }

    @Test
    public void testRefaisSansFuturNeCassePasLEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(2, 2);

        assertFalse("Refaire sans futur doit échouer", jeu.peutRefaire());
        assertEquals("Le joueur ne doit pas changer", 1, jeu.getJoueur());
        // (2,2) -> index 2*4+2 = 10 : mangée
        assertFalse("La case jouée doit rester à false", jeu.getGrille()[10]);
    }
}