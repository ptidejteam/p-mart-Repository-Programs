/**
 * WMC - Weight of Methods Computed with method's complexity considered
 * to be unity [Chidamber and Kememerer, 1994]
 * 
 * @author Jean-Yves Guyomarc'h
 * @since  2006/03/10
 * 
 */

package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IOperation;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class WMC1 extends AbstractMetric implements IMetric, IUnaryMetric {
	public WMC1(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	/**
	 * The default constructor is considered during the computation even if not explcitely declared
	 */
	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return this.listOfElements(firstClassEntity).size();
	}

	public String getDefinition() {
		String def =
			"Computes the weight of an entity considering the complexity of a method to be unity.";
		System.out.println(def);
		return def;
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		List list = new ArrayList();

		final Iterator iterAbstractMethod =
			firstClassEntity.getIteratorOnConstituents(IOperation.class);

		while (iterAbstractMethod.hasNext()) {
			list.add(iterAbstractMethod.next());
		}
		return list;
	}
}
