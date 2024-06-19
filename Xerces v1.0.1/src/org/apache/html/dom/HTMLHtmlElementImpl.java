package org.apache.html.dom;


import org.w3c.dom.*;
import org.w3c.dom.html.*;


/**
 * @version $Revision: 1.1 $ $Date: 2006/02/02 02:35:48 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLHtmlElement
 * @see ElementImpl
 */
public final class HTMLHtmlElementImpl
    extends HTMLElementImpl
    implements HTMLHtmlElement
{

    
    public String getVersion()
    {
        return capitalize( getAttribute( "version" ) );
    }
    
    
    public void setVersion( String version )
    {
        setAttribute( "version", version );
    }

    
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLHtmlElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}

