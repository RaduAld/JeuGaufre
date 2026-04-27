package Vue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdaptateurSauvegarder implements ActionListener {
	CollecteurEvenements control;

	AdaptateurSauvegarder(CollecteurEvenements c) {
		control = c;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		control.toucheClavier("Save");
	}
}


