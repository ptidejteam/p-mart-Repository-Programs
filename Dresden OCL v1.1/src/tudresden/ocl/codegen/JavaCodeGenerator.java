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
package tudresden.ocl.codegen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import tudresden.ocl.OclException;
import tudresden.ocl.check.types.Basic;
import tudresden.ocl.check.types.Collection;
import tudresden.ocl.check.types.OclType;
import tudresden.ocl.check.types.Type;
import tudresden.ocl.parser.node.AActualParameterList;
import tudresden.ocl.parser.node.AActualParameterListTail;
import tudresden.ocl.parser.node.AAdditiveExpression;
import tudresden.ocl.parser.node.AAdditiveExpressionTail;
import tudresden.ocl.parser.node.AAndLogicalOperator;
import tudresden.ocl.parser.node.AArrowPostfixExpressionTailBegin;
import tudresden.ocl.parser.node.ABooleanLiteral;
import tudresden.ocl.parser.node.AConstraintBody;
import tudresden.ocl.parser.node.ADivMultiplyOperator;
import tudresden.ocl.parser.node.AEnumLiteral;
import tudresden.ocl.parser.node.AEqualRelationalOperator;
import tudresden.ocl.parser.node.AExpression;
import tudresden.ocl.parser.node.AExpressionListOrRange;
import tudresden.ocl.parser.node.AExpressionListTail;
import tudresden.ocl.parser.node.AFeatureCall;
import tudresden.ocl.parser.node.AFeatureCallParameters;
import tudresden.ocl.parser.node.AFeaturePrimaryExpression;
import tudresden.ocl.parser.node.AGtRelationalOperator;
import tudresden.ocl.parser.node.AGteqRelationalOperator;
import tudresden.ocl.parser.node.AIfExpression;
import tudresden.ocl.parser.node.AIfPrimaryExpression;
import tudresden.ocl.parser.node.AImpliesLogicalOperator;
import tudresden.ocl.parser.node.AIntegerLiteral;
import tudresden.ocl.parser.node.AIterateDeclarator;
import tudresden.ocl.parser.node.ALetExpression;
import tudresden.ocl.parser.node.AListExpressionListOrRangeTail;
import tudresden.ocl.parser.node.ALitColPrimaryExpression;
import tudresden.ocl.parser.node.ALiteralPrimaryExpression;
import tudresden.ocl.parser.node.ALogicalExpression;
import tudresden.ocl.parser.node.ALogicalExpressionTail;
import tudresden.ocl.parser.node.ALtRelationalOperator;
import tudresden.ocl.parser.node.ALteqRelationalOperator;
import tudresden.ocl.parser.node.AMinusAddOperator;
import tudresden.ocl.parser.node.AMinusUnaryOperator;
import tudresden.ocl.parser.node.AMultMultiplyOperator;
import tudresden.ocl.parser.node.AMultiplicativeExpression;
import tudresden.ocl.parser.node.AMultiplicativeExpressionTail;
import tudresden.ocl.parser.node.ANEqualRelationalOperator;
import tudresden.ocl.parser.node.ANotUnaryOperator;
import tudresden.ocl.parser.node.AOrLogicalOperator;
import tudresden.ocl.parser.node.AParenthesesPrimaryExpression;
import tudresden.ocl.parser.node.APlusAddOperator;
import tudresden.ocl.parser.node.APostfixExpression;
import tudresden.ocl.parser.node.APostfixExpressionTail;
import tudresden.ocl.parser.node.APostfixUnaryExpression;
import tudresden.ocl.parser.node.AQualifiers;
import tudresden.ocl.parser.node.ARangeExpressionListOrRangeTail;
import tudresden.ocl.parser.node.ARealLiteral;
import tudresden.ocl.parser.node.ARelationalExpression;
import tudresden.ocl.parser.node.ARelationalExpressionTail;
import tudresden.ocl.parser.node.AStandardDeclarator;
import tudresden.ocl.parser.node.AStringLiteral;
import tudresden.ocl.parser.node.AUnaryUnaryExpression;
import tudresden.ocl.parser.node.AXorLogicalOperator;
import tudresden.ocl.parser.node.Node;
import tudresden.ocl.parser.node.PExpression;
import tudresden.ocl.parser.node.PExpressionListOrRangeTail;
import tudresden.ocl.parser.node.PLogicalOperator;
import tudresden.ocl.parser.node.PRelationalExpression;
import tudresden.ocl.parser.node.PRelationalOperator;

public class JavaCodeGenerator extends ProceduralCodeGenerator {

  String instanceName;
  String javaResult;
  
  /**
     The package prefix for all classes of the ocl library in the generated code.
     Contains the trailing dot, if not empty.
     May be empty, if an appropriate import statement is generated.
     Must not be null.
  */
  private String oclLibPackage="tudresden.ocl.lib.";

  /** maps Nodes for operators (ALogicalOperator, ARelationalOperator...) to
   *  the String containing their Java representation
   */
  NodeNameMap operatorCode=new NodeNameMap();
  StringStringMap varMap=new StringStringMap();

  /** set of all OclAny property names
   */
  static java.util.HashSet oclAnyOperations;

  static {
    oclAnyOperations=new java.util.HashSet();
    oclAnyOperations.add("oclIsKindOf");
    oclAnyOperations.add("oclIsTypeOf");
    oclAnyOperations.add("oclAsType");
    oclAnyOperations.add("oclInState");
    oclAnyOperations.add("oclIsNew");
  }

