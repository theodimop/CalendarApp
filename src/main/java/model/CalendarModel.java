package model;

import org.apache.commons.validator.routines.EmailValidator;
import sql.SqlQueries;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * The Calendar's model class.
 */
public class CalendarModel {

    private int next_id = 0;

    // Values for fields and table names in SQL
    private static final String USER_TABLE = "users";
    private static final String EMAIL_FIELD = "email";
    private static final String NAME_FIELD = "name";
    private static final String LOCATION_TABLE = "locations";
    private static final String LOCATION_FIELD = "loc_name";
    private static final String CAPACITY_FIELD = "capacity";
    private static final String START_TIME_FIELD = "startTime";
    private static final String ENDTIME_FIELD = "endTime";
    private static final String INVITED_TABLE = "invited";
    private static final String EVENT_TABLE = "events";
    private static final String ID_FIELD = "id";
    private static final String ERROR_FIELD = "Error";
    private static final int FAILURE = -1;

    /**
     * Constructor. Also gets the current id from the database.
     */
    public CalendarModel() {
        next_id = SqlQueries.getIdCount();
    }

    /**
     * Add a user with name and email.
     * @param name  the name.
     * @param email the email.
     * @return 0 for success and -1 for failure.
     */
    public int addUser(String name, String email) {
        // Check email is new to the system
        try {
            if (validUser(name, email)) {
                return SqlQueries.addEntry(USER_TABLE, new String[] {NAME_FIELD, EMAIL_FIELD}, new String[] {name, email});
            }
            else {
                return FAILURE; // Error code
            }
        }
        catch (SQLException e) {
            return FAILURE;
        }
    }

    /**
     * Ensure a user has a valid email, is not already registered and the name is not null.
     * @param name  the name.
     * @param email the email.
     * @return true for valid and registered user, false otherwise.
     * @throws SQLException an SQLException.
     */
    private Boolean validUser(String name, String email) throws SQLException {
        // Return error if email is invalid
        if (!EmailValidator.getInstance().isValid(email)) {
            return false;
        }
        // Return error if email is already in list
        if (registeredUser(email) || name == null) {
            return false;
        }
        return true;
    }

    /**
     * Check if an email is already registered.
     * @param email the email.
     * @return true if registered, false otherwise.
     * @throws SQLException an SQLException.
     */
    public Boolean registeredUser(String email) throws SQLException {
        return SqlQueries.validateEntry(USER_TABLE, new String[] {EMAIL_FIELD}, new String[] {email});
    }

    /**
     * Get the users and return as a HashMap of <user email, User>.
     * @return the Hashmap.
     */
    public HashMap<String, User> getUsers() {
        HashMap<String, User> users = new HashMap<>();
        List list = SqlQueries.getEntries(USER_TABLE, new String[] {}, new String[] {}, null);
        if (list.contains(ERROR_FIELD)) {
            users.put(ERROR_FIELD, null);
        }
        else {
            for (Object user : list) {
                users.put(((User) user).getEmail(), (User) user);
            }
        }

        return users;
    }

    /**
     * Add location using name and capacity of location.
     * @param name     the name.
     * @param capacity the capacity.
     * @return 0 for success, -1 otherwise.
     */
    public int addLocation(String name, int capacity) {
        try {
            if (addedLocation(name) || name == null) {
                return FAILURE;
            }
            else {
                return SqlQueries.addEntry(LOCATION_TABLE, new String[] {LOCATION_FIELD, CAPACITY_FIELD}, new String[] {name, "" + capacity});
            }
        }
        catch (SQLException e) {
            return FAILURE;
        }
    }

    /**
     * Check if the location already exists in the system.
     * @param name the name.
     * @return true if already added, false otherwise.
     * @throws SQLException an SQLException.
     */
    private boolean addedLocation(String name) throws SQLException {
        return SqlQueries.validateEntry(LOCATION_TABLE, new String[] {LOCATION_FIELD}, new String[] {name});
    }

