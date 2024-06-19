/*
 * IOHandler.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on March 11, 2002, 8:30 PM
 */

package pcgen.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

/**
 * <code>IOHandler</code><br>
 * Abstract IO handler class.<br>
 * An IO handler is responsible for reading
 * and/or writing PlayerCharacters in a specific format from/to a stream
 *
 * @author Thomas Behr 11-03-02
 * @version $Revision: 1.1 $
 */

abstract class IOHandler
{
	/////////////////////////////////////////////////////////////////////////////
	////////////////////////////// Abstract /////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes the contents of the given PlayerCharacter to a stream
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC   the PlayerCharacter to write
	 * @param out   the stream to be written to
	 */
	protected abstract void write(PlayerCharacter aPC, OutputStream out);

	/**
	 * Reads the contents of the given PlayerCharacter from a stream
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC   the PlayerCharacter to store the read data
	 * @param in    the stream to be read from
	 */
	protected abstract void read(PlayerCharacter aPC, InputStream in);

	/**
	 * Returns a descriptive string of the supported file format
	 *
	 * <br>author: Thomas Behr 11-03-02
	 */
	public abstract String getFileFormatString();

	/**
	 * Returns the file name extension that will be added (if not present)
	 * when saving a file.
	 *
	 * <br>author: Thomas Behr 11-03-02
	 */
	public abstract String getFileNameExtension();

	/////////////////////////////////////////////////////////////////////////////
	////////////////////////////// Convenience //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes the contents of the PlayerCharacter to a file.
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC        the PlayerCharacter to write
	 * @param filename   the name of the output file
	 */
	public final void write(PlayerCharacter aPC, String filename)
	{
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(filename);
			write(aPC, out);
		}
		catch (IOException ex)
		{
			Globals.errorPrint("Exception in IOHandler::write when writing", ex);
		}
		finally
		{
			try
			{
				out.flush();
				out.close();
			}
			catch (IOException e)
			{
				Globals.errorPrint("Exception in IOHandler::write", e);
			}
			catch (NullPointerException e)
			{
				Globals.errorPrint("Could not create FileOutputStream in IOHandler::write", e);
			}
		}
	}

	/**
	 * Fills the contents of the given graph from a file.
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC        the PlayerCharacter to store the read data
	 * @param filename   the name of the input file, i.e. the file to be read
	 */
	public final void read(PlayerCharacter aPC, String filename)
	{
		InputStream in = null;
		try
		{
			in = new FileInputStream(filename);
			read(aPC, in);
		}
		catch (IOException ex)
		{
			Globals.errorPrint("Exception in IOHandler::read when reading", ex);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				Globals.errorPrint("Exception in IOHandler::read", e);
			}
			catch (NullPointerException e)
			{
				Globals.errorPrint("Could not create file inputStream IOHandler::read", e);
			}
		}
	}

	/**
	 * Fills the contents of the given PlayerCharacter from a URL.
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC   the PlayerCharacter to store the read data
	 * @param url   the url to be read from
	 */
	public final void read(PlayerCharacter aPC, URL url)
	{
		InputStream in = null;
		try
		{
			in = url.openStream();
			read(aPC, in);
		}
		catch (IOException ex)
		{
			Globals.errorPrint("Exception in IOHandler::read when reading", ex);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				Globals.errorPrint("Exception in IOHandler::read", e);
			}
			catch (NullPointerException e)
			{
				Globals.errorPrint("Could not open InputStream inIOHandler::read", e);
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////
	////////////////////////// Capabilities of the handler //////////////////////
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * Whether this file handler supports reading from a file.
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @return <code>true</code>, if reading is supported;<br>
	 *         <code>false</code>, otherwise
	 */
	public static final boolean canRead()
	{
		return true;
	}

	/**
	 * Whether this file handler supports writing to a file.
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @return <code>true</code>, if writing is supported;<br>
	 *         <code>false</code>, otherwise
	 */
	public static final boolean canWrite()
	{
		return true;
	}
}
