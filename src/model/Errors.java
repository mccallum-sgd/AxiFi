package model;

import javax.swing.JOptionPane;

public class Errors {
	public static void showStackTraceDialog(Exception e) {
		StringBuilder sb = new StringBuilder(e.toString());
	    for (StackTraceElement ste : e.getStackTrace()) {
	        sb.append("\n\tat ");
	        sb.append(ste);
	    }
	    String trace = sb.toString();
	    JOptionPane.showMessageDialog(null, trace, "Exception occurred", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showError(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}
}
