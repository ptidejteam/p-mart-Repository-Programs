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
package util.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since  2004/07/15
 */
public class Output {
	private static Output UniqueInstance;
	public static Output getInstance() {
		if (Output.UniqueInstance == null) {
			Output.UniqueInstance = new Output();
		}

		return Output.UniqueInstance;
	}
	private PrintWriter debugOutput = new PrintWriter(new NullWriter());
	// new AutoFlushPrintWriter(new OutputStreamWriter(System.err));
	private PrintWriter errorOutput = new AutoFlushPrintWriter(
		new OutputStreamWriter(System.err));
	private PrintWriter normalOutput = new AutoFlushPrintWriter(
		new OutputStreamWriter(System.out));

	private Output() {
	}
	public PrintWriter debugOutput() {
		return this.debugOutput;
	}
	public PrintWriter errorOutput() {
		return this.errorOutput;
	}
	public PrintWriter normalOutput() {
		return this.normalOutput;
	}
	public void setDebugOutput(final PrintWriter messageWriter) {
		this.debugOutput = messageWriter;
	}
	public void setErrorOutput(final PrintWriter messageWriter) {
		this.errorOutput = messageWriter;
	}
	public void setNormalOutput(final PrintWriter messageWriter) {
		this.normalOutput = messageWriter;
	}
	public PrintWriter warningOutput() {
		return new WarningPrintWriter(this.normalOutput);
	}
	public void printSetContent(final Writer writer, final Set aSet) {
		final Iterator iterator = aSet.iterator();
		while (iterator.hasNext()) {
			final Object object = iterator.next();
			try {
				writer.write(object.toString());
				writer.write('\n');
				writer.flush();
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
