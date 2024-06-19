/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.xslt;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
// import org.apache.xerces.parser.*;
// import org.apache.xerces.parser.util.*;
import org.w3c.dom.*;
import org.apache.xalan.xpath.xml.*;
import org.xml.sax.SAXException;
import org.apache.xalan.xslt.trace.PrintTraceListener;
import org.apache.xalan.xpath.XPathException;
import org.apache.xalan.xslt.trace.TraceListener;
import org.apache.xalan.xslt.res.XSLTErrorResources;
import org.apache.xalan.xpath.xml.XSLMessages;

/**
 * <meta name="usage" content="general"/>
 * The main() method handles the Xalan command-line interface.
 */
public class Process
{
  /**
   * Prints argument options.
   */
  protected static void printArgOptions(XSLTErrorResources resbundle)
  {
    System.out.println(resbundle.getString("xslProc_option")); //"xslproc options: ");
    System.out.println(resbundle.getString("optionIN")); //"    -IN inputXMLURL");
    System.out.println(resbundle.getString("optionXSL")); //"   [-XSL XSLTransformationURL]");
    System.out.println(resbundle.getString("optionOUT")); //"   [-OUT outputFileName]");
    System.out.println(resbundle.getString("optionLXCIN")); //"   [-LXCIN compiledStylesheetFileNameIn]");
    System.out.println(resbundle.getString("optionLXCOUT")); //"   [-LXCOUT compiledStylesheetFileNameOutOut]");
    System.out.println(resbundle.getString("optionPARSER")); //"   [-PARSER fully qualified class name of parser liaison]");
    // System.out.println(resbundle.getString("optionE")); //"   [-E (Do not expand entity refs)]");
    System.out.println(resbundle.getString("optionV")); //"   [-V (Version info)]");
    System.out.println(resbundle.getString("optionQC")); //"   [-QC (Quiet Pattern Conflicts Warnings)]");
    System.out.println(resbundle.getString("optionQ")); //"   [-Q  (Quiet Mode)]");
    System.out.println(resbundle.getString("optionLF")); //"   [-LF (Use linefeeds only on output {default is CR/LF})]");
    System.out.println(resbundle.getString("optionCR")); //"   [-CR (Use carriage returns only on output {default is CR/LF})]");
    System.out.println(resbundle.getString("optionESCAPE")); //"   [-ESCAPE (Which characters to escape {default is <>&\"\'\\r\\n}]");
    System.out.println(resbundle.getString("optionINDENT")); //"   [-INDENT (Control how many spaces to indent {default is 0})]");
    System.out.println(resbundle.getString("optionTT")); //"   [-TT (Trace the templates as they are being called.)]");
    System.out.println(resbundle.getString("optionTG")); //"   [-TG (Trace each generation event.)]");
    System.out.println(resbundle.getString("optionTS")); //"   [-TS (Trace each selection event.)]");
    System.out.println(resbundle.getString("optionTTC")); //"   [-TTC (Trace the template children as they are being processed.)]");
    System.out.println(resbundle.getString("optionTCLASS")); //"   [-TCLASS (TraceListener class for trace extensions.)]");
    System.out.println(resbundle.getString("optionVALIDATE")); //"   [-VALIDATE (Set whether validation occurs.  Validation is off by default.)]");
    System.out.println(resbundle.getString("optionEDUMP")); //"   [-EDUMP {optional filename} (Do stackdump on error.)]");
    System.out.println(resbundle.getString("optionXML")); //"   [-XML (Use XML formatter and add XML header.)]");
    System.out.println(resbundle.getString("optionTEXT")); //"   [-TEXT (Use simple Text formatter.)]");
    System.out.println(resbundle.getString("optionHTML")); //"   [-HTML (Use HTML formatter.)]");
    System.out.println(resbundle.getString("optionPARAM")); //"   [-PARAM name expression (Set a stylesheet parameter)]");
    System.out.println("[-MEDIA use media attribute to find stylesheet associated with a document.]"); //"   [-PARAM name expression (Set a stylesheet parameter)]");
    System.out.println("[-SX (User Xerces Serializers]"); //"   [-PARAM name expression (Set a stylesheet parameter)]");
  }

