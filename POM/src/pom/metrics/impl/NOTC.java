/**
 *NOTC: numbers of invocations of JUNIT assert methods that occur in the code of a test case.
 *only direct invocations because it can call also a method that contains the asserts ... in that case, we will not have the number of asserts--- recursively??? it will be long!!!
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
import padl.kernel.IMethodInvocation;
import padl.kernel.IOperation;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;
import util.io.Output;
import util.lang.Modifier;

public class NOTC extends AbstractMetric implements IMetric, IUnaryMetric {
	public NOTC(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		int notc = 0;

		if (anEntity instanceof IClass) {
			final IClass clazz = (IClass) anEntity;

			final Iterator iteratorOnMethods =
				clazz.getIteratorOnConstituents(IMethod.class);
			while (iteratorOnMethods.hasNext()) {
				final IMethod method = (IMethod) iteratorOnMethods.next();
				try {
					Iterator methodInvocationsIter =
						method.getConcurrentIteratorOnConstituents(Class
							.forName("padl.kernel.impl.MethodInvocation"));

					while (methodInvocationsIter.hasNext()) {
						final IMethodInvocation methodInvocation =
							(IMethodInvocation) methodInvocationsIter.next();
						final IOperation methodInvoked =
							methodInvocation.getCalledMethod();
						if (methodInvoked != null) {
							//Is this heuristic sufficient? or add another constraint?
							if (methodInvoked.getDisplayName().contains(
								"assert")) {
								notc++;
							}
						}

					}
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		return notc;
	}

	public String getDefinition() {
		return "Return the numbers of invocations of JUNIT assert methods that occur in the code of a test case.";
	}

}
