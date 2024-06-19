/**
 *"Return the numbers of lines of code of all the methods of a test case."
 * ref: Bruntink, M., Deursen, A.V.: Predicting class testability using object oriented metrics. In: Proceedings of the IEEE International Workshop on Source Code Analysis and Manipulation, pp. 136–145 (2004) 
 * @author Aminata Sabané
 * @since  2012/07/03
 * 
 */
package pom.metrics.impl;

import java.util.Iterator;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IMethod;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;
import util.io.Output;
import util.lang.Modifier;

public class TestCaseLOC extends AbstractMetric implements IMetric, IUnaryMetric {
	public TestCaseLOC(
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
		}

		return loc;
	}

	public String getDefinition() {
		return "Return the numbers of lines of code of all the methods of a test case.";
	}

}
