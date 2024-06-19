package gombos.webbrowser;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/*
 * Created on Feb 21, 2006
 * 
 * Author: Andrew Gombos
 *
 * Main working class.  Parses a HTML document and stores the data in a HTMLStyleModel
 * 
 * Uses regexes to do the tag detection
 */

public class Parser {
	/**
	 * Read in the HTML file, and do some preprocessing     *  
	 */
	public Parser(BufferedReader r) throws ParseException {
		stream = r;
		data = new StringBuffer();
		unclosedTags = new Stack();

		readData();

		processSpacingTags();

		data = new StringBuffer(data.toString().trim());
	}

	/**
	 * Perform the actual parsing
	 */
	public HTMLStyleModel parse() throws ParseException {
		HTMLStyleModel model = new HTMLStyleModel();

		trimToBody();

		RegexGatherer rg = new RegexGatherer();
		rg.gatherMatches(data);

		int offset = 0;

		//Loop through all detected tags
		for (Iterator iter = rg.iterator(); iter.hasNext();) {
			RegexGatherer.Match m = (RegexGatherer.Match) iter.next();

			//Add the style text to the model
			//Anchors are handled inline
			if (offset != m.start) {
				model.add(
					data.substring(offset, m.start),
					getApplicableHref(),
					getApplicableTextStyles(),
					getApplicableTextColor());
			}

			//If a closing tag
			if (m.tag.charAt(0) == '/') {
				RegexGatherer.Match openingMatch =
					(RegexGatherer.Match) unclosedTags.pop();
				String openTag = openingMatch.tag.trim();

				//Check for well-formedness
				if (!openTag
					.equals(m.tag.substring(1, m.tag.length()).trim())) {
					throw new ParseException(
						"Malformed Document: Open and closing tags not equal: "
							+ openTag
							+ " & "
							+ m.tag.substring(1, m.tag.length()).trim());
				}
			}
			else {
				unclosedTags.push(m);
			}

			offset = m.end;
		}

		//Get the last chunk of the string and add it as a NORMAL style - the regexes don't match past the last tag, so
		//The last unstyled bit is left off        
		if (offset != data.length()) {
			model.add(data.substring(offset), null, NORMAL, black);
		}

		return model;
	}

	/**
	 * Get the link that matches the anchor tag 
	 */
	private String getApplicableHref() throws ParseException {
		if (unclosedTags.size() == 0)
			return null;

		//      Loop backwards, because it's a LIFO stack but the iterator treats it like a FIFO stack
		for (int i = unclosedTags.size() - 1; i >= 0; i--) {
			RegexGatherer.Match match =
				(RegexGatherer.Match) unclosedTags.elementAt(i);

			//If we have a font tag, find it's color and decode it
			if (match.tag.equalsIgnoreCase("a")) {
				//Remove any whitespace
				match.attrs = match.attrs.trim();

				//Invalid tag
				if (match.attrs.indexOf("href") == -1) {
					throw new ParseException("Malformed Document: href attribute not present in anchor");
				}

				//Handle spaces
				String href =
					match.attrs.substring(
						match.attrs.indexOf('"') + 1,
						match.attrs.lastIndexOf('"'));

				return href;
			}
		}

		//No anchor tags unclosed
		return null;
	}

	/**
	 * Determine the correct color for a piece of text.  Handles any order of color-changing elements
	 */
	private RGB getApplicableTextColor() throws ParseException {
		//The applicable color is the one that is on bottom - obeys the "inner binds tightest" rule        
		if (unclosedTags.size() == 0)
			return black;

		//Loop backwards, because it's a LIFO stack but the iterator treats it like a FIFO stack
		for (int i = unclosedTags.size() - 1; i >= 0; i--) {
			RegexGatherer.Match match =
				(RegexGatherer.Match) unclosedTags.elementAt(i);

			//Anchor is first to gain precedence over fonts
			if (match.tag.equals("a")) {
				return blue;
			}

			//If we have a font tag, find its color and decode it
			if (match.tag.equals("font")) {
				//Invalid tag                
				if (!match.attrs.matches("color[ ]*=[ ]*.*")) {
					throw new ParseException(
						"Malformed Document: font tag contains invalid color specification "
							+ match.attrs);
				}

				//Decode the color using AWT (SWT doesn't have a decoder...)
				//Handle both #RRGGBB and RRGGBB colors
				String hexEncColor = "";
				try {
					int startOffset =
						match.attrs.indexOf('#') == -1
							? match.attrs.indexOf('"')
							: match.attrs.indexOf('#');

					hexEncColor =
						match.attrs.substring(
							startOffset + 1,
							match.attrs.lastIndexOf('"'));
					Color awtColor = Color.decode("0x" + hexEncColor);

					//Return because we want the first color (see above)                
					return new RGB(
						awtColor.getRed(),
						awtColor.getGreen(),
						awtColor.getBlue());
				}
				catch (NumberFormatException nfe) {
					throw new ParseException(
						"Malformed Document: font tag contains invalid color specification "
							+ hexEncColor);
				}
			}
		}

		//If we didn't find any font tags
		return black;
	}

