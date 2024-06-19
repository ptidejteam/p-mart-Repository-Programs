/*
 * PrerequisiteOperator.java
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
 * Created on 19-Dec-2003
 *
 * Current Ver: $Revision: 1.1 $
 * 
 * Last Editor: $Author: vauchers $
 * 
 * Last Edited: $Date: 2006/02/21 01:33:20 $
 *
 */
package pcgen.core.prereq;
import pcgen.util.PropertyFactory;
import pcgen.core.utils.CoreUtility;


/**
 * @author wardc
 *
 */
public class PrerequisiteOperator 
{
	private PrerequisiteComparator value = PrerequisiteComparator.GTEQ;
	
	public PrerequisiteOperator(String operator) throws PrerequisiteException {
		value = getComparisonType(operator);
	}
	
	public PrerequisiteOperator(PrerequisiteComparator operator) {
		value = operator;
	}
	
	
	public PrerequisiteOperator invert() {
		PrerequisiteComparator comp = PrerequisiteComparator.LT;
		
		if (value.equals(PrerequisiteComparator.EQ))
		{
			comp = PrerequisiteComparator.NEQ;
		}
		else if (value.equals(PrerequisiteComparator.LT))
		{
			comp = PrerequisiteComparator.GTEQ;
		}
		else if (value.equals(PrerequisiteComparator.LTEQ))
		{
			comp = PrerequisiteComparator.GT;
		}
		else if (value.equals(PrerequisiteComparator.GT))
		{
			comp = PrerequisiteComparator.LTEQ;
		}
		else if (value.equals(PrerequisiteComparator.GTEQ))
		{
			comp = PrerequisiteComparator.LT;
		}
		else if (value.equals(PrerequisiteComparator.NEQ))
		{
			comp = PrerequisiteComparator.EQ;
		}
		return new PrerequisiteOperator(comp);
	}

	
	
	
	public String toString() {
		return value.toString();
	}
	
	
	public String toDisplayString() {
		String comp=null;
		if (value.equals(PrerequisiteComparator.EQ))
		{
			comp = PropertyFactory.getString("PrerequisiteOperator.display.eq"); //$NON-NLS-1$
		}
		else if (value.equals(PrerequisiteComparator.LT))
		{
			comp = PropertyFactory.getString("PrerequisiteOperator.display.lt"); //$NON-NLS-1$
		}
		else if (value.equals(PrerequisiteComparator.LTEQ))
		{
			comp = PropertyFactory.getString("PrerequisiteOperator.display.lteq"); //$NON-NLS-1$
		}
		else if (value.equals(PrerequisiteComparator.GT))
		{
			comp = PropertyFactory.getString("PrerequisiteOperator.display.gt"); //$NON-NLS-1$
		}
		else if (value.equals(PrerequisiteComparator.GTEQ))
		{
			comp = PropertyFactory.getString("PrerequisiteOperator.display.gteq"); //$NON-NLS-1$
		}
		else if (value.equals(PrerequisiteComparator.NEQ))
		{
			comp = PropertyFactory.getString("PrerequisiteOperator.display.neq"); //$NON-NLS-1$
		}
		return comp;
	}
	
	
	private final PrerequisiteComparator getComparisonType(final String aString) throws PrerequisiteException
	{
		if ("EQ".equalsIgnoreCase(aString) || "=".equals(aString)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return PrerequisiteComparator.EQ;
		}
		else if ("LT".equalsIgnoreCase(aString) || "<".equals(aString)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return PrerequisiteComparator.LT;
		}
		else if ("LTEQ".equalsIgnoreCase(aString) || "<=".equals(aString)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return PrerequisiteComparator.LTEQ;
		}
		else if ("GT".equalsIgnoreCase(aString) || ">".equals(aString)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return PrerequisiteComparator.GT;
		}
		else if ("GTEQ".equalsIgnoreCase(aString) || ">=".equals(aString)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return PrerequisiteComparator.GTEQ;
		}
		else if ("NEQ".equalsIgnoreCase(aString) || "!=".equals(aString)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return PrerequisiteComparator.NEQ;
		}
		else
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString("PrerequisiteOperator.error.invalid_operator", aString)); //$NON-NLS-1$
		}
	}

	public int compare(int leftHandOp, int rightHandOp) {
		return (int) compare((float) leftHandOp, (float)rightHandOp);
	}
	
	
	public float compare(float leftHandOp, float rightHandOp) {
		boolean passes=false;

		if (value.equals(PrerequisiteComparator.EQ))
		{
			passes = CoreUtility.doublesEqual(leftHandOp, rightHandOp);
		}
		else if (value.equals(PrerequisiteComparator.LT))
		{
			passes = (leftHandOp < rightHandOp);
		}
		else if (value.equals(PrerequisiteComparator.LTEQ))
		{
			passes = (leftHandOp <= rightHandOp);
		}
		else if (value.equals(PrerequisiteComparator.GT))
		{
			passes = (leftHandOp > rightHandOp);
		}
		else if (value.equals(PrerequisiteComparator.GTEQ))
		{
			passes = (leftHandOp >= rightHandOp);
		}
		else if (value.equals(PrerequisiteComparator.NEQ))
		{
			//passes = (leftHandOp != rightHandOp);
			passes = !CoreUtility.doublesEqual(leftHandOp, rightHandOp);
		}
		
		if (passes)
		{
			//if (leftHandOp == 0)
			if (CoreUtility.doublesEqual(leftHandOp,0))
			{
				return 1;
			}
			else
			{
				return leftHandOp;
			}
		}
		return 0;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof PrerequisiteOperator)
		{	
			return value.equals( ((PrerequisiteOperator)obj).value );
		}
		else if (obj instanceof PrerequisiteComparator)
		{
			return value.equals(obj);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}

}
