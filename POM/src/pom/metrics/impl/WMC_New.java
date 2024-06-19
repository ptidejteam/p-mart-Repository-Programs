/**
 * WMC - Weighted Methods Per Class Computed as the sum of VG of all methods implemented by the class - VG(m) is McCabe's cyclomatic complexity number.
 * It is given by the size of the basis set of paths through the control flow graph of function m. For a single entry, single-exit control flow graph consisting of e edges and n nodes, VG(m) =e-m+n 
 * [T. McCabe]
 * VG(m) can be computed as the number of decisions node +1
 * ref: Bruntink, M., Deursen, A.V.: Predicting class testability using object oriented metrics. In: Proceedings of the IEEE International Workshop on Source Code Analysis and Manipulation, pp. 136–145 (2004) 
 * @author Aminata Sabané
 * @since  2012/06/05
 * 
 */

package pom.metrics.impl;

import java.util.Iterator;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IConditional;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IIfInstruction;
import padl.kernel.IOperation;
import padl.kernel.ISwitchInstruction;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class WMC_New extends AbstractMetric implements IMetric, IUnaryMetric {
	public WMC_New(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	/**
	 * The default constructor is considered during the computation even if not explcitely declared
	 */
	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		double value=0;
		Iterator iterOnMethods=this.classPrimitives.listOfDeclaredMethods(firstClassEntity).iterator();
		while(iterOnMethods.hasNext()){
			//the complexity of a method is the number of branch decisions +1
			//then the complexity of a method without any branch decision is 1
			
			double methodCyclomaticComplexity=1;
			IOperation operation=(IOperation)iterOnMethods.next();
			Iterator iteratorOnConditional =operation.getConcurrentIteratorOnConstituents(IConditional.class);
			while(iteratorOnConditional.hasNext()){
				IConditional conditionStatement=(IConditional) iteratorOnConditional.next();
				if(conditionStatement instanceof IIfInstruction){
					methodCyclomaticComplexity++;
				}else{
					 
					methodCyclomaticComplexity+=((ISwitchInstruction)conditionStatement).getRange();
				}
			}
			
			//aggregate method complexity into a class
			value+=methodCyclomaticComplexity;
		}
		return value;
	}

	public String getDefinition() {
		String def =
			"Computes the weight of an entity considering the complexity of a method to be unity.";
		System.out.println(def);
		return def;
	}

	
}
