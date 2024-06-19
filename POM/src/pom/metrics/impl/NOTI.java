/**
 * NOTI - Number Of Transitive Invacation
 * 
 * @author Alban Tiberghien
 * @since  2008/08/13 
 */

package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IConstructor;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IMethodInvocation;
import padl.kernel.IOperation;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class NOTI extends AbstractMetric implements IMetric, IUnaryMetric {
	public NOTI(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		int res = -1;

		final Collection methods =
			super.classPrimitives
				.listOfOverriddenAndConcreteMethods(firstClassEntity);

		final Iterator iter = methods.iterator();
		while (iter.hasNext()) {
			final IConstructor method = (IConstructor) iter.next();
			res =
				Math.max(res, this.computeInvocation(
					firstClassEntity,
					method,
					new ArrayList(),
					0));
		}

		return res;

	}

	public String getDefinition() {
		String def =
			"Computes the highest number of transitive "
					+ "invocation amoung methods of a class. see law of demeter definition";
		System.out.println(def);
		return def;
	}

	private int computeInvocation(
		final IFirstClassEntity currentEntity,
		final IConstructor currentMethod,
		final List visitedEntities,
		final int transitiveInvocation) {

		/**
		* Alban 2008/09/08 As this metrics is not used with a boxplot and the
		* threshold defined by the literature is 4, I limited the return value
		* to 20. It's a temporary fix and I have to find why this $*&%$ algo
		* don't stop !
		*/
		visitedEntities.add(currentEntity.getDisplayID());
		if (transitiveInvocation >= 20) {
			return 20;
		}

		int cpt = -1;

		final Iterator iterator =
			currentMethod.getIteratorOnConstituents(IMethodInvocation.class);
		iterator.hasNext();
		while (iterator.hasNext()) {
			final IMethodInvocation mi = (IMethodInvocation) iterator.next();
			final IOperation calledMethod = mi.getCalledMethod();

			/**
			 * Alban 2008/09/08: Infinite loop! 
			 * It misses again a stop condition 
			 * TODO Find which one !
			 * Yann 2010/03/04: Visited Entities
			 * I added a list of visited entities to make
			 * sure that we do not visit the same entities
			 * many times around jumping from one method
			 * to the others...
			 */
			if (calledMethod != null) {
				final IFirstClassEntity targetEntity = mi.getTargetEntity();
				if (targetEntity != null
						&& !visitedEntities.contains(targetEntity
							.getDisplayID())) {

					cpt =
						Math.max(cpt, this.computeInvocation(
							targetEntity,
							(IConstructor) calledMethod,
							visitedEntities,
							transitiveInvocation + 1));
				}
			}
		}

		return cpt + 1;
	}
}
