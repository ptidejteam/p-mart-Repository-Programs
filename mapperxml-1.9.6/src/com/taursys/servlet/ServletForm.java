/**
 * ServletForm - Container for Components used by a Servlet Application
 *
 * Copyright (c) 2002
 *      Marty Phelan, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.taursys.dom.AbstractWriter;
import com.taursys.dom.DocumentAdapter;
import com.taursys.dom.XMLWriter;
import com.taursys.dom.DOM_1_20000929_DocumentAdapter;
import com.taursys.xml.event.RenderDispatcher;
import com.taursys.xml.event.Dispatcher;
import com.taursys.xml.event.RecycleException;
import com.taursys.servlet.respond.Responder;
import com.taursys.servlet.respond.HTMLResponder;
import com.taursys.xml.event.RecycleDispatcher;
import com.taursys.debug.Debug;
import com.taursys.xml.Container;

/**
 * ServletForm is the base container invoked used by Servlet application.
 * It contains all the components that make up the form.  The ServletForm
 * is responsible for servicing requests which are routed to it by the
 * ServletApp.
 * <p>
 * Your servlet forms will typically extend this base class.  The ServletForm
 * provides a default processing cycle within the <code>doGet</code> method.
 * <p>
 * The ServletForm is composed of many subcomponents to support the processing
 * of the request: ServletParameterDispatcher, ServletInputDispatcher,
 * RenderDispatcher, ServletTriggerDispatcher, DocumentAdapter and Responder.
 * <p>
 * You must supply this ServletForm with a DOM Document.  There are a variety
 * of ways you can achieve this: 1) use a DOM parser such as Xerces, 2) use
 * a DOM compiler such as Enhydra's XMLC, 3) build the DOM programatically.
 * <p>
 * The Document is normally created and attached to this ServletForm in the
 * <code>initForm</code> method.  This method is normally only called once when
 * the ServletForm is first invoked.  Below is an example using the Xerces
 * parser:
 * <pre>
 * protected void initForm() throws Exception {
 *   super.initForm();
 *   DOMParser parser = new DOMParser();
 *   InputSource is =
 *       new InputSource(getClass().getResourceAsStream("MyPage.html"));
 *   parser.parse(is);
 *   this.setDocument(parser.getDocument());
 * }
 * </pre>
 * In typical applications, you will add Components to this ServletForm and
 * set their properties to bind to the DOM Document elements, http request
 * parameters, and value objects.  These components are capable of modifying the
 * DOM Document, storing and retrieving value from bound objects, and reading
 * parameters from the http request, and parsing/converting between text values
 * and java data types.  This ServletForm is the base container for these
 * components and contains dispatchers which dispatch events to the components.
 * This ServletForm generates the events within the <code>doGet</code> method
 * in a fixed sequence (see javadoc for doGet).
 * <p>
 * The below example creates a HTMLInputText component which binds to an HTML
 * form input text field.  It also binds to the lastName property of a Java
 * value object class.
 * <pre>
 * public class MyPage extends ServletForm {
 *   HTMLInputText lastName = new HTMLInputText();
 *   VOValueHolder person = new VOValueHolder();
 *
 *   public MyPage() {
 *     lastName.setPropertyName("lastName");
 *     lastName.setValueHolder(person);
 *     lastName.setId("lastName");
 *     this.add(lastName);
 *   }
 *   ...
 *   protected void openForm() throws Exception {
 *     // Retrieve or create the value object
 *     Person personVO = new Person(1629, "Pulaski", "Katherine", null);
 *     // Bind value object to person ValueHolder
 *     person.setValueObject(personVO);
 *   }
 * </pre>
 * There are many components you can use in a ServletForm.  These include:
 * Parameter, Template, TextField, Trigger, Button, HTMLAnchorURL,
 * HTMLCheckBox, HTMLInputText, HTMLSelect, HTMLTextArea, and others.
 * <p>
 * You can control the response of this ServletForm by changing the Responder
 * subcomponent at runtime.  The default Responder is an HTMLResponder which
 * sends the DOM Document as the response.
 * <p>
 * The ServletForm is a reusable object. The ServletApp (master servlet) will
 * normally recycle ServletForms for an application unless their
 * <code>recycle</code> method returns false. The recycle method dispatches
 * a RecycleEvent to all components.
 * <p>
 * The ServletForm also supports multipart type requests. A multipart request
 * is sent by the browser when form data contains 1 or more uploaded files.
 * To support this feature, if the incoming request has a content type of
 * "multipart/form-data", the request is wrapped in another request object
 * which is capable of processing multipart requests. The wrapper request is
 * created via the createRequestWrapper method. By default, createRequestWrapper
 * returns a HttpMultiPartServletRequest object which has a maximum file size
 * of 1 megabyte and maximum single line size of 4,096 bytes. You can change
 * this by overriding the createRequestWrapper method:
 * <pre>
 * protected HttpServletRequest createRequestWrapper(HttpServletRequest rq)
 *     throws Exception {
 *   HttpMultiPartServletRequest multi = new HttpMultiPartServletRequest(rq);
 *   // set maximum sizes if defaults if needed
 *   multi.setMaxFileSize(2048);
 *   multi.setMaxLineLength(80);
 *   // parse the request
 *   multi.parseRequest();
 *   return multi;
 * }
 * </pre>
 * @see HttpMultiPartServletRequest
 */
