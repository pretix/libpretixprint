package eu.pretix.libpretixprint.templating;

import org.json.JSONObject;

import java.io.InputStream;

public interface ContentProvider {
    String getTextContent(String content, String text, JSONObject textI18n);
    String getBarcodeContent(String content, String text, JSONObject textI18n);
	InputStream getImageContent(String content);
}
