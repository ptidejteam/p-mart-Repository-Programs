package plugin.pcgtracker;

import gmgen.plugin.PlayerCharacterOutput;
import javax.swing.DefaultListModel;
import pcgen.core.PlayerCharacter;

public class PCGTrackerModel extends DefaultListModel {
	/**
	 * Creates an instance of a <code>PCGTrackerModel</code>.  This class holds
	 * all the characters that are loaded.
	 */
	public PCGTrackerModel() {
		super();
	}

	public void add(PlayerCharacter pc) {
		if(pc != null) {
			addElement(new LoadedPC(pc));
		}
	}

	public void remove(PlayerCharacter pc) {
		for(int i = 0; i < size(); i++) {
			LoadedPC lpc = (LoadedPC) elementAt(i);
			if(lpc.getPC() == pc) {
				removeElement(lpc);
			}
		}
	}

	public Object get(int i) {
		LoadedPC lpc = (LoadedPC) elementAt(i);
		return lpc.getPC();
	}

	public PlayerCharacter get(Object o) {
		if(contains(o)) {
			LoadedPC lpc = (LoadedPC) o;
			return lpc.getPC();
		}
		return null;
	}

	private class LoadedPC {
		private PlayerCharacter pc;
		public LoadedPC(PlayerCharacter pc) {
			this.pc = pc;
		}

		public PlayerCharacter getPC() {
			return pc;
		}

		public String toString() {
			StringBuffer outbuf = new StringBuffer();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);
			if(pc.isDirty()) {
				outbuf.append("* ");
			}
			else {
				outbuf.append("  ");
			}
			outbuf.append(pcOut.getName() + " (");
			outbuf.append(pcOut.getRace() + " ");
			outbuf.append(pcOut.getClasses() + " ");
			outbuf.append(pcOut.getGender() + ")");
			return outbuf.toString();
		}
	}
}
