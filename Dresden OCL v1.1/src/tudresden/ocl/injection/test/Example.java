/*
Copyright (C) 2000  Ralf Wiebicke

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

package// hallo
  tudresden.ocl.injection.test;

import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
   Represents an attribute or association partner of a class.
   Note: type==Model.AMIGOUS means, the attribute cannot be used in OCL due to attribute ambiguities.
   See OCL spec 5.4.1. for details.
*/
public abstract class Example implements Runnable
{
  private String name;
  private Integer type=new Integer(5);
  private volatile Integer[] qualifiers;
  String hallo="hallo";
  
  /**TestCommentCommaSeparated123*/
  int commaSeparated1,commaSeparated2=0,commaSeparated3; 
  /**TestCommentCommaSeparated456*/
  int commaSeparated4=80,commaSeparated5,commaSeparated6=200; 

  // these attributes test the ability of the parser
  // to skip more complex (ugly) attribute initializers
  String   uglyAttribute1="some'Thing{some\"Thing;Else";
  char     uglyAttribute2=';';
  char     uglyAttribute3='{';
  char     uglyAttribute4='"';
  char     uglyAttribute5='\'';
  String[] uglyAttribute6=
  {
    "some'Thing{some\"Thing;Else", // ugly ; { " ' comment
    "some'Thing{some\"Thing;Else"
  };
  char[]   uglyAttribute7={';','{','"','\''};
  Runnable uglyAttribute8=new Runnable()
  {
    // ugly ; { " ' comment
    String   uglyInnerAttribute1="some'Thing{some\"Thing;Else";
    char     uglyInnerAttribute2=';';
    char     uglyInnerAttribute3='{';
    char     uglyInnerAttribute4='"';
    char     uglyInnerAttribute5='\'';
    String[] uglyInnerAttribute6=
    {
      "some'Thing{some\"Thing;Else", // ugly ; { " ' comment
      "some'Thing{some\"Thing;Else"
    };
    char[]   uglyInnerAttribute7={';','{','"','\''};
    public void run()
    {
      // ugly ; { " ' comment
      String   uglyVariable1="some'Thing{some\"Thing;Else";
      char     uglyVariable2=';';
      char     uglyVariable3='{';
      char     uglyVariable4='"';
      char     uglyVariable5='\'';
      String[] uglyVariable6=
      {
        "some'Thing{some\"Thing;Else", // ugly ; { " ' comment
        "some'Thing{some\"Thing;Else"
      };
      char[]   uglyAttribute7={';','{','"','\''};
    }
    // ugly ; { " ' comment
  };
  // end of ugly attributes
  

  class Inner implements Runnable
  {
    class Drinner implements Runnable
    {
      boolean someDrinnerBoolean=true;
    
      public void run()
      {
      }
    }

    boolean someInnerBoolean=true;
    
    public void run()
    {
    }
  }  

  /**
    * Testcomment A
    */
  private int a;
  
  private List collectionWithoutComment;
  
  /**
    * Testcomment B
    */
  private int b;
  
  private Map mapWithoutComment;

  private Example()
  {
    namedIntegers.put("5", new Integer(5));
  }
  
  public Example(String name, Integer type)
  {
    super();
    qualifiers=new Integer[6];
    namedIntegers.put("5", new Integer(5));
  }

  public void set(String name, Integer type,// what a cool parameter
    final Integer[] qualifiers)
  {
    if(name==null)
      throw new IllegalArgumentException(); // ugly comment : { {
    this.name=name;
    String x="ugly { string \" { literal";
    char c='{';

    /**
      ugly comment *
    **/
    if(type==null)
      throw new IllegalArgumentException(); // some other comment
    this.type=type;

    if(qualifiers!=null&&qualifiers.length==0)
      throw new IllegalArgumentException();
    this.qualifiers=qualifiers;
    
    int a=20;
    a=a/(a+b); // ugly expression
  }

  abstract void abstractMethod();

  /**
     Some example doc-comment.
  */
  public void run()
  {}

  /**
     A collection of Strings.
     @element-type java.lang.String
     @see java.lang.String
     @invariant stringsMinusGreaterincludes_inlinehallo_:        strings->includes('inlinehallo')
     @invariant stringsMinusGreaterincludes_inline_space_hallo_: strings->includes('inline space hallo')
  */
  Set myStrings=new HashSet();
  
  /**
     @element-type Integer
  */
  Set integers=new HashSet();
  
  Integer anInteger=new Integer(5);
  
