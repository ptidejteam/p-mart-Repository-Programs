/*
 * IOConstants.java
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
 * Created on March 19, 2002, 5:15 PM
 */
package pcgen.io;

/**
 * <code>IOConstants</code>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

interface IOConstants
{

        // System information
        public static final String TAG_CAMPAIGNS            = "CAMPAIGNS";
        public static final String TAG_VERSION              = "VERSION";
        public static final String TAG_ROLLMETHOD           = "ROLLMETHOD";
        public static final String TAG_PURCHASEPOINTS       = "PURCHASEPOINTS";
        public static final String TAG_UNLIMITEDPOOLCHECKED = "UNLIMITEDPOOLCHECKED";
        public static final String TAG_POOLPOINTS           = "POOLPOINTS";
        public static final String TAG_GAMEMODE             = "GAMEMODE";

        // Character bio
        public static final String TAG_CHARACTERNAME     = "CHARACTERNAME";
        public static final String TAG_PLAYERNAME        = "PLAYERNAME";
        public static final String TAG_HEIGHT            = "HEIGHT";
        public static final String TAG_WEIGHT            = "WEIGHT";
        public static final String TAG_AGE               = "AGE";
        public static final String TAG_GENDER            = "GENDER";
        public static final String TAG_HANDED            = "HANDED";
        public static final String TAG_SKINCOLOR         = "SKINCOLOR";
        public static final String TAG_EYECOLOR          = "EYECOLOR";
        public static final String TAG_HAIRCOLOR         = "HAIRCOLOR";
        public static final String TAG_HAIRSTYLE         = "HAIRSTYLE";
        public static final String TAG_LOCATION          = "LOCATION";
        public static final String TAG_REGION            = "REGION";
        public static final String TAG_PERSONALITYTRAIT1 = "PERSONALITYTRAIT1";
        public static final String TAG_PERSONALITYTRAIT2 = "PERSONALITYTRAIT2";
        public static final String TAG_SPEECHPATTERN     = "SPEECHPATTERN";
        public static final String TAG_PHOBIAS           = "PHOBIAS";
        public static final String TAG_INTERESTS         = "INTERESTS";
        public static final String TAG_CATCHPHRASE       = "CATCHPHRASE";

        // Character attributes
        public static final String TAG_STAT      = "STAT";
        public static final String TAG_ALIGNMENT = "ALIGN";
        public static final String TAG_RACE      = "RACE";

        // Character class(es)
        public static final String TAG_CLASS               = "CLASS";
        public static final String TAG_LEVEL               = "LEVEL";
        public static final String TAG_CLASSABILITIESLEVEL = "CLASSABILITIESLEVEL";

        // Character templates
        public static final String TAG_TEMPLATESAPPLIED = "TEMPLATESAPPLIED";

        // Character skills
        public static final String TAG_CLASSBOUGHT = "CLASSBOUGHT";
        public static final String TAG_SKILL       = "SKILL";
        public static final String TAG_CROSSCLASS  = "CROSSCLASS";
        public static final String TAG_RANK        = "RANK";
        public static final String TAG_COST        = "COST";
        public static final String TAG_SYNERGY     = "SYNERGY";

        // Character feats
        public static final String TAG_FEAT = "FEAT";
        public static final String TAG_TYPE = "TYPE";
        public static final String TAG_DESC = "DESC";

        // Character equipment
        public static final String TAG_EQUIPNAME = "EQUIPNAME";
        public static final String TAG_QUANTITY  = "QUANTITY";
        public static final String TAG_CARRIED   = "CARRIED";
        public static final String TAG_EQUIPPED  = "EQUIPPED";
        public static final String TAG_CONTAINS  = "CONTAINS";
        public static final String TAG_TOTALWT   = "TOTALWT";
        public static final String TAG_WT        = "WT";
        public static final String TAG_MISC      = "MISC";
        public static final String TAG_DATA      = "DATA";

        // Character deity/domain
        public static final String TAG_DEITY          = "DEITY";
        public static final String TAG_DEITYDOMAINS   = "DEITYDOMAINS";
        public static final String TAG_ALIGNALLOW     = "ALIGNALLOW";
        public static final String TAG_HOLYITEM       = "HOLYITEM";
        public static final String TAG_SYMBOL         = "SYMBOL";
        public static final String TAG_DEITYFAVWEAP   = "DEITYFAVWEAP";
        public static final String TAG_DEITYALIGN     = "DEITYALIGN";
        public static final String TAG_DOMAIN         = "DOMAIN";
        public static final String TAG_DOMAINFEATS    = "DOMAINFEATS";
        public static final String TAG_DOMAINGRANTS   = "DOMAINGRANTS";
        public static final String TAG_DOMAINSKILLS   = "DOMAINSKILLS";
        public static final String TAG_DOMAINSPECIALS = "DOMAINSPECIALS";
        public static final String TAG_DOMAINSPELLS   = "DOMAINSPELLS";

        // Character spells information
        public static final String TAG_CANCASTPERDAY = "CANCASTPERDAY";
        public static final String TAG_SPELLNAME = "SPELLNAME";
        public static final String TAG_POWERNAME = "POWERNAME";
        public static final String TAG_SCHOOL = "SCHOOL";
        public static final String TAG_SUBSCHOOL = "SUBSCHOOL";
        public static final String TAG_COMP = "COMP";
        public static final String TAG_CT = "CT";
        public static final String TAG_RANGE = "RANGE";
        public static final String TAG_EFFECT = "EFFECT";
        public static final String TAG_EFFECTTYPE = "EFFECTTYPE";
        public static final String TAG_DURATION = "DURATION";
        public static final String TAG_SAVE = "SAVE";
        public static final String TAG_SR = "SR";
        public static final String TAG_DESCRIPTOR = "DESCRIPTOR";

        // Character description/bio/history
        public static final String TAG_CHARACTERBIO = "CHARACTERBIO";
        public static final String TAG_CHARACTERDESC = "CHARACTERDESC";
}
