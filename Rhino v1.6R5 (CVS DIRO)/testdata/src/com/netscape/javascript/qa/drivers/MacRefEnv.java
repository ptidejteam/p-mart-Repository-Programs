/* -*- Mode: java; tab-width: 8 -*-
 * Copyright (C) 1997, 1998 Netscape Communications Corporation,
 * All Rights Reserved.
 */

package com.netscape.javascript.qa.drivers;

import java.util.Vector;
import java.util.Date;
import java.io.*;
import java.applet.Applet;

/**
 *  Test environment for running the JavaScript language tests on the Macintosh.
 *  On the Macintosh, we have to use JBindery, a MRJ tool, to run the tests.
 *
 *  <p>
 *
 *  In order to get tests to run on the Mac, the shell must be built with
 *  MAC_TEST_HACK defined.  When MAC_TEST_HACK is defined, the shell expects
 *  to find file called "testargs.txt" in the directory where the shell
 *  executable is.  The shell expects testargs.txt to have one argument per line.
 *  Legal arguments in testargs.txt are the same as the command line arguments
 *  to the shell (although we only use the -f argument, which tells the shell
 *  to evaluate files).
 *
 *  <p>
 *
 *  The shell writes any output generated by the print statement to a file
 *  called  "results.txt".  The test environment parses the results.txt file
 *  in the same way that RefEnv parses the output stream of the JavaScript
 *  shell process.
 *
 *  <p>
 */

public class MacRefEnv implements TestEnvironment {
    TestFile    file;
    TestSuite   suite;
    RefDrv      driver;
    String      directoryName;
    TestLog     testargs;
    File        results;
    Process     task;

    /**
     *  Create a new MacRefEnv
     *
     */

    public MacRefEnv ( TestFile f, TestSuite s, RefDrv d ) {
        this.file = f;
        this.suite = s;
        this.driver = d;
    }

    /**
     *  This implementation does nothing, since creating the JS shell 
     *  automatically runs the test on the Mac.
     *
     */
    public Object executeTestFile() {
        return null;
    }

    /**
     *  Instantiate a new JavaScript shell which has been built with 
     *  MAC_TEST_HACK defined.
     */
    public Object createContext() {
        try { 
            // look for a file called flagfile.flg
            File flag = new File ( "flagfile.flg" );
            
            if ( flag.exists() ) {
                flag.delete(); 
            }
            
            task = Runtime.getRuntime().exec(driver.EXECUTABLE);
            
            // wait a maximum of five minutes
            
            int i = 0;

            while ( (! flag.exists()) && i++ <= 60 ) {
                Thread.currentThread().sleep(5000); 
            }
            
        } catch (IOException x) { 
           System.out.println("IOException in RunJS : " + x); 
        } catch (InterruptedException x) { 
            System.out.println("InterruptedException in RunJS : " + x); 
        } 
        
        return null;
    }   

    public boolean setupMacFiles() {
        
        boolean result1 = getDirectoryName();
        deleteResultsFile();
        boolean result3 = createTestargsFile();
        boolean result4 = writeTestargsFile();
        
       
        if ( result1 && result3 && result4 ) {
            return true;
        } else {
            return false; 
        }
    }
    
    public synchronized void runTest() {
        try {
            if ( setupMacFiles() ) {
                file.startTime = driver.getCurrentTime();
                createContext();
                file.endTime = driver.getCurrentTime();
            }
/*
            if (task.getExitValue() != 0) {
                if ( file.name.endsWith( "-n.js" )) {
                    file.passed = true;
                } else {                    
                    suite.passed   = false;
                    file.passed    = false;
                }                
            }
*/            
            if ( ! parseResult() ) {
                if ( file.name.endsWith( "-n.js" ) ) {
                    file.passed = true; 
                } else {                    
                    suite.passed   = false;
                    file.passed    = false;
                }                    
//                file.exception = new String(task.getError());                
            }

        } catch ( Exception e ) {
            suite.passed = false;
            file.passed  = false;
            file.exception = "Unknown process exception.";
/*            
            file.exception =  new String(task.getError())
                              + " exit value: " + task.getExitValue()
                              + "\nThrew Exception:" + e;
*/                              
        }
    }
    
