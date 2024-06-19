/*
 * Created on 28-Nov-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package pcgen.core.prereq;

import pcgen.core.PObject;


public class PreReqState
{
	PObject theObj;
	String preString;
	String aType;
	String aList;
	boolean handled;
	
	public PreReqState(String prereq) {
		preString = prereq;
	}
	
	/**
	 * @return
	 */
	public String getParameterString() {
		return aList;
	}

	/**
	 * @return
	 */
	public String getKind() {
		return aType;
	}

	/**
	 * @return
	 */
	public boolean isHandled() {
		return handled;
	}

	/**
	 * @return
	 */
	public String toString() {
		return preString;
	}

	/**
	 * @return
	 */
	public PObject getTheObj() {
		return theObj;
	}

	/**
	 * @param string
	 */
	public void setParameterString(String string) {
		aList = string;
	}

	/**
	 * @param string
	 */
	public void setKind(String string) {
		aType = string;
	}

	/**
	 * @param b
	 */
	public void setHandled(boolean b) {
		handled = b;
	}


	/**
	 * @param object
	 */
	public void setTheObj(PObject object) {
		theObj = object;
	}

}
