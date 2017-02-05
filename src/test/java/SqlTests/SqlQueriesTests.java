package SqlTests;

import Utilites.H2TestServer;
import model.Event;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sql.SqlQueries;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by irs6 on 14/12/16.
 */
public class SqlQueriesTests {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String USER_TABLE = "users";
    private static final String EMAIL_FIELD = "email";
    private static final String NAME_FIELD = "name";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String LOCATION_TABLE = "locations";
    private static final String LOCATION_FIELD = "loc_name";
    private static final String CAPACITY_FIELD = "capacity";
    private static final String STARTTIME_FIELD = "startTime";
    private static final String ENDTIME_FIELD = "endTime";
    private static final String INVITED_TABLE = "invited";
    private static final String EVENT_TABLE = "events";
    private static final String ID_FIELD = "id";
    private static final String ERROR_FIELD = "Error";
    private static final String[] userFields = new String[]{EMAIL_FIELD, NAME_FIELD};
    private static final String[] locationFields = new String[]{LOCATION_FIELD, CAPACITY_FIELD};
    private static final String[] eventFields = new String[]{ID_FIELD, DESCRIPTION_FIELD, LOCATION_FIELD, STARTTIME_FIELD, ENDTIME_FIELD, EMAIL_FIELD};
    private static final String testUserName = "Test";
    private static final String testUserEmail = "email@email.com";
    private static final String testLocationName = "Room 1";
    private static final String testLocationCapacity = "5";
    private static final String testDescription = "TEST";
    private static final String testStartTime = format.format(new java.sql.Date(java.util.Calendar.getInstance().getTimeInMillis()));
    private static final String testEndTime = format.format(new java.sql.Date(java.util.Calendar.getInstance().getTimeInMillis()));
    private static final String testID = "0";
    private static final String[] userValues = new String[]{testUserEmail, testUserName};
    private static final String[] locationValues = new String[]{testLocationName, testLocationCapacity};
    private static final String[] eventValues = new String[]{testID, testDescription, testLocationName, testStartTime, testEndTime, testUserEmail};
    private static final String testUserEmail2 = "email2@email.com";
    private static final String testLocationName2 = "Room 2";

    static H2TestServer server = H2TestServer.getInstance();

    @Before
    public void createTables() throws SQLException {
        server.createTables();
        SqlQueries.addEntry(USER_TABLE, userFields, userValues);
        SqlQueries.addEntry(LOCATION_TABLE, locationFields, locationValues);
        SqlQueries.addEntry(EVENT_TABLE, eventFields, eventValues);
    }



    @After
    public void dropTables() throws SQLException {
        server.dropTables();
    }

    @Test
    public void validateEventEntryTest() throws SQLException {
        boolean b = SqlQueries.validateEntry(EVENT_TABLE, eventFields, eventValues);
        assertTrue(b);
    }

    @Test
    public void validateLocationEntryTest() throws SQLException {
        boolean b = SqlQueries.validateEntry(LOCATION_TABLE, locationFields, locationValues);
        assertTrue(b);
    }

    @Test
    public void validateUserEntryTest() throws SQLException {
        boolean b = SqlQueries.validateEntry(USER_TABLE, userFields, userValues);
        assertTrue(b);
    }

    @Test
    public void validateInvalidEventEntryTest() throws SQLException {
        boolean b = SqlQueries.validateEntry(EVENT_TABLE, eventFields, new String[]{"1", testDescription, testLocationName, testStartTime, testEndTime, testUserEmail});
        assertFalse(b);
    }

    @Test
    public void validateInvalidLocationEntryTest() throws SQLException {
        boolean b = SqlQueries.validateEntry(LOCATION_TABLE, locationFields, new String[]{testLocationName2, testLocationCapacity});
        assertFalse(b);
    }

    @Test
    public void validateInvalidUserEntryTest() throws SQLException {
        boolean b = SqlQueries.validateEntry(USER_TABLE, userFields, new String[]{testUserEmail2, testUserName});
        assertFalse(b);
    }

    @Test
    public void addEventEntryTest() throws SQLException{
        int i = SqlQueries.addEntry(EVENT_TABLE, eventFields, new String[]{"1", testDescription, testLocationName, testStartTime, testEndTime, testUserEmail});
        assertEquals(i, 0);
    }

    @Test
    public void addUserEntryTest() throws SQLException{
        int i = SqlQueries.addEntry(USER_TABLE, userFields, new String[]{testUserEmail2, testUserName});
        assertEquals(i, 0);
    }

    @Test
    public void addLocationEntryTest() throws SQLException{
        int i = SqlQueries.addEntry(LOCATION_TABLE, locationFields, new String[]{testLocationName2, testLocationCapacity});
        assertEquals(i, 0);
    }

    @Test
    public void  addInvalidEntryTest() throws SQLException{
        boolean thrown = false;
        try {
            int i = SqlQueries.addEntry(EVENT_TABLE, locationFields, new String[]{testLocationName2, testLocationCapacity});
            assertEquals(i, -1);
        }
        catch (SQLException e){
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void getEntriesEventTest() throws SQLException{
        List<User> users = SqlQueries.getEntries(USER_TABLE, userFields, userValues, null);
        assertFalse(users.isEmpty());
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getName(), testUserName);
        assertEquals(users.get(0).getEmail(), testUserEmail);
    }

    @Test
    public void getInvitedEventsByDate() throws SQLException{
        SqlQueries.addEntry(INVITED_TABLE, new String[]{"id", "email"}, new String[]{"0", testUserEmail});
        List<Event> events = SqlQueries.getInvitedEventsForDate(testUserEmail, testStartTime);
        assertFalse(events == null);
        assertFalse(events.isEmpty());
        assertEquals(events.size(), 1);
    }

    @Test
    public void getIdCountTest() throws SQLException{
        int count = SqlQueries.getIdCount();
        assertEquals(count,  1);
    }
}
