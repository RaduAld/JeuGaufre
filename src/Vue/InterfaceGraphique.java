package Vue;
import Modele.Jeu;
import java.awt.*;
import javax.swing.*;
public class InterfaceGraphique implements Runnable{
    Jeu jeu;
    Gaufre gauf;
    JLabel gameOver,joueur;
    JToggleButton annuler,rejouer;
    JButton sauvegarder,restaurer,nouvellePartie;
    //CollecteurEvenements control;
    JFrame frame;
    InterfaceGraphique(Jeu j){
        jeu = j;
        //control = c;
    }
    public static void demarrer(Jeu j){
        InterfaceGraphique vue = new InterfaceGraphique(j);
        //control.ajouteInterfaceUtilisateur(vue);
        SwingUtilities.invokeLater(vue);
    }

    @Override
    public void run(){
        frame = new JFrame("Gauffre");
        frame.setSize(800, 400); 
        Box droite = createVerticalBox();
        JPanel gameContainer = new JPanel(new BorderLayout());
        gauf = new Gaufre(jeu);
        gameContainer.add(gauf, BorderLayout.CENTER);
        joueur = createJLabel("joueur en cours: 1");
        annuler = createToggleButton("annuler");
        rejouer = createToggleButton("rejouer");
        sauvegarder= createJButton("sauvegarder");
        restaurer=createJButton("restaurer");
        nouvellePartie=createJButton("nouvelle partie");
        
        droite.add(joueur);
        droite.add(annuler);
        droite.add(rejouer);
        droite.add(sauvegarder);
        droite.add(restaurer);
        droite.add(nouvellePartie);
        frame.add(droite,BorderLayout.EAST);
        frame.add(gameContainer);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private JButton createJButton(String texte){
        JButton button = new JButton(texte);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusable(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        return button;
    }
    private JToggleButton createToggleButton(String texte){
        JToggleButton toggleButton = new JToggleButton(texte);
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButton.setFocusable(false);
        toggleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, toggleButton.getPreferredSize().height));
        return toggleButton;
    }
    private JLabel createJLabel(String texte){
        JLabel label = new JLabel(texte);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    private Box createHorizontalBox(){
        Box horiz = Box.createHorizontalBox();
        horiz.setAlignmentX(Component.CENTER_ALIGNMENT);
        return horiz;
    }
    private Box createVerticalBox(){
        Box vertic = Box.createVerticalBox();
        vertic.setAlignmentX(Component.CENTER_ALIGNMENT);
        return vertic;
    }
}