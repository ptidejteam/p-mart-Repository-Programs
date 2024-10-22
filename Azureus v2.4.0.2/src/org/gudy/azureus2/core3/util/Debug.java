/*
 * Created on Nov 19, 2003
 * Created by Alon Rohter
 *
 * Copyright (C) 2003, 2004, 2005, 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package org.gudy.azureus2.core3.util;

import java.io.*;
import java.util.*;

/**
 * Debug-assisting class.
 */
public class Debug {
  
	private static AEDiagnosticsLogger	diag_logger	= AEDiagnostics.getLogger( "debug" );

  
  /**
   * Prints out the given debug message to System.out,
   * prefixed by the calling class name, method and
   * line number.
   */
  public static void out(final String _debug_message) {
    out( _debug_message, null );
  }
  
  /**
   * Prints out the given exception stacktrace to System.out,
   * prefixed by the calling class name, method and
   * line number.
   */
  public static void out(final Throwable _exception) {
    out( "", _exception );
  }
  
  public static void
  outNoStack(
  	String		str )
  {
  	outNoStack( str, false );
  }
  
  public static void
  outNoStack(
  	String		str,
	boolean		stderr)
  {
    diagLoggerLogAndOut("DEBUG::"+ new Date(SystemTime.getCurrentTime()).toString(), stderr );
    
    diagLoggerLogAndOut("  " + str, stderr );
  }
  
  /**
   * Prints out the given debug message to System.out,
   * prefixed by the calling class name, method and
   * line number, appending the stacktrace of the given exception.
   */
  public static void out(final String _debug_msg, final Throwable _exception) {
    String header = "DEBUG::";
    header = header + new Date(SystemTime.getCurrentTime()).toString() + "::";
    String className;
    String methodName;
    int lineNumber;
    String	trace_trace_tail = null;
    
    try {
      throw new Exception();
    }
    catch (Exception e) {
    	StackTraceElement[]	st = e.getStackTrace();
    	
      StackTraceElement first_line = st[2];
      className = first_line.getClassName() + "::";
      methodName = first_line.getMethodName() + "::";
      lineNumber = first_line.getLineNumber();
      
    	trace_trace_tail = getCompressedStackTrace(e, 3, 200);
    }
    
    diagLoggerLogAndOut(header+className+(methodName)+lineNumber+":", true);
    if (_debug_msg.length() > 0) {
    	diagLoggerLogAndOut("  " + _debug_msg, true);
    }
    if ( trace_trace_tail != null ){
    	diagLoggerLogAndOut( "    " + trace_trace_tail, true);
    }
    if (_exception != null) {
    	diagLoggerLogAndOut(_exception);
    }
  }
  
  public static String getLastCaller() {
  	return getLastCaller(0);
  }

  public static String getLastCaller(int numToGoBackFurther) {
    try {
      throw new Exception();
    }
    catch (Exception e) {
      // [0] = our throw
      // [1] = the line that called getLastCaller
      // [2] = the line that called the function that has getLastCaller
      StackTraceElement st[] = e.getStackTrace();
      if (st == null || st.length == 0)
      	return "??";
      if (st.length > 3 + numToGoBackFurther)
        return st[3 + numToGoBackFurther].toString();

      return st[st.length - 1].toString();
    }
  }

  public static void outStackTrace() {
    // skip the last, since they'll most likely be main
	  diagLoggerLogAndOut(getStackTrace(1),false);
  }

  private static String getStackTrace(int endNumToSkip) {
		String sStackTrace = "";
    try {
      throw new Exception();
    }
    catch (Exception e) {
      StackTraceElement st[] = e.getStackTrace();
      for (int i = 1; i < st.length - endNumToSkip; i++) {
        if (!st[i].getMethodName().endsWith("StackTrace"))
        	sStackTrace += st[i].toString() + "\n";
      }
      if (e.getCause() != null)
      	sStackTrace += "\tCaused By: " + getStackTrace(e.getCause()) + "\n";
    }
    return sStackTrace;
  }

