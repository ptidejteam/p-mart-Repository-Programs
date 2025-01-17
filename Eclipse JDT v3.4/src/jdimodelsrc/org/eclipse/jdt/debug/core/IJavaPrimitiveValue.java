/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.core;

 
/**
 * A primitive value on a Java debug target.
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJavaPrimitiveValue extends IJavaValue {
	
	/**
	 * Returns this value as a boolean.
	 * 
	 * @return this value as a boolean
	 */
	public boolean getBooleanValue();
	
	/**
	 * Returns this value as a byte
	 * 
	 * @return this value as a byte
	 */
	public byte getByteValue();
	
	/**
	 * Returns this value as a char
	 * 
	 * @return this value as a char
	 */
	public char getCharValue();
	
	/**
	 * Returns this value as a double
	 * 
	 * @return this value as a double
	 */
	public double getDoubleValue();
	
	/**
	 * Returns this value as a float
	 * 
	 * @return this value as a float
	 */
	public float getFloatValue();
	
	/**
	 * Returns this value as an int
	 * 
	 * @return this value as an int
	 */
	public int getIntValue();
	
	/**
	 * Returns this value as a long
	 * 
	 * @return this value as a long
	 */
	public long getLongValue();
	
	/**
	 * Returns this value as a short
	 * 
	 * @return this value as a short
	 */
	public short getShortValue();
}
