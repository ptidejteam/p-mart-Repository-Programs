/**
 * ChooserRadio.java
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
 * Created on Jan 21st, 2003, 11:44 PM
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:28:27 $
 *
 **/
package pcgen.gui.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface for a dialog accepts a list of available items,
 * and creates a set of radio buttons
 * This forces the user to choose one and only one selection
 * The dialog is always modal, so a call to show() will block execution
 **/
public interface ChooserRadio
{
	ArrayList getSelectedList();

	void setAvailableList(List availableList);

	void setMessageText(String messageText);

	void setComboData(final String cmbLabelText, List cmbData);

	void show();

	void setVisible(boolean b);

	void setTitle(String title);

}
