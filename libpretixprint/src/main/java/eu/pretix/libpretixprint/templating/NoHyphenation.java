package eu.pretix.libpretixprint.templating;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.HyphenationEvent;

public class NoHyphenation implements HyphenationEvent {

    @Override
    public String getHyphenSymbol() {
        return "-";
    }

    @Override
    public String getHyphenatedWordPre(String word, BaseFont font, float fontSize, float remainingWidth) {
        return "";
    }

    @Override
    public String getHyphenatedWordPost() {
        return "";
    }
}
