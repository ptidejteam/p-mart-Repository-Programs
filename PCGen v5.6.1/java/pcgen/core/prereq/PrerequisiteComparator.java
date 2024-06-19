/*
 * PrerequisiteComparator.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 12-Jan-2004
 *
 * Current Ver: $Revision: 1.1 $
 * 
 * Last Editor: $Author: vauchers $
 * 
 * Last Edited: $Date: 2006/02/21 01:33:20 $
 *
 */
package pcgen.core.prereq;

/**
 * @author frugal@purplewombat.co.uk
 *
 */
public class PrerequisiteComparator {
	private final String name;
	
	private PrerequisiteComparator(String name) 
	{ 
		this.name = name; 
	}
	
	public String toString() 
	{ 
		return name; 
	}
	
	// Prevent subclasses from overriding Object.equals
	public final boolean equals(Object that)
	{
		return super.equals(that);
	}
	
	
	public final int hashCode()
	{
		return super.hashCode();
	}
	
	
	public static final PrerequisiteComparator GTEQ = new PrerequisiteComparator("gteq"); //$NON-NLS-1$
	public static final PrerequisiteComparator GT = new PrerequisiteComparator("gt"); //$NON-NLS-1$
	public static final PrerequisiteComparator EQ = new PrerequisiteComparator("eq"); //$NON-NLS-1$
	public static final PrerequisiteComparator NEQ = new PrerequisiteComparator("neq"); //$NON-NLS-1$
	public static final PrerequisiteComparator LT = new PrerequisiteComparator("lt"); //$NON-NLS-1$
	public static final PrerequisiteComparator LTEQ = new PrerequisiteComparator("lteq"); //$NON-NLS-1$
	
}
