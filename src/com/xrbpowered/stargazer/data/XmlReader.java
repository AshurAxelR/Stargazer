package com.xrbpowered.stargazer.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlReader {

	public static Element load(String uri) {
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream(uri);
			if(in==null)
				in = new FileInputStream(new File(uri));
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			in.close();
			return doc.getDocumentElement();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Element element(Element e, String name) {
		if(e==null)
			return null;
		NodeList ns = e.getElementsByTagName(name);
		if(ns.getLength()==0)
			return null;
		else
			return (Element)ns.item(0);
	}
	
	public static LinkedList<Element> elements(Element e, String name) {
		LinkedList<Element> list = new LinkedList<>();
		if(e==null)
			return list;
		NodeList ns = e.getElementsByTagName(name);
		for(int i=0; i<ns.getLength(); i++) {
			list.add((Element)ns.item(i));
		}
		return list;
	}
	
	public static void parseOptions(Element e, OptionParser parser) {
		for(Element oe : elements(e, "opt")) {
			String key = attr(oe, "key", null);
			String value = attr(oe, "value", null);
			if(key!=null && value!=null) {
				parser.setOption(key, value);
			}
		}
	}
	
	public static String attr(Element e, String name, String def) {
		if(e.hasAttribute(name))
			return e.getAttribute(name);
		else
			return def;
	}

	public static boolean toBool(String s, boolean def) {
		if(s==null)
			return def;
		if(s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("true") || s.equals("1"))
			return true;
		if(s.equalsIgnoreCase("no") || s.equalsIgnoreCase("false") || s.equals("0"))
			return false;
		return def;
	}
	
	public static boolean attrBool(Element e, String name, boolean def) {
		return toBool(attr(e, name, null), def);
	}

	public static int toInt(String s, int def) {
		try {
			return Integer.parseInt(s);
		}
		catch(Exception ex) {
			return def;
		}
	}
	
	public static int attrInt(Element e, String name, int def) {
		return toInt(attr(e, name, null), def);
	}
	
	public static long toLong(String s, long def) {
		try {
			return Long.parseLong(s);
		}
		catch(Exception ex) {
			return def;
		}
	}
	
	public static long attrLong(Element e, String name, long def) {
		return toLong(attr(e, name, null), def);
	}

	public static float toFloat(String s, float def) {
		try {
			return Float.parseFloat(s);
		}
		catch(Exception ex) {
			return def;
		}
	}
	
	public static float attrFloat(Element e, String name, float def) {
		return toFloat(attr(e, name, null), def);
	}

	public static double toDouble(String s, double def) {
		try {
			return Double.parseDouble(s);
		}
		catch(Exception ex) {
			return def;
		}
	}
	
	public static double attrDouble(Element e, String name, double def) {
		return toDouble(attr(e, name, null), def);
	}

}
