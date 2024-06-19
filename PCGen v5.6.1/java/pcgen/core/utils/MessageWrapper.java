/*
 * MessageWrapper.java
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

import java.awt.Component;

import javax.swing.Icon;


public class MessageWrapper {
	private Component parent;
	private Object message;
	private String title;
	private int messageType;
	private Icon icon;
	
	
	public MessageWrapper(Component parent, Object message, String title, int messageType, Icon icon) {
		super();
		this.parent = parent;
		this.message = message;
		this.title = title;
		this.messageType = messageType;
		this.icon = icon;
	}

	public MessageWrapper(Component parent, Object message, String title, int messageType) {
		super();
		this.parent = parent;
		this.message = message;
		this.title = title;
		this.messageType = messageType;
		this.icon = null;
	}

	/**
	 * @return Returns the icon.
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * @return Returns the message.
	 */
	public Object getMessage() {
		return message;
	}

	/**
	 * @return Returns the messageType.
	 */
	public int getMessageType() {
		return messageType;
	}

	/**
	 * @return Returns the parent.
	 */
	public Component getParent() {
		return parent;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

}
