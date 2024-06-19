/*
 * LstSystemLoader.java
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
 * Created on September 22, 2003, 11:29 AM
 *
 * Current Ver: $Revision: 1.1 $ <br>
 * Last Editor: $Author: vauchers $ <br>
 * Last Edited: $Date: 2006/02/21 01:18:47 $ 
 */

package pcgen.persistence.lst;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.GuiFacade;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * This class is a base class for LST file loaders.  It is part of
 * a refactoring effort for LstSystemLoader.
 * 
 * NOTE:  This class is a work-in-progress!  It's
 * in CVS so it can be properly versioned and modified from
 * multiple locations.  
 * 
 * Unless your SF.Net login is sage_sam, keep your paws off it,
 * ya dirty ape!
 */
public class LstFileLoader
{

	/**
	 * Constructor for LstFileLoader.
	 */
	public LstFileLoader()
	{
		super();
	}

	/**
	 * This method loads the given list of LST files.
	 * @param fileList containing the list of files to read
	 * @throws PersistenceLayerException if there is a problem with the LST syntax
	 */
	public void loadLstFiles( List fileList ) throws PersistenceLayerException
	{
		// First sort the file list to optimize loads.
		sortFilesForOptimalLoad( fileList );
		
		// Load the files themselves as thoroughly as possible
		Iterator fileIter = fileList.iterator();
		while( fileIter.hasNext() )
		{
			String fileName = (String)fileIter.next();
			loadLstFile( fileName );
		}
		
		// Next we perform copy operations
		processCopies();
		
		// Now handle .MOD items
		processMods();
		
		// Finally, forget the .FORGET items
		processForgets();		
	}

	/**
	 * This method will sort the list of files into an order such that
	 * loads will be optimized.
	 * <br>
	 * Unless overridden, this method will sort files such that files
	 * to be loaded in entirety are loaded first, then files performing
	 * excludes of individual objects, then files including only specific 
	 * objects within the files.
	 * 
	 * @param fileList list of String file names to optimize
	 */
	protected void sortFilesForOptimalLoad(List fileList)
	{
	}


	/**
	 * This method will process the lines containing a .COPY directive
	 */
	private void processCopies() throws PersistenceLayerException
	{
		Iterator copyIter = copyLineList.iterator();
		while( copyIter.hasNext() )
		{
			performCopy( (String)copyIter.next() );
		}
	}

	/**
	 * This method, when implemented, will perform a single .COPY 
	 * operation.
	 * @param lstLine String containing the LST source for the 
	 * .COPY operation
	 */
	protected void performCopy(String lstLine)
	{
	}

	/**
	 * This method will process the lines containing a .FORGET directive
	 */
	private void processForgets() throws PersistenceLayerException
	{
		Iterator forgetIter = forgetLineList.iterator();
		while( forgetIter.hasNext() )
		{
			performForget( (String)forgetIter.next() );
		}
	}

	/**
	 * This method, when implemented, will perform a single .FORGET 
	 * operation.
	 * @param lstLine String containing the LST source for the 
	 * .FORGET operation
	 */
	protected void performForget(String string)
	{
	}

	/**
	 * This method will process the lines containing a .MOD directive
	 */
	private void processMods() throws PersistenceLayerException
	{
		Iterator modIter = modLineList.iterator();
		while( modIter.hasNext() )
		{
			performMod( (String)modIter.next() );
		}
	}

	/**
	 * This method, when implemented, will perform a single .MOD 
	 * operation.
	 * @param lstLine String containing the LST source for the 
	 * .MOD operation
	 */
	protected void performMod(String string)
	{
	}

	/**
	 * This method loads a single LST formatted file.
	 * @param fileName String containing the absolute file path 
	 * or the URL from which to read LST formatted data.
	 */
	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		StringBuffer dataBuffer = new StringBuffer();
		
		URL fileURL = readFileGetURL( fileName, dataBuffer );

		final String newlinedelim = "\r\n";
		final String aString = dataBuffer.toString();
		final StringTokenizer fileLines = new StringTokenizer(aString, newlinedelim);
		PObject target = null;
		
		while (fileLines.hasMoreTokens())
		{
			String line = fileLines.nextToken();
			
			// check for comments, copies, mods, and forgets
			if( line.indexOf(".COPY") > 0 )
			{
				copyLineList.add( line );
				target=null;
			}
			else if( line.indexOf(".MOD") > 0 )
			{
				modLineList.add( line );
				target=null;
			}
			else if( line.indexOf(".FORGET") > 0 )
			{
				forgetLineList.add( line );
				target=null;
			}
			else
			{				
				target = parseLine( target, line, fileURL);
			}
		}
		
