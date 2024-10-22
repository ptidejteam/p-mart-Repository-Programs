/*
 *  Author:  Chris Seguin
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package org.acm.seguin.parser.ast;

import org.acm.seguin.parser.JavaParser;
import org.acm.seguin.parser.JavaParserVisitor;

/**
 *  Declares an expression that takes only one argument
 *
 *@author     Chris Seguin
 *@created    October 13, 1999
 */
public class ASTUnaryExpression extends SimpleNode {
	//  Instance Variables
	private String name = "";


	/**
	 *  Constructor for the ASTUnaryExpression object
	 *
	 *@param  id  Description of Parameter
	 */
	public ASTUnaryExpression(int id) {
		super(id);
	}


	/**
	 *  Constructor for the ASTUnaryExpression object
	 *
	 *@param  p   Description of Parameter
	 *@param  id  Description of Parameter
	 */
	public ASTUnaryExpression(JavaParser p, int id) {
		super(p, id);
	}


	/**
	 *  Set the object's name
	 *
	 *@param  newName  the new name
	 */
	public void setName(String newName) {
		name = newName.intern();
	}


	/**
	 *  Get the object's name
	 *
	 *@return    the name
	 */
	public String getName() {
		return name;
	}


	/**
	 *  Convert this object to a string
	 *
	 *@return    a string representing this object
	 */
	public String toString() {
		return super.toString() + " [" + getName() + "]";
	}


	/**
	 *  Accept the visitor.
	 *
	 *@param  visitor  Description of Parameter
	 *@param  data     Description of Parameter
	 *@return          Description of the Returned Value
	 */
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
