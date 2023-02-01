package eu.pretix.libpretixprint.templating;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import eu.pretix.libpretixprint.helpers.BarcodeQR;
import eu.pretix.libpretixprint.helpers.EmbeddedLogos;
import eu.pretix.libpretixprint.helpers.StreamUtils;
import eu.pretix.libpretixprint.helpers.codec.binary.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Color;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.lowagie.text.Utilities.millimetersToInches;
import static com.lowagie.text.Utilities.millimetersToPoints;

public class Layout {
    private JSONArray elements;
    private InputStream backgroundInputStream;
    private Iterator<ContentProvider> contentProviders;
    private float default_width = 8f * 72f;
    private float default_height = 3.25f * 72f;

    public Layout(JSONArray elements, String background, Iterator<ContentProvider> contentProvider) throws FileNotFoundException {
        this(elements, new FileInputStream(background), contentProvider);
    }

    public Layout(JSONArray elements, InputStream background, Iterator<ContentProvider> contentProviders) {
        this.elements = elements;
        this.backgroundInputStream = background;
        this.contentProviders = contentProviders;
    }

    public float getDefaultWidth() {
        return default_width;
    }

    public void setDefaultWidth(float default_width) {
        this.default_width = default_width;
    }

    public float getDefaultHeight() {
        return default_height;
    }

    public void setDefaultHeight(float default_height) {
        this.default_height = default_height;
    }


    private void drawImage(JSONObject data, InputStream istream, PdfContentByte cb) throws IOException, DocumentException, JSONException {
        if (istream == null) return;
        Image img = Image.getInstance(StreamUtils.inputStreamToByteArray(istream));
        float width = millimetersToPoints((float) data.getDouble("width"));
        float height = millimetersToPoints((float) data.getDouble("height"));
        img.scaleToFit(width, height);
        float x = millimetersToPoints((float) data.getDouble("left"));
        float y = millimetersToPoints((float) data.getDouble("bottom"));
        if (img.getScaledWidth() < width) {
            x += (width - img.getScaledWidth()) / 2.0;
        }
        if (img.getScaledHeight() < height) {
            y += (height - img.getScaledHeight()) / 2.0;
        }
        cb.addImage(img, img.getScaledWidth(), 0, 0, img.getScaledHeight(), x, y);
    }

    private void drawPoweredBy(JSONObject data, String style, PdfContentByte cb) throws IOException, DocumentException, JSONException {
        String b64data = "";
        if (style.equals("white")) {
            b64data = EmbeddedLogos.POWERED_BY_PRETIX_WHITE;
        } else {
            b64data = EmbeddedLogos.POWERED_BY_PRETIX_DARK;
        }
        Image img = Image.getInstance(Base64.decodeBase64(b64data.getBytes(StandardCharsets.UTF_8)));
        float size = millimetersToPoints((float) data.getDouble("size"));
        img.scalePercent(size * 100f / img.getPlainHeight());
        cb.addImage(
                img, img.getScaledWidth(), 0, 0, img.getScaledHeight(),
                millimetersToPoints((float) data.getDouble("left")),
                millimetersToPoints((float) data.getDouble("bottom"))
        );
    }

