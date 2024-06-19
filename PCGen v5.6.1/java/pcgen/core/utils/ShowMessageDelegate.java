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
 * $Id: ShowMessageDelegate.java,v 1.1 2006/02/21 01:33:38 vauchers Exp $
 */
package pcgen.core.utils;

import java.awt.Component;
import java.util.Observable;

import javax.swing.Icon;

/**
 * This is a facade for gui objects in the core code.
 *
 * @author     Jonas Karlsson
 * @version    $Revision: 1.1 $
 */
public final class ShowMessageDelegate extends Observable
{
	private static ShowMessageDelegate instance = new ShowMessageDelegate();
	
	
	private ShowMessageDelegate()
	{
		super();
	}


	public static void showMessageDialog(Component parent, Object message, String title, int messageType, Icon icon) {
		showMessageDialog(new MessageWrapper(parent, message, title, messageType, icon));
	}

	public static void showMessageDialog(Component parent, Object message, String title, int messageType) {
		showMessageDialog(new MessageWrapper(parent, message, title, messageType));
	}

	public static void showMessageDialog(MessageWrapper messageWrapper)
	{
		instance.setChanged();
		instance.notifyObservers(messageWrapper);
	}

	
	
	/**
	 * @return Returns the instance.
	 */
	public static ShowMessageDelegate getInstance() {
		return instance;
	}

}
