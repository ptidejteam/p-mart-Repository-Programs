package org.apache.html.dom;


import org.w3c.dom.*;
import org.w3c.dom.html.*;


/**
 * @version $Revision: 1.1 $ $Date: 2006/02/02 02:35:48 $
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLLIElement
 * @see ElementImpl
 */
public final class HTMLLIElementImpl
    extends HTMLElementImpl
	    implements HTMLLIElement
{

    
    public String getType()
    {
        return getAttribute( "type" );
    }
    
    
    public void setType( String type )
    {
        setAttribute( "type", type );
    }
        

    public int getValue()
    {
        return getInteger( getAttribute( "value" ) );
    }

    
    public void setValue( int value )
    {
        setAttribute( "value", String.valueOf( value ) );
    }

    
    /**
     * Constructor requires owner document.
     * 
     * @param owner The owner HTML document
     */
    public HTMLLIElementImpl( HTMLDocumentImpl owner, String name )
    {
        super( owner, name );
    }


}