public class ServletForm extends Container {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private ServletParameterDispatcher parameterDispatcher;
  private AbstractWriter xmlWriter;
  private RenderDispatcher renderDispatcher = new RenderDispatcher(this);
  private RecycleDispatcher recycleDispatcher = new RecycleDispatcher(this);
  private boolean initialized;
  private com.taursys.servlet.ServletInputDispatcher inputDispatcher;
  private com.taursys.servlet.ServletTriggerDispatcher triggerDispatcher;
  private DocumentAdapter documentAdapter;
  private boolean enableInput = true;
  private boolean enableActions = true;
  private com.taursys.servlet.respond.Responder responder;

  // ************************************************************************
  //                   Constructors and Recycle Method
  // ************************************************************************

  /**
   * Creates new servlet form and default dispatchers.
   */
  public ServletForm() {
    parameterDispatcher = createDefaultServletParameterDispatcher();
    inputDispatcher = createDefaultServletInputDispatcher();
    triggerDispatcher = createDefaultServletTriggerDispatcher();
    xmlWriter = createDefaultWriter();
    setResponder(createDefaultResponder());
  }

  /**
   * Dispatches a RecycleEvent to all components and returns true if successful.
   * If an exception occurs during recycling, it is logged using Debug and
   * this method returns false.
   * If the form cannot be reused, override this method and return false.
   * Override this method to provide custom behavior to recycle this form
   * for future re-use.
   */
  public boolean recycle() {
    try {
      recycleDispatcher.dispatch();
      return true;
    } catch (RecycleException ex) {
      com.taursys.debug.Debug.error("Problem during recycling",ex);
      return false;
    }
  }

  // ************************************************************************
  //               Primary Method for Processing Request
  // ************************************************************************

