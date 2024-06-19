/*
 * PJEP.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on January 28, 2003, 11:18 PM
 *
 * @(#) $Id: PJEP.java,v 1.1 2006/02/21 01:08:01 vauchers Exp $
 */

package pcgen.util;

import java.util.Stack;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

/**
 * <code>PJEP</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 *
 * Provides a common interface setup for Singular Systems' Java Mathematical Expression Parser
 *
 * Provides the following functions:
 *   ceil, floor, getvar, max, min
 *
 * Provides the following variables:
 *   FALSE, TRUE
 *
 */

public final class PJEP extends JEP
{
	private Object parent = null;

	public PJEP()
	{
		this(null);
	}

	public PJEP(final Object argParent)
	{
		parent = argParent;
		setAllowUndeclared(true);
		addStandardFunctions();
		addFunction("ceil", new Ceil());
		addFunction("floor", new Floor());
		addFunction("max", new Max());
		addFunction("min", new Min());
		addFunction("getvar", new GetVar());
		addVariable("TRUE", 1);
		addVariable("FALSE", 0);

	}

	//
	// eg. getvar("CL=Fighter")
	//
	private final class GetVar extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		public GetVar()
		{
			numberOfParameters = -1;		// allow variable # of parameters
		}

		/**
		 * Runs getvar on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the variable's value is
		 * pushed back to the top of <code>inStack</code>.
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param1;
			Object param2 = null;

			//
			// have to do this in reverse order...this is a stack afterall
			//
			if (curNumberOfParameters == 1)
			{
				param1 = inStack.pop();
			}
			else if (curNumberOfParameters == 2)
			{
				param2 = inStack.pop();
				param1 = inStack.pop();
				if (!(param2 instanceof Double))
				{
					throw new ParseException("Invalid parameter type");
				}
			}
			else
			{
				throw new ParseException("Invalid parameter count");
			}

			Object result = null;

			if (param1 instanceof String)
			{
				if (parent instanceof PlayerCharacter)
				{
					result = ((PlayerCharacter) parent).getVariable((String) param1, true, true, "", "");
				}
				else if (parent instanceof Equipment)
				{
					boolean bPrimary = true;
					if (param2 != null)
					{
						bPrimary = (((Double) param2).intValue() != 0);
					}
					result = ((Equipment) parent).getVariableValue((String) param1, "", "", bPrimary);
				}
				else if (parent == null)
				{
					PlayerCharacter aPC = Globals.getCurrentPC();
					if (aPC != null)
					{
						result = aPC.getVariable((String) param1, true, true, "", "");
					}
				}
				if (result == null)
				{
					throw new ParseException("Error retreiving variable:" + (String) param1);
				}
				inStack.push(result);
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}

	//
	// eg. ceil(12.6) --> 13
	//
	private static final class Ceil extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		public Ceil()
		{
			numberOfParameters = 1;
		}

		/**
		 * Runs ceil on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the ceiling of it's value is
		 * pushed back to the top of <code>inStack</code>.
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param = inStack.pop();

			// check whether the argument is of the right type
			if (param instanceof Double)
			{
				// calculate the result
				double r = Math.ceil(((Double) param).doubleValue());
				// push the result on the inStack
				inStack.push(new Double(r));
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}

	//
	// eg. floor(12.6) --> 12
	//
	private static final class Floor extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		public Floor()
		{
			numberOfParameters = 1;
		}

		/**
		 * Runs floor on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the floor of it's value is
		 * pushed back to the top of <code>inStack</code>.
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param = inStack.pop();

			// check whether the argument is of the right type
			if (param instanceof Double)
			{
				// calculate the result
				double r = Math.floor(((Double) param).doubleValue());
				// push the result on the inStack
				inStack.push(new Double(r));
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}

	//
	// eg. max(12.6, 20) --> 20
	//
	private static final class Max extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		public Max()
		{
			numberOfParameters = 2;
		}

		/**
		 * Runs the max operation on the inStack. The parameters are popped
		 * off the <code>inStack</code>, and the max of the two values is
		 * pushed back to the top of <code>inStack</code>.
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param1 = inStack.pop();
			Object param2 = inStack.pop();

			if ((param1 instanceof Double) && (param2 instanceof Double))
			{
				inStack.push(new Double(Math.max(((Double) param1).doubleValue(), ((Double) param2).doubleValue())));
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}

	//
	// eg. min(12.6, 20) --> 12.6
	//
	private static final class Min extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		public Min()
		{
			numberOfParameters = 2;
		}

		/**
		 * Runs the min operation on the inStack. The parameters are popped
		 * off the <code>inStack</code>, and the min of the two values is
		 * pushed back to the top of <code>inStack</code>.
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param1 = inStack.pop();
			Object param2 = inStack.pop();

			if ((param1 instanceof Double) && (param2 instanceof Double))
			{
				inStack.push(new Double(Math.min(((Double) param1).doubleValue(), ((Double) param2).doubleValue())));
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}

}

