package pom.metrics.impl;

import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IField;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;
import util.lang.Modifier;

/**
 * @author Foutse Khomh
 * @since  2007/03/01
 */
public class DAM extends AbstractMetric implements IMetric, IUnaryMetric {

	public DAM(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		final List Attrib = listOfElements(firstClassEntity);
		final int size = Attrib.size();

		if (size > 0) {
			double priv = 0;
			for (int i = 0; i < size; i++) {
				final IField aField = (IField) Attrib.get(i);
				if (aField.isPrivate()
						|| (aField.getVisibility() & Modifier.PROTECTED) == Modifier.PROTECTED) {

					priv = priv + 1;
				}

			}
			return priv / (double) size;
		}
		else
			return 0;

	}

	public String getDefinition() {
		String def =
			"Returns the ratio of the number of private(protected) Attributes to the total number of Attributes declared"
					+ " in a class.";
		System.out.println(def);
		return def;
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		return super.classPrimitives.listOfImplementedFields(firstClassEntity);
	}

}
