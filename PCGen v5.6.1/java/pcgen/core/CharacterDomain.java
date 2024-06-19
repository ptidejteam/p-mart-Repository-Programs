/*
 * Campaign.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:33:15 $
 *
 */

package pcgen.core;

import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;
import pcgen.util.Logging;

/**
 * <code>CharacterDomain</code>.
 *
 * A cleric domain that is used by a character.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class CharacterDomain
{
	/**
	 * Returns whether the domain is from a feat
	 * @return boolean true if the domain is from a feat, else false
	 */
	public boolean isFromFeat()
	{
		return fromFeat;
	}

	/**
	 * domainSource is the way to track where a domain came from - if it
	 * was due to a class, feat, item, etc
	 * syntax is "PObject|name[|level]"
	 * e.g: "PCClass|Cleric|1" (since the level is relevant)
	 * e.g: "Feat|Awesome Divinity" (to attach a domain to a feat)
	 */
	private String domainSource = ""; // domain source String (delimited)
	private String objectType = ""; // type of domain -- feat, class, etc
	private String objectName = ""; // domain name
	private int level = 0; // pre-req level
	private Domain domain = null; // reference to the domain
	private boolean fromFeat = false; // true if domain is from a feat
	private boolean fromPCClass = false; // true if domain is from a PC Class
	private boolean rebuildSource = true; // whether to rebuild the delimited source

	/** The object type for a domain from a PC Class */
	public static final String PC_CLASS_TYPE = "PCClass";

	/** The object type for a domain from a Feat */
	public static final String FEAT_CLASS_TYPE = "Feat";

	/** Returns the domain
	 * @return Domain the domain
	 **/
	public Domain getDomain()
	{
		return domain;
	}

	/**
	 * Sets the domain
	 * @param aDomain Domain the domain to be set
	 */
	public Domain setDomain(Domain aDomain)
	{
		setSpecialAbilities(domain, false);
		if (aDomain == null)
		{
			domain = null;
		}
		else
		{
			domain = (Domain) aDomain.clone();
			setSpecialAbilities(domain, true);
		}
		return domain;
	}

	/**
	 * Returns the source of the domain in the format "PObject|name[|level]"
	 * e.g: "PCClass|Cleric|1"
	 * (since the level is relevant)
	 * e.g: "Feat|Awesome Divinity" to attach a domain to a feat
	 *
	 * This method should NOT be called outside of file i/o routines!
	 * DO NOT perform comparisons on this String!
	 *
	 * @return String the source of the domain
	 */
	public String getDomainSource()
	{
		if (rebuildSource)
		{
			StringBuffer buff = new StringBuffer(30);
			buff.append(objectType);
			buff.append('|');
			buff.append(objectName);

			if (level > 0)
			{
				buff.append('|');
				buff.append(level);
			}

			domainSource = buff.toString();
			rebuildSource = false;
		}
		return domainSource;
	}

	/**
	 * What the name of the domain's source is
	 * e.g: Cleric (if from the Cleric class)
	 * @return String the name of the source
	 */
	public String getObjectName()
	{
		return objectName;
	}

	/**
	 * Set the source of the domain
	 * This method should NOT be called outside of file i/o routines!
	 * @param aSource the source to be set
	 * See getDomainSource() for details.
	 **/
	public void setDomainSource(String aSource)
	{
		final StringTokenizer aTok = new StringTokenizer(aSource, "|", false);
		if (aTok.countTokens() < 2)
		{
			Logging.errorPrint("Invalid Domain Source:" + aSource);
			return;
		}
		objectType = aTok.nextToken().toUpperCase();
		if (objectType.equalsIgnoreCase(PC_CLASS_TYPE))
		{
			fromPCClass = true;
		}
		else if (objectType.equalsIgnoreCase(FEAT_CLASS_TYPE))
		{
			fromFeat = true;
		}
		objectName = aTok.nextToken().toUpperCase();
		if (aTok.hasMoreTokens())
		{
			level = Integer.parseInt(aTok.nextToken());
		}
		domainSource = aSource;
		rebuildSource = false;
	}

	/**
	 * Checks if a pc can take a domain
	 */
	boolean isDomainValidFor(PlayerCharacter aPC)
	{
		boolean valid = false;
		if (domain == null || domainSource == null)
		{
			valid = false;
		}
		if (fromPCClass)
		{
			final PCClass aClass = aPC.getClassNamed(objectName);
			valid = (aClass != null && aClass.getLevel() >= level);
		}
		// Just preparing for the eventuality
		// that feats will add domains.
		// merton_monk@yahoo.com
		if (fromFeat)
		{
			valid = (aPC.hasFeat(objectName) || aPC.hasFeatAutomatic(objectName) || aPC.hasFeatVirtual(objectName));
		}
		return valid;
	}

	/**
	 * Converts this object to a String
	 * The String format is as follows (without the braces) :
	 * <ul>
	 * <li>[class name]:[domainName] (if from a class)</li>
	 * <li>[feat name]:[domainName] (if from a feat)</li>
	 * <li>[domainName] (if from a feat)</li>
	 * <li>[domainName] (if from a non-feat non-class source)</li>
	 * <li>An empty string (if the domain is unset)</li>
	 * </ul>
	 */
	public String toString()
	{
		String string = "";
		if (domain != null)
		{
			final StringBuffer name = new StringBuffer(domain.getName());
			if (fromPCClass)
			{
				final PCClass aClass = Globals.getClassNamed(objectName);
				if (aClass != null)
				{
					name.insert(0, aClass.getName() + ":");
				}
			}
			else if (fromFeat)
			{
				final Feat aFeat = Globals.getFeatNamed(objectName);
				if (aFeat != null)
				{
					name.insert(0, aFeat.getName() + ":");
				}
			}
			string = name.toString();
		}
		return string;
	}

	/**
	 * This method adds/removes the special abilities
	 * on the current PC that have been granted by this domain
	 * @param aDomain Domain granting the abilities
	 * @param addIt boolean true if the abilities should be added, or
	 * false if they should be removed
	 **/
	private static void setSpecialAbilities(Domain aDomain, boolean addIt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if ((aPC != null) && (aDomain != null))
		{
			final String aString = "DOMAIN:" + aDomain.getName() + '|';
			for (int i = 0; i < aDomain.getVariableCount(); i++)
			{
				final String aVar = aString + aDomain.getVariableDefinition(i);
				if (addIt)
				{
					aPC.addVariable(aVar);
				}
				else
				{
					aPC.removeVariable(aVar);
				}
			}
		}
	}

	/**
	 * Returns whether the domain is from a PC Class
	 * @return boolean true if the domain is from a PC Class, else false
	 **/
	public boolean isFromPCClass()
	{
		return fromPCClass;
	}

	/**
	 * Returns whether the domain is from the given PC Class
	 * @param pcClassName String name of PC Clas to check
	 * @return boolean true if the domain is from the given PC Class
	 **/
	public boolean isFromPCClass(String pcClassName)
	{
		if (fromPCClass)
		{
			return objectName.equalsIgnoreCase(pcClassName);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Sets whether the domain is from a PC Class
	 * @param isPCClass boolean true if the domain is from a PC Class
	 **/
	public void setFromPCClass(boolean isPCClass)
	{
		fromPCClass = isPCClass;
		objectType = PC_CLASS_TYPE;
		rebuildSource = true;
	}

	/**
	 * Sets the minimum level for this domain
	 * @param level int containing the new minimum level for the domain
	 **/
	void setLevel(int level)
	{
		this.level = level;
		rebuildSource = true;
	}

	/**
	 * Sets the name of the domain's source
	 * e.g: Cleric (if from the Cleric class)
	 **/
	public void setObjectName(String aName)
	{
		objectName = aName;
		rebuildSource = true;
	}

	public Set getVariableNamesAsUnmodifiableSet()
	{
		if (domain != null)
		{
			return domain.getVariableNamesAsUnmodifiableSet();
		}
		return Collections.EMPTY_SET;
	}
}
