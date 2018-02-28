package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Logger {

	public static void init() {
		try {
			File f = new File(Settings.getSetting("logs", "name").getStringValue());
			if (!f.exists()) f.createNewFile();
			CopyPrintStream cpStream = new CopyPrintStream(new FileOutputStream(f, false), System.out);
			System.setOut(cpStream);
			System.setErr(cpStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
