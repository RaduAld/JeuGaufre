package Vue;
import Modele.Jeu;
import java.awt.Component;
import javax.swing.*;
public class InterfaceGraphique implements Runnable{
    Jeu jeu;
    Gaufre gauf;
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
        frame.setSize(500, 500);  
        gauf = new Gaufre(jeu);
        frame.add(gauf);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private JButton createJButton(String texte){
        JButton button = new JButton(texte);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusable(false);
        return button;
    }
    private JToggleButton createToggleButton(String texte){
        JToggleButton toggleButton = new JToggleButton(texte);
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButton.setFocusable(false);
        return toggleButton;
    }
}