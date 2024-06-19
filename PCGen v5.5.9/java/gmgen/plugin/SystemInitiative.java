/*
 * This file is Open Game Content, covered by the OGL.
 */
package gmgen.plugin;


public class SystemInitiative {

	protected SystemAttribute attribute;
	protected int bonus;
	protected int currentInitiative = 0;
	protected Dice die;
	protected int roll;
	protected int mod = 0;

	public SystemInitiative(SystemAttribute attribute, int bonus) {
		this.attribute = attribute;
		this.bonus = bonus;
		die = new Dice(1, 20);
	}

	public SystemInitiative(SystemAttribute attribute) {
		this(attribute, 0);
	}

	public SystemInitiative(int bonus) {
		this(new SystemAttribute("Attribute", 10), bonus);
	}

	public SystemInitiative() {
		this(new SystemAttribute("Attribute", 10), 0);
	}

	public int check() {
		return check(0);
	}

	public int check(int mod) {
		roll = die.roll();
		this.mod = mod;
		setCurrentInitiative(roll + getModifier() + mod);
		return currentInitiative;
	}

	public int checkExtRoll(int roll) {
		return checkExtRoll(roll, 0);
	}

	public int checkExtRoll(int roll, int mod) {
		this.roll = roll;
		this.mod = mod;
		setCurrentInitiative(roll + getModifier() + mod);
		return currentInitiative;
	}

	public int refocus() {
		return checkExtRoll(20, 0);
	}

	public int refocus(int mod) {
		return checkExtRoll(20, mod);
	}

	public SystemAttribute getAttribute() {
		return attribute;
	}

	public int getBonus() {
		return bonus;
	}

	public int getModifier() {
		return attribute.getModifier() + bonus;
	}

	public int getCurrentInitiative() {
		return currentInitiative;
	}

	public void setAttribute(SystemAttribute attribute) {
		this.attribute = attribute;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus - attribute.getModifier();
		setCurrentInitiative(roll + getModifier() + mod);
	}

	public void setCurrentInitiative(int currentInitiative) {
		if(currentInitiative >= 1) {
			this.currentInitiative = currentInitiative;
		}
		else {
			this.currentInitiative = 1;
		}
	}
}
