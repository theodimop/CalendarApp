package sql;

import model.Event;
import model.Location;
import model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Class to create and execute queries.
 */
public class SqlQueries {

    private static Connection connection = ConnectionHandler.getInstance().getConnection();

    /**
     * Checks if the db contains an other entry with these values by counting how many similar entries there are.
     * @param table  The table to look at.
     * @param keys   The keys to look at.
     * @param values The values to look for.
     * @return If such an entry exists.
     * @throws SQLException an SqlException.
     */
    public static boolean validateEntry(String table, String[] keys, String[] values) throws SQLException {
        String sqlKeys = formatValues(keys, "", "");
        String sqlValues = formatValues(values, "\'", "\'");
        Statement stmt = connection.createStatement();
        String sql = "select count(*) AS count from " + table + " where (" + sqlKeys + ") = (" + sqlValues + ")";
        ResultSet resultSet = stmt.executeQuery(sql);
        int count = -1;

        if (resultSet.next()) {
            count = resultSet.getInt("count");
        }

        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * Adds an entry on the given table for the given fields and values.
     * @param table  The table to insert the entry.
     * @param fields The fields of the entry.
     * @param values The values of the fields.
     * @return The result. 0 for success -1 for failure.
     * @throws SQLException an SqlException.
     */
    public static int addEntry(String table, String[] fields, String[] values) throws SQLException {
        //transform the info to sql syntax
        String sqlFields = formatValues(fields, "", "");
        String sqlValues = formatValues(values, "\'", "\'");

        Statement stmt = connection.createStatement();
        //sql statement to insert and also to confirm that the insertion was successful.
        String sql = "insert into " + table + " (" + sqlFields + ") values (" + sqlValues + ")";

        //0 on success (1-1) or -1 on failure (0-1)
        return stmt.executeUpdate(sql) - 1;
    }

    /**
     * Returns a list of entries from the table for the values in the provided fields.
     * @param table     the table.
     * @param fields    the fields.
     * @param values    the values.
     * @param searchFor the field to look for. If null, it searches for "*"
     * @return the list.
     */
    public static List getEntries(String table, String[] fields, String[] values, String searchFor) {
        List list;
        String emailValue = null, startDateValue = null;

        if (searchFor == null) {
            searchFor = "*";
        }

        if (table.equals("events")) {
            list = new ArrayList<Event>();
        }
        else if (table.equals("users")) {
            list = new ArrayList<User>();
        }
        else if (table.equals("locations")) {
            list = new ArrayList<Location>();
        }
        else {
            list = new ArrayList<String>();
            list.add("Error");
            return list;
        }

        try {
            String sql;

            if (fields.length == 0 || values.length == 0) {
                sql = "SELECT " + searchFor + " FROM " + table;
            }
            else {
                sql = "SELECT " + searchFor + " FROM " + table + " where (";
                for (int i = 0; i < fields.length; i++) {
                    sql += fields[i] + " ";
                    if (fields[i].equals("email")) {
                        emailValue = values[i];
                    }
                    if (fields[i].equals("startTime") || fields[i].equals("endTime")) {
                        if (fields[i].equals("startTime")) {
                            startDateValue = values[i];
                        }
                        sql += "like \'" + values[i] + "%\'";
                    }
                    else {
                        sql += "= \'" + values[i] + "\'";
                    }
                    if (i < fields.length - 1) {
                        sql += " AND ";
                    }
                }
                sql += ")";
                System.out.println(sql);
            }

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                if (table.equals("events")) {
                    Calendar startDate = Calendar.getInstance();
                    startDate.setTimeInMillis(resultSet.getTimestamp("startTime").getTime());
                    Calendar endDate = Calendar.getInstance();
                    endDate.setTimeInMillis(resultSet.getTimestamp("endTime").getTime());
                    Event event = new Event(resultSet.getString("description"), startDate, endDate, resultSet.getString("loc_name"), resultSet.getString("email"));
                    event.setId(resultSet.getInt("id"));
                    list.add(event);
                }
                else if (table.equals("users")) {
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    list.add(new User(name, email));
                }
                else if (table.equals("locations")) {
                    String loc_name = resultSet.getString("loc_name");
                    int capacity = resultSet.getInt("capacity");
                    list.add(new Location(loc_name, capacity));
                }
            }

            //add the events the user is invited in on that date
            if (table.equals("events")) {
                if (emailValue != null && startDateValue != null) {
                    List<Event> temp = getInvitedEventsForDate(emailValue, startDateValue);
                    boolean b;
                    for (Event event : temp) {
                        b = true;
                        for (Event event1 : (List<Event>) list) {
                            if (event.getId() == event1.getId()) {
                                b = false;
                                break;
                            }
                        }
                        if (b) {
                            list.add(event);
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            list = new ArrayList<String>();
            list.add("Error");
        }

        return list;
    }

    /**
     * Get the events a user is invited to on a date.
     * @param email the user email.
     * @param date  the date to look for.
     * @return the List with the relevant events.
     */
    public static List<Event> getInvitedEventsForDate(String email, String date) {
        List<Event> list = new ArrayList<>();
        try {
            if (!validateEntry("users", new String[] {"email"}, new String[] {email})) {
                return null;
            }
            String sql = "SELECT * FROM events where (id) IN (SELECT invited.id FROM invited WHERE invited.email = \'" + email + "\') AND events.startTime LIKE \'" + date + "%\'";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(resultSet.getTimestamp("startTime").getTime());
                Calendar endDate = Calendar.getInstance();
                endDate.setTimeInMillis(resultSet.getTimestamp("endTime").getTime());
                Event event = new Event(resultSet.getString("description"), startDate, endDate, resultSet.getString("loc_name"), resultSet.getString("email"));
                event.setId(resultSet.getInt("id"));
                list.add(event);
            }
        }
        catch (SQLException e) {
            list = new ArrayList<>();
            list.add(new Event("Error", null, null, null, null));
        }
        return list;
    }

    /**
     * Takes a string array and returns a string containing the values of the array separated by the provided dividers.
     * @param values       The values to concatenate.
     * @param leftDivider  Left divider.
     * @param rightDivider Right divider.
     * @return The concatenated string.
     */
    private static String formatValues(String[] values, String leftDivider, String rightDivider) {
        StringBuilder stringBuilder = new StringBuilder(values.length);

        stringBuilder.append(leftDivider + values[0] + rightDivider);
        int counter = 1;

        while (counter < values.length) {
            stringBuilder.append("," + leftDivider + values[counter] + rightDivider);
            counter++;
        }

        return stringBuilder.toString();
    }

    /**
     * Get the the current event id.
     * @return the event id.
     */
    public static int getIdCount() {
        try (Statement stmt = connection.createStatement()) {
            String sql = "SELECT count(*) AS count FROM events";
            ResultSet resultSet = stmt.executeQuery(sql);

            if (resultSet.next()) {
                return resultSet.getInt("count");
            }

            return 0;
        }
        catch (SQLException e) {
            return 0;
        }
    }

}
