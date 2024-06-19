/*
 * (c) Copyright 2001, 2002 Hervé Albin-Amiot and Yann-Gaël Guéhéneuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * Soft-Maint S.A.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHORS ARE ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.kernel.impl;

import java.util.Arrays;
import java.util.Iterator;
import padl.kernel.Constants;
import padl.kernel.IConstituent;
import padl.kernel.IConstituentOfOperation;
import padl.kernel.IConstructor;
import padl.kernel.IElementMarker;
import padl.kernel.IFilter;
import padl.kernel.IMethod;
import padl.kernel.IMethodInvocation;
import padl.kernel.IOperation;
import padl.kernel.IParameter;
import padl.kernel.IVisitor;
import padl.kernel.exception.ModelDeclarationException;
import padl.util.Util;
import util.io.Output;
import util.lang.Modifier;
import util.multilingual.MultilingualManager;
//Angelino Picone 21/08/2012 : Change the visibility to public in order to allow other project to extend from this class in the particular case of eclipse bundle loader (avoid IllegalAccessError).
public class Constructor extends Operation implements IElementMarker, IConstructor {
	private static final long serialVersionUID = -3313404261410898384L;

	private GenericContainerOfConstituents container =
		new GenericContainerOfInsertionOrderedConstituents(this);
	//	public Constructor(final MethodInfo aMethod) {
	//		this(aMethod.getName(), aMethod);
	//	}
	public Constructor(final char[] anID) {
		super(anID);
		this.updatePathWithParameters();
	}
	public Constructor(final char[] anID, final IMethod attachedMethod)
			throws ModelDeclarationException {

		super(anID);
		this.attachTo(attachedMethod);
		this.updatePathWithParameters();
	}
	//	public Constructor(final String anID, final MethodInfo aMethod) {
	//		super(anID);
	//
	//		try {
	//			this.setVisibility(aMethod.getAccess());
	//		}
	//		catch (final ModelDeclarationException e) {
	//			// No error can occur.
	//		}
	//
	//		final String[] detectedParameters = aMethod.getParams();
	//		for (int i = 0; i < detectedParameters.length; i++) {
	//			try {
	//				this.addConstituent(new Parameter(Util
	//					.stripAndCapQualifiedName(detectedParameters[i])));
	//			}
	//			catch (final ModelDeclarationException e) {
	//			}
	//		}
	//	}

	public Constructor(final IMethod attachedMethod)
			throws ModelDeclarationException {

		this(attachedMethod.getID(), attachedMethod);
		this.updatePathWithParameters();
	}
	public void accept(final IVisitor visitor) {
		this.accept(visitor, "open");
		final Iterator iterator = this.getConcurrentIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IConstituent constituent = (IConstituent) iterator.next();
			// System.out.println(constituent.toString());
			constituent.accept(visitor);
		}
		this.accept(visitor, "close");
	}
	// TODO: Move these methods to Operation!
	public void addConstituent(final IConstituent aConstituent)
			throws ModelDeclarationException {

		if (aConstituent instanceof IConstituentOfOperation) {
			this.addConstituent((IConstituentOfOperation) aConstituent);
		}
		else {
			throw new ModelDeclarationException(MultilingualManager.getString(
				"PARAM_OR_METHOD_ADD",
				Constructor.class));
		}
	}
	public void addConstituent(final IConstituentOfOperation aMethodConstituent)
			throws ModelDeclarationException {

		this.updatePathWithParameters();

		// Yann 2004/07/31: Test and order II!
		// I cannot use the addConstituent() method here because I don't
		// want the method invocations to be sorted!
		//	super.addConstituent(aMethodInvocation);
		this.container.directlyAddConstituentWithUniqueID(aMethodConstituent);
	}
	private void updatePathWithParameters() {
		// Yann 2009/09/11: Paths of method need parameters
		// I add the parameter types to the path of method
		// as appropriate :-)
		final StringBuffer buffer = new StringBuffer();
		buffer.append(this.getPath());
		final int indexOfLastOpeningParenthesis = buffer.lastIndexOf("(");
		if (indexOfLastOpeningParenthesis > -1) {
			buffer.delete(indexOfLastOpeningParenthesis, buffer.length());
		}
		buffer.append('(');
		final Iterator iterator =
			this.getIteratorOnConstituents(IParameter.class);
		while (iterator.hasNext()) {
			final IParameter parameter = (IParameter) iterator.next();
			buffer.append(parameter.getTypeName());
			if (iterator.hasNext()) {
				buffer.append(", ");
			}
		}
		buffer.append(')');
		this.setPath(buffer.toString().toCharArray());
	}
	public boolean doesContainConstituentWithID(final char[] anID) {
		return this.container.doesContainConstituentWithID(anID);
	}
	public boolean doesContainConstituentWithName(final char[] aName) {
		return this.container.doesContainConstituentWithName(aName);
	}
	// Farouk 2004/02/13
	// Method added to compare two methods.
	/**
	 * Return if two methods are equal.
	 * This method compares the names, the number
	 * of parameters and the parameter types.
	 * 
	 * @param m	a method
	 * @return <code>true</code> if the methods are equal,
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(final Object o) {
		if (!(o instanceof IOperation)) {
			return false;
		}

		// Yann 2004/03/31: ConstituentID!
		// This method needs to test the actor ID only.
		// Method might be different if they don't
		// have the same sets of method invocations!
		// Yann 2011/04/12: Pot-pourri
		// If by chance operations from different class are put
		// in a same list, then to find them back, I must test
		// also their names, not just their signatures...
		final IOperation otherMethod = (IOperation) o;
		return Arrays.equals(this.getID(), otherMethod.getID())
				&& Arrays.equals(this.getName(), otherMethod.getName());
		// Yann 2006/02/06: Revert change
		// I remove this test because it does not make real sense 
		// to compare the number of actors... would be (much) better
		// to compare the real signature of the methods... their ID?
		//	&& this.getNumberOfConstituents(IParameter.class)
		//		== otherMethod.getNumberOfConstituents(IParameter.class);
	}
	public String getCallDeclaration() {
		final StringBuffer codeEq = new StringBuffer();
		codeEq.append(this.getName());
		codeEq.append('(');
		final Iterator iterator =
			this.getIteratorOnConstituents(IParameter.class);
		// Yann 2004/04/10: Method invocation!
		// A method may now contain instances of IMethodInvocation
		// in addition to instances of IParameter.
		// Yann 2005/10/12: Iterator!
		// I have now an iterator able to iterate over a
		// specified type of constituent of a list.
		//	boolean hasPredecessor = false;
		//	while (enum.hasNext()) {
		//		final IConstituent constituent = (IConstituent) enum.next();
		//		if (constituent instanceof IParameter) {
		//			if (hasPredecessor) {
		//				codeEq.append(",");
		//			}
		//			else {
		//				hasPredecessor = true;
		//			}
		//			codeEq.append(constituent.getName());
		//		}
		//	}
		while (iterator.hasNext()) {
			final IParameter parameter = (IParameter) iterator.next();
			codeEq.append(parameter.getName());
			if (iterator.hasNext()) {
				codeEq.append(',');
			}
		}
		codeEq.append(')');
		return codeEq.toString();
	}
	public Iterator getConcurrentIteratorOnConstituents() {
		return this.container.getConcurrentIteratorOnConstituents();
	}

	public Iterator getConcurrentIteratorOnConstituents(final IFilter filter) {
		return this.container.getConcurrentIteratorOnConstituents();
	}

	public Iterator getConcurrentIteratorOnConstituents(
		final java.lang.Class aConstituentType) {

		return this.container
			.getConcurrentIteratorOnConstituents(aConstituentType);
	}

	public IConstituent getConstituentFromID(final char[] anID) {
		return this.container.getConstituentFromID(anID);
	}

	public IConstituent getConstituentFromID(final String anID) {
		return this.getConstituentFromID(anID.toCharArray());
	}
	public IConstituent getConstituentFromName(final char[] aName) {
		return this.container.getConstituentFromName(aName);
	}
	public IConstituent getConstituentFromName(final String aName) {
		return this.container.getConstituentFromName(aName.toCharArray());
	}
	public Iterator getIteratorOnConstituents() {
		return this.container.getIteratorOnConstituents();
	}
	public Iterator getIteratorOnConstituents(final IFilter aFilter) {
		return this.container.getIteratorOnConstituents(aFilter);
	}
	public Iterator getIteratorOnConstituents(java.lang.Class aConstituentType) {
		return this.container.getIteratorOnConstituents(aConstituentType);
	}
	public int getNumberOfConstituents() {
		return this.container.getNumberOfConstituents();
	}
	public int getNumberOfConstituents(final java.lang.Class aConstituentType) {
		return this.container.getNumberOfConstituents(aConstituentType);
	}
	/**
	 * This methods is used by the clone protocol.
	 */
	public void performCloneSession() {
		super.performCloneSession();

		// Yann 2005/05/09: Clones of parameters and other stuff...
		// I should not forget to clone any constituents contained
		// in a method, such as parameters, method invocations...
		final Iterator iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IConstituent constituent = (IConstituent) iterator.next();

			constituent.startCloneSession();
			constituent.performCloneSession();

			try {
				((Constructor) this.getClone()).addConstituent(constituent
					.getClone());
			}
			catch (final ModelDeclarationException e) {
				e.printStackTrace(Output.getInstance().errorOutput());
			}

			constituent.endCloneSession();
		}
	}
	public void removeAllConstituent() {
		this.container.removeAllConstituent();
	}
	public void removeConstituentFromID(final char[] anID) {
		this.container.removeConstituentFromID(anID);
		this.updatePathWithParameters();
	}
	public void startCloneSession() {
		super.startCloneSession();
		// Yann 2010/10/03: Objects!
		// The "container" is now an instance of a class
		// and must be assigned a new instance independently.
		//	((Constructor) this.getClone()).container.resetListOfConstituents();
		((Constructor) this.getClone()).container =
			new GenericContainerOfNaturallyOrderedConstituents(
				((Constructor) this.getClone()));
	}
	public String toString() {
		if (Constants.DEBUG) {
			Output.getInstance().debugOutput().println(
				"// Constructor.toString()");
		}
		return this.toString(0);
	}
	public String toString(final int tab) {
		if (Constants.DEBUG) {
			Output.getInstance().debugOutput().println(
				"// Constructor.toString(int)");
		}
		final StringBuffer codeEq = new StringBuffer();
		this.toStringStart(tab, codeEq);
		this.toStringSignature(codeEq);
		this.toStringBody(tab, codeEq);
		return codeEq.toString();
	}
	protected void toStringBody(final int tab, final StringBuffer codeEq) {
		codeEq.append(" {\n");
		final Iterator iterator =
			this.getIteratorOnConstituents(IMethodInvocation.class);
		// Yann 2005/10/12: Iterator!
		// I have now an iterator able to iterate over a
		// specified type of constituent of a list.
		//	while (iterator.hasNext()) {
		//		final IElement element = (IElement) iterator.next();
		//		if (element instanceof IMethodInvocation) {
		//			Util.addTabs(tab + 1, codeEq);
		//			codeEq.append("// Method invocation: ");
		//			codeEq.append(element.toString());
		//			codeEq.append('\n');
		//		}
		//	}
		while (iterator.hasNext()) {
			final IMethodInvocation methodInvocation =
				(IMethodInvocation) iterator.next();
			Util.addTabs(tab + 1, codeEq);
			codeEq.append("// Method invocation: ");
			codeEq.append(methodInvocation.toString());
			codeEq.append('\n');
		}
		String[] codeLines = this.getCodeLines();
		if (codeLines != null) {
			for (int i = 0; i < codeLines.length; i++) {
				Util.addTabs(tab + 1, codeEq);
				codeEq.append(codeLines[i]);
				codeEq.append('\n');
			}
		}
		Util.addTabs(tab, codeEq);
		codeEq.append('}');
	}
	protected void toStringSignature(final StringBuffer codeEq) {
		codeEq.append(this.getName());
		codeEq.append('(');
		final Iterator iterator =
			this.getIteratorOnConstituents(IParameter.class);
		// Yann 2005/10/12: Iterator!
		// I have now an iterator able to iterate over a
		// specified type of constituent of a list.
		//	while (iterator.hasNext()) {
		//		final IElement element = (IElement) iterator.next();
		//		if (element instanceof IParameter) {
		//			codeEq.append(element.toString());
		//			// The list of actors of a methods contains
		//			// both parameters and method invocations, so the
		//			// following test while succeeds even if there is
		//			// no more parameters...
		//			if (iterator.hasNext()) {
		//				codeEq.append(", ");
		//			}
		//		}
		//	}
		while (iterator.hasNext()) {
			final IParameter parameter = (IParameter) iterator.next();
			codeEq.append(parameter.toString());
			if (iterator.hasNext()) {
				codeEq.append(", ");
			}
		}
		codeEq.append(')');
	}
	protected void toStringStart(final int tab, final StringBuffer codeEq) {
		Util.addTabs(tab, codeEq);
		if (this.getComment() != null) {
			codeEq.append("/* ");
			codeEq.append(this.getComment());
			codeEq.append(" */\n");
			Util.addTabs(tab, codeEq);
		}
		codeEq.append(Modifier.toString(this.getVisibility()));
		if (this.getVisibility() != 0) {
			codeEq.append(' ');
		}
	}
}
