package pom.metrics;

import padl.kernel.IFirstClassEntity;

public interface IUnaryMetric extends IMetric {
	double compute(final IFirstClassEntity anEntity);
}
