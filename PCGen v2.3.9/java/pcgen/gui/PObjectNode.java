/**
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
 * Created on December 29, 2001, 7:15 PM
 * Bryan McRoberts (merton_monk@yahoo.com)
 */
package pcgen.gui;

import java.awt.Color;
import java.util.Iterator;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

class PObjectNode
{
	PObjectNode parent = null;
	Object item = null; // could be a String, could be a Feat (or anything subclassed from PObject)
	PObjectNode[] children = new PObjectNode[0]; // array of children
	boolean isValid = true;
	static private PlayerCharacter aPC = null;
	private boolean checkFeatState = false; // currently used only for Feat tab

	/** Contructor for PObjectNode */
	protected PObjectNode()
	{
	}

	// All PObjects have 2 states (qualified or not qualified), Feats have 2 extra states
	// which are only checked for the "Selected Table". This boolean controls
	// whether we are interested in these extra feats or not
	public void setCheckFeatState(boolean aBool)
	{
		checkFeatState = aBool;
		if (aBool == true && aPC == null)
			aPC = Globals.getCurrentPC();
	}

	public String toString()
	{
		if (item == null)
			return "";
		if (item instanceof PObject)
		{
			if (checkFeatState)
			{
				// if the PC has selected/gained the feat, return it's name
				if (aPC.hasFeat(((PObject)item).getName()))
				{
					return item.toString();
				}
				// if the PC has the feat as an automatic feat,
				// return the name with a color state to tell
				// LabelTreeCellRenderer what color to use for its text
				if (aPC.hasFeatAutomatic(item.toString()))
				{
					// Currently hard-coded to dark yellow, this should
					// be made into a user-preference
					final Color aColor = Color.yellow;
					return "|" + aColor.darker().getRGB() + "|" + item.toString();
				}
				// The only remaining option is that the feat is a virtual feat
				// Again, this is hard-coded and the color should be user-preference
				final Color aColor = Color.magenta;
				return "|" + aColor.getRGB() + "|" + item.toString();
			}
			if (!(((PObject)item).passesPreReqTests()))
			{
				// indicates to LabelTreeCellRenderer to change background color
				// currently set to red, should be changed to a user-preference
				final Color aColor = Color.red;
				return "|" + aColor.getRGB() + "|" + item.toString();
			}
			else
			{
				return item.toString();
			}
		}
		return item.toString();
	}

	/**
	 * Returns the parent of the receiver.
	 */
	public PObjectNode getParent()
	{
		return parent;
	}

	public void setParent(PObjectNode aNode)
	{
		parent = aNode;
	}

	public String getPreReqs()
	{
		if (item != null && (item instanceof PObject))
			return ((PObject)item).preReqStrings();
		return "";
	}

	// Returns a String with the (currently only for Feats) choices in a
	// comma-delimited list.  Otherwise returns an empty string.
	public String getChoices()
	{
		if (item != null && (item instanceof Feat))
		{
			final Feat aFeat = (Feat)item;
			if (aFeat.isMultiples())
			{
				String aString = "";
				boolean addComma = false;
				for (Iterator i = aFeat.getAssociatedList().iterator(); i.hasNext();)
				{
					if (addComma)
						aString = aString + ",";
					else
						addComma = true;
					aString = aString + i.next().toString();
				}
				return aString;
			}
			else
				return "";
		}
		return "";
	}

	// Sets the object (Could be a String or something subclassed from PObject)
	public void setItem(Object anItem)
	{
		item = anItem;
	}

	// Gets the object (Could be a String or something subclassed from PObject)
	public Object getItem()
	{
		return item;
	}

	// Gets the object's Source if it's a PObject, otherwise returns an empty string
	public String getSource()
	{
		if (item instanceof PObject)
			return ((PObject)item).getSource();
		return "";
	}

	public boolean isValid()
	{
		return isValid;
	}

	public void setIsValid(boolean aBool)
	{
		isValid = aBool;
	}

	/**
	 * Returns true if the receiver represents a leaf, that is it is
	 * isn't a directory.
	 */
	public boolean isLeaf()
	{
		return children.length == 0;
	}

	/**
	 * Loads the children, caching the results in the children
	 * instance variable.
	 */
	protected PObjectNode[] getChildren()
	{
		return children;
	}

	/**
	 * Sets the children of the receiver, updates the total size,
	 * and if generateEvent is true a tree structure changed event
	 * is created.
	 */

	protected void setChildren(PObjectNode[] newChildren, boolean generateEvent)
	{
		children = newChildren;
	}

	/*  This adds a child, if there are existing children, the new child
	 *  gets pointed to the same parent.  The first child will have to
	 *  specifically set its own parent.
	 */
	public void addChild(PObjectNode aChild)
	{
		PObjectNode[] c = new PObjectNode[children.length + 1];
		for (int i = 0; i < children.length; i++)
			c[i] = children[i];
		if (children.length > 0)
			aChild.setParent(children[0].getParent());
		c[children.length] = aChild;
		setChildren(c, false);
	}

}
