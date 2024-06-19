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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import util.io.Output;
import util.xml.DOMVisitorAdapter;
import dpl.XMLConstants;

public class XMLStatisticGenerator {
	public static void main(final String[] args) {
		final DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document =
				builder.parse(new File(XMLConstants.ORIGIN_XML_FILE));
			final float startTime = System.currentTimeMillis();

			final PrintWriter writer =
				new PrintWriter(new FileWriter(
					XMLConstants.STATISTICS_XML_FILE_LOG));
			Output.getInstance().setNormalOutput(writer);
			Output.getInstance().setErrorOutput(writer);
			new DOMVisitorAdapter(document).accept(new XMLStatisticsVisitor(
				writer));
			writer.close();

			System.err.print("\nMetrics computed in ");
			System.err.print(System.currentTimeMillis() - startTime);
			System.err.println(" ms.");
		}
		catch (final ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		catch (final SAXException saxe) {
			saxe.printStackTrace();
		}
		catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