    /**
     * Get locations returns as HashMap with String name and Location.
     * @return the HashMap.
     */
    public HashMap<String, Location> getLocations() {
        HashMap<String, Location> locations = new HashMap<>();
        List list = SqlQueries.getEntries(LOCATION_TABLE, new String[] {}, new String[] {}, null);

        if (list.contains(ERROR_FIELD)) {
            locations.put(ERROR_FIELD, null);
        }
        else {
            for (Object location : list) {
                locations.put(((Location) location).getLocationName(), (Location) location);
            }
        }
        return locations;
    }

    /**
     * Add event with all the fields, returns int ID.
     * @param desc      the title.
     * @param startDate the start date.
     * @param endDate   the end date.
     * @param location  the location name.
     * @param owner     the creator/owner email.
     * @param users     the invitees array.
     * @return 0 for success, -1 otherwise.
     */
    public int addEvent(String desc, Calendar startDate, Calendar endDate, String location, String owner, String[] users) {
        try {
            String sqlStartDate = calendarToSqlDate(startDate);
            String sqlEndDate = calendarToSqlDate(endDate);

            if (validateAddEvent(desc, startDate, endDate, location, owner, users)) {

                SqlQueries.addEntry(EVENT_TABLE, new String[] {ID_FIELD, "description", LOCATION_FIELD, START_TIME_FIELD, ENDTIME_FIELD, EMAIL_FIELD},
                        new String[] {"" + next_id, desc, location, sqlStartDate, sqlEndDate, owner});

                //add owner to the event as invitee as well
                SqlQueries.addEntry(INVITED_TABLE, new String[] {ID_FIELD, EMAIL_FIELD}, new String[] {"" + next_id, owner});

                // If other users have been invited to the event
                if (validateInvitedUsers(users)) {
                    for (String user : users) {
                        SqlQueries.addEntry(INVITED_TABLE, new String[] {ID_FIELD, EMAIL_FIELD}, new String[] {"" + next_id, user});
                    }
                }
                return next_id++;
            }
            else {
                return FAILURE;
            }

        }
        catch (SQLException e) {
            return FAILURE;
        }
    }

