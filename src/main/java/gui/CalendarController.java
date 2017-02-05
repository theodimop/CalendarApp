package gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jfoenix.controls.JFXDatePicker;
import httpserver.Client;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import json_adaptor.JsonRequestBuilder;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the GUI class.
 */
public class CalendarController {

    static final int SUCCESS = 0;
    static final int FORM_CONTENTS_ERROR = 1;
    static final int INSERTION_TO_DB_ERROR = 2;

    private JsonRequestBuilder jsonRequestBuilder = new JsonRequestBuilder();

    private Client client;  //Application Calendar Client
    private String username = "";

    /**
     * Constructor. It initializes the client.
     */
    public CalendarController() {
        this.client = new Client();
    }

    /**
     * Add event.
     * Controls[0] -> Date
     * Controls[1] -> Start Time
     * Controls[2] -> End Time
     * Controls[3] -> Location
     * Controls[4] -> Date
     * Controls[5] -> Invitees
     * Implements the adding of new event, with its details given by the user.
     * @param controls        List includes the controls that contain event details
     * @param recurring       recurring frequency.
     * @param recurringNumber recurring times.
     * @return Returns True if the event was added to calendar.
     */
    public int addEvent(List<Control> controls, String recurring, int recurringNumber) {
        int date = 0, startTime = 1, endTime = 2, location = 3, descNum = 4, invitees = 5;
        //  controls.add(0,null);
        try {
            //Read data
            JFXDatePicker datePicker = (JFXDatePicker) controls.get(date);
            JFXDatePicker timeStartPicker = (JFXDatePicker) controls.get(startTime);
            JFXDatePicker timeEndPicker = (JFXDatePicker) controls.get(endTime);
            TextField locationField = (TextField) controls.get(location);
            TextArea description = (TextArea) controls.get(descNum);
            TextArea invitationsText = (TextArea) controls.get(invitees);

            // Parse information
            // Not does not support multi-day events
            LocalDate lDate = datePicker.getValue();
            LocalTime start = timeStartPicker.getTime();
            LocalTime end = timeEndPicker.getTime();
            String loc = locationField.getText();
            String owner = username.equals("") ? "admin@admin.com" : username;
            String desc = description.getText();

            String[] invitations = (invitationsText.getText().isEmpty()) ? null : invitationsText.getText().split(",");

            // Check if times are chronological
            if (start.isAfter(end)) {
                return FORM_CONTENTS_ERROR;
            }

            // Build Json
            String jsonRequest = jsonRequestBuilder.makeAddEventRequest(desc, lDate, start, lDate, end, loc, owner, invitations, recurring, recurringNumber);
            //System.out.println(jsonRequest);
            //Make request
            String serverResponse = client.makeRequest(jsonRequest);
            //System.out.println(serverResponse);

            if (!serverResponse.toLowerCase().contains("error") && invitations != null && invitations.length > 0) {
                String emailRequest = jsonRequestBuilder.makeSendEmailRequest(owner, invitationsText.getText(), desc, lDate, loc);
                //  System.out.println(emailRequest);
                String emailResponse = client.makeRequest(emailRequest);
                //  System.out.println(emailResponse);
            }

            return serverResponse.toLowerCase().contains("error") ? INSERTION_TO_DB_ERROR : SUCCESS;
        }
        catch (Exception e) {
            e.printStackTrace();
            return FORM_CONTENTS_ERROR;
        }
    }

