/*
GNU Lesser General Public License

ExtendedHTMLEditorKit
Copyright (C) 2001-2002  Frits Jalvingh & Howard Kistler

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package gmgen.gui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
  * This class extends HTMLEditorKit so that it can provide other renderer classes
  * instead of the defaults. Most important is the part which renders relative
  * image paths.
  *
  * @author <a href="mailto:jal@grimor.com">Frits Jalvingh</a>
  * @version 1.0
  */

public class ExtendedHTMLEditorKit extends HTMLEditorKit {
	/** Constructor
	  */
	public ExtendedHTMLEditorKit() {
	}

	/** Method for returning a ViewFactory which handles the image rendering.
	  */
	public ViewFactory getViewFactory() {
		return new HTMLFactoryExtended();
	}

/* WACKY GERMAN CODE */
	public Document createDefaultDocument() {
		StyleSheet styles = getStyleSheet();
		StyleSheet ss = new StyleSheet();
		ss.addStyleSheet(styles);
		ExtendedHTMLDocument doc = new ExtendedHTMLDocument(ss);
		doc.setParser(getParser());
		doc.setAsynchronousLoadPriority(4);
		doc.setTokenThreshold(100);
		return doc;
	}

	public static boolean checkParentsTag(Element e, HTML.Tag tag) {
		if(e.getName().equalsIgnoreCase(tag.toString())) {
			return true;
		}
		do {
			if((e = e.getParentElement()).getName().equalsIgnoreCase(tag.toString())) {
				return true;
			}
		} while(!(e.getName().equalsIgnoreCase("html")));
		return false;
	}

	/**
	 * Fetch a resource relative to the HTMLEditorKit classfile.
	 * If this is called on 1.2 the loading will occur under the
	 * protection of a doPrivileged call to allow the HTMLEditorKit
	 * to function when used in an applet.
	 *
	 * @param name the name of the resource, relative to the
	 *  HTMLEditorKit class
	 * @return a stream representing the resource
	 */
	static InputStream getResourceAsStream(String name) {
		return ExtendedHTMLEditorKit.class.getResourceAsStream(name);
		/*try {
			return ResourceLoader.getResourceAsStream(name);
		} catch (Throwable e) {
			// If the class doesn't exist or we have some other
			// problem we just try to call getResourceAsStream directly.
			return ExtendedHTMLEditorKit.class.getResourceAsStream(name);
		}*/
	}

	public static void delete(JTextPane pane) throws BadLocationException, IOException {
		ExtendedHTMLDocument htmlDoc = (ExtendedHTMLDocument)pane.getStyledDocument();
		int selStart = pane.getSelectionStart();
		int selEnd = pane.getSelectionEnd();
		String[] posStrings = getUniqueString(2, pane.getText());
		if(posStrings == null) {
			return;
		}
		htmlDoc.insertString(selStart, posStrings[0], null);
		htmlDoc.insertString(selEnd + posStrings[0].length(), posStrings[1], null);
		int start = pane.getText().indexOf(posStrings[0]);
		int end = pane.getText().indexOf(posStrings[1]);
		if(start == -1 || end == -1) {
			return;
		}
		String htmlString = pane.getText().substring(0,start);
		htmlString += pane.getText().substring(start + posStrings[0].length(), end);
		htmlString += pane.getText().substring(end + posStrings[1].length(), pane.getText().length());
		String source = htmlString;
		end -= posStrings[0].length();
		htmlString = source.substring(0,start);
		htmlString += getAllTableTags(source.substring(start, end));
		htmlString += source.substring(end, source.length());
		pane.setText(htmlString);
	}

	private static String getAllTableTags(String source) throws BadLocationException, IOException {
		StringBuffer result = new StringBuffer();
		int caret = -1;
		do {
			caret++;
			int[] tableCarets = new int[6];
			tableCarets[0] = source.indexOf("<table",caret);
			tableCarets[1] = source.indexOf("<tr",caret);
			tableCarets[2] = source.indexOf("<td",caret);
			tableCarets[3] = source.indexOf("</table",caret);
			tableCarets[4] = source.indexOf("</tr",caret);
			tableCarets[5] = source.indexOf("</td",caret);
			java.util.Arrays.sort(tableCarets);
			caret = -1;
			for(int i = 0; i < tableCarets.length; i++) {
				if(tableCarets[i] >= 0) {
					caret = tableCarets[i];
					break;
				}
			}
			if(caret != -1) {
				result.append(source.substring(caret,source.indexOf(">",caret)+1));
			}
		} while(caret != -1);
		return result.toString();
	}

