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

import java.text.MessageFormat;
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
 *
 * This good as is, as far as I can tell
 *
 * Mario Bonassin
 */
public final class PropertyFactory
{
	private static Properties properties;
	public static final String UNDEFINED = " not defined.";

	/**
	 * author: Thomas Behr 03-01-02
	 */
	static
	{
		init();
	}

	public static char getMnemonic(String property)
	{
		return getMnemonic(property, '\0');
	}

	/**
	 * convenience method
	 * author: Thomas Behr 03-01-02
	 */
	public static String getString(String key)
	{
		return getProperty(key);
	}

	/**
	 * convenience method
	 * author: Frugal 2004/01/13
	 */
	public static String getFormattedString(String key, Object arg0)
	{
		String prop = getString(key);
		Object[] args = new Object[] { arg0 };
		
		return MessageFormat.format(prop, args);
	}

	/**
	 * convenience method
	 * author: Frugal 2004/01/13
	 */
	public static String getFormattedString(String key, Object arg0, Object arg1)
	{
		String prop = getString(key);
		Object[] args = new Object[] { arg0, arg1 };
		
		return MessageFormat.format(prop, args);
	}
	
	/**
	 * convenience method
	 * author: Frugal 2004/01/13
	 */
	public static String getFormattedString(String key, Object arg0, Object arg1, Object arg2)
	{
		String prop = getString(key);
		Object[] args = new Object[] { arg0, arg1, arg2 };
		
		return MessageFormat.format(prop, args);
	}
	
	/**
	 * convenience method
	 * author: Frugal 2004/01/13
	 */
	public static String getFormattedString(String key, Object args[])
	{
		String prop = getString(key);
		
		return MessageFormat.format(prop, args);
	}
	
	private static char getMnemonic(String property, char def)
	{
		final String mnemonic = getProperty(property);

		if (mnemonic.length() != 0)
		{
			return mnemonic.charAt(0);
		}

		return def;
	}

	/**
	 * author: Thomas Behr 03-01-02
	 */
	private static String getProperty(String key)
	{
		String value = properties.getProperty(key);

		if (value == null)
		{
			value = key + UNDEFINED;
			properties.setProperty(key, value);
		}

		return value;
	}

	/**
	 * author: Thomas Behr 03-01-02
	 */
	private static void init()
	{
		properties = new Properties();

		//load all known property files here
		final Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		ResourceBundle[] bundles = new ResourceBundle[1];

		try
		{
			bundles[0] = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
		}
		catch (MissingResourceException mrex)
		{
			bundles[0] = null;
			Logging.errorPrint("Can't find language bundle", mrex);
		}

		String key;

		for (int i = 0; i < bundles.length; i++)
		{
			if (bundles[i] != null)
			{
				for (Enumeration keys = bundles[i].getKeys(); keys.hasMoreElements();)
				{
					key = (String) keys.nextElement();
					properties.setProperty(key, bundles[i].getString(key));
				}
			}
		}
	}
}
