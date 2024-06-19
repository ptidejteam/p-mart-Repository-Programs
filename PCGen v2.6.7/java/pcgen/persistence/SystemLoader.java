/*
 * SystemLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id: SystemLoader.java,v 1.1 2006/02/20 23:57:43 vauchers Exp $
 */

package pcgen.persistence;

import java.util.List;
import java.util.Set;

/** <code>SystemLoader</code> is an abstract factory class that hides
 * the implementation details of the actual loader.  The initialize method
 * creates an instance of the underlying loader and calls abstract methods to
 * do the loading of system files.
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public interface SystemLoader
{
	public void initialize() throws PersistenceLayerException;

	public void emptyLists();

	public String getCurrentSource();

	public void setCurrentSource(String source);

	public Set getSources();

	public List getChosenCampaignSourcefiles();

	public void setChosenCampaignSourcefiles(List l);

	public void loadCampaigns(List aSelectedCampaignsList) throws PersistenceLayerException;

	public boolean isCustomItemsLoaded();

	public int saveSource(String src);

	public String savedSource(int idx);

	// Methods that should be removed.
	public int saveSourceFile(String src);

	public String savedSourceFile(int idx);

	public List initFile(String fileName, int fileType, List aList) throws PersistenceLayerException;

	/**
	 * Check for an updated set of campaigns, and update Globals.campaignList accordingly.
	 *
	 * @author Ryan Koppenhaver &lt;rlkoppenhaver@yahoo.com&gt;
	 *
	 * @see pcgen.core.Globals#getCampaignList
	 */
	public void refreshCampaigns();

}
