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

package tudresden.ocl;

import tudresden.ocl.check.OclTypeException;
import tudresden.ocl.parser.OclParserException;
import tudresden.ocl.parser.analysis.DepthFirstAdapter;
import tudresden.ocl.parser.node.AAdditiveExpression;
import tudresden.ocl.parser.node.AExpression;
import tudresden.ocl.parser.node.AFeaturePrimaryExpression;
import tudresden.ocl.parser.node.ALogicalExpression;
import tudresden.ocl.parser.node.AMultiplicativeExpression;
import tudresden.ocl.parser.node.ANamePathNameBegin;
import tudresden.ocl.parser.node.APathName;
import tudresden.ocl.parser.node.APostfixExpression;
import tudresden.ocl.parser.node.ARelationalExpression;
import tudresden.ocl.parser.node.ATypeNamePathNameBegin;
import tudresden.ocl.parser.node.AUnaryUnaryExpression;
import tudresden.ocl.parser.node.TName;

/** A tree traversal class that searchs for a name in a expression,
 *  asserting that there is nothing else in the expression than
 *  the name; whether name means TName or APathName can be configured.
 */
public class OnlyNameFinder extends DepthFirstAdapter {

  AExpression expr;
  TName resultName;
  APathName resultPathName;

  boolean pathName;

  /** @param pathName indicates whether a path name should be searched,
   *                  rather than a TName
   */
  public OnlyNameFinder(boolean pathName) {
    this.pathName=pathName;
  }

  public void inAExpression(AExpression e) {
    expr=e;
    assertTrue(e.getLetExpression().isEmpty());
  }

  public void inALogicalExpression(ALogicalExpression le) {
    assertTrue(le.getLogicalExpressionTail().isEmpty());
  }

  public void inARelationalExpression(ARelationalExpression re) {
    assertTrue(re.getRelationalExpressionTail()==null);
  }

  public void inAAdditiveExpression(AAdditiveExpression ae) {
    assertTrue(ae.getAdditiveExpressionTail().isEmpty());
  }

  public void inAMultiplicativeExpression(AMultiplicativeExpression me) {
    assertTrue(me.getMultiplicativeExpressionTail().isEmpty());
  }

  public void inAUnaryUnaryExpression(AUnaryUnaryExpression uue) {
    assertTrue(false);
  }

  public void inAPostfixExpression(APostfixExpression pe) {
    assertTrue(pe.getPostfixExpressionTail().isEmpty());
    assertTrue(pe.getPrimaryExpression() instanceof AFeaturePrimaryExpression);
  }

  public void inAFeaturePrimaryExpression(AFeaturePrimaryExpression fpe) {
    assertTrue(fpe.getTimeExpression()==null);
    assertTrue(fpe.getQualifiers()==null);
    assertTrue(fpe.getFeatureCallParameters()==null);
  }

  public void inAPathName(APathName pn) {
    if (! pathName) {
      assertTrue(pn.getPathNameTail().isEmpty());
    } else {
      resultPathName=pn;
    }
  }

  public void caseATypeNamePathNameBegin(ATypeNamePathNameBegin tnpnb) {
    if (! pathName) {
      assertTrue(false);
    }
  }

  public void caseANamePathNameBegin(ANamePathNameBegin npnb) {
    resultName=npnb.getName();
  }

  public TName getName() {
    assertTrue(resultName!=null);
    return resultName;
  }

  public APathName getPathName() {
    assertTrue(resultPathName!=null);
    return resultPathName;
  }

  protected void assertTrue(boolean b) {
    if (b==false) {
      if (pathName) {
        throw new OclTypeException(
          "illegal format in feature call parameter: \""+expr+"\" is expected "+
          "to be a state name"
        );
      } else {
        throw new OclParserException(
          "illegal format in declarator: \""+expr+"\" should be a name"
        );
      }
    }
  }
}


