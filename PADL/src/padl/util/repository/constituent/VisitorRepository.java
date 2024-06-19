package padl.util.repository.constituent;

import java.util.ArrayList;
import java.util.List;
import padl.FileAccessException;
import padl.IFileRepository;
import padl.kernel.IGenerator;
import padl.kernel.IWalker;
import util.PropertyManager;
import util.io.Output;
import util.io.SubtypeLoader;
import com.ibm.toad.cfparse.ClassFile;

public class VisitorRepository {
	private static VisitorRepository UniqueInstance;
	private IWalker[] listOfWalkers;
	private IGenerator[] listOfBuilders;

	public static VisitorRepository getInstance(
		final IFileRepository fileRepository) {
		if (VisitorRepository.UniqueInstance == null) {
			VisitorRepository.UniqueInstance =
				new VisitorRepository(fileRepository);
		}
		return VisitorRepository.UniqueInstance;
	}
	//	public static void main(final String args[]) {
	//		final VisitorRepository visitorsRepository =
	//			VisitorRepository.getCurrentVisitorRepository();
	//		OutputManager.getCurrentOutputManager().getNormalOutput().println(
	//			visitorsRepository.toString());
	//	}

	private VisitorRepository(final IFileRepository fileRepository) {
		try {
			final ClassFile[] classFiles =
				SubtypeLoader.loadSubtypesFromStream(
					null,
					fileRepository.getFiles(),
					PropertyManager.getPADLVisitorPackage(),
					PropertyManager.getPADLVisitorExtension());

			final List currentListOfBuilders = new ArrayList();
			final List currentListOfWalkers = new ArrayList();
			for (int i = 0; i < classFiles.length; i++) {
				try {
					final Class currentClass =
						Class.forName(classFiles[i].getName());
					if (IGenerator.class.isAssignableFrom(currentClass)) {
						Output.getInstance().normalOutput().print("Loading: ");
						Output.getInstance().normalOutput().println(
							currentClass);

						final IGenerator currentBuilder =
							(IGenerator) currentClass
								.getConstructor(null)
								.newInstance(null);

						currentListOfBuilders.add(currentBuilder);
					}
					if (IWalker.class.isAssignableFrom(currentClass)) {
						Output.getInstance().normalOutput().print("Loading: ");
						Output.getInstance().normalOutput().println(
							currentClass);

						final IWalker currentWalker =
							(IWalker) currentClass
								.getConstructor(null)
								.newInstance(null);

						currentListOfWalkers.add(currentWalker);
					}
				}
				catch (final Exception e) {
					e.printStackTrace(Output.getInstance().errorOutput());
				}
			}
			Output.getInstance().normalOutput().println();

			this.listOfBuilders = new IGenerator[currentListOfBuilders.size()];
			currentListOfBuilders.toArray(this.listOfBuilders);
			this.listOfWalkers = new IWalker[currentListOfWalkers.size()];
			currentListOfWalkers.toArray(this.listOfWalkers);
			Output.getInstance().normalOutput().println();
		}
		catch (final FileAccessException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
	}
	public IWalker[] listOfWalkers() {
		return this.listOfWalkers;
	}
	public IGenerator[] listOfBuilders() {
		return this.listOfBuilders;
	}

	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("Visitors Repository:\n");
		for (int x = 0; x < this.listOfBuilders.length; x++) {
			buffer.append('\t');
			buffer.append(this.listOfBuilders[x].getName());
			buffer.append('\n');
		}
		for (int x = 0; x < this.listOfWalkers.length; x++) {
			buffer.append('\t');
			buffer.append(this.listOfWalkers[x].getName());
			buffer.append('\n');
		}
		return buffer.toString();
	}
}