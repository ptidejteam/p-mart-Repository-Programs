/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 29, 2001, 7:15 PM
 * Bryan McRoberts (merton_monk@yahoo.com)
 */
package pcgen.gui.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.gui.CharacterInfo;
import pcgen.gui.PCGen_Frame1;
import pcgen.util.Logging;
import pcgen.util.ResetableListIterator;

/**
 * <code>PObjectNode</code> -- No, binkley isn't really the author, I
 * it is Bryan, but <em>somebody</em> has got to take code ownership and
 * clean this up.  Options include replacing with
 * <code>DefaultMutableTreeNode</code> or
 * <code>JTree.DynamicUtilTreeNode</code> (radical change); or cleaning
 * up memory allocation and fixing all the spots in Info*.java which make
 * assumptions about getChildren().length, <i>etc.</i>.
 *
 * TODO: This class overrides equals(Object), but does not override hashCode(), and inherits the implementation of hashCode() from java.lang.Object (which returns the identity hash code, an arbitrary value assigned to the object by the VM).  Therefore, the class is very likely to violate the invariant that equal objects must have equal hashcodes.
 * TODO: This class implements the java.util.Iterator interface.  However, its next() method is not capable of throwing java.util.NoSuchElementException.  The next() method should be changed so it throws NoSuchElementException if is called when there are no more elements to return.
 *
 * @author B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 * @version $Revision: 1.1 $
 */

// Consider replacing ResetableListIterator with ResetableListIterator, if any
// classes look like they want to be bidirectional.

public class PObjectNode implements Cloneable, ResetableListIterator
{
	private PObjectNode parent = null;
	private Object item = null; // could be a String, could be a Feat (or anything subclassed from PObject)
	private ArrayList children = null;
	private boolean isValid = true;
	private static PlayerCharacter aPC = null;

	public static final int NOT_A_FEAT = 0;
	public static final int CAN_GAIN_FEAT = 1;
	public static final int CAN_USE_FEAT = 2;

	private int checkFeatState = NOT_A_FEAT; // feat tab

	/** Contructor for PObjectNode */
	public PObjectNode()
	{
	}

	/** Constructor for PObjectNode with an item */
	public PObjectNode(Object item)
	{
		setItem(item);
	}

	// All this is for free when we get rid of PObjectNode[] for a
	// Collection.  XXX
	private int mark = 0;

	public boolean hasNext()
	{
		return mark < getChildCount();
	}

	public boolean hasPrevious()
	{
		return mark > 0;
	}

	public Object next()
	{
		return getChild(mark++);
	}

	public Object previous()
	{
		return getChild(--mark);
	}

	public int nextIndex()
	{
		return mark;
	}

	public int previousIndex()
	{
		return mark - 1;
	}

	// XXX	Fix after switching to ArrayList
	public void add(Object obj)
	{
		throw new UnsupportedOperationException();
	}

	// XXX	Fix after switching to ArrayList
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	// XXX	Fix after switching to ArrayList
	public void set(Object obj)
	{
		throw new UnsupportedOperationException();
	}

	public void reset()
	{
		mark = 0;
	}

	// All PObjects have 2 states (qualified or not qualified), Feats have 2 extra states
	// which are only checked for the "Selected Table". This boolean controls
	// whether we are interested in these extra feats or not
	public void setCheckFeatState(int state)
	{
		checkFeatState = state;
		if (state != NOT_A_FEAT && aPC == null)
		{
			aPC = Globals.getCurrentPC();
		}
	}

	public static void resetPC()
	{
		aPC = Globals.getCurrentPC();
	}

