package model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Settings {
	private static String xmlFile = "settings.xml";
	private static Document xmlDoc;
	private static final Settings thiz = new Settings();  // For instantiation of inner classes
	
	private static ArrayList<SettingCategory> settings;
	
	public enum Input {
		FIELD("textfield"),
		CHECKBOX("checkbox"),
		COMBO("combobox"),
		PATH("path");
		private String val;
		private Input(String val) {
			this.val = val;
		}
		public String val() {return val;}
		public static Input getEnumFor(String val) {
			for (Input t: Input.values())
				if (t.val.equals(val))
					return t;
			return null;
		}
	}
	
	public class SettingCategory implements Comparable<SettingCategory> {
		private String name;
		private List<Setting> settings;
		
		public SettingCategory(String name, List<Setting> settings) {
			if (settings == null)
				throw new NullPointerException("settings cannot be null");
			this.name = name;
			Collections.sort(settings);
			this.settings = settings;
		}

		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		public List<Setting> getSettings() {return settings;}
		public void setSettings(List<Setting> settings) {this.settings = settings;}
		
		public Setting getSetting(String name) {
			try {
				return settings.get(Collections.binarySearch(settings, new Setting(name, "", null, null)));
			} catch (ArrayIndexOutOfBoundsException e) {
				settings.forEach(s -> System.out.println(s.name));
				throw new NoSuchElementException("No such setting in cateogry " + this.name + ": " + name);
			}
		}

		@Override
		public int compareTo(SettingCategory o) {
			return this.name.compareTo(o.getName());
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			boolean first = true;
			for (Setting s: settings)
				if (first) {
					first = false;
					sb.append(s);
				} else {
					sb.append(s);
					sb.append("\n");
				}
			sb.append("]");
			return sb.toString();
		}
	}
	
	public class Setting implements Comparable<Setting> {
		private String name;
		private String value;
		private List<String> possibleVals;
		private Input type;
		
		
		Setting(String name, String value, List<String> possibleVals, Input type) {
			this.name = name;
			this.value = value;
			this.possibleVals = possibleVals;
			this.type = type;
		}
	
		public void setName(String name) {this.name = name;}
		public String getName() {return name;}
		
		public void setStringValue(String value) {
			replaceXmlValue(this, value);
			this.value = value;
		}
		public void setBooleanValue(Boolean value) {
			replaceXmlValue(this, String.valueOf(value));
			this.value = String.valueOf(value);
		}
		public String getStringValue() {return value;}
		public Boolean getBooleanValue() {return new Boolean(value);}
		public List<String> getPossibleVals() {return possibleVals;}
		public String getPossibleValsString() {
			StringJoiner join = new StringJoiner(", ");
			possibleVals.forEach(p -> join.add(p));
			return join.toString();
		}
		public Input getType() {return type;}
		
		public String toString() {
			return name + ", " + value + ", " + possibleVals + ", " + type.val();
		}

		@Override
		public int compareTo(Setting o) {
			return this.name.compareTo(o.getName());
		}
	}
	
	public static void init(String fileName) {
		Settings.xmlFile = fileName;
		init();
	}
	
	public static void init() {
		settings = new ArrayList<SettingCategory>();
		File file = new File(xmlFile);
		if (!file.exists())
			JOptionPane.showMessageDialog(null, "ERROR: settings file is missing.");
		else ;//file.setReadOnly();;
		createDOM();
		parseXml();
	}
	
	public static Setting getSetting(String category, String name) {
		int cat = 0;
		try {
			cat = Collections.binarySearch(settings, thiz.new SettingCategory(category, new ArrayList<Setting>()));
			return settings.get(cat).getSetting(name);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new NoSuchElementException("No such setting category: " + category);
		}
	}
	
	public static List<SettingCategory> getSettingCategories() { return settings; }
	
	private static void createDOM() {
		try {
			File file = new File(xmlFile);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			xmlDoc = docBuilder.parse(trimXml(file));
			xmlDoc.getDocumentElement().normalize();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			Errors.showStackTraceDialog(e);
			e.printStackTrace();
		}
	}
	
	private static void parseXml() {
		try {
			//TODO - create schema
			NodeList categoryNodes = xmlDoc.getDocumentElement().getElementsByTagName("category");
			for (int i = 0; i < categoryNodes.getLength(); i++) {
				String name = getNodeAttrValue(categoryNodes.item(i), "name");
				List<Setting> tmp = new ArrayList<Setting>();
				NodeList settingNodes = categoryNodes.item(i).getChildNodes();
				for (int j = 0; j < settingNodes.getLength(); j++) {
					String settingName = getNodeAttrValue(settingNodes.item(j), "name");
					Node valueE = settingNodes.item(j).getFirstChild();
					Input type = Input.getEnumFor(getNodeAttrValue(valueE, "input"));
					List<String> possibleVals = new ArrayList<String>(Arrays.asList(getNodeAttrValue(valueE, "possible").split(" ")));
					String value = valueE.getTextContent();
					tmp.add(thiz.new Setting(settingName, value, possibleVals, type));
				}
				settings.add(thiz.new SettingCategory(name, tmp));
			}
			Collections.sort(settings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getNodeAttrValue(Node n, String name) {
		if (n.getNodeType() == Node.ELEMENT_NODE)
			return ((Element)n).getAttribute(name);
		else return n.getAttributes().getNamedItem(name).getNodeValue();
	}
	
	private static void replaceXmlValue(Setting setting, String newValue) {
		try {
			NodeList settingsNodes = xmlDoc.getElementsByTagName("setting");
			for (int i = 0; i < settingsNodes.getLength(); i++) {
				if (((Element)settingsNodes.item(i)).getAttribute("name").equals(setting.getName())) {
					settingsNodes.item(i).getFirstChild().setTextContent(newValue);
				}
			}
	    } catch (Exception e) {
	    	Errors.showStackTraceDialog(e);
	        e.printStackTrace();
	    }
	}
	
	public static InputStream trimXml(File input) {
	    try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
	    	StringBuffer result = new StringBuffer();
	        String line;
	        while ( (line = reader.readLine() ) != null)
	            result.append(line.trim());
	        return new ByteArrayInputStream(result.toString().getBytes(StandardCharsets.UTF_8));
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public static void printDocument(Document doc) {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		    transformer.transform(new DOMSource(doc), 
		         new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
		} catch (UnsupportedEncodingException | TransformerException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/*public static void main(String[] args) {
		try {
			// Test data
			String root = "settings",
					setting = "setting",
					name = "name",
					value = "value",
					possible = "possible",
					type = "type",
					
					s1 = "test1",
					s1PVal = "none hidden all",
					s1ValType = "String",
					s1Val = "hidden",
					
					s2 = "test2",
					s2PVal = "true false",
					s2ValType = "boolean",
					s2Val = "true",
					
					s3 = "test3",
					s3PVal = "any",
					s3ValType = "String",
					s3Val = "Sean";
			
			//Create xml
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(root);
			doc.appendChild(rootElement);
			
			Element setting1 = doc.createElement(setting);
			setting1.setAttribute(name, s1);
			Element value1 = doc.createElement(value);
			value1.setAttribute(possible, s1PVal);
			value1.setAttribute(type, s1ValType);
			value1.setTextContent(s1Val);
			setting1.appendChild(value1);
			rootElement.appendChild(setting1);
			
			Element setting2 = doc.createElement(setting);
			setting2.setAttribute(name, s2);
			Element value2 = doc.createElement(value);
			value2.setAttribute(possible, s2PVal);
			value2.setAttribute(type, s2ValType);
			value2.setTextContent(s2Val);
			setting2.appendChild(value2);
			rootElement.appendChild(setting2);
			
			Element setting3 = doc.createElement(setting);
			setting3.setAttribute(name, s3);
			Element value3 = doc.createElement(value);
			value3.setAttribute(possible, s3PVal);
			value3.setAttribute(type, s3ValType);
			value3.setTextContent(s3Val);
			setting3.appendChild(value3);
			rootElement.appendChild(setting3);
			
			// Write xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File f = new File("tmp.xml");
			StreamResult result = new StreamResult(new File("tmp.xml"));
			
			transformer.transform(source, result);
			
			printDocument(doc);
			
			Settings.init("tmp.xml");
			
			// TEST 1
			Setting test1 = Settings.getSetting("test1");
			String test1Name = test1.getName(),
					test1Val = test1.getStringValue();
			assert test1Name.equals("test1") && test1Val.equals("123"): "test 1 fail: " + test1.getName() + "=" + test1.getStringValue();
			
			// TEST 2
			boolean test2 = Settings.getSetting("test2").getBooleanValue();
			assert test2 == true: "test 2 fail: " + test2;
			
			// TEST 2
			Setting test3 = Settings.getSetting("test3");
			Settings.replaceXmlValue(test3, "54321");
			String test3After = Settings.getSetting("test3").getStringValue();
			assert test3After.equals("54321"): "test 3 fail: Expected: 54321, Output: " + test3After;  
			
			f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
}
