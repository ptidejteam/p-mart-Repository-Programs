package gombos.webbrowser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * Created on Feb 20, 2006
 * 
 *  Author: Andrew Gombos
 *
 * HTML renderer area. Calls the parser, and creates the display based on the HTMLStyleModel returned.
 * 
 * Manages hyperlink detection, and listing the hyperlinks available in each page 
 */

public class HTMLRenderer extends MouseAdapter {
	/**
	 * Class to represent a hyperlink - offset and new link
	 */
	protected class Hyperlink {
		public Hyperlink(String href, int start, int end) {
			this.href = href;
			this.start = start;
			this.end = end;
		}

		/**
		 * Return whether the hyperlink text is associated with the given text offset (ie a link is under the click) 
		 */
		public boolean encapsulates(int offset) {
			return (start <= offset) && (offset <= end);
		}

		public String toString() {
			return "HREF: " + href;
		}

		String href;
		int start, end;
	}

	public HTMLRenderer(Shell s) {
		display =
			new StyledText(
				s,
				SWT.BORDER
					| SWT.H_SCROLL
					| SWT.V_SCROLL
					| SWT.MULTI
					| SWT.READ_ONLY
					| SWT.WRAP);

		renderingFont =
			new Font(Display.getCurrent(), "Times New Roman", 14, SWT.NORMAL);
		display.setFont(renderingFont);

		createdColors = new HashMap();
		hyperlinks = new ArrayList();

		display.addMouseListener(this);
	}

	/**
	 *  Destroy any OS-level resources we allocated
	 */
	public void cleanup() {
		renderingFont.dispose();

		Set colors = createdColors.entrySet();

		for (Iterator iter = colors.iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();

			((Color) entry.getValue()).dispose();
		}
	}

	/**
	 * Detect and tell the state manager to handle clicked hyperlinks
	 */
	public void mouseUp(MouseEvent e) {
		//1 == left mouse button
		if (e.button != 1)
			return;

		//Determine the offset of the click, and compare it to a list of all current hyperlink offsets
		try {
			int offset = display.getOffsetAtLocation(new Point(e.x, e.y));

			URL newURL = null;

			for (Iterator iter = hyperlinks.iterator(); iter.hasNext();) {
				Hyperlink link = (Hyperlink) iter.next();

				//If the offset is inside this hyperlinks' range
				if (link.encapsulates(offset)) {
					newURL = new URL(stateManager.getCurrentURL(), link.href);
					break;
				}
			}

			if (newURL != null)
				stateManager.open(newURL);
		}
		catch (IllegalArgumentException iae) {
			//Ignore - means no character was at the clicked location
		}
		catch (MalformedURLException mue) {
			stateManager.invalidURL();
			display.setText("Malformed URL in link: " + mue.getMessage());
		}
	}

	/**
	 * Remove data structures used to render any current document
	 */
	public void removeCurrentDocument() {
		display.setText("");
		display.setStyleRange(null);
		hyperlinks.clear();
	}

	/**
	 *  Renders a style model, once the page is parsed
	 *  
	 *  Translates the model into StyleRanges
	 */
	public void render(HTMLStyleModel model) {
		StyleRange styles[] = new StyleRange[model.size()];

		int count = 0;

		//Clear the old page and styles
		display.setText("");
		display.setStyleRange(null);
		hyperlinks.clear();

		//Loop through all the style object created
		for (Iterator iter = model.iterator(); iter.hasNext();) {
			HTMLStyleModel.Style style = (HTMLStyleModel.Style) iter.next();

			int offset = display.getText().length();
			display.setText(display.getText() + style.text);

			//Create the style range
			StyleRange sRange = new StyleRange();
			sRange.fontStyle =
				style.fontStyle & (Parser.BOLD | Parser.ITALIC);
			//Mask off the bits like underline that aren't valid
			sRange.underline =
				((style.fontStyle & Parser.UNDERLINE) == Parser.UNDERLINE)
					|| ((style.fontStyle & Parser.ANCHOR) == Parser.ANCHOR);
			sRange.start = offset;
			sRange.length = style.text.length();
			sRange.foreground = getInstantiatedColor(style.color);

			if ((style.fontStyle & Parser.ANCHOR) == Parser.ANCHOR)
				hyperlinks.add(
					new Hyperlink(
						style.href,
						sRange.start,
						sRange.length + sRange.start));

			styles[count++] = sRange;
		}

		display.setStyleRanges(styles);
	}

	/**
	 * Parse and render a URL into the display area, or display an error message 
	 */
	public void render(URL url) {
		//Open a connection to the server to get data
		URLConnection connection = null;
		try {
			connection = url.openConnection();

			connection.connect();

			try {
				//Parse the file
				Parser p =
					new Parser(
						new BufferedReader(
							new InputStreamReader(
								connection.getInputStream())));

				HTMLStyleModel hsm = p.parse();

				render(hsm);
			}
			catch (ParseException pe) {
				//Show any error
				display.setText(pe.getMessage());

				stateManager.invalidURL();
			}
		}
		catch (FileNotFoundException fnfe) {
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection con = (HttpURLConnection) connection;
				try {
					showErrorPage(
						fnfe,
						con.getResponseCode(),
						con.getResponseMessage());
				}
				catch (IOException e) {
					//Just give an error message with no details
					showErrorPage(e, 0, "Unknown");
				}
			}
			else
				showErrorPage(fnfe, 0, "Unknown");
		}
		catch (IOException e) {
			showErrorPage(e, 0, e.getMessage());
		}
	}

	/**
	 * Return the color for a given RGB tuple - a cache for created colors
	 */
	private Color getInstantiatedColor(RGB rgb) {
		if (createdColors.containsKey(rgb))
			return (Color) createdColors.get(rgb);

		//We have to create the color
		Color newColor = new Color(Display.getCurrent(), rgb);

		createdColors.put(rgb, newColor);

		return newColor;
	}

	/**
	 * Show an error message in the rendering area - tries to give information
	 * Without being complex to call. Information is useful, if not pretty
	 */
	private void showErrorPage(
		Exception e,
		int responseCode,
		String responseMessage) {
		display.setText(
			"Error: Unable to load page. Server responded: "
				+ responseCode
				+ " "
				+ responseMessage
				+ "\nURL: "
				+ stateManager.getCurrentURL()
				+ "\n\n Java exception: "
				+ e);

		stateManager.invalidURL();
	}

	/**
	 * Internal data structures
	 */
	public StyledText display;
	private ArrayList hyperlinks;
	private Font renderingFont;
	private StateManager stateManager = StateManager.getInstance();

	private static HashMap createdColors;
}
