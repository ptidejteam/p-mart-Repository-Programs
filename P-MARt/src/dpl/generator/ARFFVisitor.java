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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import padl.util.Util;
import util.xml.DOMVisitor;
import dpl.ARFFConstants;
import dpl.XMLConstants;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since 2004/05/06
 * 
 *        The output of the ARFF file decomposes in two steps because of this
 *        particular file format: - The data are output, the list of possible
 *        roles is empty. - The output is modified to write the appropriate list
 *        of possible roles.
 */
public class ARFFVisitor implements DOMVisitor {
	private static final String RELATION_NAME = "Metrics-Roles";

	private static final String SEPARATOR = ",";
	private static final String ZERO = "0.0";

	private static final int DEFAULT = 0;
	private static final int ACTOR_STRUCTURE = 1;
	private static final int PROPERTY = 2;
	private static final int PROPERTY_NAME = 3;
	private static final int DESIGN_PATTERN = 4;
	private static final int MICRO_ARCHITECTURE = 5;
	private static final int ACTOR_NAME = 6;
	private static final int PROPERTIES = 7;

	private static final Set setOfDesiredAttributes;
	static {
		setOfDesiredAttributes = new HashSet();
		setOfDesiredAttributes.add("ACAIC");
		setOfDesiredAttributes.add("ACMIC");
		setOfDesiredAttributes.add("CBO");
		setOfDesiredAttributes.add("CLD");
		setOfDesiredAttributes.add("connectivity");
		setOfDesiredAttributes.add("DCAEC");
		setOfDesiredAttributes.add("DCMEC");
		setOfDesiredAttributes.add("DIT");
		setOfDesiredAttributes.add("LCOM5");
		setOfDesiredAttributes.add("NOC");
		setOfDesiredAttributes.add("NMA");
		setOfDesiredAttributes.add("NMI");
		setOfDesiredAttributes.add("NMO");
		setOfDesiredAttributes.add("NCM");
		setOfDesiredAttributes.add("WMC");
	}

	private final Writer dataWriter;
	private final Writer roleKindWriter;
	private final StringBuffer dataOutputBuffer;
	private final List attributeList;
	private int state;
	private String designPatternName;
	private String actorRole;
	private String entityKind;
	private String roleKind;
	private final Map entityRoleKindMap;
	private final Map roleRoleKindMap;
	private String previousAttribute;
	private int rolePlaceHolder;

	public ARFFVisitor(final Writer dataWriter, final Writer roleKindWriter)
			throws IOException {

		this.dataWriter = dataWriter;
		this.roleKindWriter = roleKindWriter;
		this.dataOutputBuffer = new StringBuffer();
		this.attributeList = new ArrayList();
		this.entityRoleKindMap = new HashMap();
		this.roleRoleKindMap = new HashMap();
	}

	public void open(final Document aDocument) {
		this.state = ARFFVisitor.DEFAULT;
		this.dataOutputBuffer.append(ARFFConstants.RELATION);
		this.dataOutputBuffer.append(' ');
		this.dataOutputBuffer.append(ARFFVisitor.RELATION_NAME);
		this.dataOutputBuffer.append("\n\n");
	}

	private boolean isDesiredAttribute(final String name) {
		final Iterator iterator = ARFFVisitor.setOfDesiredAttributes.iterator();
		while (iterator.hasNext()) {
			if (name.equals((String) iterator.next())) {
				return true;
			}
		}
		return false;
	}

