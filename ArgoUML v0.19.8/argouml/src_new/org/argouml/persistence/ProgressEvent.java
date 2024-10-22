// $Id: ProgressEvent.java,v 1.2 2006/03/02 05:08:31 vauchers Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.persistence;

import java.util.EventObject;

/**
 * An event to be fired in order to tell some other listener of progress
 * through some activity.
 * @author Bob Tarling
 */
public class ProgressEvent extends EventObject {

    private long length;
    
    private long position;
    
    private String description;
    
    /**
     * Constructor for a new ProgressEvent
     * @param source the source that generated this event
     * @param thePosition the position to which progress has reach as a 
     *        proportion of length
     * @param theLength the total length the progress is measuring
     */
    public ProgressEvent(Object source, long thePosition, long theLength) {
        super(source);
        this.length = theLength;
        this.position = thePosition;
    }

    /**
     * Constructor for a new ProgressEvent
     * @param source the source that generated this event
     * @param thePosition the position to which progress has reach as a 
     *        proportion of length
     * @param theLength the total length the progress is measuring
     * @param theDescription a text description of progress
     */
    public ProgressEvent(
            Object source, 
            long thePosition, 
            long theLength, 
            String theDescription) {
        super(source);
        this.length = theLength;
        this.position = thePosition;
        this.description = theDescription;
    }

    /**
     * Return the position of progress as a proportion of length.
     * @return progress position.
     */
    public long getPosition() {
        return position;
    }

    /**
     * Return the length that progress is measuring. Typically this is the
     * length of a file or 100 if percentage progress is being measured.
     * @return progress length.
     */
    public long getLength() {
        return length;
    }

    /**
     * An potional description of progress. The GUI should replace any existing
     * progress description it displays if it find that this is non-null.
     * @return progress description or null if no change.
     */
    public long getDescription() {
        return length;
    }
}
