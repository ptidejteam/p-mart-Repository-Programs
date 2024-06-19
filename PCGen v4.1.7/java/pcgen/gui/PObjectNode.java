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
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;

/**
 * <code>PortraitChooser</code>
 *
 * @author ???
 * @version $Revision: 1.1 $
 */

final class PObjectNode implements Cloneable
{
	private PObjectNode parent = null;
	private Object item = null; // could be a String, could be a Feat (or anything subclassed from PObject)
	private PObjectNode[] children = null;
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
		if (aBool && aPC == null)
			aPC = Globals.getCurrentPC();
	}

	public static void resetPC()
	{
		aPC = Globals.getCurrentPC();
	}

	public Object clone()
	{
		Object retVal = null;
		try
		{
			retVal = super.clone();
			PObjectNode[] d = null;
			if (children != null)
			{
				d = new PObjectNode[children.length];
				for (int i = 0; i < children.length; i++)
				{
					d[i] = (PObjectNode) children[i].clone();
					d[i].setParent((PObjectNode) retVal);
				}
			}
			((PObjectNode) retVal).setChildren(d, false);
		}
		catch (CloneNotSupportedException exc)
		{
			Globals.errorPrint("ERROR:", exc);
		}
		return retVal;
	}

	public String toString()
	{
		if (item == null)
		{
			return "";
		}
		if (item instanceof PObject)
		{
			if (checkFeatState)
			{
				final Feat aFeat = (Feat) item;

				switch (aFeat.getFeatType())
				{
					case Feat.FEAT_NORMAL:
						return ((PObject) item).piString();

					case Feat.FEAT_AUTOMATIC:
						return "|" + SettingsHandler.getFeatAutoColor() + "|" + ((PObject) item).piString();

					case Feat.FEAT_VIRTUAL:
						return "|" + SettingsHandler.getFeatVirtualColor() + "|" + ((PObject) item).piString();

					default:
						return "|" + Color.red.getRGB() + "|" + ((PObject) item).piString();
				}

			}
			if (item instanceof Equipment)
			{
				final Equipment e = (Equipment) item;
				if ((e.isShield() || e.isWeapon() || e.isArmor()) &&
					!aPC.isProficientWith(e))
				{
					// indicates to LabelTreeCellRenderer to change text color
					// to a user-preference (default is red)
					Color aColor = Color.red;
					if (SettingsHandler.getPrereqFailColor() != 0)
					{
						aColor = new Color(SettingsHandler.getPrereqFailColor());
					}
					return "|" + aColor.getRGB() + "|" + ((PObject) item).piString();
				}
				return ((PObject) item).piString();
			}
			if (item instanceof Race)
			{
				// if we are on the Resource tab,
				// don't check race preReq stuff
				final CharacterInfo cp = Globals.getRootFrame().getCharacterPane();
				if ((cp != null) && (cp.getSelectedIndex() == cp.indexOfTab("Resources")))
					return ((PObject) item).piString();
			}
			if (!(((PObject) item).passesPreReqTests()))
			{
				// indicates to LabelTreeCellRenderer to change text color
				// to a user-preference (default is red)
				Color aColor = Color.red;
				if (SettingsHandler.getPrereqFailColor() != 0)
					aColor = new Color(SettingsHandler.getPrereqFailColor());
				return "|" + aColor.getRGB() + "|" + ((PObject) item).piString();
			}
			return ((PObject) item).piString();
		}
		if (item instanceof SpellInfo)
		{
			final CharacterSpell spellA = ((SpellInfo) item).getOwner();
			final boolean isSpecial = spellA.isSpecialtySpell();
			final int times = ((SpellInfo) item).getTimes();

			final StringBuffer val = new StringBuffer(80);

			// first, set the name
			val.append(spellA.getSpell().piSubString());	// gets name of spell
			// now tack on any extra crap such as domains, etc
			val.append(((SpellInfo) item).toString());		// appends feat list if any
			if (isSpecial && (spellA.getOwner() instanceof Domain))
			{
				val.append(" [").append(spellA.getSpell().getDescriptor(", ")).append(']');
			}

			// finaly add on the number of times
			if (times > 1)
			{
				val.append(" (").append(((SpellInfo) item).getTimes()).append(')');
			}
			//
			// Only wrap in html if might contain html
			// html messes up the display when using Java 1.3--causes the spell names to disappear
			//
			if (val.toString().indexOf('<') >= 0)
			{
				val.insert(0, "<html>");
				val.append("</html>");
			}
			return val.toString();
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
			return ((PObject) item).preReqStrings();
		return "";
	}

	// Returns a String with the (currently only for Feats) choices in a
	// comma-delimited list.  Otherwise returns an empty string.
	public String getChoices()
	{
		final StringBuffer aString = new StringBuffer();
		if (item != null && (item instanceof Feat))
		{
			final Feat aFeat = (Feat) item;
			if (aFeat.isMultiples())
			{
				boolean addComma = false;
				for (int i = 0; i < aFeat.getAssociatedCount(true); i++)
				{
					if (addComma)
					{
						aString.append(',');
					}
					else
					{
						addComma = true;
					}
					aString.append(aFeat.getAssociated(i, true));
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
			return ((PObject) item).getSource();
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
		if (children == null) return true;
		return children.length == 0;
	}

	/**
	 * Get the child of an arbritrary node
	 */
	public static PObjectNode getChild(Object parent, int i)
	{
		final PObjectNode parentAsPObjectNode = (PObjectNode) parent;
		return parentAsPObjectNode.getChild(i);
	}

	/**
	 * Get the child
	 */
	protected PObjectNode getChild(int i)
	{
		return children[i];
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
	 * Gets the number of children
	 */
	public static int getChildCount(Object parent)
	{
		final PObjectNode parentAsPObjectNode = (PObjectNode) parent;
		return parentAsPObjectNode.getChildCount();
	}

	/**
	 * Gets the number of children
	 */
	protected int getChildCount()
	{
		return (children == null) ? 0 : children.length;
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

	/**  This adds a child, if there are existing children, the new child
	 *  gets pointed to the same parent.  The first child will have to
	 *  specifically set its own parent.
	 */
	public void addChild(PObjectNode aChild)
	{
		aChild.setParent(this);

		PObjectNode[] c;
		if (children == null)
		{
			c = new PObjectNode[1];
		}
		else
		{
			c = new PObjectNode[children.length + 1];
			for (int i = 0; i < children.length; i++)
			{
				c[i] = children[i];
			}
		}
		c[c.length - 1] = aChild;

		setChildren(c, false);
	}

	public void addChild(PObjectNode aChild, int index)
	{
		aChild.setParent(this);
		PObjectNode[] c;
		if (children == null)
		{
			c = new PObjectNode[1];
			c[0] = aChild;
		}
		else
		{
			c = new PObjectNode[children.length + 1];
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
		if (children == null) return;
		if (children.length == 1)
		{
			children = null;
			return;
		}
		int x = 0;
		final PObjectNode[] c = new PObjectNode[children.length - 1];
		for (int i = 0; i < c.length; i++)
		{
			if (x == 0 && children[i] == aChild)
			{
				x = 1;
			}
			c[i] = (PObjectNode) children[i + x].clone();
		}
		setChildren(c, false);
	}
}
