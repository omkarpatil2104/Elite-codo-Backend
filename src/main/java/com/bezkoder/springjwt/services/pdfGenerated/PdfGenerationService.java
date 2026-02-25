package com.bezkoder.springjwt.services.pdfGenerated;

import com.bezkoder.springjwt.dto.PdfRequestDTO;
import com.bezkoder.springjwt.dto.QuestionDTO;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.kernel.geom.*;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

@Service
public class PdfGenerationService {

    private static final float PAGE_MARGIN   = 40f;
    private static final float BANNER_SPACER = 120f;
    private static final float QA_SPACING    = 10f;

    public byte[] buildPaper(PdfRequestDTO dto) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter   writer = new PdfWriter(baos);
            PdfDocument pdf    = new PdfDocument(writer);
            Document    doc    = new Document(pdf, PageSize.A4);

            // 1) set uniform margins
            doc.setMargins(PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN);

            // 2) two-column renderer on all pages
            if (dto.isTwoColumn()) {
                float colW    = (PageSize.A4.getWidth() - PAGE_MARGIN*2 - 10) / 2f;
                float usableH = PageSize.A4.getHeight() - PAGE_MARGIN*2;
                Rectangle[] cols = {
                        new Rectangle(PAGE_MARGIN, PAGE_MARGIN, colW, usableH),
                        new Rectangle(PAGE_MARGIN+colW+10, PAGE_MARGIN, colW, usableH)
                };
                doc.setRenderer(new ColumnDocumentRenderer(doc, cols));

                pdf.addEventHandler(PdfDocumentEvent.END_PAGE,
                        new ColumnSeparatorHandler(cols[0].getRight() + 5));
            }

            // 3) banner+logo+meta+subject ribbon on page 1
            pdf.addEventHandler(PdfDocumentEvent.START_PAGE,
                    new BannerHeader(dto));

            // 4) optional watermark
            if (dto.getWatermark() != null && !dto.getWatermark().isEmpty()) {
                ImageData wm = loadImage(dto.getWatermark());
                pdf.addEventHandler(PdfDocumentEvent.END_PAGE,
                        new WatermarkHandler(wm, dto.getAngle(), dto.getOpacity()));
            }

            // 5) spacer below banner in BOTH columns
           /* -----------------------------------------------------------
   Pad BOTH columns on the first page so content starts below
   the header. The dummy 0-height paragraph prevents iText
   from skipping straight to page-2.
   ----------------------------------------------------------- */
//            if (dto.isTwoColumn()) {
//                // area-1 : spacer
//                doc.add(new Paragraph().setHeight(BANNER_SPACER));
//
//                // area-2 : spacer
//                doc.add(new AreaBreak(AreaBreakType.NEXT_AREA));
//                doc.add(new Paragraph().setHeight(BANNER_SPACER));
//
//                // one invisible line ==> marks area-2 as used
//                doc.add(new Paragraph().setMargin(0).setPadding(0).setFontSize(0));
//
//                // back to normal flow (area-3 = left column, same page)
//                doc.add(new AreaBreak(AreaBreakType.NEXT_AREA));
//            } else {
                // single-column version: just one spacer
                doc.add(new Paragraph().setHeight(BANNER_SPACER));
//            }


            // 6) questions
            int idx = 1;
            for (QuestionDTO q : dto.getQuestions()) {
                addQuestion(doc, idx++, q);
            }