    private void drawQrCode(JSONObject data, String text, boolean nowhitespace, PdfContentByte cb) throws IOException, DocumentException, JSONException {
        Map<EncodeHintType, Object> hints = new HashMap<>();

        // Heuristic to avoid tiny pixel sizes if we can. Error correction doesn't help us below printer resolution ;)
        if (text.length() > 128) {
            // Typical size for external or long signed codes
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        } else if (text.length() > 32) {
            // Typical size for regular signed codes
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        } else {
            // Typical size for random codes
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        }

        BarcodeQR bqr;
        if (nowhitespace) {
            // default rendering starting from pretix 4.0
            hints.put(EncodeHintType.MARGIN, 0);
            bqr = new BarcodeQR(
                    text,
                    (int) (millimetersToInches((float) data.getDouble("size")) * 600),  // Render barcode at 600 dpi
                    hints
            );
        } else {
            // legacy rendering
            bqr = new BarcodeQR(
                    text,
                    (int) millimetersToPoints((float) data.getDouble("size")),
                    hints
            );
        }
        float size = millimetersToPoints((float) data.getDouble("size"));
        Image img = bqr.getImage();
        img.scaleToFit(size, size);
        cb.addImage(
                img, img.getScaledWidth(), 0, 0, img.getScaledHeight(),
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
        } else if (data.getBoolean("italic")) {
            style = FontSpecification.Style.ITALIC;
        }

        float fontsize = (float) data.getDouble("fontsize");

        FontRegistry fontRegistry = FontRegistry.getInstance();
        BaseFont baseFont = fontRegistry.get(data.getString("fontfamily"), style);
        if (baseFont == null) {
            System.out.print("Unable to load font " + data.getString("fontfamily"));
            baseFont = fontRegistry.get("Open Sans", style);
        }
        Font font = new Font(baseFont, fontsize);

        font.setColor(
                data.getJSONArray("color").getInt(0),
                data.getJSONArray("color").getInt(1),
                data.getJSONArray("color").getInt(2)
        );

        ColumnText ct = new ColumnText(cb);

        text = text.replaceAll("<br[^>]*>", "\n");
        text = Normalizer.normalize(text, Normalizer.Form.NFKC);
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

        float lineheight;
        if (data.has("lineheight")) {
            lineheight = (float) (data.getDouble("lineheight") * 1.15);
        } else {
            lineheight = 1;
        }
        para.setLeading(lineheight * fontsize);

        // Position with lower bound of "x" instead of lower bound of text, to be consistent with other implementations
        float ycorr = baseFont.getDescentPoint("x", fontsize) - baseFont.getDescentPoint(text, fontsize);

        // Simulate rendering to obtain real height
        ct.addElement(para);
        ct.setSimpleColumn(
                millimetersToPoints((float) data.getDouble("left")),
                millimetersToPoints((float) data.getDouble("bottom")) + ycorr,
                millimetersToPoints((float) (data.getDouble("left") + data.getDouble("width"))),
                millimetersToPoints((float) (data.getDouble("bottom") + 3000)) + ycorr,
                fontsize,
                alignment
        );
        ct.go(true);

        // Real rendering
        // We need to take into account the actual height of the text as well as put in some buffer for floating
        // point rounding, otherwise lines might go missing.
        cb.saveState();

        double alpha = -1D * data.optDouble("rotation", 0D) * 3.141592653589793D / 180.0D;
        float cos = (float) Math.cos(alpha);
        float sin = (float) Math.sin(alpha);

        float adheight = (float) (Math.max(fontsize, baseFont.getAscentPoint(text, fontsize) - baseFont.getDescentPoint(text, fontsize)) + 0.0001);
        float adheightPlusLH = adheight;
        if (lineheight != 1) {
            adheightPlusLH += (lineheight - 1.0) * fontsize;
        }
        ct.addElement(para);
        float lowerLeftY = millimetersToPoints((float) (data.getDouble("bottom")));
        float upperRightY = millimetersToPoints((float) (data.getDouble("bottom"))) + ct.getLinesWritten() * adheightPlusLH;
        float lowerLeftX = millimetersToPoints((float) data.getDouble("left"));
        float ycorrtop = baseFont.getAscentPoint("X", fontsize) - baseFont.getAscentPoint(text, fontsize);
        if (data.optBoolean("downward", false)) {
            lowerLeftY = millimetersToPoints((float) (data.getDouble("bottom"))) - ct.getLinesWritten() * adheightPlusLH;
            upperRightY = millimetersToPoints((float) data.getDouble("bottom"));
            ycorr = 0;

            if (lineheight != 1) {
                ycorr += (lineheight - 1.0) * fontsize;
            }

        } else {
            ycorrtop = 0;
        }
        cb.concatCTM(cos, sin, -sin, cos, lowerLeftX, upperRightY);

        ct.setSimpleColumn(
                0,
                lowerLeftY - upperRightY - ycorrtop + ycorr,
                millimetersToPoints((float) data.getDouble("width")),
                -ycorrtop + ycorr,
                fontsize,
                alignment
        );
        ct.go();

        /*
        Uncomment the following if you want to see the bounding box while debuggign
        cb.rectangle(
                0,
                lowerLeftY - upperRightY - ycorrtop + ycorr,
                millimetersToPoints((float) data.getDouble("width")),
                -ycorrtop + ycorr - (lowerLeftY - upperRightY - ycorrtop + ycorr)
        );
        cb.setColorStroke(Color.RED);
        cb.stroke();
        */

        cb.restoreState();
    }

    private void drawWhiteBackground(Rectangle pageSize, PdfContentByte cb) throws DocumentException {
        Rectangle r = new Rectangle(pageSize);
        r.setBackgroundColor(Color.WHITE);
        r.setBorder(Rectangle.NO_BORDER);
        cb.rectangle(r);
    }

    public void render(String filename)
            throws Exception {
        render(new FileOutputStream(filename));
    }

    public void render(OutputStream os)
            throws Exception {
        Document document;
        PdfReader reader = null;
        if (backgroundInputStream != null) {
            reader = new PdfReader(backgroundInputStream);
            if (reader.getNumberOfPages() < 1) {
                throw new Exception("Background PDF does not have a first page.");
            }

            document = new Document(reader.getPageSize(1));
        } else {
            document = new Document(new RectangleReadOnly(
                    default_width,
                    default_height
            ));
        }
        int pagecount = reader != null ? reader.getNumberOfPages() : 1;
        boolean firstPage = true;
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();

        PdfContentByte cb = writer.getDirectContent();

        while (contentProviders.hasNext()) {
            ContentProvider cp = contentProviders.next();

            for (int pagenum = 0; pagenum < pagecount; pagenum++) {
                if (firstPage) {
                    firstPage = false;
                } else {
                    document.newPage();
                }

                if (reader != null) {
                    document.setPageSize(reader.getPageSize(pagenum + 1));
                }
                drawWhiteBackground(document.getPageSize(), cb);
                if (reader != null) {
                    cb.addTemplate(writer.getImportedPage(reader, pagenum + 1), 0, 0);
                }

                for (int i = 0; i < elements.length(); i++) {
                    JSONObject obj = elements.getJSONObject(i);
                    if (obj.optInt("page", 1) != pagenum + 1) {
                        continue;
                    }
                    if (obj.getString("type").equals("barcodearea")) {
                        String content = cp.getBarcodeContent(obj.optString("content"), obj.optString("text", ""), obj.optJSONObject("text_i18n"));
                        if (content.isEmpty()) {
                            content = " ";  // ZXing crashes when asked to draw empty QR codes
                        }
                        drawQrCode(obj, content, obj.optBoolean("nowhitespace", false), cb);
                    } else if (obj.getString("type").equals("textarea")) {
                        drawTextarea(obj, cp.getTextContent(obj.getString("content"), obj.optString("text", ""), obj.optJSONObject("text_i18n")), cb);
                    } else if (obj.getString("type").equals("imagearea")) {
                        drawImage(obj, cp.getImageContent(obj.getString("content")), cb);
                    } else if (obj.getString("type").equals("poweredby")) {
                        drawPoweredBy(obj, obj.getString("content"), cb);
                    }
                }
            }
        }
        document.close();
    }
}
