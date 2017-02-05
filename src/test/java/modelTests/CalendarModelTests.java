package modelTests;

import Utilites.H2TestServer;
import model.CalendarModel;
import org.junit.*;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CalendarModelTests {

	String desc = "description";
	java.util.Calendar startTime = java.util.Calendar.getInstance();
	java.util.Calendar endTime = java.util.Calendar.getInstance();
	String locName = "Room 1";
	String userEmail = "email@email.com";

	static CalendarModel calendar;
	static H2TestServer server = H2TestServer.getInstance();

	@Before
	public void createTables() throws SQLException {
		server.createTables();
		calendar = new CalendarModel();
	}

	@After
	public void dropTables() throws SQLException {
		server.dropTables();
	}

	@Test
	public void addUser() {
		calendar.addUser("Test User", userEmail);
		assertEquals(1, calendar.getUsers().size());
	}

	@Test
	public void addMultipleUsers() {
		calendar.addUser("Test User", userEmail);
		calendar.addUser("Test User2", "email2@email.com");
		assertEquals(2, calendar.getUsers().size());
	}

	@Test
	public void addUsersSameEmail() {
		calendar.addUser("Test User", userEmail);
		int status = calendar.addUser("Test User", userEmail);
		assertEquals(1, calendar.getUsers().size());
		assertEquals(-1, status);
	}

	@Test
	public void addUserInvalidEmail() {
		int status = calendar.addUser("Test User", "abcdefg");
		assertEquals(-1, status); // Error
		assertEquals(0, calendar.getUsers().size());
	}

	@Test
	public void addUserNullInput() {
		int status = calendar.addUser(null, userEmail);
		assertEquals(-1, status); // Error
		assertEquals(0, calendar.getUsers().size());
	}

	@Test
	public void addLocation() throws SQLException {
		calendar.addLocation(locName, 10);
		assertEquals(1, calendar.getLocations().size());
	}

	@Test
	public void addMultipleLocations() {
		calendar.addLocation(locName, 10);
		calendar.addLocation("Room 2", 15);
		assertEquals(2, calendar.getLocations().size());
	}

	@Test
	public void addLocationsSameName() {
		calendar.addLocation(locName, 10);
		int status = calendar.addLocation(locName, 15);
		assertEquals(-1, status);
		assertEquals(1, calendar.getLocations().size());
	}

	@Test
	public void addLocationNullInput() {
		calendar.addLocation(null, 10);
		assertEquals(0, calendar.getLocations().size());
	}

	@Test
	public void addEvent() {
		calendar.addUser("Test User", userEmail);
		calendar.addLocation(locName, 10);
		int id = calendar.addEvent(desc, startTime, endTime, locName, userEmail, null);
		assertEquals(1, calendar.getEvents().size());
		assertEquals(0, id);
	}

	@Test
	public void addMultipleEvents() throws SQLException {
		calendar.addUser("Test User", userEmail);
		calendar.addLocation(locName, 10);
		int id1 = calendar.addEvent(desc, startTime, endTime, locName, userEmail, null);
		startTime.add(java.util.Calendar.HOUR, 10);
		endTime.add(java.util.Calendar.HOUR, 12);
		int id2 = calendar.addEvent("aaa", startTime, endTime, locName, userEmail, null);
		assertEquals(0, id1);
		assertEquals(1, id2);
		assertEquals(2, calendar.getEvents().size());
	}

	@Test
	public void addEventUserNotRegistered() {
		calendar.addLocation(locName, 10);
		int status = calendar.addEvent(desc, startTime, endTime, locName, userEmail, null);
		assertEquals(-1, status);
		assertEquals(0, calendar.getEvents().size());
	}

	@Test
	public void addEventLocationNotAdded() {
		calendar.addUser("Test User", userEmail);
		int status = calendar.addEvent(desc, startTime, endTime, locName, userEmail, null);
		assertEquals(-1, status);
		assertEquals(0, calendar.getEvents().size());
	}

	@Test
	public void addEventDescriptionNull() {
		calendar.addUser("Test User", userEmail);
		calendar.addLocation(locName, 10);
		int id = calendar.addEvent(null, startTime, endTime, locName, userEmail, null);
		assertEquals(-1, id);
		assertEquals(0, calendar.getEvents().size());
	}

	@Test
	public void addEventWithInvitedUsers() {
		calendar.addUser("Test User", userEmail);
		calendar.addUser("Test User1", "abc@gmail.com");
		calendar.addUser("Test User2", "def@gmail.com");
		calendar.addLocation(locName, 10);

		String[] users = new String[2];
		users[0] = "abc@gmail.com";
		users[1] = "def@gmail.com";

		int id = calendar.addEvent(desc, startTime, endTime, locName, userEmail, users);
		assertEquals(0, id);
		assertEquals(1, calendar.getEvents().size());
	}

	@Test
	public void addEventWithInvitedUsersRoomCapacityLow() {
		calendar.addUser("Test User", userEmail);
		calendar.addUser("Test User1", "abc@gmail.com");
		calendar.addUser("Test User2", "def@gmail.com");
		calendar.addLocation(locName, 1);

		String[] users = new String[2];
		users[0] = "abc@gmail.com";
		users[1] = "def@gmail.com";

		int id = calendar.addEvent(desc, startTime, endTime, locName, userEmail, users);
		assertEquals(-1, id);
		assertEquals(0, calendar.getEvents().size());
	}

	//TODO redo
	//@Test
	//public void checkWhenUserFree() throws ParseException {
	//	calendar.addUser("Test User", userEmail);
	//	calendar.addLocation(locName, 10);
	//	endTime = Calendar.getInstance();
	//	endTime.add(Calendar.HOUR, 2);
	//	calendar.addEvent(desc, startTime, endTime, locName, userEmail, null);
    //
	//	String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	//	Calendar now = Calendar.getInstance();
	//	HashMap<Integer, Boolean> list = calendar.checkWhenFree("email@email.com", date, 'U');
	//	assertTrue(!(list.get(now.get(Calendar.HOUR_OF_DAY)) && list.get(now.get(Calendar.HOUR_OF_DAY) + 1)
	//			&& list.get(now.get(Calendar.HOUR_OF_DAY) + 2)));
	//}

    //TODO redo
	//@Test
	//public void checkWhenRoomFree() throws ParseException {
	//	calendar.addUser("Test User", userEmail);
	//	calendar.addLocation(locName, 10);
	//	endTime = Calendar.getInstance();
	//	endTime.add(Calendar.HOUR, 2);
	//	calendar.addEvent(desc, startTime, endTime, locName, userEmail, null);
	//	String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	//	Calendar now = Calendar.getInstance();
	//	HashMap<Integer, Boolean> list = calendar.checkWhenFree(locName, date, 'L');
	//	System.out.println(list);
	//	assertTrue(!(list.get(now.get(Calendar.HOUR_OF_DAY)) && list.get(now.get(Calendar.HOUR_OF_DAY) + 1)
	//			&& list.get(now.get(Calendar.HOUR_OF_DAY) + 2)));
	//	assertTrue(list.get((Calendar.HOUR_OF_DAY) - 5));
	//}

	@Test
	public void addMultipleEventsDoubleBooking() throws SQLException, ParseException {
		calendar.addUser("Test User", userEmail);
		calendar.addLocation(locName, 10);

		Calendar cal1 = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dateInString = "14-06-2011 10:00:00";
		Date date = sdf.parse(dateInString);
		cal1.setTime(date);

		Calendar cal2 = Calendar.getInstance();
		String dateInString2 = "14-06-2011 10:30:00";
		Date date2 = sdf.parse(dateInString2);
		cal2.setTime(date2);

		Calendar cal3 = Calendar.getInstance();
		String dateInString3 = "14-06-2011 10:15:00";
		Date date3 = sdf.parse(dateInString3);
		cal3.setTime(date3);

		Calendar cal4 = Calendar.getInstance();
		String dateInString4 = "14-06-2011 12:30:00";
		Date date4 = sdf.parse(dateInString4);
		cal4.setTime(date4);

		int id1 = calendar.addEvent(desc, cal1, cal2, locName, userEmail, null);
		int id2 = calendar.addEvent("aaa", cal3, cal4, locName, userEmail, null);
		assertEquals(0, id1);
		assertEquals(-1, id2);
		assertEquals(1, calendar.getEvents().size());
	}

	@Test
	public void addMultipleEventsDoubleBooking2() throws SQLException, ParseException {
		calendar.addUser("Test User", userEmail);
		calendar.addLocation(locName, 10);

		Calendar cal1 = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dateInString = "14-06-2011 10:00:00";
		Date date = sdf.parse(dateInString);
		cal1.setTime(date);

		Calendar cal2 = Calendar.getInstance();
		String dateInString2 = "14-06-2011 10:30:00";
		Date date2 = sdf.parse(dateInString2);
		cal2.setTime(date2);

		Calendar cal3 = Calendar.getInstance();
		String dateInString3 = "14-06-2011 10:15:00";
		Date date3 = sdf.parse(dateInString3);
		cal3.setTime(date3);

		Calendar cal4 = Calendar.getInstance();
		String dateInString4 = "14-06-2011 10:20:00";
		Date date4 = sdf.parse(dateInString4);
		cal4.setTime(date4);

		int id1 = calendar.addEvent(desc, cal1, cal2, locName, userEmail, null);
		int id2 = calendar.addEvent("aaa", cal3, cal4, locName, userEmail, null);
		assertEquals(0, id1);
		assertEquals(-1, id2);
		assertEquals(1, calendar.getEvents().size());
	}

	@Test
	public void addMultipleEventsDoubleBooking3() throws SQLException, ParseException {
		calendar.addUser("Test User", userEmail);
		calendar.addLocation(locName, 10);

		Calendar cal1 = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dateInString = "14-06-2011 10:00:00";
		Date date = sdf.parse(dateInString);
		cal1.setTime(date);

		Calendar cal2 = Calendar.getInstance();
		String dateInString2 = "14-06-2011 10:30:00";
		Date date2 = sdf.parse(dateInString2);
		cal2.setTime(date2);

		Calendar cal3 = Calendar.getInstance();
		String dateInString3 = "14-06-2011 10:30:00";
		Date date3 = sdf.parse(dateInString3);
		cal3.setTime(date3);

		Calendar cal4 = Calendar.getInstance();
		String dateInString4 = "14-06-2011 11:00:00";
		Date date4 = sdf.parse(dateInString4);
		cal4.setTime(date4);

		int id1 = calendar.addEvent(desc, cal1, cal2, locName, userEmail, null);
		int id2 = calendar.addEvent("aaa", cal3, cal4, locName, userEmail, null);
		assertEquals(0, id1);
		assertEquals(1, id2);
		assertEquals(2, calendar.getEvents().size());
	}

	@Test
	public void addMultipleEventsDoubleBooking4() throws SQLException, ParseException {
		calendar.addUser("Test User", userEmail);
		calendar.addLocation(locName, 10);

		Calendar cal1 = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dateInString = "14-06-2011 10:00:00";
		Date date = sdf.parse(dateInString);
		cal1.setTime(date);

		Calendar cal2 = Calendar.getInstance();
		String dateInString2 = "14-06-2011 10:30:00";
		Date date2 = sdf.parse(dateInString2);
		cal2.setTime(date2);

		Calendar cal3 = Calendar.getInstance();
		String dateInString3 = "14-06-2011 11:30:00";
		Date date3 = sdf.parse(dateInString3);
		cal3.setTime(date3);

		Calendar cal4 = Calendar.getInstance();
		String dateInString4 = "14-06-2011 12:00:00";
		Date date4 = sdf.parse(dateInString4);
		cal4.setTime(date4);

		int id1 = calendar.addEvent(desc, cal1, cal2, locName, userEmail, null);
		int id2 = calendar.addEvent("aaa", cal3, cal4, locName, userEmail, null);
		assertEquals(0, id1);
		assertEquals(1, id2);
		assertEquals(2, calendar.getEvents().size());
	}
}
