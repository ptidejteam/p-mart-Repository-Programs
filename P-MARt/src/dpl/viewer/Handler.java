/*
 * (c) Copyright 2001-2004 Yann-Gaël Guéhéneuc,
 * University of Montréal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package dpl.viewer;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import dpl.XMLConstants;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since  2004/04/21
 */
public class Handler extends DefaultHandler {
	private static final int DEFAULT = 0;
	private static final int PROGRAM = 1;
	private static final int PROGRAMNAME = 2;
	private static final int ACTOR = 3;
	private static final int ACTORNAME = 4;
	private static final int COMMENT = 5;

	private final StringBuffer comment = new StringBuffer();
	private int state = Handler.DEFAULT;
	private String previousRole;

	public void setDocumentLocator(final Locator locator) {
	}
	public void startDocument() throws SAXException {
	}
	public void endDocument() throws SAXException {
	}
	public void startPrefixMapping(final String prefix, final String uri)
			throws SAXException {
	}
	public void endPrefixMapping(final String prefix) throws SAXException {
	}
	public void startElement(
		final String namespaceURI,
		final String localName,
		final String rawName,
		final Attributes atts) throws SAXException {

		if (rawName.equals(XMLConstants.DESIGN_PATTERN)) {
			System.err.print("Design pattern: ");
			System.out.println(atts.getValue(0));
		}
		else if (rawName.equals(XMLConstants.PROGRAM)) {
			System.err.print(atts.getValue(0));
			System.err.print(" program: ");
			this.state = Handler.PROGRAM;
		}
		else if (rawName.equals(XMLConstants.ROLES)) {
			this.state = Handler.ACTOR;
		}
		else if (rawName.equals(XMLConstants.NAME)) {
			if (this.state == Handler.PROGRAM) {
				this.state = Handler.PROGRAMNAME;
			}
			else if (this.state == Handler.ACTOR) {
				this.state = Handler.ACTORNAME;
			}
		}
		else if (rawName.equals(XMLConstants.MICRO_ARCHITECTURE)) {
			System.err.print("Micro-architecture: ");
			System.out.println(atts.getValue(0));
		}
		else if (rawName.equals(XMLConstants.COMMENT)) {
			this.state = Handler.COMMENT;
		}
		else {
			this.previousRole = rawName;
		}
	}
	public void endElement(
		final String namespaceURI,
		final String localName,
		final String rawName) throws SAXException {

		if (rawName.equals(XMLConstants.COMMENT)) {
			System.out.print("Comment: ");
			System.out.println(this.comment);
			this.comment.setLength(0);
			this.state = Handler.DEFAULT;
		}
	}
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {

		if (this.state == Handler.PROGRAMNAME) {
			System.out.println(new String(ch, start, length));
			this.state = Handler.PROGRAM;
		}
		else if (this.state == Handler.ACTORNAME) {
			System.out.print(this.previousRole);
			System.out.print(": ");
			System.out.println(new String(ch, start, length));
			this.state = Handler.ACTOR;
		}
		else if (this.state == Handler.COMMENT) {
			this.comment.append(new String(ch, start, length)
				.replace('\n', ' ')
				.trim());
		}
	}
	public void ignorableWhitespace(
		final char[] ch,
		final int start,
		final int length) throws SAXException {
	}
	public void processingInstruction(final String target, final String data)
			throws SAXException {
	}
	public void skippedEntity(final String name) throws SAXException {
	}
}
