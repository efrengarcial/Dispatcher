package com.wtf.commons;

import java.util.ResourceBundle;

public class Configuration {

	   private static ResourceBundle props = ResourceBundle.getBundle("configuration");
	   
	   public static final String PROTOCOL = props.getString("PROTOCOL");
	   public static final String PORT = props.getString("PORT");
	   public static final String lOCALHOST = props.getString("HOST");
	   public static final String IP = props.getString("IP");
	   public static final String FRECUENCY = props.getString("FRECUENCY");
	   
	
}
