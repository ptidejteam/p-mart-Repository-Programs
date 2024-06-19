/**
 * NMA - Number of Methods A******  Ask Yann
 * 
 * @author Moha N. & Huynh D.L.
 * @since  2005/08/18
 * 
 * Modifications made to fit the new architecture
 */

package pom.metrics.impl;

import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class NAD extends AbstractMetric implements IMetric, IUnaryMetric {
	public NAD(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return listOfElements(firstClassEntity).size();
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		return super.classPrimitives.listOfImplementedFields(firstClassEntity);
	}

	public String getDefinition() {
		return "number of attributes declared";
	}
}
