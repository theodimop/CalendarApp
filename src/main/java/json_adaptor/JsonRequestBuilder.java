package json_adaptor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Class to correctly create JSON strings.
 */
public class JsonRequestBuilder {

    /**
     * Creates a JSON string for a an addEventRequest.
     * @param desc            the description.
     * @param ld1             the starting date.
     * @param lt1             the starting time.
     * @param ld2             the ending date.
     * @param lt2             the ending time.
     * @param location        the location name.
     * @param owner           the owner's email.
     * @param invited         the invitee array.
     * @param recurring       the recurring frequency.
     * @param recurringNumber the recurring times.
     * @return the JSON string.
     */
    public String makeAddEventRequest(String desc, LocalDate ld1, LocalTime lt1, LocalDate ld2, LocalTime lt2, String location, String owner, String[] invited, String recurring,
            int recurringNumber) {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        details.addProperty("description", desc);
        details.addProperty("startTime", formatDate(ld1) + " " + formatTime(lt1));
        details.addProperty("endTime", formatDate(ld2) + " " + formatTime(lt2));
        details.addProperty("location", location);
        details.addProperty("owner", owner);
        if (invited != null) {
            JsonArray users = new JsonArray();
            for (String user : invited) {
                users.add(user);
            }
            details.add("invited", users);
        }

        if (recurring != null) {
            details.addProperty("recurring", recurring);
            details.addProperty("recurringNumber", recurringNumber);
        }

        request.add("addEvent", details);
        return request.toString();
    }

    /**
     * Convert a LocalDate date to a String.
     * @param ld the date.
     * @return the string.
     */
    private String formatDate(LocalDate ld) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MM-yy");
        return ld.format(date);
    }

    /**
     * Convert a LocalTime time to a String.
     * @param lt the time.
     * @return the string.
     */
    private String formatTime(LocalTime lt) {
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
        return lt.format(time);
    }

    /**
     * Creates a JSON string for a an addUser Request.
     * @param name  user's name.
     * @param email user's email.
     * @return the JSON string.
     */
    public String makeAddUserRequest(String name, String email) {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        details.addProperty("name", name);
        details.addProperty("email", email);

        request.add("addUser", details);

        return request.toString();
    }

    /**
     * Creates a JSON string for a an addLocation Request.
     * @param name     location's name.
     * @param capacity location's email.
     * @return the JSON string.
     */
    public String makeAddLocationRequest(String name, int capacity) {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        details.addProperty("name", name);
        details.addProperty("capacity", capacity);

        request.add("addLocation", details);

        return request.toString();
    }

    /**
     * Creates a JSON string for a an getAllEvents Request.
     * @return the JSON string.
     */
    public String getAllEvents() {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        request.add("getAllEvents", details);

        return request.toString();
    }

    /**
     * Creates a JSON string for a an getAllEvents Request with email and date.
     * @param email the email.
     * @param ld    the date.
     * @return the JSON string.
     */
    public String getAllEventsOnDate(String email, LocalDate ld) {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        details.addProperty("email", email);
        details.addProperty("date", date.format(ld));

        request.add("getAllEvents", details);

        return request.toString();
    }

    /**
     * Creates a JSON string for a an getAllEvents Request for a user.
     * @param email the user's email.
     * @return the JSON string.
     */
    public String getAllEventsByUser(String email) {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        details.addProperty("email", email);

        request.add("getAllEvents", details);

        return request.toString();
    }

    /**
     * Creates a JSON string for a an getAllEvents Request for a location.
     * @param location the location.
     * @return the JSON string.
     */
    public String getAllEventsAtLocation(String location) {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        details.addProperty("location", location);

        request.add("getAllEvents", details);

        return request.toString();
    }

    /**
     * Creates a JSON string for a an checkUser request.
     * @param email user's email.
     * @return the JSON string.
     */
    public String userExists(String email) {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        details.addProperty("email", email);

        request.add("userExists", details);

        return request.toString();
    }

    /**
     * Creates a JSON string for makeIsUserAvailable request.
     * @param email User email.
     * @return the JSON string.
     */
    public String makeIsUserAvailableRequest(String email, String date) {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        details.addProperty("email", email);
        details.addProperty("date", date);
        request.add("checkUserAvailability", details);
        return request.toString();
    }

    /**
     * Creates a JSON string for a sendEmail request.
     * @param from      sender's email.
     * @param addresses recipients array.
     * @param title     the title.
     * @param date      the date.
     * @param location  the location.
     * @return the JSON string.
     */
    public String makeSendEmailRequest(String from, String addresses, String title, LocalDate date, String location) {
        JsonObject request = new JsonObject();
        JsonObject details = new JsonObject();

        details.addProperty("from", from);
        details.addProperty("addresses", addresses);
        details.addProperty("description", title);
        details.addProperty("date", formatDate(date));
        details.addProperty("location", location);
        request.add("sendEmail", details);

        return request.toString();
    }

}
