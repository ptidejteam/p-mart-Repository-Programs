/*
 * BootTest.java
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
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

package test.pcgen.xml;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

import pcgen.xml.Boot;

/**
 * JUnit 3.6 testcases for <code>pcgen.xml.Boot</code>.
 *
 * @author B. K. Oxley (binkley) <binkley@bigfoot.com>
 * @version $Revision: 1.1 $
 */
public class BootTest
    extends TestCase
{
    public BootTest (String name)
    {
	super (name);
    }

    public static void main (String[] args)
    {
	junit.textui.TestRunner.run (BootTest.class);
    }

    public void testDocuments ( )
    {
      /**
	assert Boot.documents != null);
       */
    }

    public void testMasterDocument ( )
    {
			/*
	assert (Boot.masterDocument != null);
	assert (Boot.masterDocument instanceof org.w3c.dom.Document);
			*/
    }
}