	public static void
	killAWTThreads()
	{
		ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
			
		killAWTThreads( threadGroup );
	}

	private static String getCompressedStackTrace(Throwable t,
			int frames_to_skip) {
		return getCompressedStackTrace(t, frames_to_skip, 200);
	}

	private static String getCompressedStackTrace(Throwable t,
			int frames_to_skip, int iMaxLines) {
		String sStackTrace = "";
  	StackTraceElement[]	st = t.getStackTrace();
	   
  	int iMax = Math.min(st.length, iMaxLines + frames_to_skip);
    for (int i = frames_to_skip; i < iMax; i++) {
    	
    	if (i > frames_to_skip)
    		sStackTrace += ",";
    	
    	String cn = st[i].getClassName();
    	cn = cn.substring( cn.lastIndexOf(".")+1);
    	
    	sStackTrace += cn +"::"+st[i].getMethodName()+"::"+st[i].getLineNumber();
    }
    
    if (t.getCause() != null) {
    	sStackTrace += "\n\tCaused By: " + getCompressedStackTrace(t, 0);
    }
    
    return sStackTrace;
	}

	public static String getStackTrace(boolean bCompressed, boolean bIncludeSelf) {
		return getStackTrace(bCompressed, bIncludeSelf, 0, 200);
	}

	public static String getStackTrace(boolean bCompressed, boolean bIncludeSelf,
			int iNumLinesToSkip, int iMaxLines) {
		if (bCompressed)
			return getCompressedStackTrace(bIncludeSelf ? 2 + iNumLinesToSkip
					: 3 + iNumLinesToSkip, iMaxLines);

		// bIncludeSelf not supported gor non Compressed yet
		return getStackTrace(1);
	}

	private static String getCompressedStackTrace(int frames_to_skip,
			int iMaxLines) {
		String trace_trace_tail = null;

		try {
			throw new Exception();
		} catch (Exception e) {
			trace_trace_tail = getCompressedStackTrace(e, frames_to_skip, iMaxLines);
		}

		return (trace_trace_tail);
	}
	
	public static void
	killAWTThreads(
		   ThreadGroup	threadGroup )
	{
		 Thread[] threadList = new Thread[threadGroup.activeCount()];
			
		 threadGroup.enumerate(threadList);
			
		 for (int i = 0;	i < threadList.length;	i++){

		 	Thread t = 	threadList[i];
		 	
		 	if ( t != null ){
		 		
		 		String 	name = t.getName();
		 		
		 		if ( name.startsWith( "AWT" )){
		 			
		 			out( "Interrupting thread '".concat(t.toString()).concat("'" ));
		 			
		 			t.interrupt();
		 		}
			}
		}
		
		if ( threadGroup.getParent() != null ){
	  	
			killAWTThreads(threadGroup.getParent());
		}	
	}
		
	public static void
	dumpThreads(
		String	name )
	{
		out(name+":");
			
	  	ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
			
	  	dumpThreads( threadGroup, "\t" );
	}
   
   public static void
   dumpThreads(
   		ThreadGroup	threadGroup,
   		String		indent )
   {
	  Thread[] threadList = new Thread[threadGroup.activeCount()];
			
	  threadGroup.enumerate(threadList);
			
	  for (int i = 0;	i < threadList.length;	i++){

		Thread t = 	threadList[i];
		
		if ( t != null ){		
		
		   out( indent.concat("active thread = ").concat(t.toString()).concat(", daemon = ").concat(String.valueOf(t.isDaemon())));
		}
	  }
	  
	  if ( threadGroup.getParent() != null ){
	  	
	  	dumpThreads(threadGroup.getParent(),indent+"\t");
	  }
   }
   
   public static void
   dumpThreadsLoop(
   	final String	name )
   {
   	new AEThread("Thread Dumper")
	   {
		   public void 
		   runSupport()
		   {	
			   while(true){
				   Debug.dumpThreads(name);
				   
				   try{
				   	Thread.sleep(5000);
				   }catch( Throwable e ){
				   	Debug.printStackTrace( e );
				   }
			   }
		   }
	   }.start();
   }
   
