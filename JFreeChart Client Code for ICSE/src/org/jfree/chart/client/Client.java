package org.jfree.chart.client;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.BarRenderer;

public class Client {
	public static void main(final String[] args) {
		final Graphics2D g2 = null;
		final Rectangle2D dataArea = null;
		final CategoryPlot plot = null;
		final ChartRenderingInfo info = null;
		final AbstractCategoryItemRenderer renderer = new BarRenderer()
				.initialise(g2, dataArea, plot, info);
	}
}
