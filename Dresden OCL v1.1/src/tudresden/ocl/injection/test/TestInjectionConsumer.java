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

package tudresden.ocl.injection.test;

import java.io.IOException;
import java.io.Writer;

import tudresden.ocl.injection.InjectionConsumer;
import tudresden.ocl.injection.InjectorParseException;
import tudresden.ocl.injection.JavaAttribute;
import tudresden.ocl.injection.JavaBehaviour;
import tudresden.ocl.injection.JavaClass;
import tudresden.ocl.injection.JavaFeature;
import tudresden.ocl.injection.JavaFile;

public class TestInjectionConsumer implements InjectionConsumer
{
  private Writer output;

  public TestInjectionConsumer(Writer output)
  {
    this.output=output;
  }

  public void onPackage(JavaFile javafile)
    throws InjectorParseException
  {
    try
    {
      output.write("[onPackage("+javafile.getPackageName()+")]");
    }
    catch(IOException e) { System.out.println(e); };
  }

  public void onImport(String importname)
  {
    try
    {
      output.write("[onImport("+importname+")]");
    }
    catch(IOException e) { System.out.println(e); };
  }

  public void onClass(JavaClass cc)
  {
    try
    {
      output.write("[onClass("+cc.getName()+")]");
    }
    catch(IOException e) { System.out.println(e); };
  }

  public void onClassEnd(JavaClass cc)
    throws java.io.IOException, InjectorParseException
  {
    try
    {
      output.write("[onClassEnd("+cc.getName()+")]");
    }
    catch(IOException e) { System.out.println(e); };
  }

  public void onBehaviourHeader(JavaBehaviour jb)
    throws java.io.IOException
  {
    try
    {
      output.write("[onBehaviourHeader]");
      output.write(jb.getLiteral());
      output.write("[/onBehaviourHeader]");
    }
    catch(IOException e) { System.out.println(e); };
  }

  public void onAttributeHeader(JavaAttribute ja)
    throws java.io.IOException
  {
    try
    {
      output.write("[onAttributeHeader]");
    }
    catch(IOException e) { System.out.println(e); };
  }

  public void onClassFeature(JavaFeature cf,String doccomment)
    throws java.io.IOException, InjectorParseException
  {
    try
    {
      output.write("[onClassFeature("+cf.getName()+")");
      if(doccomment!=null)
        output.write("{"+doccomment+"}");
      output.write("]");
    }
    catch(IOException e) { System.out.println(e); };
  }

  public boolean onDocComment(String doccomment)
    throws java.io.IOException
  {
    try
    {
      output.write("[onDocComment]");
      output.write(doccomment);
      output.write("[/onDocComment]");
    }
    catch(IOException e) { System.out.println(e); };
    return true;
  }

  public void onFileDocComment(String doccomment)
    throws java.io.IOException
  {
    try
    {
      output.write("[onFileDocComment]");
      output.write(doccomment);
      output.write("[/onFileDocComment]");
    }
    catch(IOException e) { System.out.println(e); };
  }

  public void onFileEnd()
  {
    try
    {
      output.write("[onFileEnd]");
    }
    catch(IOException e) { System.out.println(e); };
  }

}
