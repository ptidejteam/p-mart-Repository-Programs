/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/taglib/tiles/PutTag.java,v 1.6 2003/02/27 19:18:55 cedric Exp $
 * $Revision: 1.6 $
 * $Date: 2003/02/27 19:18:55 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.struts.taglib.tiles;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.tiles.util.TagUtils;
import org.apache.struts.tiles.AttributeDefinition;
import org.apache.struts.tiles.DefinitionNameAttribute;
import org.apache.struts.tiles.DirectStringAttribute;
import org.apache.struts.tiles.PathAttribute;


  /**
   * Put an attribute in enclosing attribute container tag.
   * Enclosing attribute container tag can be : &lt;insert&gt; or &lt;definition&gt;.
   * Exception is thrown if no appropriate tag can be found.
   * Put tag can have following atributes :
   * <li>
   * <ul>name : Name of the attribute</ul>
   * <ul>value | content : value to put as attribute</ul>
   * <ul>type : value type. Only valid if value is a String and is set by
   * value="something" or by a bean.
   * Possible type are : string (value is used as direct string),
   * page | template (value is used as a page url to insert),
   * definition (value is used as a definition name to insert)</ul>
   * <ul>direct : Specify if value is to be used as a direct string or as a
   * page url to insert. This is another way to specify the type. It only apply
   * if value is set as a string, and type is not present.</ul>
   * <ul>beanName : Name of a bean used for setting value. Only valid if value is not set.
   * If property is specified, value come from bean's property. Otherwise, bean
   * itself is used for value.</ul>
   * <ul>beanProperty : Name of the property used for retrieving value.</ul>
   * <ul>beanScope : Scope containing bean. </ul>
   * <ul>role : Role to check when 'insert' will be called. If enclosing tag is
   * &lt;insert&gt;, role is checked immediately. If enclosing tag is
   * &lt;definition&gt;, role will be checked when this definition will be
   * inserted.</ul>
   * </li>
   * Value can also come from tag body. Tag body is taken into account only if
   * value is not set by one of the tag attributes. In this case Attribute type is
   * "string", unless tag body define another type.
   */
public class PutTag extends BodyTagSupport implements  ComponentConstants {


    /* JSP Tag attributes */
    /** Name of attribute to put in component context */
  protected String attributeName = null;
    /** associated attribute value */
  private Object value = null;
    /** JSP Template compatibility */
  private String direct = null;
    /** Requested type for the value */
  private String valueType = null;
    /** Bean name attribute */
  private String beanName = null;
    /** Bean property attribute */
  private String beanProperty = null;
    /** Bean scope attribute */
  private String beanScope = null;
    /** Role attribute */
  private String role = null;

    /* Internal properties */
    /** cached real value computed from tag attributes */
  protected Object realValue = null;

  /**
   * Default constructor.
   */
  public PutTag() {
    super();
  }

    /**
     * Release all allocated resources.
     */
    public void release() {

        super.release();

        attributeName = null;
        valueType = null;
        direct = null;
        value = null;
        beanName = null;
        beanProperty = null;
        beanScope = null;
        role = null;
    }

    /**
     * Release internal properties.
     */
    protected void releaseInternal()
      {
      realValue = null;
      }

    /**
     * Set name.
     */
  public void setName(String value){
    this.attributeName = value;
  }

    /**
     * Get name.
     */
  public String getName(){
    return attributeName;
  }

    /**
     * Set value.
     * Method added to satisfy Tomcat (bug ?).
     */
  public void setValue(String value){
    this.value = value;
  }

    /**
     * Get value.
     * Method added to satisfy Tomcat (bug ?).
     */
  public String getValue(){
    return (String)this.value;
  }

    /**
     * Set value.
     */
  public void setValue(Object value){
    this.value = value;
  }

    /**
     * Set property value as an object.
     * Added because some web containers react badly to value as <code>Object</code>.
     */
  public void setObjectValue(Object value){
    this.value = value;
  }

    /**
     * Set content.
     * Method added to satisfy Tomcat (bug ?).
     */
  public void setContent(String value){
    this.value = value;
  }

    /**
     * Get content.
     * Method added to satisfy Tomcat (bug ?).
     */
  public String getContent(){
    return (String)value;
  }

    /**
     * Set content.
     */
  public void setContent(Object value){
    this.value = value;
  }

    /**
     * Set direct.
     * Method added for compatibility with JSP1.1.
     */
  public void setDirect( String isDirect ){
       this.direct = isDirect;
  }

    /**
     * Set type.
     */
  public void setType( String value ){
       this.valueType = value;
  }

    /**
     * Get type.
     */
  public String getType( ){
       return this.valueType;
  }

    /**
     * Set bean name.
     */
  public void setBeanName(String value){
    this.beanName = value;
  }

    /**
     * Get bean name.
     */
  public String getBeanName(){
    return beanName;
  }

    /**
     * Set bean property.
     */
  public void setBeanProperty(String value){
    this.beanProperty = value;
  }

    /**
     * Get bean property.
     */
  public String getBeanProperty(){
    return beanProperty;
  }

    /**
     * Set bean scope.
     */
  public void setBeanScope(String value){
    this.beanScope = value;
  }

      /**
     * Get bean scope.
     */
  public String getBeanScope(){
    return beanScope;
  }


