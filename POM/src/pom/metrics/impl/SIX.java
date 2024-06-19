/**
 * SIX - Specialisation IndeX
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
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class SIX extends AbstractMetric implements IMetric, IUnaryMetric {
	public SIX(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		double nmoTimesDit =
			super.getUnaryMetricInstance("NMO").compute(firstClassEntity)
					* super.getUnaryMetricInstance("DIT").compute(firstClassEntity);
		double nbMethodsOfEntity =
			super.classPrimitives.listOfAllMethods(firstClassEntity).size();
		if (nbMethodsOfEntity == 0)
			nbMethodsOfEntity = 1;
		return nmoTimesDit / nbMethodsOfEntity;
	}

	public String getDefinition() {
		String def = "Returns the SIX (Specialisation IndeX) of an entity.";
		System.out.println(def);
		return def;
	}

	//	public List listOfElements(IEntity entity)
	//	{
	//		List list1 = new ArrayList();
	//		
	//		list1.addAll(super.getInstanceOfUnaryMetric("NMO").listOfElements(entity));
	//		list1.addAll(super.getInstanceOfUnaryMetric("DIT").listOfElements(entity));
	//		
	//		List list2 = new ArrayList();
	//		list2.addAll(super.classPrimitives
	//				.allEntityMethods(entity));
	//		
	//		List list = new ArrayList();
	//		
	//		
	//		for(int i = 0; i < list2.size(); i ++)
	//		{
	//			boolean unique = true;
	//			for(int j = 0; j < list1.size(); j++)
	//			{
	//				if(list1.get(j).equals(list2.get(i)))
	//				unique = false;
	//			}
	//			
	//			if(unique)
	//				list.add(list2.get(i));
	//		}
	//		
	//		return list;
	//	}
}