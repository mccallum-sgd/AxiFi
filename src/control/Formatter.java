package control;

import javafx.scene.control.Label;

public class Formatter {

	public static String capitalize(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
	
	public static void capitalize(Label lbl) {
		lbl.setText(capitalize(lbl.getText()));
	}
}
