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

import java.awt.*;
import java.util.Iterator;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

class PObjectNode implements Cloneable
{
	private PObjectNode parent = null;
	private Object item = null; // could be a String, could be a Feat (or anything subclassed from PObject)
	private PObjectNode[] children = new PObjectNode[0]; // array of children
	private boolean isValid = true;
	private static PlayerCharacter aPC = null;
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

	public void resetPC()
	{
		aPC = Globals.getCurrentPC();
	}

	public Object clone()
	{
		Object retVal = null;
		try
		{
			retVal = super.clone();
			PObjectNode[] d = new PObjectNode[children.length];
			for (int i = 0; i < children.length; i++)
			{
				d[i] = (PObjectNode)children[i].clone();
				d[i].setParent((PObjectNode)retVal);
			}
			((PObjectNode)retVal).setChildren(d, false);
		}
		catch (Exception exc)
		{
			System.err.println("ERROR:" + exc.getMessage());
		}
		return retVal;
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
				// indicates to LabelTreeCellRenderer to change text color
				// to a user-preference (default is red)
				Color aColor = Color.red;
				if (Globals.getPrereqFailColor()!=0)
					aColor = new Color(Globals.getPrereqFailColor());
				return "|" + aColor.getRGB() + "|" + item.toString();
			}
			if (item instanceof Equipment)
			{
				Equipment e = (Equipment)item;
				if ((e.isType("Shield") || e.isType("Weapon") || e.isType("Armor")) &&
					!aPC.isProficientWith(e))
				{
					// indicates to LabelTreeCellRenderer to change text color
					// to a user-preference (default is red)
					Color aColor = Color.red;
					if (Globals.getPrereqFailColor()!=0)
						aColor = new Color(Globals.getPrereqFailColor());
					return "|" + aColor.getRGB() + "|" + item.toString();
				}
				return item.toString();
			}
			else
			{
				return item.toString();
			}
		}
		return item.toString();
	}

	public int compareTo(PObjectNode o)
	{
		return this.toString().compareTo(o.toString());
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
		StringBuffer aString = new StringBuffer();
		if (item != null && (item instanceof Feat))
		{
			final Feat aFeat = (Feat)item;
			if (aFeat.isMultiples())
			{
				boolean addComma = false;
				for (Iterator i = aFeat.getAssociatedList().iterator(); i.hasNext();)
				{
					if (addComma)
						aString.append(",");
					else
						addComma = true;
					aString.append(i.next().toString());
				}
			}
		}
		return aString.toString();
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
		if (children.length > 0)
		{
			aChild.setParent(children[0].getParent());
		}

		PObjectNode[] c = new PObjectNode[children.length + 1];
		for (int i = 0; i < children.length; i++)
		{
			c[i] = children[i];
		}
		c[children.length] = aChild;

		setChildren(c, false);
	}

	public void addChild(PObjectNode aChild, int index)
	{
		if (children.length > 0)
		{
			aChild.setParent(children[0].getParent());
		}

		PObjectNode[] c = new PObjectNode[children.length + 1];
		c[children.length] = aChild;
		int idx = 0;
		for (int i = 0; i < children.length; i++)
		{
			if (index == i)
			{
				c[idx++] = aChild;
			}
			c[idx++] = children[i];
		}

		setChildren(c, false);
	}

	public boolean addChild(PObjectNode aChild, boolean sort)
	{
		boolean added = true;
		if (!sort || (children == null) || (aChild.item == null))
		{
			addChild(aChild);
		}
		else
		{
			final String itemName = aChild.item.toString();
			int x = 0;
			for (; x < children.length; x++)
			{
				final Object childItem = children[x].getItem();
				int comp = 1;
				if (childItem != null)
				{
					comp = itemName.compareTo(childItem.toString());
				}
				if (comp == 0)
				{
					added = false;
					break;
				}
				else if (comp < 0)
				{
					addChild(aChild, x);
					break;
				}
			}
			if (x >= children.length)
			{
				addChild(aChild);
			}
		}
		return added;
	}

	/**
	 * This removes the child
	 */
	public void removeChild(PObjectNode aChild)
	{
		int x = 0;
		PObjectNode c[] = new PObjectNode[children.length - 1];
		for (int i = 0; i < c.length; i++)
		{
			if (x == 0 && children[i] == aChild)
			{
				x = 1;
			}
			c[i] = (PObjectNode)children[i + x].clone();
		}
		setChildren(c, false);
	}

}
