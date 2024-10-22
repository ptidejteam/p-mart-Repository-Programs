/*
 * Created on 24 sept. 2004
 * Created by Olivier Chalouhi
 * 
 * Copyright (C) 2004 Aelitis SARL, All rights Reserved
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * AELITIS, SARL au capital de 30,000 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package org.gudy.azureus2.core3.config.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gudy.azureus2.core3.config.StringIterator;
import org.gudy.azureus2.core3.config.StringList;

/**
 * @author Olivier Chalouhi
 *
 */
public class StringListImpl implements StringList {

	List list;
	
	public StringListImpl() {
		this.list = new ArrayList();
	}
	
	public StringListImpl(StringListImpl _list) {
		list = new ArrayList(_list.getList());
	}
	
	/*
	 * package accessor to load / save the list.
	 */	
	StringListImpl(List _list) {
		//Atempt to convert list to String List
		this();		
		Iterator iter = _list.iterator();
		while(iter.hasNext()) {
			Object obj = iter.next();
			if(obj instanceof String) {
				list.add(obj);
			} else if(obj instanceof byte[]) {
				list.add(new String((byte[]) obj));
			} else if(obj instanceof Object) {
				list.add(obj.toString());
			}
		}		
	}
	
	List getList() {
		return list;
	}
	
	// -----------------------------------------
	
	public int size() {
		return list.size();
	}

	public String get(int i) {
		return (String) list.get(i);
	}

	public void add(String str) {
		list.add(str);
	}
	
	public void add(int index,String str) {
		list.add(index,str);
	}

	public StringIterator iterator() {
		return new StringIteratorImpl(list.iterator());
	}
	
	public int indexOf(String str) {
		return list.indexOf(str);
	}
	
	public boolean contains(String str) {
		return list.contains(str);
	}

}
