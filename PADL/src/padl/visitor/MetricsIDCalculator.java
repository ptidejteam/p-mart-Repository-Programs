/*
 * Created on 2004-01-15
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package padl.visitor;

import java.util.Iterator;
import java.util.Vector;

import padl.kernel.IAbstractLevelModel;
import padl.kernel.IAggregation;
import padl.kernel.IAssociation;
import padl.kernel.IClass;
import padl.kernel.IComposition;
import padl.kernel.IConstituent;
import padl.kernel.IConstructor;
import padl.kernel.IContainerAggregation;
import padl.kernel.IContainerComposition;
import padl.kernel.ICreation;
import padl.kernel.IDelegatingMethod;
import padl.kernel.IDesignMotifModel;
import padl.kernel.IField;
import padl.kernel.IGetter;
import padl.kernel.IGhost;
import padl.kernel.IInterface;
import padl.kernel.IMemberClass;
import padl.kernel.IMemberGhost;
import padl.kernel.IMemberInterface;
import padl.kernel.IMethod;
import padl.kernel.IMethodInvocation;
import padl.kernel.IPackage;
import padl.kernel.IParameter;
import padl.kernel.ISetter;
import padl.kernel.IUseRelationship;
import padl.kernel.IWalker;
import util.io.Output;

/**
 * @author Khashayar Khosravi
 */
public class MetricsIDCalculator implements IWalker {
	private int anmvc;
	private int numberOfFields;
	private Vector numberOfFieldsPerClass;

	public void close(final IAbstractLevelModel anAbstractLevelModel) {
		int sum = 0;
		final Iterator iterator = this.numberOfFieldsPerClass.iterator();
		while (iterator.hasNext()) {
			sum += ((Integer) iterator.next()).intValue();
		}
		this.anmvc = sum / this.numberOfFieldsPerClass.size();
		Output.getInstance().normalOutput().println(
			"Average Number of Member Variables per Class: " + this.anmvc);
	}
	public void close(final IClass aClass) {
		this.numberOfFieldsPerClass.add(new Integer(this.numberOfFields));
	}
	public void close(final IConstructor aConstructor) {
	}
	public void close(final IDelegatingMethod aDelegatingMethod) {
	}
	public void close(final IDesignMotifModel aPatternModel) {
	}
	public void close(final IGetter aGetter) {
	}
	public void close(final IGhost aGhost) {
	}
	public void close(final IInterface anInterface) {
	}
	public void close(final IMemberClass aMemberClass) {
	}
	public void close(final IMemberGhost aMemberGhost) {
	}
	public void close(final IMemberInterface aMemberInterface) {
	}
	public void close(final IMethod aMethod) {
	}
	public void close(final IPackage aPackage) {
	}
	public void close(final ISetter aSetter) {
	}
	public String getName() {
		return "ID metric";
	}
	public Object getResult() {
		return new Integer(this.anmvc);
	}
	public void open(final IAbstractLevelModel anAbstractLevelModel) {
		this.numberOfFieldsPerClass = new Vector();
	}
	public void open(final IClass aClass) {
		this.numberOfFields = 0;
	}
	public void open(final IConstructor p) {
	}
	public void open(final IDelegatingMethod aDelegatingMethod) {
	}
	public void open(final IDesignMotifModel aPatternModel) {
	}
	public void open(final IGetter aGetter) {
	}
	public void open(final IGhost aGhost) {
	}
	public void open(final IInterface anInterface) {
	}
	public void open(final IMemberClass aMemberClass) {
	}
	public void open(final IMemberGhost aMemberGhost) {
	}
	public void open(final IMemberInterface aMemberInterface) {
	}
	public void open(final IMethod aMethod) {
	}
	public void open(final IPackage aPackage) {
	}
	public void open(final ISetter aSetter) {
	}
	public void reset() {
	}
	public void visit(final IAggregation anAggregation) {
	}
	public void visit(final IAssociation anAssociation) {
	}
	public void visit(final IComposition aComposition) {
	}
	public void visit(final IContainerAggregation aContainerAggregation) {
	}
	public void visit(final IContainerComposition aContainerComposition) {
	}
	public void visit(final ICreation aCreation) {
	}
	public void visit(final IField aField) {
		this.numberOfFields++;
	}
	public void visit(final IMethodInvocation aMethodInvocation) {
	}
	public void visit(final IParameter aParameter) {
	}
	public void visit(final IUseRelationship aUse) {
	}
	public void unknownConstituentHandler(
		final String calledMethodName,
		final IConstituent constituent) {
	}
}
