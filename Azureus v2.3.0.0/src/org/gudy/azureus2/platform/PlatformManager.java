/*
 * Created on 18-Apr-2004
 * Created by Paul Gardner
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package org.gudy.azureus2.platform;

/**
 * @author parg
 *
 */
public interface 
PlatformManager 
{
	public static final int	PT_WINDOWS		= 1;
	public static final int PT_OTHER		= 2;
    public static final int PT_MACOSX 		= 3;

	public int
	getPlatformType();
	
	public String
	getVersion()
	
		throws PlatformManagerException;
	
	public String
	getUserDataDirectory()
	
		throws PlatformManagerException;
	
	public boolean
	isApplicationRegistered()
	
		throws PlatformManagerException;
	
	public void
	registerApplication()
	
		throws PlatformManagerException;
	
	public void
	createProcess(
		String	command_line,
		boolean	inherit_handles )
	
		throws PlatformManagerException;
	
	public void
    performRecoverableFileDelete(
		String	file_name )
	
		throws PlatformManagerException;

		/**
		 * enable or disable the platforms support for TCP TOS
		 * @param enabled
		 * @throws PlatformManagerException
		 */
	
	public void
	setTCPTOSEnabled(
		boolean		enabled )
		
		throws PlatformManagerException;

    /**
     * Reveals the file or directory with the platform's default browser
     * @param file_name The full path to a file or directory
     * @throws PlatformManagerException If this operation fails
     */
	public void
    showFile(
		String	file_name )

		throws PlatformManagerException;

    /**
     * <p>Gets whether the platform manager supports a capability</p>
     * <p>Users of PlatformManager should check for supported capabilities before calling
     * the corresponding methods</p>
     * <p>Note that support for a particular capability may change arbitrarily in
     * the duration of the application session, but the manager will cache where
     * necessary.</p>
     * @param capability A platform manager capability
     * @return True if the manager supports the capability
     */
    public boolean
	hasCapability(
		PlatformManagerCapabilities	capability );

    /**
     * Disposes system resources. This method is optional.
     */
    public void
    dispose();
}
