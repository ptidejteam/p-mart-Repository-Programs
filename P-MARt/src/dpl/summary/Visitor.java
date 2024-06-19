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
package dpl.summary;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import padl.creator.CompleteClassFileCreator;
import padl.event.IModelListener;
import padl.kernel.ICodeLevelModel;
import padl.kernel.exception.CreationException;
import padl.kernel.impl.Factory;
import padl.util.ModelStatistics;
import padl.util.Util;
import padl.util.repository.constituent.ConstituentRepository;
import padl.util.repository.file.FileRepositoryManager;
import util.xml.DOMVisitor;
import dpl.XMLConstants;
import dpl.util.SilentModelStatistics;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since 2004/04/21
 */
public class Visitor implements DOMVisitor {
	private static final int DEFAULT = 0;
	private static final int MICRO_ARCHITECTURE_NAME = 1;
	private static final int PROGRAM = 2;
	private static final int PROGRAM_NAME = 3;
	private static final int ROLE = 4;
	private static final int ROLE_NAME = 5;

	private ICodeLevelModel codeLevelModel;
	private final PrintWriter messageWriter;
	private final Map microArchitecturesPerMotifsAndPrograms;
	private final Map microArchitetcuresCount;
	private final IModelListener modelsStatistics;
	private String programName;
	private final Map roleCountPerMotif;
	private int state;

	public Visitor(final PrintWriter messageWriter) {
		this.microArchitetcuresCount = new HashMap();
		this.roleCountPerMotif = new HashMap();
		this.microArchitecturesPerMotifsAndPrograms = new HashMap();
		this.messageWriter = messageWriter;
		this.modelsStatistics = new ModelStatistics();
	}

	public void close(final Document aDocument) {
		this.messageWriter.println();
		this.messageWriter.println(this.modelsStatistics);
		this.messageWriter.println();
		this.messageWriter.flush();

		this.displayStatistics();
	}

	public void close(final Node aNode) {
	}

	private void displayStatistics() {
		// Yann 2004/10/19: Sort.
		// I convert the set of keys representing design motif names in
		// an array of String to sort the names alphabetically.
		final Set setOfDesignMotifs = this.roleCountPerMotif.keySet();
		final String[] arrayOfDesignMotifs =
			new String[setOfDesignMotifs.size()];
		setOfDesignMotifs.toArray(arrayOfDesignMotifs);
		Arrays.sort(arrayOfDesignMotifs);
		for (int i = 0; i < arrayOfDesignMotifs.length; i++) {
			final String designMotifName = arrayOfDesignMotifs[i];
			this.messageWriter.println(designMotifName);

			final Map roles = (Map) this.roleCountPerMotif.get(designMotifName);
			final Iterator iteratorOnRoles = roles.keySet().iterator();
			int numberOfClassesPlayingARole = 0;
			while (iteratorOnRoles.hasNext()) {
				final String roleName = (String) iteratorOnRoles.next();
				this.messageWriter.print('\t');
				this.messageWriter.print(Util.capitalizeFirstLetter(roleName
					.toCharArray()));
				this.messageWriter.print(": ");
				this.messageWriter.println(roles.get(roleName));

				numberOfClassesPlayingARole +=
					((Integer) roles.get(roleName)).intValue();
			}

			this.messageWriter
				.println("\t-> Distribution of the micro-architectures per program: ");
			final Map programs =
				(Map) this.microArchitecturesPerMotifsAndPrograms
					.get(designMotifName);
			final Set setOfPrograms = programs.keySet();
			final String[] arrayOfPrograms = new String[setOfPrograms.size()];
			setOfPrograms.toArray(arrayOfPrograms);
			Arrays.sort(arrayOfPrograms);
			for (int j = 0; j < arrayOfPrograms.length; j++) {
				final String programName = arrayOfPrograms[j];
				this.messageWriter.print("\t\t");
				this.messageWriter.print(programName);
				this.messageWriter.print(": ");
				this.messageWriter.println(programs.get(programName));
			}
			this.messageWriter.print("\t-> Number of micro-architectures: ");
			this.messageWriter.println(((Integer) this.microArchitetcuresCount
				.get(designMotifName)).intValue());
			this.messageWriter.print("\t-> Number of roles: ");
			this.messageWriter.println(roles.keySet().size());
			this.messageWriter
				.print("\t-> Number of classing playing a role: ");
			this.messageWriter.println(numberOfClassesPlayingARole);
		}
		this.messageWriter.flush();
	}

	public void open(final Document aDocument) {
		this.state = Visitor.DEFAULT;
	}

