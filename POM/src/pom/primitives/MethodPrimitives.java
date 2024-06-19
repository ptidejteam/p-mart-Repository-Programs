package pom.primitives;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IOperation;
import padl.kernel.IElement;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IField;
import padl.kernel.IMethodInvocation;
import pom.util.CacheManager;
import pom.util.NoSuchValueInCacheException;

/**
 * @author Farouk Zaidi
 * @since  2004/02/05
 * 
 * @author Yann
 */
public class MethodPrimitives extends Primitives {
	private static MethodPrimitives UniqueInstance;
	public static MethodPrimitives getInstance(
		final IAbstractLevelModel anAbstractLevelModel) {
		if (MethodPrimitives.UniqueInstance == null
				|| MethodPrimitives.UniqueInstance.abstractLevelModel != anAbstractLevelModel) {

			MethodPrimitives.UniqueInstance =
				new MethodPrimitives(anAbstractLevelModel);
		}
		return MethodPrimitives.UniqueInstance;
	}
	private final CacheManager cacheManger;

	private MethodPrimitives(final IAbstractLevelModel anAbstractLevelModel) {
		super(anAbstractLevelModel);
		this.cacheManger = CacheManager.getInstance(anAbstractLevelModel);
	}
	public int numberOfUsesByFieldsOrMethods(
		final IFirstClassEntity anEntity,
		final IFirstClassEntity anotherEntity) {

		try {
			return ((Integer) this.cacheManger.retrievePrimitiveResult(
				"countNumberOfUsesByFieldsOrMethods",
				anEntity,
				anotherEntity)).intValue();
		}
		catch (final NoSuchValueInCacheException e) {
			final List invokedMethod = new ArrayList();
			final List usedFields = new ArrayList();

			final Iterator iteratorOnAbstractMethods =
				anEntity.getIteratorOnConstituents(IOperation.class);
			while (iteratorOnAbstractMethods.hasNext()) {
				final IOperation method =
					(IOperation) iteratorOnAbstractMethods.next();
				invokedMethod.addAll(this.listOfSisterMethodCalledByMethod(
					anotherEntity,
					method));
				usedFields.addAll(this.listOfFieldsUsedByMethod(
					anotherEntity,
					method));
			}

			int result = invokedMethod.size() + usedFields.size();
			this.cacheManger.cachePrimitiveResult(
				"countNumberOfUsesByFieldsOrMethods",
				anEntity,
				anotherEntity,
				new Integer(result));
			return result;
		}
	}
	/*
	 * Farouk 2004/04/02, 14h52 
	 * Modified to consider multiple access to fields.
	 */
	public List listOfFieldsUsedByMethod(
		final IFirstClassEntity anEntity,
		final IOperation aMethod) {

		try {
			return (List) this.cacheManger.retrievePrimitiveResult(
				"listFieldsUsedByMethod",
				anEntity,
				aMethod);
		}
		catch (final NoSuchValueInCacheException e) {
			final ClassPrimitives classPrimitives =
				ClassPrimitives.getInstance(this.abstractLevelModel);
			final List usedFields = new ArrayList();
			final List entityFields =
				classPrimitives.listOfImplementedFields(anEntity);

			for (final Iterator iteratorOnConstituents =
				aMethod.getIteratorOnConstituents(); iteratorOnConstituents
				.hasNext();) {
				final IElement element =
					(IElement) iteratorOnConstituents.next();
				if (element instanceof IMethodInvocation) {
					final IMethodInvocation mi = (IMethodInvocation) element;

					//				mi.getFieldDeclaringEntity()
					//				mi.getFirstCallingField()

					final Iterator iterCallingField =
						mi.getIteratorOnCallingFields();
					while (iterCallingField.hasNext()) {
						final IField aField = (IField) iterCallingField.next();
						if (aField != null) {
							if (entityFields.contains(aField)) {
								usedFields.add(aField);
							}
						}
					}
				}
			}

			this.cacheManger.cachePrimitiveResult(
				"listFieldsUsedByMethod",
				anEntity,
				aMethod,
				usedFields);
			return usedFields;
		}
	}
	public List listOfSisterMethodCalledByMethod(
		final IFirstClassEntity anEntity,
		final IOperation aMethod) {

		try {
			return (List) this.cacheManger.retrievePrimitiveResult(
				"listOfSisterMethodCalledByMethod",
				anEntity,
				aMethod);
		}
		catch (final NoSuchValueInCacheException e) {
			final List invokedMethods = new ArrayList();
			final Collection concreteMethods =
				ClassPrimitives
					.getInstance(this.abstractLevelModel)
					.listOfOverriddenAndConcreteMethods(anEntity);

			final Iterator iteratorOnMethodInvocations =
				aMethod.getIteratorOnConstituents(IMethodInvocation.class);
			while (iteratorOnMethodInvocations.hasNext()) {
				final IMethodInvocation mi =
					(IMethodInvocation) iteratorOnMethodInvocations.next();

				if (mi.getTargetEntity() != null
						&& mi.getTargetEntity().equals(anEntity)) {

					if (concreteMethods.contains(mi.getCalledMethod())) {
						invokedMethods.add(mi.getCalledMethod());
					}
				}
			}

			this.cacheManger.cachePrimitiveResult(
				"listOfSisterMethodCalledByMethod",
				anEntity,
				aMethod,
				invokedMethods);
			return invokedMethods;
		}
	}
}
