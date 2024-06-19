/*
 * LanguageBasePanel.java
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
 * Created on November 22, 2002, 11:38 PM
 *
 * @(#) $Id: LanguageBasePanel.java,v 1.1 2006/02/21 01:00:36 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import pcgen.util.PropertyFactory;

/**
 * <code>LanguageBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public class LanguageBasePanel extends BasePanel
{

	private AvailableSelectedPanel pnlLanguageType;

	/** Creates new form LanguageBasePanel */
	public LanguageBasePanel()
	{
		initComponents();
		intComponentContents();
	}

	private void intComponentContents()
	{
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		pnlLanguageType = new AvailableSelectedPanel();

		setLayout(new BorderLayout());

		pnlLanguageType.setHeader(PropertyFactory.getString("in_type"));
		add(pnlLanguageType, BorderLayout.CENTER);
	}

	public void setTypesAvailableList(final ArrayList aList, final boolean sort)
	{
		pnlLanguageType.setAvailableList(aList, sort);
	}

	public Object[] getTypesAvailableList()
	{
		return pnlLanguageType.getAvailableList();
	}

	public void setTypesSelectedList(final ArrayList aList, final boolean sort)
	{
		pnlLanguageType.setSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return pnlLanguageType.getSelectedList();
	}
}
