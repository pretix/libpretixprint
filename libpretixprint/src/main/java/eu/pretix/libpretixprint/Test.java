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
    public static String testData = "[{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"17.50\",\"bottom\":\"274.60\",\"fontsize\":\"16.0\",\"color\":[0,0,0,1],\"fontfamily\":\"Open Sans\",\"bold\":false,\"italic\":false,\"width\":\"175.00\",\"downward\":false,\"content\":\"event_name\",\"text\":\"Beispielevent\",\"rotation\":0,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"17.50\",\"bottom\":\"262.90\",\"fontsize\":\"13.0\",\"color\":[0,0,0,1],\"fontfamily\":\"Open Sans\",\"bold\":false,\"italic\":false,\"width\":\"110.00\",\"downward\":false,\"content\":\"itemvar\",\"text\":\"Beispielprodukt – Beispielvariante\",\"rotation\":0,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"17.50\",\"bottom\":\"252.50\",\"fontsize\":\"13.0\",\"color\":[0,0,0,1],\"fontfamily\":\"Open Sans\",\"bold\":false,\"italic\":false,\"width\":\"110.00\",\"downward\":false,\"content\":\"attendee_name\",\"text\":\"Dr. Max Mustermann\",\"rotation\":0,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"17.50\",\"bottom\":\"242.10\",\"fontsize\":\"13.0\",\"color\":[0,0,0,1],\"fontfamily\":\"Open Sans\",\"bold\":false,\"italic\":false,\"width\":\"110.00\",\"downward\":false,\"content\":\"event_date_range\",\"text\":\"31. Mai – 4. Juni 2017\",\"rotation\":0,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"149.57\",\"bottom\":\"162.34\",\"fontsize\":\"14.0\",\"color\":[0,0,0,1],\"fontfamily\":\"Open Sans\",\"bold\":false,\"italic\":false,\"width\":\"112.05\",\"downward\":true,\"content\":\"other\",\"text\":\"Fontsize 14, Font height: 4,94mm\",\"rotation\":0,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"291.02\",\"bottom\":\"108.06\",\"fontsize\":\"13.0\",\"color\":[0,0,0,1],\"fontfamily\":\"Open Sans\",\"bold\":false,\"italic\":false,\"width\":\"45.00\",\"downward\":false,\"content\":\"price\",\"text\":\"123,45 EUR\",\"rotation\":5.710235519337863,\"align\":\"right\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"30.00\",\"bottom\":\"115.00\",\"fontsize\":\"13.0\",\"color\":[52,34,162,1],\"fontfamily\":\"DejaVu Sans\",\"bold\":false,\"italic\":false,\"width\":\"80.00\",\"downward\":false,\"content\":\"other\",\"text\":\"Beispielevent\",\"rotation\":0,\"align\":\"left\"},{\"type\":\"barcodearea\",\"left\":\"130.40\",\"bottom\":\"204.50\",\"size\":\"64.00\"},{\"type\":\"poweredby\",\"left\":\"103.16\",\"bottom\":\"26.02\",\"size\":\"20.00\",\"content\":\"dark\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"190.20\",\"bottom\":\"118.29\",\"fontsize\":\"13.0\",\"color\":[52,34,162,1],\"fontfamily\":\"DejaVu Sans\",\"bold\":false,\"italic\":false,\"width\":\"72.86\",\"downward\":true,\"content\":\"other\",\"text\":\"Beispieltext\\nmit zwei Zeilen\",\"rotation\":320.2,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"110.00\",\"bottom\":\"75.00\",\"fontsize\":\"13.0\",\"color\":[52,34,162,1],\"fontfamily\":\"DejaVu Sans\",\"bold\":false,\"italic\":false,\"width\":\"80.00\",\"downward\":false,\"content\":\"other\",\"text\":\"Beispielevent\",\"rotation\":180,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"110.00\",\"bottom\":\"115.00\",\"fontsize\":\"13.0\",\"color\":[52,34,162,1],\"fontfamily\":\"DejaVu Sans\",\"bold\":false,\"italic\":false,\"width\":\"40.00\",\"downward\":false,\"content\":\"other\",\"text\":\"Beispielevent\",\"rotation\":90,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"30.00\",\"bottom\":\"75.00\",\"fontsize\":\"13.0\",\"color\":[52,34,162,1],\"fontfamily\":\"DejaVu Sans\",\"bold\":false,\"italic\":false,\"width\":\"40.00\",\"downward\":false,\"content\":\"other\",\"text\":\"Beispielevent\",\"rotation\":-90,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"250.00\",\"bottom\":\"60.23\",\"fontsize\":\"13.0\",\"color\":[52,34,162,1],\"fontfamily\":\"DejaVu Sans\",\"bold\":false,\"italic\":false,\"width\":\"60.00\",\"downward\":true,\"content\":\"other\",\"text\":\"Beispieltext\\nmit zwei Zeilen\\noder drei\",\"rotation\":180,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"160.00\",\"bottom\":\"30.00\",\"fontsize\":\"13.0\",\"color\":[52,34,162,1],\"fontfamily\":\"Glacial Indifference\",\"bold\":false,\"italic\":false,\"width\":\"60.00\",\"downward\":true,\"content\":\"other\",\"text\":\"Beispieltext\\nmit zwei Zeilen\\noder drei\",\"rotation\":0,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"250.00\",\"bottom\":\"120.11\",\"fontsize\":\"13.0\",\"color\":[52,34,162,1],\"fontfamily\":\"DejaVu Sans\",\"bold\":false,\"italic\":false,\"width\":\"60.00\",\"downward\":true,\"content\":\"other\",\"text\":\"Beispieltext\\nmit zwei Zeilen\\noder drei\",\"rotation\":90,\"align\":\"left\"},{\"type\":\"textarea\",\"locale\":\"en\",\"left\":\"149.40\",\"bottom\":\"163.09\",\"fontsize\":\"13.0\",\"color\":[0,0,0,1],\"fontfamily\":\"Open Sans\",\"bold\":false,\"italic\":false,\"width\":\"112.05\",\"downward\":false,\"content\":\"other\",\"text\":\"Fontsize 13, Font height: 4,59mm\",\"rotation\":0,\"align\":\"left\"}]";

    public static void main(String[] args)
            throws IOException, DocumentException {
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
