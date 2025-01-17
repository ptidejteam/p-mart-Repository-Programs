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
 * @(#) $Id: LanguageBasePanel.java,v 1.1 2006/02/21 01:18:42 vauchers Exp $
 */

package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.util.PropertyFactory;

/**
 * <code>LanguageBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public class LanguageBasePanel extends BasePanel
{

	//private AvailableSelectedPanel pnlLanguageType;
	private TypePanel pnlLanguageType;

	/** Creates new form LanguageBasePanel */
	public LanguageBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	private void initComponentContents()
	{
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		//pnlLanguageType = new AvailableSelectedPanel();
		pnlLanguageType = new TypePanel(PropertyFactory.getString("in_demEnterNewType"));

		setLayout(new BorderLayout());

		//pnlLanguageType.setHeader(PropertyFactory.getString("in_type"));
		add(pnlLanguageType, BorderLayout.CENTER);
	}

	public void setTypesAvailableList(final List aList, final boolean sort)
	{
		pnlLanguageType.setAvailableList(aList, sort);
	}

	public void setTypesSelectedList(final List aList, final boolean sort)
	{
		pnlLanguageType.setSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return pnlLanguageType.getSelectedList();
	}

	public void updateView(PObject thisPObject)
	{
		String aString;
		Iterator e;
		Language thisLanguage = (Language) thisPObject;

		List availableList = new ArrayList();
		List selectedList = new ArrayList();
		for (e = Globals.getLanguageList().iterator(); e.hasNext();)
		{
			final Language aLanguage = (Language) e.next();
			for (int i = aLanguage.getMyTypeCount(); i > 0;)
			{
				aString = aLanguage.getMyType(--i);
				if (!aString.equals(Constants.s_CUSTOM))
				{
					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
			}
		}
		//
		// remove this language's type from the available list and place into selected list
		//
		for (int i = thisLanguage.getMyTypeCount(); i > 0;)
		{
			aString = thisLanguage.getMyType(--i);
			if (!aString.equals(Constants.s_CUSTOM))
			{
				selectedList.add(aString);
				availableList.remove(aString);
			}
		}
		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);
	}

	public void updateData(PObject thisPObject)
	{
		Object[] sel = getTypesSelectedList();
		thisPObject.setTypeInfo(".CLEAR");
		for (int i = 0; i < sel.length; ++i)
		{
			thisPObject.setTypeInfo(sel[i].toString());
		}
	}
}
