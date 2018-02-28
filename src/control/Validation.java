package control;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Callback;
import view.Animations;

class Validation<N extends Node> {
	
	private Label lbl;
	private String origTxt,
					origLblStyle,
					origNStyle;
	private N n;
	private List<Predicate<N>> tests;
	private List<String> errorMsgs;
	private Consumer<N> callBack;
	
/*---------------------------------------------------------------------------*/
	
	Validation(Label lbl, N n, Predicate<N> test, String errorMsg) {
		this.lbl = lbl;
		origTxt = lbl.getText();
		origLblStyle = lbl.getStyle();
		origNStyle = n.getStyle();
		this.n = n;
		this.tests = new ArrayList<Predicate<N>>(Arrays.asList(test));
		this.errorMsgs = new ArrayList<String>(Arrays.asList(errorMsg));
	}
	
	Validation(Label lbl, N n, Predicate<N> test, String errorMsg, Consumer<N> callBack) {
		this.lbl = lbl;
		origTxt = lbl.getText();
		origLblStyle = lbl.getStyle();
		origNStyle = n.getStyle();
		this.n = n;
		this.tests = new ArrayList<Predicate<N>>(Arrays.asList(test));
		this.errorMsgs = new ArrayList<String>(Arrays.asList(errorMsg));
		this.callBack = callBack;
	}
	
	Validation(N n, Predicate<N> test) {
		this.n = n;
		this.tests = new ArrayList<Predicate<N>>(Arrays.asList(test));
	}
	
	Validation(N n, Predicate<N> test, Consumer<N> callBack) {
		this.n = n;
		this.tests = new ArrayList<Predicate<N>>(Arrays.asList(test));
		this.callBack = callBack;
	}
	
	Validation<N> add(Predicate<N> test, String errorMsg) {
		tests.add(test);
		errorMsgs.add(errorMsg);
		return this; //chaining
	}
	
/*---------------------------------------------------------------------------*/
	
	boolean run() {
		boolean pass = true;
		for (int i = 0; i < tests.size(); i++)
			if (tests.get(i).test(n)) {
				if (errorMsgs != null)
					showError(errorMsgs.get(i));
				pass = false;
			} else if (errorMsgs != null) 
				clearError();
		if (pass && callBack != null) 
			callBack.accept(n);
		return pass;
	}
	
	private void showError(String msg) {
		if (!lbl.isVisible()) {
			lbl.setVisible(true);
			lbl.setText("");
		}
		lbl.setText(msg);
		lbl.setStyle("-fx-text-fill: #ff0000; font-weight: bold; " + lbl.getStyle());
		n.setStyle("-fx-border-color: #ff0000; " + n.getStyle());
		Animations.shake(lbl);
		Toolkit.getDefaultToolkit().beep();
	}
	
	void clearError() {
		lbl.setText(origTxt);
		lbl.setStyle(origLblStyle);
		n.setStyle(origNStyle);
	}
	
/*---------------------------------------------------------------------------*/
	
	/**
	 * Adds listener to each {@code Validation}'s {@code node} that runs 
	 * its test when the node's focus is lost (i.e. focusedProperty fires).
	 * @param vs - the validations to set up
	 */
	static void setupOnFocusLost(Validation<?>... vs) {
		for (Validation<?> v: vs)
			v.n.focusedProperty().addListener((obs, lost, gained) -> {
				if (lost)
					v.run();
			});
	}
	
	/**
	 * Runs tests on all of the validations, returning true only if
	 * all tests were successful and false otherwise.
	 * 
	 * @param vs - validations to run tests
	 * @return whether or not all tests were successful
	 */
	static boolean run(Validation<?>... vs) {
		boolean pass = true;
		for (Validation<?> v: vs)
			if (!v.run())
				pass = false;
			else v.clearError();
		return pass;
	}
	
	static boolean matchValues(String value, List<String> possibleVals) {
		boolean pass = true;
		for (String p: possibleVals)
			if (!p.equals(value))
				pass = false;
		return pass;
	}
}
