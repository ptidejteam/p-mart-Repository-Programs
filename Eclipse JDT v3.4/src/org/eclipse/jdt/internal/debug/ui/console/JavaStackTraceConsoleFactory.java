/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;

/**
 * Creates a new console into which users can paste stack traces and follow
 * the hyperlinks.
 * 
 * @since 3.1
 */
public class JavaStackTraceConsoleFactory implements IConsoleFactory {
    private IConsoleManager fConsoleManager = null;
    private JavaStackTraceConsole fConsole = null;

    public JavaStackTraceConsoleFactory() {
        fConsoleManager = ConsolePlugin.getDefault().getConsoleManager();
        fConsoleManager.addConsoleListener(new IConsoleListener() {
            public void consolesAdded(IConsole[] consoles) {
            }

            public void consolesRemoved(IConsole[] consoles) {
                for (int i = 0; i < consoles.length; i++) {
                    if(consoles[i] == fConsole) {
                        fConsole.saveDocument();
                        fConsole = null;
                    }
                }
            }
        
        });
    }
    /* (non-Javadoc)
     * @see org.eclipse.ui.console.IConsoleFactory#openConsole()
     */
    public void openConsole() {
        if (fConsole == null) {
            fConsole = new JavaStackTraceConsole(); 
            fConsole.initializeDocument();
	        fConsoleManager.addConsoles(new IConsole[]{fConsole});
        }
        fConsoleManager.showConsoleView(fConsole);
    }
}
