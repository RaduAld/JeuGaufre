import Modele.Jeu;

void main() {
    int firstPlayer = 0;
    Jeu monJeu = new Jeu(firstPlayer);
    monJeu.afficheGrille();
    monJeu.joue(2,2);
    monJeu.joue(1,4);
    monJeu.afficheGrille();

    monJeu.annule();
    monJeu.afficheGrille();

    monJeu.annule();
    monJeu.afficheGrille();

    monJeu.refais();
    monJeu.afficheGrille();

}
