/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * OCL Compiler                                                      *
 * Copyright (C) 1999, 2000 Frank Finger (frank@finger.org).         *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a diploma project at the Chair for Software *
 * Technology, Dresden University Of Technology, Germany             *
 * (http://www-st.inf.tu-dresden.de).  It is understood that any     *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other projects, please visit the web site:       *
 * http://www-st.inf.tu-dresden.de/ (Chair home page) or             *
 * http://www-st.inf.tu-dresden.de/ocl/ (project home page)          *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
// FILE: d:/java/classes/de/tudresden/ocl/OclMultiplyable.java

package tudresden.ocl.lib;

/** A interface declaring operations expected of classes that have the
 *  <code>*</code> and <code>/</code> operators defined.
 *
 * @see OclReal
 * @author Frank Finger
 */
public interface OclMultiplyable extends OclRoot {

  /** This operation corresponds to the <code>*</code> operator.
   *
   *  @throws OclClassCastException if the type of the parameter prevents it
   *          from being multiplied with this object; this should never happen
   *          as long as only one class implements this interface
   */
  public OclMultiplyable multiply(OclMultiplyable m);

  /** This operation corresponds to the <code>/</code> operator.
   *
   *  @throws OclClassCastException if the type of the parameter prevents this
   *          object from being divided by it; this should never happen
   *          as long as only one class implements this interface
   */
  public OclMultiplyable divide(OclMultiplyable m);

} /* end interface OclMultiplyable */

