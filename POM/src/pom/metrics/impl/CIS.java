package pom.metrics.impl;

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
public class CIS extends AbstractMetric implements IMetric, IUnaryMetric {
	public CIS(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	public double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		final List meth = listOfElements(firstClassEntity);
		final int size = meth.size();

		double pubMeth = 0;
		for (int i = 0; i < size; i++) {
			final IOperation m = (IOperation) meth.get(i);
			if (m.isPublic()) {
				pubMeth = pubMeth + 1;
			}

		}
		return pubMeth;

	}

	public String getDefinition() {
		String def = "Counts the number of public methods in a class";
		System.out.println(def);
		return def;
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		return super.classPrimitives.listOfDeclaredMethods(firstClassEntity);
	}

}
