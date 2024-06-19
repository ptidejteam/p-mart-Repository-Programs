/*
 * RuleCheck.java
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Novmeber 06, 2003, 11:59 PM PST
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:27:59 $
 *
 */

package pcgen.core;


/**
 * <code>RuleCheck</code> describes checks that can be turned on or off
 * in the GUI by the users
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class RuleCheck
{
	private String name = "";
	private String key = "";
	private String var = "";
	private String parm = "";
	private String excludeKey = "";
	private String desc = "";
	private boolean status = false;

	/**
	 * Default constructor for RuleCheck
	 **/
	public RuleCheck()
	{
	}

	/**
	 * Sets the Name (and key if not already set)
	 * @param aName set name to
	 */
	public void setName(String aName)
	{
		name = aName;
		if (key.length() <= 0)
		{
			key = aName;
		}
	}

	public String getName()
	{
		return name;
	}

	/**
	 * @param aString set key and var to
	 **/
	public void setVariable(String aString)
	{
		var =  aString;
		key =  aString;
	}

	public String getVariable()
	{
		return var;
	}

	/**
	 * @param aString set parm, key and var to
	 **/
	public void setParameter(String aString)
	{
		parm = aString;
		key =  aString;
		if (var.length() <= 0)
		{
			var = aString;
		}
	}

	public String getParameter()
	{
		return parm;
	}

	/**
	 * Returns the unique key for this Rule
	 **/
	public String getKey()
	{
		return key;
	}

	/**
	 * @param aString Used to set on/off status
	 **/
	public void setDefault(String aString)
	{
		status = aString.startsWith("Y") || aString.startsWith("y");
	}

	public boolean getDefault()
	{
		return status;
	}

	/**
	 * @param aString set exclude to
	 **/
	public void setExclude(String aString)
	{
		excludeKey = aString;
	}

	public String getExcludeKey()
	{
		return excludeKey;
	}

	public boolean isExclude()
	{
		return (excludeKey.length() > 0);
	}

	/**
	 * @param aString set desc to
	 **/
	public void setDesc(String aString)
	{
		desc = aString;
	}

	public String getDesc()
	{
		return desc;
	}

}
