package padl.statement.creator.classfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import padl.analysis.IAnalysis;
import padl.analysis.UnsupportedSourceModelException;
import padl.kernel.IAbstractModel;
import ptidej.statement.creator.classfiles.conditionals.BCEL2PADLAdaptor;
import ptidej.statement.creator.classfiles.conditionals.BCELInstructionFinder;
import ptidej.statement.creator.classfiles.conditionals.InstructionSetter;
import util.io.Output;

/**
 * @author St�phane Vaucher
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2008/10/01
 * 
 * I built this annotator as a UI Viewer Extension but it 
 * could also be akin to a PADL Creator!
 * TODO: Make it similar to a Creator.
 */
public class ConditionalModelAnnotator implements IAnalysis {
	private final String[] fileNames;
	private final BCELInstructionFinder instFinder;

	public ConditionalModelAnnotator(final String[] someFileNames) {
		this.fileNames = someFileNames;
		this.instFinder = new BCELInstructionFinder();
		this.instFinder.setAdaptor(new BCEL2PADLAdaptor());
	}
	private void annotateFromFileOrDir(
		final String path,
		final IAbstractModel anAbstractModel) {

		// Yann 2006/03/09: Callback.
		// I make sure we can work on many directories at once.
		final File file = new File(path);
		if (file.isDirectory()) {
			final String[] paths = file.list();
			for (int i = 0; i < paths.length; i++) {
				final String newPath = path + '/' + paths[i];
				this.annotateFromFileOrDir(newPath, anAbstractModel);
			}
		}
		else if (path.endsWith(".class")) {
			try {
				final FileInputStream fis = new FileInputStream(path);
				final ClassParser parser = new ClassParser(fis, path);
				final JavaClass clazz = parser.parse();
				clazz.accept(this.instFinder);
				fis.close();
			}
			catch (final FileNotFoundException fnfe) {
				// Yann 2009/05/22: Windows...
				// It is possible that a file does not exist if its
				// path is greater than 256 characters... on Windows!
			}
			catch (final IOException ioe) {
				ioe.printStackTrace(Output.getInstance().errorOutput());
			}
			catch (final ClassFormatException cfe) {
				Output.getInstance().errorOutput().println(
					"This is most likely a bug in BCEL, cf. Google.");
				cfe.printStackTrace(Output.getInstance().errorOutput());
			}
		}
	}
	private void annotateFromJAR(
		final String jarFile,
		final IAbstractModel anAbstractModel) {

		try {
			if (new File(jarFile).exists()) {
				final JarFile jar = new JarFile(jarFile);
				final Enumeration enumeration = jar.entries();
				while (enumeration.hasMoreElements()) {
					final ZipEntry entry = (ZipEntry) enumeration.nextElement();

					if (!entry.isDirectory()
							&& entry.getName().endsWith(".class")) {

						final InputStream is = jar.getInputStream(entry);
						final ClassParser parser =
							new ClassParser(is, entry.getName());
						final JavaClass clazz = parser.parse();
						clazz.accept(this.instFinder);
						is.close();
					}
				}
			}
		}
		catch (final IOException ioe) {
			ioe.printStackTrace(Output.getInstance().errorOutput());
		}
	}
	public String getName() {
		return "Annotator with Conditionals (ClassFiles)";
	}
	public IAbstractModel invoke(final IAbstractModel anAbstractModel)
			throws UnsupportedSourceModelException {

		for (int i = 0; i < this.fileNames.length; i++) {
			final String fileName = this.fileNames[i];
			if (fileName.endsWith(".jar")) {
				this.annotateFromJAR(fileName, anAbstractModel);
			}
			else {
				this.annotateFromFileOrDir(fileName, anAbstractModel);
			}
		}
		//	else {
		//		Output.getInstance().errorOutput().print("Cannot understand: ");
		//		Output.getInstance().errorOutput().println(fileName);
		//	}
		anAbstractModel.walk(new InstructionSetter(this.instFinder));
		return anAbstractModel;
	}
}