	/**
	 * Determine how the text needs to be styled - combine a series of flags to denote the found tags
	 */
	private int getApplicableTextStyles() {
		//Loop through the stack        
		if (unclosedTags.size() == 0)
			return NORMAL;

		int styles = 0;

		for (Iterator iter = unclosedTags.iterator(); iter.hasNext();) {
			RegexGatherer.Match match = (RegexGatherer.Match) iter.next();

			styles |= getTagType(match.tag);
		}

		return styles;
	}

	/**
	 * Translate a tag name into an integer
	 */
	private int getTagType(String tag) {
		if (tag.equals("b"))
			return BOLD;
		else if (tag.equals("u"))
			return UNDERLINE;
		else if (tag.equals("i"))
			return ITALIC;
		else if (tag.equals("font"))
			return FONT;
		if (tag.equals("a"))
			return ANCHOR;

		return -1;
	}

	/**
	 * Translate a style integer into text, for debugging 
	 */
	public static String tagTypeToString(int type) {
		String types = "";

		if ((type & BOLD) == BOLD)
			types += "BOLD ";
		if ((type & UNDERLINE) == UNDERLINE)
			types += "UNDERLINE ";
		if ((type & ITALIC) == ITALIC)
			types += "ITALIC ";
		if ((type & FONT) == FONT)
			types += "FONT ";
		if ((type & ANCHOR) == ANCHOR)
			types += "ANCHOR ";
		if ((type & NORMAL) == NORMAL)
			types += "NORMAL ";

		return types;
	}

	/**
	 * Read the input HTML into a StringBuffer
	 */
	private void readData() throws ParseException {
		char[] buffer = new char[1024];
		int bytesRead = 0;

		try {
			while (bytesRead != -1) {
				bytesRead = stream.read(buffer);

				if (bytesRead != -1)
					data.append(buffer, 0, bytesRead);
			}
		}
		catch (IOException e) {
			//Report any errors
			throw new ParseException("Unable to load document: " + e);
		}
	}

	/**
	 * Remove <html><head><body> tags and thier closings
	 * 
	 * Has a few provisions for missing closing tags 
	 */
	private void trimToBody() throws ParseException {
		//Find the beginning <html> tag
		//Also look for, and delete, a head sequence
		int headerOffset = 0;
		headerOffset = data.indexOf("<html>") + "<html>".length();
		if (data.indexOf("<head>") != -1)
			headerOffset = data.indexOf("</head>") + "</head>".length();

		data.delete(0, headerOffset);

		//Look for, and delete, the <body> tag.  Also, delete the end </body></html> sequence
		if (data.indexOf("<body>") == -1)
			throw new ParseException("Malformed Document: <body> not present");
		data.delete(0, data.indexOf("<body>") + "<body>".length());

		//Delete to the end because there isn't any data after the body close
		//Handle a missing '/' to close the body tag
		try {
			int footerOffset =
				data.indexOf("<", data.lastIndexOf("body>") - 3);
			data.delete(footerOffset, data.length());
		}
		catch (StringIndexOutOfBoundsException sioobe) {
			throw new ParseException("Malformed Document: </body></html> section format invalid or missing");
		}
	}

	/**
	 * Process <p> and <br> tags
	 * Also remove any line terminators in the original source
	 */
	private void processSpacingTags() {
		Pattern spacingTags = Pattern.compile("</?(br|p)>");
		Pattern lineTermTags = Pattern.compile("[\\r\\n]+");

		Matcher spacingMatcher = spacingTags.matcher(data);
		Matcher lineTermMatcher = lineTermTags.matcher(data);

		//Strip out line terminators
		while (lineTermMatcher.find()) {
			data.delete(lineTermMatcher.start(), lineTermMatcher.end());

			//Reset so we don't go outside the string bounds
			lineTermMatcher.reset();
		}

		//Process tags and add in new line terminators
		while (spacingMatcher.find()) {
			if (spacingMatcher.group(1).trim().equals("p"))
				data.replace(
					spacingMatcher.start(),
					spacingMatcher.end(),
					"\n\n");
			else {
				//Must be <br>, because that's all the regex matches
				data.replace(
					spacingMatcher.start(),
					spacingMatcher.end(),
					"\n");
			}

			spacingMatcher.reset();
		}
	}

	private StringBuffer data;
	private BufferedReader stream;

	private Stack unclosedTags;

	public static final RGB black = new RGB(0, 0, 0);
	public static final RGB blue = new RGB(0, 0, 255);

	/**
	 * Text style fields
	 */
	public static final int NORMAL = SWT.NORMAL;
	public static final int BOLD = SWT.BOLD;
	public static final int ITALIC = SWT.ITALIC;
	public static final int UNDERLINE = (1 << 2);
	public static final int FONT = (1 << 3);
	public static final int ANCHOR = (1 << 4);
}
