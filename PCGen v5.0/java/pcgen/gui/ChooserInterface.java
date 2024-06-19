/*
 * Chooser.java
 * Copyright 2002 (C) Jonas Karlsson
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
 */
package pcgen.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface for a dialog accepts a list of available items, a choice
 * limit, and some additional flags and switches. The user can
 * select and remove values until the required number of
 * choices have been made. The dialog is always modal, so a
 * call to show() will block program execution.
 *
 * @author    Jonas Karlsson
 * @version $Revision: 1.1 $
 */
public interface ChooserInterface
{
	void clearSelectedList();

	List getAvailableColumnNames();

	ArrayList getAvailableList();

	List getSelectedColumnNames();

	ArrayList getSelectedList();

	void setAllowsDups(boolean aBool);

	void setAvailableList(List availableList);

	void setCostColumnNumber(int costColumnNumber);

	void setMessageText(String messageText);

	void setPool(int anInt);

	int getPool();

	void setPoolFlag(boolean poolFlag);

	void setSelectedListTerminator(String aString);

	void setSelectedList(List selectedList);

	void setUniqueList(List uniqueList);

	void show();

	//From Component
	void setVisible(boolean b);

	//From Dialog
	void setTitle(String title);

	void setNegativeAllowed(final boolean argFlag);

}
