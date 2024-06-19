/*
 * (c) Copyright 2001-2007 Yann-Gaël Guéhéneuc,
 * University of Montréal.
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
package padl.visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

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

/**
 * @author Yann-Gaël Guéhéneuc
 * @since  2008/03/14
 */
public class MAGMaGeneratorVertices implements IWalker {
	private final StringBuffer vertices;
	private Stack stackOfEnclosingEntities;
	private List listOfEntities;
	private int numberOfVertices;

	public MAGMaGeneratorVertices() {
		this.vertices = new StringBuffer();
		this.stackOfEnclosingEntities = new Stack();
		this.listOfEntities = new ArrayList();
	}
	public void close(final IAbstractLevelModel anAbstractLevelModel) {
	}
	public void close(final IClass aClass) {
		this.close((IFirstClassEntity) aClass);
	}
	public void close(final IConstructor aConstructor) {
	}
	public void close(final IDelegatingMethod aDelegatingMethod) {
	}
	public void close(final IDesignMotifModel aPatternModel) {
	}
	private void close(final IFirstClassEntity anEntity) {
		this.vertices.append(this.numberOfVertices);
		this.vertices.append(',');
		this.vertices.append(this.getCurrentEntityName());
		this.vertices.append('\n');
		this.listOfEntities.add(this.getCurrentEntityName());

		this.numberOfVertices++;
		this.stackOfEnclosingEntities.pop();
	}
	public void close(final IGetter aGetter) {
	}
	public void close(final IGhost aGhost) {
		this.close((IFirstClassEntity) aGhost);
	}
	public void close(final IInterface anInterface) {
		this.close((IFirstClassEntity) anInterface);
	}
	public void close(final IMemberClass aMemberClass) {
		this.close((IFirstClassEntity) aMemberClass);
	}
	public void close(final IMemberGhost aMemberGhost) {
		this.close((IFirstClassEntity) aMemberGhost);
	}
	public void close(final IMemberInterface aMemberInterface) {
		this.close((IFirstClassEntity) aMemberInterface);
	}
	public void close(final IMethod aMethod) {
	}
	public void close(final IPackage aPackage) {
	}
	public void close(final ISetter aSetter) {
	}
	private String getCurrentEntityName() {
		final StringBuffer buffer = new StringBuffer();
		final Iterator iterator = this.stackOfEnclosingEntities.iterator();
		while (iterator.hasNext()) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) iterator.next();
			buffer.append(firstClassEntity.getID());
			if (iterator.hasNext()) {
				buffer.append('.');
			}
		}
		return buffer.toString();
	}
	public String getName() {
		return "MAGMa Domain (Vertices)";
	}
	public List getListOfEntities() {
		return this.listOfEntities;
	}
	public Object getResult() {
		return this.vertices;
	}
	public void open(final IAbstractLevelModel anAbstractLevelModel) {
	}
	public void open(final IClass aClass) {
		this.stackOfEnclosingEntities.push(aClass);
	}
	public void open(final IConstructor aConstructor) {
	}
	public void open(final IDelegatingMethod aDelegatingMethod) {
	}
	public void open(final IDesignMotifModel aPatternModel) {
	}
	public void open(final IGetter aGetter) {
	}
	public void open(final IGhost aGhost) {
		this.stackOfEnclosingEntities.push(aGhost);
	}
	public void open(final IInterface anInterface) {
		this.stackOfEnclosingEntities.push(anInterface);
	}
	public void open(final IMemberClass aMemberClass) {
		this.open((IClass) aMemberClass);
	}
	public void open(final IMemberGhost aMemberGhost) {
		this.open((IGhost) aMemberGhost);
	}
	public void open(final IMemberInterface aMemberInterface) {
		this.open((IInterface) aMemberInterface);
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
