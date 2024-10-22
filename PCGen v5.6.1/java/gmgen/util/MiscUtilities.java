/*
 *  MiscUtilities.java - Various miscallaneous utility functions
 *  :noTabs=false:
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit, Copyright (C) 1999, 2003 Slava Pestov,
 *    Portions copyright (C) 2000 Richard S. Hall,
 *    Portions copyright (C) 2001 Dirk Moebius
 *  Derived from PCGen, Copyright (C) 2000, 2002, 2003, Bryan McRoberts
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gmgen.util;

import gmgen.GMGenSystem;
import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import pcgen.core.Globals;
import pcgen.util.Logging;

/**
 *  Misc Utilities, all static.  Will create and store a private static instance
 *  of itself that reads in properties and localization strings
 *
 *@author     Devon Jones
 *@created    May 30, 2003
 *@since        GMGen 3.3
 */
public class MiscUtilities {
	private static MiscUtilities inst = new MiscUtilities();
	private static Properties options;
	private static Properties localization;


	private MiscUtilities() {
		localization = new Properties();
		readLocalizationProperties();
	}


	/**
	 *  Returns if the specified path name is an absolute path or URL.
	 *
	 *@param  path  a path
	 *@return       The absolute path
	 *@since        GMGen 3.3
	 */
	public static boolean isAbsolutePath(String path) {
		if (isURL(path)) {
			return true;
		}
		else if (path.startsWith("~/") || path.startsWith("~" + File.separator) || path.equals("~")) {
			return true;
		}
		else if (OperatingSystem.isDOSDerived()) {
			if (path.length() == 2 && path.charAt(1) == ':') {
				return true;
			}
			if (path.length() > 2 && path.charAt(1) == ':' && path.charAt(2) == '\\') {
				return true;
			}
			if (path.startsWith("\\\\")) {
				return true;
			}
		}
		else if (OperatingSystem.isUnix()) {
			// nice and simple
			if (path.length() > 0 && path.charAt(0) == '/') {
				return true;
			}
		}
		return false;
	}


	/**
	 *  Returns the canonical form of the specified path name. Currently only
	 *  expands a leading <code>~</code>. <b>For local path names only.</b>
	 *
	 *@param  path  The path name
	 *@return       the canonical form of the specified path name
	 *@since        GMGen 3.3
	 */
	public static String canonPath(String path) {
		if (path.startsWith("file://")) {
			path = path.substring("file://".length());
		}
		else if (path.startsWith("file:")) {
			path = path.substring("file:".length());
		}
		else if (isURL(path)) {
			return path;
		}

		if (File.separatorChar == '\\') {
			// get rid of mixed paths on Windows
			path = path.replace('/', '\\');
		}

		if (path.startsWith("~" + File.separator)) {
			path = path.substring(2);
			String home = System.getProperty("user.home");

			if (home.endsWith(File.separator)) {
				return home + path;
			}
			else {
				return home + File.separator + path;
			}
		}
		else if (path.equals("~")) {
			return System.getProperty("user.home");
		}
		else {
			return path;
		}
	}


	/**
	 *  Constructs an absolute path name from a directory and another path name.
	 *  This method is VFS-aware.
	 *
	 *@param  parent  The directory
	 *@param  path    The path name
	 *@return         the absolute path name
	 *@since          GMGen 3.3
	 */
	public static String constructPath(String parent, String path) {
		if (isAbsolutePath(path)) {
			return canonPath(path);
		}

		// have to handle this case specially on windows.
		// insert \ between, eg A: and myfile.txt.
		if (OperatingSystem.isDOSDerived()) {
			if (path.length() == 2 && path.charAt(1) == ':') {
				return path;
			}
			else if (path.length() > 2 && path.charAt(1) == ':' && path.charAt(2) != '\\') {
				path = path.substring(0, 2) + '\\' + path.substring(2);
				return canonPath(path);
			}
		}

		String dd = ".." + File.separator;
		String d = "." + File.separator;

		if (parent == null) {
			parent = Globals.getDefaultPath();
		}

		//DJ: This sucks, this also needs to be fixed
		for (; ; ) {
			if (path.equals(".")) {
				return parent;
			}
			else if (path.equals("..")) {
				return getParentOfPath(parent);
			}
			else if (path.startsWith(dd) || path.startsWith("../")) {
				parent = getParentOfPath(parent);
				path = path.substring(3);
			}
			else if (path.startsWith(d)) {
				path = path.substring(2);
			}
			else {
				break;
			}
		}

		if (OperatingSystem.isDOSDerived() && path.startsWith("\\")) {
			parent = parent.substring(0, 2);
		}

		if (!path.endsWith("\\") && !path.endsWith("/")) {
			parent += File.separator;
		}

		return parent + path;
	}


