/**
 * Tidy_DOM_2_20001113_DocumentAdapter - DocumentAdapter for Tidy DOM version 2 11/13/2000
 *
 * Copyright (c) 2002
 *      Marty Phelan, All rights reserved.
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.dom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tidy_DOM_2_20001113_DocumentAdapter is DocumentAdapter for Tidy DOM version 2 11/13/2000
 * @author marty
 */
public class Tidy_DOM_2_20001113_DocumentAdapter extends
    DOM_2_20001113_DocumentAdapter {

  /**
   * Constructs a new Tidy_DOM_2_20001113_DocumentAdapter for given document.
   * Also maps all the identifiers for this document
   * @param newDoc document managed by this adapter
   */
  public Tidy_DOM_2_20001113_DocumentAdapter(Object newDoc) {
    super(newDoc);
  }

  /**
   * Import the contents from the given sourceAdapter at the sourceId and
   * replace the contents of the destId in this DocumentAdapter. All the
   * child contents of the sourceId are copied (not including the element
   * with the sourceId). The id's are then re-mapped for this document.
   * 
   * @param sourceAdapter the source DocumentAdapter for the import
   * @param sourceId the parent element to import contents from
   * @param destId the parent element to import the contents to
   * 
   * @throws IllegalArgumentException if any arguments are null
   * @throws DocumentAdapterException if sourceId or destId is invalid
   */
  public void importContents(DocumentAdapter sourceAdapter, 
      String sourceId, String destId) throws DocumentAdapterException {
    if (sourceAdapter == null) {
      throw new IllegalArgumentException("Source DocumentAdapter is null");
    }
    Element sourceElement = sourceAdapter.getElementById(sourceId);
    if (sourceElement == null) {
      throw new DocumentAdapterException(
          "Element for sourceId=" + sourceId + 
          " does not exist in source document");
    }
    Element destElement = getElementById(destId);
    if (destElement == null) {
      throw new DocumentAdapterException(
          "Element for destId=" + destId + 
          " does not exist in destination document");
    }
    // remove all children from destId
    Node child;
    while ((child = destElement.getFirstChild()) != null) {
      destElement.removeChild(child);
    }
    // Append child branch to destination element
    destElement.appendChild(sourceElement);
    // re-map all identifiers
    getIdentifierMap().clear();
    mapIdentifiers(getDocument());
  }

}
