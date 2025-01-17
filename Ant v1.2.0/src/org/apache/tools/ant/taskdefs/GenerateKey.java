/*
 * The Apache Software License, Version 1.1
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.Commandline;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Generates a key.
 * 
 * @author Peter Donald <a href="mailto:donaldp@mad.scientist.com">donaldp@mad.scientist.com</a>
 */
public class GenerateKey extends Task {

    public static class DnameParam {
        private String name;
        private String value;
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class DistinguishedName {
        private String name;
        private String path;
        
        private Vector params = new Vector();
        
        public Object createParam() {
            DnameParam param = new DnameParam();
            params.addElement(param);
            
            return param;
        }
        
        public Enumeration getParams() {
            return params.elements();
        }

        public String toString() {
		  final int size = params.size();
		  final StringBuffer sb = new StringBuffer();
		  boolean firstPass = true;

		  for( int i = 0; i < size; i++ ) {
			  if( !firstPass ) {
				sb.append(" ,");
			  }
			  firstPass = false;

			  final DnameParam param = (DnameParam)params.elementAt( i );
			  sb.append( encode( param.getName() ) );
			  sb.append( '=' );
			  sb.append( encode( param.getValue() ) );
		  }
			
		  return sb.toString();
        }

	  public String encode( final String string ) {
		int end = string.indexOf(',');

		if( -1 == end ) return string;
		
		final StringBuffer sb = new StringBuffer();
		
		int start = 0;

		while( -1 != end )
		  {
			sb.append( string.substring( start, end ) );
			sb.append( "\\," );
			start = end + 1;
			end = string.indexOf( ',', start );
		  }

		sb.append( string.substring( start ) );
		
		return sb.toString();		
	  }
    }

    /**
     * The alias of signer.
     */
    protected String alias;

    /**
     * The name of keystore file.
     */
    protected String keystore;
    protected String storepass;
    protected String storetype;
    protected String keypass;

    protected String sigalg;
    protected String keyalg;
    protected String dname;
    protected DistinguishedName expandedDname;
    protected int keysize;
    protected int validity;
    protected boolean verbose;

    public DistinguishedName createDname() throws BuildException {
	    if( null != expandedDname ) {
		  throw new BuildException("DName sub-element can only be specified once.");
		}
		if( null != dname ) {
		  throw new BuildException("It is not possible to specify dname both as attribute and element.");
		}
		expandedDname = new DistinguishedName();
		return expandedDname;
    }
  
    public void setDname(final String dname) {
	    if( null != expandedDname ) {
		  throw new BuildException("It is not possible to specify dname both as attribute and element.");
		}
        this.dname = dname;
    } 

    public void setAlias(final String alias) {
        this.alias = alias;
    } 

    public void setKeystore(final String keystore) {
        this.keystore = keystore;
    } 

    public void setStorepass(final String storepass) {
        this.storepass = storepass;
    } 

    public void setStoretype(final String storetype) {
        this.storetype = storetype;
    } 

    public void setKeypass(final String keypass) {
        this.keypass = keypass;
    } 

    public void setSigalg(final String sigalg) {
        this.sigalg = sigalg;
    } 

    public void setKeyalg(final String keyalg) {
        this.keyalg = keyalg;
    } 

    public void setKeysize(final String keysize) throws BuildException {
	  try { this.keysize = Integer.parseInt(keysize); }
	  catch(final NumberFormatException nfe) 
		{
		  throw new BuildException( "KeySize attribute should be a integer" );
		}
    } 

    public void setValidity(final String validity) throws BuildException {
	  try { this.validity = Integer.parseInt(validity); }
	  catch(final NumberFormatException nfe) 
		{
		  throw new BuildException( "Validity attribute should be a integer" );
		}
    } 

    public void setVerbose(final String verbose) {
        this.verbose = project.toBoolean(verbose);
    } 

    public void execute() throws BuildException {
        if (project.getJavaVersion().equals(Project.JAVA_1_1)) {
            throw new BuildException("The genkey task is only available on JDK versions 1.2 or greater");
        } 

        if (null == alias) {
            throw new BuildException("alias attribute must be set");
        } 

        if (null == storepass) {
            throw new BuildException("storepass attribute must be set");
        } 

        if (null == dname && null == expandedDname) {
            throw new BuildException("dname must be set");
        } 

        final StringBuffer sb = new StringBuffer();

        sb.append("keytool -genkey ");

        if (verbose) {
            sb.append("-v ");
        } 

		sb.append("-alias \"");
		sb.append(alias);
		sb.append("\" ");

        if (null != dname) {
            sb.append("-dname \"");
            sb.append(dname);
            sb.append("\" ");
        } 

        if (null != expandedDname) {
            sb.append("-dname \"");
            sb.append(expandedDname);
            sb.append("\" ");
        } 

        if (null != keystore) {
		    sb.append("-keystore \"");
            sb.append(keystore);
			sb.append("\" ");
        }

        if (null != storepass) {
            sb.append("-storepass \"");
            sb.append(storepass);
            sb.append("\" ");
        } 

        if (null != storetype) {
            sb.append("-storetype \"");
            sb.append(storetype);
            sb.append("\" ");
        } 

		sb.append("-keypass \"");
        if (null != keypass) {
            sb.append(keypass);
        } 
		else {
            sb.append(storepass);
		}
		sb.append("\" ");

        if (null != sigalg) {
            sb.append("-sigalg \"");
            sb.append(sigalg);
            sb.append("\" ");
        } 

        if (null != keyalg) {
            sb.append("-keyalg \"");
            sb.append(keyalg);
            sb.append("\" ");
        } 


        if (0 < keysize) {
            sb.append("-keysize \"");
            sb.append(keysize);
            sb.append("\" ");
        } 

        if (0 < validity) {
            sb.append("-validity \"");
            sb.append(validity);
            sb.append("\" ");
        } 

        log("Generating Key for " + alias );
        final ExecTask cmd = (ExecTask) project.createTask("exec");
        cmd.setCommand(new Commandline(sb.toString()));
        cmd.setFailonerror(true);
        cmd.setTaskName( getTaskName() );
        cmd.execute();
    } 
}