	public static String[] getUniqueString(int strings, String source) {
		String[] result = new String[strings];
		for(int i = 0; i < strings; i++) {
			int start = -1, end = -1;
			boolean hit = false;
			String idString;
			int counter = 0;
			do {
				hit = false;
				idString = "diesisteineidzumsuchen" + counter + "#" + i;
				if(source.indexOf(idString) > -1) {
					counter++;
					hit = true;
					if(counter > 10000) {
						return null;
					}
				}
			} while(hit);
			result[i] = idString;
		}
		return result;
	}

	public static Element getListItemParent(Element eleSearch) {
		String listItemTag = HTML.Tag.LI.toString();
		do {
			if(listItemTag.equals(eleSearch.getName())) {
				return eleSearch;
			}
			eleSearch = eleSearch.getParentElement();
		} while(
			!((eleSearch.getName()).equals(HTML.Tag.HTML.toString())));
		return null;
	}

	public static void removeTag(JTextPane pane, Element element, boolean closingTag) {
		if(element == null) {
			return;
		}
		int pos = pane.getCaretPosition();
		HTML.Tag tag = getHTMLTag(element);
		// Versieht den Tag mit einer einmaligen ID
		String source = pane.getText();
		boolean hit;
		String idString;
		int counter = 0;
		do {
			hit = false;
			idString = "diesisteineidzumsuchenimsource" + counter;
			if(source.indexOf(idString) > -1) {
				counter++;
				hit = true;
				if(counter > 10000) {
					return;
				}
			}
		} while(hit);
		SimpleAttributeSet sa = new SimpleAttributeSet(element.getAttributes());
		sa.addAttribute("id", idString);
		((ExtendedHTMLDocument)pane.getStyledDocument()).replaceAttributes(element, sa, tag);
		source = pane.getText();
		StringBuffer newHtmlString = new StringBuffer();
		int[] position = getPositions(element, source, closingTag, idString);
		if(position == null) {
			return;
		}
		for(int i = 0; i < position.length; i++) {
			if(position[i] < 0) {
				return;
			}
		}
		int beginStartTag = position[0];
		int endStartTag = position[1];
		if(closingTag) {
			int beginEndTag = position[2];
			int endEndTag = position[3];
			newHtmlString.append(source.substring(0, beginStartTag));
			newHtmlString.append(source.substring(endStartTag, beginEndTag));
			newHtmlString.append(source.substring(endEndTag, source.length()));
		}
		else {
			newHtmlString.append(source.substring(0, beginStartTag));
			newHtmlString.append(source.substring(endStartTag, source.length()));
		}
		pane.setText(newHtmlString.toString());
	}

	public static void insertListElement(JTextPane pane, String content) {
		int pos = pane.getCaretPosition();
		ExtendedHTMLDocument htmlDoc = (ExtendedHTMLDocument)pane.getStyledDocument();
		String source = pane.getText();
		boolean hit;
		String idString;
		int counter = 0;
		do {
			hit = false;
			idString = "diesisteineidzumsuchenimsource" + counter;
			if(source.indexOf(idString) > -1) {
				counter++;
				hit = true;
				if(counter > 10000) {
					return;
				}
			}
		} while(hit);
		Element element = getListItemParent(htmlDoc.getCharacterElement(pane.getCaretPosition()));
		if(element == null) {
			return;
		}
		SimpleAttributeSet sa = new SimpleAttributeSet(element.getAttributes());
		sa.addAttribute("id", idString);
		((ExtendedHTMLDocument)pane.getStyledDocument()).replaceAttributes(element, sa, HTML.Tag.LI);
		source = pane.getText();
		StringBuffer newHtmlString = new StringBuffer();
		int[] positions = getPositions(element, source, true, idString);
		newHtmlString.append(source.substring(0, positions[3]));
		newHtmlString.append("<li>");
		newHtmlString.append(content);
		newHtmlString.append("</li>");
		newHtmlString.append(source.substring(positions[3] + 1, source.length()));
		pane.setText(newHtmlString.toString());
		pane.setCaretPosition(pos - 1);
		element = getListItemParent(htmlDoc.getCharacterElement(pane.getCaretPosition()));
		sa = new SimpleAttributeSet(element.getAttributes());
		sa = removeAttributeByKey(sa, "id");
		((ExtendedHTMLDocument)pane.getStyledDocument()).replaceAttributes(element, sa, HTML.Tag.LI);
	}