  /**
   * Command line interfact to transform the XML according to
   * the instructions found in the XSL stylesheet.
   * <pre>
   *    -IN inputXMLURL
   *    -XSL XSLTransformationURL
   *    -OUT outputFileName
   *    -LXCIN compiledStylesheetFileNameIn
   *    -LXCOUT compiledStylesheetFileNameOut
   *    -PARSER fully qualified class name of parser liaison
   *    -V (Version info)
   *    -QC (Quiet Pattern Conflicts Warnings)
   *    -Q  (Quiet Mode)
   *    -LF (Use linefeeds only on output -- default is CR/LF)
   *    -CR (Use carriage returns only on output -- default is CR/LF)
   *    -INDENT (Number of spaces to indent each level in output tree --default is 0)
   *    -TT (Trace the templates as they are being called)
   *    -TG (Trace each result tree generation event)
   *    -TS (Trace each selection event)
   *    -TTC (Trace the template children as they are being processed)
   *    -VALIDATE (Validate the XML and XSL input -- validation is off by default)
   *    -EDUMP [optional]FileName (Do stackdump on error)
   *    -XML (Use XML formatter and add XML header)
   *    -TEXT (Use simple Text formatter)
   *    -HTML (Use HTML formatter)
   *    -PARAM name expression (Set a stylesheet parameter)
   * </pre>
   *  <p>Use -IN to specify the XML source document. To specify the XSL stylesheet, use -XSL
   *  or -LXCIN. To compile an XSL stylesheet for future use as -LXCIN input, use -LXCOUT.</p>
   *  <p>Include -PARSER if you supply your own parser liaison class, which is required
   *  if you do not use the &xml4j; parser.</p>
   *  <p>Use -TEXT if you want the output to include only element values (not element tags with element names and
   *  attributes). Use -HTML to write 4.0 transitional HTML (some elements, such as &lt;br&gt;, are
   *  not well formed.</p>
   *  <p>To set stylesheet parameters from the command line, use -PARAM name expression. If
   *  you want to set the parameter to a string value, enclose the string in single quotes (') to
   */
  public static void main( String argv[] )
  {
    Runtime.getRuntime().traceMethodCalls(false); // turns tracing off
    boolean doStackDumpOnError = false;
    boolean setQuietMode = false;

    // Runtime.getRuntime().traceMethodCalls(false);
    // Runtime.getRuntime().traceInstructions(false);
    /**
    * The default diagnostic writer...
    */
    java.io.PrintWriter diagnosticsWriter = new PrintWriter(System.err, true);
    java.io.PrintWriter dumpWriter = diagnosticsWriter;

    XSLTErrorResources resbundle = (XSLTErrorResources)(XSLMessages.loadResourceBundle(Constants.ERROR_RESOURCES));

    if(argv.length < 1)
    {
      printArgOptions(resbundle);
    }
    else
    {
      XMLParserLiaison xmlProcessorLiaison;
      String parserLiaisonClassName = Constants.LIAISON_CLASS;
      try
      {
        boolean usingDefault = true;
        for (int i = 0;  i < argv.length;  i ++)
        {
          if("-PARSER".equalsIgnoreCase(argv[i]))
          {
            i++;
            parserLiaisonClassName = argv[i];
            usingDefault = false;
          }
        }

        Class parserLiaisonClass = Class.forName(parserLiaisonClassName);

        Constructor parserLiaisonCtor = parserLiaisonClass.getConstructor(null);
        xmlProcessorLiaison
          = (XMLParserLiaison)parserLiaisonCtor.newInstance(null);
      }
      catch(Exception e)
      {
        System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_COULD_NOT_CREATE_XML_PROC_LIAISON, new Object[] {parserLiaisonClassName})); //"Could not create XML Processor Liaison: "+parserLiaisonClassName);
        return;
      }