  /** @param instanceName a Java expression that will be evaluated to the
   *                      instance that is checked for constraint conformance
   *  @param resultName   a Java expression that will be evaluated to the
   *                      result of constrained operation (can be
   *                      <code>null</code> for other constraints than post
   *                      conditions
   */
  public JavaCodeGenerator(String instanceName, String resultName) {
    this.instanceName=instanceName;
    this.javaResult=resultName;
  }

  public JavaCodeGenerator(
    String instanceName, 
    String resultName,
    String oclLibPackage)
  {
    this.instanceName=instanceName;
    this.javaResult=resultName;
    this.oclLibPackage=oclLibPackage;
  }

  /** @param instanceName a Java expression that will be evaluated to the
   *                      instance that is checked for constraint conformance
   */
  public JavaCodeGenerator(String instanceName) {
    this(instanceName, null);
  }

  public JavaCodeGenerator() {
    this("this");
  }

  // ---- abstract methods from ProceduralCodeGenerator: ----

  protected String getTransferCode(String var, String type) {
    StringBuffer ret=new StringBuffer();
    for(int i=0; i<indent; i++)
      ret.append(' ');
    ret.append(oclLibPackage+type+' '+var+";\n");
    return ret.toString();
  }

  protected void requireTreeInvariants() {
    // unique variables
    tree.requireInvariant(
      "context AExpression inv : letExpression -> forAll ( le | not self . parent . boundNames -> includes ( le . name ) )  "
    );
    tree.requireInvariant(
      "context AStandardDeclarator : not parent . parent . boundNames -> includes ( name.toString() )"
    );
    tree.requireInvariant(
      "context AIterateDeclarator : let ppb = parent . parent . boundNames in not ( ppb -> includes ( iterator.toString() ) or ppb -> includes( accumulator.toString() ) )"
    );
    // no multiple iterators
    tree.requireInvariant(
      "context AStandardDeclarator inv : declaratorTail -> isEmpty"
    );
    // explicitly qualified names
    tree.requireInvariant(
      "context ANamePathNameBegin inv : boundNames -> includes ( name )"
    );
    // iterators where possible
    tree.requireInvariant(
      "context AFeatureCall inv : "+
      "let iteratingMethodNames : Set ( String ) = Set { 'collect', 'exists' , 'forAll' , 'isUnique' , 'reject' , 'select' , 'sortedBy' } in "+
      "iteratingMethodNames -> includes ( self . pathName . toString ( ) ) implies "+
      "( self . featureCallParameters . declarator -> size = 1 "+
      "and self . featureCallParameters . declarator . oclType = AStandardDeclarator )"
    );
    tree.requireInvariant(
      "context ANamePathNameBegin inv : defaultContext <> '' implies boundNames -> includes ( defaultContext )"
    );
    // named constraints
    tree.requireInvariant(
      "context AConstraintBody inv : name -> size = 1"
    );
  }

  /**
     This method generates the declaration prefix of a node.
     Is looks like this:
     <pre>
        final tudresden.ocl.lib.&lt;type&gt; &lt;variable&gt; =
     </pre>
     The type is ommited, if the variable is one of the transfer variables.
  */
  private String createDecl(String type, String variable)
  {
    if(preVarTypes.containsKey(variable))
      return variable+'=';
    else if(type.indexOf('.')<0)
      return "final "+oclLibPackage+type+' '+variable+'=';
    else
      return "final "+type+' '+variable+'=';
  }
  
  private String qualifyType(String type)
  {
    return (type.indexOf('.')<0) ? oclLibPackage+type : type;
  }

  // ---------- tree traversal: -----------------------------

  /** the variable for the AConstraintBody node is the variable for &quot;self&quot;
   */
  public void inAConstraintBody(AConstraintBody cb) {
    varMap.clear();
    appendCode(createDecl("OclAnyImpl",getVariable(cb))+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor("+instanceName+") );\n" );
    varMap.put("self", getVariable(cb));
    if (parameters!=null) {
      for (int i=0; i<parameters.length; i++) {
        Type oclParamType=tree.getTypeFor(parameters[i][0], cb);
        String javaParamType=getJavaType(oclParamType);
        String javaParamName=tree.getNameCreator().getUniqueName("OpPar");
        appendCode(createDecl(javaParamType,javaParamName)+
          oclLibPackage+"Ocl.to"+javaParamType+"( "+oclLibPackage+"Ocl.getFor("+parameters[i][0]+") );\n");
        varMap.put(parameters[i][0], javaParamName);
      }
    }
    if ( topOfStack.getKind()==CodeFragment.POST ) {
      Type oclReturnType=tree.getTypeFor("result", cb.getExpression());
      if (oclReturnType!=null && ! oclReturnType.equals(Basic.VOID)) {
        if (javaResult==null) {
          throw new OclException("tried to generate code for post condition without setting Java code for result");
        }
        String javaType=getJavaType(oclReturnType);
        String result=tree.getNameCreator().getUniqueName("Result");
        varMap.put("result", result);
        writeToStandardCodeOnly();
        appendCode(createDecl(javaType,result)+oclLibPackage+"Ocl.to"+javaType+"( "+oclLibPackage+"Ocl.getFor("+javaResult+") );\n");
        writeToPreCodeOnly();
        // the following works only for instantiable classes
        // of the ocl library. TODO
        appendCode(createDecl(javaType,result)+"new "+oclLibPackage+javaType+"(0,\"created by JavaCodeGenerator\");\n");
        writeToBothCodes();
      }
    }
  }

