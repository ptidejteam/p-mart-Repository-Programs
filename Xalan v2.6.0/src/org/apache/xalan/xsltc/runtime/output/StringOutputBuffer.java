/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
/*
 * $Id: StringOutputBuffer.java,v 1.1 2006/03/09 00:08:07 vauchers Exp $
 */

package org.apache.xalan.xsltc.runtime.output;


/**
 * @author Santiago Pericas-Geertsen
 */
class StringOutputBuffer implements OutputBuffer {
    private StringBuffer _buffer;

    public StringOutputBuffer() {
	_buffer = new StringBuffer();
    }

    public String close() {
	return _buffer.toString();
    }

    public OutputBuffer append(String s) {
	_buffer.append(s);
	return this;
    }

    public OutputBuffer append(char[] s, int from, int to) {
	_buffer.append(s, from, to);
	return this;
    }

    public OutputBuffer append(char ch) {
	_buffer.append(ch);
	return this;
    }
}