	public static void
	dumpSystemProperties()
	{
		out( "System Properties:");
		
 		Properties props = System.getProperties();
 		
 		Iterator it = props.keySet().iterator();
 		
 		while(it.hasNext()){
 			
 			String	name = (String)it.next();
 			
 			out( "\t".concat(name).concat(" = '").concat(props.get(name).toString()).concat("'" ));
 		}
	}
	
	public static String
	getNestedExceptionMessage(
		Throwable 		e )
	{
		String	last_message	= "";
		
		while( e != null ){
			
			String	this_message = e.getMessage();
			
			if ( this_message == null || this_message.length() == 0 ){
				
				this_message = e.getClass().getName();
				
				int	pos = this_message.lastIndexOf(".");
				
				this_message = this_message.substring( pos+1 );
			}
			
			if ( last_message.indexOf( this_message ) == -1 ){
				
				last_message	= this_message + ( last_message.length()==0?"":(", " + last_message ));
			}
			
			e	= e.getCause();
		}
		
		return( last_message );
	}
	
	public static String
	getExceptionMessage(
		Throwable	e )
	{
		String	message = e.getMessage();
		
		if ( message == null || message.length() == 0 ){
			
			message = e.getClass().getName();
			
			int	pos = message.lastIndexOf(".");
			
			message = message.substring( pos+1 );
			
		}else if ( e instanceof ClassNotFoundException ){
			
			if ( message.toLowerCase().indexOf("found") == -1 ){
				
				message = "Class " + message + " not found";
			}
		}
		
		return( message );
	}
	
	public static void
	printStackTrace(
		Throwable e )
	{
		String header = "DEBUG::";
		header = header + new Date(SystemTime.getCurrentTime()).toString() + "::";
		String className	= "?::";
		String methodName	= "?::";
		int lineNumber		= -1;
		
	    try {
	        throw new Exception();
	    }catch (Exception f) {
	      	StackTraceElement[]	st = f.getStackTrace();
	      	
	      	for (int i=1;i<st.length;i++){
		        StackTraceElement first_line = st[i];
		        className = first_line.getClassName() + "::";
		        methodName = first_line.getMethodName() + "::";
		        lineNumber = first_line.getLineNumber();
		        
		        	// skip stuff generated by the logger
		        
		        if ( 	className.indexOf( ".logging." ) != -1 ||
		        		className.endsWith( ".Debug" )){
		        	
		        	continue;
		        }
		        
		        break;
	      }
	    }
	      
	    diagLoggerLogAndOut(header+className+(methodName)+lineNumber+":", true);
	      
		try{
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			
			PrintWriter	pw = new PrintWriter( new OutputStreamWriter( baos ));
			
			e.printStackTrace( pw );
			
			pw.close();
			
			String	stack = baos.toString();
					    
			diagLoggerLogAndOut("  " + stack, true );			
		}catch( Throwable ignore ){
			
			e.printStackTrace();
		}
	}

	public static String getStackTrace(Throwable e) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos));

			e.printStackTrace(pw);

			pw.close();

			return baos.toString();

		} catch (Throwable ignore) {
			return "";
		}
	}
	
	private static void
	diagLoggerLogAndOut(
		String	str,
		boolean	stderr )
	{
			// handle possible recursive initialisation problems where the init of diag-logger gets
			// back here....
		
		if ( diag_logger == null ){
			if ( stderr ){
				System.err.println( str );
			}else{
				System.out.println( str );
			}
		}else{
			diag_logger.logAndOut( str, stderr );
		}
	}
	private static void
	diagLoggerLogAndOut(
		Throwable e )
	{
			// handle possible recursive initialisation problems where the init of diag-logger gets
			// back here....
		
		if ( diag_logger == null ){
			e.printStackTrace();
		}else{
			diag_logger.logAndOut( e );
		}
	}
}
