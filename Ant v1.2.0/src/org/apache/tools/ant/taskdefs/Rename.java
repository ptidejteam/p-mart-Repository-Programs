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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

/**
 * Renames a file.
 *
 * @author haas@softwired.ch
 *
 * @deprecated The rename task is deprecated.  Use move instead.
 */
public class Rename extends Task {

    private File src;
    private File dest;
    private boolean replace = true;


    /**
     * Sets the file to be renamed.
     * @param src the file to rename
     */
    public void setSrc(String src) {
        this.src = project.resolveFile(src);
    }

    /**
     * Sets the new name of the file.
     * @param dest the new name of the file.
     */
    public void setDest(String dest) {
        this.dest = project.resolveFile(dest);
    }

    /**
     * Sets wheter an existing file should be replaced.
     * @param replace <code>on</code>, if an existing file should be replaced.
     */
    public void setReplace(String replace) {
        this.replace = project.toBoolean(replace);
    }


    /**
     * Renames the file <code>src</code> to <code>dest</code>
     * @exception org.apache.tools.ant.BuildException The exception is
     * thrown, if the rename operation fails.
     */
    public void execute() throws BuildException {
        log("DEPRECATED - The rename task is deprecated.  Use move instead.");

        if (dest == null) {
            throw new BuildException("dest attribute is required", location);
        }

        if (src == null) {
            throw new BuildException("src attribute is required", location);
        }

        if (replace && dest.exists()) {
            if (!dest.delete()) {
                throw new BuildException("Unable to remove existing file " +
                      dest);
            }
       }
        if (!src.renameTo(dest)) {
            throw new BuildException("Unable to rename " + src + " to " +
                  dest);
        }
    }
}