  public void outAExpression(AExpression e) {
    reachThrough(e, e.getLogicalExpression());
  }

  /** if a then b else c endif   <br>
   *  Ocl? result=(a.isTrue()) ? (b) : (c);
   *  <p>
   *  For each if expression a new variable is generated.
   */
  public void outAIfExpression(AIfExpression ie) {
    String javaType=getJavaType(tree.getNodeType(ie));
    appendCode(createDecl(javaType,getVariable(ie))+'('+getVariable(ie.getIfBranch())+".isTrue()) ? (");
    appendCode(getVariable(ie.getThenBranch())+") : ("+getVariable(ie.getElseBranch())+");\n");
  }

  /** <CODE>and</CODE>, <CODE>or</CODE> and <CODE>xor</CODE> have precedence
   *  over <CODE>implies</CODE> (<code>and</code> binds as strongly as
   *  <code>or</code>)
   *  <p>
   *  a and b or c   <br>
   *  OclBoolean result = a.and(b).or(c);
   *  <p>
   *  A variable is generated for each ALogicalExpression with tail (not for a
   *  ALogicalExpressionTail).
   */
  public void outALogicalExpression(ALogicalExpression le) {
    if (le.getLogicalExpressionTail().isEmpty()) {
      reachThrough( le, le.getRelationalExpression() );
    } else {
      int tailElements=le.getLogicalExpressionTail().size();
      PRelationalExpression[] relExprs=new PRelationalExpression[tailElements+1];
      PLogicalOperator[] logOps=new PLogicalOperator[tailElements];
      boolean[] opIsImplies=new boolean[tailElements];
      // logOps[i] is the operator AFTER relExprs[i]
      Iterator iter=le.getLogicalExpressionTail().iterator();
      int index=0;
      relExprs[0]=le.getRelationalExpression();
      while (iter.hasNext()) {
        ALogicalExpressionTail let=(ALogicalExpressionTail)iter.next();
        logOps[index]=let.getLogicalOperator();
        opIsImplies[index]=(logOps[index] instanceof AImpliesLogicalOperator);
        index++;
        relExprs[index]=let.getRelationalExpression();
      }

      appendCode(createDecl("OclBoolean",getVariable(le)));
      int lastNonImplies=0; // index of operand after last implies
      boolean foundImplies=false;
      for (int i=0; i<tailElements; i++) {
        if (opIsImplies[i]) {
          int from=lastNonImplies;
          int to=i;
          appendAndOrLogExpr(relExprs, logOps, from, to);
          lastNonImplies=to+1;
          if (foundImplies) {
            appendCode(")");
          }
          appendCode(".implies(");
          foundImplies=true;
        }
      }
      appendAndOrLogExpr(relExprs, logOps, lastNonImplies, tailElements);
      if (foundImplies) {
        appendCode(")");
      }
      appendCode(";\n");
    }
  }

  /** Append code for the part of a logical expression between the indexes of
   *  relational subexpression given as parameters <code>from</code> and
   *  <code>to</code>, both relational expressions included. The part must not
   *  contain an <code>implies</code> operator. <CODE>from</CODE> may be equal
   *  to <CODE>to</CODE>.
   */
  protected void appendAndOrLogExpr(
      PRelationalExpression[] relExprs, PLogicalOperator[] logOps,
      int from, int to ) {
    appendCode( getVariable(relExprs[from]) );
    for (int i=from+1; i<=to; i++) {
      appendCode( "." );
      appendCode( operatorCode.get(logOps[i-1]) ); // "and" or "or"
      appendCode( "("+getVariable(relExprs[i])+")" );
    }
  }

  /** a&lt;b = c&gt;d is not allowed by the OCL grammar, therefore we need not
   *  care about precedence
   *  <p>
   *  a = b  <br>
   *  OclBoolean result=a.isEqualTo(b); <br>
   *  a > b <br>
   *  OclBoolean result=Ocl.toOclComparable(a).isGreaterThan(Ocl.toOclComparable(b));
   *  <p>
   *  A variable is generated for each ARelationalExpression if it has
   *  a ARelationalExpressionTail subnode.
   *
   */
  public void outARelationalExpression(ARelationalExpression re) {
    if (re.getRelationalExpressionTail()==null) {
      reachThrough(re, re.getAdditiveExpression());
    } else {
      ARelationalExpressionTail ret=
        (ARelationalExpressionTail) re.getRelationalExpressionTail();
      PRelationalOperator relOp=ret.getRelationalOperator();
      boolean comparison=true;
      if (relOp instanceof AEqualRelationalOperator ||
          relOp instanceof ANEqualRelationalOperator) {
        comparison=false;
      }
      appendCode(createDecl("OclBoolean",getVariable(re)));
      //if (comparison) appendCode("Ocl.toOclComparable(");
      appendCode( getVariable(re.getAdditiveExpression()) );
      //if (comparison) appendCode(")");
      appendCode( "."+operatorCode.get(ret.getRelationalOperator())+"(" );
      //if (comparison) appendCode("Ocl.toOclComparable(");
      appendCode( getVariable(ret.getAdditiveExpression()) );
      //if (comparison) appendCode(")");
      appendCode(");\n");
    }
  }

