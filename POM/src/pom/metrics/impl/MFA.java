package pom.metrics.impl;

import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Foutse Khomh
 * @since  2007/03/01
 */
public class MFA extends AbstractMetric implements IMetric, IUnaryMetric {
	public MFA(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public String getDefinition() {
		String def =
			"The ratio of the number of methods inherited by a class to the "
					+ "number of methods accessible by member methods of the class";
		System.out.println(def);
		return def;
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		if ((super.classPrimitives.listOfDeclaredMethods(firstClassEntity).size() + super.classPrimitives
			.listOfInheritedMethods(firstClassEntity)
			.size()) > 0) {
			return (double) (super.classPrimitives
				.listOfInheritedMethods(firstClassEntity).size())
					/ (double) (super.classPrimitives.listOfDeclaredMethods(
						firstClassEntity).size() + super.classPrimitives
						.listOfInheritedMethods(firstClassEntity)
						.size());

		}
		else {
			return 0;
		}
	}
}