	public void open(final Node aNode) {
		if (aNode.getNodeName().equals(XMLConstants.PROGRAM)) {
			this.state = Visitor.PROGRAM;
		}
		else if (aNode.getNodeName().equals(XMLConstants.ROLES)) {
			this.state = Visitor.ROLE;
		}
		else if (aNode.getNodeName().equals(XMLConstants.NAME)) {
			if (this.state == Visitor.PROGRAM) {
				this.state = Visitor.PROGRAM_NAME;
			}
		}
		else if (aNode.getNodeName().equals(XMLConstants.MICRO_ARCHITECTURE)) {
			this.state = Visitor.MICRO_ARCHITECTURE_NAME;
		}
		else if (aNode.getNodeName().equals(XMLConstants.ENTITY)) {
			if (this.state == Visitor.ROLE) {
				this.state = Visitor.ROLE_NAME;
			}
		}
		else if (aNode.getNodeName().equals(XMLConstants.TEXT)) {
			if (this.state == Visitor.PROGRAM_NAME) {
				// if (this.idiomLevelModel != null) {
				// this.displayRoleCountPerDesignMotifs();
				// }

				// Yann 2004/05/10: GC!
				// Following Farouk's advice, I make sure
				// the memory is as clean as possible...
				this.codeLevelModel = null;
				for (int i = 0; i < 3; i++) {
					System.gc();
				}

				this.programName = aNode.getNodeValue();
				this.codeLevelModel =
					Factory.getInstance().createCodeLevelModel(this.programName);

				// Yann 2004/05/24: Statistics.
				// I create a new listener for each program
				// because I want to have statistics for
				// each program independently.
				// Yann 2004/10/16: Output
				// Thus, each model is listened by two listeners: A general
				// one plus this one which is particular to this model. The
				// output is doubled.
				final IModelListener modelStatistics =
					new SilentModelStatistics();
				this.codeLevelModel.addModelListener(modelStatistics);
				this.codeLevelModel.addModelListener(this.modelsStatistics);

				// if (programName.startsWith("5 - ")) {
				try {
					this.codeLevelModel.create(new CompleteClassFileCreator(
						ConstituentRepository.getInstance(FileRepositoryManager
							.getRepository(this.getClass())),
						new String[] { "../" + this.programName + "/bin/" },
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
				this.messageWriter.println(this.programName);
				this.messageWriter.println(modelStatistics);
				this.messageWriter.println();
				this.messageWriter.flush();
				this.codeLevelModel.removeModelListener(modelStatistics);

				this.state = Visitor.PROGRAM;
			}
			else if (this.state == Visitor.ROLE_NAME) {
				final Node roleNode = aNode.getParentNode().getParentNode();
				final String roleName = roleNode.getNodeName();

				// Role -> Roles -> Micro-architecture -> Micro-architectures ->
				// Design motif
				final Node designMotifNode =
					roleNode
						.getParentNode()
						.getParentNode()
						.getParentNode()
						.getParentNode()
						.getParentNode();
				final String designPatternName;
				try {
					designPatternName =
						designMotifNode
							.getAttributes()
							.getNamedItem(XMLConstants.NAME)
							.getNodeValue();

					Map roles =
						(Map) this.roleCountPerMotif.get(designPatternName);
					if (roles == null) {
						roles = new HashMap();
						this.roleCountPerMotif.put(designPatternName, roles);
					}
					Integer count = (Integer) roles.get(roleName);
					if (count == null) {
						count = new Integer(1);
					}
					else {
						count = new Integer(count.intValue() + 1);
					}
					roles.put(roleName, count);
				}
				catch (final NullPointerException e) {
					// Yann 2004/10/14: Mismatch
					System.err
						.println("The XML file does not respect the depth for the declarations of entities!");
					System.err.println(roleName);
					System.err.println(designMotifNode);
				}

				this.state = Visitor.ROLE;
			}
			else if (this.state == Visitor.MICRO_ARCHITECTURE_NAME) {
				final String designPatternName =
					aNode
						.getParentNode()
						.getParentNode()
						.getParentNode()
						.getAttributes()
						.getNamedItem(XMLConstants.NAME)
						.getNodeValue();

				// Yann 2004/10/12: Count of micro-architectures
				// I record the total number of micro-architectures
				// similar to a design motif in the DPL.
				Integer count =
					(Integer) this.microArchitetcuresCount
						.get(designPatternName);
				if (count == null) {
					this.microArchitetcuresCount.put(
						designPatternName,
						new Integer(1));
				}
				else {
					this.microArchitetcuresCount.put(
						designPatternName,
						new Integer(count.intValue() + 1));
				}

				// Yann 2004/10/19: Micro-architectures per motif per program
				// I record the number of micro-architectures for a given
				// design motif in a given program.
				Map programs =
					(Map) this.microArchitecturesPerMotifsAndPrograms
						.get(designPatternName);
				if (programs == null) {
					programs = new HashMap();
					this.microArchitecturesPerMotifsAndPrograms.put(
						designPatternName,
						programs);
				}
				final String shortProgramName =
					this.programName
						.substring(this.programName.indexOf('-') + 2);
				count = (Integer) programs.get(shortProgramName);
				if (count == null) {
					programs.put(shortProgramName, new Integer(1));
				}
				else {
					programs.put(shortProgramName, new Integer(
						count.intValue() + 1));
				}

				this.state = Visitor.PROGRAM;
			}
		}
	}
}