  /** a + b <br>
   *  Ocl? result=Ocl.toOcl?(a).add(Ocl.toOcl?(b)); <br>
   *  with Ocl? = OclReal | OclInteger | OclSet
   *  <p>
   *  A variable is generated for each AAdditiveExpression with tail.
   */
  public void outAAdditiveExpression(AAdditiveExpression ae) {
    if (ae.getAdditiveExpressionTail().isEmpty()) {
      reachThrough(ae, ae.getMultiplicativeExpression());
    } else {
      Type nodeType=tree.getNodeType(ae);
      String javaType=getJavaType(nodeType);
      appendCode(createDecl(javaType,getVariable(ae)));
      appendCode( getVariable(ae.getMultiplicativeExpression()) );
      Iterator iter=ae.getAdditiveExpressionTail().iterator();
      while (iter.hasNext()) {
        AAdditiveExpressionTail aet = (AAdditiveExpressionTail) iter.next();
        appendCode( "."+operatorCode.get(aet.getAddOperator())+"(" );
        appendCode( getVariable(aet.getMultiplicativeExpression()) );
        appendCode( ")" );
      }
      appendCode(";\n");
    }
  }

  /** a * b <br>
   *  Ocl? result=Ocl.toOcl?(a).multiply(Ocl.toOcl?(b)); <br>
   *  with Ocl? = OclReal | OclInteger
   *  <p>
   *  A variable is generated for each AMultiplicativeExpression with tail.
   */
  public void outAMultiplicativeExpression(AMultiplicativeExpression me) {
    if (me.getMultiplicativeExpressionTail().isEmpty()) {
      reachThrough(me, me.getUnaryExpression());
    } else {
      String javaType=getJavaType( tree.getNodeType(me) );
      appendCode(createDecl(javaType,getVariable(me)));
      appendCode(oclLibPackage+"Ocl.to"+getJavaType( tree.getNodeType(me.getUnaryExpression()) )+"(");
      appendCode( getVariable(me.getUnaryExpression()) );
      appendCode(")");
      Iterator iter=me.getMultiplicativeExpressionTail().iterator();
      while (iter.hasNext()) {
        AMultiplicativeExpressionTail met=(AMultiplicativeExpressionTail)iter.next();
        javaType=getJavaType( tree.getNodeType(met.getUnaryExpression()) );
        appendCode("."+operatorCode.get(met.getMultiplyOperator())+"(");
        appendCode(getVariable(met.getUnaryExpression())+")");
      }
      appendCode(";\n");
    }
  }

  /** not a <br>
   *  OclBoolean result=a.not();
   *  <p>
   *  A variable is generated for each AUnaryUnaryExpression.
   */
  public void outAUnaryUnaryExpression(AUnaryUnaryExpression uue) {
    Type oclType=tree.getNodeType(uue);
    String javaType=getJavaType( oclType );
    appendCode(createDecl(javaType,getVariable(uue)));
    if (oclType==Basic.INTEGER) appendCode(oclLibPackage+"Ocl.toOclInteger(");
    appendCode(getVariable(uue.getPostfixExpression())+"."+
      operatorCode.get( uue.getUnaryOperator() )+"()");
    if (oclType==Basic.INTEGER) appendCode(")");
    appendCode(";\n");
  }

  public void outAPostfixUnaryExpression(APostfixUnaryExpression pue) {
    reachThrough(pue, pue.getPostfixExpression());
  }

  public void outAPostfixExpression(APostfixExpression pe) {
    if (pe.getPostfixExpressionTail().isEmpty()) {
      reachThrough(pe, pe.getPrimaryExpression());
    } else {
      APostfixExpressionTail last = (APostfixExpressionTail)
        pe.getPostfixExpressionTail().getLast();
      reachThrough(pe, last);
    }
  }

  public void outAFeaturePrimaryExpression(AFeaturePrimaryExpression pe) {
    Type oclType=tree.getNodeType(pe);
    if (oclType instanceof OclType) {
      // type name
      appendCode(createDecl("OclType",getVariable(pe))+oclLibPackage+"OclType.getOclTypeFor("+
        instanceName+", \""+pe.toString().trim()+"\");\n");
    } else {
      setVariable(pe, varMap.get( pe.toString().trim() ));
    }
  }

  /** This method breaks the usual pattern of generating Java code postfix
   *  since the collection must be declared and instanciated prior to setting
   *  its contents (in <CODE>outAExpressionListOrRange</CODE>).
   */
  public void inALitColPrimaryExpression(ALitColPrimaryExpression lcpe) {
    String javaType=getJavaType( tree.getNodeType(lcpe) );
    appendCode(createDecl(javaType,getVariable(lcpe))+oclLibPackage+javaType+".getEmpty"+javaType+"();\n");
  }

  public void outALiteralPrimaryExpression(ALiteralPrimaryExpression lpe) {
    reachThrough(lpe, lpe.getLiteral());
  }

  public void outAParenthesesPrimaryExpression(AParenthesesPrimaryExpression lpe) {
    reachThrough(lpe, lpe.getExpression());
  }

  public void outAIfPrimaryExpression(AIfPrimaryExpression ipe) {
    reachThrough(ipe, ipe.getIfExpression());
  }

