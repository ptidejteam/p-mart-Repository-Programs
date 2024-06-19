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
package dpl.metrics;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import padl.creator.CompleteClassFileCreator;
import padl.event.IModelListener;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IEntity;
import padl.kernel.IGhost;
import padl.kernel.IInterface;
import padl.kernel.exception.CreationException;
import padl.kernel.impl.Factory;
import padl.util.ModelStatistics;
import padl.util.repository.constituent.ConstituentRepository;
import padl.util.repository.file.FileRepositoryManager;
import pom.metrics.Repository;
import util.xml.DOMVisitor;
import wayne.xml.XmlFormatter;
import dpl.XMLConstants;
import dpl.util.SilentModelStatistics;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since 2004/04/21
 */
public class Visitor implements DOMVisitor {
	private static final boolean DEBUG = false;
	private static final boolean COMPUTE_UNARY_CONSTRAINTS = true;
	private static final boolean COMPUTE_BINARY_CONSTRAINTS = true;

	private static final List binaryMethods = new ArrayList();
	private static final List unaryMethods = new ArrayList();
	static {
		System.out.println("Computing metrics: ");
		final Method[] methods = Metrics.class.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if ((methods[i].getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC
					&& (methods[i].getModifiers() & Modifier.STATIC) != Modifier.STATIC) {

				System.out.print('\t');
				System.out.print(methods[i].getName());
				if (methods[i].getParameterTypes().length == 1) {
					unaryMethods.add(methods[i]);
					System.out.println(" (unary)");
				} else if (methods[i].getParameterTypes().length == 2) {
					binaryMethods.add(methods[i]);
					System.out.println(" (binary)");
				}
			}
		}
		System.out.println();
	}
	private static final int ACTOR = 3;
	private static final int ACTORNAME = 4;
	private static final int DEFAULT = 0;
	private static final int PROGRAM = 1;
	private static final int PROGRAMNAME = 2;

	private final Writer dataWriter;
	private final PrintWriter messageWriter;
	private final IModelListener modelsStatistics;
	private ICodeLevelModel codeLevelModel;
	private Repository metrics;
	private int state;

	public Visitor(final Writer dataWriter, final PrintWriter messageWriter) {

		this.dataWriter = dataWriter;
		this.messageWriter = messageWriter;
		this.modelsStatistics = new ModelStatistics();
	}

	public void close(final Document aDocument) {
		try {
			if (Visitor.DEBUG) {
				this.messageWriter.println("close(Document) (state = "
						+ this.state + ')');
				this.messageWriter.flush();
			}
			this.dataWriter.write(XMLConstants.XMLHEADER);
			this.dataWriter.write(new XmlFormatter(1, '\t').format(aDocument));
			this.dataWriter.flush();

			this.messageWriter.println();
			this.messageWriter.println(this.modelsStatistics);
			this.messageWriter.flush();
		} catch (final IOException ioe) {
			ioe.printStackTrace(this.messageWriter);
			this.messageWriter.flush();
		}

		// Yann 2004/04/21: Explanations.
		// When the visit of the DOM is done, I output the newly
		// modified DOM (for which metrics have been computed)
		// in to a new file.
		// try {
		// final TransformerFactory transformerFactory =
		// TransformerFactory.newInstance();
		// final Transformer transformer =
		// transformerFactory.newTransformer();
		// final DOMSource source = new DOMSource(aDocument);
		// final StreamResult result =
		// new StreamResult(new File(Constants.TARGETXMLFILE));
		// transformer.transform(source, result);
		// }
		// catch (final TransformerConfigurationException tce) {
		// tce.printStackTrace(this.messageWriter);
		// }
		// catch (final TransformerFactoryConfigurationError tfce) {
		// tfce.printStackTrace(this.messageWriter);
		// }
		// catch (final TransformerException te) {
		// te.printStackTrace(this.messageWriter);
		// }
	}

