/*
 * EntityEncoder.java
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
 * Created on September 09, 2002, 0:00 AM
 */
package pcgen.io;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * <code>EntityEncoder</code><br>
 * Encodes reserved characters and escape sequences as entities.<br>
 * Decodes entities as reserved characters and escape sequences.
 *
 * @author Thomas Behr
 */
class EntityEncoder 
{
        private final static String ENCODE = "\\\n\r\f:|[]&";
        private final static EntityMap ENTITIES;

        static 
        {
                ENTITIES = new EntityMap();
                ENTITIES.put("\n", "&nl;");
                ENTITIES.put("\r", "&cr;");
                ENTITIES.put("\f", "&lf;");
                ENTITIES.put(":", "&colon;");
                ENTITIES.put("|", "&pipe;");
                ENTITIES.put("[", "&lbracket;");
                ENTITIES.put("]", "&rbracket;");
                ENTITIES.put("&", "&amp;");
        }

	/**
	 * encode characters
	 * "\n" -> "&nl;"
	 * "\r" -> "&cr;"
	 * "\f" -> "&lf;"
         * ":" -> "&colon;"
         * "|" -> "&pipe;"
         * "[" -> "&lbracket;"
         * "]" -> "&rbracket;"
         * "&" -> "&amp;"
	 *
	 * <br>author: Thomas Behr 09-09-02
	 *
	 * @param s   the String to encode
	 * @return the encoded String
	 */
	public static String encode(String s)
	{
		final StringBuffer buffer = new StringBuffer();
		final StringTokenizer tokens = new StringTokenizer(s, ENCODE, true);

                while (tokens.hasMoreTokens())
                {
                        buffer.append(ENTITIES.get(tokens.nextToken()));
                }

                return buffer.toString();
        }

        /**
	 * decode characters
	 * "\n" <- "&nl;"
	 * "\r" <- "&cr;"
	 * "\f" <- "&lf;"
         * ":" <- "&colon;"
         * "|" <- "&pipe;"
         * "[" <- "&lbracket;"
         * "]" <- "&rbracket;"
         * "&" <- "&amp;"
	 *
	 * <br>author: Thomas Behr 09-09-02
	 *
	 * @param s   the String to decode
	 * @return the decoded String
	 */
	public static String decode(String s)
	{
		final StringBuffer buffer = new StringBuffer();
		final StringTokenizer tokens = new StringTokenizer(s, "&;", true);

                String cToken;
                while (tokens.hasMoreTokens()) {
                        cToken = tokens.nextToken();

                        if (cToken.equals("&")) {
                                buffer.append(ENTITIES.get(
                                        cToken + tokens.nextToken() + tokens.nextToken()));
                        }
                        else {
                                buffer.append(cToken);
                        }
                }
                
		return buffer.toString();
	}
}

class EntityMap
{
        private Map map = new HashMap();

        public void put(String key, String value) 
        {
                map.put(key, value);
                map.put(value, key);
        }
        
        public String get(String key) 
        {
                final Object value = map.get(key);
                return (value == null) ? key : (String)value;
        }
}



