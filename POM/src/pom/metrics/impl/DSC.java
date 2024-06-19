package pom.metrics.impl;

import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Foutse Khomh
 * @since  2007/03/01
 */
public class DSC extends AbstractMetric implements IMetric, IUnaryMetric {
	public DSC(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	public double concretelyCompute(final IFirstClassEntity anEntity) {
		//       int numb=0;
		//       final Iterator iter =
		//			this.anAbstractLevelModel.getIteratorOnTopLevelEntities(); 
		//       while (iter.hasNext()) {
		//			final IEntity anElement = (IEntity) iter.next();
		//			
		//			if(anElement instanceof IClass){
		//				numb=numb+1;
		//			}
		//			}
		//       
		//		return numb;

		return this.abstractLevelModel.getNumberOfTopLevelEntities();
	}
	public String getDefinition() {
		final String def = "Total number of classes in the design";
		return def;
	}
}