    /**
     * Set role attribute.
     * @param role The role the user must be in to store content.
     */
   public void setRole(String role) {
      this.role = role;
   }

    /**
     * Get role attribute
     * @return The role defined in the tag or <code>null</code>.
     */
   public String getRole() {
      return role;
   }

    /**
     * Get real value according to tag attribute.
     * Real value is the value computed after attribute processing.
     * @return Real value.
     * @throws JspException If something goes wrong while getting value from bean.
     */
   public Object getRealValue() throws JspException
   {
   if( realValue == null )
     computeRealValue();

   return realValue;
   }

    /**
     * Compute real value according to tag attributes.
     * @throws JspException If something goes wrong while getting value from bean.
     */
  protected void computeRealValue() throws JspException
    {
        // Compute real value from attributes set.
    realValue = value;

        // If realValue is not set, value must come from body
    if( value == null && beanName == null )
        {
          // Test body content in case of empty body.
        if( bodyContent != null )
          realValue = bodyContent.getString();
         else
          realValue = "";
        }

        // Does value comes from a bean ?
      if( realValue == null && beanName != null )
        {
        getRealValueFromBean();
        return;
        } // end if

      // Is there a type set ?
      // First check direct attribute, and translate it to a valueType.
      // Then, evaluate valueType, and create requested typed attribute.
      // If valueType is not set, use the value "as is".
    if( valueType==null && direct != null )
      {
      if( Boolean.valueOf(direct).booleanValue() == true )
        valueType = "string";
       else
        valueType = "path";
      }  // end if

    if( realValue != null && valueType!=null && !(value instanceof AttributeDefinition) )
      {
      String strValue = realValue.toString();
        if( valueType.equalsIgnoreCase( "string" ) )
          {
          realValue = new DirectStringAttribute( strValue );
          }
         else if( valueType.equalsIgnoreCase( "page" ) )
          {
          realValue = new PathAttribute( strValue );
          }
         else if( valueType.equalsIgnoreCase( "template" ) )
          {
          realValue = new PathAttribute( strValue );
          }
         else if( valueType.equalsIgnoreCase( "instance" ) )
          {
          realValue = new DefinitionNameAttribute( strValue );
          }
         else if( valueType.equalsIgnoreCase( "definition" ) )
          {
          realValue = new DefinitionNameAttribute( strValue );
          }
         else
          { // bad type
          throw new JspException( "Warning - Tag put : Bad type '" + valueType + "'." );
          }  // end if
      } //  end  if

  }


    /**
     * Extract real value from specified bean.
     * @throws JspException If something goes wrong while getting value from bean.
     */
  protected void getRealValueFromBean() throws JspException
    {
    try
      {
        Object bean = TagUtils.retrieveBean( beanName, beanScope, pageContext );
        if( bean != null && beanProperty != null )
            realValue = TagUtils.getProperty( bean, beanProperty );
           else
            realValue = bean;   // value can be null
      }
     catch( NoSuchMethodException ex )
      {
      throw new JspException( "Error - component.PutAttributeTag : Error while retrieving value from bean '"
                              + beanName + "' with property '"
                              + beanProperty + "' in scope '"
                              + beanScope + "'. (exception : "
                              + ex.getMessage() );
      }
     catch( InvocationTargetException ex )
      {
      throw new JspException( "Error - component.PutAttributeTag : Error while retrieving value from bean '"
                              + beanName + "' with property '"
                              + beanProperty + "' in scope '"
                              + beanScope + "'. (exception : "
                              + ex.getMessage() );
      }
     catch( IllegalAccessException ex )
      {
      throw new JspException( "Error - component.PutAttributeTag : Error while retrieving value from bean '"
                              + beanName + "' with property '"
                              + beanProperty + "' in scope '"
                              + beanScope + "'. (exception : "
                              + ex.getMessage() );
      }
    }

    /**
     * Do start tag.
     */
  public int doStartTag() throws JspException
    {
      // Do we need to evaluate body ?
    if( value == null && beanName == null )
      return EVAL_BODY_TAG;
      // Value is set, don't evaluate body.
    return SKIP_BODY;
    }

    /**
     * Do end tag.
     */
  public int doEndTag() throws JspException
    {
      // Call parent tag which in turn does what it want
    callParent();
        // clean up tag handler for reuse.
    releaseInternal();

    return EVAL_PAGE;
  }

    /**
     * Find parent tag which must implement AttributeContainer.
     * @throws JspException If we can't find an appropriate enclosing tag.
     */
  protected void callParent() throws JspException
    {
            // Get enclosing parent
    PutTagParent enclosingParent = findEnclosingPutTagParent();
    enclosingParent.processNestedTag( this );
    }

    /**
     * Find parent tag which must implement AttributeContainer.
     * @throws JspException If we can't find an appropriate enclosing tag.
     */
  protected PutTagParent findEnclosingPutTagParent() throws JspException {
    try
      {
      PutTagParent parent = (PutTagParent)findAncestorWithClass(this,PutTagParent.class);
      if( parent == null )
        {
        throw new JspException( "Error - tag put : enclosing tag doesn't accept 'put' tag." );
        }
      return parent;
      }
     catch( ClassCastException ex )
      {
      throw new JspException( "Error - tag put : enclosing tag doesn't accept 'put' tag." );
      }
  }

}









