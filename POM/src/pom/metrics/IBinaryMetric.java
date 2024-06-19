package pom.metrics;

import padl.kernel.IFirstClassEntity;

public interface IBinaryMetric extends IMetric {
	double compute(final IFirstClassEntity anEntityA, final IFirstClassEntity anEntityB);
	boolean isSymmetrical();
}
