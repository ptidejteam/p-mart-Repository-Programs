
package gmgen.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.Serializable;

public class GridBoxLayout extends GridLayout implements Serializable {
	public GridBoxLayout() {
		this(1, 0, 0, 0);
	}

	public GridBoxLayout(int rows, int cols) {
		this(rows, cols, 0, 0);
	}

	public GridBoxLayout(int rows, int cols, int hgap, int vgap) throws IllegalArgumentException {
		super(rows, cols, hgap, vgap);
	}

	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int nComponents = parent.getComponentCount();
			int nRows = getRows();
			int nCols = getColumns();

			if (nRows > 0) {
				nCols = (nComponents + nRows - 1) / nRows;
			}
			else {
				nRows = (nComponents + nCols - 1) / nCols;
			}

			int[] widths = new int[nCols];
			int[] heights = new int[nRows];
			int c;
			int r;

			for (c = 0; c < nCols; c++) {
				widths[c] = Integer.MIN_VALUE;
			}
			for (r = 0; r < nRows; r++) {
				heights[r] = Integer.MIN_VALUE;
			}

			c = 0;
			r = 0;
			for (int i = 0; i < nComponents; i++) {
				Component comp = parent.getComponent(i);
				Dimension d = comp.getPreferredSize();
				if (widths[c] < d.width) {
					widths[c] = d.width;
				}
				if (heights[r] < d.height) {
					heights[r] = d.height;
				}
				if (++c >= nCols) {
					c = 0;
					r++;
				}
			}

			int w = 0;
			int h = 0;
			for (c = 0; c < nCols; c++) {
				w += widths[c];
			}
			for (r = 0; r < nRows; r++) {
				h += heights[r];
			}

			return new Dimension(insets.left + insets.right + w + (nCols - 1) * getHgap(),
			 insets.top + insets.bottom + h + (nRows - 1) * getVgap());
    }
  }

	public Dimension minimumLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int nComponents = parent.getComponentCount();
			int nRows = getRows();
			int nCols = getColumns();
	
			if (nRows > 0) {
				nCols = (nComponents + nRows - 1) / nRows;
			}
			else {
				nRows = (nComponents + nCols - 1) / nCols;
			}
	
			int[] widths = new int[nCols];
			int[] heights = new int[nRows];
			int c;
			int r;
	
			for (c = 0; c < nCols; c++) {
				widths[c] = Integer.MIN_VALUE;
			}
			for (r = 0; r < nRows; r++) {
				heights[r] = Integer.MIN_VALUE;
			}
	
			c = 0;
			r = 0;
			for (int i = 0; i < nComponents; i++) {
				Component comp = parent.getComponent(i);
				Dimension d = comp.getMinimumSize();
				if (widths[c] < d.width) {
					widths[c] = d.width;
				}
				if (heights[r] < d.height) {
					heights[r] = d.height;
				}
				if (++c >= nCols) {
					c = 0;
					r++;
				}
			}
	
			int w = 0;
			int h = 0;
			for (c = 0; c < nCols; c++) {
				w += widths[c];
			}
			for (r = 0; r < nRows; r++) {
				h += heights[r];
			}
	
			return new Dimension(insets.left + insets.right + w + (nCols - 1) * getHgap(),
			 insets.top + insets.bottom + h + (nRows - 1) * getVgap());
		}
  }

	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int nComponents = parent.getComponentCount();
			int nRows = getRows();
			int nCols = getColumns();
	
			if (nComponents == 0) {
				return;
			}
	
			if (nRows > 0) {
				nCols = (nComponents + nRows - 1) / nRows;
			}
			else {
				nRows = (nComponents + nCols - 1) / nCols;
			}
	
			int[] widths = new int[nCols];
			int[] heights = new int[nRows];
			int c;
			int r;
	
			for (c = 0; c < nCols; c++) {
				widths[c] = Integer.MIN_VALUE;
			}
			for (r = 0; r < nRows; r++) {
				heights[r] = Integer.MIN_VALUE;
			}
	
			c = 0;
			r = 0;
			for (int i = 0; i < nComponents; i++) {
				Component comp = parent.getComponent(i);
				Dimension d = comp.getPreferredSize();
				if (widths[c] < d.width) {
					widths[c] = d.width;
				}
				if (heights[r] < d.height) {
					heights[r] = d.height;
				}
				if (++c >= nCols) {
					c = 0;
					r++;
				}
			}
	
			int w = 0;
			int h = 0;
			for (c = 0; c < nCols; c++) {
				w += widths[c];
			}
			for (r = 0; r < nRows; r++) {
				h += heights[r];
			}
			
			Dimension pDim = parent.getSize();
	
			int pWidth = pDim.width - (insets.left + insets.right) - (nCols -	1) * getHgap();
			int pHeight = pDim.height - (insets.top + insets.bottom) - (nRows -	1) * getVgap();
	
			float widthProportion = (float) pWidth / w;
			float heightProportion = (float) pHeight / h;
	
			for (c = 0; c < nCols; c++) {
				widths[c] = (int) (widthProportion * widths[c]);
			}
			for (r = 0; r < nRows; r++) {
				heights[r] = (int) (heightProportion * heights[r]);
			}
	
			int comp = 0;
			int y = insets.top;
			outer:
			for (r = 0; r < nRows; r++) {
				int x = insets.left;
				for (c = 0; c < nCols; c++) {
					if (comp >= nComponents) {
						break outer;
					}
					parent.getComponent(comp++).setBounds(x, y, widths[c], heights[r]);
					x += widths[c] + getHgap();
				}
				y += heights[r] + getVgap();
			}
		}
	}
}