	/**
	 *  Constructs an absolute path name from three path components. This method is
	 *  VFS-aware.
	 *
	 *@param  parent  The parent directory
	 *@param  path1   The first path
	 *@param  path2   The second path
	 *@return         the absolute path name
	 *@since          GMGen 3.3
	 */
	public static String constructPath(String parent, String path1, String path2) {
		return constructPath(constructPath(parent, path1), path2);
	}


	/**
	 *  Returns the parent of the specified path.
	 *
	 *@param  path  The path name
	 *@return       The parentOfPath value
	 *@since        GMGen 3.3
	 */
	public static String getParentOfPath(String path) {
		// ignore last character of path to properly handle
		// paths like /foo/bar/
		int count = Math.max(0, path.length() - 2);
		int index = path.lastIndexOf(File.separatorChar, count);
		if (index == -1) {
			index = path.lastIndexOf('/', count);
		}
		if (index == -1) {
			// this ensures that getFileParent("protocol:"), for
			// example, is "protocol:" and not "".
			index = path.lastIndexOf(':');
		}

		return path.substring(0, index + 1);
	}


	/**
	 *  Checks if the specified string is a URL.
	 *
	 *@param  str  The string to check
	 *@return      True if the string is a URL, false otherwise
	 *@since       GMGen 3.3
	 */
	public static boolean isURL(String str) {
		int fsIndex = Math.max(str.indexOf(File.separatorChar), str.indexOf('/'));
		if (fsIndex == 0) {
			// /etc/passwd
			return false;
		}
		else if (fsIndex == 2) {
			// C:\AUTOEXEC.BAT
			return false;
		}

		int cIndex = str.indexOf(':');
		if (cIndex <= 1) {
			// D:\WINDOWS
			return false;
		}
		else if (fsIndex != -1 && cIndex > fsIndex) {
			// /tmp/RTF::read.pm
			return false;
		}
		return true;
	}


	/**
	 *  Returns the file name component of the specified path.
	 *
	 *@param  path  The path
	 *@return       The file name
	 *@since        GMGen 3.3
	 */
	public static String getFileName(String path) {
		if (path.equals("/")) {
			return path;
		}

		if (path.endsWith("/") || path.endsWith(File.separator)) {
			path = path.substring(0, path.length() - 1);
		}

		int index = Math.max(path.lastIndexOf('/'), path.lastIndexOf(File.separatorChar));
		if (index == -1) {
			index = path.indexOf(':');
		}

		// don't want getFileName("roots:") to return ""
		if (index == -1 || index == path.length() - 1) {
			return path;
		}
		return path.substring(index + 1);
	}


	/**
	 *  Converts a class name to a file name. All periods are replaced with slashes
	 *  and the '.class' extension is added.
	 *
	 *@param  name  The class name
	 *@return       the file name
	 *@since        GMGen 3.3
	 */
	public static String classToFile(String name) {
		return name.replace('.', '/').concat(".class");
	}


	/**
	 *  Converts a file name to a class name. All slash characters are replaced
	 *  with periods and the trailing '.class' is removed.
	 *
	 *@param  name  The file name
	 *@return       the class name
	 *@since        GMGen 3.3
	 */
	public static String fileToClass(String name) {
		char[] clsName = name.toCharArray();
		for (int i = clsName.length - 6; i >= 0; i--) {
			if (clsName[i] == '/') {
				clsName[i] = '.';
			}
		}
		return new String(clsName, 0, clsName.length - 6);
	}


	/**
	 *  Reads the localization properties file.
	 *@since        GMGen 3.3
	 */
	public static void readLocalizationProperties() {
		try {
			FileInputStream in = new FileInputStream("/pcgen/gui/prop/LanguageBundle.properties");
			localization.load(in);
		}
		catch (IOException e) {
			Logging.errorPrint("No localization file found.");
		}
	}


