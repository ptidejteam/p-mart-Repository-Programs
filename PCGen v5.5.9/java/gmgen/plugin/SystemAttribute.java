/*
 * This file is Open Game Content, covered by the OGL.
 */
package gmgen.plugin;


public class SystemAttribute {

	private String name;
	private int value;
	private String description;
	private SystemDie die;

	public SystemAttribute(String name, int value, String description, SystemDie die) {
		this.name = name;
		this.value = value;
		this.description = description;
		this.die = die;
	}

	public SystemAttribute(String name, int value, SystemDie die) {
		this(name, value, "", die);
	}

	public SystemAttribute(String name, int value, String description) {
		this(name, value, description, new SystemDie());
	}

	public SystemAttribute(String name, int value) {
		this(name, value, "", new SystemDie());
	}

	public int check() {
		return check(0);
	}

	public int check(int mod) {
		return die.roll() + this.getModifier() + mod;
	}

	public boolean difficultyCheck(int difficulty) {
		return difficultyCheck(difficulty, 0);
	}

	public boolean difficultyCheck(int difficulty, int mod) {
		int check = check(mod);
		return check >= difficulty;
	}

	public String getDescription() {
		return description;
	}

	public int getModifier() {
		return (value / 2) - 5;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setDie(SystemDie die) {
			this.die = die;
	}
}
