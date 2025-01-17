package gmgen.io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
* This class is used to set the file filters in add/save dialogs.
* @author Expires 2003
* @version 2.10
*/
public class SimpleFileFilter extends FileFilter {

	/** The extensions of the wanted files. */
	private String[] extensions;

	/** The description to go along with the extensions. */
	private String description;

	/**
	 * Creates an instance of this class taking in an extension.
	 * @param ext a file extension.
	 */
	public SimpleFileFilter(String ext) {
		this (new String[] {ext}, null);
	}

	/**
	 * Creates an instance of this class given some extensions
	 * and a description.
	 * @param exts the used extensions.
	 * @param desc the description of the filter.
	 */
	public SimpleFileFilter(String[] exts, String desc){
		//clone and lowercase the extensions
		extensions = new String[exts.length];
		for(int i = exts.length-1; i >= 0; i--){
			extensions[i] = exts[i].toLowerCase();
		}
		desc = (desc == null ? "Unknown File" : desc);
		StringBuffer strbDesc = new StringBuffer(desc + " (");
		for(int i = 0; i < extensions.length; i++) {
			if(i > 0) {
				strbDesc.append(", ");
			}
			strbDesc.append("*." + extensions[i]);
		}
		strbDesc.append(")");
		description = strbDesc.toString();
	}

	/**
	 * Checks the file for validity.
	 * @param f the file that is chosen.
	 * @return true / false on user acceptance.
	 */
	public boolean accept(File f){
		//we always allow directories, regardless of their extension
		if (f.isDirectory()) { return true; }

		//ok if its a regular file, so check the extension
		String name = f.getName().toLowerCase();
		for(int i = extensions.length - 1; i >= 0; i --) {
			if (name.endsWith(extensions[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the description of the filter.
	 * @return the description.
	 */
	public String getDescription() { return description; }
}