  /**
    * A collection of dates.
    *
    * Here are some lines to test the reveng GUI....
    *
     @element-type Date
  */
  List dates=new ArrayList();
  
  Date aDate=new Date();
  
  /**
     @element-type AbstractImplementation
  */
  Set interfaces=new HashSet();
  
  AbstractImplementation anInterface=new Implementation();
  
  /**
     @element-type Format
  */
  Vector formats=new Vector();
  
  Format aFormat=new java.text.DecimalFormat();

  /**
  *
  * A map from strings to integers.
  * * * * *  These stars should be removed by the reveng GUI...
  
  *
     @element-type Integer
     @key-type String
  */
  HashMap namedIntegers=new HashMap();
  
  public boolean poly1(Interface someInterface)
  {
    return true;
  }

  public String getName()
  {
    return name;
  }

  public Integer getType()
  {
    return type;
  }

  /**
     @precondition  stringsMinusGreaterincludes_inlinehallopre_:          strings->includes('inlinehallopre')
     @precondition  stringsMinusGreaterincludes_inline_space_hallo_pre_:  strings->includes('inline space hallo pre')
     @postcondition stringsMinusGreaterincludes_inlinehallopost_:         strings->includes('inlinehallopost')
     @postcondition stringsMinusGreaterincludes_inline_space_hallo_post_: strings->includes('inline space hallo post')
  */
  public Integer[] getQualifiers()
  {
    namedIntegers.put("10", new Integer(10));
    return qualifiers;
  }

  public Integer unqualifiedType=null;

  public Integer getUnqualifiedType() throws IllegalArgumentException
  {
    if(unqualifiedType!=null)
      return unqualifiedType;

    if(qualifiers==null)
      throw new IllegalArgumentException();

    unqualifiedType=
      (type instanceof Integer) ? type : type;
    return unqualifiedType;
  }

  private Object parent;

  public void setParent  (Object parent)
    throws
      IllegalArgumentException,
      NullPointerException
  {
    if(this.parent==null)
      this.parent=parent;
    else
      throw new IllegalArgumentException("An attributes parent cannot be set twice.");
  }

  public Object getParent()
  {
    return parent;
  }

  public void printData
    (java.io.PrintStream o)
  {
  }
  
  private   void accessifierPrivate() {}
  protected void accessifierProtected() {}
            void accessifierPackage() {}
  public    void accessifierPublic() {}
  
  void test_super()
  {
  }
  
  Collection testTypeTrace=new HashSet();
  public void addTestTypeTrace(Object o)
  {
    testTypeTrace.add(o);
  }
	
	/**
		Tests null strings to be treated as empty strings.
		@invariant nullString: nullString=''
	*/
	String nullString=null;

	/**
		Tests null collections to be treated as empty collections.
		@element-type String
	*/
	Collection nullCollection=null;

	/**
		Tests null sets to be treated as empty sets.
		@invariant nullSet: nullSet->size=0
		@element-type String
	*/
	Set nullSet=null;

	/**
		Tests null lists to be treated as empty lists.
		@invariant nullList: nullList->size=0
		@element-type String
	*/
	List nullList=null;

	/**
		Tests null maps to be treated as empty maps.
		@element-type String
		@key-type String
	*/
	Map nullMap=null;

	static public void main(String[] args)
  {
    tudresden.ocl.lib.Ocl.TOLERATE_NONEXISTENT_FIELDS=false;
    tudresden.ocl.lib.Ocl.setNameAdapter(new tudresden.ocl.lib.ArgoNameAdapter());
    SecondExample e2=new SecondExample();
    e2.getQualifiers();
    e2.i=10;
    e2.anInteger=new Integer(8);
    e2.getQualifiers();
    e2.test_super();
    e2.addTestTypeTrace(new ThirdExample());
    e2.addTestTypeTrace(new SecondExample());
  }

}

class SecondExample extends Example{
  int i;
  
  /**
     @invariant testingInheritance: self.hallo='prollo'
  */
  SecondExample()
  {
    super("somename", new Integer(5));
  }
  
  {
    // Object initializer as defined in Java Language Spec D.1.3
    i=10;
  }

  void abstractMethod()  {}
  
  /**
     Tests, whether injection does not produce infinite loops,
     when wrapping methods with calls to super.
  */
  void test_super()
  {
    super.test_super();
  }
  
  static
  {
    // this has to be tested too.
  }
  
  public String toString()
  {
    return getClass().getName();
  }
  
}

class ThirdExample extends SecondExample
{
  // this class has no explicit constructor
  void abstractMethod()  {}
}

interface ExampleFour
{
  public int someMethod(double x);
}
