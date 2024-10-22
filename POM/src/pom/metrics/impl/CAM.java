package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IOperation;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IParameter;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Foutse Khomh
 * @since  2007/03/01
 */
public class CAM extends AbstractMetric implements IMetric, IUnaryMetric {
	public CAM(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	public String getDefinition() {
		String def =
			"Computes the relateness among methods of the class based upon the parameter list of the methods. The "
					+ "metrics is computed using the summation of the intersection of parameters of a method with the maximum independant set "
					+ "of all parameter types in the class";
		System.out.println(def);
		return def;
	}
	public double concretelyCompute(IFirstClassEntity firstClassEntity) {
		final Collection implementedMethods =
			this.classPrimitives.listOfOverriddenAndConcreteMethods(firstClassEntity);

		double camValue = 0;

		final Collection totalParam = new ArrayList();
		for (final Iterator iterMethod = implementedMethods.iterator(); iterMethod
			.hasNext();) {
			IOperation m = (IOperation) iterMethod.next();

			for (final Iterator IterParam =
				m.getIteratorOnConstituents(IParameter.class); IterParam
				.hasNext();) {

				IParameter p = (IParameter) IterParam.next();
				totalParam.add(p);
			}
		}
		if (totalParam.size() > 0) {
			for (final Iterator iterMethod = implementedMethods.iterator(); iterMethod
				.hasNext();) {

				IOperation m = (IOperation) iterMethod.next();

				final Collection mParam = new ArrayList();
				for (final Iterator IterParam =
					m.getIteratorOnConstituents(IParameter.class); IterParam
					.hasNext();) {

					IParameter p = (IParameter) IterParam.next();
					mParam.add(p);
				}
				camValue =
					camValue
							+ ((double) (super.operators.intersection(
								mParam,
								totalParam)).size())
							/ (double) totalParam.size();
			}
			return camValue;
		}
		else {
			return 0;
		}
	}
}