	public void close(final Node aNode) {
		if (Visitor.DEBUG) {
			this.messageWriter.println("close(Node) (state = " + this.state
					+ ')');
			this.messageWriter.flush();
		}
	}

	private void computeBinaryMetrics(final Node aNode) {
		if (Visitor.DEBUG) {
			this.messageWriter.println("computeBinaryMetrics(Node)");
			this.messageWriter.flush();
		}
		/*
		 * <relations> <relation> <target>toto</target> <properties> <property>
		 * <name>CBO</name> <value>1</value> </property> </properties>
		 * </relation> </relations>
		 */
		final Node actorNode = aNode.getParentNode().getParentNode();
		// Yann 2004/04/23: Garantee!
		// No garantuee is made that a node "Property" does
		// not exist already.
		final Document document = actorNode.getOwnerDocument();
		final Element relationsNode = document
				.createElement(XMLConstants.RELATIONS);
		actorNode.appendChild(relationsNode);

		final IEntity entity = (IEntity) this.codeLevelModel
				.getConstituentFromName(aNode.getNodeValue());
		if (entity != null) {
			final Iterator entities = this.codeLevelModel
					.getIteratorOnConstituents(IEntity.class);
			while (entities.hasNext()) {
				final IEntity targetEntity = (IEntity) entities.next();

				final Element relationNode = document
						.createElement(XMLConstants.RELATION);
				final Element targetElement = document
						.createElement(XMLConstants.TARGET);
				relationNode.appendChild(targetElement);
				final Text nameElement = document.createTextNode(targetEntity
						.getDisplayName());
				targetElement.appendChild(nameElement);
				final Element propertiesNode = document
						.createElement(XMLConstants.PROPERTIES);
				relationNode.appendChild(propertiesNode);

				final Iterator binaryMetrics = Visitor.binaryMethods.iterator();
				while (binaryMetrics.hasNext()) {
					final Method metrics = (Method) binaryMetrics.next();
					try {
						final Double result = Double.valueOf(metrics.invoke(
								this.metrics,
								new Object[] { entity, targetEntity })
								.toString());
						if (result.doubleValue() != 0) {
							final Element propertyNode = document
									.createElement(metrics.getName());
							propertyNode.setAttribute(XMLConstants.VALUE,
									result.toString());
							propertiesNode.appendChild(propertyNode);

							relationsNode.appendChild(relationNode);
						}
					} catch (final IllegalArgumentException e) {
						e.printStackTrace(this.messageWriter);
						this.messageWriter.flush();
					} catch (final DOMException e) {
						e.printStackTrace(this.messageWriter);
						this.messageWriter.flush();
					} catch (final IllegalAccessException e) {
						e.printStackTrace(this.messageWriter);
						this.messageWriter.flush();
					} catch (final InvocationTargetException e) {
						e.printStackTrace(this.messageWriter);
						this.messageWriter.flush();
					}
				}
			}
		} else {
			this.messageWriter.print("Cannot find ");
			this.messageWriter.println(aNode.getNodeValue());
			this.messageWriter.flush();
		}
	}

