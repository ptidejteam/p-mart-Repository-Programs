/*
 * This file is Open Game Content, covered by the OGL.
 */
package gmgen.plugin;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.StatList;

public class SystemHP {

	private SystemAttribute attribute;
	private int max;
	private int current;
	private String state = "";
	private int regenAmount = 0;
	private int regenFrequency = 0;
	private int regenRounds = 0;
	private int subdual = 0;
	private boolean firstround = false;

	public SystemHP(SystemAttribute attribute, int hpmax, int current) {
		this.attribute = attribute;
		this.max = hpmax;
		this.current = current;
	}

	public SystemHP(SystemAttribute attribute, int hpmax) {
		this(attribute, hpmax, hpmax);
	}

	public SystemHP(int hpmax) {
		this(new SystemAttribute("Attribute", 10), hpmax, hpmax);
	}

	//public SystemHP(SystemAttribute attribute, Element hpElement) {
	//}

	public String damage(int damage) {
		if(current > -1 && (current - damage) < 0) {
			boolean dyingStart = SettingsHandler.getGMGenOption("Initiative.Damage.Dying.Start", true);
			if(!dyingStart) {
				firstround = true;
			}
		}
		current = current - damage;
		if(current == 0) {
			state = "Disabled";
		}
		else if(current < 0) {
			state = "Bleeding";
		}
		//TODO: find a way for this to be PreferencesPanel.DAMAGE_DEATH_NEG_TEN, not '1'
		int deathType = SettingsHandler.getGMGenOption("Initiative.Damage.Death", 1);
		if(deathType == 1) {
			if(current <= -10) {
				state = "Dead";
				current = 0;
			}
		}
		//TODO: find a way for this to be PreferencesPanel.DAMAGE_DEATH_NEG_CON, not '2'
		else if(deathType == 2) {
			if(current <= (-1 * attribute.getValue())) {
				state = "Dead";
				current = 0;
			}
		}
		checkSubdual();
		return state;
	}

	public String subdualDamage(int damage) {
		subdual = subdual + damage;
		return checkSubdual();
	}

	public String nonLethalDamage(boolean type) {
		if(state.equals("")) {
			if(type) {
				state = "Unconsious";
			}
			else {
				state = "Dazed";
			}
		}
		return state;
	}

	public String endDurationedStatus() {
		if(state.equals("Unconsious") || state.equals("Dazed")) {
			state = "";
		}
		return state;
	}

	private String checkSubdual() {
		if((state.equals("") || state.equals("Staggered") || state.equals("Unconsious")) && subdual > 0) {
			if(subdual == current) {
				state = "Staggered";
			}
			else if(subdual > current) {
				state = "Unconsious";
			}
		}
		return state;
	}

	public void endRound() {
		firstround = false;
	}

	public String heal(int heal) {
		if(!state.equals("Dead")) {
			current = current + heal;
			subdual = subdual - heal;
			if(current > max) {
				current = max;
			}
			if(subdual < 0) {
				subdual = 0;
			}
			if(state.equals("Bleeding")) {
				state = "Stable";
			}
			if(current > 0) {
				state = "";
			}
			checkSubdual();
		}
		return state;
	}

	public String stabilize() {
		if(state.equals("Bleeding")) {
			state = "Stable";
		}
		return state;
	}

	public String raise() {
		if(state.equals("Dead")) {
			state = "";
			current = 1;
		}
		return state;
	}

	public String bleed() {
		if(state.equals("Bleeding") && !firstround) {
			damage(1);
		}
		return state;
	}

	public String kill() {
		state = "Dead";
		current = 0;
		return state;
	}

	public SystemAttribute getAttribute() {
		return attribute;
	}

	public int getMax() {
		return max;
	}

	public int getCurrent() {
		return current;
	}

	public int getSubdual() {
		return subdual;
	}

	public String getState() {
		return state;
	}

	public void setAttribute(SystemAttribute attribute) {
		this.attribute = attribute;
	}

	public void setMax(int hpmax) {
		this.max = hpmax;
		if(max > current) {
			current = max;
		}
	}

	public void setCurrent(int current) {
		if(current > max) {
			current = max;
		}

		if(current > this.current) {
			heal(current - this.current);
		}
		else if(current < this.current) {
			damage(this.current - current);
		}
	}

	public void setSubdual(int subdual) {
		subdualDamage(subdual - getSubdual());
	}

	public void setState(String state) {
		this.state = state;
	}

	public static boolean isDndMassive(Combatant cbt, int damage) {
		int damageThreshold = 50;
		if(SettingsHandler.getGMGenOption("Initiative.Damage.Massive.SizeMod", true)) {
			if (cbt instanceof PcgCombatant) {
				PcgCombatant pcgcbt = (PcgCombatant)cbt;
				PlayerCharacter pc = pcgcbt.getPC();
				String size = pc.getSize();

				//FIX: This needs to be moved to pcgen's sizeAdjustment.lst
				if(size.equals("Fine")) {
					damageThreshold = 10;
				}
				if(size.equals("Diminutive")) {
					damageThreshold = 20;
				}
				if(size.equals("Tiny")) {
					damageThreshold = 30;
				}
				if(size.equals("Small")) {
					damageThreshold = 40;
				}
				//Medium 50
				if(size.equals("Large")) {
					damageThreshold = 60;
				}
				if(size.equals("Huge")) {
					damageThreshold = 70;
				}
				if(size.equals("Gargantuan")) {
					damageThreshold = 80;
				}
				if(size.equals("Colossal")) {
					damageThreshold = 90;
				}
			}
		}
		if(damage >= damageThreshold) {
			return true;
		}
		return false;
	}

	public static boolean isD20ModernMassive(Combatant cbt, int damage) {
		if (cbt instanceof PcgCombatant) {
			PcgCombatant pcgcbt = (PcgCombatant)cbt;
			PlayerCharacter pc = pcgcbt.getPC();
			Globals.setCurrentPC(pc);
			StatList sl = pc.getStatList();
			if(damage > sl.getTotalStatFor("CON")) {
				return true;
			}
		}
		else {
			if(damage > cbt.getHP().getAttribute().getValue()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isHouseHalfMassive(Combatant cbt, int damage) {
		SystemHP hp = cbt.getHP();
		if(damage > hp.getMax()) {
			return true;
		}
		return false;
	}
}
