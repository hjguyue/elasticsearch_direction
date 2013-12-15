package setup;

import java.io.*;
import java.util.Properties;

public class Property {

	// main properties:
	public static String masterIP = "";
	public static String indexName = "";
	public static String disBound = "";
	
	// inputStream for properties:
	public static InputStream inputStream;
	
	public static void load(){
		try {
			inputStream = new FileInputStream("data/desk.properties");
			Properties properties = new Properties();
			properties.load(inputStream);
			masterIP = properties.getProperty("master_ip");
			indexName = properties.getProperty("index_name");
			disBound = properties.getProperty("distanceBound");
					
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String argv[]){
		load();
		System.out.println(masterIP);
	}
	
}
