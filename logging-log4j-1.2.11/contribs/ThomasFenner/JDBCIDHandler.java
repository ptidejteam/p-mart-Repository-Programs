/*
 * Copyright 1999-2005 The Apache Software Foundation.
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

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
*/

package com.klopotek.utils.log;


/**
This interface has to be implemented to provide ID-columns with unique IDs and its used in class JDBCLogger.

<p><b>Author : </b><A HREF="mailto:t.fenner@klopotek.de">Thomas Fenner</A></p>

@since 1.0
*/
public interface JDBCIDHandler
{
	/**Get a unique ID*/
	Object getID();
}



