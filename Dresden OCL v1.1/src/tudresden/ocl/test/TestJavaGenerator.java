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
package tudresden.ocl.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import tudresden.ocl.DocCheck;
import tudresden.ocl.OclTree;
import tudresden.ocl.codegen.CodeFragment;
import tudresden.ocl.codegen.JavaCodeGenerator;

public class TestJavaGenerator extends TestCase {

  static StringBuffer javaFile=new StringBuffer();
  static int index=0;

  static String oclLibPackage="tudresden.ocl.lib.";

  public TestJavaGenerator(String n) {
    super(n);
  }

  public void testLet() {
    assertGenerate(
      "context Person inv: let a=age in a>0",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode1="+oclLibPackage+"Ocl.toOclInteger(tudOclNode0.getFeature(\"age\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2=new "+oclLibPackage+"OclInteger(0);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3=tudOclNode1.isGreaterThan(tudOclNode2);"
    );
    assertGenerate(
      "context Person inv: let a=age in let b=a in b>0",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode1="+oclLibPackage+"Ocl.toOclInteger(tudOclNode0.getFeature(\"age\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2=new "+oclLibPackage+"OclInteger(0);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3=tudOclNode1.isGreaterThan(tudOclNode2);"
    );
    assertGenerate(
      "context Company inv: let var1=manager.age in let var2=var1+1 in var2>var1",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclAnyImpl tudOclNode1="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclNode0.getFeature(\"manager\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2="+oclLibPackage+"Ocl.toOclInteger(tudOclNode1.getFeature(\"age\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3=new "+oclLibPackage+"OclInteger(1);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode4=tudOclNode2.add(tudOclNode3);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode5=tudOclNode4.isGreaterThan(tudOclNode2);\n"
    );
  }

  public void testColLiteral() {
    assertGenerate(
      "context Person inv: Set{ 0 }->includes(age)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"OclSet.getEmptyOclSet();\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2=new "+oclLibPackage+"OclInteger(0);\n"+
      "tudOclNode1.setToInclude(tudOclNode2);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3="+oclLibPackage+"Ocl.toOclInteger(tudOclNode0.getFeature(\"age\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode1.includes(tudOclNode3);\n"
    );
    assertGenerate(
      "context Person inv: Set{ 0 .. 199 }->includes(age)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"OclSet.getEmptyOclSet();\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2=new "+oclLibPackage+"OclInteger(0);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3=new "+oclLibPackage+"OclInteger(199);\n"+
      "tudOclNode1.setToRange(tudOclNode2, tudOclNode3);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode4="+oclLibPackage+"Ocl.toOclInteger(tudOclNode0.getFeature(\"age\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode5=tudOclNode1.includes(tudOclNode4);\n"
    );
    assertGenerate(
      "context Person inv: Set{ 0, 1, 2, 3, 4 }->includes(age)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"OclSet.getEmptyOclSet();\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2=new "+oclLibPackage+"OclInteger(0);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3=new "+oclLibPackage+"OclInteger(1);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode4=new "+oclLibPackage+"OclInteger(2);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode5=new "+oclLibPackage+"OclInteger(3);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode6=new "+oclLibPackage+"OclInteger(4);\n"+
      "tudOclNode1.setToInclude(tudOclNode2);\n"+
      "tudOclNode1.setToInclude(tudOclNode3);\n"+
      "tudOclNode1.setToInclude(tudOclNode4);\n"+
      "tudOclNode1.setToInclude(tudOclNode5);\n"+
      "tudOclNode1.setToInclude(tudOclNode6);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode7="+oclLibPackage+"Ocl.toOclInteger(tudOclNode0.getFeature(\"age\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode8=tudOclNode1.includes(tudOclNode7);\n"
    );
  }

  public void testPostfix() {
    // postfix property of basic types
    assertGenerate(
      "context Person inv: (-1). abs=1",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode1=new "+oclLibPackage+"OclInteger(1);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2="+oclLibPackage+"Ocl.toOclInteger(tudOclNode1.negative());\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3="+oclLibPackage+"Ocl.toOclInteger(tudOclNode2.abs());\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode4=new "+oclLibPackage+"OclInteger(1);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode5=tudOclNode3.isEqualTo(tudOclNode4);\n"
    );
    // basic type, with parameters
    assertGenerate(
      "context Person inv: let firstLetter=self.name.substring(1,1) in "+
      "firstLetter=firstLetter.toUpper",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclString tudOclNode1="+oclLibPackage+"Ocl.toOclString(tudOclNode0.getFeature(\"name\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2=new "+oclLibPackage+"OclInteger(1);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3=new "+oclLibPackage+"OclInteger(1);\n"+
      "final "+oclLibPackage+"OclString tudOclNode4=tudOclNode1.substring(tudOclNode2, tudOclNode3);\n"+
      "final "+oclLibPackage+"OclString tudOclNode5=tudOclNode4.toUpper();\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode6=tudOclNode4.isEqualTo(tudOclNode5);\n"
    );
    // operation of application type, without parameters
    assertGenerate(
      "context Company inv: getOldestEmployeeAge()<manager.age+20",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode1="+oclLibPackage+"Ocl.toOclInteger(tudOclNode0.getFeature(\"getOldestEmployeeAge\", null));\n"+
      "final "+oclLibPackage+"OclAnyImpl tudOclNode2="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclNode0.getFeature(\"manager\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3="+oclLibPackage+"Ocl.toOclInteger(tudOclNode2.getFeature(\"age\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode4=new "+oclLibPackage+"OclInteger(20);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode5=tudOclNode3.add(tudOclNode4);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode6=tudOclNode1.isLessThan(tudOclNode5);\n"
    );
    // operation of application type, with parameters
    assertGenerate(
      "context Person inv: getIncomeAfterTax(0.3)<=getIncomeAfterTax(0)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclReal tudOclNode1=new "+oclLibPackage+"OclReal(0.3);\n"+
      "Object[] tudOclParam0=new Object[1];\n"+
      "tudOclParam0[0]="+oclLibPackage+"Ocl.reconvert(null, tudOclNode1);\n"+
      "final "+oclLibPackage+"OclReal tudOclNode2="+oclLibPackage+"Ocl.toOclReal(tudOclNode0.getFeature(\"getIncomeAfterTax\", tudOclParam0));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3=new "+oclLibPackage+"OclInteger(0);\n"+
      "Object[] tudOclParam1=new Object[1];\n"+
      "tudOclParam1[0]="+oclLibPackage+"Ocl.reconvert(null, tudOclNode3);\n"+
      "final "+oclLibPackage+"OclReal tudOclNode4="+oclLibPackage+"Ocl.toOclReal(tudOclNode0.getFeature(\"getIncomeAfterTax\", tudOclParam1));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode5=tudOclNode2.isLessEqual(tudOclNode4);\n"
    );
    // iterating methods are tested in testIterate
  }

  public void testIterate() {
    // forAll
    assertGenerate(
      "context Company inv: employees->forAll(not isUnemployed)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employees\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclBooleanEvaluatable tudOclEval0=new "+oclLibPackage+"OclBooleanEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclBoolean evaluate() {\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"isUnemployed\"));\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode3=tudOclNode2.not();\n"+
      "    return tudOclNode3;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode1.forAll(tudOclIter0, tudOclEval0);\n"
    );
    // exists
    assertGenerate(
      "context Company inv: employees->exists(p|p=self.manager)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employees\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclBooleanEvaluatable tudOclEval0=new "+oclLibPackage+"OclBooleanEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclBoolean evaluate() {\n"+
      "    final "+oclLibPackage+"OclAnyImpl tudOclNode2="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclNode0.getFeature(\"manager\"));\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode3="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).isEqualTo(tudOclNode2);\n"+
      "    return tudOclNode3;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode1.exists(tudOclIter0, tudOclEval0);\n"
    );
    // Iterator with type Integer
    assertGenerate(
      "context Person inv: Set{1, 2, 3}->forAll(i|(i+1)>1)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"OclSet.getEmptyOclSet();\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2=new "+oclLibPackage+"OclInteger(1);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3=new "+oclLibPackage+"OclInteger(2);\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode4=new "+oclLibPackage+"OclInteger(3);\n"+
      "tudOclNode1.setToInclude(tudOclNode2);\n"+
      "tudOclNode1.setToInclude(tudOclNode3);\n"+
      "tudOclNode1.setToInclude(tudOclNode4);\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclBooleanEvaluatable tudOclEval0=new "+oclLibPackage+"OclBooleanEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclBoolean evaluate() {\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode5=new "+oclLibPackage+"OclInteger(1);\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode6="+oclLibPackage+"Ocl.toOclInteger(tudOclIter0.getValue()).add(tudOclNode5);\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode7=new "+oclLibPackage+"OclInteger(1);\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode8=tudOclNode6.isGreaterThan(tudOclNode7);\n"+
      "    return tudOclNode8;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode9=tudOclNode1.forAll(tudOclIter0, tudOclEval0);\n"
    );
    // isUnique
    assertGenerate(
      "context Company inv: employees->isUnique(name)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employees\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclRootEvaluatable tudOclEval0=new "+oclLibPackage+"OclRootEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclRoot evaluate() {\n"+
      "    final "+oclLibPackage+"OclString tudOclNode2="+oclLibPackage+"Ocl.toOclString("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"name\"));\n"+
      "    return tudOclNode2;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3=tudOclNode1.isUnique(tudOclIter0, tudOclEval0);\n"
    );
    // sortedBy
    assertGenerate(
      "context Person inv: managedCompanies->sortedBy(numberOfEmployees)->first.numberOfEmployees>100",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"managedCompanies\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclComparableEvaluatable tudOclEval0=new "+oclLibPackage+"OclComparableEvaluatable() {\n"+
      "  public java.lang.Comparable evaluate() {\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode2="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"numberOfEmployees\"));\n"+
      "    return tudOclNode2;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclSequence tudOclNode3=tudOclNode1.sortedBy(tudOclIter0, tudOclEval0);\n"+
      "final "+oclLibPackage+"OclAnyImpl tudOclNode4="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclNode3.first());\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode5="+oclLibPackage+"Ocl.toOclInteger(tudOclNode4.getFeature(\"numberOfEmployees\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode6=new "+oclLibPackage+"OclInteger(100);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode7=tudOclNode5.isGreaterThan(tudOclNode6);\n"
    );
    // selected
    assertGenerate(
      "context Customer inv: cards->select(color=color_gold)->notEmpty implies title<>''",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"cards\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclBooleanEvaluatable tudOclEval0=new "+oclLibPackage+"OclBooleanEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclBoolean evaluate() {\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode2="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"color\"));\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode3="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"color_gold\"));\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode2.isEqualTo(tudOclNode3);\n"+
      "    return tudOclNode4;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclSet tudOclNode5="+oclLibPackage+"Ocl.toOclSet(tudOclNode1.select(tudOclIter0, tudOclEval0));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode6=tudOclNode5.notEmpty();\n"+
      "final "+oclLibPackage+"OclString tudOclNode7="+oclLibPackage+"Ocl.toOclString(tudOclNode0.getFeature(\"title\"));\n"+
      "final "+oclLibPackage+"OclString tudOclNode8=new "+oclLibPackage+"OclString(\"\");\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode9=tudOclNode7.isNotEqualTo(tudOclNode8);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode10=tudOclNode6.implies(tudOclNode9);\n"
    );
    // reject
    assertGenerate(
      "context Company inv: employees->reject(isUnemployed) = employees",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employees\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclBooleanEvaluatable tudOclEval0=new "+oclLibPackage+"OclBooleanEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclBoolean evaluate() {\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"isUnemployed\"));\n"+
      "    return tudOclNode2;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclSet tudOclNode3="+oclLibPackage+"Ocl.toOclSet(tudOclNode1.reject(tudOclIter0, tudOclEval0));\n"+
      "final "+oclLibPackage+"OclSet tudOclNode4="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employees\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode5=tudOclNode3.isEqualTo(tudOclNode4);\n"
    );
    // collect collection
    assertGenerate(
      "context Person inv: managedCompanies->collect(employees)->includes(self)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"managedCompanies\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclRootEvaluatable tudOclEval0=new "+oclLibPackage+"OclRootEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclRoot evaluate() {\n"+
      "    final "+oclLibPackage+"OclSet tudOclNode2="+oclLibPackage+"Ocl.toOclSet("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"employees\"));\n"+
      "    return tudOclNode2;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclBag tudOclNode3="+oclLibPackage+"Ocl.toOclBag(tudOclNode1.collect(tudOclIter0, tudOclEval0));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode3.includes(tudOclNode0);\n"
    );
    // collect basic value
    assertGenerate(
      "context Company inv: employees->collect(age)->sum > 30",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employees\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclRootEvaluatable tudOclEval0=new "+oclLibPackage+"OclRootEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclRoot evaluate() {\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode2="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"age\"));\n"+
      "    return tudOclNode2;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclBag tudOclNode3="+oclLibPackage+"Ocl.toOclBag(tudOclNode1.collect(tudOclIter0, tudOclEval0));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode4="+oclLibPackage+"Ocl.toOclInteger(tudOclNode3.sum());\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode5=new "+oclLibPackage+"OclInteger(30);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode6=tudOclNode4.isGreaterThan(tudOclNode5);\n"
    );
    // iterate
    assertGenerate(
      "context Company inv: employees->iterate(p:Person; i:Integer=0 | i+p.age)>30",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employees\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode2=new "+oclLibPackage+"OclInteger(0);\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclContainer tudOclAccu0=new "+oclLibPackage+"OclContainer(tudOclNode2);\n"+
      "final "+oclLibPackage+"OclRootEvaluatable tudOclEval0=new "+oclLibPackage+"OclRootEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclRoot evaluate() {\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode3="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"age\"));\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode4="+oclLibPackage+"Ocl.toOclInteger(tudOclAccu0.getValue()).add(tudOclNode3);\n"+
      "    return tudOclNode4;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode5="+oclLibPackage+"Ocl.toOclInteger(tudOclNode1.iterate(tudOclIter0, tudOclAccu0, tudOclEval0));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode6=new "+oclLibPackage+"OclInteger(30);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode7=tudOclNode5.isGreaterThan(tudOclNode6);\n"
    );
    // twice iterate
    assertGenerate(
      "context ProgramPartner inv: \n"+
      "self.deliveredServices.transactions->iterate(\n"+
      "  t:Transaction; result:Integer=0 |\n"+
      "  if t.oclIsTypeOf(Burning) then result+t.points else result endif\n"+
      ")\n"+
      ">=\n"+
      "self.deliveredServices.transactions->iterate(\n"+
      "  t:Transaction; result:Integer=0 |\n"+
      "  if t.oclIsTypeOf(Earning) then result+t.points else result endif\n"+
      ")\n",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"deliveredServices\"));\n"+
      "final "+oclLibPackage+"OclBag tudOclNode2="+oclLibPackage+"Ocl.toOclBag(tudOclNode1.getFeature(\"transactions\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode3=new "+oclLibPackage+"OclInteger(0);\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter1=tudOclNode2.getIterator();\n"+
      "final "+oclLibPackage+"OclContainer tudOclAccu1=new "+oclLibPackage+"OclContainer(tudOclNode3);\n"+
      "final "+oclLibPackage+"OclRootEvaluatable tudOclEval0=new "+oclLibPackage+"OclRootEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclRoot evaluate() {\n"+
      "    final "+oclLibPackage+"OclType tudOclNode4="+oclLibPackage+"OclType.getOclTypeFor(this, \"Burning\");\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode5="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter1.getValue()).oclIsTypeOf(tudOclNode4);\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode6="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter1.getValue()).getFeature(\"points\"));\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode7="+oclLibPackage+"Ocl.toOclInteger(tudOclAccu1.getValue()).add(tudOclNode6);\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode8=(tudOclNode5.isTrue()) ? (tudOclNode7) : ("+oclLibPackage+"Ocl.toOclInteger(tudOclAccu1.getValue()));\n"+
      "    return tudOclNode8;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode9="+oclLibPackage+"Ocl.toOclInteger(tudOclNode2.iterate(tudOclIter1, tudOclAccu1, tudOclEval0));\n"+
      "final "+oclLibPackage+"OclSet tudOclNode10="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"deliveredServices\"));\n"+
      "final "+oclLibPackage+"OclBag tudOclNode11="+oclLibPackage+"Ocl.toOclBag(tudOclNode10.getFeature(\"transactions\"));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode12=new "+oclLibPackage+"OclInteger(0);\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode11.getIterator();\n"+
      "final "+oclLibPackage+"OclContainer tudOclAccu2=new "+oclLibPackage+"OclContainer(tudOclNode12);\n"+
      "final "+oclLibPackage+"OclRootEvaluatable tudOclEval1=new "+oclLibPackage+"OclRootEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclRoot evaluate() {\n"+
      "    final "+oclLibPackage+"OclType tudOclNode13="+oclLibPackage+"OclType.getOclTypeFor(this, \"Earning\");\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode14="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).oclIsTypeOf(tudOclNode13);\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode15="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"points\"));\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode16="+oclLibPackage+"Ocl.toOclInteger(tudOclAccu2.getValue()).add(tudOclNode15);\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode17=(tudOclNode14.isTrue()) ? (tudOclNode16) : ("+oclLibPackage+"Ocl.toOclInteger(tudOclAccu2.getValue()));\n"+
      "    return tudOclNode17;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode18="+oclLibPackage+"Ocl.toOclInteger(tudOclNode11.iterate(tudOclIter0, tudOclAccu2, tudOclEval1));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode19=tudOclNode9.isGreaterEqual(tudOclNode18);\n"
    );

    // double: forAll, exists
    assertGenerate(
      "context Person inv: self.employers->forAll(c|c.employees->exists(p|p=self)) or self.isUnemployed",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employers\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclBooleanEvaluatable tudOclEval0=new "+oclLibPackage+"OclBooleanEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclBoolean evaluate() {\n"+
      "    final "+oclLibPackage+"OclSet tudOclNode2="+oclLibPackage+"Ocl.toOclSet("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"employees\"));\n"+
      "    final "+oclLibPackage+"OclIterator tudOclIter1=tudOclNode2.getIterator();\n"+
      "    final "+oclLibPackage+"OclBooleanEvaluatable tudOclEval1=new "+oclLibPackage+"OclBooleanEvaluatable() {\n"+
      "      public "+oclLibPackage+"OclBoolean evaluate() {\n"+
      "        final "+oclLibPackage+"OclBoolean tudOclNode3="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter1.getValue()).isEqualTo(tudOclNode0);\n"+
      "        return tudOclNode3;\n"+
      "      }\n"+
      "    };\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode2.exists(tudOclIter1, tudOclEval1);\n"+
      "    return tudOclNode4;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode5=tudOclNode1.forAll(tudOclIter0, tudOclEval0);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode6="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode7=tudOclNode5.or(tudOclNode6);\n"
    );
    // double: select, isUnique
    assertGenerate(
      "context Company inv: self.employees->select(p:Person|p.age<18)->isUnique(name)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employees\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter1=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclBooleanEvaluatable tudOclEval0=new "+oclLibPackage+"OclBooleanEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclBoolean evaluate() {\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode2="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter1.getValue()).getFeature(\"age\"));\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode3=new "+oclLibPackage+"OclInteger(18);\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode2.isLessThan(tudOclNode3);\n"+
      "    return tudOclNode4;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclSet tudOclNode5="+oclLibPackage+"Ocl.toOclSet(tudOclNode1.select(tudOclIter1, tudOclEval0));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode5.getIterator();\n"+
      "final "+oclLibPackage+"OclRootEvaluatable tudOclEval1=new "+oclLibPackage+"OclRootEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclRoot evaluate() {\n"+
      "    final "+oclLibPackage+"OclString tudOclNode6="+oclLibPackage+"Ocl.toOclString("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"name\"));\n"+
      "    return tudOclNode6;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode7=tudOclNode5.isUnique(tudOclIter0, tudOclEval1);\n"
    );
    // double: sortedBy, iterate
    assertGenerate(
      "context Person inv: employers->sortedBy(numberOfEmployees)->iterate(c:Company; s:String=''| s.concat(c.manager.name)) = 'bla'",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"employers\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclComparableEvaluatable tudOclEval0=new "+oclLibPackage+"OclComparableEvaluatable() {\n"+
      "  public java.lang.Comparable evaluate() {\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode2="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).getFeature(\"numberOfEmployees\"));\n"+
      "    return tudOclNode2;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclSequence tudOclNode3=tudOclNode1.sortedBy(tudOclIter0, tudOclEval0);\n"+
      "final "+oclLibPackage+"OclString tudOclNode4=new "+oclLibPackage+"OclString(\"\");\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter1=tudOclNode3.getIterator();\n"+
      "final "+oclLibPackage+"OclContainer tudOclAccu0=new "+oclLibPackage+"OclContainer(tudOclNode4);\n"+
      "final "+oclLibPackage+"OclRootEvaluatable tudOclEval1=new "+oclLibPackage+"OclRootEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclRoot evaluate() {\n"+
      "    final "+oclLibPackage+"OclAnyImpl tudOclNode5="+oclLibPackage+"Ocl.toOclAnyImpl("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter1.getValue()).getFeature(\"manager\"));\n"+
      "    final "+oclLibPackage+"OclString tudOclNode6="+oclLibPackage+"Ocl.toOclString(tudOclNode5.getFeature(\"name\"));\n"+
      "    final "+oclLibPackage+"OclString tudOclNode7="+oclLibPackage+"Ocl.toOclString(tudOclAccu0.getValue()).concat(tudOclNode6);\n"+
      "    return tudOclNode7;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclString tudOclNode8="+oclLibPackage+"Ocl.toOclString(tudOclNode3.iterate(tudOclIter1, tudOclAccu0, tudOclEval1));\n"+
      "final "+oclLibPackage+"OclString tudOclNode9=new "+oclLibPackage+"OclString(\"bla\");\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode10=tudOclNode8.isEqualTo(tudOclNode9);\n"
    );
    // select and collect
    assertGenerate(
      "context LoyaltyAccount inv: transactions->select( oclIsTypeOf(Earning) )->collect(points)->sum >= 0",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclSet tudOclNode1="+oclLibPackage+"Ocl.toOclSet(tudOclNode0.getFeature(\"transactions\"));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter0=tudOclNode1.getIterator();\n"+
      "final "+oclLibPackage+"OclBooleanEvaluatable tudOclEval0=new "+oclLibPackage+"OclBooleanEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclBoolean evaluate() {\n"+
      "    final "+oclLibPackage+"OclType tudOclNode2="+oclLibPackage+"OclType.getOclTypeFor(this, \"Earning\");\n"+
      "    final "+oclLibPackage+"OclBoolean tudOclNode3="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter0.getValue()).oclIsTypeOf(tudOclNode2);\n"+
      "    return tudOclNode3;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclSet tudOclNode4="+oclLibPackage+"Ocl.toOclSet(tudOclNode1.select(tudOclIter0, tudOclEval0));\n"+
      "final "+oclLibPackage+"OclIterator tudOclIter1=tudOclNode4.getIterator();\n"+
      "final "+oclLibPackage+"OclRootEvaluatable tudOclEval1=new "+oclLibPackage+"OclRootEvaluatable() {\n"+
      "  public "+oclLibPackage+"OclRoot evaluate() {\n"+
      "    final "+oclLibPackage+"OclInteger tudOclNode5="+oclLibPackage+"Ocl.toOclInteger("+oclLibPackage+"Ocl.toOclAnyImpl(tudOclIter1.getValue()).getFeature(\"points\"));\n"+
      "    return tudOclNode5;\n"+
      "  }\n"+
      "};\n"+
      "final "+oclLibPackage+"OclBag tudOclNode6="+oclLibPackage+"Ocl.toOclBag(tudOclNode4.collect(tudOclIter1, tudOclEval1));\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode7="+oclLibPackage+"Ocl.toOclInteger(tudOclNode6.sum());\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode8=new "+oclLibPackage+"OclInteger(0);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode9=tudOclNode7.isGreaterEqual(tudOclNode8);\n"
    );
  }

  public void testLogExpressions() {
    assertGenerate(
      "context Person inv: isMarried and isUnemployed",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3=tudOclNode1.and(tudOclNode2);\n"
    );
    assertGenerate(
      "context Person inv: isMarried and isUnemployed or isMarried",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode1.and(tudOclNode2).or(tudOclNode3);\n"
    );
    assertGenerate(
      "context Person inv: isMarried implies isUnemployed",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3=tudOclNode1.implies(tudOclNode2);\n"
    );
    assertGenerate(
      "context Person inv: isMarried and isUnemployed implies isMarried",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode1.and(tudOclNode2).implies(tudOclNode3);\n"
    );
    assertGenerate(
      "context Person inv: isMarried implies isUnemployed and isMarried",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode1.implies(tudOclNode2.and(tudOclNode3));\n"
    );
    assertGenerate(
      "context Person inv: isMarried implies isUnemployed implies isMarried",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4=tudOclNode1.implies(tudOclNode2).implies(tudOclNode3);\n"
    );
    assertGenerate(
      "context Person inv: isMarried or isUnemployed implies isMarried and isUnemployed",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode5=tudOclNode1.or(tudOclNode2).implies(tudOclNode3.and(tudOclNode4));\n"
    );
    assertGenerate(
      "context Person inv: isMarried or isUnemployed implies isMarried and isUnemployed implies isMarried",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode2="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode4="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isUnemployed\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode5="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode6=tudOclNode1.or(tudOclNode2).implies(tudOclNode3.and(tudOclNode4)).implies(tudOclNode5);\n"
    );
  }

  public void testElements() {
    assertGenerate(
      "context Person inv: isMarried = ( wife->notEmpty or husband->notEmpty)",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"Ocl.toOclBoolean(tudOclNode0.getFeature(\"isMarried\"));\n"+
      "final "+oclLibPackage+"OclAnyImpl tudOclNode2="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclNode0.getFeature(\"wife\"));\n"+
      "final "+oclLibPackage+"OclSet tudOclSet0="+oclLibPackage+"OclSet.getEmptyOclSet();\n"+
      "tudOclSet0.setToInclude(tudOclNode2);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3=tudOclSet0.notEmpty();\n"+
      "final "+oclLibPackage+"OclAnyImpl tudOclNode4="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclNode0.getFeature(\"husband\"));\n"+
      "final "+oclLibPackage+"OclSet tudOclSet1="+oclLibPackage+"OclSet.getEmptyOclSet();\n"+
      "tudOclSet1.setToInclude(tudOclNode4);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode5=tudOclSet1.notEmpty();\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode6=tudOclNode3.or(tudOclNode5);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode7=tudOclNode1.isEqualTo(tudOclNode6);\n"
    );
    assertGenerate(
      "context Company inv: manager->notEmpty",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclAnyImpl tudOclNode1="+oclLibPackage+"Ocl.toOclAnyImpl(tudOclNode0.getFeature(\"manager\"));\n"+
      "final "+oclLibPackage+"OclSet tudOclSet0="+oclLibPackage+"OclSet.getEmptyOclSet();\n"+
      "tudOclSet0.setToInclude(tudOclNode1);\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode2=tudOclSet0.notEmpty();\n"
    );
  }

  public void testPre() {
    String[] target=new String[3];
    // 0 -> evaluating code; 1 -> preparation code; 2 -> transfer code
    target[0]=
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclReal tudOclOpPar0="+oclLibPackage+"Ocl.toOclReal( "+oclLibPackage+"Ocl.getFor(r) );\n"+
      "final "+oclLibPackage+"OclReal tudOclResult0="+oclLibPackage+"Ocl.toOclReal( "+oclLibPackage+"Ocl.getFor(result) );\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode1="+oclLibPackage+"Ocl.toOclInteger(tudOclNode0.getFeature(\"age\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3=tudOclNode1.isEqualTo(tudOclNode2);\n";
    target[1]=
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclReal tudOclOpPar0="+oclLibPackage+"Ocl.toOclReal( "+oclLibPackage+"Ocl.getFor(r) );\n"+
      "final "+oclLibPackage+"OclReal tudOclResult0=new "+oclLibPackage+"OclReal(0,\"created by JavaCodeGenerator\");\n"+
      "final "+oclLibPackage+"OclInteger tudOclNode1="+oclLibPackage+"Ocl.toOclInteger(tudOclNode0.getFeature(\"age\"));\n"+
      "tudOclNode2="+oclLibPackage+"Ocl.toOclInteger(tudOclNode0.getFeature(\"age\"));\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode3=tudOclNode1.isEqualTo(tudOclNode2);\n";
    target[2]=
      oclLibPackage+"OclInteger tudOclNode2;\n";
    assertGenerate(
      "context Person::getIncomeAfterTax(r: Real):Real post: age=age@pre",
      target
    );
  }

  public void testCompile() {
    try {
      FileWriter fw=new FileWriter("TestOclCompile.java");
      fw.write("import tudresden.ocl.lib.*;\n\n");
      fw.write("class TestOclCompile {\n\n");
      fw.write(javaFile.toString());
      fw.write("}\n");
      fw.close();
    }
    catch (IOException ioe) {
      ioe.printStackTrace(System.out);
      assertTrue(false);
    }
  }
  
  public void testPostConstraints() {
    // bug report by Caroline Nyholm: Code to wrap "result" variable is 
    // generated for post constraints of void-methods
    assertGenerate(
      "context Person::setAge(i: Integer) post: true",
      "final "+oclLibPackage+"OclAnyImpl tudOclNode0="+oclLibPackage+"Ocl.toOclAnyImpl( "+oclLibPackage+"Ocl.getFor(this) );\n"+
      "final "+oclLibPackage+"OclInteger tudOclOpPar0="+oclLibPackage+"Ocl.toOclInteger( "+oclLibPackage+"Ocl.getFor(i) );\n"+
      "final "+oclLibPackage+"OclBoolean tudOclNode1="+oclLibPackage+"OclBoolean.TRUE;\n"
    );      
  }

  protected void assertGenerate(String ocl, String java) {
    String[] array=new String[1];
    array[0]=java;
    assertGenerate(ocl, array);
  }

  protected void assertGenerate(String ocl, String[] java) {
    boolean allIsFine=true;
    StringBuffer errorMessage = new StringBuffer();
    OclTree tree=null;
    try {
      tree=DocCheck.createTree(ocl);
      tree.applyDefaultNormalizations();
      CodeFragment[] frags=tree.getCode(new JavaCodeGenerator("this", "result", oclLibPackage));
      if (java.length!=frags.length) errorMessage.append("wrong number of fragments\n");
      assertTrue( java.length == frags.length );
      for (int i=0; i<java.length; i++) {
        boolean ok=java[i].trim().equals( frags[i].getCode().trim() );
        if (!ok) {
          errorMessage.append("OCL expression:\n"+ocl+"\n");
          errorMessage.append("error in fragment "+i);
          errorMessage.append("  (name "+frags[i].getName()+", kind "+frags[i].getKind()+")\n");
          StringTokenizer stTarget=new StringTokenizer(java[i], "\n");
          StringTokenizer stOutput=new StringTokenizer(frags[i].getCode(), "\n");
          if (stTarget.countTokens()!=stOutput.countTokens()) {
            errorMessage.append("should be:\n"+java[i]+"\n");
            errorMessage.append("("+stTarget.countTokens()+" lines)\n");
            errorMessage.append("but is:\n"+frags[i].getCode()+"\n");
            errorMessage.append("("+stOutput.countTokens()+" lines)\n");
          } else {
            errorMessage.append("(T is target, O is output)\n");
            while (stTarget.hasMoreTokens()) {
              String lineTarget=stTarget.nextToken();
              String lineOutput=stOutput.nextToken();
              errorMessage.append("T "+lineTarget+"\n");
              errorMessage.append("O "+lineOutput+"\n");
              errorMessage.append("  ");
              for (int iLine=0; iLine<lineTarget.length() && iLine<lineOutput.length(); iLine++) {
                if (lineTarget.charAt(iLine)!=lineOutput.charAt(iLine)) {
                  errorMessage.append("x");
                } else {
                  errorMessage.append(" ");
                }
              }
              if (lineOutput.length()!=lineTarget.length()) errorMessage.append(" difference in line length");
              errorMessage.append("\n");
            }
          }
          allIsFine=false;
        }
        if (frags[i].getKind()==CodeFragment.TRANSFER) {
          javaFile.append(frags[i].getCode());
        } else {
          javaFile.append(
            "void test"+(index++)+"() {\n"+
            "Object result=null;\n"+
            frags[i].getCode()+
            "}\n\n");
        }
      }
    }
    catch (Exception e) {
      allIsFine=false;
      errorMessage.append("exception in TestJavaGenerator: \n");
      errorMessage.append("generating code for\n");
      errorMessage.append(ocl+"\n");
      if (tree!=null) {
        errorMessage.append("normalized:\n");
        errorMessage.append(tree.toString()+"\n");
      }
      StringWriter strWriter = new StringWriter();
      e.printStackTrace( new PrintWriter( strWriter ) );
      errorMessage.append( strWriter );
    }
    assertTrue(errorMessage.toString(), allIsFine);
  }

  /*
  public static Test suite() {
    TestSuite t=new TestSuite();
    t.addTest(new TestJavaGenerator("testLet"));
    t.addTest(new TestJavaGenerator("testColLiteral"));
    t.addTest(new TestJavaGenerator("testPostfix"));
    t.addTest(new TestJavaGenerator("testIterate"));
    t.addTest(new TestJavaGenerator("testLogExpressions"));
    t.addTest(new TestJavaGenerator("testElements"));
    t.addTest(new TestJavaGenerator("testPre"));

    //t.addTest(new TestJavaGenerator("testCompile"));
    return t;
  }
*/
}
