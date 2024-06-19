/*
 * NameButton.java
 *
 * Created on May 2, 2003, 11:00 AM
 */

package plugin.doomsdaybook.gui;

import plugin.doomsdaybook.util.DataElement;

/**
 *
 * @author  devon
 */
public class NameButton extends javax.swing.JButton {
	DataElement element;

	/** Creates a new instance of NameButton */
	public NameButton(DataElement element) {
		this.element = element;
		super.setText(element.getTitle());
	}

	public DataElement getDataElement() {
		return element;
	}
}
