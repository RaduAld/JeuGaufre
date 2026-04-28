package Modele;

public class MainIA {
    public static void main(String[] args) {
        System.out.println("execution de deux ia");
        Jeu jeu = new Jeu(0);
        IA ia1 = IA.nouvelle(jeu);
        IA ia2 = IA.nouvelle(jeu);
        IA[] ias = new IA[2];
        ias[0] = ia1;
        ias[1] = ia2;
        jeu.afficheGrille();
        int nbTours = 0;
        while (!jeu.jeuTermine() && nbTours < 50) {
            int joueurCourant = jeu.getJoueur();
            System.out.println("tour du joueur : " + joueurCourant);
            Coup coupIA = ias[joueurCourant].elaboreCoup();
            if (coupIA != null) {
                System.out.println("coup joué : " + coupIA.getL() + " " + coupIA.getC());
                jeu.joue(coupIA.getL(), coupIA.getC());

            } else {// ??? aucun coup généré
                System.out.println("aucun coup genere ??");
                break;
            }
            jeu.afficheGrille();
            nbTours++;

        }
        if (jeu.jeuTermine()) {
            System.out.println("le vainquer est : " + jeu.getJoueur());
        }
    }
}
