/**
 * NMD - Number of Methods Declared
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

public class NMD extends AbstractMetric implements IMetric, IUnaryMetric {
	public NMD(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	/**
	 * @param iEntity
	 * @return double : number of methods declared COMMENTS [warning] Number of
	 *         methods declared + CONSTRUCTOR of the class
	 */
	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return listOfElements(firstClassEntity).size();
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		List c = super.classPrimitives.listOfDeclaredMethods(firstClassEntity);
		//
		//		for (Iterator it = c.iterator(); it.hasNext();) {
		//			System.out.println(((IConstituent) it.next()).getName());
		//		}
		return c;
	}

	public String getDefinition() {
		return "number of methods declared";
	}
}