	public void close(final Document aDocument) {
		try {
			// Yann 2004/05/06: ARFF file format!
			// Because of the file format, I must
			// now "reopen" the output and modify
			// it to add the list of roles.
			final StringBuffer buffer = new StringBuffer();
			Iterator iterator = this.entityRoleKindMap.keySet().iterator();
			while (iterator.hasNext()) {
				buffer.append(iterator.next());
				if (iterator.hasNext()) {
					buffer.append(',');
				}
			}
			this.dataOutputBuffer.insert(
				this.rolePlaceHolder,
				buffer.toString());

			this.dataWriter.write(this.dataOutputBuffer.toString());
			this.dataWriter.close();

			// Yann 2004/05/14: Be kinds!
			// I want to store for each role, the
			// expected kind of the entity
			// (either Class or AbstractClass) and
			// the kind of the entities playing this
			// role (either Class, AbstractClass...).
			iterator = this.roleRoleKindMap.keySet().iterator();
			while (iterator.hasNext()) {
				final String role = (String) iterator.next();
				// No space in keys!
				final String roleToWrite = role.replace(' ', '_');
				this.roleKindWriter.write(roleToWrite);
				this.roleKindWriter.write('.');
				this.roleKindWriter.write(ARFFConstants.KIND_EXPECTED);
				this.roleKindWriter.write(" = ");
				this.roleKindWriter.write((String) this.roleRoleKindMap
					.get(role));
				this.roleKindWriter.write('\n');
				this.roleKindWriter.write(roleToWrite);
				this.roleKindWriter.write('.');
				this.roleKindWriter.write(ARFFConstants.KIND_ACTUAL);
				this.roleKindWriter.write(" = ");
				this.roleKindWriter.write((String) this.entityRoleKindMap
					.get(role));
				this.roleKindWriter.write("\n\n");
			}
			this.roleKindWriter.close();
		}
		catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void open(final Node aNode) {
		switch (this.state) {
			case ARFFVisitor.DEFAULT :
				if (aNode.getNodeName().equals(XMLConstants.ROLE_STRUCTURE)) {

					this.state = ARFFVisitor.ACTOR_STRUCTURE;
				}
				else if (aNode
					.getNodeName()
					.equals(XMLConstants.DESIGN_PATTERN)) {

					this.designPatternName =
						aNode
							.getAttributes()
							.getNamedItem(XMLConstants.NAME)
							.getNodeValue();
					this.state = ARFFVisitor.DESIGN_PATTERN;
				}
				break;
			case ARFFVisitor.ACTOR_STRUCTURE :
				if (aNode.getNodeName().equals(XMLConstants.PROPERTY)) {
					this.state = ARFFVisitor.PROPERTY;
				}
				break;
			case ARFFVisitor.PROPERTY :
				if (aNode.getNodeName().equals(XMLConstants.NAME)) {
					this.state = ARFFVisitor.PROPERTY_NAME;
				}
				break;
			case ARFFVisitor.PROPERTY_NAME :
				if (aNode.getNodeName().equals(XMLConstants.TEXT)) {
					final String attributeName = aNode.getNodeValue();
					if (this.isDesiredAttribute(attributeName)) {
						this.attributeList.add(attributeName);
						this.dataOutputBuffer.append(ARFFConstants.ATTRIBUTE);
						this.dataOutputBuffer.append(' ');
						this.dataOutputBuffer.append(attributeName);
						this.dataOutputBuffer.append(' ');
						this.dataOutputBuffer.append(ARFFConstants.REAL);
						this.dataOutputBuffer.append('\n');
					}
				}
				break;
			case ARFFVisitor.DESIGN_PATTERN :
				if (aNode.getNodeName().equals(XMLConstants.MICRO_ARCHITECTURE)) {

					this.state = ARFFVisitor.MICRO_ARCHITECTURE;
				}
				break;
			case ARFFVisitor.MICRO_ARCHITECTURE :
				if (aNode.getNodeName().equals(XMLConstants.ENTITY)) {
					this.state = ARFFVisitor.ACTOR_NAME;
				}
				else if (aNode.getNodeName().equals(XMLConstants.PROPERTIES)) {
					this.state = ARFFVisitor.PROPERTIES;
				}
				break;
			case ARFFVisitor.ACTOR_NAME :
				this.actorRole =
					aNode.getParentNode().getParentNode().getNodeName();
				this.entityKind =
					aNode
						.getParentNode()
						.getAttributes()
						.getNamedItem(XMLConstants.KIND)
						.getNodeValue();
				this.roleKind =
					aNode
						.getParentNode()
						.getParentNode()
						.getAttributes()
						.getNamedItem(XMLConstants.ROLE_KIND)
						.getNodeValue();
				break;
			case ARFFVisitor.PROPERTIES :
				final String attributeName = aNode.getNodeName();
				if (this.isDesiredAttribute(attributeName)) {
					int shift;
					if (this.previousAttribute != null) {
						shift =
							this.attributeList.indexOf(aNode.getNodeName())
									- this.attributeList
										.indexOf(this.previousAttribute) - 1;
					}
					else {
						shift = this.attributeList.indexOf(aNode.getNodeName());
					}
					for (int i = 0; i < shift; i++) {
						this.dataOutputBuffer.append(ARFFVisitor.ZERO);
						this.dataOutputBuffer.append(ARFFVisitor.SEPARATOR);
					}
					this.dataOutputBuffer.append(aNode
						.getAttributes()
						.getNamedItem(XMLConstants.VALUE)
						.getNodeValue());
					this.previousAttribute = aNode.getNodeName();
					this.dataOutputBuffer.append(ARFFVisitor.SEPARATOR);
				}
				break;
		}
	}

	public void close(final Node aNode) {
		switch (this.state) {
			case ARFFVisitor.ACTOR_STRUCTURE :
				if (aNode.getNodeName().equals(XMLConstants.ROLE_STRUCTURE)) {

					// Yann 2004/05/06: Position!
					// I must write out the header of the file
					// in one shot because I need to record
					// where the list of roles begins to later
					// insert the roles when the document closes.
					this.dataOutputBuffer.append(ARFFConstants.ATTRIBUTE);
					this.dataOutputBuffer.append(' ');
					this.dataOutputBuffer.append(ARFFConstants.ROLES);
					this.dataOutputBuffer.append(" {");
					this.rolePlaceHolder = this.dataOutputBuffer.length();
					this.dataOutputBuffer.append("}\n\n");
					this.dataOutputBuffer.append(ARFFConstants.DATA);
					this.dataOutputBuffer.append('\n');
					this.state = ARFFVisitor.DEFAULT;
				}
				break;
			case ARFFVisitor.PROPERTY :
				if (aNode.getNodeName().equals(XMLConstants.PROPERTY)) {
					this.state = ARFFVisitor.ACTOR_STRUCTURE;
				}
				break;
			case ARFFVisitor.PROPERTY_NAME :
				if (aNode.getNodeName().equals(XMLConstants.NAME)) {
					this.state = ARFFVisitor.PROPERTY;
				}
				break;
			case ARFFVisitor.DESIGN_PATTERN :
				if (aNode.getNodeName().equals(XMLConstants.DESIGN_PATTERN)) {

					this.state = ARFFVisitor.DEFAULT;
				}
				break;
			case ARFFVisitor.MICRO_ARCHITECTURE :
				if (aNode.getNodeName().equals(XMLConstants.MICRO_ARCHITECTURE)) {

					this.state = ARFFVisitor.DESIGN_PATTERN;
				}
				break;
			case ARFFVisitor.ACTOR_NAME :
				if (aNode.getNodeName().equals(XMLConstants.ENTITY)) {
					this.state = ARFFVisitor.MICRO_ARCHITECTURE;
				}
				break;
			case ARFFVisitor.PROPERTIES :
				if (aNode.getNodeName().equals(XMLConstants.PROPERTIES)) {
					// Yann 2004/05/05: Zeros!
					// I don't forget to write trailing zeros in
					// case the last written property wasn't the
					// last possible property.
					int shift;
					if (this.previousAttribute != null) {
						shift =
							this.attributeList.size()
									- this.attributeList
										.indexOf(this.previousAttribute) - 1;
					}
					else {
						shift = this.attributeList.size();
					}
					for (int i = 0; i < shift; i++) {
						this.dataOutputBuffer.append(ARFFVisitor.ZERO);
						this.dataOutputBuffer.append(ARFFVisitor.SEPARATOR);
					}

					final StringBuffer buffer = new StringBuffer();
					buffer.append('\'');
					buffer.append(this.designPatternName);
					buffer.append('-');
					buffer.append(Util.capitalizeFirstLetter(this.actorRole
						.toCharArray()));
					buffer.append('\'');

					this.dataOutputBuffer.append(buffer.toString());
					this.dataOutputBuffer.append('\n');

					// Yann 2004/05/14: Be kinds!
					// I want to store for each role, the
					// expected kind of the entity
					// (either Class or AbstractClass) and
					// the kind of the entities playing this
					// role (either Class, AbstractClass...).
					String actualRoleKind =
						(String) this.entityRoleKindMap.get(buffer.toString());
					if (actualRoleKind != null) {
						// if (this
						// .entityKind
						// .equals(XMLConstants.KIND_GHOST)) {
						//
						// roleKinds = XMLConstants.KIND_GHOST;
						// }
						// else
						if (this.entityKind.equals(XMLConstants.KIND_CLASS) // &&
																			// !roleKinds.equals(XMLConstants.KIND_GHOST)
						) {

							actualRoleKind = XMLConstants.KIND_CLASS;
						}
						else if (this.entityKind
							.equals(XMLConstants.KIND_ABSTRACT_CLASS)
								&& !actualRoleKind
									.equals(XMLConstants.KIND_CLASS) // &&
																		// !roleKinds.equals(XMLConstants.KIND_GHOST)
						) {

							actualRoleKind = XMLConstants.KIND_ABSTRACT_CLASS;
						}
						else if (this.entityKind
							.equals(XMLConstants.KIND_INTERFACE)
								&& !actualRoleKind
									.equals(XMLConstants.KIND_ABSTRACT_CLASS)
								&& !actualRoleKind
									.equals(XMLConstants.KIND_CLASS) // &&
																		// !roleKinds.equals(XMLConstants.KIND_GHOST)
						) {

							actualRoleKind = XMLConstants.KIND_INTERFACE;
						}
					}
					else {
						actualRoleKind = this.entityKind;
					}
					this.entityRoleKindMap.put(
						buffer.toString(),
						actualRoleKind);
					this.roleRoleKindMap.put(buffer.toString(), this.roleKind);

					this.previousAttribute = null;
					this.state = ARFFVisitor.MICRO_ARCHITECTURE;
				}
				break;
		}
	}
}
