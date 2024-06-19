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
 * $Id: GuiFacade.java,v 1.1 2006/02/21 01:18:42 vauchers Exp $
 */
package pcgen.gui.utils;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import pcgen.core.Globals;
import pcgen.util.Logging;

/**
 * This is a facade for gui objects in the core code.
 *
 * @author     Jonas Karlsson
 * @version    $Revision: 1.1 $
 */

public final class GuiFacade extends JOptionPane
{
	////
	//// Message types. Used by the UI to determine what icon to display,
	//// and possibly what behavior to give based on the type.
	////
	/// ** Used for error messages. * /
	//public static final int ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;
	/// ** Used for information messages. * /
	//public static final int INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;
	/// ** Used for warning messages. * /
	//public static final int WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE;
	/// ** Used for questions. * /
	//public static final int QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;
	/// ** No icon is used. * /
	//public static final int PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;

	//provide private constructor so utility classes can't be instantiated
	private GuiFacade()
	{
		super();
	}

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
		else if (parent != null)
		{
			Logging.errorPrint("Dialog parent: " + parent.toString() + " message: " + message + "title: " + title + " messageType: " + messageType);
		}
		else
		{
			Logging.errorPrint("Message: " + message + "title: " + title + " messageType: " + messageType);
		}
	}

	public static void showMessageDialog(Component parent, Object message, String title, int messageType)
	{
		if (Globals.getUseGUI())
		{
			showMessageDialog(parent, message, title, messageType, null);
		}
		else if (parent != null)
		{
			Logging.errorPrint("Dialog parent: " + parent.toString() + " message: " + message + "title: " + title + " messageType: " + messageType);
		}
		else
		{
			Logging.errorPrint("Message: " + message + "title: " + title + " messageType: " + messageType);
		}
	}

	public static Object showInputDialog(Component parentComponent, Object message, String title, int messageType, Icon icon, Object[] selectionValues, Object initialSelectionValue)
	{
		if (Globals.getUseGUI())
		{
			return JOptionPane.showInputDialog(parentComponent, message, title, messageType, icon, selectionValues, initialSelectionValue);
		}
		else
		{
			//TODO: This should probably prompt, but not sure if that makes sense on the command line
			throw new IllegalStateException("Cannot showInputDialog when getUseGUI returns false");
		}
	}
}
