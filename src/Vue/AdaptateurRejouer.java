package Vue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdaptateurRejouer implements ActionListener {
    CollecteurEvenements control;

    AdaptateurRejouer(CollecteurEvenements c) {
        control = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        control.toucheClavier("Redo");
    }
}