  public void outAExpressionListOrRange(AExpressionListOrRange elor) {
    String collectionVar=getVariable(elor.parent().parent());;
    PExpressionListOrRangeTail tail=elor.getExpressionListOrRangeTail();
    if (tail==null) {
      addExpressionToCollection(elor.getExpression(), collectionVar);
    } else if (tail instanceof AListExpressionListOrRangeTail) {
      AListExpressionListOrRangeTail list=(AListExpressionListOrRangeTail)tail;
      addExpressionToCollection(elor.getExpression(), collectionVar);
      Iterator iter=list.getExpressionListTail().iterator();
      while (iter.hasNext()) {
        AExpressionListTail next=(AExpressionListTail)iter.next();
        addExpressionToCollection(next.getExpression(), collectionVar);
      }
    } else {
      // tail instanceof ARangeExpressionListOrRangeTail
      ARangeExpressionListOrRangeTail range=(ARangeExpressionListOrRangeTail)tail;
      appendCode(collectionVar+".setToRange("+getVariable(elor.getExpression())+
        ", "+getVariable(range.getExpression())+");\n");
    }
  }

  /** Iterating methods parameters must be inserted into an inner class,
   *  therefore it is not possible to translate APostfixExpressionTail
   *  postfix. Hence, the case method is overwritten.
   */
  public void caseAPostfixExpressionTail(APostfixExpressionTail pet) {
    Node appliedTo=getPreviousNode(pet);
    Type appliedType=tree.getNodeType(appliedTo);
    AFeatureCall fc=(AFeatureCall)pet.getFeatureCall();
    if (fc.getTimeExpression()!=null) {
      // moved here from method bottom 
      addPreVariable( getVariable(pet), getJavaType( tree.getNodeType(pet) ) );
      // moved here from method bottom end.
      assurePreCode();
      writeToPreCodeOnly();
    }
    if ( pet.getPostfixExpressionTailBegin() instanceof
                AArrowPostfixExpressionTailBegin ) {
      appendPostfixArrowOp(pet, appliedTo, appliedType);
    } else if (appliedType instanceof Basic) {
      appendPostfixBasic(pet, appliedTo, (Basic)appliedType);
    } else {
      String featurePathName=fc.getPathName().toString().trim();
      if (oclAnyOperations.contains(featurePathName)) {
        // property of OclAny, handled like basic types
        appendPostfixBasic(pet, appliedTo, appliedType);
      } else {
        appendPostfixDotOp(pet, appliedTo, appliedType);
      }
    }
    if (fc.getTimeExpression()!=null) {
      writeToBothCodes();
    }
  }

  protected void appendPostfixBasic(APostfixExpressionTail pet, Node appliedTo, Type appliedType) {
    super.caseAPostfixExpressionTail(pet);  // recursive descent
    Type oclType=tree.getNodeType(pet);
    String javaType=getJavaType( oclType );
    AFeatureCall fc=(AFeatureCall) pet.getFeatureCall();
    String featureName=fc.getPathName().toString().trim();
    if (featureName.equals("oclIsNew")) {
      throw new OclException(
        "JavaCodeGenerator does not support the OclAny property \"oclIsNew\""
      );
    }
    appendCode(createDecl(javaType,getVariable(pet)));
    boolean insertCastToInteger=featureName.equals("abs") && (oclType==Basic.INTEGER);
    boolean insertCastToOclAnyImpl=featureName.equals("oclAsType") && (javaType.equals("OclAnyImpl"));
    if (insertCastToInteger) appendCode(oclLibPackage+"Ocl.toOclInteger(");
    if (insertCastToOclAnyImpl) appendCode(oclLibPackage+"Ocl.toOclAnyImpl(");
    appendCode(getVariable(appliedTo)+"."+featureName+"(");
    if (fc.getFeatureCallParameters()!=null) {
      AFeatureCallParameters fcp = (AFeatureCallParameters) fc.getFeatureCallParameters();
      appendActualParameterList((AActualParameterList)fcp.getActualParameterList());
    }
    if (insertCastToInteger || insertCastToOclAnyImpl) appendCode(")");
    appendCode(");\n");
  }


