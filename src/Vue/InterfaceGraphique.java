package Vue;
import Modele.Jeu;
import Patterns.Observateur;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class InterfaceGraphique implements Runnable,InterfaceUtilisateur, Observateur{
    Jeu jeu;
    Gaufre gauf;
    JLabel gameOver,joueur, choixjeu;
    JToggleButton annuler,rejouer;
    JButton sauvegarder,restaurer,nouvellePartie, Joueur_Vs_AI;
    CollecteurEvenements control;
    JFrame frame;
    JPanel gameContainer;

    InterfaceGraphique(Jeu j,CollecteurEvenements c){
        jeu = j;
        control = c;

    }

    public static void demarrer(Jeu j,CollecteurEvenements c){
        InterfaceGraphique vue = new InterfaceGraphique(j,c);
        c.ajouteInterfaceUtilisateur(vue);
        SwingUtilities.invokeLater(vue);
    }

    @Override
    public void run(){
        frame = new JFrame("Gaufre");
        frame.setSize(800, 500);
        gauf = new Gaufre(jeu);

        //creer panel de jeu
        gameContainer = new JPanel(new BorderLayout());
        gameContainer.setBorder(new EmptyBorder(50,50,50,50));
        gameContainer.add(gauf, BorderLayout.CENTER);

        //creer panel de controle
        JPanel droite = new JPanel();
        droite.setLayout(new BoxLayout(droite,BoxLayout.Y_AXIS));
        droite.setBorder(new EmptyBorder(50,50,50,50));
        //première interface pour doite
        choixjeu = createJLabel("Joueur VS : Joueur");
        Joueur_Vs_AI=createJButton("Joueur vs AI");
        droite.add(choixjeu);
        droite.add(Box.createVerticalStrut(20));
        droite.add(Joueur_Vs_AI);
        droite.add(Box.createVerticalStrut(20));
        droite.add(Box.createVerticalStrut(10));

        droite.add(Box.createGlue());

        //creer et ajouter composants d'interface
        joueur = createJLabel("joueur en cours: 1");
        annuler = createToggleButton("annuler");
        rejouer = createToggleButton("rejouer");
        sauvegarder= createJButton("sauvegarder");
        restaurer=createJButton("restaurer");
        nouvellePartie=createJButton("nouvelle partie");
     
        droite.add(Box.createGlue());
        droite.add(joueur);
        droite.add(Box.createVerticalStrut(40));
        droite.add(annuler);
        droite.add(Box.createVerticalStrut(10));
        droite.add(rejouer);
        droite.add(Box.createGlue());
        droite.add(sauvegarder);
        droite.add(Box.createVerticalStrut(10));
        droite.add(restaurer);
        droite.add(Box.createGlue());
        droite.add(nouvellePartie);
        droite.add(Box.createGlue());
      
        droite.setBackground(new Color(180, 125, 107));
        gameContainer.setBackground(new Color(41, 16, 7));

        //retransmission evenements au controleur
        gauf.addMouseListener(new AdaptateurSouris(gauf,control));
        annuler.addActionListener(new AdaptateurAnnuler( control));
        rejouer.addActionListener(new AdaptateurRejouer(control));
        sauvegarder.addActionListener(new AdaptateurSauvegarder(control));
        restaurer.addActionListener(new AdaptateurRestaurer(control));
        nouvellePartie.addActionListener(new AdaptateurNouvellePartie( control));
        Joueur_Vs_AI.addActionListener(new AdaptateurJoueurVsIA(control));

        jeu.ajouteObservateur(this);
        
        Timer timer = new Timer(30, e -> control.tictac());
        timer.start();

        //mise en place interface
        frame.add(gameContainer);
        frame.add(droite,BorderLayout.EAST);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    @Override
    public void toggleIA(boolean b){
        if(b)
            choixjeu.setText("Joueur VS : IA");
        else
            choixjeu.setText("Joueur VS : Joueur");
    }
    private JButton createJButton(String texte){
        JButton button = new JButton(texte);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusable(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.setFont(new Font("Monserrat",Font.BOLD,16));
        button.setBackground(new Color(224, 209, 197));
        button.setBorderPainted(false);
        return button;
    }
    
    private JToggleButton createToggleButton(String texte){
        JToggleButton toggleButton = new JToggleButton(texte);
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButton.setFocusable(false);
        toggleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, toggleButton.getPreferredSize().height));
        toggleButton.setFont(new Font("Monserrat",Font.BOLD,16));
        toggleButton.setBackground(new Color(224, 209, 197));
        toggleButton.setBorderPainted(false);
        return toggleButton;
    }
    
    private JLabel createJLabel(String texte){
        JLabel label = new JLabel(texte);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Monserrat",Font.BOLD,18));
        label.setForeground(new Color(41, 16, 7));
        return label;
    }

    @Override
    public void miseAJour() {
        gauf.repaint();
        int joueurEnCours = jeu.getJoueur() + 1;
        joueur.setText("joueur en cours: " + joueurEnCours);
        annuler.setEnabled(true);
        rejouer.setEnabled(true);
        sauvegarder.setEnabled(true);
        restaurer.setEnabled(true);
        nouvellePartie.setEnabled(true);

        if (gameOver != null) {
            gameOver.setVisible(false);
            gameContainer.remove(gameOver);
            gameContainer.add(gauf, BorderLayout.CENTER);
            gameContainer.revalidate();
            gameContainer.repaint();
        }
        
        if (jeu.jeuTermine()) {
           defaite(joueurEnCours);
        }
    }

    public void defaite(int joueurGagnant) {
        gameContainer.remove(gauf);

        gameOver = createJLabel("Game Over - Joueur " + joueurGagnant + " gagne !");
        gameOver.setForeground(new Color(224, 133, 57));
        gameOver.setHorizontalAlignment(SwingConstants.CENTER);

        gameContainer.add(gameOver, BorderLayout.CENTER);

        annuler.setEnabled(false);
        rejouer.setEnabled(false);
        sauvegarder.setEnabled(false);
        restaurer.setEnabled(false);
        nouvellePartie.setEnabled(true);

        gameContainer.revalidate(); 
        gameContainer.repaint();    
    }
}