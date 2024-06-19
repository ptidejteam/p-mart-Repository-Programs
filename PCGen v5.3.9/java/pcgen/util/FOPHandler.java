/*
 * FOPHandler.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOInputHandler;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.InputHandler;
import org.apache.fop.apps.XSLTInputHandler;
import org.apache.fop.render.Renderer;
import org.apache.fop.render.awt.AWTRenderer;
import org.apache.fop.viewer.SecureResourceBundle;
import org.xml.sax.XMLReader;
import pcgen.core.Constants;

/**
 * Title:        FOPHandler.java
 * Description:  Interface to the Apache FOP API;
 *               this class handles all the interaction
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public final class FOPHandler implements Runnable
{

	public static final int PDF_MODE = 0;
	public static final int AWT_MODE = 1;

	private Driver driver;

	//private File inFile;
	private InputHandler inputHandler;
	private File outFile;
	private FileOutputStream fos;

	private int mode;

	private Renderer renderer;

	private StringBuffer errBuffer;

	/**
	 *
	 */
	public FOPHandler()
	{
		driver = new Driver();
		inputHandler = null;
		outFile = null;
		mode = PDF_MODE;

		errBuffer = new StringBuffer();

		// renderers
		renderer = null;
	}

	/**
	 *
	 */
	public String getErrorMessage()
	{
		return errBuffer.toString();
	}

	/**
	 *
	 */
	public void setMode(int m)
	{
		mode = m;
	}

	/**
	 * Dekker500
	 * Feb 1, 2003
	 * Immediately convert file into appropriatee InputHandler type
	 */
	public void setInputFile(File in)
	{
		try
		{
			if (in == null)
			{
				throw new NullPointerException("XML file must be specified for the tranform mode");
			}
			if (!in.exists())
			{
				throw new FileNotFoundException("xml file "
					+ in.getAbsolutePath()
					+ " not found ");
			}
			inputHandler = new FOInputHandler(in);
		}
		catch (FileNotFoundException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.s_LINE_SEP);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
	}

	/**
	 * Dekker500
	 * Feb 1, 2003
	 * Immediately convert file into appropriatee InputHandler type
	 */
	public void setInputFile(File xmlFile, File xsltFile)
	{
		try
		{
			if (xmlFile == null)
			{
				throw new NullPointerException("XML file must be specified for the tranform mode");
			}
			if (xsltFile == null)
			{
				throw new NullPointerException("XSLT file must be specified for the tranform mode");
			}

			if (!xmlFile.exists())
			{
				throw new FileNotFoundException("xml file "
					+ xmlFile.getAbsolutePath()
					+ " not found ");
			}
			if (!xsltFile.exists())
			{
				throw new FileNotFoundException("xsl file "
					+ xsltFile.getAbsolutePath()
					+ " not found ");
			}
			inputHandler = new XSLTInputHandler(xmlFile, xsltFile);
		}
		catch (FileNotFoundException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.s_LINE_SEP);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
		catch (NullPointerException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.s_LINE_SEP);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
		catch (FOPException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.s_LINE_SEP);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
	}

	/**
	 *
	 */
	public void setOutputFile(File out)
	{
		outFile = out;
	}

	/**
	 *
	 */
	public Renderer getRenderer()
	{
		return renderer;
	}

	/**
	 *
	 */

	public void run()
	{
		errBuffer.delete(0, errBuffer.length());

		// setting up driver
		driver.reset();

		if (mode == PDF_MODE)
		{

			fos = null;
			renderer = null;
			driver.setRenderer(Driver.RENDER_PDF);

			/**
			 * Dekker500
			 * Feb 1, 2003
			 * Expanded functionality to be able to handle XSLT files.
			 * Now operates based on InputHandlers.
			 */

			try
			{
				XMLReader parser = inputHandler.getParser();

				Hashtable rendererOptions = new Hashtable();
				rendererOptions.put("fineDetail", new Boolean(false));
				driver.getRenderer().setOptions(rendererOptions);
				driver.getRenderer().setProducer("PC Gen Character Generator");

				driver.setOutputStream(fos = new FileOutputStream(outFile));

				// render
				driver.render(parser, inputHandler.getInputSource());

			}
			catch (FOPException fopex)
			{
				errBuffer.append(fopex.getMessage()).append(Constants.s_LINE_SEP);
				Logging.errorPrint("Exception in FOPHandler:run", fopex);
			}
			catch (IOException ioex)
			{
				errBuffer.append(ioex.getMessage()).append(Constants.s_LINE_SEP);
				Logging.errorPrint("Exception in FOPHandler:run", ioex);
			}
			finally
			{
				if (fos != null)
				{
					try
					{
						fos.close();
					}
					catch (IOException ioex)
					{
						errBuffer.append(ioex.getMessage()).append(Constants.s_LINE_SEP);
						Logging.errorPrint("Exception in FOPHandler:run", ioex);
					}
				}
			}

		}
		else if (mode == AWT_MODE)
		{

			renderer = createAWTRenderer();
			driver.setRenderer(renderer);
/*			Hashtable rendererOptions = new Hashtable();
			rendererOptions.put("fineDetail", new Boolean(false));
			driver.getRenderer().setOptions(rendererOptions);
			driver.getRenderer().setProducer("PC Gen Character Generator");
*/
			try
			{
				XMLReader parser;
				parser = inputHandler.getParser();
//				parser.setFeature("http://xml.org/sax/features/namespace-prefixes",	true);
				// render
//				driver.buildFOTree(parser, inputHandler.getInputSource());
				driver.render(parser, inputHandler.getInputSource());
			}
			catch (FOPException fopex)
			{
				errBuffer.append(fopex.getMessage()).append(Constants.s_LINE_SEP);
				Logging.errorPrint("Exception in FOPHandler:run", fopex);
			}
/*			catch (IOException ioex)
			{
				errBuffer.append(ioex.getMessage()).append(Constants.s_LINE_SEP);
				Globals.errorPrint("Exception in FOPHandler:run", ioex);
			}
*/
/*			catch (SAXException ex)
			{
				errBuffer.append(ex.getMessage()).append(Constants.s_LINE_SEP);
				Globals.errorPrint("Exception in FOPHandler:run \n"
					+ "Error in setting up parser feature namespace-prefixes\n"
					+ "You need a parser which supports SAX version 2", ex);
			}
*/
		}
		else
		{
			Logging.errorPrint("Unsupported mode for file export.");
		}

	}

	/**
	 * author: Thomas Behr 25-02-02
	 */
	private static AWTRenderer createAWTRenderer()
	{
		final byte[] bytes = new byte[0];
		return new AWTRenderer(new SecureResourceBundle(
			new ByteArrayInputStream(bytes)));
	}
}