    public boolean parseResult() {
        String line;
        int i, j;

        results = new File( "results.txt"); 

        if (! results.exists()) { 
            return false;
        }        

        // spit out all the lines we find in the result file 
        try {
            FileReader fr = new FileReader(results); 
            LineNumberReader lnr = new LineNumberReader(fr); 
                do {
                    line = lnr.readLine();
                    driver.p( line );
                    if (line == null) {
                        driver.p("\tERROR: No lines to read");
                        return false;
                    }
                } while (!line.equals(sizeTag));
                
                if ((line = lnr.readLine()) == null) {
                    return false;
                }
                
                file.totalCases = Integer.valueOf(line).intValue();
                
                if ((line = lnr.readLine()) == null) {
                    driver.p("\tERROR: No lines after " + sizeTag);
                    return false;
                }
                
            for ( i = 0; i < file.totalCases; i++) {
                String values[] = new String[tags.length];
                try {
                    for ( j = 0; j < tags.length; j++) {
                        values[j] = null;
                
                        if (!line.startsWith(tags[j])) {
                            driver.p("line didn't start with " + tags[j] +":"+line);
                            return false;
                        }                    
                        while (((line = lnr.readLine()) != null) && 
                            (!(line.startsWith(startTag))))
                        {
                            values[j] = (values[j] == null) ? line : (values[j] + 
                            "\n" + line);
                        }
                        if (values[j] == null) values[j] = "";
                    }
                    if ((line == null) && (i < file.totalCases - 1)) {
                        driver.p("line == null and " + i + "<" +
                            (file.totalCases - 1));
                        return false;
                    }                
                } catch ( IOException e ) {
                    driver.p( "Exception reading process output: " + e );
                    file.exception = e.toString();
                    return false;
                }                
                
                TestCase rt = new TestCase(values[0],values[1],values[4],values[2],
                    values[3],values[5]);
                
                file.bugnumber = values[6];
                file.caseVector.addElement( rt );

                if ( rt.passed.equals("false") ) {
                    if ( file.name.endsWith( "-n.js" ) ) {
                        this.file.passed = true;
                    } else {                    
                        this.file.passed  = false;
                        this.suite.passed = false;
                    }                    
                }
            }            
            try {
                lnr.close(); 
                fr.close(); 
            } catch (IOException x) { 
                System.out.println("IOException in RunJS : " + x); 
                return false;
            } 

                
        } catch (NumberFormatException nfe) {
            System.out.println("\tERROR: No integer after " + sizeTag);
            return false;
        } catch ( IOException e ) {
            System.out.println( "Exception reading process output:" + e.toString() );
            file.exception  = e.toString();
            return false;
        }                
        
        return true;
    }        

    /**
     *  Get the executable's directory, which is where we will write testargs.txt
     *  and from where we will get results.txt.
     */

    public boolean getDirectoryName() {
        directoryName = ":";
        return true;
    }

    /**
     *  Called by the driver to execute the test program.
     *
     */

    /**
     *  Check to see whether testargs.txt exists.  If so, delete it.
     */
    public boolean createTestargsFile() {     
        String testargsname = "testargs.txt";
        
        File testargsfile = new File( testargsname );
        if ( testargsfile.exists() ) {
            testargsfile.delete();
        }            
        
        testargs = new TestLog( "testargs.txt", "" );
                    
        return true;
    }

    public boolean deleteResultsFile() {     
        String resultsname = "results.txt";
        
        File resultsfile = new File( resultsname );
        if ( resultsfile.exists() ) {
            resultsfile.delete();
        }            
        return true;
    }
    
    /**
     *  Write testargs.txt to the directory where the executable is.
     */
     public boolean writeTestargsFile() {
        String helper = getMacFileString( ":"+ driver.HELPER_STRING );
        String testfile = getMacFileString( ":"+file.filePath );
        
        testargs.writeLine( "-f" );
        testargs.writeLine( helper );
        testargs.writeLine( "-f" );
        testargs.writeLine( testfile );
        testargs.closeLog();
        
        return true;
     }

     public void close() {
        return;
     }
    /**
     *  Replace slashes in a string with colons.
     *
     */
    public String getMacFileString( String path ) {
        StringBuffer buffer = new StringBuffer();
        int i;
       
        for ( i = 0; i < path.length(); i++ ) {
            if ( path.charAt(i) == '/' ) {
                buffer.append(":");
            } else {
                buffer.append( path.charAt(i) );
            }
        }
        
        return buffer.toString();
    }        


    /**
     * array of in output file to get attributes of a specific TestCase
     */
    public static final String tags[];
    /**
     * tag to specify the number of TestCases
     */
    public static final String sizeTag  = "<#TEST CASES SIZE>";
    /**
     * beginning of a tag
     */
    public static final String startTag = "<#TEST CASE";
    /**
     * end of a tag
     */
    public static final String endTag   = ">";

    /**
     * creating of String tags[]
     */
    static {
        String fields[] = { "PASSED", "NAME", "EXPECTED", "ACTUAL", "DESCRIPTION", "REASON",
                            "BUGNUMBER" };

        tags = new String[fields.length];

        for (int i = 0; i < fields.length; ++i) {
            tags[i] = startTag + " " + fields[i] + endTag;
        }
    }    
}