		// finish the last item
		if( target!=null )
		{
			finishObject( target );
		}
	}

	/**
	 * This method is called when the end of data for a specific PObject
	 * is found, typically in order to add it to Globals.
	 * @param target PObject to perform final operations on
	 */
	protected void finishObject(PObject target)
	{
	}


	/**
	 * This method reads the given file or URL and stores its contents in the provided
	 * data buffer, returning a URL to the specified file for use in log/error
	 * messages by its caller.
	 * 
	 * @param argFileName String path of the file or URL to read
	 * @param dataBuffer StringBuffer to buffer the file content into
	 * @return URL pointing to the actual file read, for use in debug/log messages
	 * @throws PersistenceLayerException if an error occurs in reading the file
	 */
	public static URL readFileGetURL(final String argFileName, final StringBuffer dataBuffer)
		throws PersistenceLayerException
	{
		URL aURL=null;
		
		if (argFileName.length() <= 0)
		{
			// We have a problem!
			throw new PersistenceLayerException("LstSystemLoader.initFile() has a blank argFileName!");
		}

		final byte[] inputLine;
		InputStream inputStream = null;
		
		// Don't changes the slashes if this is a url.
		String fileName = argFileName;
		if (!Utility.isURL(fileName))
		{
			fileName = Utility.fixFilenamePath(fileName);
		}

		// Common case first - URL
		// because this includes file:/ - which most stuff gets translated to
		if (Utility.isURL(fileName))
		{
			aURL = readFromURL(fileName, dataBuffer);
		}
		//Uncommon case: Plain Old File Name
		else
		{
			aURL = readFromFile(fileName, dataBuffer);
		}

		return aURL;
	}

	/**
	 * This method reads the given file and stores its contents in the provided
	 * data buffer, returning a URL to the specified file for use in log/error
	 * messages by its caller.
	 * 
	 * @param argFileName String path of the file to read -- MUST be a file path, not a URL!
	 * @param dataBuffer StringBuffer to buffer the file content into
	 * @return URL pointing to the actual file read, for use in debug/log messages
	 * @throws PersistenceLayerException if an error occurs in reading the file
	 */
	private static URL readFromFile(String fileName, final StringBuffer dataBuffer)
		throws PersistenceLayerException
	{
		URL aURL = null;
		InputStream inputStream = null;

		final File aFile = new File(fileName);
		if (!aFile.exists())
		{
			// Arg! Bail!
			Logging.errorPrint(fileName + " doesn't seem to exist!");
			return null;
		}


		try
		{
			aURL = aFile.toURL();
			final int length = (int) aFile.length();
			inputStream = new FileInputStream(aFile);
			final byte[] inputLine = new byte[length];
			int bytesRead = inputStream.read(inputLine, 0, length);
			if (bytesRead != length)
			{
				Logging.errorPrint("Only read " + bytesRead + " bytes from " + fileName + " but expected " + length + " in LstSystemLoader.initFile. Continuing anyway");
			}
			dataBuffer.append( new String(inputLine) );
		}
		catch( IOException ioe )
		{
			aURL=null;
			
			// Convert and propogate the exception
			throw new PersistenceLayerException( 
				ioe, "ERROR:" + fileName + "\n" + "Exception type:" 
				+ ioe.getClass().getName() + "\n" + "Message:" + ioe.getMessage());
		}
		finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException e2)
				{
					Logging.errorPrint("Can't close inputStream in LstSystemLoader.initFile", e2);
				}
			}
		}
		
		return aURL;
	}

	/**
	 * This method reads the given URL and stores its contents in the provided
	 * data buffer, returning a URL to the specified file for use in log/error
	 * messages by its caller.
	 * 
	 * @param argFileName String path of the URL to read -- MUST be a URL path, not a file!
	 * @param dataBuffer StringBuffer to buffer the file content into
	 * @return URL pointing to the actual file read, for use in debug/log messages
	 * @throws PersistenceLayerException if an error occurs in reading the file
	 */
	private static URL readFromURL(String url, final StringBuffer dataBuffer)
		throws PersistenceLayerException
	{
		URL aURL = null;
		InputStream inputStream = null;

		try
		{
			//only load local urls, unless loading of URLs is allowed
			if (!Utility.isNetURL(url) || SettingsHandler.isLoadURLs())
			{
				aURL = new URL(url);

				inputStream = aURL.openStream();
				final InputStreamReader ir = new InputStreamReader(inputStream);
				final char[] b = new char[512];
				int n;
				while ((n = ir.read(b)) > 0)
				{
					dataBuffer.append(b, 0, n);
				}
				inputStream.close();
			}
			else
			{
				// Just to protect people from using web
				// sources without their knowledge,
				// we added a preference.
				GuiFacade.showMessageDialog(null, "Preferences are currently set to NOT allow\nloading of sources from web links. \n" + url + " is a web link" , Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
				aURL = null;
			}
		}
		catch( IOException ioe )
		{
			aURL=null;
			
			// Convert and propogate the exception
			throw new PersistenceLayerException( 
				ioe, "ERROR:" + url + "\n" + "Exception type:" 
				+ ioe.getClass().getName() + "\n" + "Message:" + ioe.getMessage());
		}
		finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException e2)
				{
					Logging.errorPrint("Can't close inputStream in LstSystemLoader.initFile", e2);
				}
			}
		}
		
		return aURL;
	}


	/**
	 * This method parses the LST file line, applying it to the provided target object.
	 * If the line indicates the start of a new target object, a new PObject of the appropriate
	 * type will be created prior to applying the line contents.  Because of this behavior,
	 * it is necessary for this method to return the new object.  Implementations of this method
	 * also MUST call finishObject with the original target prior to returning the new value.
	 * 
	 * @param lstLine String LST formatted line read from the source URL
	 * @param target PObject to apply the line to, barring the start of a new object
	 * @param sourceURL URL that the line was read from, for error reporting purposes
	 * @return PObject that was either created or modified by the provided LST line
	 * @throws if there is a problem with the LST syntax
	 */
	public PObject parseLine(PObject target, String lstLine, URL sourceURL) 
		throws PersistenceLayerException
	{
		return null;
	}

	private List copyLineList=new ArrayList();
	private List forgetLineList=new ArrayList();
	private List modLineList=new ArrayList();
}