	public static SimpleAttributeSet removeAttributeByKey(SimpleAttributeSet sourceAS, String removeKey) {
		SimpleAttributeSet temp = new SimpleAttributeSet();
		temp.addAttribute(removeKey, "NULL");
		return removeAttribute(sourceAS, temp);
	}

	public static SimpleAttributeSet removeAttribute(SimpleAttributeSet sourceAS, SimpleAttributeSet removeAS) {
		try {
			String[] sourceKeys = new String[sourceAS.getAttributeCount()];
			String[] sourceValues = new String[sourceAS.getAttributeCount()];
			Enumeration sourceEn = sourceAS.getAttributeNames();
			int i = 0;
			while(sourceEn.hasMoreElements()) {
				Object temp;
				temp = sourceEn.nextElement();
				sourceKeys[i] = temp.toString();
				sourceValues[i] = new String();
				sourceValues[i] = sourceAS.getAttribute(temp).toString();
				i++;
			}
			String[] removeKeys = new String[removeAS.getAttributeCount()];
			String[] removeValues = new String[removeAS.getAttributeCount()];
			Enumeration removeEn = removeAS.getAttributeNames();
			int j = 0;
			while(removeEn.hasMoreElements()) {
				removeKeys[j] = removeEn.nextElement().toString();
				removeValues[j] = removeAS.getAttribute(removeKeys[j]).toString();
				j++;
			}
			SimpleAttributeSet result = new SimpleAttributeSet();
			boolean hit;
			for(int countSource = 0; countSource < sourceKeys.length; countSource++) {
				hit = false;
				//TODO Are you absolutely sure you want bitwise or here?
				if("name".equals(sourceKeys[countSource]) | "resolver".equals(sourceKeys[countSource])) {
					hit = true;
				}
				else {
					for(int countRemove = 0; countRemove < removeKeys.length; countRemove++) {
						if(removeKeys[countRemove] != "NULL") {
							if(sourceKeys[countSource].equals(removeKeys[countRemove])) {
								if(removeValues[countRemove] != "NULL") {
									if(sourceValues[countSource].equals(removeValues[countRemove])) {
										hit = true;
									}
								}
								else if("NULL".equals(removeValues[countRemove])) {
									hit = true;
								}
							}
						}
						else if("NULL".equals(removeKeys[countRemove])) {
							if(sourceValues[countSource].equals(removeValues[countRemove])) {
								hit = true;
							}
						}
					}
				}
				if(!hit) {
					result.addAttribute(sourceKeys[countSource], sourceValues[countSource]);
				}
			}
			return result;
		}
		catch (ClassCastException cce) {
			return null;
		}
	}

	private static int[] getPositions(Element element, String source, boolean closingTag, String idString) {
		HTML.Tag tag = getHTMLTag(element);
		int[] position = new int[4];
		for(int i = 0; i < position.length; i++) {
			position[i] = -1;
		}
		String searchString = "<" + tag.toString();
		int caret;
		if((caret = source.indexOf(idString)) != -1) {
			position[0] = source.lastIndexOf("<",caret);
			position[1] = source.indexOf(">",caret)+1;
		}
		if(closingTag) {
			String searchEndTagString = "</" + tag.toString() + ">";
			int hitUp;
			int beginEndTag;
			int endEndTag;
			caret = position[1];
			boolean end;
			beginEndTag = source.indexOf(searchEndTagString, caret);
			endEndTag = beginEndTag + searchEndTagString.length();
			int interncaret = position[1];
			do {
				int temphitpoint;
				boolean flaghitup;
				hitUp = 0;
				do {
					flaghitup = false;
					temphitpoint = source.indexOf(searchString, interncaret);
					if(temphitpoint > 0 && temphitpoint < beginEndTag) {
						hitUp++;
						flaghitup = true;
						interncaret = temphitpoint + searchString.length();
					}
				} while(flaghitup);
				if(hitUp == 0) {
					end = true;
				}
				else {
					for(int i = 1; i <= hitUp; i++) {
						caret = endEndTag;
						beginEndTag = source.indexOf(searchEndTagString, caret);
						endEndTag = beginEndTag + searchEndTagString.length();
					}
					end = false;
				}
			} while(!end);
			if(beginEndTag < 0 | endEndTag < 0) {
				return null;
			}
			position[2] = beginEndTag;
			position[3] = endEndTag;
		}
		return position;
	}

