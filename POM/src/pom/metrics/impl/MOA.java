package pom.metrics.impl;

import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IField;
import padl.kernel.IGhost;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Foutse Khomh
 * @since  2007/03/01
 */
public class MOA extends AbstractMetric implements IMetric, IUnaryMetric {
	public MOA(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	public String getDefinition() {
		final String def =
			"Number of data declarations whose types are user defined classes";
		return def;
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		final List implantedFields =
			this.classPrimitives.listOfImplementedFields(firstClassEntity);

		double countMOA = 0;
		final Iterator iterField = implantedFields.iterator();
		while (iterField.hasNext()) {
			final IField field = (IField) iterField.next();
			if (!(this.abstractLevelModel.getTopLevelEntityFromID(field
				.getType()) instanceof IGhost)) {

				countMOA = countMOA + 1;
			}
		}
		return countMOA;
	}

}
