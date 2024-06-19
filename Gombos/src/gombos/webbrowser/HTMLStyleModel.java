package gombos.webbrowser;
import java.util.ArrayList;

import org.eclipse.swt.graphics.RGB;

/*
 * Created on Feb 20, 2006
 * 
 * Author: Andrew Gombos
 *
 * Contains information about the style content of a HTML page
 * 
 * More an organizational wrapper for an ArrayList
 */

public class HTMLStyleModel extends ArrayList {
	//Text must be added in the order it will be displayed
	//This is how my parser works, so everything is ok
	public void add(String text, String href, int fontStyle, RGB color) {
		Style s = new Style();

		s.text = text;
		s.href = href;
		s.fontStyle = fontStyle;
		s.color = color;

		add(s);
	}

	/**
	 * Container for a style - includes all possible spec'ed attributes 
	 */
	protected class Style {
		public String text;
		public String href;
		//Only makes sense when (fontSyle & ANCHOR) == ANCHOR
		public int fontStyle;
		public RGB color;

		public String toString() {
			return text
				+ ": "
				+ Parser.tagTypeToString(fontStyle)
				+ color
				+ "<"
				+ href
				+ ">";
		}
	}
}
