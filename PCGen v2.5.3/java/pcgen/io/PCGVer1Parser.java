/*
 * PCGVer1Parser.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on March 22, 2002, 12:15 AM
 */

package pcgen.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import pcgen.core.Campaign;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.Spell;

/**
 * <code>PCGVer1Parser</code><br>
 * @author Thomas Behr 22-03-02
 * @version $Revision: 1.1 $
 */

class PCGVer1Parser implements IOConstants
{
        private List warnings = new ArrayList();
        private PlayerCharacter aPC;

        /**
         * Constructor
         */
        public PCGVer1Parser(PlayerCharacter aPC)
        {
                this.aPC = aPC;
        }

        /**
         * Selector
         *
         * <br>author: Thomas Behr 22-03-02
         *
         * @return a list of warning messages
         */
        public List getWarnings()
        {
                return warnings;
        }

        /**
         * parse a String in PCG format
         *
         * <br>author: Thomas Behr 22-03-02
         *
         * @param s   the String to parse
         */
	public void parsePCG(String s) throws PCGParseException
	{
                // TODO                
        }
        
        /**
         * decode characters
         * "\\" <- "\\\\"
         * "\n" <- "\\n"
         * "\r" <- "\\r"
         * "\f" <- "\\f"
         *
         * <br>author: Thomas Behr 19-03-02
         *
         * @param s   the String to decode
         * @return the decoded String
         */
        private String decodeChars(String s) 
        {
                // TODO
                return s;
        }
}
