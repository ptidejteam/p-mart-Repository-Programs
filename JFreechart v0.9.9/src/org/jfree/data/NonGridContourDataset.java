package org.jfree.data;

/**
 * A convenience class that extends the {@link DefaultContourDataset} to acommadate non-grid data.
 */
//dmo: added Class (added)
public class NonGridContourDataset extends DefaultContourDataset {

    final int DEFAULT_NUM_X = 50;
    final int DEFAULT_NUM_Y = 50;
    final int DEFAULT_POWER = 4;

    /**
     * Constructor for NonGridContourDataset.
     */
    public NonGridContourDataset() {
        super();
    }

    /**
     * Constructor for NonGridContourDataset.  Uses default values for grid dimensions and 
     * weighting.
     * 
     * @param seriesName
     * @param xData
     * @param yData
     * @param zData
     */
    public NonGridContourDataset(String seriesName, 
                                 Object[] xData, Object[] yData, Object[] zData) {
        super(seriesName, xData, yData, zData);
        buildGrid(DEFAULT_NUM_X, DEFAULT_NUM_Y, DEFAULT_POWER);
    }

    /**
     * Constructor for NonGridContourDataset.
     * @param seriesName
     * @param xData
     * @param yData
     * @param zData
     * @param number grid cells in along the x-axis
     * @param number grid cells in along the y-axis
     * @param exponent for inverse distance weighting
     */
    public NonGridContourDataset(String seriesName, Object[] xData, Object[] yData, Object[] zData,
                                  int numX, int numY, int power) {
        super(seriesName, xData, yData, zData);
        buildGrid(numX, numY, power);
    }

    /**
     * Builds a regular grid.  Maps the non-grid data into the regular grid using an
     * inverse distance between grid and non-grid points.  Weighting of distance can
     * be controlled by setting through the power parameter that controls the exponent
     * used on the distance weighting (e.g., distance^power).
     * @param number grid points in along the x-axis
     * @param number grid points in along the y-axis
     * @param exponent for inverse distance weighting
     */
    protected void buildGrid(int numX, int numY, int power) {

        double[] xGrid = null;
        double[] yGrid = null;
        double[] zGrid = null;

        int numValues = numX * numY;
        xGrid = new double[numValues];
        yGrid = new double [numValues];
        zGrid = new double [numValues];

// Find min, max for the x and y axes
        double xMin = 1.e20;
        for (int k = 0; k < xValues.length; k++) {
            xMin = Math.min(xMin, xValues[k].doubleValue());
        }

        double xMax = -1.e20;
        for (int k = 0; k < xValues.length; k++) {
            xMax = Math.max(xMax, xValues[k].doubleValue());
        }

        double yMin = 1.e20;
        for (int k = 0; k < yValues.length; k++) {
            yMin = Math.min(yMin, yValues[k].doubleValue());
        }

        double yMax = -1.e20;
        for (int k = 0; k < yValues.length; k++) {
            yMax = Math.max(yMax, yValues[k].doubleValue());
        }


// Remove below
//      double addedY = 1*(yMax - yMin);
//      double addedX = 1*(xMax - xMin);

//      xMin -= addedX;
//      yMin -= addedY;
//      xMax += addedX;
//      yMax += addedY;
// Remove above

        Range xRange = new Range(xMin, xMax);
        Range yRange = new Range(yMin, yMax);

        xRange.getLength();
        yRange.getLength();

// Determine the cell size
        double dxGrid = xRange.getLength() / (numX - 1);
        double dyGrid = yRange.getLength() / (numY - 1);

// Generate the grid
        double x = 0.0;
        for (int i = 0; i < numX; i++) {
            if (i == 0) {
                x = xMin;
            }
            else {
                x += dxGrid;
            }
            double y = 0.0;
            for (int j = 0; j < numY; j++) {
                int k = numY * i + j;
                xGrid[k] = x;
                if (j == 0) {
                    y = yMin;
                }
                else {
                    y += dyGrid;
                }
                yGrid[k] = y;
            }
        }

// Map the nongrid data into the new regular grid
        for (int kGrid = 0; kGrid < xGrid.length; kGrid++) {
            double dTotal = 0.0;
            zGrid[kGrid] = 0.0;
            for (int k = 0; k < xValues.length; k++) {
                double xPt = xValues[k].doubleValue();
                double yPt = yValues[k].doubleValue();
                double d = distance(xPt, yPt, xGrid[kGrid], yGrid[kGrid]);
                if (power != 1) {
                    d = Math.pow(d, power);
                }
                d = Math.sqrt(d);
                if (d > 0.0) {
                    d = 1.0 / d;
                }
                else { // if d is real small set the inverse to a large number to avoid INF
                    d = 1.e20;
                }
                if (zValues[k] != null) {
                    // scale by the inverse of distance^power
                    zGrid[kGrid] += zValues[k].doubleValue() * d; 
                }
                dTotal += d;
            }
            zGrid[kGrid] = zGrid[kGrid] / dTotal;               //remove distance of the sum
        }

//initalize xValues, yValues, and zValues arrays.
        initialize(formObjectArray(xGrid), formObjectArray(yGrid), formObjectArray(zGrid));

    }

    /**
     * Calculates the distance between two points.
     * 
	 * @param xDataPt 
	 * @param yDataPt
	 * @param xGrdPt
	 * @param yGrdPt
     * 
	 * @return The distance between two points.
	 */
    protected double distance(double xDataPt, double yDataPt, double xGrdPt, double yGrdPt) {
        double dx = xDataPt - xGrdPt;
        double dy = yDataPt - yGrdPt;
        return Math.sqrt(dx * dx + dy * dy);
    }

}
