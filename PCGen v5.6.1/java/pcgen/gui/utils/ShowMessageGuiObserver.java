/*
 * ShowMessageGuiObserver.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1.1 $
 *
 * Last Editor: $Author: vauchers $
 *
 * Last Edited: $Date: 2006/02/21 01:33:05 $
 *
 */
package pcgen.gui.utils;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pcgen.core.Globals;
import pcgen.core.utils.MessageWrapper;

public class ShowMessageGuiObserver implements Observer {

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (arg instanceof MessageWrapper) {
			showMessageDialog( (MessageWrapper)arg);
		}
	}

	public static Object showInputDialog(Component parentComponent, Object message, String title, int messageType,
			Icon icon, Object[] selectionValues, Object initialSelectionValue)
	{
		if (Globals.getUseGUI())
		{
			return JOptionPane.showInputDialog(parentComponent, message, title, messageType, icon, selectionValues,
					initialSelectionValue);
		}
		else
		{
			//TODO: This should probably prompt, but not sure if that makes sense on the command line
			throw new IllegalStateException("Cannot showInputDialog when getUseGUI returns false");
		}
	}
	
	

	
	
	public static void showMessageDialog(MessageWrapper messageWrapper)
	{
		if (messageWrapper.getIcon() == null)
		{
			JOptionPane.showMessageDialog(messageWrapper.getParent(), messageWrapper.getMessage(), messageWrapper.getTitle(), messageWrapper.getMessageType());
		}
		else if (messageWrapper.getTitle() == null)
		{
			JOptionPane.showMessageDialog(messageWrapper.getParent(), messageWrapper.getMessage());
		}
		else
		{
			JOptionPane.showMessageDialog(messageWrapper.getParent(), messageWrapper.getMessage(), messageWrapper.getTitle(), messageWrapper.getMessageType(), messageWrapper.getIcon());
		}
	}
	
}
