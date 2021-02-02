package eu.pretix.libpretixprint.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
    public static byte[] inputStreamToByteArray(InputStream is) throws IOException {
        int nRead;
        byte data[] = new byte[16384];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            out.write(data, 0, nRead);
        }
        out.close();
        return out.toByteArray();
    }
}