    /**
     * Validates the invited users exist in the database.
     * @param users the users.
     * @return true on sccess, false otherwise.
     * @throws SQLException an SQLException.
     */
    private Boolean validateInvitedUsers(String[] users) throws SQLException {

        if (users != null) {
            for (String user : users) {
                if (!EmailValidator.getInstance().isValid(user) || !registeredUser(user)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Convert a Calendar date to a sql.Date like format accepted by the DB.
     * @param date the date.
     * @return the date as formatted date.
     */
    private String calendarToSqlDate(Calendar date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new java.sql.Date(date.getTimeInMillis()));
    }

    /**
     * Check an event is valid before adding it.
     * @param desc      the title.
     * @param startDate the start date.
     * @param endDate   the end date.
     * @param location  the location name.
     * @param email     the creator/owner email.
     * @param users     the invitees array.
     * @return true for valid, false otherwise.
     * @throws SQLException an SQLException.
     */
    private Boolean validateAddEvent(String desc, Calendar startDate, Calendar endDate, String location, String email, String[] users) throws SQLException {

        if (registeredUser(email) && addedLocation(location) && desc != null && startDate != null && endDate != null) {
            if (!doubleBookingRoom(location, startDate, endDate)) {
                // Checking if location capacity can accommodate number invited
                HashMap<String, Location> locs = getLocations();
                int cap = locs.get(location).getCapacity();
                if (users != null && cap < users.length + 1) {
                    return false;
                    // If no invited or location has correct capacity
                }
                return true;
                // If we are trying to double book a location
            }
            else {
                return false;
            }
            // If location or user not recognised, or any information missing
        }
        else {
            return false;
        }
    }

    /**
     * Checks if a room is booked already for a time period.
     * @param location  the location name.
     * @param startDate the start date.
     * @param endDate   the end date.
     * @return true if already booked, false otherwise.
     */
    public Boolean doubleBookingRoom(String location, Calendar startDate, Calendar endDate) {

        // Get events with location
        List<Event> events = getEvents(null, null, location);
        // To hold a list of [startDate, endDate] tuples for events at the same location
        ArrayList<Calendar[]> timesBookedForLocation = new ArrayList<Calendar[]>();
        for (Event e : events) {
            if (e.getLocation().equals(location)) {
                timesBookedForLocation.add(new Calendar[] {e.getStartDate(), e.getEndDate()});
            }
        }

        // Loop through all time ranges this location has already been booked for
        for (Calendar[] d : timesBookedForLocation) {
            // Check for overlapping time ranges
            if (d[0].before(endDate) && d[1].after(startDate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all the events and return as list.
     * @return a list with all the events.
     */
    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        List list = SqlQueries.getEntries(EVENT_TABLE, new String[] {}, new String[] {}, null);

        if (list.contains(ERROR_FIELD)) {
            events.add(new Event(ERROR_FIELD, null, null, null, null));
        }
        else {
            events.addAll(list);
        }
        return events;
    }

    /**
     * Get events with email/date/location parameters.
     * @param email    the email.
     * @param date     the date.
     * @param location the location.
     * @return a list with the events.
     */
    public List<Event> getEvents(String email, String date, String location) {
        List<Event> events = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();

        if (email != null && !email.isEmpty()) {
            fields.add(EMAIL_FIELD);
            values.add(email);
        }
        if (location != null && !location.isEmpty()) {
            fields.add(LOCATION_FIELD);
            values.add(location);
        }
        if (date != null && !date.isEmpty()) {
            System.out.println(date);
            fields.add(START_TIME_FIELD);
            values.add(date);
        }
        List list;
        if (fields.isEmpty() || values.isEmpty()) {
            list = SqlQueries.getEntries(EVENT_TABLE, new String[] {}, new String[] {}, null);
        }
        else {
            String[] arrayFields = fields.toArray(new String[] {});
            String[] arrayValues = values.toArray(new String[] {});
            list = SqlQueries.getEntries(EVENT_TABLE, arrayFields, arrayValues, null);
        }
        if (list.contains(ERROR_FIELD)) {
            events.add(new Event(ERROR_FIELD, null, null, null, null));
        }
        else {
            events.addAll(list);
        }

        return events;
    }

    /**
     * Check when a user or a location is free.
     * @param toCheck User email or location.
     * @param date    Date.
     * @param code    U or L for user or location.
     * @return Returns a string with the unavailable user hours.
     */
    public String checkWhenFree(String toCheck, String date, char code) {
        Map<Integer, Boolean> times = new HashMap<>();
        List<Event> list = null;

        switch (code) {
            case 'U':
                list = SqlQueries.getInvitedEventsForDate(toCheck, date);
                break;
            case 'L':
                list = SqlQueries.getEntries(EVENT_TABLE, new String[] {"loc_name", "startTime"}, new String[] {toCheck, date}, null);
                break;
            default:
                break;
        }

        if (list == null) {
            return "Error";
        }

        IntStream.range(0, 24).forEach(i -> times.put(i, true));

        list.forEach(e -> {
            int startHour = e.getStartDate().get(Calendar.HOUR_OF_DAY);
            int endHour = e.getEndDate().get(Calendar.HOUR_OF_DAY);

            while (startHour <= endHour) {
                times.put(startHour, false);
                startHour++;
            }
        });

        StringBuilder result = new StringBuilder();
        times.forEach((hour, available) -> {
            result.append(String.format("%02d : 00  ->  %s,", hour, available ? "available" : "occupied"));
        });

        return result.toString();
    }

    /**
     * Takes a string date and converts to a Date with format dd-MM-yyyy.
     * @param sDate the string date.
     * @return the formatted util.Date.
     * @throws ParseException a ParseException.
     */
    public Date stringToDate(String sDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.parse(sDate);
    }

}
