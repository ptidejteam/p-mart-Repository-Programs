/*
 * Boot.java
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
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

package pcgen.util;


/**
 * Get PCGen into a known state.  When this class is loaded, the
 * global program properties loaded as well.  If there is a problem,
 * dump a diagnostic and @see System#exit(int) with 1.
 *
 * @author B. K. Oxley (binkley) <binkley@bigfoot.com>
 * @version $Revision: 1.1 $
 * @see java.util.Properties
 */
public class Boot
{
	/**
	 * Toggle this to get debug output.
	 */
	public static boolean SHOW_FILES_AS_FOUND = false;

	/**
	 * The statup properties file.
	 */
	public static final String PROPERTIES_FILE = "pcgen.ini";

	/**
	 * The default startup jar file.
	 */
	public static final String DEFAULT_JAR_FILE = "pcgen.jar";

	/**
	 * The startup properties.
	 */
	public static java.util.Properties properties;

	static
	{
		try
		{
			properties = new java.util.Properties();
			// Because of chicken-egg issues with static initializers,
			// we need to hand craft the initial input stream and call
			// a special version of @see
			// AnyInputStream#createLocation(java.lang.String,java.lang.String,boolean)
			boolean booting = true;
			pcgen.util.AnyInputStream.Location location
				= AnyInputStream.createLocation
				(DEFAULT_JAR_FILE, PROPERTIES_FILE, booting);

			if (SHOW_FILES_AS_FOUND)
				System.err.println("Found " + location.url);

			properties.load(location.inputStream);
			AnyInputStream.handCraftSearchJarPaths();
		}

		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Get a <code>Collection</code> of <code>String</code>s from Java
	 * properties, breaking them on whitespace.
	 *
	 * @param property The property
	 *
	 * @return The collection of strings
	 *
	 * @see java.util.StringTokenizer
	 */
	public static java.util.Collection getPropertyStrings(String property)
	{
		java.util.Collection collection = (java.util.Collection)new java.util.Vector();
		String value = properties.getProperty(property);

		if (value == null)
			return collection;

		java.util.StringTokenizer st = new java.util.StringTokenizer(value);

		while (st.hasMoreTokens())
			collection.add(st.nextToken());

		return collection;
	}

	/**
	 * Report an internal bug.
	 *
	 * @param bug The problem
	 */
	public static void reportBug(String bug)
	{
		System.err.println
			("BUG: " + bug + ": please send a bug report.");
		new Exception().printStackTrace();
	}

	/**
	 * Report an internal bug.
	 *
	 * @param bug The problem
	 * @param e An exception associated with the bug
	 */
	public static void reportBug(String bug, Exception e)
	{
		System.err.println
			("BUG: " + bug + ": please send a bug report.");
		e.printStackTrace();
	}
}
