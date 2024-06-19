/*
 * PersistenceManager.java
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
 * $Id: PersistenceManager.java,v 1.1 2006/02/21 01:16:28 vauchers Exp $
 */

package pcgen.persistence;

import java.util.List;
import java.util.Set;
import pcgen.persistence.lst.LstSystemLoader;

/** <code>PersistenceManager</code> is a factory class that hides
 * the implementation details of the actual loader.  The initialize method
 * creates an instance of the underlying loader and calls methods to
 * do the loading of system files.
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public final class PersistenceManager
{

	private static final SystemLoader instance = new LstSystemLoader();
	private static boolean initialized = false;

	/** Private to make it impossible to create another instance of PersistenceManager */
	private PersistenceManager()
	{
	}

	/////////////////////////////////////////////////////////////////////
	// Static methods
	////////////////////////////////////////////////////////////////////
	/**
	 * Initialize the SystemLoader with the appropriate loader classes
	 * Right now this is hardcoded to LstSystemLoader, but the eventual
	 * intention is to allow any system loader to be plugged in so that
	 * the data can be loaded from .lst files or XML files,
	 * or possibly even a database.
	 */
	public static void initialize() throws PersistenceLayerException
	{
		// Bug 638568 -- sage_sam, 18 Feb 2003
		// Make sure the manager is only initialized once.
		if (!initialized)
		{
			instance.initialize();
			// Loading pending MODs
			instance.loadModItems(true);

			initialized = true;
		}
	}

	public static void emptyLists()
	{
		instance.emptyLists();
	}

	public static Set getSources()
	{
		return instance.getSources();
	}

	public static List getChosenCampaignSourcefiles()
	{
		return instance.getChosenCampaignSourcefiles();
	}

	public static void setChosenCampaignSourcefiles(List l)
	{
		instance.setChosenCampaignSourcefiles(l);
	}

	public static void loadCampaigns(List aSelectedCampaignsList) throws PersistenceLayerException
	{
		instance.loadCampaigns(aSelectedCampaignsList);
		// Loading pending MODs
		instance.loadModItems(true);
	}

	/**
	 * This method indicates whether custom items have been successfully
	 * loaded
	 * @return boolean true if custom items are loaded, else false
	 */
	public static boolean isCustomItemsLoaded()
	{
		return instance.isCustomItemsLoaded();
	}

	/**
	 * This method sets whether custom items have been successfully
	 * loaded
	 * @return boolean true if custom items are loaded, else false
	 * @deprecated each loader should determine this on their own
	 */
	public static void setCustomItemsLoaded(boolean argLoaded)
	{
		instance.setCustomItemsLoaded(argLoaded);
	}

	/**
	 * This Method to be removed.
	 * @deprecated -- this is a loader, not a saver.
	 */
	public static int saveSourceFile(String src)
	{
		return instance.saveSourceFile(src);
	}

	/**
	 * This Method to be removed.
	 * @deprecated -- this is a loader, not a saver.
	 */
	public static String savedSourceFile(int idx)
	{
		return instance.savedSourceFile(idx);
	}

	/**
	 * Loads a file containing game system information and adds details
	 * to an array. Eventually these end up in the various array list
	 * properties of <code>Global</code>.
	 * <p>
	 * Different types of files are determined by the <code>type</code>
	 * parameter. The valid <code>type</code>'s are in LstConstants.java
	 * <p>
	 * The file is opened and read. Lines are parsed by an object
	 * of the relevant type (based on <code>type</code> above), and
	 * then added to the array list.
	 *
	 * @param argFileName    name of the file to load from
	 * @param fileType    type of the file (see above for types).
	 * @param aList       <code>ArrayList</code> with existing data.
	 *                    The new data is appended to this.
	 * @return List the same list passed in, but with new data appended
	 * @deprecated use loadFileIntoList
	 */
	public static List initFile(String fileName, int fileType, List aList) throws PersistenceLayerException
	{
		return instance.initFile(fileName, fileType, aList);
	}

	/**
	 * Loads a file containing game system information and adds details
	 * to an array. Eventually these end up in the various array list
	 * properties of <code>Global</code>.
	 * <p>
	 * Different types of files are determined by the <code>type</code>
	 * parameter. The valid <code>type</code>'s are in LstConstants.java
	 * <p>
	 * The file is opened and read. Lines are parsed by an object
	 * of the relevant type (based on <code>type</code> above), and
	 * then added to the array list.
	 *
	 * @param argFileName    name of the file to load from
	 * @param fileType    type of the file (see above for types).
	 * @param aList       <code>ArrayList</code> with existing data.
	 *                    The new data is appended to this.
	 * @return List the same list passed in, but with new data appended
	 */
	public static void loadFileIntoList(String fileName, int fileType, List aList) throws PersistenceLayerException
	{
		instance.loadFileIntoList(fileName, fileType, aList);
	}

	/**
	 * Causes the SystemLoader to check for an updated set of campaigns
	 * and update Globals.campaignList accordingly
	 *
	 * author Ryan Koppenhaver <rlkoppenhaver@yahoo.com>
	 *
	 * @see pcgen.core.Globals#getCampaignList
	 */
	public static void refreshCampaigns()
	{
		instance.refreshCampaigns();
	}
}
