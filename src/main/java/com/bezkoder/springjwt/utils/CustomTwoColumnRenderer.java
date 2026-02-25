package com.bezkoder.springjwt.utils;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Two-column renderer that:
 * • honours a fixed header strip on page-1
 * • alternates left/right columns, then starts a new page
 */
public class CustomTwoColumnRenderer extends DocumentRenderer {

    private final float headerOffset;   // height of the header on page-1
    private final float columnWidth;
    private final float pageHeight;
    private final float gap = 20f;      // space between the two columns

    private int col = 0;                // 0 = left, 1 = right

    public CustomTwoColumnRenderer(Document doc,
                                   float headerOffset,
                                   float columnWidth,
                                   float pageHeight) {
        super(doc);
        this.headerOffset = headerOffset;
        this.columnWidth  = columnWidth;
        this.pageHeight   = pageHeight;
    }

    /* ────────────────────────────────────────────────
       Tell iText which rectangle is available next
       ──────────────────────────────────────────────── */
    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflow) {

        // first invocation → start on page-1, left column
        if (currentArea == null) {
            col = 0;
            return columnArea(1, col);
        }

        int page = currentArea.getPageNumber();

        // same page, switch column
        if (col == 0) {
            col = 1;
            return columnArea(page, col);
        }

        // we have filled both columns → go to next page, left column
        col = 0;
        return columnArea(page + 1, col);
    }

    private LayoutArea columnArea(int page, int column) {

        // top / bottom limits
        float top  = (page == 1 ? pageHeight - headerOffset : pageHeight - 40);
        float base = 40;

        // left edge of the rectangle
        float left = (column == 0)
                ? 40
                : 40 + columnWidth + gap;

        Rectangle rect = new Rectangle(left, base, columnWidth, top - base);
        return new LayoutArea(page, rect);
    }

    /* let iText clone the renderer when it needs a fresh one */
    @Override
    public IRenderer getNextRenderer() {
        return new CustomTwoColumnRenderer(document, headerOffset, columnWidth, pageHeight);
    }
}
