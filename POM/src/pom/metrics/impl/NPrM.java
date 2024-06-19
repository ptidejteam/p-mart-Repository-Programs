package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IConstituent;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Alban Tiberghien
 * @since 2008//08/04
 */
public class NPrM extends AbstractMetric implements IMetric, IUnaryMetric {
	public NPrM(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public String getDefinition() {
		String def = "Compute the number protected members";
		System.out.println(def);
		return def;
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		int cpt = 0;

		final List protectedMembers = new ArrayList();
		protectedMembers.addAll(super.classPrimitives
			.listOfImplementedFields(firstClassEntity));
		protectedMembers.addAll(super.classPrimitives
			.listOfDeclaredMethods(firstClassEntity));

		final Iterator iterator = protectedMembers.iterator();
		while (iterator.hasNext()) {
			final IConstituent c = (IConstituent) iterator.next();
			if (c.isProtected())
				cpt++;
		}

		return cpt;
	}

}