            doc.close();
            return baos.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException("PDF build failed", e);
        }
    }

    private void addQuestion(Document doc, int n, QuestionDTO q) throws IOException {
        String html =
                "<table style='width:100%;font-family:Helvetica;'>"
                        + "<tr><td><b>" + n + ")</b> " + strip(q.getQuestion()) + "</td></tr>"
                        + "<tr><td style='padding-left:20px;'>(a) " + strip(q.getOption1()) + "</td></tr>"
                        + "<tr><td style='padding-left:20px;'>(b) " + strip(q.getOption2()) + "</td></tr>"
                        + "<tr><td style='padding-left:20px;'>(c) " + strip(q.getOption3()) + "</td></tr>"
                        + "<tr><td style='padding-left:20px;'>(d) " + strip(q.getOption4()) + "</td></tr>"
                        + "</table>";

        List<IElement> els = HtmlConverter.convertToElements(
                new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)),
                new ConverterProperties());

        for (IElement e : els) {
            if (e instanceof IBlockElement) {
                doc.add((IBlockElement) e);
            }
        }
        doc.add(new Paragraph().setMarginBottom(QA_SPACING));
    }

    private static class BannerHeader implements IEventHandler {
        private static final Map<String,String> LOGOS = new HashMap<>();
        static {
            LOGOS.put("PHOENIX",
                    "https://zplusglobalmarketinsights.com/auctionBanksImages/image%20(1).png");
            LOGOS.put("CHINTAMANI",
                    "https://zplusglobalmarketinsights.com/auctionBanksImages/Screenshot%202025-05-19%20173311.png");
            LOGOS.put("TEST_PLANNERS",
                    "https://zplusglobalmarketinsights.com/auctionBanksImages/image%20(2).png");
        }

        private final PdfRequestDTO dto;
        private final PdfFont font;
        private final ImageData logo;

        BannerHeader(PdfRequestDTO dto) throws Exception {
            this.dto  = dto;
            this.font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            String key = dto.getInstitute() == null ? "" : dto.getInstitute().toUpperCase();
            String url = LOGOS.get(key);
            this.logo = url == null ? null : loadImage(url);
        }

        @Override
        public void handleEvent(Event ev) {
            PdfDocumentEvent event = (PdfDocumentEvent) ev;
            if (event.getDocument().getPageNumber(event.getPage()) != 1) return;

            Rectangle pg   = event.getPage().getPageSize();
            PdfCanvas cvs  = new PdfCanvas(event.getPage());
            float y        = pg.getTop() - 25;

            // 1) logo
            if (logo != null) {
                Image img = new Image(logo).scaleToFit(pg.getWidth()-80, 120f);
                img.setFixedPosition((pg.getWidth()-img.getImageScaledWidth())/2f,
                        y - img.getImageScaledHeight());
                new Canvas(cvs, pg).add(img);
                y -= img.getImageScaledHeight() + 6;
            }

            // 2) meta line
            Paragraph meta = new Paragraph()
                    .setFont(font).setFontSize(11)
                    .add(text("Date :", dto.getDate()))
                    .add(text("Time :", dto.getDuration()))
                    .add(text("Exam :", dto.getExamName()))
                    .add(text("Mark :", String.valueOf(dto.getTotalMarks())));
            new Canvas(cvs, pg)
                    .showTextAligned(meta, pg.getWidth()/2f, y, TextAlignment.CENTER);

            // 3) horizontal rule
            cvs.setLineWidth(1)
                    .moveTo(pg.getLeft()+40, y-6)
                    .lineTo(pg.getRight()-40, y-6)
                    .stroke();
            y -= 10;

            // 4) subject ribbon box
            String subject = dto.getInstitute() == null ? "PHYSICS" : "PHYSICS";
            float boxH = 20f,
                    boxL = pg.getLeft() + 40,
                    boxR = pg.getRight() - 40;
            cvs.setLineWidth(1)
                    .rectangle(boxL, y - boxH, boxR - boxL, boxH)
                    .stroke();

            // subject text inside the box
            new Canvas(cvs, pg)
                    .showTextAligned(new Paragraph(subject)
                                    .setFont(font)
                                    .setFontSize(12),
                            pg.getWidth()/2f, y - boxH/2f, TextAlignment.CENTER);
        }

        private Text text(String k, String v) {
            return new Text(k + " " + v + "    ");
        }
    }

    private static class WatermarkHandler implements IEventHandler {
        private final ImageData img;
        private final float angle, opacity;
        WatermarkHandler(ImageData img, float angle, float opacity) {
            this.img = img; this.angle = angle; this.opacity = opacity;
        }
        @Override
        public void handleEvent(Event ev) {
            PdfDocumentEvent event = (PdfDocumentEvent) ev;
            PdfCanvas under = new PdfCanvas(
                    event.getPage().newContentStreamBefore(),
                    event.getPage().getResources(),
                    event.getDocument());
            under.setExtGState(new PdfExtGState().setFillOpacity(opacity));

            Rectangle pg = event.getPage().getPageSize();
            float cx = (pg.getLeft()+pg.getRight())/2f,
                    cy = (pg.getBottom()+pg.getTop())/2f;

            Image w = new Image(img)
                    .setFixedPosition(cx - img.getWidth()/2f,
                            cy - img.getHeight()/2f)
                    .setRotationAngle((float)Math.toRadians(angle))
                    .setOpacity(opacity);
            new Canvas(under, pg).add(w);
        }
    }

    private static class ColumnSeparatorHandler implements IEventHandler {
        private final float x;
        ColumnSeparatorHandler(float x){this.x = x;}
        @Override
        public void handleEvent(Event ev) {
            PdfDocumentEvent event = (PdfDocumentEvent) ev;
            Rectangle pg = event.getPage().getPageSize();
            new PdfCanvas(event.getPage())
                    .setLineWidth(1)
                    .setStrokeColor(DeviceRgb.BLACK)
                    .moveTo(x, pg.getBottom() + PAGE_MARGIN)
                    .lineTo(x, pg.getTop()    - PAGE_MARGIN)
                    .stroke();
        }
    }

    private static String strip(String html){
        if (html == null) return "";
        org.jsoup.nodes.Document d = Jsoup.parse(html);
        for (Element p : d.select("p")) p.unwrap();
        return d.body().html();
    }
    private static ImageData loadImage(String src) throws IOException {
        if (src.startsWith("http")) {
            try (InputStream in = new URL(src).openStream()) {
                return ImageDataFactory.create(StreamUtils.copyToByteArray(in));
            }
        }
        String b64 = src.replaceFirst("^data:[^,]+,", "");
        return ImageDataFactory.create(Base64.getDecoder().decode(b64));
    }
}
