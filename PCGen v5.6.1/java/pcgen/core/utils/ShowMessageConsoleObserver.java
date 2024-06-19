/*
 * ShowMessageConsoleObserver.java
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
 * Last Edited: $Date: 2006/02/21 01:33:38 $
 *
 */
package pcgen.core.utils;

import java.util.Observable;
import java.util.Observer;

import pcgen.util.Logging;


public class ShowMessageConsoleObserver implements Observer {

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (arg instanceof MessageWrapper) {
			showMessageDialog( (MessageWrapper)arg);
		}
	}

	
	private void showMessageDialog(MessageWrapper messageWrapper)
	{
		if (messageWrapper.getParent() != null)
		{
			Logging.errorPrint("Dialog parent: " + messageWrapper.getParent().toString() + " message: " + messageWrapper.getMessage() + "title: " + messageWrapper.getTitle()
					+ " messageType: " + messageWrapper.getMessageType());
		}
		else
		{
			Logging.errorPrint("Message: " + messageWrapper.getMessage() + "title: " + messageWrapper.getTitle() + " messageType: " + messageWrapper.getMessageType());
		}
	}
	
}
