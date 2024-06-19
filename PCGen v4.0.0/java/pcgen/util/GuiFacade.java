/*
 * GuiFacade.java
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
 * $Id: GuiFacade.java,v 1.1 2006/02/21 00:47:07 vauchers Exp $
 */
package pcgen.util;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import pcgen.core.Globals;

/**
 * This is a facade for gui objects in the core code.
 *
 * @author     Jonas Karlsson
 * @version    $Revision: 1.1 $
 */

public class GuiFacade
{
	//
	// Message types. Used by the UI to determine what icon to display,
	// and possibly what behavior to give based on the type.
	//
	/** Used for error messages. */
	public static final int ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;
	/** Used for information messages. */
	public static final int INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;
	/** Used for warning messages. */
	public static final int WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE;
	/** Used for questions. */
	public static final int QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;
	/** No icon is used. */
	public static final int PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;

	public static void showMessageDialog(Component parent, Object message, String title, int messageType, Icon icon)
	{
		if (Globals.getUseGUI())
		{
			if (icon == null)
			{
				JOptionPane.showMessageDialog(parent, message, title, messageType);
			}
			else if (title == null)
			{
				JOptionPane.showMessageDialog(parent, message);
			}
			else
			{
				JOptionPane.showMessageDialog(parent, message, title, messageType, icon);
			}
		}
		else
		{
			Globals.debugPrint("Dialog parent: " + parent.toString() + " message: " + message + "title: " + title + " messageType: " + messageType);
		}
	}

	public static void showMessageDialog(Component parent, Object message, String title, int messageType)
	{
		showMessageDialog(parent, message, title, messageType, null);
	}

	public static void showMessageDialog(Component parent, Object message)
	{
		showMessageDialog(parent, message, null, -1, null);
	}

}
