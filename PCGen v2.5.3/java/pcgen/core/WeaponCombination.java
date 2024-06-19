/**
 * WeaponCombination.java
 * Copyright 2001 (C) Thomas G. W. Epperly <tomepperly@home.com>
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
 * Created on May 1, 2001, 9:45 PM
 */

package pcgen.core;

/**
 * An object to store a collection of weapons/armor that the character
 * may at times use simultaneously. For examples, sometimes a
 * character may choose to fight with a long sword in their primary
 * hand and a shield in the secondary hand. In other situations, the
 * character may prefer to fight with two daggers.  In other
 * situations, the character fights with a long bow using both hands.
 * Each of these possibilities could be presented as a
 * WeaponCombination.
 *
 * A WeaponCombination can be listed or not. Being listed means that
 * it should appear on the character sheet.
 *
 * A WeaponCombination can be equipped or not.  Being equipped means
 * that the character is currently using or uses by default this
 * WeaponCombination.
 *
 * @author Tom Epperly <tepperly@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class WeaponCombination
	extends PObject
{
	/* private class data */

	private static final String[] s_handNames = {
		"Primary",
		"Secondary",
		"Teritary",
		"Quaternary"
	};

	/**
	 * This has an entry for each hand (primary=0, secondary=1,
	 * tertiary=2, etc.). It may be possible to equip several items on/in
	 * one hand.
	 *
	 * Assuming the character has at least one hand, d_hands[0] is a
	 * <code>EquipmentGroup</code> . d_hands[0].getChild(0) is an
	 * <code>Equipment</code> object.
	 *
	 * The number of hands can be determined by taking the length of
	 * d_hands.
	 *
	 * @see pcgen.core.Equipment */
	private EquipmentGroup[] d_hands;

	/**
	 * This stores whether the weapon combination should be listed on
	 * the character sheet. The user may wish to have combinations that
	 * aren't printed normally.
	 */
	private boolean d_listed = true;

	/**
	 * Only one weapon combination can be equipped at a time.  This is
	 * the weapon combination that the character is currently or
	 * normally expects to use.  When this weapon combination is
	 * equipped, <code>d_equipped</code> is <code>true</code>.
	 */
	private boolean d_equipped = false;

	/**
	 * Create a new weapon combination for a creature with the given
	 * number of hands.  The weapon combination starts out with no
	 * equipment assigned to each hand.
	 *
	 * @param numHands the number of hands that the creature has
	 *                 (normally 2). A negative value is treated as
	 *                 zero.
	 */
	public WeaponCombination(int numHands)
	{
		numHands = Math.max(numHands, 0);
		d_hands = new EquipmentGroup[numHands];
		for (int i = 0; i < numHands; ++i)
		{
			final String name =
				((i < s_handNames.length)
				? s_handNames[i]
				: (Integer.toString(i) + "'th")) + " hand";
			d_hands[i] = new EquipmentGroup(name, Equipment.class);
		}
	}

	/**
	 * Set the listed property of the
	 * WeaponCombination. <code>true</code> means the WeaponCombination
	 * should be listed on the character sheet.
	 *
	 * @param listed  the new value of the listed property
	 */
	public void setListed(boolean listed)
	{
		d_listed = listed;
	}

	/**
	 * Return the current value of the listed
	 * property. <code>true</code> means the weapon combination should
	 * be listed on the character sheet.
	 *
	 * @return the value of the listed property
	 */
	public boolean isListed()
	{
		return d_listed;
	}

	/**
	 * Change the equipped property to the setting given.  Only one
	 * combination should be equipped at a time; this is the client
	 * responsibility. This method does not attempt to manage other
	 * weapon combinations.
	 *
	 * @param equipped <code>true</code> means the weapon combination is
	 *                 currently being used or should be considered the
	 *                 default on the character sheet.
	 */
	public void setEquipped(boolean equipped)
	{
		d_equipped = equipped;
	}

	/**
	 * Return the current equipped property of the weapon combination.
	 * A value of <code>true</code> means that this weapon combination
	 * is currently in use or should be considered the default when
	 * writing the character sheet.
	 *
	 * @return <code>true</code> means the weapon is equipped.
	 */
	public boolean isEquipped()
	{
		return d_equipped;
	}

	/**
	 * Return the number of hands available in this weapon combination.
	 *
	 * @return the number of hands in the weapon combination.
	 */
	public int getNumHands()
	{
		return d_hands.length;
	}

	/**
	 * Return the total number of items equipped in this weapon
	 * combination.
	 *
	 * @return the total number of items equipped in this combination
	 */
	public int getNumItems()
	{
		int result = 0;
		for (int i = 0; i < d_hands.length; ++i)
			result += d_hands[i].getChildCount();
		return result;
	}

	/**
	 * Return the weapons equipped in the indicated hand.  This returns
	 * the <code>EquipmentGroup</code> used by the object. If the client
	 * makes changes to this <code>EquipmentGroup</code>, it will change
	 * the state of this object.  If the client wished to avoid making
	 * changes to this object, the client should clone the
	 * <code>EquipmentGroup</code>.
	 *
	 * @param hand the number of the hand whose list of
	 *             <code>Equipment</code> objects will be returned.
	 * @return the <code>EquipmentGroup</code> of <code>Equipment</code> held
	 *         or affixed to <code>hand</code>.
	 * @exception ArrayIndexOutOfBoundsException this is thrown if
	 * the <code>hand</code> is an illegal index.  */
	public EquipmentGroup getEquipment(int hand)
	{
		return d_hands[hand];
	}

	/**
	 * Set the list of items that are equipped in/on a particular hand.
	 * This will replace the current contents of that hand.  This will
	 * use the <code>EquipmentGroup</code> given (i.e. it does not make
	 * a clone of the <code>EquipmentGroup</code>).
	 *
	 * @param hand      the number of the hand whose list of
	 *                  <code>Equipment</code> objects will be replaced.
	 * @param equipment the list of items to associate with
	 *                  <code>hand</code>. <code>null</code> is not
	 *                  allowed here.
	 * @exception ArrayIndexOutOfBoundsException this is thrown if
	 * the <code>hand</code> is an illegal index.
	 */
	public void setEquipment(int hand, EquipmentGroup equipment)
	{
		d_hands[hand] = equipment;
	}
}
