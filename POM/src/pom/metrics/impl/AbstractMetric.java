package pom.metrics.impl;

import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IBinaryMetric;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;
import pom.metrics.Repository;
import pom.operators.Operators;
import pom.primitives.ClassPrimitives;
import pom.primitives.MethodPrimitives;
import pom.util.CacheManager;
import pom.util.NoSuchValueInCacheException;

/**
 * 
 * @author Yann
 * @author Foutse
 * 
 * DO NOT EVER NEVER EVER USE THIS CLASS TO IMPLEMENT
 * CONVENIENCE METHODS. USE THE PRIMITIVES IN pom.primitives.
 * IN OTHER WORD, DO NOT TOUCH THIS CLASS.
 *
 */
abstract class AbstractMetric {
	protected final IAbstractLevelModel abstractLevelModel;

	/**
	 * Cache manager.
	 */
	protected final CacheManager cacheManager;

	/**
	 * ClassPrimitives that allows to extract primitives from the model.
	 * (The model is the analysed program (or a set of classes) and described by
	 * the metamodel.)
	 */
	protected final ClassPrimitives classPrimitives;

	protected final IFileRepository fileRepository;

	/**
	 * MethodPrimitives that calculates primitives for a specified method.
	 */
	protected final MethodPrimitives methodPrimitives;

	/**
	 * Operators. Useful for operations on sets
	 */
	protected final Operators operators;

	/**
	 * @author Yann
	 * @since 24/08/2005
	 * 
	 * A private constructor to make sure classes that extends this class can't
	 * use an empty constructor
	 * 
	 * @param anAbstractLevelModel
	 */
	protected AbstractMetric(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		this.fileRepository = aFileRepository;
		this.abstractLevelModel = anAbstractLevelModel;
		this.operators = Operators.getInstance();
		this.classPrimitives =
			ClassPrimitives.getInstance(this.abstractLevelModel);
		this.methodPrimitives =
			MethodPrimitives.getInstance(this.abstractLevelModel);
		this.cacheManager = CacheManager.getInstance(this.abstractLevelModel);
	}
	public final double compute(final IFirstClassEntity anEntity) {
		try {
			return this.cacheManager.retrieveUnaryMetricValue(
				(IMetric) this,
				anEntity);
		}
		catch (final NoSuchValueInCacheException e) {
			double result = this.concretelyCompute(anEntity);
			this.cacheManager.cacheUnaryMetricValue(
				(IMetric) this,
				anEntity,
				result);
			return result;
		}
	}
	public final double compute(
		final IFirstClassEntity anEntity,
		final IFirstClassEntity anotherEntity) {

		try {
			return this.cacheManager.retrieveBinaryMetricValue(
				(IBinaryMetric) this,
				anEntity,
				anotherEntity);
		}
		catch (final NoSuchValueInCacheException e) {
			double result = this.concretelyCompute(anEntity, anotherEntity);
			this.cacheManager.cacheBinaryMetricValue(
				(IBinaryMetric) this,
				anEntity,
				anotherEntity,
				result);
			return result;
		}
	}
	protected abstract double concretelyCompute(final IFirstClassEntity anEntity);
	protected double concretelyCompute(
		final IFirstClassEntity anEntity,
		final IFirstClassEntity anotherEntity) {

		// Auto-generated method stub.
		// Useless for unary metrics.
		return 0;
	}
	protected IBinaryMetric getBinaryMetricInstance(final String metricName) {
		return (IBinaryMetric) Repository.getInstance(
			this.fileRepository,
			this.abstractLevelModel).getMetricInstance(metricName);
	}
	protected IUnaryMetric getUnaryMetricInstance(final String aMetricName) {
		return (IUnaryMetric) Repository.getInstance(
			this.fileRepository,
			this.abstractLevelModel).getMetricInstance(aMetricName);
	}
	public boolean isSymmetrical() {
		return false;
	}
}
