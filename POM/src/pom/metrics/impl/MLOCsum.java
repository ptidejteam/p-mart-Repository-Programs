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
package pom.metrics.impl;

import java.util.Iterator;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IField;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IMethod;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;
import util.io.Output;
import util.lang.Modifier;

/**
 * 
 * @author Yann
 * @date 2012/03/27
 * 
 * See Foutse's TSE paper.
 *
 */
public class MLOCsum extends AbstractMetric implements IMetric, IUnaryMetric {
	public MLOCsum(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		int loc = 0;

		if (anEntity instanceof IClass) {
			final IClass clazz = (IClass) anEntity;

			final Iterator iteratorOnMethods =
				clazz.getIteratorOnConstituents(IMethod.class);
			while (iteratorOnMethods.hasNext()) {
				final IMethod method = (IMethod) iteratorOnMethods.next();
				if (!method.isAbstract()
						&& (method.getVisibility() & Modifier.NATIVE) == 0) {

					final String[] codeLines = method.getCodeLines();
					if (codeLines != null) {
						loc += method.getCodeLines().length;
					}
					else {
						Output.getInstance().errorOutput().print("Method ");
						Output.getInstance().errorOutput().print(
							clazz.getName());
						Output.getInstance().errorOutput().print('.');
						Output.getInstance().errorOutput().print(
							method.getName());
						Output.getInstance().errorOutput().println(
							" has no code lines!");
					}
				}
			}

			final Iterator iteratorOnFields =
				clazz.getIteratorOnConstituents(IField.class);
			while (iteratorOnFields.hasNext()) {
				final IField field = (IField) iteratorOnFields.next();
				// Yann 2012/03/27: Could it be possible to get the "number of lines"
				// for a field from the debug information in a class file?
				// loc += field.getCodeLines().length;
				loc++;
			}
		}

		return loc;
	}

	public String getDefinition() {
		return "Return the numbers of lines of code of all the methods of a class.";
	}

}
