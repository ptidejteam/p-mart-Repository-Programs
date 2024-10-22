/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: UseStylesheetPI.java,v 1.1 2006/03/09 00:08:07 vauchers Exp $
 */

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;	

public class UseStylesheetPI
{
  public static void main(String[] args)
	  throws TransformerException, TransformerConfigurationException
	{
	  String media= null , title = null, charset = null;
	  try
	  {	
    	TransformerFactory tFactory = TransformerFactory.newInstance();
      Source stylesheet = tFactory.getAssociatedStylesheet
        (new StreamSource("fooX.xml"),media, title, charset);
      
      Transformer transformer = tFactory.newTransformer(stylesheet);
        
		   transformer.transform(new StreamSource("fooX.xml"), 
                             new StreamResult(new java.io.FileOutputStream("foo.out")));
       
      System.out.println("************* The result is in foo.out *************");
       
	  }
  	  catch (Exception e)
	  {
	    e.printStackTrace();
	  }
  }
}
