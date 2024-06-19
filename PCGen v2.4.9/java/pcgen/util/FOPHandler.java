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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.fop.render.Renderer;
import org.apache.fop.render.awt.AWTRenderer;
import org.apache.fop.viewer.SecureResourceBundle;
import org.xml.sax.InputSource;

/**
 * Title:        FOPHandler.java
 * Description:  Interface to the Apache FOP API;
 *               this class handles all the interaction
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public class FOPHandler implements Runnable
{

	public static final int PDF_MODE = 0;
	public static final int AWT_MODE = 1;

	private Driver driver;

	private File inFile;
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
		inFile = null;
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
	 *
	 */
	public void setInputFile(File in)
	{
		inFile = in;
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

	public void render()
	{
		Thread t = new Thread(this);
		t.start();
	}

	/**
	 *
	 */
	public void run()
	{
		errBuffer.delete(0, errBuffer.length());

		try
		{
			if (!inFile.exists())
			{
				throw new FileNotFoundException(inFile.getAbsolutePath() + " (File not found.)");
			}
		}
		catch (FileNotFoundException fnfex)
		{
			errBuffer.append(fnfex.getMessage() + "\n");
			fnfex.printStackTrace();
			return;
		}

		// setting up driver
		driver.reset();

		if (mode == PDF_MODE)
		{

			fos = null;
			renderer = null;
			driver.setRenderer(Driver.RENDER_PDF);

			try
			{

				driver.setInputSource(new InputSource(
					new FileInputStream(inFile)));
				driver.setOutputStream(fos = new FileOutputStream(outFile));

				// render
				driver.run();

//  				fos.close();

			}
			catch (FOPException fopex)
			{
//                                  errBuffer.append( "An error ocurred:\n" );
				errBuffer.append(fopex.getMessage() + "\n");
				fopex.printStackTrace();
			}
			catch (IOException ioex)
			{
//                                  errBuffer.append( "An error ocurred:\n" );
				errBuffer.append(ioex.getMessage() + "\n");
				ioex.printStackTrace();
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
//                                                  errBuffer.append( "An error ocurred:\n" );
						errBuffer.append(ioex.getMessage() + "\n");
						ioex.printStackTrace();
					}
				}
			}

		}
		else if (mode == AWT_MODE)
		{

			renderer = createAWTRenderer();
			driver.setRenderer(renderer);

			try
			{

				driver.setInputSource(new InputSource(
					new FileInputStream(inFile)));

				// render
				driver.run();

			}
			catch (FOPException fopex)
			{
				errBuffer.append(fopex.getMessage() + "\n");
				fopex.printStackTrace();
			}
			catch (IOException ioex)
			{
				errBuffer.append(ioex.getMessage() + "\n");
				ioex.printStackTrace();
			}

		}
		else
		{
			System.out.println("Unsupported mode for file export.");
		}

	}

	/**
	 * author: Thomas Behr 25-02-02
	 */
	private AWTRenderer createAWTRenderer()
	{
		byte bytes[] = new byte[0];
		return new AWTRenderer(new SecureResourceBundle(
			new ByteArrayInputStream(bytes)));
	}
}
