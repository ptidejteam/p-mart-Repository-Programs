/* (c) Copyright 2001 and following years, Yann-Gaël Guéhéneuc,
 * University of Montreal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package pom.metrics.impl;

import padl.IFileRepository;
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
import padl.kernel.IFirstClassEntity;
import padl.kernel.IField;
import padl.kernel.IGetter;
import padl.kernel.IGhost;
import padl.kernel.IIfInstruction;
import padl.kernel.IInterface;
import padl.kernel.IMemberClass;
import padl.kernel.IMemberGhost;
import padl.kernel.IMemberInterface;
import padl.kernel.IMethod;
import padl.kernel.IMethodInvocation;
import padl.kernel.IPackage;
import padl.kernel.IParameter;
import padl.kernel.ISetter;
import padl.kernel.IStatementWalker;
import padl.kernel.ISwitchInstruction;
import padl.kernel.IUseRelationship;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class McCabe extends AbstractMetric implements IMetric, IUnaryMetric {
	// Must be public because of reflection use to traverse the entity below...
	public class McCabeComputer implements IStatementWalker {
		private int mcCabe;
		public void close(final IAbstractLevelModel anAbstractLevelModel) {
		}
		public void close(final IClass class1) {
		}
		public void close(final IConstructor constructor) {
		}
		public void close(final IDelegatingMethod delegatingMethod) {
		}
		public void close(final IDesignMotifModel patternModel) {
		}
		public void close(final IGetter getter) {
		}
		public void close(final IGhost ghost) {
		}
		public void close(final IInterface anInterface) {
		}
		public void close(final IMemberClass memberClass) {
		}
		public void close(final IMemberGhost memberGhost) {
		}
		public void close(final IMemberInterface memberInterface) {
		}
		public void close(final IMethod method) {
		}
		public void close(final IPackage package1) {
		}
		public void close(final ISetter setter) {
		}
		public String getName() {
			return "McCabe Computer";
		}
		public Object getResult() {
			return new Integer(this.mcCabe);
		}
		public void open(final IAbstractLevelModel anAbstractLevelModel) {
		}
		public void open(final IClass class1) {
		}
		public void open(final IConstructor constructor) {
		}
		public void open(final IDelegatingMethod delegatingMethod) {
		}
		public void open(final IDesignMotifModel patternModel) {
		}
		public void open(final IGetter getter) {
		}
		public void open(final IGhost ghost) {
		}
		public void open(final IInterface anInterface) {
		}
		public void open(final IMemberClass memberClass) {
		}
		public void open(final IMemberGhost memberGhost) {
		}
		public void open(final IMemberInterface memberInterface) {
		}
		public void open(final IMethod method) {
		}
		public void open(final IPackage package1) {
		}
		public void open(final ISetter setter) {
		}
		public void reset() {
		}
		public void unknownConstituentHandler(
			final String calledMethodName,
			final IConstituent constituent) {
		}
		public void visit(final IAggregation anAggregation) {
		}
		public void visit(final IAssociation anAssociation) {
		}
		public void visit(final IComposition composition) {
		}
		public void visit(final IContainerAggregation containerAggregation) {
		}
		public void visit(final IContainerComposition containerComposition) {
		}
		public void visit(final ICreation creation) {
		}
		public void visit(final IField field) {
		}
		public void visit(final IIfInstruction anIfInstruction) {
			this.mcCabe++;
		}
		public void visit(final IMethodInvocation methodInvocation) {
		}
		public void visit(final IParameter parameter) {
		}
		public void visit(final ISwitchInstruction switchInstruction) {
			this.mcCabe += switchInstruction.getRange();
		}
		public void visit(final IUseRelationship use) {
		}
	}
	public McCabe(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		final IStatementWalker walker = new McCabeComputer();
		firstClassEntity.accept(walker);
		return ((Integer) walker.getResult()).doubleValue();
	}
	public String getDefinition() {
		return "McCabe Complexity: Number of points of decision + 1";
	}
}
