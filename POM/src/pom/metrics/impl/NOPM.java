package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IOperation;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Foutse Khomh
 * @since  2007/03/01
 */
public class NOPM extends AbstractMetric implements IMetric, IUnaryMetric {
	public NOPM(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	public String getDefinition() {
		String def =
			"Number of methods that can exhibit polymorphic behavior.(a method can exhibit polymorphic behaviour if it is overridden by one or more descendent classes).";
		return def;
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		final Collection Methods =
			this.classPrimitives.listOfDeclaredMethods(anEntity);
		final List descendent =
			super.classPrimitives.listOfDescendents(anEntity);

		double result = 0;
		final Iterator iterMethod = Methods.iterator();
		while (iterMethod.hasNext()) {
			final IOperation method = (IOperation) iterMethod.next();
			final List overidden = new ArrayList();
			final Iterator iterdest = descendent.iterator();
			while (iterdest.hasNext()) {
				final IFirstClassEntity firstClassEntity = (IFirstClassEntity) iterdest.next();
				if (super.operators.belongTo(method, this.classPrimitives
					.listOfOverriddenMethods(firstClassEntity))) {

					overidden.add(firstClassEntity);
				}

			}
			if (overidden.size() > 0) {
				result = result + 1;
			}
		}

		return result;
	}
}