  /** The APostfixExpressionTail begins with '->' and is therefore a collection
   *  operation applied either to a collection or to a single, possible undefined
   *  element (as in <CODE>context Person inv: ... husband->isEmpty ...</CODE>).
   */
  protected void appendPostfixArrowOp(APostfixExpressionTail pet, Node appliedTo, Type appliedType) {
    String javaType=getJavaType( tree.getNodeType(pet) );
    AFeatureCall fc=(AFeatureCall)pet.getFeatureCall();
    String featureName=fc.getPathName().toString().trim();
    AFeatureCallParameters fcp=(AFeatureCallParameters)fc.getFeatureCallParameters();
    AActualParameterList apl=null;
    if (fcp!=null) {
      apl=(AActualParameterList)fcp.getActualParameterList();
    }

    if (featureName.equals("iterate")) {

      String appliedToVariable;
      if (appliedType instanceof Collection) {
        appliedToVariable=getVariable(appliedTo);
      } else {
        appliedToVariable=convertToCollection(appliedTo);
      }
      AIterateDeclarator decl = (AIterateDeclarator) fcp.getDeclarator();
      String oclIter=decl.getIterator().toString().trim();
      String oclAccum=decl.getAccumulator().toString().trim();
      String javaIter;
      if (oclIter.startsWith(tree.getNameCreator().getPrefix())) {
        javaIter=oclIter;
      } else {
        javaIter=tree.getNameCreator().getUniqueName("Iter");
      }
      String javaEvalName=tree.getNameCreator().getUniqueName("Eval");
      String javaCont=tree.getNameCreator().getUniqueName("Accu");
      String javaIterType=getJavaType(tree.getTypeFor(oclIter, apl.getExpression()));
      String javaContType=getJavaType(tree.getTypeFor(oclAccum, apl.getExpression()));
      varMap.put(oclIter, oclLibPackage+"Ocl.to"+javaIterType+"("+javaIter+".getValue())");
      varMap.put(oclAccum, oclLibPackage+"Ocl.to"+javaContType+"("+javaCont+".getValue())");

      decl.getExpression().apply(this); // insert code for accumulator initialization

      appendCode(createDecl("OclIterator",javaIter)+getVariable(appliedTo)+".getIterator();\n");
      appendCode(createDecl("OclContainer",javaCont)+"new "+oclLibPackage+"OclContainer("+getVariable(decl.getExpression())+");\n");
      appendCode(createDecl("OclRootEvaluatable",javaEvalName)+"new "+oclLibPackage+"OclRootEvaluatable() {\n");
      appendCode("  public "+oclLibPackage+"OclRoot evaluate() {\n");
      increaseIndent(4);
      apl.apply(this);
      decreaseIndent(4);
      appendCode("    return "+getVariable(apl.getExpression())+";\n");
      appendCode("  }\n");
      appendCode("};\n");
      appendCode(createDecl(javaType,getVariable(pet)));
      appendCode(oclLibPackage+"Ocl.to"+javaType+"(");
      appendCode(appliedToVariable+".iterate("+javaIter+", "+javaCont+", "+javaEvalName+"));\n");

    } else if (tudresden.ocl.check.TypeChecker.setOfIteratingMethodNames.contains(featureName)) {

      String appliedVariable;
      if (appliedType instanceof Collection) {
        appliedVariable=getVariable(appliedTo);
      } else {
        appliedVariable=convertToCollection(appliedTo);
      }
      AStandardDeclarator decl = (AStandardDeclarator) fcp.getDeclarator();
      String oclIter=decl.getName().toString().trim();
      String javaIter;
      if (oclIter.startsWith(tree.getNameCreator().getPrefix())) {
        javaIter=oclIter;
      } else {
        javaIter=tree.getNameCreator().getUniqueName("Iter");
      }
      String javaEvalName=tree.getNameCreator().getUniqueName("Eval");
      String[] types=getEvaluatableTypes(featureName);
      String javaIterType=getJavaType(tree.getTypeFor(oclIter, apl.getExpression()));
      String javaEvalType=types[0];
      String javaEvalReturn=types[1];
      String javaIterReturn=types[2];
      appendCode(createDecl("OclIterator",javaIter)+getVariable(appliedTo)+".getIterator();\n");
      appendCode(createDecl(javaEvalType,javaEvalName)+"new "+qualifyType(javaEvalType)+"() {\n");
      appendCode("  public "+qualifyType(javaEvalReturn)+" evaluate() {\n");
      varMap.put(oclIter, oclLibPackage+"Ocl.to"+javaIterType+"("+javaIter+".getValue())");
      increaseIndent(4);
      super.caseAPostfixExpressionTail(pet);  // recursive decent
      decreaseIndent(4);
      appendCode("    return "+getVariable(apl.getExpression())+";\n");
      appendCode("  }\n");
      appendCode("};\n");
      appendCode(createDecl(javaType,getVariable(pet)));
      if (javaType!=javaIterReturn) {
        appendCode(oclLibPackage+"Ocl.to"+javaType+"(");
      }
      appendCode(appliedVariable+"."+featureName+"("+javaIter+", "+javaEvalName+")");
      if (javaType!=javaIterReturn) {
        appendCode(")");
      }
      appendCode(";\n");
    } else {
      super.caseAPostfixExpressionTail(pet);  // recursive descent
      String appliedVariable;
      if (appliedType instanceof Collection) {
        appliedVariable=getVariable(appliedTo);
      } else {
        appliedVariable=convertToCollection(appliedTo);
      }
      boolean insertCast=false;
      if (
          featureName.equals("first") || featureName.equals("last") ||
          featureName.equals("sum") || featureName.equals("at") ||
          featureName.equals("including") || featureName.equals ("excluding")) {
        insertCast=true;
      }
      appendCode(createDecl(javaType,getVariable(pet)));
      if (insertCast) appendCode(oclLibPackage+"Ocl.to"+javaType+"(");
      appendCode(appliedVariable+"."+featureName+"(");
      appendActualParameterList(apl);
      if (insertCast) appendCode(")");
      appendCode(");\n");
    }
  }

  protected String convertToCollection(Node appliedTo) {
    String name=tree.getNameCreator().getUniqueName("Set");
    appendCode(createDecl("OclSet",name)+oclLibPackage+"OclSet.getEmptyOclSet();\n" );
    appendCode( name+".setToInclude("+getVariable(appliedTo)+");\n" );
    return name;
  }