      XSLTProcessor processor = XSLTProcessorFactory.getProcessor(xmlProcessorLiaison);
      boolean formatOutput = false;

      QName mode = null;
      String inFileName = null;
      String outFileName = null;
      String dumpFileName = null;
      String xslFileName = null;
      String compiledStylesheetFileNameOut = null;
      String compiledStylesheetFileNameIn = null;
      String treedumpFileName = null;
      boolean didSetCR = false;
      boolean didSetLF = false;
      boolean stripCData = false;
      boolean escapeCData = false;
      boolean useXercesSerializers = false;
      PrintTraceListener tracer = null;
      FileOutputStream compiledStylesheetOutputStream = null;
      ObjectOutputStream compiledStylesheetOutput = null;
      String outputType = null;
      String media = null;

      for (int i = 0;  i < argv.length;  i ++)
      {
        if("-TT".equalsIgnoreCase(argv[i]))
        {
          if(null == tracer)
            tracer = new PrintTraceListener(diagnosticsWriter);
          tracer.m_traceTemplates = true;
          // processor.setTraceTemplates(true);
        }
        else if("-TG".equalsIgnoreCase(argv[i]))
        {
          if(null == tracer)
            tracer = new PrintTraceListener(diagnosticsWriter);
          tracer.m_traceGeneration = true;
          // processor.setTraceSelect(true);
        }
        else if("-TS".equalsIgnoreCase(argv[i]))
        {
          if(null == tracer)
            tracer = new PrintTraceListener(diagnosticsWriter);
          tracer.m_traceSelection = true;
          // processor.setTraceTemplates(true);
        }
        else if("-TTC".equalsIgnoreCase(argv[i]))
        {
          if(null == tracer)
            tracer = new PrintTraceListener(diagnosticsWriter);
          tracer.m_traceElements = true;
          // processor.setTraceTemplateChildren(true);
        }
        else if ("-TCLASS".equalsIgnoreCase(argv[i]))
        {
          if ( i+1 < argv.length)
          {
            String className=argv[++i];
            try
            {
              Class traceClass = Class.forName(className);
              Constructor traceCtor = traceClass.getConstructor(null);
              TraceListener traceL = (TraceListener)traceCtor.newInstance(null);
              processor.addTraceListener(traceL);
            }
            catch(Exception e)
            {
              System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_COULD_NOT_CREATE_TRACELISTENER, new Object[] {className})); //"Could not create TraceListener: "+className);
            }
          }
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-TCLASS"})); //"Missing argument for);

        }
        else if ("-ESCAPE".equalsIgnoreCase(argv[i]))
        {
          if ( i+1 < argv.length)
            xmlProcessorLiaison.setSpecialCharacters(argv[++i]);
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-ESCAPE"})); //"Missing argument for);

        }
        else if ("-INDENT".equalsIgnoreCase(argv[i]))
        {
          int indentAmount;
          if(((i+1) < argv.length) && (argv[i+1].charAt(0) != '-'))
          {
            indentAmount = Integer.parseInt( argv[++i] );
          }
          else
          {
            indentAmount = 0;
          }
          xmlProcessorLiaison.setIndent(indentAmount);
        }
        else if ("-IN".equalsIgnoreCase(argv[i]))
        {
          if ( i+1 < argv.length)
            inFileName = argv[++i];
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-IN"})); //"Missing argument for);

        }
        else if ("-MEDIA".equalsIgnoreCase(argv[i]))
        {
          if ( i+1 < argv.length)
            media = argv[++i];
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-MEDIA"})); //"Missing argument for);

        }

        else if ("-OUT".equalsIgnoreCase(argv[i]))
        {
          if ( i+1 < argv.length)
            outFileName = argv[++i];
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-OUT"})); //"Missing argument for);

        }
        else if ("-XSL".equalsIgnoreCase(argv[i]))
        {
          if ( i+1 < argv.length)
            xslFileName = argv[++i];
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-XSL"})); //"Missing argument for);

        }
        else if("-LXCIN".equalsIgnoreCase(argv[i]))
        {
          if ( i+1 < argv.length)
            compiledStylesheetFileNameIn = argv[++i];
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-LXCIN"})); //"Missing argument for);

        }
        else if("-LXCOUT".equalsIgnoreCase(argv[i]))
        {
          if ( i+1 < argv.length)
            compiledStylesheetFileNameOut = argv[++i];
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-LXCOUT"})); //"Missing argument for);

        }
        else if ("-PARAM".equalsIgnoreCase(argv[i]))
        {
          if ( i+2 < argv.length)
          {
            String name = argv[++i];
            String expression = argv[++i];
            processor.setStylesheetParam(name, expression);
          }
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-PARAM"})); //"Missing argument for);

        }
        else if ("-treedump".equalsIgnoreCase(argv[i]))
        {
          if ( i+1 < argv.length)
            treedumpFileName = argv[++i];
          else
            System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_ARG_FOR_OPTION, new Object[] {"-treedump"})); //"Missing argument for);

        }
        else if("-F".equalsIgnoreCase(argv[i]))
        {
          formatOutput = true;
        }
        else if("-E".equalsIgnoreCase(argv[i]))
        {
          xmlProcessorLiaison.setShouldExpandEntityRefs(false);
        }
        else if("-V".equalsIgnoreCase(argv[i]))
        {
          diagnosticsWriter.println(resbundle.getString("version") //">>>>>>> Xalan Version "
                                    +XSLProcessorVersion.S_VERSION+", "+
                                    xmlProcessorLiaison.getParserDescription()+
                                    resbundle.getString("version2"));// "<<<<<<<");
        }
        else if("-QC".equalsIgnoreCase(argv[i]))
        {
          processor.setQuietConflictWarnings(true);
        }
        else if("-Q".equalsIgnoreCase(argv[i]))
        {
          setQuietMode = true;
        }
        else if("-VALIDATE".equalsIgnoreCase(argv[i]))
        {
          String shouldValidate;
          if(((i+1) < argv.length) && (argv[i+1].charAt(0) != '-'))
          {
            shouldValidate = argv[++i];
          }
          else
          {
            shouldValidate = "yes";
          }

          xmlProcessorLiaison.setUseValidation(shouldValidate.equalsIgnoreCase("yes"));
        }
        else if("-PARSER".equalsIgnoreCase(argv[i]))
        {
          i++;
          // Handled above
        }
        else if("-XML".equalsIgnoreCase(argv[i]))
        {
          outputType = "xml";
        }
        else if("-TEXT".equalsIgnoreCase(argv[i]))
        {
          outputType = "text";
        }
        else if("-HTML".equalsIgnoreCase(argv[i]))
        {
          outputType = "html";
        }
        else if("-STRIPCDATA".equalsIgnoreCase(argv[i]))
        {
          stripCData = true;
        }
        else if("-ESCAPECDATA".equalsIgnoreCase(argv[i]))
        {
          escapeCData = true;
        }
        else if("-ESCAPECDATA".equalsIgnoreCase(argv[i]))
        {
          useXercesSerializers = true;
        }
        else if("-EDUMP".equalsIgnoreCase(argv[i]))
        {
          doStackDumpOnError = true;
          if(((i+1) < argv.length) && (argv[i+1].charAt(0) != '-'))
          {
            dumpFileName = argv[++i];
          }
        }
        else
          System.err.println(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_OPTION, new Object[] {argv[i]})); //"Invalid argument:);

      }

      // The main XSL transformation occurs here!
      try
      {
        processor.setDiagnosticsOutput( setQuietMode ? null : diagnosticsWriter );
        // processor.pushTime(processor);

        if(null != dumpFileName)
        {
          dumpWriter = new PrintWriter( new FileWriter(dumpFileName) );
        }

        StylesheetRoot stylesheet = null;

        if(null != compiledStylesheetFileNameIn)
        {
          try
          {
            FileInputStream fileInputStream
              = new FileInputStream(compiledStylesheetFileNameIn);
            ObjectInputStream objectInput
              = new ObjectInputStream(fileInputStream);
            stylesheet = (StylesheetRoot)objectInput.readObject();
            objectInput.close();
          }
          catch(java.io.UnsupportedEncodingException uee)
          {
            stylesheet = null;
            diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_ENCODING_NOT_SUPPORTED, new Object[] {stylesheet.m_encoding})); //"Encoding not supported: "+stylesheet.m_encoding);
            throw new XSLProcessorException(XSLMessages.createMessage(XSLTErrorResources.ER_ENCODING_NOT_SUPPORTED, new Object[] {stylesheet.m_encoding})); //"Encoding not supported: "+stylesheet.m_encoding);
          }
        }
        else if(null != xslFileName)
        {
          stylesheet = processor.processStylesheet(xslFileName);
        }

        PrintWriter resultWriter;
        String mimeEncoding = null;

        OutputStream outputStream = null;
        if(null != outFileName)
        {
          outputStream = new FileOutputStream(outFileName);
          // processor.setOutputFileName(outFileName);
        }

        if (null == outputStream)
          outputStream = System.out;

        if(null != stylesheet)
        {
          if(null != outputType)
            stylesheet.setOutputMethod(outputType);
          stylesheet.m_useXercesSerializers = useXercesSerializers;
        }

        if(null != tracer)
          processor.addTraceListener(tracer);

        if(null != inFileName)
        {
          if(null != stylesheet)
          {
            Node sourceTree = processor.getSourceTreeFromInput(new XSLTInputSource(inFileName));
            stylesheet.process(processor, sourceTree, new XSLTResultTarget(outputStream));
          }
          else if(null != media)
          {
            StylesheetSpec spec = processor.getAssociatedStylesheet(new XSLTInputSource(inFileName),
                                                                    media, null);
            if(null != spec)
            {
              if(spec.getSystemId() != null)
              {
                URL url = processor.getXMLProcessorLiaison().getURLFromString(spec.getSystemId(), inFileName);
                spec.setSystemId(url.toExternalForm());
              }
              processor.process(new XSLTInputSource(inFileName),
                                spec,
                                new XSLTResultTarget(outputStream));
            }
            else
            {
              throw new XSLProcessorException("No stylesheet found for media: "+media);
            }
          }
          else
          {
            processor.process(new XSLTInputSource(inFileName),
                              (XSLTInputSource)null,
                              new XSLTResultTarget(outputStream));
          }
        }
        else
        {
          if(null != stylesheet)
          {
            if(null == compiledStylesheetFileNameOut)
            {
              Document dummyDoc = xmlProcessorLiaison.createDocument();
              stylesheet.process(processor, dummyDoc, new XSLTResultTarget(outputStream));
            }
          }
        }

        if(null != stylesheet)
        {
          if(null != compiledStylesheetFileNameOut)
          {
            compiledStylesheetOutputStream
              = new FileOutputStream(compiledStylesheetFileNameOut);
            compiledStylesheetOutput
              = new ObjectOutputStream(compiledStylesheetOutputStream);

            compiledStylesheetOutput.writeObject(stylesheet);
          }
        }
      }
      catch(TooManyListenersException tmle)
      {
        if(doStackDumpOnError)
          tmle.printStackTrace(dumpWriter);
        // else
        //  System.out.println("Error! "+se.getMessage());
        diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUCCESSFUL, null)); //"XSL Process was not successful.");
      }
      catch(XPathException xpe)
      {
        if(doStackDumpOnError)
          xpe.printStackTrace(dumpWriter);

        if((null != xpe.getStylesheetNode())
           &&  xpe.getStylesheetNode() instanceof ElemTemplateElement)
        {
          ElemTemplateElement elem = (ElemTemplateElement)xpe.getStylesheetNode();
          diagnosticsWriter.println(elem.m_stylesheet.m_baseIdent+"; " + resbundle.getString("line")+
                                    elem.m_lineNumber+"; " + resbundle.getString("column")+
                                    elem.m_columnNumber+"; "+xpe.getMessage());
        }
        else
        {
          if(xpe instanceof XSLProcessorException)
          {
            diagnosticsWriter.println("XSLT: "+xpe.getMessage());
          }
          else
          {
            diagnosticsWriter.println("XPATH: "+xpe.getMessage());
          }
        }
        diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUCCESSFUL, null)); //"XPATH: XSL Process was not successful.");
      }
      catch(SAXException se)
      {
        Exception containedException = se.getException();
        if(null != containedException)
        {
          if(doStackDumpOnError)
            containedException.printStackTrace(dumpWriter);
          // else
          //  System.out.println("Error! "+se.getMessage());
          diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUCCESSFUL, null)); //"XSL Process was not successful.");
        }
        else
        {
          if(doStackDumpOnError)
            se.printStackTrace(dumpWriter);
          // else
          //  System.out.println("Error! "+se.getMessage());
          diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUCCESSFUL, null) ); //"XSL Process was not successful.");
        }
      }
      catch(MalformedURLException mue)
      {
        if(doStackDumpOnError)
          mue.printStackTrace(dumpWriter);
        else
          System.out.println("Error! "+mue.getMessage());
        diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUCCESSFUL, null)); //"XSL Process was not successful.");
      }
      catch(ClassNotFoundException cnfe)
      {
        if(doStackDumpOnError)
          cnfe.printStackTrace(dumpWriter);
        else
          System.out.println("Error! "+cnfe.getMessage());
        diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUCCESSFUL, null)); //"XSL Process was not successful.");
      }
      catch(FileNotFoundException fne)
      {
        if(doStackDumpOnError)
          fne.printStackTrace(dumpWriter);
        else
          System.out.println("Error! "+fne.getMessage());
        diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUCCESSFUL, null)); //"XSL Process was not successful.");
      }
      catch(IOException ioe)
      {
        if(doStackDumpOnError)
          ioe.printStackTrace(dumpWriter);
        else
          System.out.println("Error! "+ioe.getMessage());
        diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUCCESSFUL, null)); //"XSL Process was not successful.");
      }
      catch(java.lang.NoClassDefFoundError ncdfe)
      {
        String msg = ncdfe.getMessage();

        if(msg.indexOf("com/ibm/xml/parser/Parser") >= 0)
        {
          diagnosticsWriter.println("==========================================");
          diagnosticsWriter.println(resbundle.getString("noParsermsg1")); //"XSL Process was not successful.");
          diagnosticsWriter.println(resbundle.getString("noParsermsg2")); //"** Could not find parser **");
          diagnosticsWriter.println(resbundle.getString("noParsermsg3")); //"Please check your classpath.");
          diagnosticsWriter.println(resbundle.getString("noParsermsg4")); //"If you don't have IBM's XML Parser for Java, you can download it from");
          diagnosticsWriter.println(resbundle.getString("noParsermsg5")); //"IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml");
          diagnosticsWriter.println("==========================================");
        }
        else
        {
          if(doStackDumpOnError)
            ncdfe.printStackTrace(dumpWriter);
          else
            System.out.println("Error! "+ncdfe.getMessage());
          diagnosticsWriter.println(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUCCESSFUL, null)); //"XSL Process was not successful.");
        }
      }
      finally
      {
        try
        {
          if(null != compiledStylesheetOutput)
          {
            compiledStylesheetOutput.flush();
            compiledStylesheetOutput.close();
          }
          if(null != compiledStylesheetOutputStream)
          {
            compiledStylesheetOutputStream.flush();
            compiledStylesheetOutputStream.close();
          }
        }
        catch(Exception e)
        {
        }
      }

      if(null != dumpFileName)
      {
        dumpWriter.close();
      }
      if(null != diagnosticsWriter)
      {
        // diagnosticsWriter.close();
      }
      if(!setQuietMode)
        diagnosticsWriter.println(resbundle.getString("xsldone")); //"Xalan: done");
      else
        diagnosticsWriter.println(""); //"Xalan: done");
    }
  }
}
