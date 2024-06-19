/*
 * DecimalNumberField.java
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
 * Created on January 13, 2003, 2:05 AM
 */
package pcgen.util;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class DecimalNumberField extends JTextField
{

	private Toolkit toolkit;
	private DecimalFormat doubleFormatter;
	private double lastVal = 0.0f;
	private boolean allowSign = false;

	public DecimalNumberField(double value, int columns)
	{
		super(columns);
		toolkit = Toolkit.getDefaultToolkit();
		doubleFormatter = new DecimalFormat();
		doubleFormatter.setParseIntegerOnly(false);
		setValue(value);
		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					lastVal = doubleFormatter.parse(getText()).floatValue();
				}
				catch (ParseException p)
				{
					setText(doubleFormatter.format(lastVal));
					toolkit.beep();
				}
			}
		});
	}

	public void setMaximumFractionDigits(final int newValue)
	{
		doubleFormatter.setMaximumFractionDigits(newValue);
	}

	public void setMaximumIntegerDigits(final int newValue)
	{
		doubleFormatter.setMaximumIntegerDigits(newValue);
	}

	public void allowNegative(final boolean argNeg)
	{
		allowSign = argNeg;
	}

	public double getValue()
	{
		double retVal = 0.0;
		try
		{
			retVal = doubleFormatter.parse(getText()).doubleValue();
			lastVal = retVal;
		}
		catch (ParseException e)
		{
			retVal = lastVal;
			setText(doubleFormatter.format(lastVal));
			toolkit.beep();
		}
		finally
		{
			return retVal;
		}
	}

	public void setValue(double value)
	{
		lastVal = value;
		setText(doubleFormatter.format(value));
	}

	protected Document createDefaultModel()
	{
		return new DecimalNumberDocument();
	}

	protected class DecimalNumberDocument extends PlainDocument
	{

		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
		{
			char[] source = str.toCharArray();
			char[] result = new char[source.length];
			int j = 0;

			String curText = "";
			try
			{
				curText = getText(0, getLength());
			}
			catch(Exception n)
			{
			}
			boolean foundPoint = (curText.indexOf('.') >= 0);
			boolean foundSign =  (curText.indexOf('-') >= 0);

			for (int i = 0; i < result.length; ++i)
			{
				final char ch = source[i];
				if (!allowSign && (ch == '-'))
				{
					toolkit.beep();
				}
				else if ((ch == '-') && ((i + offs) == 0))
				{
					if (!foundSign)
					{
						result[j++] = ch;
					}
					else
					{
						toolkit.beep();
					}
				}
				else
				{
					if (foundSign && ((i + offs) == 0))
					{
						toolkit.beep();
					}
					else if ((ch == '.') && (!foundPoint))
					{
						foundPoint = true;
						result[j++] = ch;
					}
					else
					{
						if (Character.isDigit(ch))
						{
							result[j++] = ch;
						}
						else
						{
							toolkit.beep();
						}
					}
				}
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}

}
