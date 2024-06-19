/*
 * Cache.java
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
 * Created on September 08, 2002, 12:15 AM
 */

package pcgen.io;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>Cache</code><br>
 * Convenience wrapper class for a Hashtable containing
 * only List intances filled with String instances.
 *
 * @author Thomas Behr 08-00-02
 * @version $Revision: 1.1 $
 */

class Cache
{
        private Hashtable hashtable;
        
        public Cache()
        {
                hashtable = new Hashtable();
        }
        
        public Cache(int initialCapacity)
        {
                hashtable = new Hashtable(initialCapacity);
        }
        
        public void put(String key, String value)
        {
                if (hashtable.containsKey(key))
                {
                        ((List)hashtable.get(key)).add(value);
                }
                else
                {
                        List values = new ArrayList();
                        values.add(value);
                        hashtable.put(key, values);
                }
        }
        
        public List get(String key)
        {
                return (List)hashtable.get(key);
        }
        
        public boolean containsKey(String key)
        {
                return hashtable.containsKey(key);
        }
}

