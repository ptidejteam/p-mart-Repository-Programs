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
package util.process;

import java.io.IOException;
import java.io.PrintStream;
import util.io.Output;
import util.io.OutputMonitor;
import util.io.WriterOutputStream;

public class CallerHelper {
	public static void execute(final String aHeader, final String aCommandLine) {
		try {
			Output.getInstance().normalOutput().print("Executing: \"");
			Output.getInstance().normalOutput().print(aCommandLine);
			Output.getInstance().normalOutput().println("\"...");
			final Process process = Runtime.getRuntime().exec(aCommandLine);

			final OutputMonitor streamOutputMonitor =
				new OutputMonitor(
					"Ouput Stream Monitor",
					aHeader,
					process.getInputStream(),
					new PrintStream(new WriterOutputStream(Output
						.getInstance()
						.normalOutput())));
			streamOutputMonitor.start();

			// A thread to catch and to display the output of the remote JVM.
			final OutputMonitor streamErrorMonitor =
				new OutputMonitor(
					"Error Stream Monitor",
					aHeader,
					process.getErrorStream(),
					new PrintStream(new WriterOutputStream(Output
						.getInstance()
						.errorOutput())));
			streamErrorMonitor.start();

			final long startTime = System.currentTimeMillis();
			try {
				process.waitFor();
			}
			catch (final InterruptedException ie) {
				ie.printStackTrace(Output.getInstance().errorOutput());
			}
			final long endTime = System.currentTimeMillis();

			// I check if everything went alright.
			if (process.exitValue() != 0) {
				while (process.getErrorStream().available() > 0) {
					Output
						.getInstance()
						.errorOutput()
						.print((char) process.getErrorStream().read());
				}
			}

			Output.getInstance().normalOutput().print("Done in ");
			Output.getInstance().normalOutput().print(endTime - startTime);
			Output.getInstance().normalOutput().println("ms");

			//	try {
			//		Thread.sleep(5000);
			//	}
			//	catch (final InterruptedException e) {
			//		e.printStackTrace();
			//	}
		}
		catch (final IOException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
	}
}
