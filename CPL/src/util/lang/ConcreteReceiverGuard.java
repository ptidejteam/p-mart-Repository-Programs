/* (c) Copyright 2008 and following years, Yann-Gaël Guéhéneuc,
 * École Polytechnique de Montréal.
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
package util.lang;

import util.io.Output;

public final class ConcreteReceiverGuard {
	protected static ConcreteReceiverGuard UniqueInstance;

	public static ConcreteReceiverGuard getInstance() {
		if (ConcreteReceiverGuard.UniqueInstance == null) {
			ConcreteReceiverGuard.UniqueInstance = new ConcreteReceiverGuard();
		}
		return ConcreteReceiverGuard.UniqueInstance;
	}
	protected ConcreteReceiverGuard() {
	}

	private void doCheck(
		final String aConcreteReceiverClassToEnforce,
		final String anErrorMessage) {

		class ConcreteReceiverGuardThrownException extends RuntimeException {
			private static final long serialVersionUID = -4100342857707204144L;
		}

		try {
			throw new ConcreteReceiverGuardThrownException();
		}
		catch (final ConcreteReceiverGuardThrownException e) {
			final StackTraceElement[] stackTrace = e.getStackTrace();
			if (stackTrace.length < 4) {
				Output
					.getInstance()
					.errorOutput()
					.print("ConcreteReceiverGuard cannot check for ");
				Output
					.getInstance()
					.errorOutput()
					.println(aConcreteReceiverClassToEnforce);
			}
			else {
				final StringBuffer buffer = new StringBuffer();
				buffer.append(stackTrace[2].getClassName());
				buffer.append('.');
				buffer.append(stackTrace[2].getMethodName());
				final String nameOfGuardedMethod = buffer.toString();
				final String nameOfClassCallingGuardedMethod =
					stackTrace[3].getClassName();

				if (!nameOfClassCallingGuardedMethod
					.equals(aConcreteReceiverClassToEnforce)) {

					Output
						.getInstance()
						.errorOutput()
						.print("Runtime deprecation! Calling method \"");
					Output
						.getInstance()
						.errorOutput()
						.print(nameOfGuardedMethod);
					Output
						.getInstance()
						.errorOutput()
						.print("()\" from class \"");
					Output
						.getInstance()
						.errorOutput()
						.print(nameOfClassCallingGuardedMethod);
					Output.getInstance().errorOutput().println('\"');
					Output.getInstance().errorOutput().println(anErrorMessage);
				}
			}
		}
	}

	public void check(
		final String aConcreteReceiverClassToEnforce,
		final String anErrorMessage) {

		// Yann 2013/04/05: Stack!
		// I added this spurious method to make sure that the 
		// doCheck() method is always called with the same stack
		// depth from this class, i.e., 2 :-)
		this.doCheck(aConcreteReceiverClassToEnforce, anErrorMessage);
	}
	public void check(
		final Class aConcreteReceiverClassToEnforce,
		final String anErrorMessage) {

		this.doCheck(aConcreteReceiverClassToEnforce.getName(), anErrorMessage);
	}
}
