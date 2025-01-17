/*
 * Exception.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on February 27, 2002, 7:27 PM
 */

package pcgen.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class Exception extends java.lang.Exception
{
	private Throwable rootCause;

	/**
	 * Creates a new instance of <code>Exception</code> without detail message.
	 */
	public Exception()
	{
		super();
	}

	/**
	 * Constructs an instance of <code>Exception</code> with the specified detail message.
	 * @param msg the detail message.
	 */
	public Exception(String msg)
	{
		super(msg);
	}

	/**
	 * Constructs an instance of <code>Exception</code> with the specified {@link Throwable rootCause}.
	 * @param rootCause the root cause of the exception.
	 */
	public Exception(Throwable rootCause)
	{
		super();
		this.rootCause = rootCause;
	}

	/**
	 * Constructs an instance of <code>Exception</code> with the specified {@link Throwable rootCause}
	 * and the specified detail message.
	 * @param rootCause the root cause of the exception.
	 * @param msg the detail message.
	 */
	public Exception(Throwable rootCause, String msg)
	{
		super(msg);
		this.rootCause = rootCause;
	}

	/**
	 * Return the root cause of this exception.
	 */
	public Throwable getRootCause()
	{
		return rootCause;
	}

	/**
	 * Print the stack trace.  {@link java.lang.Exception#printStackTrace()} does not call
	 * {@link #printStackTrace(PrintStream)} by default, and this method has
	 * been overridden to print the root cause of the exception.
	 */
	public void printStackTrace()
	{
		printStackTrace(System.err);
	}

	/**
	 * Print the stack trace.  If there is a root cause print the root cause of
	 * the exception.
	 * @param ps The {@link PrintStream print stream} to print the stack trace to
	 */
	public void printStackTrace(PrintStream ps)
	{
		synchronized(ps)
		{
			if (rootCause != null)
			{
				ps.println(this);               // print this exception's name and message
				rootCause.printStackTrace(ps);  // print the root cause's stack trace
			}
			else
			{
				super.printStackTrace(ps);      // print this exception's stack trace
			}
		}
	}

	/**
	 * Print the stack trace.  If there is a root cause print the root cause of
	 * the exception.
	 * @param pw The {@link PrintWriter print writer} to print the stack trace to
	 */
	public void printStackTrace(PrintWriter pw)
	{
		synchronized(pw)
		{
			if (rootCause != null)
			{
				pw.println(this);               // print this exception's name and message
				rootCause.printStackTrace(pw);  // print the root cause's stack trace
			}
			else
			{
				super.printStackTrace(pw);      // print this exception's stack trace
			}
		}
	}
}
