package json_adaptor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Event;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Class to format the response in a JSON string.
 */
public class JsonResponseBuilder {

    private static SimpleDateFormat date = new SimpleDateFormat("dd-MM-yy");
    private static SimpleDateFormat time = new SimpleDateFormat("HH:mm");

    /**
     * Convert a list of events in a JSON string.
     * @param events the events list.
     * @return the JSON string.
     */
    public static String returnEventsList(List<Event> events) {
        JsonArray json = new JsonArray();

        for (Event event : events) {
            JsonObject jo = new JsonObject();
            jo.addProperty("description", event.getTitle());
            jo.addProperty("date", date.format(event.getStartDate().getTime()));
            jo.addProperty("startTime", time.format(event.getStartDate().getTime()));
            jo.addProperty("location", event.getLocation());
            json.add(jo);
        }

        return json.toString();
    }

}
