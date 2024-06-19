/**
 * DCMEC - Descendants Class-Method Export Coupling
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IConstructor;
import padl.kernel.IElement;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IParameter;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class DCMEC extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * @param anAbstractLevelModel
	 */
	public DCMEC(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return listOfElements(firstClassEntity).size();
	}

	public String getDefinition() {
		String def =
			"Returns the DCMEC (Descendants Class-Method Export Coupling) of one entity.";
		System.out.println(def);
		return def;
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		List dcmecList = new ArrayList();

		final char[] entityName = firstClassEntity.getID();

		final List descendents =
			super.classPrimitives.listOfDescendents(firstClassEntity);
		for (final Iterator iterDesc = descendents.iterator(); iterDesc
			.hasNext();) {
			final IFirstClassEntity aDesc = (IFirstClassEntity) iterDesc.next();
			final Collection newMethods =
				super.classPrimitives.listOfNewMethods(aDesc);
			for (final Iterator iter = newMethods.iterator(); iter.hasNext();) {
				final IConstructor method = (IConstructor) iter.next();
				for (final Iterator iterator =
					method.getIteratorOnConstituents(); iterator.hasNext();) {
					final IElement element = (IElement) iterator.next();
					if (element instanceof IParameter) {
						final IParameter parameter = (IParameter) element;
						if (Arrays.equals(parameter.getTypeName(), entityName)) {
							dcmecList.add(iterator);
						}
					}
				}
			}
		}
		return dcmecList;
	}
}
