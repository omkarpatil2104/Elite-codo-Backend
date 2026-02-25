package com.bezkoder.springjwt.services.pdfGenerated;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Two-column renderer that knows a fixed header height on page 1.
 * – Margins left/right/top/bottom come from the <code>Document</code> itself
 * – Column #0 = left  Column #1 = right
 */
class HeaderAwareTwoColumnRenderer extends DocumentRenderer {

    private final float headerH;   // reserved on page-1
    private final float columnW;   // single column width
    private final float pageH;     // full page height
    private final float gap = 20f; // space between the columns

    private int col = 0;           // 0 → left   1 → right

    HeaderAwareTwoColumnRenderer(Document doc,
                                 float headerHeight,
                                 float columnWidth,
                                 float pageHeight) {
        super(doc);
        this.headerH = headerHeight;
        this.columnW = columnWidth;
        this.pageH   = pageHeight;
    }

    /* alternate left / right; on overflow switch page & reset to left */
    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflow) {
        if (currentArea == null) {              // very first call
            col = 0;
            return createArea(1, col);
        }

        int page = currentArea.getPageNumber();

        if (col == 0) {                         // switch to right column
            col = 1;
            return createArea(page, col);
        }
        col = 0;                                // we were in right → new page
        return createArea(page + 1, col);
    }

    /** produce a rectangle for (page, column) respecting header strip */
    private LayoutArea createArea(int page, int column) {

        float margin = document.getTopMargin();     // all four margins identical
        float topY    = (page == 1)
                ? pageH - headerH - margin          // leave room for banner strip
                : pageH - margin;
        float bottomY = margin;

        if (topY <= bottomY)                        // defensive – never negative
            topY = bottomY + 10;

        float leftX  = (column == 0)
                ? margin
                : margin + columnW + gap;

        Rectangle area = new Rectangle(leftX, bottomY,
                columnW, topY - bottomY);
        return new LayoutArea(page, area);
    }

    @Override
    public IRenderer getNextRenderer() {
        return new HeaderAwareTwoColumnRenderer(document, headerH, columnW, pageH);
    }
}
