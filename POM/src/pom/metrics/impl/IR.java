package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IOperation;
import padl.kernel.IConstituent;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IField;
import padl.kernel.IGhost;
import padl.kernel.IMethodInvocation;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Alban Tiberghien
 * @since 2008/08/08
 */
public class IR extends AbstractMetric implements IMetric, IUnaryMetric {
	public IR(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public String getDefinition() {
		String def = "Inheritance Ratio";
		System.out.println(def);
		return def;
	}

	public double concretelyCompute(final IFirstClassEntity anEntity) {
		Set protectedMembers = new HashSet();

		final List parents =
			super.classPrimitives.listOfAllDirectParents(anEntity);
		final Iterator iterator = parents.iterator();
		while (iterator.hasNext()) {
			final IFirstClassEntity parent = (IFirstClassEntity) iterator.next();
			if (!(parent instanceof IGhost)) {
				final List allMembers = new ArrayList();
				allMembers.addAll(super.classPrimitives
					.listOfImplementedFields(parent));
				allMembers.addAll(super.classPrimitives
					.listOfDeclaredMethods(parent));

				final Iterator iterator2 = allMembers.iterator();
				while (iterator2.hasNext()) {
					final IConstituent c = (IConstituent) iterator2.next();
					if ((c instanceof IField && c.isProtected())
							|| (c instanceof IOperation && !c.isPrivate())) {
						protectedMembers.add(c.getDisplayName());
					}
				}
			}
		}
		int acceptedBequest = 0;

		Collection allMethods =
			super.classPrimitives.listOfAllMethods(anEntity);
		for (Iterator iter = allMethods.iterator(); iter.hasNext();) {

			IOperation method = (IOperation) iter.next();

			Iterator iter2 =
				method.getIteratorOnConstituents(IMethodInvocation.class);
			if (!iter2.hasNext()) {
				acceptedBequest--;
			}
			else {

				if (protectedMembers.contains(method.getDisplayName())) {
					acceptedBequest++;
				}

				while (iter2.hasNext()) {

					IMethodInvocation mi = (IMethodInvocation) iter2.next();

					IOperation calledMethod = mi.getCalledMethod();
					IField calledField = mi.getFirstCallingField();
					if ((calledMethod != null && protectedMembers
						.contains(calledMethod.getDisplayName()))
							|| (calledField != null && protectedMembers
								.contains(calledField.getDisplayName()))) {
						acceptedBequest++;
					}
				}
			}
		}
		return acceptedBequest;
	}
}