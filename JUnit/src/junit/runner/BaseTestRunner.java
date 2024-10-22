package junit.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestSuite;

/**
 * Base class for all test runners.
 * This class was born live on stage in Sardinia during XP2000.
 */
public abstract class BaseTestRunner implements TestListener {
	public static final String SUITE_METHODNAME= "suite";
	
	static Properties fPreferences;
	static int fgMaxMessageLength= 500;
	static boolean fgFilterStack= true;
	boolean fLoading= true;
	
	/**
	 * Returns the Test corresponding to the given suite. This is
	 * a template method, subclasses override runFailed(), clearStatus().
	 */
	public Test getTest(String suiteClassName) {
		if (suiteClassName.length() <= 0) {
			clearStatus();
			return null;
		}
		Class testClass= null;
		try {
			testClass= loadSuiteClass(suiteClassName);
		} catch (ClassNotFoundException e) {
			String clazz= e.getMessage();
			if (clazz == null) 
				clazz= suiteClassName;
			runFailed("Class not found \""+clazz+"\"");
			return null;
		} catch(Exception e) {
			runFailed("Error: "+e.toString());
			return null;
		}
		Method suiteMethod= null;
		try {
			suiteMethod= testClass.getMethod(SUITE_METHODNAME, new Class[0]);
	 	} catch(Exception e) {
	 		// try to extract a test suite automatically
			clearStatus();			
			return new TestSuite(testClass);
		}
		Test test= null;
		try {
			test= (Test)suiteMethod.invoke(null, new Class[0]); // static method
			if (test == null)
				return test;
		} 
		catch (InvocationTargetException e) {
			runFailed("Failed to invoke suite():" + e.getTargetException().toString());
			return null;
		}
		catch (IllegalAccessException e) {
			runFailed("Failed to invoke suite():" + e.toString());
			return null;
		}
		
		clearStatus();
		return test;
	}
	
	/**
	 * Returns the formatted string of the elapsed time.
	 */
	public String elapsedTimeAsString(long runTime) {
		return NumberFormat.getInstance().format((double)runTime/1000);
	}
	
	/**
	 * Processes the command line arguments and
	 * returns the name of the suite class to run or null
	 */
	protected String processArguments(String[] args) {
		String suiteName= null;
		for (int i= 0; i < args.length; i++) {
			if (args[i].equals("-noloading")) {
				setLoading(false);
			} else if (args[i].equals("-nofilterstack")) {
				fgFilterStack= false;
			} else if (args[i].equals("-c")) {
				if (args.length > i+1)
					suiteName= extractClassName(args[i+1]);
				else
					System.out.println("Missing Test class name");
				i++;
			} else {
				suiteName= args[i];
			}
		}
		return suiteName;		
	}

	/**
	 * Sets the loading behaviour of the test runner
	 */
	public void setLoading(boolean enable) {
		this.fLoading= enable;
	}
	/**
	 * Extract the class name from a String in VA/Java style
	 */
	public String extractClassName(String className) {
		if(className.startsWith("Default package for")) 
			return className.substring(className.lastIndexOf(".")+1);
		return className;
	}
	
	/**
	 * Truncates a String to the maximum length.
	 */
	public static String truncate(String s) {
		if (fgMaxMessageLength != -1 && s.length() > fgMaxMessageLength)
			s= s.substring(0, fgMaxMessageLength)+"...";
		return s;
	}
	
	/**
	 * Override to define how to handle a failed loading of
	 * a test suite.
	 */
	protected abstract void runFailed(String message);
	
	/**
	 * Returns the loaded Class for a suite name. 
	 */
	protected Class loadSuiteClass(String suiteClassName) throws ClassNotFoundException {
		return getLoader().load(suiteClassName);
	}
	
	/**
	 * Clears the status message.
	 */
	protected void clearStatus() { // Belongs in the GUI TestRunner class
	}
	
	/**
	 * Returns the loader to be used.
	 */
	public TestSuiteLoader getLoader() {
		if (useReloadingTestSuiteLoader())
			return new ReloadingTestSuiteLoader();
		return new StandardTestSuiteLoader();
	}
	
	protected boolean useReloadingTestSuiteLoader() {
		return getPreference("loading").equals("true") && !inVAJava() && this.fLoading;
	}
	
	private static File getPreferencesFile() {
	 	String home= System.getProperty("user.home");
 		return new File(home, "junit.properties");
 	}
 	
 	private static void readPreferences() {
 		InputStream is= null;
 		try {
 			is= new FileInputStream(getPreferencesFile());
 			fPreferences= new Properties(fPreferences);
			fPreferences.load(is);
		} catch (IOException e) {
			try {
				if (is != null)
					is.close();
			} catch (IOException e1) {
			}
		}
 	}
 	
 	public static String getPreference(String key) {
 		return fPreferences.getProperty(key);
 	}
 	
 	public static int getPreference(String key, int dflt) {
 		String value= getPreference(key);
 		int intValue= dflt;
 		if (value == null)
 			return intValue;
 		try {
 			intValue= Integer.parseInt(value);
 	 	} catch (NumberFormatException ne) {
 		}
 		return intValue;
 	}

 	public static boolean inVAJava() {
		try {
			Class.forName("com.ibm.uvm.tools.DebugSupport");
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}


	/**
	 * Returns a filtered stack trace
	 */
	public static String getFilteredTrace(Throwable t) { 
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer= stringWriter.getBuffer();
		String trace= buffer.toString();
		return BaseTestRunner.filterStack(trace);
	}

	/**
	 * Filters stack frames from internal JUnit classes
	 */
	public static String filterStack(String stack) {
		if (!getPreference("filterstack").equals("true") || fgFilterStack == false)
			return stack;
			
		StringWriter sw= new StringWriter();
		PrintWriter pw= new PrintWriter(sw);
		StringReader sr= new StringReader(stack);
		BufferedReader br= new BufferedReader(sr);
		
		String line;
		try {	
			while ((line= br.readLine()) != null) {
				if (!filterLine(line))
					pw.println(line);
			}
		} catch (Exception IOException) {
			return stack; // return the stack unfiltered
		}
		return sw.toString();
	}
	
	static boolean filterLine(String line) {
		String[] patterns= new String[] {
			"junit.framework.TestCase",
			"junit.framework.TestResult",
			"junit.framework.TestSuite",
			"junit.framework.Assert.", // don't filter AssertionFailure
			"junit.swingui.TestRunner",
			"junit.awtui.TestRunner",
			"junit.textui.TestRunner",
			"java.lang.reflect.Method.invoke("
		};
		for (int i= 0; i < patterns.length; i++) {
			if (line.indexOf(patterns[i]) > 0)
				return true;
		}
		return false;
	}

 	{
 		fPreferences= new Properties();
 		//JDK 1.2 feature
 		//fPreferences.setProperty("loading", "true");
 		fPreferences.put("loading", "true");
 		fPreferences.put("filterstack", "true");
  		readPreferences();
 		fgMaxMessageLength= getPreference("maxmessage", fgMaxMessageLength);
 	}
 	
}