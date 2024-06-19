/**
 * CBO - Coupling Between Objects
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
import pom.metrics.IBinaryMetric;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class CBO extends AbstractMetric implements IMetric, IUnaryMetric,
		IBinaryMetric {

	private static IBinaryMetric CBOinMetric;
	private static IBinaryMetric CBOoutMetric;

	public CBO(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		// Yann 2009/07/29: Cache...again!
		// I don't call the compute(anEntity, otherEntity)
		// method anymore because then the values for CBOin
		// and CBOout (unary) would not be properly stored!
		// Then, they would be recomputed again and again.
		// Instead, I call CBOin (unary) and then CBOout
		// (unary) and sum the two values.
		//	final Iterator iterator =
		//		super.classPrimitives.getIteratorOnTopLevelEntities();
		//	while (iterator.hasNext()) {
		//		final IFirstClassEntity otherEntity =
		//			(IFirstClassEntity) iterator.next();
		//
		//		if (!otherEntity.equals(anEntity)) {
		//			result += this.compute(anEntity, otherEntity);
		//		}
		//	}

		if (CBO.CBOinMetric == null) {
			CBO.CBOinMetric = super.getBinaryMetricInstance("CBOin");
		}
		if (CBO.CBOoutMetric == null) {
			CBO.CBOoutMetric = super.getBinaryMetricInstance("CBOout");
		}

		final double result =
			((IUnaryMetric) CBO.CBOinMetric).compute(anEntity)
					+ ((IUnaryMetric) CBO.CBOoutMetric).compute(anEntity);
		return result;
	}
	protected double concretelyCompute(
		final IFirstClassEntity anEntityA,
		final IFirstClassEntity anEntityB) {

		if (CBO.CBOinMetric == null) {
			CBO.CBOinMetric = super.getBinaryMetricInstance("CBOin");
		}
		if (CBO.CBOoutMetric == null) {
			CBO.CBOoutMetric = super.getBinaryMetricInstance("CBOout");
		}

		// Yann 2009/03/11
		// CBO is the only one to know that it decomposes in 
		// the sum of CBOin and CBOout and that these metrics
		// can be used with (A,B) or (B,A) to obtain the expected
		// result. So, it tries to minimise the computation time
		// by using whatever value is available in the cache.

		double cboInValue = 0;
		if (this.cacheManager.isBinaryMetricValueInCache(
			CBO.CBOinMetric,
			anEntityA,
			anEntityB)) {

			cboInValue =
				super.getBinaryMetricInstance("CBOin").compute(
					anEntityA,
					anEntityB);
		}
		else
		//	if (this.cacheManager.isSymetricalBinaryMetricValueInCache(
		//	CBO.CBOoutClass,
		//	anEntityB,
		//	anEntityA)) 
		{
			cboInValue =
				super.getBinaryMetricInstance("CBOout").compute(
					anEntityB,
					anEntityA);
		}

		double cboOutValue = 0;
		if (this.cacheManager.isBinaryMetricValueInCache(
			CBO.CBOoutMetric,
			anEntityA,
			anEntityB)) {

			cboOutValue =
				super.getBinaryMetricInstance("CBOout").compute(
					anEntityA,
					anEntityB);
		}
		else
		//	if (this.cacheManager.isSymetricalBinaryMetricValueInCache(
		//	CBO.CBOinClass,
		//	anEntityB,
		//	anEntityA)) 
		{
			cboOutValue =
				super.getBinaryMetricInstance("CBOin").compute(
					anEntityB,
					anEntityA);
		}

		final double cbo = cboInValue + cboOutValue;
		return cbo;
	}
	public String getDefinition() {
		final String def = "Coupling Between Objects of one entity";
		return def;
	}
	public boolean isSymmetrical() {
		return true;
	}
}