	private void computeUnaryMetrics(final Node aNode) {
		if (Visitor.DEBUG) {
			this.messageWriter.println("computeUnaryMetrics(Node)");
			this.messageWriter.flush();
		}
		/*
		 * <properties> <AID value="1"/> <CLD value="2"/> </properties>
		 */
		final Node actorNode = aNode.getParentNode().getParentNode();
		// Yann 2004/04/23: Garantee!
		// No garantuee is made that a node "Property" does
		// not exist already.
		final Document document = actorNode.getOwnerDocument();
		final Element propertiesNode = document
				.createElement(XMLConstants.PROPERTIES);
		actorNode.appendChild(propertiesNode);

		final IEntity entity = (IEntity) this.codeLevelModel
				.getConstituentFromName(aNode.getNodeValue());

		if (entity != null) {
			// Yann 2004/05/14: Kind of entity.
			// I add a kind attribute to the entity
			// to know whether it is a class, an
			// abstract class, or an interface.
			final Element entityNode = (Element) aNode.getParentNode();
			final String kind;
			if (entity instanceof IInterface) {
				kind = XMLConstants.KIND_INTERFACE;
			} else if (entity instanceof IGhost) {
				kind = XMLConstants.KIND_GHOST;
			} else if (entity.isAbstract()) {
				kind = XMLConstants.KIND_ABSTRACT_CLASS;
			} else {
				kind = XMLConstants.KIND_CLASS;
			}
			entityNode.setAttribute(XMLConstants.KIND, kind);

			final Iterator unaryMetrics = Visitor.unaryMethods.iterator();
			while (unaryMetrics.hasNext()) {
				final Method metrics = (Method) unaryMetrics.next();
				try {
					final Double result = Double.valueOf(metrics.invoke(
							this.metrics, new Object[] { entity }).toString());
					if (result.doubleValue() != 0) {
						final Element propertyNode = document
								.createElement(metrics.getName());
						propertyNode.setAttribute(XMLConstants.VALUE,
								result.toString());
						propertiesNode.appendChild(propertyNode);
					}
				} catch (final IllegalArgumentException e) {
					e.printStackTrace(this.messageWriter);
					this.messageWriter.flush();
				} catch (final DOMException e) {
					e.printStackTrace(this.messageWriter);
					this.messageWriter.flush();
				} catch (final IllegalAccessException e) {
					e.printStackTrace(this.messageWriter);
					this.messageWriter.flush();
				} catch (final InvocationTargetException e) {
					e.printStackTrace(this.messageWriter);
					this.messageWriter.flush();
				}
			}
		} else {
			this.messageWriter.print("Cannot find ");
			this.messageWriter.println(aNode.getNodeValue());
			this.messageWriter.flush();

			// Yann 2004/05/14: Kind of entity.
			// I add a kind attribute to the entity
			// to know whether it is a class, an
			// abstract class, or an interface.
			final Element entityNode = (Element) aNode.getParentNode();
			entityNode.setAttribute(XMLConstants.KIND, XMLConstants.KIND_GHOST);
		}

	}

	public void open(final Document aDocument) {
		if (Visitor.DEBUG) {
			this.messageWriter.println("open(Document) (state = " + this.state
					+ ')');
			this.messageWriter.flush();
		}
		/*
		 * <ObjectProperties> <Prop> <PropName>WMC</PropName>
		 * <ProType>int</ProType> <ProComments>None</ProComments> </Prop>
		 * </ObjectProperties> <RelationProperties> <RelationProperty>
		 * <PropName>CBO</PropName> <ProType>int</ProType>
		 * <ProComments>None</ProComments> </RelationProperty>
		 * </RelationProperties>
		 */
		final Element actorStructure = aDocument
				.createElement(XMLConstants.ROLE_STRUCTURE);
		final Node designPatterns = aDocument.getElementsByTagName(
				XMLConstants.DESIGN_PATTERNS).item(0);
		designPatterns.insertBefore(actorStructure,
				designPatterns.getFirstChild());

		final Element properties = aDocument
				.createElement(XMLConstants.PROPERTIES);
		actorStructure.appendChild(properties);
		final Iterator unaryMetrics = Visitor.unaryMethods.iterator();
		while (unaryMetrics.hasNext()) {
			final Method metrics = (Method) unaryMetrics.next();
			final Element property = aDocument
					.createElement(XMLConstants.PROPERTY);
			properties.appendChild(property);

			final Element name = aDocument.createElement(XMLConstants.NAME);
			property.appendChild(name);
			final Text nameText = aDocument.createTextNode(metrics.getName());
			name.appendChild(nameText);

			final Element type = aDocument.createElement(XMLConstants.TYPE);
			property.appendChild(type);
			final Text typeText = aDocument.createTextNode(metrics
					.getReturnType().getName());
			type.appendChild(typeText);
		}

		final Element relations = aDocument
				.createElement(XMLConstants.RELATIONS);
		actorStructure.appendChild(relations);
		final Iterator binaryMetrics = Visitor.binaryMethods.iterator();
		while (binaryMetrics.hasNext()) {
			final Method metrics = (Method) binaryMetrics.next();
			final Element relation = aDocument
					.createElement(XMLConstants.RELATION);
			relations.appendChild(relation);

			final Element name = aDocument.createElement(XMLConstants.NAME);
			relation.appendChild(name);
			final Text nameText = aDocument.createTextNode(metrics.getName());
			name.appendChild(nameText);

			final Element type = aDocument.createElement(XMLConstants.TYPE);
			relation.appendChild(type);
			final Text typeText = aDocument.createTextNode(metrics
					.getReturnType().getName());
			type.appendChild(typeText);
		}

		this.state = Visitor.DEFAULT;
	}

