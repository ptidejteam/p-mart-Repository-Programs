/* (c) Copyright 2001 and following years, Yann-Gaël Guéhéneuc,
 * University of Montreal.
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
package dpl.util;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import util.xml.DOMVisitor;
import dpl.XMLConstants;

public class XMLStatisticsVisitor implements DOMVisitor {
	private int numberOfMicroarchitectures;
	private int numberOfPatterns;
	private int numberOfPrograms;
	private final Set setOfUniquePatterns;
	private final PrintWriter writer;

	public XMLStatisticsVisitor(final PrintWriter aWriter) {
		this.writer = aWriter;
		this.setOfUniquePatterns = new HashSet();
	}

	public void close(final Document aDocument) {
		this.writer.print("Number of programs: ");
		this.writer.println(this.numberOfPrograms);
		this.writer.print("Number of microarchitectures: ");
		this.writer.println(this.numberOfMicroarchitectures);
		this.writer.print("Number of patterns: ");
		this.writer.println(this.numberOfPatterns);
		this.writer.print("Number of unique patterns: ");
		this.writer.println(this.setOfUniquePatterns.size());
	}
	public void close(final Node aNode) {
	}
	public void open(final Document aDocument) {
	}
	public void open(final Node aNode) {
		final String name = aNode.getNodeName();
		if (name.equals(XMLConstants.PROGRAM)) {
			this.numberOfPrograms++;
		}
		else if (name.equals(XMLConstants.MICRO_ARCHITECTURE)) {
			this.numberOfMicroarchitectures++;
		}
		else if (name.equals(XMLConstants.DESIGN_PATTERN)) {
			this.numberOfPatterns++;
			final NamedNodeMap map = aNode.getAttributes();
			final String designPatternName =
				map.getNamedItem(XMLConstants.NAME).getNodeValue();
			this.setOfUniquePatterns.add(designPatternName);
		}
	}
}
