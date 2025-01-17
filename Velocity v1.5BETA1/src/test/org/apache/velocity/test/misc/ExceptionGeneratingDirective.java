package org.apache.velocity.test.misc;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * that always throws an exception.  Used to test
 * that RuntimeExceptions are passed through.
 * 
 * @author <a href="mailto:wglass@forio.com">Will Glass-Husain</a>
 * @version $Id: ExceptionGeneratingDirective.java 365832 2006-01-04 05:45:40Z wglass $
 */
public class ExceptionGeneratingDirective extends Directive
{

    public String getName()
    {
        return "Exception";
    }

    public int getType()
    {
        return Directive.BLOCK;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node)
            throws IOException, ResourceNotFoundException, ParseErrorException,
            MethodInvocationException
    {
        throw new RuntimeException("exception");
    }

}
