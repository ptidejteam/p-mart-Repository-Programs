/*
 * $Header: /home/cvs/jakarta-struts/src/test/org/apache/struts/mock/MockPrincipal.java,v 1.5 2004/03/14 06:23:52 sraeburn Exp $
 * $Revision: 1.5 $
 * $Date: 2004/03/14 06:23:52 $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.struts.mock;


import java.security.Principal;


/**
 * <p>Mock <strong>Principal</strong> object for low-level unit tests
 * of Struts controller components.  Coarser grained tests should be
 * implemented in terms of the Cactus framework, instead of the mock
 * object classes.</p>
 *
 * <p><strong>WARNING</strong> - Only the minimal set of methods needed to
 * create unit tests is provided, plus additional methods to configure this
 * object as necessary.  Methods for unsupported operations will throw
 * <code>UnsupportedOperationException</code>.</p>
 *
 * <p><strong>WARNING</strong> - Because unit tests operate in a single
 * threaded environment, no synchronization is performed.</p>
 *
 * @version $Revision: 1.5 $ $Date: 2004/03/14 06:23:52 $
 */

public class MockPrincipal implements Principal {


    public MockPrincipal() {
        super();
        this.name = "";
        this.roles = new String[0];
    }


    public MockPrincipal(String name) {
        super();
        this.name = name;
        this.roles = new String[0];
    }


    public MockPrincipal(String name, String roles[]) {
        super();
        this.name = name;
        this.roles = roles;
    }


    protected String name = null;


    protected String roles[] = null;


    public String getName() {
        return (this.name);
    }


    public boolean isUserInRole(String role) {
        for (int i = 0; i < roles.length; i++) {
            if (role.equals(roles[i])) {
                return (true);
            }
        }
        return (false);
    }


    public boolean equals(Object o) {
        if (o == null) {
            return (false);
        }
        if (!(o instanceof Principal)) {
            return (false);
        }
        Principal p = (Principal) o;
        if (name == null) {
            return (p.getName() == null);
        } else {
            return (name.equals(p.getName()));
        }
    }


    public int hashCode() {
        if (name == null) {
            return ("".hashCode());
        } else {
            return (name.hashCode());
        }
    }


}
