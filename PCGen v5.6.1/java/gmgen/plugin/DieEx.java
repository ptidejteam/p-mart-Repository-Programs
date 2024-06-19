package gmgen.plugin;

import java.util.Arrays;
import java.util.StringTokenizer;

/** This class does the rolling of the dice for the GMGen system.
 * @author Expires 2003
 * @version 2.10
 */
public class DieEx extends Die {
	/** Number of sides of the die that is being rolled */
	public int modifier = 0;

	/** Dice roll that is dropped */
	private int drops;

	/** Drop high roll */
	private boolean highDrop;

	/** Creates an instance of this class to vet values as a die roll.
         * @param roll Roll that needs to be made
         */
	public DieEx(String roll) {
		StringTokenizer strTok = new StringTokenizer(roll, "d ");
		String hold = "";
		num = Integer.parseInt(strTok.nextToken());
		sides = Integer.parseInt(strTok.nextToken());
		rolls = new Integer[num];
		if(strTok.hasMoreTokens()) {
			try {
				hold = strTok.nextToken();
				hold = strTok.nextToken();
			} catch(Exception e) {
				drops = 0;
			}
			try {
				drops = Integer.parseInt(hold);
			} catch(Exception e) {
				drops = 0;
			}
			try {
				hold = strTok.nextToken();
			} catch(Exception e) {
				hold = "";
			}
			if(hold.equals("lowest") || hold.equals("")) {
				highDrop = false;
			} else {
				highDrop = true;
			}
		}
	}

	/** Creates an instance of this class using the default roll */
	public DieEx() {
		this("1d6");
	}

	/** Rolls the die using the paramaters set
         * @return Value of the die rolls
         */
	public int roll() {
		total = 0;
		for(int x = 0; x < num; x++) {
			rolls[x] = new Integer((int)(Math.random() * sides)+1);
			total += rolls[x].intValue();
		}
		if(drops != 0) {
			// sort rolls first or this doesn't work.
			Arrays.sort(rolls);
			if(!highDrop) {
				for(int x=0; (x<drops && x < rolls.length); x++) {
					total -= rolls[x].intValue();
				}
			} else {
				for(int x = rolls.length - 1; x > rolls.length - drops - 1; x--) {
					total -= rolls[x].intValue();
				}
			}
		}
		timesRolled++;
		return total;
	}

	/** Method used for testing and running on it's own
         * @param args Command line arguments
         */
	public static void main (String[] args) {
		DieEx DieRoller;
		String temp = "";
		for(int x=0; x<args.length; x++) {
			temp += args[x] + " ";
		}
		DieRoller = new DieEx(temp);
		System.out.println("you rolled " + DieRoller.roll());
	}

	/** Creates a <code>String</code> representation of this class
         * @return This class as a <code>String</code>.
         */
	public String toString() {
		return Integer.toString(num) + "d" + Integer.toString(sides);
	}
}