	public static HTML.Tag getHTMLTag(Element e) {
		//Set List of tags
		Hashtable tags = new Hashtable();
		HTML.Tag[] tagList = HTML.getAllTags();
		for(int i = 0; i < tagList.length; i++) {
			tags.put(tagList[i].toString(), tagList[i]);
		}

		//Get Tag
		if(tags.containsKey(e.getName())) {
			return (HTML.Tag)tags.get(e.getName());
		}
		else {
			return null;
		}
	}

/* Inner Classes --------------------------------------------- */

	/** Class that replaces the default ViewFactory and supports
	  * the proper rendering of both URL-based and local images.
	  */
	public static class HTMLFactoryExtended extends HTMLFactory implements ViewFactory {
		/** Constructor
		  */
		public HTMLFactoryExtended() {
		}

		/** Method to handle IMG tags and
		  * invoke the image loader.
		  */
		public View create(Element elem) {
			Object obj = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
			if(obj instanceof HTML.Tag) {
				HTML.Tag tagType = (HTML.Tag)obj;
				if(tagType == HTML.Tag.IMG) {
					return new RelativeImageView(elem);
				}
				else if ((tagType == HTML.Tag.UL) || (tagType == HTML.Tag.OL)) {
					//return new ListView(elem);
				}
			}
			return super.create(elem);
		}
	}

	public static class InsertListAction extends InsertHTMLTextAction {
		private HTML.Tag baseTag;

		public InsertListAction(String label, HTML.Tag listType) {
			super(label, "", listType, HTML.Tag.LI);
			baseTag = listType;
		}

		public void actionPerformed(ActionEvent ae) {
			try {
				JEditorPane editor = getEditor(ae);
				ExtendedHTMLDocument doc = (ExtendedHTMLDocument)editor.getDocument();
				String selTextBase = editor.getSelectedText();
				Element elem = doc.getParagraphElement(editor.getCaretPosition());
				int textLength = -1;
				if(selTextBase != null) {
					textLength = selTextBase.length();
				}
				if(selTextBase == null || textLength < 1) {
					int pos = editor.getCaretPosition();
					//parentEkit.setCaretPosition(pos);
					if(ae.getActionCommand() != "newListPoint") {
						if(checkParentsTag(elem, HTML.Tag.OL) || checkParentsTag(elem, HTML.Tag.UL)) {
							//Can't have a multilevel list
							return;
						}
					}
					String sListType = (baseTag == HTML.Tag.OL ? "ol" : "ul");
					StringBuffer sbNew = new StringBuffer();
					if(checkParentsTag(elem, baseTag)) {
						sbNew.append("<li></li>");
						insertHTML(editor, doc, editor.getCaretPosition(), sbNew.toString(), 0, 0, HTML.Tag.LI);
					}
					else {
						sbNew.append("<" + sListType + "><li></li></" + sListType + "><p>&nbsp;</p>");
						insertHTML(editor, doc, editor.getCaretPosition(), sbNew.toString(), 0, 0, (sListType.equals("ol") ? HTML.Tag.OL : HTML.Tag.UL));
					}
				}
				else {
					String sListType = (baseTag == HTML.Tag.OL ? "ol" : "ul");
					HTMLDocument htmlDoc = (HTMLDocument)(editor.getDocument());
					int iStart = editor.getSelectionStart();
					int iEnd = editor.getSelectionEnd();
					String selText = htmlDoc.getText(iStart, iEnd - iStart);
					StringBuffer sbNew = new StringBuffer();
					String sToken = ((selText.indexOf("\r") > -1) ? "\r" : "\n");
					StringTokenizer stTokenizer = new StringTokenizer(selText, sToken);
					sbNew.append("<" + sListType + ">");
					while(stTokenizer.hasMoreTokens()) {
						sbNew.append("<li>");
						sbNew.append(stTokenizer.nextToken());
						sbNew.append("</li>");
					}
					sbNew.append("</" + sListType + "><p>&nbsp;</p>");
					htmlDoc.remove(iStart, iEnd - iStart);
					insertHTML(editor, htmlDoc, iStart, sbNew.toString(), 1, 1, null);
				}
				//Refresh
			}
			catch (BadLocationException ble) {
			}
		}
	}
}
