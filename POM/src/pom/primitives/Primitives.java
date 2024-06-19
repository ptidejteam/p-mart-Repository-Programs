/**
 * @author Farouk ZAIDI - 2004-02-09
 * zaidifar-program-MetricsByPrimitives
 *
 */

package pom.primitives;

import padl.kernel.IAbstractLevelModel;

/**
 * The class Primitives has been created to be a common class 
 * for different primitives. It is true that there is a few primitives. 
 * But it is a common basis for future sets of primitives.
 */
public class Primitives {
	protected IAbstractLevelModel abstractLevelModel;

	/**
	 * Constructor: handles the Idiom level model that allows to manipulate the program
	 * 
	 * @param primitiveIntrospector
	 */
	public Primitives(final IAbstractLevelModel anAbstactLevelModel) {
		this.abstractLevelModel = anAbstactLevelModel;
	}
}
