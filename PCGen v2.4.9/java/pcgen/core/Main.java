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
 */

package pcgen.core;


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
public class Main
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

		if (args.length > 0)
		{
			mainName = args[0];
			int ni = args.length > 1 ? args.length - 1 : 0;
			String[] newArgs = new String[ni];
			for (int i = 1; i < args.length; ++i)
				newArgs[i - 1] = args[i];
			args = newArgs;
		}

		try
		{
			// The real magic
			Class.forName(mainName)
				.getDeclaredMethod("main", new Class[]{String[].class})
				.invoke(null, new Object[]{args});
		}

		catch (Exception ex)
		{
			System.err.println("Bad main entry point in "
				+ mainName);
			ex.printStackTrace();
			System.exit(1);
		}

	}
}

