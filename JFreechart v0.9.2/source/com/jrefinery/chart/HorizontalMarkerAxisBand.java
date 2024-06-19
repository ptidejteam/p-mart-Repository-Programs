package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Insets;
import java.awt.Color;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Iterator;

public class HorizontalMarkerAxisBand {

    protected HorizontalNumberAxis axis;

    protected double topOuterGap;
    protected double topInnerGap;

    protected double bottomOuterGap;
    protected double bottomInnerGap;

    protected Font font;

    protected List markers;

    public HorizontalMarkerAxisBand(HorizontalNumberAxis axis,
                                    double topOuterGap, double topInnerGap,
                                    double bottomOuterGap, double bottomInnerGap, Font font) {
        this.axis = axis;
        this.topOuterGap = topOuterGap;
        this.topInnerGap = topInnerGap;
        this.bottomOuterGap = bottomOuterGap;
        this.bottomInnerGap = bottomInnerGap;
        this.font = font;
        this.markers = new java.util.ArrayList();
    }

    public void addMarker(Marker marker) {
        markers.add(marker);
    }

    public double getHeight(Graphics2D g2) {

        // calculate the height of the band...
        double result = 0.0;
        if (markers.size()>0) {
            LineMetrics metrics = font.getLineMetrics("123g", g2.getFontRenderContext());
            result = this.topOuterGap+this.topInnerGap+metrics.getHeight()
                    +this.bottomInnerGap+this.bottomOuterGap;
        }
        return result;

    }

    private void drawStringInRect(Graphics2D g2, Rectangle2D bounds, Font font, String text) {
        g2.setFont(font);
        Rectangle2D r = font.getStringBounds(text, g2.getFontRenderContext());
        double x = bounds.getX();
        if (r.getWidth()<bounds.getWidth()) {
            x = x+(bounds.getWidth()-r.getWidth())/2;
        }
        LineMetrics metrics = font.getLineMetrics(text, g2.getFontRenderContext());
        g2.drawString(text, (float)x, (float)(bounds.getMaxY()-this.bottomInnerGap
                                      -metrics.getDescent()));
    }

    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea,
                     double x, double y) {

        double h = getHeight(g2);
        Iterator iterator = this.markers.iterator();
        while (iterator.hasNext()) {
            IntervalMarker marker = (IntervalMarker)iterator.next();
            double start = Math.max(marker.getStartValue(), axis.getRange().getLowerBound());
            double end = Math.min(marker.getEndValue(), axis.getRange().getUpperBound());
            double s = axis.translateValueToJava2D(start, dataArea);
            double e = axis.translateValueToJava2D(end, dataArea);
            Rectangle2D r = new Rectangle2D.Double(s, y+topOuterGap, e-s, h-topOuterGap-bottomOuterGap);

            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.fill(r);
            g2.setPaint(marker.getOutlinePaint());
            g2.draw(r);
            g2.setComposite(originalComposite);

            g2.setPaint(Color.black);
            drawStringInRect(g2, r, this.font, marker.getLabel());
        }

    }

}