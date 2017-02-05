package json_adaptor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import email.SendEmail;
import model.CalendarModel;
import model.Event;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Class to handle JSON strings.
 */
public class JsonAdaptor {

    private String errorJson = "Error";
    private CalendarModel cal;

    /**
     * Constructor. Initializes the CalendarModel.
     */
    public JsonAdaptor() {
        cal = new CalendarModel();
    }

    /**
     * Parses the request.
     * @param jsonRequest the JSON string request.
     * @return the result of the CalendarModel computation if successful, "Error" otherwise..
     */
    public String request(String jsonRequest) {
        // parse to jsonObject
        JsonObject json;
        try {
            Gson gson = new Gson();
            json = gson.fromJson(jsonRequest, JsonObject.class);
        }
        catch (JsonSyntaxException e) {
            return errorJson;
        }

        // get requestType
        String requestType = getRequestType(json);
        if (requestType == null) {
            return errorJson;
        }

        // get requestDetails
        JsonObject request = getRequestDetails(requestType, json);

        // validate requestDetails
        if (!validateRequest(requestType, request)) {
            return errorJson;
        }

        // handle request
        return handleRequest(requestType, request);
    }

    /**
     * Extracts the request type from the request string.
     * @param json the request string.
     * @return the request type.
     */
    private String getRequestType(JsonObject json) {
        if (json.has("addEvent")) {
            return "addEvent";
        }
        else if (json.has("addUser")) {
            return "addUser";
        }
        else if (json.has("addLocation")) {
            return "addLocation";
        }
        else if (json.has("getAllEvents")) {
            return "getAllEvents";
        }
        else if (json.has("userExists")) {
            return "userExists";
        }
        else if (json.has("checkUserAvailability")) {
            return "checkUserAvailability";
        }
        else if (json.has("sendEmail")) {
            return "sendEmail";
        }
        return null;
    }

    /**
     * Gets the request details from the request.
     * @param requestType the relevant request type.
     * @param json        the request string.
     * @return the details JSONObject.
     */
    private JsonObject getRequestDetails(String requestType, JsonObject json) {
        return json.get(requestType).getAsJsonObject();
    }

    /**
     * Validates that the request can be handled.
     * @param requestType the request type.
     * @param request     the request string.
     * @return true if successful, false otherwise.
     */
    private boolean validateRequest(String requestType, JsonObject request) {
        switch (requestType) {
            case "addEvent":
                return validateAddEvent(request);
            case "addUser":
                return validateAddUser(request);
            case "addLocation":
                return validateAddLocation(request);
            case "userExists":
                return validateUserExists(request);
            case "getAllEvents":
                return validateGetAllEvents(request);
            case "checkUserAvailability":
                return validateCheckUserAvailability(request);
            case "sendEmail":
                return validateSendEmail(request);
            default:
                break;
        }
        return false;
    }