  /** @param  featureName one of the iterating method names, excluding iterate
   *
   *  @return an String array containing the types connected to the iterating
   *          method with the given name; the returned array has length 3, with
   *          the name of the evaluatable interface at index 0, the name of the
   *          <CODE>evaluate()</CODE> methods return type at index 1, and the
   *          return type of the iterating method at index 2.
   */
  protected String[] getEvaluatableTypes(String featureName) {
    String[] ret=new String[3];
    if (featureName.equals("collect")) {
      ret[0]="OclRootEvaluatable";
      ret[1]="OclRoot";
      ret[2]="OclCollection";
    } else if (featureName.equals("isUnique")) {
      ret[0]="OclRootEvaluatable";
      ret[1]="OclRoot";
      ret[2]="OclBoolean";
    } else if (featureName.equals("sortedBy")) {
      ret[0]="OclComparableEvaluatable";
      ret[1]="java.lang.Comparable";
      ret[2]="OclSequence";
    } else if (featureName.equals("exists")) {
      ret[0]="OclBooleanEvaluatable";
      ret[1]="OclBoolean";
      ret[2]="OclBoolean";
    } else if (featureName.equals("forAll")) {
      ret[0]="OclBooleanEvaluatable";
      ret[1]="OclBoolean";
      ret[2]="OclBoolean";
    } else if (featureName.equals("reject")) {
      ret[0]="OclBooleanEvaluatable";
      ret[1]="OclBoolean";
      ret[2]="OclCollection";
    } else if (featureName.equals("select")) {
      ret[0]="OclBooleanEvaluatable";
      ret[1]="OclBoolean";
      ret[2]="OclCollection";
    } else {
      throw new RuntimeException("Illegal iterating method name");
    }
    return ret;
  }

  /** The APostfixExpressionTail has a '.' begin and is not applied to basic type.
   *  Hence it must be a simple feature call on an application object or the shorthand for
   *  collect.
   */
  protected void appendPostfixDotOp(APostfixExpressionTail pet, Node appliedTo, Type appliedType) {
    super.caseAPostfixExpressionTail(pet); // recursive descent
    String appliedVar=getVariable(appliedTo);
    String javaType=getJavaType( tree.getNodeType(pet) );
    String paramVar=null;
    String qualifVar=null;
    AFeatureCall fc=(AFeatureCall)pet.getFeatureCall();
    if (fc.getFeatureCallParameters()!=null) {
      // access to operation of application type
      AFeatureCallParameters fcp=(AFeatureCallParameters)fc.getFeatureCallParameters();
      AActualParameterList apl=(AActualParameterList)fcp.getActualParameterList();
      if (apl==null) {
        // operation without parameters
        paramVar="null";
      } else {
        // operation with parameters
        paramVar=tree.getNameCreator().getUniqueName("Param");
        java.util.ArrayList paramExpressions=new java.util.ArrayList();
        paramExpressions.add(apl.getExpression());
        Iterator iter=apl.getActualParameterListTail().iterator();
        while (iter.hasNext()) {
          AActualParameterListTail aplt=(AActualParameterListTail) iter.next();
          paramExpressions.add(aplt.getExpression());
        }
        appendCode("Object[] "+paramVar+"=new Object["+paramExpressions.size()+"];\n");
        for (int i=0; i<paramExpressions.size(); i++) {
          // the "null" in the next statement is the point where reconversion information
          // could be added
          appendCode(paramVar+"["+i+"]="+oclLibPackage+"Ocl.reconvert("+
            "null, "+getVariable((AExpression)paramExpressions.get(i))+");\n");
        }
      }
    }
    if(fc.getQualifiers()!=null)
    {
      AQualifiers aqs=(AQualifiers)fc.getQualifiers();
      AActualParameterList apl=(AActualParameterList)aqs.getActualParameterList();
      LinkedList tail=apl.getActualParameterListTail();
      if(!tail.isEmpty())
        throw new RuntimeException("Java code generator can handle on qualifier only.");
      AExpression theQualifier=(AExpression)apl.getExpression();
      qualifVar=tree.getNameCreator().getUniqueName("Qualif");
      appendCode("Object "+qualifVar+"="+oclLibPackage+"Ocl.reconvert("+
        "null, "+getVariable(theQualifier)+");\n");
    }
    appendCode(createDecl(javaType,getVariable(pet))+
      oclLibPackage+"Ocl.to"+javaType+"("+
      appliedVar+".getFeature"+(qualifVar!=null?"Qualified":"")+"(\""+
      fc.getPathName().toString().trim()+
      "\""
    );
    if (paramVar!=null) {
      appendCode(", "+paramVar);
    }
    if (qualifVar!=null) {
      appendCode(", "+qualifVar);
    }
    appendCode("));\n");
  }

  /** append the Java code for an AActualParameterList, excluding the
   *  parentheses
   *
   *  @param apl may be null, then nothing is done
   */
  protected void appendActualParameterList(AActualParameterList apl) {
    if (apl!=null) {
      appendCode( getVariable(apl.getExpression()) );
      Iterator iter=apl.getActualParameterListTail().iterator();
      while (iter.hasNext()) {
        AActualParameterListTail aplt=(AActualParameterListTail)iter.next();
        appendCode(
          ", "+
          getVariable(aplt.getExpression())
        );
      }
    }
  }

  public void outALetExpression(ALetExpression le) {
    reachThrough(le, le.getExpression());
    varMap.put(le.getName().toString().trim(), getVariable(le));
  }

