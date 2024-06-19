/*
 * BasePanel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on November 5, 2002, 4:29 PM
 *
 * @(#) $Id: BasePanel.java,v 1.1 2006/02/21 01:18:42 vauchers Exp $
 */

package pcgen.gui.editor;

import javax.swing.JPanel;
import pcgen.core.PObject;

/**
 * <code>BasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

class BasePanel extends JPanel implements PObjectUpdater
{
	public void updateView(PObject thisPObject)
	{
	}

	public void updateData(PObject thisPObject)
	{
	}
}