	/**
	 *  Sets the any property values that need to be set on close
	 *@since        GMGen 3.3
	 */
	public static void setOptionsProperties() {
		//Set any properties that need to be set based on the last run.
	}


	/**
	 *  Returns the localization property with the specified name, formatting it
	 *  with the <code>java.text.MessageFormat.format()</code> method.
	 *
	 *@param  name  The localization property
	 *@param  args  The positional parameters
	 *@return       The localization value
	 *@since        GMGen 3.3
	 */
	public static final String getLocalization(String name, Object[] args) {
		if (name == null) {
			return null;
		}
		if (args == null) {
			return localization.getProperty(name);
		}
		else {
			String value = localization.getProperty(name);
			if (value == null) {
				return null;
			}
			else {
				return MessageFormat.format(value, args);
			}
		}
	}


	/**
	 *  A more intelligent version of String.compareTo() that handles numbers
	 *  specially. For example, it places "My file 2" before "My file 10".
	 *
	 *@param  str1        The first string
	 *@param  str2        The second string
	 *@param  ignoreCase  If true, case will be ignored
	 *@return             negative If str1 &lt; str2, 0 if both are the same,
	 *                    positive if str1 &gt; str2
	 */
	public static int compareStrings(String str1, String str2, boolean ignoreCase) {
		char[] char1 = str1.toCharArray();
		char[] char2 = str2.toCharArray();

		int len = Math.min(char1.length, char2.length);

		for (int i = 0, j = 0; i < len && j < len; i++, j++) {
			char ch1 = char1[i];
			char ch2 = char2[j];
			if (Character.isDigit(ch1) && Character.isDigit(ch2) && ch1 != '0' && ch2 != '0') {
				int _i = i + 1;
				int _j = j + 1;

				for (; _i < char1.length; _i++) {
					if (!Character.isDigit(char1[_i])) {
						break;
					}
				}

				for (; _j < char2.length; _j++) {
					if (!Character.isDigit(char2[_j])) {
						break;
					}
				}

				int len1 = _i - i;
				int len2 = _j - j;
				if (len1 > len2) {
					return 1;
				}
				else if (len1 < len2) {
					return -1;
				}
				else {
					for (int k = 0; k < len1; k++) {
						ch1 = char1[i + k];
						ch2 = char2[j + k];
						if (ch1 != ch2) {
							return ch1 - ch2;
						}
					}
				}

				i = _i - 1;
				j = _j - 1;
			}
			else {
				if (ignoreCase) {
					ch1 = Character.toLowerCase(ch1);
					ch2 = Character.toLowerCase(ch2);
				}
				if (ch1 != ch2) {
					return ch1 - ch2;
				}
			}
		}
		return char1.length - char2.length;
	}


	/**
	 *  Converts an internal version number (build) into a `human-readable' form.
	 *
	 *@param  build  The build number
	 *@return        The Formatted Version Number
	 */
	public static String buildToVersion(String build) {
		StringTokenizer bt = new StringTokenizer(build, ".");

		// First 2 chars are the major version number
		int major = 0;
		if(bt.hasMoreTokens()) {
			major = Integer.parseInt(bt.nextToken());
		}

		// Second 2 are the minor number
		int minor = 0;
		if(bt.hasMoreTokens()) {
			minor = Integer.parseInt(bt.nextToken());
		}

		// Then the pre-release status
		int beta = 0;
		if(bt.hasMoreTokens()) {
			beta = Integer.parseInt(bt.nextToken());
		}

		// Finally the bug fix release
		int rc = 0;
		if(bt.hasMoreTokens()) {
			rc = Integer.parseInt(bt.nextToken());
		}

		// Finally the bug fix release
		int bugfix = 0;
		if(bt.hasMoreTokens()) {
			bugfix = Integer.parseInt(bt.nextToken());
		}

		return "" + major + "." + minor + (beta != 99 ? "pre" + beta : (rc != 99 ? "rc" + rc : (bugfix != 0 ? "." + bugfix : "final")));
	}

	/**
	 *  Set the cursor for the specified component to the wait cursor
	 *
	 *@param  component  The component to set the cursor for
	 *@return        The currently set cursor
	 */
	public static Cursor setBusyCursor(java.awt.Component component) {
		Cursor old = component.getCursor();
		component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		return old;
	}  // end setBusyCursor

