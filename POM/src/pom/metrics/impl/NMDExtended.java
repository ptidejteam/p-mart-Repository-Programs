/**
 * NMD - Number of Methods Declared
 * 
 * @author Moha N. & Huynh D.L.
 * @since  2005/08/18
 * 
 * Modifications made to fit the new architecture
 */

package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IInterface;
import padl.kernel.IMemberClass;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class NMDExtended extends AbstractMetric implements IMetric,
		IUnaryMetric {

	public NMDExtended(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	/**
	 * @param iEntity
	 * @return double : number of methods declared COMMENTS [warning] Number of
	 *         methods declared + CONSTRUCTOR of the class
	 *         + number of methods of the member classes
	 */
	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return this.listOfElements(firstClassEntity).size();
	}
	private List listOfElements(IFirstClassEntity firstClassEntity) {
		List implementedMethods =
			super.classPrimitives.listOfDeclaredMethods(firstClassEntity);

		List results = new ArrayList();
		final Iterator iteratorOnMemberClasses =
			firstClassEntity.getIteratorOnConstituents(IMemberClass.class);
		while (iteratorOnMemberClasses.hasNext()) {
			final IMemberClass aMemberClass =
				(IMemberClass) iteratorOnMemberClasses.next();

			if (aMemberClass.getDisplayName().length() > 2) {
				results.addAll(this.listOfElements(aMemberClass));
			}
		}

		final Iterator iteratorOnMemberInterfaces =
			firstClassEntity.getIteratorOnConstituents(IInterface.class);
		while (iteratorOnMemberInterfaces.hasNext()) {
			final IInterface aInterface =
				(IInterface) iteratorOnMemberInterfaces.next();
			results.addAll(this.listOfElements(aInterface));
		}

		// concat the list of methods of the class and 
		// the list of methods of all the member classes.
		results.addAll(implementedMethods);
		return results;
	}
	public String getDefinition() {
		return "Number of methods declared in the class and in its member classes";
	}
}
