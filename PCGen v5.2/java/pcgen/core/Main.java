/*
 * Main.java
 * Copyright 2002 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
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
 * Created on January 28, 2002.
 *
 * $Id: Main.java,v 1.1 2006/02/21 01:13:12 vauchers Exp $
 */

package pcgen.core;

import java.lang.reflect.InvocationTargetException;
import pcgen.util.Logging;

/**
 * <code>Main</code> wraps the real entry point for PCGen.  It checks
 * the command line for an alternative main entry point, defaulting to
 * <code>pcGenGUI</code>.  This makes it easy to run command line test
 * cases by including a main entry point in a class, and then giving
 * the full name of the class on the command line like this:<pre>
 *
 *   $ ./pcgen.sh pcgen.util.DiceExpression '1+d4'
 *
 * </pre>
 *
 * @author B. K. Oxley (binkley) <a href="mailto:binkley@bigfoot.com">&lt;binkley@bigfoot.com&gt;</a>
 * @version $Revision: 1.1 $
 */
final class Main
{
	/**
	 * <code>main</code> is a wrapper entry point for the real
	 *  main as determined by the command line argument.
	 *
	 * @param args String[] command line arguments
	 */
	public static void main(String[] args)
	{
		String mainName = "pcgen.gui.pcGenGUI";

		// if arg[0] doesn't start with "pcgen." then it's not
		// likely to be a class we can instantiate and run
		if (args.length > 0 && args[0].length() > 6 && "pcgen.".equals(args[0].substring(0, 6)))
		{
			mainName = args[0];
			final int ni = (args.length > 1) ? args.length - 1 : 0;
			final String[] newArgs = new String[ni];
			for (int i = 1; i < args.length; ++i)
			{
				newArgs[i - 1] = args[i];
			}
			args = newArgs;
		}

		try
		{
			// The real magic
			Class.forName(mainName).getDeclaredMethod("main", new Class[]{String[].class}).invoke(null, new Object[]{args});
		}
		catch (ClassNotFoundException ex)
		{
			Logging.errorPrint("Bad main entry point in " + mainName, ex);
			System.exit(1);
		}
		catch (NoSuchMethodException ex)
		{
			Logging.errorPrint("Bad main entry point in " + mainName, ex);
			System.exit(1);
		}
		catch (IllegalAccessException ex)
		{
			Logging.errorPrint("Bad main entry point in " + mainName, ex);
			System.exit(1);
		}
		catch (InvocationTargetException ex)
		{
			Logging.errorPrint("Bad main entry point in " + mainName, ex);
			System.exit(1);
		}

	}
}

