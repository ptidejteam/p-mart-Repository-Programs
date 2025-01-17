 /*
Copyright (C) 2001  Sten Loecher

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

package tudresden.ocl.sql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tudresden.ocl.codegen.decl.Guide;
import tudresden.ocl.codegen.decl.MappedClass;
import tudresden.ocl.codegen.decl.ORMappingScheme;
import tudresden.ocl.codegen.decl.Table;

/**
 * An implementation of the ORMappingScheme interface. It uses an implementation
 * of the ORMapping interface to query object relational mapping information and
 * provides these information to the SQLCodegenerator with help of the appropriate
 * structures.
 * @author Sten Loecher
 * @see tudresden.ocl.codegen.decl.ORMappingScheme
 * @see tudresden.ocl.codegen.decl.SQLCodeGenerator
 * @see tudresden.ocl.codegen.decl.MappedClass
 * @see ORMapping
 */
public class ORMappingSchemeImp implements ORMappingScheme {

  ORMapping theORM;

  /**
   * Maps classifiers to MappedClass objects.
   * @key-type String
   * @element-type MappedClass
   */
  Map classifiersToMappedClasses;

  /**
   * Call this constructor to create an instance of ORMappingSchemeImp.
   * @param orm an implementation of the ORMapping interface
   * @exception IllegalArgumentException if orm is null
   */
  public ORMappingSchemeImp(ORMapping orm)
  throws IllegalArgumentException {
 	if (orm == null) throw new IllegalArgumentException("Parameter orm must not be null !");

	theORM = orm;
	classifiersToMappedClasses = new HashMap();

	createMappedClasses();
	createMappedClassLinks();
	insertQueryMethodes();
  }

  /**
   * @param name the name of an application type
   * @return a MappedObject containing all necessary information to generate SQL code
   */
  public MappedClass getMappedClass(String name) {
 	return (MappedClass)classifiersToMappedClasses.get(name);
  }

  //-----------------------------------------------------------------------------------------
  /**
   * Creates a MappedClass object for each classifier and fills it with
   * the according tables that already contain the attribute information.
   * As far as associations are mapped to foreign key references with the
   * help of attributes, they are already contained within that tables too.
   */
  private void createMappedClasses() {
  	String classifier;
  	MappedClass mc;

  	for (Iterator i=theORM.classifiers().iterator(); i.hasNext(); ) {
  		classifier = (String)i.next();

  		mc = new MappedClass(classifier);
  		for (Iterator k=theORM.getClassTables(classifier).iterator(); k.hasNext(); ) {
  			mc.addTable((Table)k.next());
  		}
  		classifiersToMappedClasses.put(classifier, mc);
  	}
  }

  /**
   * Creates all necessary links between MappedClass objects. These links
   * result from association ends and generalization relationships.
   * Futhermore, the according guides will be inserted.
   */
  private void createMappedClassLinks() {
  	Set dstn;
  	Map gtae, assEnds;
  	String classifier, temp, assEndName, assEndClassifier;
  	MappedClass mc;
  	List guides;

 	for (Iterator i=classifiersToMappedClasses.keySet().iterator(); i.hasNext(); ) {
  		classifier = (String)i.next();
  		mc = (MappedClass)classifiersToMappedClasses.get(classifier);

  		// links resulting from generalization relationships
  		dstn = theORM.directSupertypeNames(classifier);
  		for (Iterator k=dstn.iterator(); k.hasNext(); ) {
  			mc.addSuperclass(classifier, (MappedClass)classifiersToMappedClasses.get((String)k.next()));
  		}

  		// links resulting from association ends
  		assEnds = theORM.associationEnds(classifier);
  		for (Iterator k=assEnds.keySet().iterator(); k.hasNext(); ) {
  			assEndName = (String)k.next();
  			assEndClassifier = (String)assEnds.get(assEndName);
  			mc.addAssociationEnd(assEndName, (MappedClass)classifiersToMappedClasses.get(assEndClassifier));
  			
  			// add according guides
  			guides = theORM.guidesToAssociationEnds(classifier, assEndName);
  			for (Iterator l=guides.iterator(); l.hasNext(); ) {
  				mc.addJoinGuide(assEndName, (Guide)l.next());
  			}  			
  		}
 	}
   }

  /**
   * Inserts information about query methodes contained by the classes in
   * the class model.
   */
  private void insertQueryMethodes() {
  	String classifier;
  	MappedClass mc;
  	Set ops;

  	for (Iterator i=classifiersToMappedClasses.keySet().iterator(); i.hasNext(); ) {
  		classifier = (String)i.next();
  		mc = (MappedClass)classifiersToMappedClasses.get(classifier);

  		ops = theORM.operations(classifier);
  		for (Iterator k=ops.iterator(); k.hasNext(); ) {
			mc.addQuery((String)k.next());
  		}
  	}
  }

}
