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
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * <code>CharacterDomain</code>.
 *
 * A cleric domain that is used by a character.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public class CharacterDomain
{
	/** domainSource is the way to track where a domain came from - if it
	 *  was due to a class, feat, item, whatever.
	 * syntax is "PObject|name[|level]" e.g. "PCClass|Cleric|1" (since the level
	 * is relevant). e.g. "Feat|Awesome Divinity" to attach a domain to a feat.
	 */
	private String domainSource = ""; // from what does the domain come
	private String objectType = "";
	private String objectName = "";
	private int level = 0;
	private Domain _domain = null;

	/** Returns the domain
	 * @return the domain
	 **/
	public Domain getDomain()
	{
		return _domain;
	}

	/**
	 * Sets the domain.
	 * @param Domain the domain to be set
	 */
	public void setDomain(Domain aDomain)
	{
		setSpecialAbilities(_domain, false);
		if (aDomain == null)
		{
			_domain = null;
		}
		else
		{
			_domain = (Domain)aDomain.clone();
			setSpecialAbilities(_domain, true);
		}
	}

	/**
	 * Returns the source of the domain, in the format
	 * "PObject|name[|level]" e.g. "PCClass|Cleric|1" (since the level
	 * is relevant). e.g. "Feat|Awesome Divinity" to attach a domain to a feat.
	 * @return String the source of the domain
	 */
	public String getDomainSource()
	{
		return domainSource;
	}

	/**
	 * What the name of the domain's source is (e.g. Cleric)
	 * @return String the name of the source
	 */
	public String getSourceName()
	{
		return objectName;
	}

	/**
	 * What the source of the domain is (e.g. FEAT, PCClass...)
	 * @return String the source
	 */
	public String getSourceType()
	{
		return objectType;
	}

	/**
	 * Set the source of the domain.
	 * @param String the source to be set. See getDomainSource() for details.
	 */
	public void setDomainSource(String aSource)
	{
		final StringTokenizer aTok = new StringTokenizer(aSource, "|", false);
		if (aTok.countTokens() < 2)
		{
			System.out.println("Invalid Domain Source:" + aSource);
			return;
		}
		objectType = aTok.nextToken().toUpperCase();
		objectName = aTok.nextToken().toUpperCase();
		if (aTok.hasMoreTokens())
			level = Integer.parseInt(aTok.nextToken());
		domainSource = aSource;
	}

	/**
	 * Sets both the domain and the source
	 * @param Domain the domain
	 * @param String the source of the domain
	 */
	public void setDomainAndSource(Domain aDomain, String aSource)
	{
		setDomain(aDomain);
		setDomainSource(aSource);
	}

	/**
	 * Checks if a pc can take a domain.
	 */
	public boolean isDomainValidFor(PlayerCharacter aPC)
	{
		if (_domain == null || domainSource == null)
			return false;
		if (objectType.equals("PCCLASS"))
		{
			final PCClass aClass = aPC.getClassNamed(objectName);
			return (aClass != null && aClass.getLevel().intValue() >= level);
		}
		/** Just preparing for the eventuality that feats will add domains. merton_monk@yahoo.com */
		if (objectType.equals("FEAT"))
		{
			return (aPC.hasFeat(objectName) || aPC.hasFeatAutomatic(objectName) || aPC.hasFeatVirtual(objectName));
		}
		return false;
	}

	private void setSpecialAbilities(Domain aDomain, boolean addIt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if ((aPC != null) && (aDomain != null))
		{
			final ArrayList varList = aDomain.getVariableList();
			final String aString = "DOMAIN:" + aDomain.getName() + "|";
			for (Iterator e1 = varList.iterator(); e1.hasNext();)
			{
				final String aVar = aString + e1.next();
				if (addIt)
					aPC.addVariable(aVar);
				else
					aPC.removeVariable(aVar);
			}

			final ArrayList aArrayList = new ArrayList();
			final StringTokenizer aTok = new StringTokenizer(aDomain.getSpecialAbility(), ",", false);
			while (aTok.hasMoreTokens())
			{
				aArrayList.add("0:" + aTok.nextToken());
			}
			for (int i = 0; i < 20; i++)
			{
				aPC.changeSpecialAbilitiesForLevel(i, addIt, aArrayList);
			}
		}
	}

}