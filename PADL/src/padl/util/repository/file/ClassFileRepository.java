/*
 * (c) Copyright 2001-2004 Yann-Gaël Guéhéneuc,
 * University of Montréal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.util.repository.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;
import padl.FileAccessException;
import padl.IFileRepository;
import util.io.NamedInputStream;
import util.io.Output;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since  2004/07/21
 */
public class ClassFileRepository implements IFileRepository {
	private static IFileRepository UniqueInstance;
	private static Class ClassInTheDirectoryOfInterest;
	public static IFileRepository getInstance(
		final Class aClassInTheDirectoryOfInterest) {
		if (ClassFileRepository.UniqueInstance == null
				|| ClassFileRepository.ClassInTheDirectoryOfInterest != aClassInTheDirectoryOfInterest) {

			ClassFileRepository.ClassInTheDirectoryOfInterest =
				aClassInTheDirectoryOfInterest;
			ClassFileRepository.UniqueInstance = new ClassFileRepository();
		}

		return ClassFileRepository.UniqueInstance;
	}
	private static final void storeFiles(
		final File theCurrentDirectory,
		final List aListOfFiles) throws FileAccessException {

		final String[] files = theCurrentDirectory.list();
		if (files == null) {
			throw new FileAccessException();
		}
		for (int i = 0; i < files.length; i++) {
			final File file =
				new File(theCurrentDirectory.getAbsolutePath()
						+ File.separatorChar + files[i]);
			if (file.isFile()) {
				// Stephane 2008/07/16: Too many FIS open!
				// The following code closes unused FIS
				// to allow analysing large number of files.
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(file);
					aListOfFiles.add(new NamedInputStream(file
						.getCanonicalPath(), fileInputStream));
				}
				catch (final FileNotFoundException fnfe) {
					fnfe.printStackTrace(Output.getInstance().errorOutput());
				}
				catch (final IOException ioe) {
					ioe.printStackTrace(Output.getInstance().errorOutput());
				}
				finally {
					if (fileInputStream != null) {
						try {
							fileInputStream.close();
						}
						catch (final IOException ioe) {
							Output.getInstance().errorOutput().println(
								"Warning: cannot close file!");
						}
					}
				}
			}
			else {
				ClassFileRepository.storeFiles(file, aListOfFiles);
			}
		}
	}
	private static final NamedInputStream[] getMetaModelFiles(final Class aClass)
			throws FileAccessException {

		// Yann 2004/07/28: Demo!
		// I must catch the AccessControlException
		// thrown when attempting loading anything
		// from the applet viewer.
		try {
			final String directory =
				util.io.Files.getClassPath(aClass).replace('\\', '/');

			Output.getInstance().debugOutput().print("Accessing repository ");
			Output.getInstance().debugOutput().println(directory);

			final File directoryFile = new File(directory);
			final List listOfFiles = new ArrayList();
			ClassFileRepository.storeFiles(directoryFile, listOfFiles);
			final NamedInputStream[] arrayOfFiles =
				new NamedInputStream[listOfFiles.size()];
			listOfFiles.toArray(arrayOfFiles);
			return arrayOfFiles;
		}
		catch (final AccessControlException ace) {
			return new NamedInputStream[0];
		}
	}

	private NamedInputStream[] fileStreams;
	// TODO: Why is there both a Singleton and a public constructor?
	private ClassFileRepository() {
	}
	public NamedInputStream[] getFiles() throws FileAccessException {
		if (this.fileStreams == null) {
			this.fileStreams =
				ClassFileRepository
					.getMetaModelFiles(ClassFileRepository.ClassInTheDirectoryOfInterest);
		}
		return this.fileStreams;
	}
	public String toString() {
		return this.fileStreams.length + " files in repository.";
	}
}