    /**
     * /**
     * Controls[0] -> Location name
     * Controls[1] -> Capacity
     * Implements the adding of new location, with its details given
     * by the user.
     * @param controls List includes the controls that contain event details
     * @return Returns True if the location was added to database.
     */
    public boolean addLocation(List<Control> controls) {
        try {
            //Read data
            TextField locationName = (TextField) controls.get(0);
            TextField capacity = (TextField) controls.get(1);

            // Parse information
            String location = locationName.getText();
            int cap = Integer.parseInt(capacity.getText());

            // Check capacity
            if (cap < 1) {
                return false;
            }

            // Build Json
            String jsonRequest = jsonRequestBuilder.makeAddLocationRequest(location, cap);
            //Make request
            String serverResponse = client.makeRequest(jsonRequest);
            //System.out.println(jsonRequest);

            return !serverResponse.contains("Error");
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * /**
     * Controls[0] -> Username
     * Controls[1] -> Email
     * Implements the adding of new user.
     * @param controls List includes the controls that contain event details
     * @return Returns True if the user was added to database.
     */
    public boolean addUser(List<Control> controls) {
        try {
            //Read data
            TextField name = (TextField) controls.get(0);
            TextField email = (TextField) controls.get(1);

            // Parse data
            String nameS = name.getText();
            String emailS = email.getText();

            // Build Json
            String jsonRequest = jsonRequestBuilder.makeAddUserRequest(nameS, emailS);

            //Make request
            String serverResponse = client.makeRequest(jsonRequest);

            //Make request
            return !serverResponse.contains("Error");
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Query get all user events.
     * @return an array.
     */
    public CalendarView.EventTableView[] getAllUserEvents() {
        String json = makeRequest(jsonRequestBuilder.getAllEventsByUser(username)).replace("Response Code: 200, contents: ", "");
        CalendarView.EventTableView[] eventTableView = parseUserEventsJson(json);

        return eventTableView;
    }

    /**
     * Query get all user events on a date.
     * @param localDate the date.
     * @return an array.
     */
    public CalendarView.EventTableView[] getEventsByDate(LocalDate localDate) {
        String json = makeRequest(jsonRequestBuilder.getAllEventsOnDate(username, localDate)).replace("Response Code: 200, contents: ", "");

        Gson gson = new Gson();
        CalendarView.EventTableView[] eventTableView = parseUserEventsJson(json);

        //ObservableList<CalendarView.EventTableView> data = FXCollections.observableArrayList();
        //data.addAll(eventTableView);

        return eventTableView;
    }

    /**
     * Authenticates user.
     * @param result The typed name in Window, if any.
     * @return true if success, false otherwise.
     */
    public boolean authenticateUser(Optional<String> result) {
        String jsonRequest;
        String serverResponse;

        if (result.isPresent()) {       //Ok button pressed
            username = result.get();
            // the way the user name is selected may need to be changed here
            jsonRequest = jsonRequestBuilder.userExists(username.equals("") ? "admin@admin.com" : username);
            serverResponse = makeRequest(jsonRequest);
            return !serverResponse.contains("Error") && !serverResponse.endsWith("(Connection refused)");
        }
        else {
            System.exit(0);             //Cancel button pressed
            return false;
        }
    }

    /**
     * Generate a string with the unavailable time.
     * @param email  User email.
     * @param dateLD Date
     * @return Returns a string with the user unavailable times on a given date.
     */
    public String checkIfAvailable(String email, LocalDate dateLD) {
        if (email != null && dateLD != null && EmailValidator.getInstance().isValid(email)) {
            String date = dateLD.toString();
            String jsonRequest = jsonRequestBuilder.makeIsUserAvailableRequest(email, date);
            //System.out.println(jsonRequest);

            //Make request
            String unavailableHours = makeRequest(jsonRequest).replace("Response Code: 200, contents: ", "");
            //System.out.println(unavailableHours);
            if (unavailableHours.toLowerCase().equals("error")) {
                return "error";
            }
            else if (unavailableHours.isEmpty()) {
                return "";
            }
            else {
                return unavailableHours;
            }
        }

        return null;
    }

    /**
     * Make a request to server.
     * @param request The json request.
     * @return Returns the server response.
     */
    private String makeRequest(String request) {
        String serverResponse = client.makeRequest(request);
        // System.out.println(request);
        System.out.println(serverResponse);
        return serverResponse;
    }

    /**
     * Parse the json string which contains user events.
     * @param json Json String.
     * @return Returns an array with TableViewEvents.
     */
    private CalendarView.EventTableView[] parseUserEventsJson(String json) {
        if (json != null) {
            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
            CalendarView.EventTableView[] arr = new CalendarView.EventTableView[jsonArray.size()];
            CalendarView.EventTableView e;
            JsonObject jo;
            for (int i = 0; i < jsonArray.size(); i++) {
                jo = jsonArray.get(i).getAsJsonObject();
                String desc = gson.fromJson(jo.get("description"), String.class);
                String date = gson.fromJson(jo.get("date"), String.class);
                String startTime = gson.fromJson(jo.get("startTime"), String.class);
                String location = gson.fromJson(jo.get("location"), String.class);
                e = new CalendarView.EventTableView(desc, date, startTime, location);
                arr[i] = e;
            }
            return arr;
        }
        return null;
    }

}
