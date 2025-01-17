/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * OCL Compiler                                                      *
 * Copyright (C) 1999, 2000 Frank Finger (frank@finger.org).         *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a diploma project at the Chair for Software *
 * Technology, Dresden University Of Technology, Germany             *
 * (http://www-st.inf.tu-dresden.de).  It is understood that any     *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other projects, please visit the web site:       *
 * http://www-st.inf.tu-dresden.de/ (Chair home page) or             *
 * http://www-st.inf.tu-dresden.de/ocl/ (project home page)          *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// HISTORY
//
// 02/27/2001   [sz9 ] Adjusted public constructor to also find constraint name.

package tudresden.ocl;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;

import tudresden.ocl.check.NameBoundQueryable;
import tudresden.ocl.check.QueryableFactory;
import tudresden.ocl.check.TypeCheckerFactory;
import tudresden.ocl.check.TypeQueryable;
import tudresden.ocl.check.types.DefaultTypeFactory;
import tudresden.ocl.check.types.ModelFacade;
import tudresden.ocl.check.types.Type;
import tudresden.ocl.check.types.TypeFactory;
import tudresden.ocl.codegen.CodeFragment;
import tudresden.ocl.codegen.CodeGenerator;
import tudresden.ocl.normalize.CompoundNormalizer;
import tudresden.ocl.normalize.ConstraintNaming;
import tudresden.ocl.normalize.DefaultContextInsertion;
import tudresden.ocl.normalize.IteratorInsertion;
import tudresden.ocl.normalize.MultipleIteratorSolving;
import tudresden.ocl.normalize.NormalizerPass;
import tudresden.ocl.normalize.PreconditionViolatedException;
import tudresden.ocl.normalize.TypeInformationInsertion;
import tudresden.ocl.normalize.VariableClarification;
import tudresden.ocl.parser.OclParser;
import tudresden.ocl.parser.OclParserException;
import tudresden.ocl.parser.analysis.DepthFirstAdapter;
import tudresden.ocl.parser.lexer.Lexer;
import tudresden.ocl.parser.node.AConstraint;
import tudresden.ocl.parser.node.AConstraintBody;
import tudresden.ocl.parser.node.Node;
import tudresden.ocl.parser.node.PConstraintBody;
import tudresden.ocl.parser.node.Start;
import tudresden.ocl.parser.node.Switch;
import tudresden.ocl.parser.node.Switchable;

public class OclTree implements NameBoundQueryable, TypeQueryable, Switchable {

  /** @see #ast
   */
  protected static final String NAME_SEPARATOR = "; ";

  protected Start ast;
  /** the name of the constraint, i.e. the names of the constraint bodies
   *  concatenated with <code>NAME_SEPARATOR</code> as separator
   *  @see #NAME_SEPARATOR
   */
  protected String name;
  protected HashSet invariants;
  protected NameCreator nameCreator;

  protected TypeQueryable typeQueryable;
  protected NameBoundQueryable nameBoundQueryable;
  protected QueryableFactory qFactory=new TypeCheckerFactory();

  protected TypeFactory tFactory;

  public OclTree(Start ast) {
    this();
    this.ast=ast;

    // Find constraint name. Added 02/27/2001, sz9.
    ConstraintNameFinder nameFinder = new ConstraintNameFinder();
    ast.apply (nameFinder);
    name = nameFinder.getName();
  }

  protected OclTree() {
    invariants=new HashSet();
  }

  /** creates an OclTree that uses a <code>TypeChecker</code> to acquire
   *  type and name binding information
   */
  public static OclTree createTree(String oclExpression, ModelFacade mf) throws
               tudresden.ocl.parser.OclParserException,
               IOException {
    return createTree(
      oclExpression,
      mf,
      null
    );
  }

  /** @param qf may be <code>null</code>
   */
  public static OclTree createTree(
        String oclExpression, ModelFacade mf, QueryableFactory qf
    )  throws IOException {
    try {
      OclTree tree=new OclTree();
      if (qf!=null) tree.setQueryableFactory(qf);
      tree.tFactory=createTypeFactory(mf);
      Lexer lexer = new Lexer(
        new PushbackReader(
          new StringReader(oclExpression)
        )
      );
      OclParser p=new OclParser(lexer);
      Start startNode = p.parse();
	  tree.ast = startNode;

	  // find name of constraint
	  ConstraintNameFinder nameFinder = new ConstraintNameFinder();
	  startNode.apply(nameFinder);
	  tree.name = nameFinder.getName();
	  return tree;
    }
    catch (tudresden.ocl.parser.parser.ParserException e) {
      throw new OclParserException(e.getMessage());
    }
    catch (tudresden.ocl.parser.lexer.LexerException e) {
      throw new OclParserException(e.getMessage());
    }
  }

  protected static TypeFactory createTypeFactory(ModelFacade mf) {
    return new DefaultTypeFactory(mf);
  }

  /** starts a type check if tree has not already been checked
   */
  public void assureTypes() {
    checkTypeQueryable();
  }

  public void applyGeneratedTests() {
    tudresden.ocl.lib.Ocl.setNameAdapter(
      new tudresden.ocl.check.bootstrap.SableNameAdapter()
    );
    tudresden.ocl.lib.Ocl.setFactory(
      new tudresden.ocl.check.bootstrap.SableOclFactory(this)
    );
    tudresden.ocl.check.bootstrap.GeneratedTests gt=
      new tudresden.ocl.check.bootstrap.GeneratedTests();
    apply(gt);
  }

  public void applyDefaultNormalizations() {
    CompoundNormalizer cn=new CompoundNormalizer();
    NormalizerPass np1=new NormalizerPass();
    NormalizerPass np2=new NormalizerPass();
    cn.add(np1);
    cn.add(new MultipleIteratorSolving());
    cn.add(np2);
    cn.add(new VariableClarification());

    np1.add( new ConstraintNaming() );
    //np1.add( new CollectShorthandExpansion() );
    np1.add( new IteratorInsertion() );

    np2.add( new DefaultContextInsertion() );
    np2.add( new TypeInformationInsertion() );

    cn.normalize( this );
  }

  /** @return true if this OclTree and the OclTree given as parameter
   *          contain equal ASTs
   */
  public boolean equalsExpression(OclTree tree) {
    return this.getExpression().equals( tree.getExpression() );
  }

  public String getConstraintName() {
	return name;
  }


  public Start getRoot() {
    return ast;
  }

  /** @return a String representation of the OCL constraint held by this
   *          OclTree
   */
  public String getExpression() {
    return ast.toString();
  }

  /** A NameCreator is created on demand when getNameCreator() is called for
   *  the first time; calling this operation is necessary only if you want
   *  several OCL trees to use the same NameCreator, which will then create
   *  unique names beyond the scope of a single OCL constraint.<br>
   *  It is safe to first create the tree using createTree and afterwards
   *  set the NameCreator.
   */
  public void setNameCreator(NameCreator nc) {
    nameCreator=nc;
    nameCreator.reserveAllNames(this);
  }

  /** @return a NameCreator that creates unique names for this OCL constraint
   */
  public NameCreator getNameCreator() {
    if (nameCreator==null) {
      nameCreator=new NameCreator();
      nameCreator.reserveAllNames(this);
    }
    return nameCreator;
  }

  /** By calling this method, a normalizer assures that the given OCL
   *  invariant holds for this AST.
   *
   *  @param oclConstraint the String representation of an OCL constraint;
   *                       it must contain exactly those ignored tokens
   *                       it would contain if it was parsed and then
   *                       retransformed into a String
   */
  public void assureInvariant(String oclConstraint) {
    invariants.add(oclConstraint.trim());
  }

  /** This method determines whether the AST fulfills a given OCL invariant.
   *  For performance reasons it does this not be examination of the tree but
   *  by looking up if the assureConstraint method was called earlier with
   *  exaclty the given String.
   *
   *  @param oclConstraint the String representation of an OCL constraint;
   *                       it must contain exactly those ignored tokens
   *                       it would contain if it was parsed and then
   *                       retransformed into a String
   *  @see #assureInvariant(String inv)
   */
  public boolean fulfillsInvariant(String oclConstraint) {
    return invariants.contains(oclConstraint.trim());
  }

  /** @throws PreconditionViolatedException if the AST does not fulfill the given
   *          precondition
   *  @see #assureInvariant(String inv)
   */
  public void requireInvariant(String oclConstraint) throws PreconditionViolatedException {
    if (!fulfillsInvariant(oclConstraint)) {
      throw new PreconditionViolatedException(oclConstraint);
    }
  }

  /** A normalizer can use this method to notify the OclTree that the AST no
   *  longer fulfilles an invariant. If the tree didn't fulfill the invariant
   *  anyway nothing happens.
   */
  public void breakInvariant(String oclConstraint) {
    invariants.remove(oclConstraint.trim());
  }

  public String toString() {
    if (ast==null) {
      return "empty OCLTree";
    }
    return getExpression();
  }

  /**
  	*	apply a tree traversal to the abstract syntax tree; see the
  	*	<a href="http://www.sablecc.org/thesis/thesis.html#PAGE52">SableCC documentation</a>
  	*	for tree traversals
   */
  public void apply(Switch s) {
    ast.apply(s);
  }

  public CodeFragment[] getCode(CodeGenerator cgen) {
    return cgen.getCode(this);
  }

  public String getDefaultContext(Node n) {
    checkNameBoundQueryable();
    return nameBoundQueryable.getDefaultContext(n);
  }

  public void changeNotify() {
    this.changeNotify(ast);
  }

  public void changeNotify(Node subtree) {
    checkNameBoundQueryable();
    nameBoundQueryable.changeNotify(subtree);
    if (nameBoundQueryable!=typeQueryable && typeQueryable!=null) {
      typeQueryable.changeNotify(subtree);
    }
  }

  /** @return the type of the variable bound to the given name, or <code>null</code>
   *          if the name is not bound
   */
  public Type getTypeFor(String name, Node node) {
    checkTypeQueryable();
    return typeQueryable.getTypeFor(name, node);
  }

  public Type getNodeType(Node n) {
    checkTypeQueryable();
    return typeQueryable.getNodeType(n);
  }

  public boolean isNameBound(String name, Node node) {
    checkNameBoundQueryable();
    return nameBoundQueryable.isNameBound(name, node);
  }

  public HashSet getBoundNames(Node n) {
    checkNameBoundQueryable();
    return nameBoundQueryable.getBoundNames(n);
  }

  protected void checkNameBoundQueryable() {
    if (nameBoundQueryable==null) {
      nameBoundQueryable=qFactory.getNameBoundQueryable(typeQueryable, this, tFactory);
      ast.apply(nameBoundQueryable);
    }
  }

  protected void checkTypeQueryable() {
    if (typeQueryable==null) {
      typeQueryable=qFactory.getTypeQueryable(nameBoundQueryable, this, tFactory);
      ast.apply(typeQueryable);
    }
  }

  /** calling this method is only effective <em>before</em>
   *  <CODE>checkNameBoundQueryable</CODE> and <CODE>checkTypeQueryable</CODE>
   *  are called for the first time
   */
  public void setQueryableFactory(QueryableFactory qf) {
    qFactory=qf;
  }
}

class ConstraintNameFinder extends DepthFirstAdapter
{
	String foundName;

	public void caseAConstraint(AConstraint node)
	{
		Iterator iter = node.getConstraintBody().iterator();
		while (iter.hasNext())
		{
			PConstraintBody next = (PConstraintBody) iter.next();
			next.apply(this);
		}
	}

	public void caseAConstraintBody(AConstraintBody node)
	{
		String constraintName;
		if (node.getName()==null)
		{
			constraintName = "unnamedConstraint";
		}
		else
		{
			constraintName = node.getName().toString().trim();
		}

		if (foundName==null)
		{
			foundName = constraintName;
		}
		else
		{
			foundName = foundName + OclTree.NAME_SEPARATOR + constraintName;
		}
	}

	public String getName()
	{
		return foundName;
	}
}
