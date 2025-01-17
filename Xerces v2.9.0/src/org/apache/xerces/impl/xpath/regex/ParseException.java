/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.xerces.impl.xpath.regex;

/**
 * @xerces.internal
 *
 * @author TAMURA Kent &lt;kent@trl.ibm.co.jp&gt;
 * @version $Id: ParseException.java 446721 2006-09-15 20:35:34Z mrglavas $
 */
public class ParseException extends RuntimeException {
    
    /** Serialization version. */
    static final long serialVersionUID = -7012400318097691370L;
    
    int location;

    /*
    public ParseException(String mes) {
        this(mes, -1);
    }
    */
    /**
     *
     */
    public ParseException(String mes, int location) {
        super(mes);
        this.location = location;
    }

    /**
     *
     * @return -1 if location information is not available.
     */
    public int getLocation() {
        return this.location;
    }
}
