/*
Copyright (C) 2000  Steffen Zschaler

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*
 * CollectionTreeNode.java
 *
 * Created on 16. August 2000, 15:48
 */
 
package tudresden.ocl.injection.reverseeng;

import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.DefaultTreeModel;

import tudresden.ocl.injection.reverseeng.propertypages.DefaultPropertyPage;

/** 
  *
  * @author  sz9 (Steffen Zschaler)
  * @version 0.1
  */
public class CollectionTreeNode extends AbstractFeatureTreeNode {
  
  static Icon s_iOK = new javax.swing.ImageIcon (CollectionTreeNode.class.getResource ("resources/collection.gif"));
  static Icon s_iInCompl = new javax.swing.ImageIcon (CollectionTreeNode.class.getResource ("resources/collectionInCompl.gif"));
  
  public CollectionTreeNode (DefaultTreeModel dtmModel, CollectionDescriptor cd) {
    super (dtmModel, cd);
  }

  public Icon getIcon (boolean fExpanded) {
    if (getDescriptor().isIncomplete()) {
      return s_iInCompl;
    }
    else {
      return s_iOK;
    }
  }

  public CollectionDescriptor getDescriptor() {
    return (CollectionDescriptor) getUserObject();
  }

  public List getPropertyPages() {
    List lppReturn = super.getPropertyPages();
    lppReturn.add (new DefaultPropertyPage ("Element type", new TypeEditPage (getDescriptor(), new TypeEditPage.TypeDescriptor() {
      public void onTypeSelectionChanged (String sNewSelection) {
        getDescriptor().setElementType (sNewSelection);
      }
      
      public List getProposedTypes() {
        return getDescriptor().getProposedElementTypes();
      }
      
      public String getCurrentType() {
        return getDescriptor().getElementType();
      }
    })));
    
    return lppReturn;
  }

  public String toString () {      
    CollectionDescriptor cd = getDescriptor();

    return cd.getDisplayName (true) + 
            ((cd.getElementType() != null)?(" : " + cd.getElementType()):(""));
  }
  
  public String getToolTip() {
    return "Collection: " + super.getToolTip();
  }
}