/*
 * File    : ItemEnumerator.java
 * Created : 24 nov. 2003
 * By      : Olivier
 *
 * Azureus - a Java Bittorrent client
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
 */
 
package org.gudy.azureus2.ui.swt.views.tableitems.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Olivier
 *
 */
public class ItemEnumerator {

  private List items;
  private Map lookUp;
  
  protected ItemEnumerator(ItemDescriptor[] items) {
    this.items = new ArrayList(items.length);
    lookUp = new HashMap();
    for(int i = 0 ; i < items.length ; i++) {
      this.lookUp.put(items[i].getName(),items[i]);
      this.items.add(items[i]);
    }
    insureIntegrity();
  }
  
  public ItemDescriptor[] getItems() {
    return (ItemDescriptor[]) this.items.toArray(new ItemDescriptor[items.size()]);
  }
  
  public void addItemDescriptor(ItemDescriptor item) {
    this.items.add(item);
    this.lookUp.put(item.getName(),items);
    insureIntegrity();
  }
  
  public void setPositionByName(String name,int position) {    
      ItemDescriptor item = (ItemDescriptor) lookUp.get(name);
      if(item != null)
        item.setPosition(position);    
    }
  
  public int getPositionByName(String name) {    
    ItemDescriptor item = (ItemDescriptor) lookUp.get(name);
    if(item != null)
      return item.getPosition();    
    //In case the name isn't found
    return -1;
  }
  
  public int getWidthByName(String name) {    
    ItemDescriptor item = (ItemDescriptor) lookUp.get(name);
    if(item != null)
      return item.getWidth();    
    //In case the name isn't found
    return 0;
  }
  
  public int getTypeByName(String name) {    
    ItemDescriptor item = (ItemDescriptor) lookUp.get(name);
    if(item != null)
      return item.getType();    
    //In case the name isn't found
    return 0;
  }
  
  private void insureIntegrity() {
    int nbColumns = 0;
    Iterator iter = items.iterator();
    while(iter.hasNext()) {
      ItemDescriptor item = (ItemDescriptor) iter.next();
      if(item.getPosition() != -1)
        nbColumns++;
    }
    iter = items.iterator();
    while(iter.hasNext()) {
      ItemDescriptor item = (ItemDescriptor) iter.next();
      if(item.getPosition() >= nbColumns) {
        item.setPosition(nbColumns - 1);
      }        
    }
  }

}
