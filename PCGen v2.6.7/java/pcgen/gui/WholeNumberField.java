/*
 * WholeNumberField.java
 * Copyright 2001 (C) Mario Bonassin <zebuleon@peoplepc.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui;

import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


/**
 * <code>WholeNumberField</code> .
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class WholeNumberField extends JTextField implements java.io.Serializable
{
	private Toolkit toolkit;
	private NumberFormat integerFormatter;

	public WholeNumberField()
	{
		super(0);
		toolkit = Toolkit.getDefaultToolkit();
		integerFormatter = NumberFormat.getNumberInstance(Locale.US);
		integerFormatter.setParseIntegerOnly(true);
		setValue(0);
	}

	public WholeNumberField(int value, int columns)
	{
		super(columns);
		toolkit = Toolkit.getDefaultToolkit();
		integerFormatter = NumberFormat.getNumberInstance(Locale.US);
		integerFormatter.setParseIntegerOnly(true);
		setValue(value);
	}

	public int getValue()
	{
		int retVal = 0;
		try
		{
			retVal = integerFormatter.parse(getText()).intValue();
		}
		catch (ParseException e)
		{
			// This should never happen because insertString allows
			// only properly formatted data to get in the field.
			toolkit.beep();
		}
		return retVal;
	}

	public void setValue(int value)
	{
		setText(java.lang.Integer.toString(value));
	}

	protected Document createDefaultModel()
	{
		return new WholeNumberDocument();
	}

	protected class WholeNumberDocument extends PlainDocument
	{
		public void insertString(int offs,
			String str,
			AttributeSet a)
			throws BadLocationException
		{
			final char[] source = str.toCharArray();
			char[] result = new char[source.length];
			int j = 0;

			for (int i = 0; i < result.length; i++)
			{
				if ((i == 0) && (source[i] == '-'))
				{
					result[j++] = source[i];
				}
				else if (Character.isDigit(source[i]))
				{
					result[j++] = source[i];
				}
				else
				{
					toolkit.beep();
					System.out.println("insertString: " + source[i] + " in " + str);
				}
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}
}
