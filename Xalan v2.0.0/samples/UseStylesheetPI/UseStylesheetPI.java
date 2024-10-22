// Imported TraX classes
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

// Imported java.io classes
import java.io.FileOutputStream;
import java.io.IOException;	

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
