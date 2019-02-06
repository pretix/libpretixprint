package eu.pretix.libpretixprint;

import com.lowagie.text.DocumentException;
import eu.pretix.libpretixprint.templating.ContentProvider;
import eu.pretix.libpretixprint.templating.FontRegistry;
import eu.pretix.libpretixprint.templating.FontSpecification;
import eu.pretix.libpretixprint.templating.Layout;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static String testData = "[{\"bottom\": \"274.60\", \"italic\": false, \"fontfamily\": \"Open Sans\", \"text\": \"Sample event name\", \"color\": [0, 0, 0, 1], \"align\": \"left\", \"type\": \"textarea\", \"left\": \"17.50\", \"content\": \"event_name\", \"fontsize\": \"16.0\", \"bold\": false, \"width\": \"175.00\"}, {\"bottom\": \"262.90\", \"italic\": false, \"fontfamily\": \"Open Sans\", \"text\": \"Sample product description\", \"color\": [0, 0, 0, 1], \"align\": \"left\", \"type\": \"textarea\", \"left\": \"17.50\", \"content\": \"itemvar\", \"fontsize\": \"13.0\", \"bold\": false, \"width\": \"110.00\"}, {\"bottom\": \"252.50\", \"italic\": false, \"fontfamily\": \"Open Sans\", \"text\": \"John Doe\", \"color\": [0, 0, 0, 1], \"align\": \"left\", \"type\": \"textarea\", \"left\": \"17.50\", \"content\": \"attendee_name\", \"fontsize\": \"13.0\", \"bold\": false, \"width\": \"110.00\"}, {\"bottom\": \"180.18\", \"italic\": false, \"fontfamily\": \"Open Sans\", \"text\": \"Fontsize 10, Font height: 3,52mm\", \"color\": [0, 0, 0, 1], \"align\": \"left\", \"type\": \"textarea\", \"left\": \"149.53\", \"content\": \"other\", \"fontsize\": \"10.0\", \"bold\": false, \"width\": \"113.45\"}, {\"bottom\": \"174.69\", \"italic\": false, \"fontfamily\": \"Open Sans\", \"text\": \"Fontsize 11, Font height: 3,88mm\", \"color\": [0, 0, 0, 1], \"align\": \"left\", \"type\": \"textarea\", \"left\": \"149.62\", \"content\": \"other\", \"fontsize\": \"11.0\", \"bold\": false, \"width\": \"113.45\"}, {\"bottom\": \"169.20\", \"italic\": false, \"fontfamily\": \"Open Sans\", \"text\": \"Fontsize 12, Font height: 4,23mm\", \"color\": [0, 0, 0, 1], \"align\": \"left\", \"type\": \"textarea\", \"left\": \"149.58\", \"content\": \"other\", \"fontsize\": \"12.0\", \"bold\": false, \"width\": \"113.45\"}, {\"bottom\": \"162.95\", \"italic\": false, \"fontfamily\": \"Open Sans\", \"text\": \"Fontsize 13, Font height: 4,59mm\", \"color\": [255, 255, 0, 1], \"align\": \"left\", \"type\": \"textarea\", \"left\": \"149.51\", \"content\": \"other\", \"fontsize\": \"13.0\", \"bold\": false, \"width\": \"113.45\"}, {\"bottom\": \"156.94\", \"italic\": false, \"fontfamily\": \"Open Sans\", \"text\": \"Fontsize 14, Font height: 4,94mm\", \"color\": [0, 0, 0, 1], \"align\": \"left\", \"type\": \"textarea\", \"left\": \"149.54\", \"content\": \"other\", \"fontsize\": \"14.0\", \"bold\": false, \"width\": \"113.45\"}, {\"bottom\": \"149.90\", \"italic\": false, \"fontfamily\": \"Open Sans\", \"text\": \"Fontsize 15, Font height: 5,29mm\", \"color\": [0, 0, 0, 1], \"align\": \"left\", \"type\": \"textarea\", \"left\": \"149.55\", \"content\": \"other\", \"fontsize\": \"15.0\", \"bold\": false, \"width\": \"113.45\"}, {\"type\": \"barcodearea\", \"left\": \"190.00\", \"size\": \"60.00\", \"bottom\": \"60.00\"}]";

    public static void main(String[] args)
            throws DocumentException, IOException {
        FontRegistry.getInstance().add("Open Sans", FontSpecification.Style.REGULAR, "/home/raphael/proj/pretix/src/pretix/static/fonts/OpenSans-Regular.ttf");
        ContentProvider cp = new ContentProvider() {
            @Override
            public String getTextContent(String content, String text) {
                return text;
            }

            @Override
            public String getBarcodeContent(String content) {
                return "asdsdgncvbcövjbhdkfjghd";
            }
        };
        List<ContentProvider> i = new ArrayList<>();
        i.add(cp);
        try {
            Layout l = new Layout(
                    new JSONArray(testData),
                    "/home/raphael/proj/pretix/res/testbackground.pdf",
                    i.iterator()
            );
            l.render("/tmp/java-out.pdf");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