	/**
	 *  Set the cursor the the specified component to the specified cursor
	 *
	 *@param  component  The component to set the cursor for
	 *@param  cursor  The cursor to set
	 */
	public static void setCursor(java.awt.Component component, Cursor cursor) {
		component.setCursor(cursor);
	}  // end setCursor

	// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
	// Copyright (c) 1997 by David Flanagan
	// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
	// You may study, use, modify, and distribute it for non-commercial purposes.
	// For any commercial use, see http://www.davidflanagan.com/javaexamples

	/**
		* The static method that actually performs the file copy.
		* Before copying the file, however, it performs a lot of tests to make
		* sure everything is as it should be.
		*/
	public static void copy(File from_file, File to_file) throws IOException {
		// First make sure the source file exists, is a file, and is readable.
		if (!from_file.exists()) {
			throw new IOException("FileCopy: no such source file: " + from_file.getPath());
		}
		if (!from_file.isFile()) {
			throw new IOException("FileCopy: can't copy directory: " + from_file.getPath());
		}
		if (!from_file.canRead()) {
			throw new IOException("FileCopy: source file is unreadable: " + from_file.getPath());
		}

		// If the destination is a directory, use the source file name
		// as the destination file name
		if (to_file.isDirectory()) {
			to_file = new File(to_file, from_file.getName());
		}

		// If the destination exists, make sure it is a writeable file
		// and ask before overwriting it.  If the destination doesn't
		// exist, make sure the directory exists and is writeable.
		if (to_file.exists()) {
			if (!to_file.canWrite()) {
				throw new IOException("FileCopy: destination file is unwriteable: " + to_file.getPath());
			}
			// Ask whether to overwrite it
			int choice = JOptionPane.showConfirmDialog(GMGenSystem.inst, "Overwrite existing file " + to_file.getPath(), "File Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION) {
				throw new IOException("FileCopy: existing file was not overwritten.");
			}
		}
		else {
			// if file doesn't exist, check if directory exists and is writeable.
			// If getParent() returns null, then the directory is the current dir.
			// so look up the user.dir system property to find out what that is.
			String parent = to_file.getParent();  // Get the destination directory
			if (parent == null) {
				parent = Globals.getDefaultPath(); // or CWD
			}
			File dir = new File(parent);          // Convert it to a file.
			if (!dir.exists()) {
				throw new IOException("FileCopy: destination directory doesn't exist: " + parent);
			}
			if (dir.isFile()) {
				throw new IOException("FileCopy: destination is not a directory: " + parent);
			}
			if (!dir.canWrite()) {
				throw new IOException("FileCopy: destination directory is unwriteable: " + parent);
			}
		}

		// If we've gotten this far, then everything is okay.
		// So we copy the file, a buffer of bytes at a time.
		FileInputStream from = null;  // Stream to read from source
		FileOutputStream to = null;   // Stream to write to destination
		try {
			from = new FileInputStream(from_file);  // Create input stream
			to = new FileOutputStream(to_file);     // Create output stream
			byte[] buffer = new byte[4096];         // A buffer to hold file contents
			int bytes_read;                         // How many bytes in buffer
			// Read a chunk of bytes into the buffer, then write them out,
			// looping until we reach the end of the file (when read() returns -1).
			// Note the combination of assignment and comparison in this while
			// loop.  This is a common I/O programming idiom.
			while((bytes_read = from.read(buffer)) != -1) { // Read bytes until EOF
				to.write(buffer, 0, bytes_read);              //   write bytes
			}
		}
		// Always close the streams, even if exceptions were thrown
		finally {
			if (from != null) try { from.close(); } catch (IOException e) { ; }
			if (to != null) try { to.close(); } catch (IOException e) { ; }
		}
	}

	public static String expandRelativePath(String path) {
		if(path.startsWith("@")) {
			path = Globals.getDefaultPath() + File.separator + path.substring(1);
		}
		return path;
	}

	// becasue user.dir changes to the pcgen dir, this doesn't work as well as it did in pcgen - we need to get tiso it functions correctly.
	public static String retractRelativePath(String path) {
		File systemDir = new File(Globals.getDefaultPath());
		if(path.startsWith(systemDir.getAbsolutePath())) {
			path = "@" + path.substring(systemDir.getAbsolutePath().length());
		}
		return path;
	}
}

