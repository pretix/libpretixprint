package eu.pretix.libpretixprint.templating;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.SortedMap;

public class FontRegistry {
    private static FontRegistry ourInstance = new FontRegistry();

    public static FontRegistry getInstance() {
        return ourInstance;
    }

    private SortedMap<FontSpecification, BaseFont> fontPaths;

    private FontRegistry() { fontPaths = new TreeMap<>(); }

    public void add(String fontName, FontSpecification.Style style, String path) throws IOException, DocumentException {
        BaseFont baseFont = BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        fontPaths.put(new FontSpecification(fontName, style), baseFont);
    }

    public BaseFont get(String fontName, FontSpecification.Style style) {
        return fontPaths.get(new FontSpecification(fontName, style));
    }

    public BaseFont[] getFonts() {
        return fontPaths.values().toArray(new BaseFont[0]);
    }

    public BaseFont[] getFonts(FontSpecification.Style style) {
        ArrayList<BaseFont> fonts = new ArrayList<BaseFont>();
        for (Map.Entry<FontSpecification, BaseFont> entry: fontPaths.entrySet()) {
            if (entry.getKey().getStyle().equals(style)) {
                fonts.add(entry.getValue());
            }
        }
        return fonts.toArray(new BaseFont[0]);
    }
}
