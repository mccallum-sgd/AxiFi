package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

public class Settings {
	private static String fileName = "settings.conf";
	private static final String delimiter = "=";
	private static final Settings thiz = new Settings();
	
	private static ArrayList<Setting> settings;
	
	class Setting {
		private String name;
		private String value;
		
		
		Setting(String name, String value) {
			this.name = name;
			this.value = value;
		}
	
		void setName(String name) {this.name = name;}
		String getName() {return name;}
		
		void setStringValue(String value) {replaceSettingValue(this, value);}
		void setBooleanValue(Boolean value) {replaceSettingValue(this, String.valueOf(value));}
		private void setValue(String value) {this.value = value;}
		String getStringValue() {return value;}
		Boolean getBooleanValue() {return new Boolean(value);}
		String toEntry() {return name+delimiter+value;}
	}
	
	public static Setting getSetting(String name) {
		return settings.get(Collections.binarySearch(settings, thiz.new Setting(name, ""), new Comparator<Setting>() {
			@Override
			public int compare(Setting o1, Setting o2) {
				return o1.getName().compareTo(o2.getName());
			}
		}));
	}
	
	public static void init(String fileName) {
		Settings.fileName = fileName;
		init();
	}
	
	public static void init() {
		settings = new ArrayList<Setting>();
		File file = new File("settings.conf");
		if (!file.exists()) {
			JOptionPane.showMessageDialog(null, "ERROR: settings file is missing.");
			// file.setReadOnly();
		}
		else sortFile();
		readFile();
	}
	
	private static void replaceSettingValue(Setting setting, String newValue) {
		try {
	        // Read file
			File file = new File(fileName);
	        StringBuffer inputBuffer = new StringBuffer();
			Scanner in = new Scanner(file);
			while (in.hasNextLine()) {
				inputBuffer.append(in.nextLine());
				inputBuffer.append("\n");
			}
	        String inputStr = inputBuffer.toString();
	        in.close();
	        // Replace
	        String oldEntry = setting.toEntry();
	        setting.setValue(newValue);
	        inputStr.replace(oldEntry, setting.toEntry());

	        // Write
	        FileOutputStream fileOut = new FileOutputStream(file);
	        fileOut.write(inputStr.getBytes());
	        fileOut.close();
	    } catch (Exception e) {
	    	Errors.showStackTraceDialog(e);
	        e.printStackTrace();
	    }
	}
	
	private static void readFile() {
		Scanner in;
		
		try {
			File file = new File(fileName);
			in = new Scanner(file);
			while (in.hasNext()) {
				Scanner lineIn = new Scanner(in.nextLine()).useDelimiter(delimiter); 
				String name = lineIn.next();
				String value = lineIn.next();
				settings.add(thiz.new Setting(name, value));
				lineIn.close();
			}
			in.close();
		} catch (FileNotFoundException e) {
			Errors.showStackTraceDialog(e);
			e.printStackTrace();
		}
		
		
	}
	
	
	private static void sortFile() {
		try {
			File file = new File(fileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String inputLine;
			List<String> lineList = new ArrayList<String>();
			while ((inputLine = bufferedReader.readLine()) != null) {
					lineList.add(inputLine);
			}
			fileReader.close();
	
			Collections.sort(lineList);
	
			FileWriter fileWriter = new FileWriter(file);
			PrintWriter out = new PrintWriter(fileWriter);
			for (String outputLine : lineList) {
				out.println(outputLine);
			}
			out.flush();
			out.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			String s1 = "test1123",
					s2 = "test2=true",
					s3 = "test3=12345";
			File f = new File("test.conf");
			f.createNewFile();
			FileWriter os = new FileWriter(f);
			os.write("test1=123");
			os.write("\n");
			os.write("test2=true");
			os.write("\n");
			os.write("test3=12345");
			os.close();
			
			Settings.init("test.conf");
			
			String result = "";
			try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			      result = reader.lines().collect(Collectors.joining());
			}
			assert result.equals(s1 + "\n" + s2 + "\n" + s3): "File write error: Expected:\n" + s1 + "\n" + s2 + "\n" + s3 + "Output:\n" + result;
			
			// TEST 1
			Setting test1 = Settings.getSetting("test1");
			String test1Name = test1.getName(),
					test1Val = test1.getStringValue();
			assert test1Name.equals("test1") && test1Val.equals("123"): "test 1 fail: " + test1.toEntry();
			
			// TEST 2
			boolean test2 = Settings.getSetting("test2").getBooleanValue();
			assert test2 == true: "test 2 fail: " + test2;
			
			// TEST 2
			Setting test3 = Settings.getSetting("test3");
			Settings.replaceSettingValue(test3, "54321");
			String test3After = Settings.getSetting("test3").getStringValue();
			assert test3After.equals("54321"): "test 3 fail: Expected: 54321, Output: " + test3After;  
			
			f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
