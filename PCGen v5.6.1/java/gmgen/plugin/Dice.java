/*
 *  Dice.java
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 */
package gmgen.plugin;


/** A normal die
 * @author Soulcatcher
 * @created May 24, 2003
 */
public class Dice extends Die {
	/** Die modifier */
	private int modifier = 0;


	/** Constructor for the Dice object
         * @param num Number of dice
         * @param sides Number of sides
         * @param modifier Modifier to the die roll
         */
	public Dice(int num, int sides, int modifier) {
		this.num = num;
		this.sides = sides;
		this.modifier = modifier;
		rolls = new Integer[num];
		roll();
	}


	/** Constructor for the Dice object
         * @param num Number of dice
         * @param sides Number of sides per die
         */
	public Dice(int num, int sides) {
		this(num, sides, 0);
	}


	/** Rolls the die, and returns the result
         * @return Result of the die roll
         */
	public int roll() {
		int value = 0;
		int i;
		total = 0;
		for (i = 0; i < num; i++) {
			rolls[i] = new Integer(rand.nextInt(sides) + 1);
			value = rolls[i].intValue() + value;
		}
		total = value + modifier;
		timesRolled++;
		return total;
	}


	/** Name of the die in the nds+m format
         * @return Name of the die
         */
	public String toString() {
		if (modifier == 0) {
			return num + "d" + sides;
		} else {
			return num + "d" + sides + "+" + modifier;
		}
	}
}

