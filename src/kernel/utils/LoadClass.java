package kernel.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class LoadClass {
	
	public static Object loadInstance(String propertyName){
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("config.properties"));
			@SuppressWarnings("rawtypes")
			Class c = Class.forName(properties.getProperty(propertyName));
			return c.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 	
		
	}
	
	public static Class<?> loadClass(String propertyName){
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("config.properties"));
			Class<?> c = Class.forName(properties.getProperty(propertyName));
			return c;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 	
		
	}
	
	public static Class<?> loadClassDirect(String className){
		
		try {
			Class<?> c = Class.forName(className);
			return c;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 	
		
	}
	
	public static String loadString(String propertyName){
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("config.properties"));
			return properties.getProperty(propertyName);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 	
		
	}

}
