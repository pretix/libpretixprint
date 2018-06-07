package eu.pretix.libpretixprint.templating;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eu.pretix.libpretixprint.helpers.BarcodeQR;

import static com.itextpdf.text.Utilities.millimetersToPoints;

public class Layout {
    private JSONArray elements;
    private String background;
    private ContentProvider contentProvider;

    public Layout(JSONArray elements, String background, ContentProvider contentProvider) {
        this.elements = elements;
        this.background = background;
        this.contentProvider = contentProvider;
    }

    private void drawQrCode(JSONObject data, String text, PdfContentByte cb) throws IOException, DocumentException, JSONException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        BarcodeQR bqr = new BarcodeQR(
                text,
                (int) millimetersToPoints((float) data.getDouble("size")),
                hints
        );

        Image img = bqr.getImage();
        img.scaleAbsoluteHeight(millimetersToPoints((float) data.getDouble("size")));
        cb.addImage(
                img, img.getWidth(), 0, 0, img.getHeight(),
                millimetersToPoints((float) data.getDouble("left")),
                millimetersToPoints((float) data.getDouble("bottom"))
        );
    }

    private void drawTextarea(JSONObject data, String text, PdfContentByte cb) throws IOException, DocumentException, JSONException {
        FontSpecification.Style style = FontSpecification.Style.REGULAR;
        if (data.getBoolean("bold") && data.getBoolean("italic")) {
            style = FontSpecification.Style.BOLDITALIC;
        } else if (data.getBoolean("bold")) {
            style = FontSpecification.Style.BOLD;
        } else if (data.getBoolean("bold")) {
            style = FontSpecification.Style.ITALIC;
        }

        float fontsize = (float) data.getDouble("fontsize");

        FontRegistry fontRegistry = FontRegistry.getInstance();
        BaseFont baseFont = fontRegistry.get(data.getString("fontfamily"), style);
        Font font = new Font(baseFont, fontsize);

        font.setColor(
                data.getJSONArray("color").getInt(0),
                data.getJSONArray("color").getInt(1),
                data.getJSONArray("color").getInt(2)
        );

        ColumnText ct = new ColumnText(cb);

        text = text.replaceAll("<br[^>]*>", "\n");
        Paragraph para = new Paragraph(text, font);
        int alignment = 0;
        if (data.getString("align").equals("left")) {
            alignment = Element.ALIGN_LEFT;
        } else if (data.getString("align").equals("center")) {
            alignment = Element.ALIGN_CENTER;
        } else if (data.getString("align").equals("right")) {
            alignment = Element.ALIGN_RIGHT;
        }
        para.setAlignment(alignment);

        para.setLeading(fontsize);

        // Position with lower bound of "x" instead of lower bound of text, to be consistent with other implementations
        float ycorr = baseFont.getDescentPoint("x", fontsize) - baseFont.getDescentPoint(text, fontsize);

        // Simulate rendering to obtain real height
        ct.addElement(para);
        ct.setSimpleColumn(
                millimetersToPoints((float) data.getDouble("left")),
                millimetersToPoints((float) data.getDouble("bottom")) + ycorr,
                millimetersToPoints((float) (data.getDouble("left") + data.getDouble("width"))),
                millimetersToPoints((float) (data.getDouble("bottom") + 30)) + ycorr,
                millimetersToPoints(fontsize),
                alignment
        );
        ct.go(true);

        // Real rendering
        ct.addElement(para);
        ct.setSimpleColumn(
                millimetersToPoints((float) data.getDouble("left")),
                millimetersToPoints((float) data.getDouble("bottom")) + ycorr,
                millimetersToPoints((float) (data.getDouble("left") + data.getDouble("width"))),
                (float) (millimetersToPoints((float) (data.getDouble("bottom"))) + ct.getLinesWritten() * fontsize) + ycorr,
                millimetersToPoints(fontsize),
                alignment
        );
        ct.go();
    }

    public void render(String ouputFilename)
            throws Exception {
        PdfReader reader = new PdfReader(background);
        if (reader.getNumberOfPages() < 1) {
            throw new Exception("Background PDF does not have a first page.");
        }

        Document document = new Document(reader.getPageSize(1));
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(ouputFilename));
        document.open();

        PdfContentByte cb = writer.getDirectContent();

        // Copy background
        cb.addTemplate(writer.getImportedPage(reader, 1), 0, 0);

        for (int i = 0; i < elements.length(); i++) {
            JSONObject obj = elements.getJSONObject(i);
            if (obj.getString("type").equals("barcodearea")) {
                drawQrCode(obj, contentProvider.getBarcodeContent(obj.optString("content")), cb);
            } else if (obj.getString("type").equals("textarea")) {
                drawTextarea(obj, contentProvider.getTextContent(obj.getString("content"), obj.getString("text")), cb);
            }
        }
        document.close();
    }
}