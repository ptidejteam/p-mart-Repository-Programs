/*
 * FakeWindowsLookAndFeel.java
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
 * Created on April 21, 2001, 2:15 PM
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 */

package pcgen.gui;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import javax.swing.UIDefaults;

/**
 * Support Windows95 L&F on non-Windows platforms.  This is ridiculously
 * simple.
 *
 * @author B. K. Oxley (binkley) <a href="mailto:binkley@bigfoot.com">&lt;binkley@bigfoot.com&gt;</a>
 * @version $Revision: 1.1 $
 */
public class FakeWindowsLookAndFeel extends WindowsLookAndFeel
{
	/**
	 * Support Windows95 L&F on non-Windows platforms.  Simple
	 * return <code>true</code>.
	 *
	 * @return boolean <code>true</code> always
	 */
	public boolean isSupportedLookAndFeel()
	{
		return true;
	}

	// These hacks convinces JDK 1.4 to use the Win2K UI instead
	// of the Win95 one.  Should be configurable!  XXX --bko

	public void initialize()
	{
		if (UIFactory.isWindowsPlatform())
			super.initialize();

		else
		{ // fake it for non-Windows platforms
			String osVersion = System.getProperty("os.version");
			System.setProperty("os.version", "5.0");
			super.initialize();
			System.setProperty("os.version", osVersion);
		}
	}

	protected void initComponentDefaults(UIDefaults table)
	{
		if (UIFactory.isWindowsPlatform())
			super.initComponentDefaults(table);

		else
		{ // fake it for non-Windows platforms
			String osVersion = System.getProperty("os.version");
			System.setProperty("os.version", "5.0");
			super.initComponentDefaults(table);
			System.setProperty("os.version", osVersion);
		}
	}
}
