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
package org.apache.tools.ant.taskdefs.optional.ejb;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.Path;

import java.io.File;

/**
 * Execute a Weblogic server.
 
 *
 * @author <a href="mailto:conor@cortexebusiness.com.au">Conor MacNeill</a>, Cortex ebusiness Pty Limited
 */
public class WLRun extends Task {
    static private final String defaultPolicyFile = "weblogic.policy";

    /**
     * The classpath to be used when running the Java VM. It must contain the weblogic
     * classes <b>and</b> the implementation classes of the home and remote interfaces.
     */
    private Path classpath;

    /**
     * The weblogic classpath to the be used when running weblogic.
     */
    private Path weblogicClasspath;

    private String weblogicMainClass = "weblogic.Server";
    private String additionalArgs = "";
    
    /**
     * The security policy to use when running the weblogic server
     */
    private String securityPolicy;
    
    /**
     * The weblogic system home directory
     */
    private File weblogicSystemHome;
    
    /**
     * The name of the weblogic server - used to select the server's directory in the 
     * weblogic home directory.
     */
    private String weblogicSystemName = "myserver";
    
    /**
     * The file containing the weblogic properties for this server.
     */
    private String weblogicPropertiesFile = "weblogic.properties";

    /**
     * additional args to pass to the spawned jvm
     */
    private String additionalJvmArgs = "";
    
    /**
     * Do the work.
     *
     * The work is actually done by creating a separate JVM to run a helper task. 
     * This approach allows the classpath of the helper task to be set. Since the 
     * weblogic tools require the class files of the project's home and remote 
     * interfaces to be available in the classpath, this also avoids having to 
     * start ant with the class path of the project it is building.
     *
     * @exception BuildException if someting goes wrong with the build
     */
    public void execute() throws BuildException {
        if (weblogicSystemHome == null) {
            throw new BuildException("weblogic home must be set");
        }
        if (!weblogicSystemHome.isDirectory()) {
            throw new BuildException("weblogic home directory " + weblogicSystemHome.getPath() + 
                                     " is not valid");
        }
                
        File propertiesFile = new File(weblogicSystemHome, weblogicPropertiesFile);
        
        if (!propertiesFile.exists()) {
            throw new BuildException("Properties file " + weblogicPropertiesFile +
                                     " not found in weblogic home " + weblogicSystemHome);
        }

        File securityPolicyFile = null;
        if (securityPolicy == null) {
            securityPolicyFile = new File(weblogicSystemHome, defaultPolicyFile);
        }
        else {
            securityPolicyFile = new File(weblogicSystemHome, securityPolicy);
        }
        
        if (!securityPolicyFile.exists()) {
            throw new BuildException("Security policy " + securityPolicyFile +
                                     " was not found.");
        }

        Java weblogicServer = (Java)project.createTask("java");
        weblogicServer.setTaskName(getTaskName());
        weblogicServer.setFork(true);
        weblogicServer.setClassname(weblogicMainClass);

        String jvmArgs = "";
        
        if (weblogicClasspath != null) {
            jvmArgs += " -Dweblogic.class.path=" + weblogicClasspath;
        }
            
        jvmArgs += " -Djava.security.manager -Djava.security.policy==" + securityPolicyFile;
        jvmArgs += " -Dweblogic.system.home=" + weblogicSystemHome;
        jvmArgs += " -Dweblogic.system.name=" + weblogicSystemName;
        jvmArgs += " -Dweblogic.system.propertiesFile=" + weblogicPropertiesFile;
        jvmArgs += " " + additionalJvmArgs;

        weblogicServer.createJvmarg().setLine(jvmArgs);
        weblogicServer.createArg().setLine(additionalArgs);
        
        weblogicServer.setClasspath(classpath);                         
        if (weblogicServer.executeJava() != 0) {                         
            throw new BuildException("Execution of weblogic server failed");
        }
    }

    
    /**
     * Set the classpath to be used for this compilation.
     *
     * @param s the classpath to use when executing the weblogic task.
     */
    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }
    
    /**
     * Set the weblogic classpath.
     *
     * The weblogic classpath is used by weblogic to support dynamic class loading.
     *
     * @param weblogicClasspath the weblogic classpath
     */
    public void setWlclasspath(Path weblogicClasspath) {
        this.weblogicClasspath = weblogicClasspath;
    }
    
    /**
     * Set the security policy for this invocation of weblogic.
     *
     * @param securityPolicy the security policy to use.
     */
    public void setPolicy(String securityPolicy) {
        this.securityPolicy = securityPolicy;
    }
    
    /**
     * The location where weblogic lives.
     *
     * @param weblogicHome the home directory of weblogic.
     *
     */
    public void setHome(String weblogicHome) {
        weblogicSystemHome = new File(weblogicHome);
    }
    
    /**
     * Set the name of the server to run
     *
     * @param systemName the name of the server.
     */
    public void setName(String serverName) {
        this.weblogicSystemName = serverName;
    }
    
    /**
     * Set the properties file to use.
     *
     * The location of the properties file is relative to the weblogi system home
     *
     * @param propertiesFilename the properties file name
     */
    public void setProperties(String propertiesFilename) {
        this.weblogicPropertiesFile = propertiesFilename;
    }

    /**
     * Set the additional arguments to pass to the weblogic JVM
     * @param args the arguments to be passed to the JVM
     */
    public void setJvmargs(String args) {
        this.additionalJvmArgs = args;
    }
    public void setArgs(String args) 
    {
        additionalArgs = args;
    }
    public void setWeblogicMainClass(String c)
    {
        weblogicMainClass = c;
    }
}
