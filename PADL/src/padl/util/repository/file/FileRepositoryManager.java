package padl.util.repository.file;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import padl.IFileRepository;

public class FileRepositoryManager {
	private static final Map repositories = new HashMap();

	//	public static IFileRepository getDefaultRepository() {
	//		return DefaultFileRepository.getInstance();
	//	}

	public static IFileRepository getRepository(final Class clazz) {
		return (IFileRepository) repositories.get(clazz);
	}

	public static IFileRepository registerRepository(
		final Class clazz,
		final Collection resourceNames) {
		try {
			final ClassLoader cl = clazz.getClassLoader();

			// Check if run within env like eclipse
			//	Class platformClass =
			//		cl.loadClass("org.eclipse.core.runtime.Platform");
			Class workspaceClass =
				cl.loadClass("org.eclipse.core.resources.IWorkspace");
			Class resourcesClass =
				cl.loadClass("org.eclipse.core.resources.ResourcesPlugin");

			if (workspaceClass != null || resourcesClass != null) {
				// not in eclipse
				final Method getWorkspace =
					resourcesClass.getDeclaredMethod(
						"getWorkspace",
						new Class[0]);
				final Object o =
					getWorkspace.invoke(resourcesClass, new Object[] {});
				if (o != null) {
					// there is a workspace, we should use eclipse bundles
					final EclipseBundleRepository eclipseRepository =
						new EclipseBundleRepository(cl, resourceNames);
					repositories.put(clazz, eclipseRepository);
					return eclipseRepository;
				}
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
		repositories.put(clazz, ClassFileRepository.getInstance(clazz));
		return ClassFileRepository.getInstance(clazz);
	}
}
