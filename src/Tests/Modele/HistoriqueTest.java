package Tests.Modele;

import Modele.Jeu;
import org.junit.Test;

import static org.junit.Assert.*;

public class HistoriqueTest {

    @Test
    public void testInitialisationJeu() {
        Jeu jeu = new Jeu(3, 4, 0);
        boolean[] grille = jeu.getGrille();

        assertEquals("Nombre de lignes incorrect",   3, jeu.getLignes());
        assertEquals("Nombre de colonnes incorrect", 4, jeu.getColonnes());
        assertEquals("Taille du vecteur doit être lignes*colonnes", 3 * 4, grille.length);
        // (0,0) -> index 0 : poison, présent
        assertTrue("La case poison (0,0) doit être présente", grille[0]);
        assertEquals("Le joueur initial doit être conservé", 0, jeu.getJoueur());
        // (2,3) -> index 2*4+3 = 11 : case normale, présente
        assertTrue("Une case normale doit être initialisée à true", grille[11]);
    }

    @Test
    public void testCoupValideMetAJourGrilleEtJoueur() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertTrue("Le coup (1,2) devrait être valide", jeu.joue(1, 2));

        boolean[] grille = jeu.getGrille();
        assertEquals("Le joueur doit changer après un coup valide", 1, jeu.getJoueur());

        // Cases hors rectangle [1..2][2..3] : non touchées
        // (0,3) -> index 3
        assertTrue("(0,3) au-dessus du coup ne doit pas changer", grille[3]);
        // (1,1) -> index 1*4+1 = 5  (à gauche du coup)
        assertTrue("(1,1) à gauche du coup ne doit pas changer", grille[5]);

        // Cases dans le rectangle : mangées (false)
        // (1,2) -> index 1*4+2 = 6
        assertFalse("La case jouée (1,2) doit passer à false", grille[6]);
        // (2,3) -> index 2*4+3 = 11
        assertFalse("La case (2,3) en bas à droite doit passer à false", grille[11]);
    }

    @Test
    public void testCoupInvalideNeModifiePasEtat() {
        Jeu jeu = new Jeu(3, 4, 0);
        assertFalse("Un coup hors grille devrait être refusé", jeu.joue(4, 1));

        boolean[] grille = jeu.getGrille();
        assertEquals("Le joueur ne doit pas changer après un coup invalide", 0, jeu.getJoueur());
        // (0,0) -> index 0 : poison, toujours présent
        assertTrue("La case poison doit rester présente", grille[0]);
        // (2,3) -> index 11 : toujours présente
        assertTrue("La grille ne doit pas être modifiée", grille[11]);
    }

    @Test
    public void testAnnuleRestaureEtatPrecedent() {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);

        assertTrue("L'annulation du dernier coup devrait réussir", jeu.peutAnnuler());
        jeu.annule();

        boolean[] grille = jeu.getGrille();
        assertEquals("Le joueur doit revenir au joueur précédent", 0, jeu.getJoueur());
        // (1,2) -> index 6 et (2,3) -> index 11 : restaurées (true)
        assertTrue("La case (1,2) jouée doit être restaurée",    grille[6]);
        assertTrue("La case (2,3) modifiée doit être restaurée", grille[11]);
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
        // (1,2) -> index 6 et (2,3) -> index 11 : de nouveau mangées (false)
        assertFalse("La case (1,2) rejouée doit revenir à false", grille[6]);
        assertFalse("La case (2,3) du coup doit revenir à false", grille[11]);
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
        // (2,2) -> index 2*4+2 = 10 : mangée par le nouveau coup
        assertFalse("La nouvelle zone jouée doit être supprimée (false)", grille[10]);
        // (1,1) -> index 1*4+1 = 5 : l'ancien coup a été annulé et non rejoué
        assertTrue("L'ancienne zone annulée ne doit pas être rejouée", grille[5]);
    }
}