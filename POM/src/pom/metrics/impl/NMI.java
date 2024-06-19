/**
 * NMI - Number Of Methods Inherited
 * 
 * @author Farouk ZAIDI
 * @since  2004/01/31 
 * 
 * @author Duc-Loc Huynh
 * @since  2005/08/18
 * 
 * Modifications made to fit the new architecture
 */

package pom.metrics.impl;

import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class NMI extends AbstractMetric implements IMetric, IUnaryMetric {
	public NMI(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return super.classPrimitives.listOfInheritedMethods(firstClassEntity).size();
	}
	public String getDefinition() {
		String def =
			"Returns the NMI (Number of Methods Inherited)" + " of an entity."
					+ " \n Constructors or not considered as method,"
					+ " they are not count in the result of the metric !";
		System.out.println(def);
		return def;
	}
}