    /**
     * Validates if the sendEmail request has the needed fields.
     * @param request The request object.
     * @return true if successful, false otherwise.
     */
    private boolean validateSendEmail(JsonObject request) {
        if (request != null) {
            if (request.has("from")) {
                if (request.has("description")) {
                    if (request.has("date")) {
                        if (request.has("location")) {
                            if (request.has("addresses")) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Validates if the checkUserAvailability request has the needed fields.
     * @param request The request object.
     * @return true if successful, false otherwise.
     */
    private boolean validateCheckUserAvailability(JsonObject request) {
        if (request != null) {
            if (request.entrySet().size() == 2) {
                if (request.has("email") && request.has("date")) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * Validates if the userExists request has the needed fields.
     * @param request The request object.
     * @return true if successful, false otherwise.
     */
    private boolean validateUserExists(JsonObject request) {
        if (request.entrySet().size() == 1) {
            if (request.has("email")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates if the addEvent request has the needed fields.
     * @param request The request object.
     * @return true if successful, false otherwise.
     */
    private boolean validateAddEvent(JsonObject request) {
        if (request.has("description")) {
            if (request.has("startTime")) {
                if (request.has("endTime")) {
                    if (request.has("location")) {
                        if (request.has("owner")) {
                            if (request.entrySet().size() == 5) {
                                return true;
                            }
                            else if (request.entrySet().size() == 6) {
                                if (request.has("invited")) {
                                    return true;
                                }
                            }
                            if (request.entrySet().size() > 6) {
                                if (request.has("recurring") && request.has("recurringNumber")) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Validates if the addUser request has the needed fields.
     * @param request The request object.
     * @return true if successful, false otherwise.
     */
    private boolean validateAddUser(JsonObject request) {
        if (request.entrySet().size() == 2) {
            if (request.has("name")) {
                if (request.has("email")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validates if the addLocation request has the needed fields.
     * @param request The request object.
     * @return true if successful, false otherwise.
     */
    private boolean validateAddLocation(JsonObject request) {
        if (request.entrySet().size() == 2) {
            if (request.has("name")) {
                if (request.has("capacity")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validates if the getAllEvents request has the needed fields.
     * @param request The request object.
     * @return true if successful, false otherwise.
     */
    private boolean validateGetAllEvents(JsonObject request) {
        if (request.entrySet().size() == 0) {
            return true;
        }
        if (request.entrySet().size() <= 3) {
            if (request.has("date") || request.has("email") || request.has("location")) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Handles a request for a requestType.
     * @param requestType the request type.
     * @param request     the request object.
     * @return the result of the handling.
     */
    private String handleRequest(String requestType, JsonObject request) {
        switch (requestType) {
            case "addEvent":
                return handleAddEvent(request);
            case "addUser":
                return handleAddUser(request);
            case "addLocation":
                return handleAddLocation(request);
            case "getAllEvents":
                return handleGetAllEvents(request);
            case "userExists":
                return handleUserExists(request);
            case "checkUserAvailability":
                return handleIsUserAvailable(request);
            case "sendEmail":
                return handleSendEmail(request);
            default:
                break;
        }
        return errorJson;
    }

    /**
     * Handles the sendEmail request for the provided request object.
     * @param request the request object.
     * @return the result of the action.
     */
    private String handleSendEmail(JsonObject request) {
        Gson gson = new Gson();
        String from = gson.fromJson(request.get("from"), String.class);
        String description = gson.fromJson(request.get("description"), String.class);
        String date = gson.fromJson(request.get("date"), String.class);
        String location = gson.fromJson(request.get("location"), String.class);
        String addresses = gson.fromJson(request.get("addresses"), String.class);

        int i = SendEmail.send(from, addresses, description, date, location);
        return gson.toJson(i);
    }

    /**
     * Handles the addEvent request for the provided request object.
     * @param request the request object.
     * @return the result of the action.
     */
    private String handleAddEvent(JsonObject request) {
        Gson gson = new Gson();

        // get variables from request
        String desc = gson.fromJson(request.get("description"), String.class);
        Calendar startTime;
        Calendar endTime;
        try {
            startTime = convertToCalendar(gson.fromJson(request.get("startTime"), String.class));
            endTime = convertToCalendar(gson.fromJson(request.get("endTime"), String.class));
        }
        catch (Exception e) {
            return errorJson;
        }
        String location = gson.fromJson(request.get("location"), String.class);
        String owner = gson.fromJson(request.get("owner"), String.class);
        String[] invited = null;
        if (request.has("invited")) {
            invited = gson.fromJson(request.get("invited"), String[].class);
        }

        int id = cal.addEvent(desc, startTime, endTime, location, owner, invited);

        //if event is recurring add multiples
        if (request.has("recurring")) {

            String recurring;
            int recurringNumber;

            recurring = gson.fromJson(request.get("recurring"), String.class);
            recurringNumber = gson.fromJson(request.get("recurringNumber"), Integer.class);

            for (int i = 0; i < recurringNumber; i++) {
                switch (recurring) {
                    case "week":
                        startTime.add(Calendar.WEEK_OF_YEAR, 1);
                        endTime.add(Calendar.WEEK_OF_YEAR, 1);
                        break;
                    case "month":
                        startTime.add(Calendar.MONTH, 1);
                        endTime.add(Calendar.MONTH, 1);
                        break;
                    case "year":
                        startTime.add(Calendar.YEAR, 1);
                        endTime.add(Calendar.YEAR, 1);
                        break;
                    default:
                        break;
                }
                cal.addEvent(desc, startTime, endTime, location, owner, invited);
            }
        }

        //Return the id of the closest event
        if (id == -1) {
            return errorJson;
        }
        else {
            return gson.toJson(id);
        }
    }

    /**
     * Converts a string date to Calendar date with format dd-MM-yy HH:mm:ss.
     * @param shortDate the date.
     * @return the Calendar date.
     * @throws ParseException a ParseException.
     */
    private Calendar convertToCalendar(String shortDate) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        cal.setTime(sdf.parse(shortDate));
        return cal;
    }

    /**
     * Handles the addUser request for the provided request object.
     * @param request the request object.
     * @return the result of the action.
     */
    private String handleAddUser(JsonObject request) {
        Gson gson = new Gson();

        // get variables from request
        String name = gson.fromJson(request.get("name"), String.class);
        String email = gson.fromJson(request.get("email"), String.class);

        // execute request
        int response = cal.addUser(name, email);
        if (response == 0) {
            return gson.toJson(response);
        }
        else {
            return errorJson;
        }
    }

    private String handleAddLocation(JsonObject request) {
        Gson gson = new Gson();

        // get variable from request
        String name = gson.fromJson(request.get("name"), String.class);
        int capacity = gson.fromJson(request.get("capacity"), int.class);

        // execute request
        int response = cal.addLocation(name, capacity);
        if (response == 0) {
            return gson.toJson(response);
        }
        else {
            return errorJson;
        }
    }

    /**
     * Handles the getAllEvents request for the provided request object.
     * @param request the request object.
     * @return the result of the action.
     */
    private String handleGetAllEvents(JsonObject request) {
        Gson gson = new Gson();
        String user = null;
        String date = null;
        String location = null;
        if (request.has("email")) {
            user = gson.fromJson(request.get("email"), String.class);
        }
        if (request.has("date")) {
            date = gson.fromJson(request.get("date"), String.class);
        }
        if (request.has("location")) {
            location = gson.fromJson(request.get("location"), String.class);
        }
        List<Event> response = cal.getEvents(user, date, location);
        if (response != null) {
            return JsonResponseBuilder.returnEventsList(response);
        }
        else {
            return errorJson;
        }
    }

    /**
     * Handles the userExists request for the provided request object.
     * @param request the request object.
     * @return the result of the action.
     */
    private String handleUserExists(JsonObject request) {
        Gson gson = new Gson();

        // get variable from request
        String email = gson.fromJson(request.get("email"), String.class);

        // execute request
        try {
            boolean exists = cal.registeredUser(email);
            if (exists) {
                return "0";
            }
            return errorJson;
        }
        catch (SQLException e) {
            return errorJson;
        }
    }

    /**
     * Handles the checkUserAvailable request for the provided request object.
     * @param request the request object.
     * @return the result of the action.
     */
    private String handleIsUserAvailable(JsonObject request) {
        if (request != null) {
            String email = new Gson().fromJson(request.get("email"), String.class);
            String date = new Gson().fromJson(request.get("date"), String.class);
            return cal.checkWhenFree(email, date, 'U');
        }
        return null;
    }

}
