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
package dpl.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import padl.util.Util;
import util.xml.DOMVisitor;
import dpl.XMLConstants;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since 2004/05/05
 */
public class TabVisitor implements DOMVisitor {
	private static final String CLASS = "Class";
	private static final String ROLE = "Role";
	private static final String SEPARATOR = "\t\t";
	private static final String ZERO = "0.0";

	private static final int DEFAULT = 0;
	private static final int ACTOR_STRUCTURE = 1;
	private static final int PROPERTY = 2;
	private static final int PROPERTY_NAME = 3;
	private static final int DESIGN_PATTERN = 4;
	private static final int MICRO_ARCHITECTURE = 5;
	private static final int ACTOR_NAME = 6;
	private static final int PROPERTIES = 7;

	private final Writer writer;
	private final List propertyList;
	private int state;
	private String designPatternName;
	private String actorRole;
	private String previousProperty;

	public TabVisitor(final Writer writer) throws IOException {
		this.writer = writer;
		this.propertyList = new ArrayList();
	}

	public void open(final Document aDocument) {
		this.state = TabVisitor.DEFAULT;
		try {
			this.writer.write(TabVisitor.CLASS);
			this.writer.write(TabVisitor.SEPARATOR);
		}
		catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void close(final Document aDocument) {
		try {
			this.writer.close();
		}
		catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void open(final Node aNode) {
		try {
			switch (this.state) {
				case TabVisitor.DEFAULT :
					if (aNode.getNodeName().equals(XMLConstants.ROLE_STRUCTURE)) {

						this.state = TabVisitor.ACTOR_STRUCTURE;
					}
					else if (aNode.getNodeName().equals(
						XMLConstants.DESIGN_PATTERN)) {

						this.designPatternName =
							aNode
								.getAttributes()
								.getNamedItem(XMLConstants.NAME)
								.getNodeValue();
						this.state = TabVisitor.DESIGN_PATTERN;
					}
					break;
				case TabVisitor.ACTOR_STRUCTURE :
					if (aNode.getNodeName().equals(XMLConstants.PROPERTY)) {
						this.state = TabVisitor.PROPERTY;
					}
					break;
				case TabVisitor.PROPERTY :
					if (aNode.getNodeName().equals(XMLConstants.NAME)) {
						this.state = TabVisitor.PROPERTY_NAME;
					}
					break;
				case TabVisitor.PROPERTY_NAME :
					if (aNode.getNodeName().equals(XMLConstants.TEXT)) {
						this.propertyList.add(aNode.getNodeValue());
						this.writer.write(aNode.getNodeValue());
						this.writer.write(TabVisitor.SEPARATOR);
					}
					break;
				case TabVisitor.DESIGN_PATTERN :
					if (aNode.getNodeName().equals(
						XMLConstants.MICRO_ARCHITECTURE)) {

						this.state = TabVisitor.MICRO_ARCHITECTURE;
					}
					break;
				case TabVisitor.MICRO_ARCHITECTURE :
					if (aNode.getNodeName().equals(XMLConstants.NAME)) {
						this.state = TabVisitor.ACTOR_NAME;
					}
					else if (aNode
						.getNodeName()
						.equals(XMLConstants.PROPERTIES)) {
						this.state = TabVisitor.PROPERTIES;
					}
					else if (!aNode.getNodeName().equals(XMLConstants.TEXT)) {
						this.actorRole = aNode.getNodeName();
					}
					break;
				case TabVisitor.ACTOR_NAME :
					if (aNode.getNodeName().equals(XMLConstants.TEXT)) {
						this.writer.write(aNode.getNodeValue());
						this.writer.write(TabVisitor.SEPARATOR);
					}
					break;
				case TabVisitor.PROPERTIES :
					if (!aNode.getNodeName().equals(XMLConstants.TEXT)) {
						int shift;
						if (this.previousProperty != null) {
							shift =
								this.propertyList.indexOf(aNode.getNodeName())
										- this.propertyList
											.indexOf(this.previousProperty) - 1;
						}
						else {
							shift =
								this.propertyList.indexOf(aNode.getNodeName());
						}
						for (int i = 0; i < shift; i++) {
							this.writer.write(TabVisitor.ZERO);
							this.writer.write(TabVisitor.SEPARATOR);
						}
						this.writer.write(aNode
							.getAttributes()
							.getNamedItem(XMLConstants.VALUE)
							.getNodeValue());
						this.previousProperty = aNode.getNodeName();
						this.writer.write(TabVisitor.SEPARATOR);
					}
					break;
			}
		}
		catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void close(final Node aNode) {
		try {
			switch (this.state) {
				case TabVisitor.ACTOR_STRUCTURE :
					if (aNode.getNodeName().equals(XMLConstants.ROLE_STRUCTURE)) {

						this.writer.write(TabVisitor.ROLE);
						this.writer.write('\n');
						this.writer.flush();
						this.state = TabVisitor.DEFAULT;
					}
					break;
				case TabVisitor.PROPERTY :
					if (aNode.getNodeName().equals(XMLConstants.PROPERTY)) {
						this.state = TabVisitor.ACTOR_STRUCTURE;
					}
					break;
				case TabVisitor.PROPERTY_NAME :
					if (aNode.getNodeName().equals(XMLConstants.NAME)) {
						this.state = TabVisitor.PROPERTY;
					}
					break;
				case TabVisitor.DESIGN_PATTERN :
					if (aNode.getNodeName().equals(XMLConstants.DESIGN_PATTERN)) {

						this.state = TabVisitor.DEFAULT;
					}
					break;
				case TabVisitor.MICRO_ARCHITECTURE :
					if (aNode.getNodeName().equals(
						XMLConstants.MICRO_ARCHITECTURE)) {

						this.state = TabVisitor.DESIGN_PATTERN;
					}
					break;
				case TabVisitor.ACTOR_NAME :
					if (aNode.getNodeName().equals(XMLConstants.NAME)) {
						this.state = TabVisitor.MICRO_ARCHITECTURE;
					}
					break;
				case TabVisitor.PROPERTIES :
					if (aNode.getNodeName().equals(XMLConstants.PROPERTIES)) {
						// Yann 2004/05/05: Zeros!
						// I don't forget to write trailing zeros in
						// case the last written property wasn't the
						// last possible property.
						int shift;
						if (this.previousProperty != null) {
							shift =
								this.propertyList.size()
										- this.propertyList
											.indexOf(this.previousProperty) - 1;
						}
						else {
							shift = this.propertyList.size();
						}
						for (int i = 0; i < shift; i++) {
							this.writer.write(TabVisitor.ZERO);
							this.writer.write(TabVisitor.SEPARATOR);
						}

						// this.writer.write(this.className);
						// this.writer.write(TabVisitor.SEPARATOR);
						this.writer.write(this.designPatternName);
						this.writer.write('-');
						this.writer
							.write(Util.capitalizeFirstLetter(this.actorRole
								.toCharArray()));
						this.writer.write('\n');
						this.previousProperty = null;
						this.state = TabVisitor.MICRO_ARCHITECTURE;
					}
					break;
			}
		}
		catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
