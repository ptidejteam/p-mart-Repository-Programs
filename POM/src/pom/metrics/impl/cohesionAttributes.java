package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IOperation;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IField;
import padl.kernel.IMethodInvocation;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class cohesionAttributes extends AbstractMetric implements IMetric,
		IUnaryMetric {

	public cohesionAttributes(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	public double concretelyCompute(final IFirstClassEntity anEntity) {
		final List methods =
			this.classPrimitives.listOfDeclaredMethods(anEntity);
		final List fields =
			this.classPrimitives.listOfInheritedAndImplantedFields(anEntity);

		if (fields.size() == 0) {
			return 0;
		}

		double result = 0;
		final Iterator iterMethod = methods.iterator();
		while (iterMethod.hasNext()) {
			final IOperation aMethod = (IOperation) iterMethod.next();

			result +=
				this.listUsedInheritedAndImplantedFieldsByMethod(
					anEntity,
					aMethod).size();
		}
		return result / (methods.size() * fields.size());
	}
	public String getDefinition() {
		return "";
	}
	private List listUsedInheritedAndImplantedFieldsByMethod(
		final IFirstClassEntity anEntity,
		final IOperation aMethod) {

		final List usedFields = new ArrayList();
		final List entityFields =
			this.classPrimitives.listOfInheritedAndImplantedFields(anEntity);

		final Iterator iteratorOnMethodInvocations =
			aMethod.getIteratorOnConstituents(IMethodInvocation.class);
		while (iteratorOnMethodInvocations.hasNext()) {
			final IMethodInvocation methodInvocation =
				(IMethodInvocation) iteratorOnMethodInvocations.next();

			final Iterator iteratorOnCallingFields =
				methodInvocation.getIteratorOnCallingFields();
			while (iteratorOnCallingFields.hasNext()) {
				final IField aField = (IField) iteratorOnCallingFields.next();
				if (aField != null && entityFields.contains(aField)) {
					usedFields.add(aField);
				}
			}
		}
		return usedFields;
	}
}
