package Vue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class AdaptateurSouris extends MouseAdapter{
    Gaufre gauf;
    CollecteurEvenements control;
    public AdaptateurSouris(Gaufre g,CollecteurEvenements c){
            gauf = g;
            control = c;
    }
    @Override
    public void mousePressed(MouseEvent e){
        int l = e.getY() / gauf.longueurCase();
        int c = e.getX() / gauf.longueurCase();
        gauf.repaint();
        control.clicSouris(l,c);
    }
}
