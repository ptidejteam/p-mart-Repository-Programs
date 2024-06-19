package org.apache.html.dom;


import org.w3c.dom.*;
import org.w3c.dom.html.*;


/**
 * @version $Revision: 1.1 $ $Date: 2006/02/02 02:35:48 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLBaseFontElement
 * @see ElementImpl
 */
public final class HTMLBaseFontElementImpl
    extends HTMLElementImpl
    implements HTMLBaseFontElement
{
    
    
    public String getColor()
    {
        return capitalize( getAttribute( "color" ) );
    }
    
    
    public void setColor( String color )
    {
        setAttribute( "color", color );
    }
    
    
    public String getFace()
    {
        return capitalize( getAttribute( "face" ) );
    }
    
    
    public void setFace( String face )
    {
        setAttribute( "face", face );
    }
    
    
    public String getSize()
    {
        return getAttribute( "size" );
    }
    
    
    public void setSize( String size )
    {
        setAttribute( "size", size );
    }

    
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLBaseFontElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}

