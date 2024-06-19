package pom.metrics;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import padl.FileAccessException;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.util.Util;
import util.io.NamedInputStream;
import util.io.Output;
import util.io.SubtypeLoader;
import com.ibm.toad.cfparse.ClassFile;

public class Repository {
	private static Repository UniqueInstance;
	public static Repository getInstance(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		if (Repository.UniqueInstance == null
				|| Repository.UniqueInstance.abstractLevelModel != anAbstractLevelModel) {

			Repository.UniqueInstance =
				new Repository(aFileRepository, anAbstractLevelModel);
		}
		return Repository.UniqueInstance;
	}

	private final IAbstractLevelModel abstractLevelModel;
	private final List binaryMetricList = new ArrayList();
	private final Map mapOfMetrics = new HashMap();
	private final List unaryMetricList = new ArrayList();
	private Repository(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		this.abstractLevelModel = anAbstractLevelModel;
		try {
			final NamedInputStream[] inputStreams = aFileRepository.getFiles();
			final ClassFile[] classFiles =
				SubtypeLoader.loadSubtypesFromStream(
					"pom.metrics.IMetric",
					inputStreams,
					"pom.metrics.impl",
					".class");
			for (int i = 0; i < classFiles.length; i++) {
				try {
					final Class metricClass =
						Class.forName(classFiles[i].getName());
					final String metricSimpleName =
						Util.computeSimpleName(metricClass.getName());

					this.mapOfMetrics.put(metricSimpleName, metricClass
						.getConstructor(
							new Class[] { IFileRepository.class,
									IAbstractLevelModel.class })
						.newInstance(
							new Object[] { aFileRepository,
									anAbstractLevelModel }));

					if (IUnaryMetric.class.isAssignableFrom(metricClass)) {
						this.unaryMetricList.add(metricSimpleName);
					}
					//Aminata - 17/05/2011
					//some are IUnaryMetric and also IBinaryMetric
					//so I removed this else -  
					//else if (IBinaryMetric.class.isAssignableFrom(metricClass)) {
					if (IBinaryMetric.class.isAssignableFrom(metricClass)) {
						this.binaryMetricList.add(metricSimpleName);
					}
				}
				catch (final ClassNotFoundException cnfe) {
				}
				catch (final IllegalArgumentException iae) {
					iae.printStackTrace(Output.getInstance().errorOutput());
				}
				catch (final SecurityException se) {
					se.printStackTrace(Output.getInstance().errorOutput());
				}
				catch (final InstantiationException ie) {
					ie.printStackTrace(Output.getInstance().errorOutput());
				}
				catch (final IllegalAccessException iae) {
					iae.printStackTrace(Output.getInstance().errorOutput());
				}
				catch (final InvocationTargetException ite) {
					ite.printStackTrace(Output.getInstance().errorOutput());
				}
				catch (final NoSuchMethodException nsme) {
					nsme.printStackTrace(Output.getInstance().errorOutput());
				}
			}
		}
		catch (final FileAccessException e) {
			e.printStackTrace();
		}
	}
	public double compute(final String aMetricName, final IFirstClassEntity anEntity) {
		final IMetric metric = this.getMetricInstance(aMetricName);

		if (metric instanceof IUnaryMetric) {
			return ((IUnaryMetric) metric).compute(anEntity);
		}

		return -1;
	}
	public double compute(
		final String aMetricName,
		final IFirstClassEntity anEntityA,
		final IFirstClassEntity anEntityB) {

		final IMetric metric = this.getMetricInstance(aMetricName);

		if (metric instanceof IBinaryMetric) {
			return ((IBinaryMetric) metric).compute(anEntityA, anEntityB);
		}

		return -1;
	}
	public IMetric getMetricInstance(final String aMetricName) {
		return (IMetric) this.mapOfMetrics.get(aMetricName);
	}
	public List listOfBinaryMetrics() {
		return this.binaryMetricList;
	}
	public List listOfMetrics() {
		return new ArrayList(this.mapOfMetrics.keySet());
	}
	public List listOfUnaryMetrics() {
		return this.unaryMetricList;
	}
}
