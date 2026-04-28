package Vue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdaptateurJoueurVsIA implements ActionListener {
    CollecteurEvenements control;

    AdaptateurJoueurVsIA(CollecteurEvenements c) {
        control = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        control.toucheClavier("IA");
    }
}
