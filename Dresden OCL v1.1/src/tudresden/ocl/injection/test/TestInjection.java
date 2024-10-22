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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import tudresden.ocl.injection.Injector;
import tudresden.ocl.test.Diff;
import tudresden.ocl.test.DiffSource;

public class TestInjection extends TestCase
{
	
	public static final String TEMP_DIR = "tudresden.ocl.test.tempdir";
	
  Reader input=null;
  Writer output=null;
  File outputfile;

  TestInjection(String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    super.setUp();
    String  inputfile=TestInjection.class.getResource("Example.java").getFile();
		outputfile = new File(new File(System.getProperty(TEMP_DIR)), "TestInjectionConsumer.result");
    input =new InputStreamReader (new  FileInputStream(inputfile));
    output=new OutputStreamWriter(new FileOutputStream(outputfile));
  }

  protected void tearDown() throws Exception
  {
    if(input!=null)  { input.close(); input=null; }
    if(output!=null) { output.close(); output=null; }
    super.tearDown();
  }


  public void testInjection() throws Exception
  {
    (new Injector(input, output, new TestInjectionConsumer(output))).parseFile();
    input.close();  input=null;
    output.close(); output=null;

    String expected=TestInjection.class.getResource("TestInjectionConsumer.result").getFile();
    Diff.diff(new DiffSource(new File(expected)), new DiffSource(outputfile));
  }

  public static Test suite()
  {
    TestSuite suite=new TestSuite();
    suite.addTest(new TestInjection("testInjection"));
    suite.addTest(new TestExtractDocParagraphs("testIt"));
    return suite;
  }

}