	public void open(final Node aNode) {
		if (Visitor.DEBUG) {
			this.messageWriter.println("open(Node) (state = " + this.state
					+ ')');
			this.messageWriter.flush();
		}
		if (aNode.getNodeName().equals(XMLConstants.PROGRAM)) {
			this.state = Visitor.PROGRAM;
		} else if (aNode.getNodeName().equals(XMLConstants.ROLES)) {
			this.state = Visitor.ACTOR;
		} else if (aNode.getNodeName().equals(XMLConstants.NAME)) {
			if (this.state == Visitor.PROGRAM) {
				this.state = Visitor.PROGRAMNAME;
			}
		} else if (aNode.getNodeName().equals(XMLConstants.ENTITY)) {
			if (this.state == Visitor.ACTOR) {
				this.state = Visitor.ACTORNAME;
			}
		} else if (aNode.getNodeName().equals(XMLConstants.TEXT)) {
			if (this.state == Visitor.PROGRAMNAME) {
				// Yann 2004/05/10: GC!
				// Following Farouk's advice, I make sure
				// the memory is as clean as possible...
				this.codeLevelModel = null;
				this.metrics = null;
				for (int i = 0; i < 3; i++) {
					System.gc();
				}

				final String programName = aNode.getNodeValue();
				this.codeLevelModel = Factory.getInstance()
						.createCodeLevelModel(programName);

				// Yann 2004/05/24: Statistics.
				// I create a new listener for each program
				// because I want to have statistics for
				// each program independently.
				final IModelListener modelStatistics = new SilentModelStatistics();
				this.codeLevelModel.addModelListener(modelStatistics);
				this.codeLevelModel.addModelListener(this.modelsStatistics);

				// if (programName.startsWith("2 - ")
				// || programName.startsWith("5 - ")
				// || programName.startsWith("6 - ")
				// || programName.startsWith("13 - ")) {

				try {
					this.codeLevelModel.create(new CompleteClassFileCreator(
						ConstituentRepository.getInstance(FileRepositoryManager
							.getRepository(this.getClass())),
						new String[] { "../" + programName + "/bin/" },
						true));
				}
				catch (final CreationException e) {
					e.printStackTrace();
				}

				// }

				// Yann 2004/05/24: Statistics.
				// I print out the statistics for the current
				// model and then I discard the listener.
				this.messageWriter.println();
				this.messageWriter.println(programName);
				this.messageWriter.println(modelStatistics);
				this.messageWriter.println();
				this.messageWriter.flush();
				this.codeLevelModel.removeModelListener(modelStatistics);

				// this.metrics = new Metrics(this.idiomLevelModel);
				this.metrics = Repository.getInstance(
						FileRepositoryManager.getRepository(this.getClass()),
						this.codeLevelModel);
				this.state = Visitor.PROGRAM;
			} else if (this.state == Visitor.ACTORNAME) {
				if (Visitor.COMPUTE_UNARY_CONSTRAINTS) {
					this.computeUnaryMetrics(aNode);
				}
				if (Visitor.COMPUTE_BINARY_CONSTRAINTS) {
					this.computeBinaryMetrics(aNode);
				}
				this.state = Visitor.ACTOR;
			}
		}
	}
}
