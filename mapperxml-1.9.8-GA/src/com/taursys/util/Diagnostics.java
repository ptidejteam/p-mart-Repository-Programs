/**
 * Diagnostics - Provides various diagnostic methods
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
package com.taursys.util;

import java.io.OutputStream;

import com.taursys.xml.Form;

/**
 * Diagnostics provides various diagnostic methods
 * @author marty
 */
public class Diagnostics {

  public static void printForm(Form form) {
    printForm(form, System.out);
  }
  
  public static void printForm(Form form, OutputStream os) {
    if (form != null) {
      if (form.getDocumentAdapter() != null) {
        if (form.getDocument() != null) {
          form.getDocumentAdapter().write(os);
        } else {
          System.out.println("Warning !!! - Form.document is null");
        }
      } else {
        System.out.println("Warning !!! - Form.documentAdapter is null");
      }
    } else {
      System.out.println("Warning !!! - Form is null");
    }
  }
}
