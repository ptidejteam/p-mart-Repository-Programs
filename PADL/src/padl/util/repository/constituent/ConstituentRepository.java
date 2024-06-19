/*
 * (c) Copyright 2001, 2002 Hervé Albin-Amiot and Yann-Gaël Guéhéneuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * Soft-Maint S.A.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHORS ARE ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.util.repository.constituent;

import padl.FileAccessException;
import padl.IFileRepository;
import util.io.Output;
import util.io.SubtypeLoader;
import com.ibm.toad.cfparse.ClassFile;

public class ConstituentRepository {
	private static ConstituentRepository UniqueInstance;
	public static ConstituentRepository getInstance(
		final IFileRepository fileRepository) {
		if (ConstituentRepository.UniqueInstance == null) {
			ConstituentRepository.UniqueInstance =
				new ConstituentRepository(fileRepository);
		}

		return ConstituentRepository.UniqueInstance;
	}
	private static String getKernelExtension() {
		return ".class";
	}
	private static String getKernelPackage() {
		return "padl.kernel.impl";
	}
	//	public static void main(final String args[]) {
	//		final ConstituentRepository typesRepository =
	//			ConstituentRepository.getCurrentConstituentRepository();
	//		OutputManager.getCurrentOutputManager().getNormalOutput().println(
	//			typesRepository);
	//	}

	private ClassFile[] elements;
	private ClassFile[] entities;
	private ConstituentRepository(final IFileRepository fileRepository) {
		try {
			this.entities =
				SubtypeLoader.loadSubtypesFromStream(
					"padl.kernel.IEntityMarker",
					fileRepository.getFiles(),
					ConstituentRepository.getKernelPackage(),
					ConstituentRepository.getKernelExtension());

			this.elements =
				SubtypeLoader.loadSubtypesFromStream(
					"padl.kernel.IElementMarker",
					fileRepository.getFiles(),
					ConstituentRepository.getKernelPackage(),
					ConstituentRepository.getKernelExtension());
		}
		catch (final FileAccessException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
	}
	public ClassFile[] getElements() {
		return this.elements;
	}
	public ClassFile[] getEntities() {
		return this.entities;
	}
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("Type Repository:\n\tEntities\n");
		for (int x = 0; x < this.getEntities().length; x++) {
			buffer.append("\t\t");
			buffer.append(this.getEntities()[x].getName());
			buffer.append('\n');
		}
		buffer.append("\tElements\n");
		for (int x = 0; x < this.getElements().length; x++) {
			buffer.append("\t\t");
			buffer.append(this.getElements()[x].getName());
			buffer.append('\n');
		}
		return buffer.toString();
	}
}