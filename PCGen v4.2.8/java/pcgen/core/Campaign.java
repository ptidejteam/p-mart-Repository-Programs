/*
 * Campaign.java
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
 */

package pcgen.core;

import java.util.Properties;
import javax.swing.JOptionPane;
import pcgen.util.GuiFacade;

/**
 * <code>Campaign</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class Campaign extends PObject
{
	private boolean isLoaded = false;
	private Integer rank = new Integer(9);
	private String game = "";
	private String infoText = "";
	private StringBuffer section15Text = null;
	private boolean showInMenu = false;
	private boolean isD20 = false;
	private boolean isOGL = false;
	private Properties options = null;

	public Object clone()
	{
		Campaign aObj = null;
		try
		{
			aObj = (Campaign) super.clone();
			aObj.isLoaded = isLoaded;
			aObj.rank = rank;
			aObj.game = game;
			aObj.infoText = infoText;
			aObj.section15Text = section15Text;
			aObj.showInMenu = showInMenu;
			aObj.isD20 = isD20;
			aObj.isOGL = isOGL;
			aObj.options = (Properties) options.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			return aObj;
		}
	}

	/**
	 * @return true if the campaign (source file set) is loaded.
	 */
	public boolean isLoaded()
	{
		return isLoaded;
	}

	/**
	 * Sets whether the campaign is loaded.
	 * @param loaded
	 */
	public void setIsLoaded(boolean loaded)
	{
		isLoaded = loaded;
	}

	/**
	 * @return the 'load rank' of the campaign.
	 */
	public Integer getRank()
	{
		return rank;
	}

	/**
	 * Sets the 'load rank' of the campaign.
	 * @param argRank the wanted load rank
	 */
	public void setRank(Integer argRank)
	{
		rank = argRank;
	}

	/**
	 * Returns the name of the game this campaign is intended for.
	 * @return the name of the game
	 */
	public String getGame()
	{
		return game;
	}

	/**
	 * Sets the name of the game this campaign is intended for.
	 * @param argGame the game
	 */
	public void setGame(String argGame)
	{
		game = argGame;
	}

	/**
	 * Returns whether this campaign should be listed in the campaigns menu
	 * @return true if this campaign should be listed in the campaigns menu
	 */
	public boolean canShowInMenu()
	{
		return showInMenu;
	}

	/**
	 * Sets whether this campaign should be listed in the campaigns menu.
	 */
	public void setShowInMenu(boolean argShow)
	{
		showInMenu = argShow;
	}

	/**
	 * @return the options which are to apply to this campaign
	 */
	public Properties getOptions()
	{
		return options;
	}

	public void setOptions(Properties options)
	{
		this.options = options;
	}

	/**
	 * @return the info on this campaign
	 */
	public String getInfoText()
	{
		return infoText;
	}

	public void setInfoText(String info)
	{
		infoText = info;
	}

	public void setIsD20(boolean argIsD20)
	{
		isD20 = argIsD20;
	}

	/**
	 * @return whether or not the d20 info will pop up when this campaign is loaded
	 */
	public boolean isD20()
	{
		return isD20;
	}

	public void setIsOGL(boolean argIsOGL)
	{
		isOGL = argIsOGL;
	}

	/**
	 * @return whether or not the OGL will pop up when this campaign is loaded
	 */
	public boolean isOGL()
	{
		return isOGL;
	}

	public void addSection15Info(String section15Info)
	{
		if (section15Text == null)
		{
			section15Text = new StringBuffer(section15Info.length() + 6);
		}
		section15Text.append(section15Info).append("<br>");
	}

	/**
	 * Returns the section 15 info for this campaign's source(book).
	 * @return the section 15 info
	 */
	public String getSection15Info()
	{
		if (section15Text != null)
		{
			return section15Text.toString();
		}
		return "";
	}
}
