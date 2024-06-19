package gombos.webbrowser;
/*
 * Created on Feb 27, 2006
 * 
 * Author: Andrew Gombos
 *
 * Simply to give a name to exceptions that occur when parsing documents
 */

public class ParseException extends Exception {
	public ParseException(String message) {
		super(message);
	}
}