  /**
   * This method is invoked by the application servlet to service the request.
   * It is invoked by the ServletApp for all request types (GET, POST, etc).
   * This method invokes a series of other support methods in a specific sequence:
   * <ul>
   * <li>If this is a multipart type request, uses the request wrapper returned by
   * createRequestWrapper in place of the original request</li>
   * <li>saves request and response in public properties</li>
   * <li>initForm (only invoked if isInitialized is false)</li>
   * <li>dispatchParameters</li>
   * <li>openForm</li>
   * <li>if enableInput flag is true, dispatchInput</li>
   * <li>if enableActions flag is true, dispatchActions</li>
   * <li>sendResponse</li>
   * </ul>
   * If an exception is generated by any of these methods, the handleException
   * method is invoked. It can either handle the exception and send the
   * response, or it can rethrow the exception and let the application servlet
   * handle it (latter is default behavior).
   * @param req the incoming HttpServletRequest
   * @param resp the outgoing HttpServletResponse
   * @throws Exception if problem during processing the request
   */
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    try {
      response = resp;
      request = req;
      if (!isInitialized())
        initForm();
      if (req.getContentType() != null && req.getContentType().startsWith(
          HttpMultiPartServletRequest.MULTIPART_FORM_DATA)) {
        request = createRequestWrapper(req);
      }
      dispatchParameters();
      openForm();
      if (enableInput) {
        dispatchInput();
      }
      if (enableActions) {
        dispatchActions();
      }
      sendResponse();
    } catch (Exception ex) {
      handleException(ex);
    } finally {
      closeForm();
    }
  }

  // ************************************************************************
  //                   Request Processing Support Methods
  // ************************************************************************

  /**
   * This method is invoked by run to initialize the form.  It is
   * the first method invoked by the doGet method before any parameters have
   * been dispatched. This method sets the initialized flag to true.
   * Override this method to provide custom behavior.  If you override,
   * be sure to invoke super.initForm or setInitialized(true) if this
   * method should only be called once.
   */
  protected void initForm() throws Exception {
    setInitialized(true);
  }

  /**
   * This method is invoked by run to dispatch parameters to the Parameter components.
   * It is invoked by run following the initForm method.
   * This method invokes the dispatch method of the servletParameterDispatcher
   * (if defined).
   * Override this method only if you need to provide custom behavior.
   */
  protected void dispatchParameters() throws Exception {
    if (parameterDispatcher != null && request != null)
      parameterDispatcher.dispatch(request);
  }

  /**
   * This method is invoked by doGet to open the form but currently does nothing.
   * It is invoked after any parameters have been read, but before input
   * values have been read.  Override this method to provide custom
   * behavior such as opening data sources.  There is no need to invoke
   * super.openForm().
   */
  protected void openForm() throws Exception {
  }

  /**
   * This method is invoked by run to dispatch input to the Field components.
   * It is invoked by doGet following the openForm method.
   * This method invokes the dispatch method of the inputDispatcher
   * (if defined).
   * Override this method only if you need to provide custom behavior.
   */
  protected void dispatchInput() throws Exception {
    if (inputDispatcher != null && request != null)
      inputDispatcher.dispatch(request);
  }

  /**
   * This method is invoked by run to dispatch actions to the Trigger components.
   * It is invoked by doGet following the dispatchInput method.
   * This method invokes the dispatch method of the triggerDispatcher
   * (if defined).
   * Override this method to provide custom behavior.
   */
  protected void dispatchActions() throws Exception {
    if (triggerDispatcher != null && request != null)
      triggerDispatcher.dispatch(request);
  }

  /**
   * Send the appropriate response. It is invoked by doGet following the
   * dispatchActions method.  This method invokes the current Responder's
   * respond method to provide the appropriate response.
   * Change the Responder to provide custom response.
   */
  protected void sendResponse() throws Exception {
    responder.respond();
  }

  /**
   * Closes the form and any resources it may have opened.  This method is
   * the final method invoked by the doGet method (even if an Exception
   * occurred).  The method currently does nothing.
   * Override this method to provide custom behavior.
   */
  protected void closeForm() throws Exception {
  }

  /**
   * This method is invoked whenever an exception occurs within doGet.
   * Override this method to provide custom exception handling behavior.
   * Throwing an exception will delegate the exception handling to the
   * caller of the doGet method.
   * The default behavior of this method is to simply re-throw the exception.
   */
  protected void handleException(Exception ex) throws Exception {
    throw ex;
  }

  // ************************************************************************
  //                     Subcomponent Creation Methods
  // ************************************************************************

  /**
   * Creates a multipart request wrapper to service a multipart request.
   * By default, this implementation creates a
   * <code>com.taursys.servlet.HttpMultiPartServletRequest</code> and invokes
   * its <code>parseRequest()</code> method.
   * You can override this method to create and initialize your own default
   * request wrapper.
   * @param the original servlet request
   * @return a request wrapper to service a multipart request.
   * @throws Exception if problem creating or initializing request wrapper
   */
  protected HttpServletRequest createRequestWrapper(HttpServletRequest rq)
      throws Exception {
    HttpMultiPartServletRequest multi = new HttpMultiPartServletRequest(rq);
    multi.parseRequest();
    return multi;
  }

  /**
   * Creates the default Responder for this component.
   * This implementation creates an com.taursys.servlet.respond.HTMLResponder.
   * You can override this method to create your own default Responder.
   * @return new instance of the default Responder for this component.
   */
  protected Responder createDefaultResponder() {
    return new HTMLResponder();
  }

  /**
   * Creates the default AbstractWriter for this component.
   * This implementation creates an com.taursys.xml.XMLWriter.
   * You can override this method to create your own default
   * AbstractWriter.
   * @deprecated the AbstractWriter is now a subcomponent of the DocumentAdapter.
   * This property is no longer used and will be removed shortly.
   */
  protected AbstractWriter createDefaultWriter() {
    return new XMLWriter();
  }

  /**
   * Creates the default ServletParameterDispatcher used by this container
   */
  protected ServletParameterDispatcher createDefaultServletParameterDispatcher() {
    return new ServletParameterDispatcher();
  }

  /**
   * Creates the default ServletInputDispatcher used by this container
   */
  protected ServletInputDispatcher createDefaultServletInputDispatcher() {
    return new ServletInputDispatcher();
  }

  /**
   * Creates the default ServletTriggerDispatcher used by this container
   */
  protected ServletTriggerDispatcher createDefaultServletTriggerDispatcher() {
    return new ServletTriggerDispatcher();
  }

  // ************************************************************************
  //                       Property Accessor Methods
  // ************************************************************************

  /**
   * Sets the HttpServletRequest object for this ServletForm.
   * This is normally only valid during the invocation of the run method.
   * @param newRequest the HttpServletRequest object for this ServletForm
   */
  public void setRequest(HttpServletRequest newRequest) {
    request = newRequest;
  }

  /**
   * Gets the HttpServletRequest object for this ServletForm.
   * This is normally only valid during the invocation of the run method.
   * @return the HttpServletRequest object for this ServletForm
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /**
   * Sets the HttpServletResponse object for this ServletForm.
   * This is normally only valid during the invocation of the run method.
   * @param newResponse the HttpServletResponse object for this ServletForm
   */
  public void setResponse(HttpServletResponse newResponse) {
    response = newResponse;
  }

  /**
   * Gets the HttpServletResponse object for this ServletForm.
   * This is normally only valid during the invocation of the run method.
   * @return the HttpServletResponse object for this ServletForm
   */
  public HttpServletResponse getResponse() {
    return response;
  }

  /**
   * Sets the AbstractWriter used by this form to render the Document to
   * an XML stream.
   * @deprecated the AbstractWriter is now a subcomponent of the DocumentAdapter.
   * This property is no longer used and will be removed shortly.
   */
  public void setXmlWriter(AbstractWriter newXmlWriter) {
    xmlWriter = newXmlWriter;
  }

  /**
   * Returns the AbstractWriter used by this form to render the Document to
   * an XML stream.
   * @deprecated the AbstractWriter is now a subcomponent of the DocumentAdapter.
   * This property is no longer used and will be removed shortly.
   */
  public AbstractWriter getXmlWriter() {
    return xmlWriter;
  }

  /**
   * Sets the ServletParameterDispatcher used by this container
   */
  public void setServletParameterDispatcher(ServletParameterDispatcher d) {
    parameterDispatcher = d;
  }

  /**
   * Returns the ServletParameterDispatcher used by this container
   */
  public ServletParameterDispatcher getServletParameterDispatcher() {
    return parameterDispatcher;
  }

  /**
   * Returns the ParameterDispatcher used by this container.
   * This is invoked by Parameter components.  It overrides the
   * method defined in the Container class.
   */
  public Dispatcher getParameterDispatcher() {
    return parameterDispatcher;
  }

  /**
   * Sets the ServletInputDispatcher used by this container
   */
  public void setServletInputDispatcher(com.taursys.servlet.ServletInputDispatcher newServletInputDispatcher) {
    inputDispatcher = newServletInputDispatcher;
  }

  /**
   * Returns the ServletInputDispatcher used by this container
   */
  public com.taursys.servlet.ServletInputDispatcher getServletInputDispatcher() {
    return inputDispatcher;
  }

  /**
   * Returns the InputDispatcher used by this container.
   * This is invoked by Field components.  It overrides the
   * method defined in the Container class.
   */
  public Dispatcher getInputDispatcher() {
    return inputDispatcher;
  }

  /**
   * Returns the RenderDispatcher used by this container.
   * This is invoked by renderable components.  It overrides the
   * method defined in the Container class.
   */
  public RenderDispatcher getRenderDispatcher() {
    return renderDispatcher;
  }

  /**
   * Returns the RecycleDispatcher used by this container.
   * This is invoked by recycleable components.  It overrides the
   * method defined in the Container class.
   */
  public RecycleDispatcher getRecycleDispatcher() {
    return recycleDispatcher;
  }

  /**
   * Sets the document and creates the documentAdapter for this form.
   * This is the document which will be typically be modified and sent
   * back as the response.
   * @param newDocument for this form.
   */
  public void setDocument(org.w3c.dom.Document newDocument) {
/** @todo Use a factory to obtain a DocumentAdapter */
    documentAdapter = new DOM_1_20000929_DocumentAdapter(newDocument);
  }

  /**
   * Returns the document for this form.  This is the document which will
   * be sent back as the response.  This is also the document which the
   * components of this form will modify.
   */
  public org.w3c.dom.Document getDocument() {
    if (documentAdapter == null)
      return null;
    else
      return documentAdapter.getDocument();
  }

  /**
   * Sets the documentAdapter for this form.  The document adapter is used
   * by components as an adapter for the actual Document.  The adapter provides
   * the needed methods for components to manipulate the Document regardless
   * of the DOM version.
   * @param newDocumentAdapter for this form.
   */
  public void setDocumentAdapter(DocumentAdapter newDocumentAdapter) {
    documentAdapter = newDocumentAdapter;
  }

  /**
   * Gets the documentAdapter for this form.  The document adapter is used
   * by components as an adapter for the actual Document.  The adapter provides
   * the needed methods for components to manipulate the Document regardless
   * of the DOM version.
   * @return the documentAdapter for this form.
   */
  public DocumentAdapter getDocumentAdapter() {
    return documentAdapter;
  }

  /**
   * Sets an indicator that the form has been initialized (via the initForm method).
   * The run method will only invoke the initForm method if this indicator is
   * false, and then it will set this indicator to true.  This will prevent the
   * form from being initialized again (if it is recycled).
   */
  public void setInitialized(boolean newInitialized) {
    initialized = newInitialized;
  }

  /**
   * Indicates whether the form has been initialized (via the initForm method).
   * The run method will only invoke the initForm method if this indicator is
   * false, and then it will set this indicator to true.  This will prevent the
   * form from being initialized again (if it is recycled).
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Sets the ServletTriggerDispatcher used by this container
   */
  public void setServletTriggerDispatcher(ServletTriggerDispatcher newTriggerDispatcher) {
    triggerDispatcher = newTriggerDispatcher;
  }

  /**
   * Returns the ServletTriggerDispatcher used by this container
   */
  public ServletTriggerDispatcher getServletTriggerDispatcher() {
    return triggerDispatcher;
  }

  /**
   * Returns the TriggerDispatcher used by this container.
   * This is invoked by Trigger components.  It overrides the
   * method defined in the Container class.
   */
  public Dispatcher getTriggerDispatcher() {
    return triggerDispatcher;
  }

  /**
   * Set enableInput flag indicating whether or not to process input.
   * If this flag is set, the run method will invoke the dispatchInput method
   * to process input parameters for components. Disable input if you are
   * processing a request where no input parameters are expected. This will
   * avoid any input being set by default values (or behavior of HTMLCheckbox).
   * The default value for this flag is <code>true</code>.
   * @param newEnableInput flag indicating whether or not to process input.
   */
  public void setEnableInput(boolean newEnableInput) {
    enableInput = newEnableInput;
  }

  /**
   * Get enableInput flag indicating whether or not to process input.
   * If this flag is set, the run method will invoke the dispatchInput method
   * to process input parameters for components. Disable input if you are
   * processing a request where no input parameters are expected. This will
   * avoid any input being set by default values (or behavior of HTMLCheckbox).
   * The default value for this flag is <code>true</code>.
   * @return flag indicating whether or not to process input.
   */
  public boolean isEnableInput() {
    return enableInput;
  }

  /**
   * Set enableActions flag indicating whether or not to process actions.
   * If this flag is set, the run method will invoke the dispatchActions method
   * to process action parameters for trigger components.
   * Disable actions if you are processing a request where no action
   * parameters are expected.  This will avoid any actions being triggered
   * by default values.
   * The default value for this flag is <code>true</code>.
   * @param newEnableActions flag indicating whether or not to process actions.
   */
  public void setEnableActions(boolean newEnableActions) {
    enableActions = newEnableActions;
  }

  /**
   * Get enableActions flag indicating whether or not to process actions.
   * If this flag is set, the run method will invoke the dispatchActions method
   * to process action parameters for trigger components.
   * Disable actions if you are processing a request where no action
   * parameters are expected.  This will avoid any actions being triggered
   * by default values.
   * The default value for this flag is <code>true</code>.
   * @return flag indicating whether or not to process actions.
   */
  public boolean isEnableActions() {
    return enableActions;
  }

  /**
   * Set the Responder which will provide appropriate response.
   * You can change the Responder during runtime to provide different
   * kinds of responses.  A default Responder is created when this ServletForm
   * is created by the createDefaultResponder method.
   * This method also sets the Responder's servletForm property to this ServletForm.
   * @param newResponder the Responder which will provide appropriate response.
   */
  public void setResponder(Responder newResponder) {
    responder = newResponder;
    responder.setServletForm(this);
  }

  /**
   * Get the Responder which will provide appropriate response.
   * You can change the Responder during runtime to provide different
   * kinds of responses.  A default Responder is created when this ServletForm
   * is created by the createDefaultResponder method.
   * @return the Responder which will provide appropriate response.
   */
  public Responder getResponder() {
    return responder;
  }

  // ************************************************************************
  //          Implementation of Abstract Component/Container Methods
  // ************************************************************************

  /**
   * Override of Component abstract method which does nothing since this is
   * the top level Container. All events originate from this Container.
   */
  public void addNotify() {}

  /**
   * Override of Component abstract method which does nothing since this is
   * the top level Container. All events originate from this Container.
   */
  public void removeNotify() {}
}
