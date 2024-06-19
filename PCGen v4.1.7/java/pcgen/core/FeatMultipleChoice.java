/*
 * FeatMultipleChoice.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on October 24, 2002, 12:35 AM
 *
 * $Id: FeatMultipleChoice.java,v 1.1 2006/02/21 00:57:42 vauchers Exp $
 */

package pcgen.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

public final class FeatMultipleChoice implements Serializable
{

	private int maxChoices = 0;
	private ArrayList choices = null;
	private static final long serialVersionUID = 1;

	public FeatMultipleChoice()
	{
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer(50);
		sb.append(maxChoices).append(':');
		if (choices != null)
		{
			sb.append(choices.size());
			for (int i = 0; i < choices.size(); ++i)
			{
				sb.append(':').append(choices.get(i).toString());
			}
		}
		else
		{
			sb.append('0');
		}
		return sb.toString();
	}

	public void setMaxChoices(int argMaxChoices)
	{
		maxChoices = argMaxChoices;
	}

	int getMaxChoices()
	{
		return maxChoices;
	}

	public void addChoice(String aChoice)
	{
		if (choices == null)
		{
			choices = new ArrayList();
		}
		choices.add(aChoice);
	}

	ArrayList getChoices()
	{
		return choices;
	}

	String getChoice(int idx)
	{
		if ((choices != null) && (idx < choices.size()))
		{
			return (String) choices.get(idx);
		}
		return "";
	}

	int getChoiceCount()
	{
		if (choices != null)
		{
			return choices.size();
		}
		return 0;
	}

	private static void readObject(ObjectInputStream in)  // FIXED
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}

}
