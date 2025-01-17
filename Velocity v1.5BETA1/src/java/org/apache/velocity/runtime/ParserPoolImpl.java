package org.apache.velocity.runtime;

import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.util.SimplePool;

/*
 * Copyright 2000-2006 The Apache Software Foundation.
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

/**
 * This wraps the original parser SimplePool class.  It also handles
 * instantiating ad-hoc parsers if none are available.
 *
 * @author <a href="mailto:sergek@lokitech.com">Serge Knystautas</a>
 * @version $Id: RuntimeInstance.java 384374 2006-03-08 23:19:30Z nbubna $
 */
public class ParserPoolImpl implements ParserPool {

    RuntimeServices rsvc = null;
    SimplePool pool = null;
    int max = RuntimeConstants.NUMBER_OF_PARSERS;

    /**
     * Create the underlying "pool".
     */
    public void initialize(RuntimeServices rsvc)
    {
        this.rsvc = rsvc;
        max = rsvc.getInt(RuntimeConstants.PARSER_POOL_SIZE, RuntimeConstants.NUMBER_OF_PARSERS);
        pool = new SimplePool(max);

        for (int i = 0; i < max; i++)
        {
            pool.put(rsvc.createNewParser());
        }

        if (rsvc.getLog().isDebugEnabled())
        {
            rsvc.getLog().debug("Created '" + max + "' parsers.");
        }
    }

    /**
     * Call the wrapped pool.  If none are available, it will create a new
     * temporary one.
     */
    public Parser get()
    {
        Parser parser = (Parser) pool.get();
        if (parser == null)
        {
            rsvc.getLog().debug("Created new " +
                    "parser (pool exhausted).  Consider " +
                    "increasing pool size.");
            parser = rsvc.createNewParser();
        }
        return parser;
    }

    /**
     * Call the wrapped pool.
     */
    public void put(Parser parser)
    {
        pool.put(parser);
    }
}