  public void outAStringLiteral(AStringLiteral sl) {
    String lit=sl.toString().trim();
    lit=lit.substring(1, lit.length()-1); // remove "'"
    appendCode(createDecl("OclString",getVariable(sl))+"new "+oclLibPackage+"OclString(\""+lit+"\");\n");
  }

  public void outARealLiteral(ARealLiteral rl) {
    String lit=rl.toString().trim();
    appendCode(createDecl("OclReal",getVariable(rl))+"new "+oclLibPackage+"OclReal("+lit+");\n");
  }

  public void outAIntegerLiteral(AIntegerLiteral il) {
    String lit=il.toString().trim();
    appendCode(createDecl("OclInteger",getVariable(il))+"new "+oclLibPackage+"OclInteger("+lit+");\n");
  }

  public void outABooleanLiteral(ABooleanLiteral bl) {
    String jc;
    if (bl.toString().trim().equals("true")) {
      jc=oclLibPackage+"OclBoolean.TRUE";
    } else {
      jc=oclLibPackage+"OclBoolean.FALSE";
    }
    appendCode(createDecl("OclBoolean",getVariable(bl))+jc+";\n");
  }

  public void outAEnumLiteral(AEnumLiteral el) {
    throw new OclException("JavaCodeGenerator can not handle Enumeration type");
  }



  /** @return the node that the APostfixExpressionTail is applied to, which must
   *          be either an PPrimaryExpression or another APostfixExpressionTail
   *          node
   */
  protected Node getPreviousNode(APostfixExpressionTail pet) {
    Node ret;
    APostfixExpression pe=(APostfixExpression)pet.parent();
    ListIterator iter=pe.getPostfixExpressionTail().listIterator();
    while (iter.next()!=pet);
    iter.previous();
    if (iter.hasPrevious()) {
      ret=(APostfixExpressionTail) iter.previous();
    } else {
      ret=pe.getPrimaryExpression();
    }
    return ret;
  }

  protected void addExpressionToCollection(PExpression e, String collVar) {
    appendCode(collVar+".setToInclude("+getVariable(e)+");\n");
  }

  protected String getJavaType(Type t) {
    if (t instanceof Basic) {
      if (t==Basic.INTEGER) return "OclInteger";
      else if (t==Basic.REAL) return "OclReal";
      else if (t==Basic.BOOLEAN) return "OclBoolean";
      else if (t==Basic.STRING) return "OclString";
      else throw new RuntimeException("illegal basic type");
    } else if (t instanceof Collection) {
      Collection c=(Collection) t;
      switch (c.getCollectionKind()) {
        case Collection.SET: return "OclSet";
        case Collection.BAG: return "OclBag";
        case Collection.SEQUENCE: return "OclSequence";
        case Collection.COLLECTION: return "OclCollection";
        default: throw new RuntimeException("illegal collection type");
      }
    } else if (t instanceof OclType) {
      return "OclType";
    } else {
      // application type
      return "OclAnyImpl";
    }
  }


  public void inAAndLogicalOperator(AAndLogicalOperator lo) {
    operatorCode.put( lo, "and" );
  }

  public void inAOrLogicalOperator(AOrLogicalOperator lo) {
    operatorCode.put( lo, "or" );
  }

  public void inAXorLogicalOperator(AXorLogicalOperator lo) {
    operatorCode.put( lo, "xor" );
  }

  public void inAImpliesLogicalOperator(AImpliesLogicalOperator lo) {
    operatorCode.put( lo, "implies" );
  }

  public void inAEqualRelationalOperator(AEqualRelationalOperator ero) {
    operatorCode.put( ero, "isEqualTo" );
  }

  public void inANEqualRelationalOperator(ANEqualRelationalOperator nero) {
    operatorCode.put( nero, "isNotEqualTo" );
  }

  public void inAGtRelationalOperator(AGtRelationalOperator ro) {
    operatorCode.put( ro, "isGreaterThan" );
  }

  public void inALtRelationalOperator(ALtRelationalOperator ro) {
    operatorCode.put( ro, "isLessThan" );
  }

  public void inAGteqRelationalOperator(AGteqRelationalOperator ro) {
    operatorCode.put( ro, "isGreaterEqual" );
  }

  public void inALteqRelationalOperator(ALteqRelationalOperator ro) {
    operatorCode.put( ro, "isLessEqual" );
  }

  public void inAPlusAddOperator(APlusAddOperator pao) {
    operatorCode.put( pao, "add" );
  }

  public void inAMinusAddOperator(AMinusAddOperator mao) {
    operatorCode.put( mao, "subtract" );
  }

  public void inAMultMultiplyOperator(AMultMultiplyOperator mmo) {
    operatorCode.put( mmo, "multiply" );
  }

  public void inADivMultiplyOperator(ADivMultiplyOperator dmo) {
    operatorCode.put( dmo, "divide" );
  }

  public void inAMinusUnaryOperator(AMinusUnaryOperator muo) {
    operatorCode.put( muo, "negative" );
  }

  public void inANotUnaryOperator(ANotUnaryOperator nuo) {
    operatorCode.put( nuo, "not" );
  }
}

class StringStringMap {

  HashMap map=new HashMap();

  public void put(String key, String value) {
    map.put(key, value);
  }

  public String get(String key) {
    if (!map.containsKey(key)) {
      throw new RuntimeException(
        "StringStringMap does not contain key: "+key
      );
    }
    return (String) map.get(key);
  }

  public void clear() {
    map.clear();
  }
}