	public Object clone()
	{
		PObjectNode retVal = null;
		try
		{
			retVal = (PObjectNode) super.clone();
			ArrayList d = null;
			if (children != null)
			{
				d = new ArrayList(children.size());
				for (Iterator it = children.iterator(); it.hasNext();)
				{
					PObjectNode node = (PObjectNode) ((PObjectNode) it.next()).clone();
					d.add(node);
				}
			}
			retVal.setChildren(d);
		}
		catch (CloneNotSupportedException exc)
		{
			Logging.errorPrint("ERROR:", exc);
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
			String itemName = ((PObject) item).piString();
			if (checkFeatState != NOT_A_FEAT)
			{
				final Feat aFeat = (Feat) item;

				switch (aFeat.getFeatType())
				{
					case Feat.FEAT_NORMAL:
						return handleCheckFeatState(aFeat, itemName);

					case Feat.FEAT_AUTOMATIC:
						return "|" + SettingsHandler.getFeatAutoColor() + "|" + itemName;

					case Feat.FEAT_VIRTUAL:
						return "|" + SettingsHandler.getFeatVirtualColor() + "|" + itemName;

					default:
						return "|" + SettingsHandler.getPrereqFailColor() + "|" + itemName;
				}

			}
			if (item instanceof Equipment)
			{
				final Equipment e = (Equipment) item;
				if (e.isAutomatic())
				{
					// Automatic Equipment uses the same color as Automatic Feats
					return "|" + SettingsHandler.getFeatAutoColor() + "|" + itemName;
				}
				if ((e.isShield() || e.isWeapon() || e.isArmor()) && !aPC.isProficientWith(e))
				{
					// indicates to LabelTreeCellRenderer to change text color
					// to a user-preference (default is red)
					Color aColor = Color.red;
					if (SettingsHandler.getPrereqFailColor() != 0)
					{
						aColor = new Color(SettingsHandler.getPrereqFailColor());
					}
					return "|" + aColor.getRGB() + "|" + itemName;
				}
				return itemName;
			}
			if (item instanceof Race)
			{
				// if we are on the Resource tab,
				// don't check race preReq stuff
				final CharacterInfo cp = PCGen_Frame1.getCharacterPane();
				if ((cp != null) && (cp.getSelectedIndex() == cp.indexOfTab("Resources")))
				{
					return itemName;
				}
			}

			if (item instanceof PCClass)
			{
				final String subClass = ((PCClass) item).getDisplayClassName();
				if (!((PCClass) item).getName().equals(subClass))
				{
					itemName = itemName + "/" + subClass;
				}
			}
			if (!(((PObject) item).passesPreReqToGain()))
			{
				// indicates to LabelTreeCellRenderer to change text color
				// to a user-preference (default is red)
				Color aColor = Color.red;
				if (SettingsHandler.getPrereqFailColor() != 0)
				{
					aColor = new Color(SettingsHandler.getPrereqFailColor());
				}
				return "|" + aColor.getRGB() + "|" + itemName;
			}
			return itemName;
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
			val.append((item).toString());		// appends feat list if any
			if (isSpecial && (spellA.getOwner() instanceof Domain))
			{
				//val.append(" [").append(spellA.getSpell().getDescriptor(", ")).append(']');
				val.append(" [").append(spellA.getOwner().getName()).append(']');
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
		if (item instanceof PlayerCharacter)
		{
			final PlayerCharacter bPC = (PlayerCharacter) item;
			return bPC.getName();
		}
		return item.toString();
	}

	private String handleCheckFeatState(final Feat aFeat, String itemName)
	{
		switch (checkFeatState)
		{
			case CAN_GAIN_FEAT:
				if (!aFeat.passesPreReqToGain())
				{
					return "|" + SettingsHandler.getPrereqFailColor() + "|" + itemName;
				}
				else
				{
					return "|" + SettingsHandler.getPrereqQualifyColor() + "|" + itemName;
				}
			case CAN_USE_FEAT:
				if (!aFeat.passesPreReqToUse())
				{
					return "|" + SettingsHandler.getPrereqFailColor() + "|" + itemName;
				}
				else
				{
					return "|" + SettingsHandler.getPrereqQualifyColor() + "|" + itemName;
				}
			default:
				Logging.errorPrint("Bad feat state: " + checkFeatState + ".  Please report this as a bug.");
				return itemName;
		}
	}

	/**
	 * Returns the parent of the receiver.
	 */
	public PObjectNode getParent()
	{
		return parent;
	}

	/**
	 *  XXX -- ugh, needs to become protected!
	 */
	public void setParent(PObjectNode aNode)
	{
		parent = aNode;
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
		{
			return ((PObject) item).getSource();
		}
		return "";
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
		if (children == null)
		{
			return true;
		}

		return children.size() == 0;
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
	public PObjectNode getChild(int i)
	{
		return (PObjectNode) children.get(i);
	}

	/**
	 * Loads the children, caching the results in the children
	 * instance variable.
	 */
	public ArrayList getChildren()
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
	public int getChildCount()
	{
		return (children == null) ? 0 : children.size();
	}

	/**
	 * Are there children?
	 */
	private boolean isChildless()
	{
		return getChildCount() == 0;
	}

	/**
	 * Sets the children of the receiver, updates the total size,
	 * and if generateEvent is true a tree structure changed event
	 * is created.
	 */
	public void setChildren(ArrayList newChildren)
	{
		if (newChildren == null)
		{
			children = null;
			reset();

			return;
		}

		children = newChildren;

		for (Iterator it = children.iterator(); it.hasNext();)
		{
			((PObjectNode) it.next()).setParent(this);
		}

		reset();
	}

	/**
	 * Sets the children of the receiver, updates the total size,
	 * and if generateEvent is true a tree structure changed event
	 * is created.
	 */
	public void setChildren(PObjectNode[] newChildren)
	{
		if (newChildren == null)
		{
			children = null;
			reset();

			return;
		}

		if (children == null)
		{
			children = new ArrayList(newChildren.length);
		}

		else
		{
			children.clear();
			children.ensureCapacity(newChildren.length);
		}

		for (int i = 0; i < newChildren.length; ++i)
		{
			newChildren[i].setParent(this);
			children.add(newChildren[i]);
		}

		reset();
	}

	/**  This adds a child, if there are existing children, the new child
	 *  gets pointed to the same parent.  The first child will have to
	 *  specifically set its own parent.
	 */
	public void addChild(PObjectNode aChild)
	{
		aChild.setParent(this);

		if (children == null)
		{
			children = new ArrayList();
		}

		children.add(aChild);
	}

	private void addChild(PObjectNode aChild, int index)
	{
		aChild.setParent(this);

		if (children == null)
		{
			children = new ArrayList();
			children.add(aChild);
		}
		else
		{
			children.add(index, aChild);
		}
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
			for (; x < children.size(); ++x)
			{
				final Object childItem = ((PObjectNode) children.get(x)).getItem();
				int comp = 1;
				if (childItem != null)
				{
					comp = itemName.compareToIgnoreCase(childItem.toString());
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
			if (x >= children.size())
			{
				addChild(aChild);
			}
		}
		return added;
	}

	/**
	 * This removes the child
	 */
	private boolean removeChild(PObjectNode aChild)
	{
		if (children == null || children.indexOf(aChild) == -1)
		{
			return false;
		}

		for (ListIterator it = children.listIterator(); it.hasNext();)
		{
			if (it.next() == aChild)
			{
				it.remove();
			}
		}

		return true;
	}

	public void removeItemFromNodes(Object e)
	{
		// if no children, remove myself and update parent
		if (isChildless() && getItem().equals(e))
		{
			getParent().removeChild(this);
		}
		else
		{
			reset();
			while (hasNext())
			{
				((PObjectNode) next()).removeItemFromNodes(e);
			}
		}
	}

	/**
	 * Retrieve the name of the node. This can be either the output name or
	 * the normal name of a PObject (depending on preferences), or for any
	 * other object it is the string representation of the object.
	 *
	 * @return String The name of the node. Null if it has no name.
	 */
	public String getNodeName()
	{
		String name = null;
		if (item instanceof PObject)
		{
			if (pcgen.core.SettingsHandler.guiUsesOutputName())
			{
				name = ((PObject) item).getOutputName();
			}
			else
			{
				name = ((PObject) item).getName();
			}
		}
		else if (item != null)
		{
			name = item.toString();
		}

		return name;
	}

}
