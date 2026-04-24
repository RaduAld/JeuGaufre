package Tests;

import Modele.Jeu;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class TestsHistorique {

    @Test
    public void testInitialisationJeu() throws Exception {
        Jeu jeu = new Jeu(3, 4, 0);
        int[][] grille =jeu.getGrille();

        assertEquals("Nombre de lignes incorrect", 3, grille.length);
        assertEquals("Nombre de colonnes incorrect", 4, grille[0].length);
        assertEquals("La case poison doit valoir 2", 2, grille[0][0]);
        assertEquals("Le joueur initial doit etre conserve", 0, jeu.getJoueur());
        assertEquals("Une case normale doit etre initialisee a 1", 1, grille[2][3]);
    }

    @Test
    public void testCoupValideMetAJourGrilleEtJoueur() throws Exception {
        Jeu jeu = new Jeu(3, 4, 0);

        assertTrue("Le coup (1,2) devrait etre valide", jeu.joue(1, 2));

        int[][] grille = jeu.getGrille();
        assertEquals("Le joueur doit changer apres un coup valide", 1, jeu.getJoueur());
        assertEquals("Une case au-dessus du coup ne doit pas changer", 1, grille[0][3]);
        assertEquals("Une case a gauche du coup ne doit pas changer", 1, grille[2][1]);
        assertEquals("La case jouee doit passer a 0", 0, grille[1][2]);
        assertEquals("Les cases en bas a droite doivent passer a 0", 0, grille[2][3]);
    }

    @Test
    public void testCoupInvalideNeModifiePasEtat() throws Exception {
        Jeu jeu = new Jeu(3, 4, 0);

        assertFalse("Un coup hors grille devrait etre refuse", jeu.joue(4, 1));

        int[][] grille = jeu.getGrille();
        assertEquals("Le joueur ne doit pas changer apres un coup invalide", 0, jeu.getJoueur());
        assertEquals("La case poison doit rester intacte", 2, grille[0][0]);
        assertEquals("La grille ne doit pas etre modifiee", 1, grille[2][3]);
    }

    @Test
    public void testAnnuleRestaureEtatPrecedent() throws Exception {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);

        assertTrue("L'annulation du dernier coup devrait reussir", jeu.annule());

        int[][] grille = jeu.getGrille();
        assertEquals("Le joueur doit revenir au joueur precedent", 0, jeu.getJoueur());
        assertEquals("La case jouee doit etre restauree", 1, grille[1][2]);
        assertEquals("Les cases modifiees doivent etre restaurees", 1, grille[2][3]);
        assertFalse("Un second undo immediat doit echouer sans historique", jeu.annule());
    }

    @Test
    public void testRefaisRejoueLeCoupAnnule() throws Exception {
        Jeu jeu = new Jeu(3, 4, 0);
        jeu.joue(1, 2);
        jeu.annule();

        assertTrue("Le redo devrait reussir apres un undo", jeu.refais());

        int[][] grille = jeu.getGrille();
        assertEquals("Le joueur doit rechanger apres redo", 1, jeu.getJoueur());
        assertEquals("La case rejouee doit revenir a 0", 0, grille[1][2]);
        assertEquals("Les cases du coup doivent revenir a 0", 0, grille[2][3]);
        assertFalse("Un second redo doit echouer si le futur est vide", jeu.refais());
    }

    @Test
    public void testNouveauCoupVideLeFutur() throws Exception {
        Jeu jeu = new Jeu(4, 4, 0);
        jeu.joue(1, 1);
        jeu.annule();

        assertTrue("Le nouveau coup doit etre valide", jeu.joue(2, 2));
        assertFalse("Le redo doit etre impossible apres un nouveau coup", jeu.refais());

        int[][] grille = jeu.getGrille();
        assertEquals("Le joueur doit correspondre au nouveau coup joue", 1, jeu.getJoueur());
        assertEquals("La nouvelle zone jouee doit etre supprimee", 0, grille[2][2]);
        assertEquals("L'ancienne zone annulee ne doit pas etre rejouee", 1, grille[1][1]);
    }

}
