package Vue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdaptateurRestaurer implements ActionListener {
	CollecteurEvenements control;

	AdaptateurRestaurer(CollecteurEvenements c) {
		control = c;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		control.toucheClavier("Restore");
	}
}


