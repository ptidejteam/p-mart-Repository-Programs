/*
 * (c) Copyright 2001-2003 Yann-Gaël Guéhéneuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHORS ARE ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.util.repository.constituent;

import java.util.ArrayList;
import java.util.List;
import padl.FileAccessException;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import util.PropertyManager;
import util.io.Output;
import util.io.SubtypeLoader;
import com.ibm.toad.cfparse.ClassFile;

public class AbstractLevelRepository {
	private static AbstractLevelRepository UniqueInstance;
	private IAbstractLevelModel[] listOfAbstractLevels;

	public static AbstractLevelRepository getInstance(
		final IFileRepository fileRepository) {
		if (AbstractLevelRepository.UniqueInstance == null) {
			AbstractLevelRepository.UniqueInstance =
				new AbstractLevelRepository(fileRepository);
		}

		return AbstractLevelRepository.UniqueInstance;
	}
	//	public static void main(final String args[]) {
	//		final AbstractLevelRepository aAbstractLevelRepository =
	//			AbstractLevelRepository.getCurrentAbstractLevelRepository();
	//		OutputManager.getCurrentOutputManager().getNormalOutput().println(
	//			aAbstractLevelRepository.toString());
	//	}

	private AbstractLevelRepository(final IFileRepository fileRepository) {
		try {
			final ClassFile[] classFiles =
				SubtypeLoader.loadSubtypesFromStream(
					null,
					fileRepository.getFiles(),
					PropertyManager.getAbstractLevelsPackage(),
					PropertyManager.getAbstractLevelsExtension());
			final List listOfAbstractLevels = new ArrayList(classFiles.length);

			for (int i = 0; i < classFiles.length; i++) {
				try {
					listOfAbstractLevels.add((IAbstractLevelModel) Class
						.forName(classFiles[i].getName())
						.newInstance());
				}
				catch (final ClassNotFoundException cnfe) {
					cnfe.printStackTrace(Output.getInstance().errorOutput());
				}
				catch (final InstantiationException ie) {
					ie.printStackTrace(Output.getInstance().errorOutput());
				}
				catch (final IllegalAccessException iae) {
					iae.printStackTrace(Output.getInstance().errorOutput());
				}
			}

			this.listOfAbstractLevels =
				new IAbstractLevelModel[classFiles.length];
			listOfAbstractLevels.toArray(this.listOfAbstractLevels);
		}
		catch (final FileAccessException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
	}
	public IAbstractLevelModel[] listOfAbstractLevels() {
		return this.listOfAbstractLevels;
	}
	public void resetAbstractModel(int modelIndex) {
		try {
			this.listOfAbstractLevels[modelIndex] =
				(IAbstractLevelModel) this.listOfAbstractLevels[modelIndex]
					.getClass()
					.getConstructor(null)
					.newInstance(null);
		}
		catch (final Exception e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
	}
	public void resetAbstractModel(String aName) {
		for (int x = 0; x < this.listOfAbstractLevels.length; x++)
			if (this.listOfAbstractLevels[x].getName().equals(aName)) {
				this.resetAbstractModel(x);
				return;
			}
	}
	public void resetModels() {
		for (int x = 0; x < this.listOfAbstractLevels.length; this
			.resetAbstractModel(x++))
			;
	}
	public String toString() {
		final StringBuffer stringEq = new StringBuffer();
		stringEq.append("AbstractLevels Repository:\n");
		for (int x = 0; x < this.listOfAbstractLevels.length; x++) {
			stringEq.append('\t');
			stringEq.append(this.listOfAbstractLevels[x].getName());
			stringEq.append('\n');
		}
		return stringEq.toString();
	}
}