/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.core.dom;

/**
 * While statement AST node type.
 *
 * <pre>
 * WhileStatement:
 *    <b>while</b> <b>(</b> Expression <b>)</b> Statement
 * </pre>
 * 
 * @since 2.0
 */
public class WhileStatement extends Statement {

	/**
	 * The expression; lazily initialized; defaults to an unspecified, but 
	 * legal, expression.
	 */
	private Expression expression = null;

	/**
	 * The body statement; lazily initialized; defaults to an empty block 
	 * statement.
	 */
	private Statement body = null;

	/**
	 * Creates a new unparented while statement node owned by the given 
	 * AST. By default, the expresssion is unspecified, but legal, and
	 * the body statement is an empty block.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	WhileStatement(AST ast) {
		super(ast);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	public int getNodeType() {
		return WHILE_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone(AST target) {
		WhileStatement result = new WhileStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.copyLeadingComment(this);
		result.setExpression((Expression) getExpression().clone(target));
		result.setBody((Statement) getBody().clone(target));
		return result;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	public boolean subtreeMatch(ASTMatcher matcher, Object other) {
		// dispatch to correct overloaded match method
		return matcher.match(this, other);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	void accept0(ASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			// visit children in normal left to right reading order
			acceptChild(visitor, getExpression());
			acceptChild(visitor, getBody());
		}
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the expression of this while statement.
	 * 
	 * @return the expression node
	 */ 
	public Expression getExpression() {
		if (expression == null) {
			// lazy initialize - use setter to ensure parent link set too
			long count = getAST().modificationCount();
			setExpression(new SimpleName(getAST()));
			getAST().setModificationCount(count);
		}
		return expression;
	}
	
	/**
	 * Sets the expression of this while statement.
	 * 
	 * @param expression the expression node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setExpression(Expression expression) {
		if (expression == null) {
			throw new IllegalArgumentException();
		}
		// a WhileStatement may occur inside an Expression - must check cycles
		replaceChild(this.expression, expression, true);
		this.expression = expression;
	}

	/**
	 * Returns the body of this while statement.
	 * 
	 * @return the body statement node
	 */ 
	public Statement getBody() {
		if (body == null) {
			// lazy initialize - use setter to ensure parent link set too
			long count = getAST().modificationCount();
			setBody(new Block(getAST()));
			getAST().setModificationCount(count);
		}
		return body;
	}
	
	/**
	 * Sets the body of this while statement.
	 * 
	 * @param statement the body statement node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setBody(Statement statement) {
		if (statement == null) {
			throw new IllegalArgumentException();
		}
		// a WhileStatement may occur inside a Statement - must check cycles
		replaceChild(this.body, statement, true);
		this.body = statement;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return super.memSize() + 2 * 4;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (expression == null ? 0 : getExpression().treeSize())
			+ (body == null ? 0 : getBody().treeSize());
	}
}

