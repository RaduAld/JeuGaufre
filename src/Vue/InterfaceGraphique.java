package Vue;
import Modele.Jeu;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
        //Box droite = createVerticalBox();
        JPanel droite = new JPanel();
        droite.setLayout(new BoxLayout(droite,BoxLayout.Y_AXIS));
        droite.setBorder(new EmptyBorder(50,50,50,50));
        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.setBorder(new EmptyBorder(50,50,50,50));
        gauf = new Gaufre(jeu);
        gameContainer.add(gauf, BorderLayout.CENTER);
        joueur = createJLabel("joueur en cours: 1");
        annuler = createToggleButton("annuler");
        rejouer = createToggleButton("rejouer");
        sauvegarder= createJButton("sauvegarder");
        restaurer=createJButton("restaurer");
        nouvellePartie=createJButton("nouvelle partie");
        droite.add(Box.createGlue());
        droite.add(joueur,BorderLayout.CENTER);
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
        frame.add(gameContainer);
        frame.add(droite,BorderLayout.EAST);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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