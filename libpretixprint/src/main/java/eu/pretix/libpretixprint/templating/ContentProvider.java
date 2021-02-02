package eu.pretix.libpretixprint.templating;

import java.io.InputStream;

public interface ContentProvider {
    String getTextContent(String content, String text);
    String getBarcodeContent(String content);
	InputStream getImageContent(String content);
}
