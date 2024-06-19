/*
 * PropertyFactory.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on January 03, 2002, 2:15 PM
 */

package pcgen.util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import pcgen.core.Globals;

/**
 * <code>PropertyFactory</code>
 *
 * @author Thomas Behr 03-01-02
 * @version $Revision: 1.1 $
 */

/**
 * This good as is, as far as I can tell
 *
 * Mario Bonassin
 */

public class PropertyFactory
{
	private static Properties properties;
	public static final String UNDEFINED = "Not defined.";

	/**
	 * @author: Thomas Behr 03-01-02
	 */
	static
	{
		init();
	}

	/**
	 * @author: Thomas Behr 03-01-02
	 */
	private static void init()
	{
		properties = new Properties();
		//load all known property files here
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		ResourceBundle bundles[] = new ResourceBundle[1];
		try
		{
			bundles[0] = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
		}
		catch (MissingResourceException mrex)
		{
			bundles[0] = null;
			mrex.printStackTrace();
		}
		String key;
		for (int i = 0; i < bundles.length; i++)
		{
			if (bundles[i] != null)
			{
				for (Enumeration enum = bundles[i].getKeys(); enum.hasMoreElements();)
				{
					key = (String)enum.nextElement();
					properties.setProperty(key, bundles[i].getString(key));
				}
			}
		}
		bundles = null;
	}

	/**
	 * @author: Thomas Behr 03-01-02
	 */
	public static boolean hasDefinedProperty(String key)
	{
		String value = properties.getProperty(key);
		if ((value == null) || value.equals(UNDEFINED))
		{
			return false;
		}
		return true;
	}

	/**
	 * @author: Thomas Behr 03-01-02
	 */
	public static String getProperty(String key)
	{
		String value = properties.getProperty(key);
		if (value == null)
		{
			value = UNDEFINED;
			properties.setProperty(key, UNDEFINED);
		}
		return value;
	}

	/**
	 * convenience method
	 * @author: Thomas Behr 03-01-02
	 */
	public static String getString(String key)
	{
		return getProperty(key);
	}
}
