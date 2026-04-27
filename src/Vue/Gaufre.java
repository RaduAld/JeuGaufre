package Vue;
import Modele.Jeu;
import javax.swing.*;
import java.awt.*;

public class Gaufre extends JComponent{
    Jeu jeu;
    public Gaufre(Jeu j){
        jeu = j;
        this.setMinimumSize(new Dimension(100, 100));
    }
    public void dessineCarre(Graphics2D g,int i,int j,int width,int height){
        Color brown = new Color(224, 133, 57);
        g.setColor(brown);
        g.fillRect(i, j,width,height);
        Color darkBrown = new Color(166, 67, 33);
        g.setColor(darkBrown);
        g.fillRect(i+width/4,j + height/4,width/2,height/2);
        g.drawRect(i, j,width,height);

    }
    public void dessinePoison(Graphics2D g,int width,int height) {
        Color green = new Color(13, 98, 47);
        g.setColor(green);
        g.fillOval(width/3, height/3,width/3,height/3);
    }
    @Override
    public void paintComponent(Graphics g){
        Graphics2D drawable = (Graphics2D) g;
        super.paintComponent(g); //effacer tout
        int l = jeu.getLignes();
        int c = jeu.getColonnes();
        int width = getWidth()/c;
        int height = getHeight()/l;
        //pour avoir des carres
        width = Math.min(width,height);
        height = width;
        //dessiner la gaufre
        for(int i = 0;i<l;i++){
            for(int j=0;j<c;j++){
                if(jeu.estPresente(i, j)){
                    int x = j * width;
                    int y = i * height;
                    dessineCarre(drawable,x,y,width,height);
                }
            }
        }
        dessinePoison(drawable,width,height);
    }
}