package json_adaptor_tests;

import Utilites.H2TestServer;
import model.CalendarModel;
import org.junit.*;
import json_adaptor.JsonAdaptor;
import sql.ConnectionHandler;

import java.lang.reflect.Field;
import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonAdaptorTests {

	String error = "Error";

	String addEvent = "{\"addEvent\":{\"description\":\"Test\",\"startTime\":\"01-01-00 00:00:00\",\"endTime\":\"02-01-00 00:00:00\",\"location\":\"Location Name\",\"owner\":\"example@email.com\"}}";
	String addEventNoDescription = "{\"addEvent\":{\"startTime\":\"01-01-00 00:00:00\",\"endTime\":\"02-01-00 00:00:00\",\"location\":\"Location Name\",\"owner\":\"example@email.com\"}}";
	String addEventWithInvited = "{\"addEvent\":{\"description\":\"Test\",\"startTime\":\"01-01-00 00:00:00\",\"endTime\":\"02-01-00 00:00:00\",\"location\":\"Location Name\",\"owner\":\"example@email.com\",\"invited\":[\"example2@email.com\"]}}";
	String addEventWithExtraField = "{\"addEvent\":{\"startTime\":\"01-01-00 00:00:00\",\"endTime\":\"02-01-00 00:00:00\",\"location\":\"Location Name\",\"owner\":\"example@email.com\",\"extra\":\"field\"}}";

	String addUser = "{\"addUser\":{\"name\":\"Tester\",\"email\":\"anotherexample@email.com\"}}";
	String addUserNoName = "{\"addUser\":{\"email\":\"anotherexample@email.com\"}}";

	String addLocation = "{\"addLocation\":{\"name\":\"Room\",\"capacity\":5}}";
	String addLocationNoCapacity = "{\"addLocation\":{\"name\":\"Room\"}}";

	String userExists = "{\"userExists\":{\"email\":\"example@email.com\"}}";
	String userNotExists = "{\"userExists\":{\"email\":\"invalid@email.com\"}}";

	String getAllEvents = "{\"getAllEvents\":{}}";

	String getAllEventsByUser = "{\"getAllEvents\":{\"email\":\"example@email.com\"}}";

	String getAllEventsAtLocation = "{\"getAllEvents\":{\"location\":\"Location Name\"}}";

	String getAllEventsOnDate = "{\"getAllEvents\":\"{\"date\":\"01-01-00\"}\"}";

	static JsonAdaptor adaptor;
	static CalendarModel cal;
	static H2TestServer server = H2TestServer.getInstance();

	@BeforeClass
	public static void setUp() throws Exception {
		// change the connection in ConnectionHandler to the h2 database in
		// memory
		ConnectionHandler ch = ConnectionHandler.getInstance();
		Field f = ConnectionHandler.class.getDeclaredField("connection");
		f.setAccessible(true);
		f.set(ch, server.getConnection());
	}

	@Before
	public void createTables() throws Exception {
		server.createTables();

		// construct the jsonAdaptor
		// needs to be called after the connection is changed
		adaptor = new JsonAdaptor();

		// set cal to accessible to add test events
		Field field = JsonAdaptor.class.getDeclaredField("cal");
		field.setAccessible(true);
		cal = (CalendarModel) field.get(adaptor);
		cal.addUser("Tester", "example@email.com");
		cal.addLocation("Location Name", 5);
	}

	@After
	public void dropTables() throws SQLException {
		server.dropTables();
	}

	public void setupEvent() {
		cal.addEvent("Test", java.util.Calendar.getInstance(), java.util.Calendar.getInstance(), "Location Name",
				"example@email.com", null);
	}

	@Test
	public void addEvent() {
		String response = adaptor.request(addEvent);
		assertEquals("0", response);
	}

	@Test
	public void addInvalidEventJson() {
		String response = adaptor.request(addEventNoDescription);
		assertEquals(error, response);
	}

	@Test
	public void addEventWithInvited() {
		cal.addUser("Tester2", "example2@email.com");
		String response = adaptor.request(addEventWithInvited);
		assertEquals("0", response);
	}

	@Test
	public void addInvalidEventWithInvitedJson() {
		String response = adaptor.request(addEventWithExtraField);
		assertEquals(error, response);
	}

	@Test
	public void addUser() {
		String response = adaptor.request(addUser);
		assertEquals("0", response);
	}

	@Test
	public void addInvalidUserJson() {
		String response = adaptor.request(addUserNoName);
		assertEquals(error, response);
	}

	@Test
	public void addLocation() {
		String response = adaptor.request(addLocation);
		assertEquals("0", response);
	}

	@Test
	public void addInvalidLocationJson() {
		String response = adaptor.request(addLocationNoCapacity);
		assertEquals(error, response);
	}

	@Test
	public void userExistsTest() {
		String response = adaptor.request(userExists);
		assertEquals("0", response);
	}

	@Test
	public void userNotExistsTest() {
		String response = adaptor.request(userNotExists);
		assertEquals(error, response);
	}

	@Test
	public void getAllEvents() {
		setupEvent();
		String responseAllEvents = adaptor.request(getAllEvents);
		assertTrue(responseAllEvents != null);
	}

	@Test
	public void getAllEventsByUser() {
		setupEvent();
		String responseAllEvents = adaptor.request(getAllEventsByUser);
		assertTrue(responseAllEvents != null);
	}

	@Test
	public void getAllEventsAtLocation() {
		setupEvent();
		String responseAllEvents = adaptor.request(getAllEventsAtLocation);
		assertTrue(responseAllEvents != null);
	}

	@Test
	public void getAllEventsOnDate() {
		setupEvent();
		String responseAllEvents = adaptor.request(getAllEventsOnDate);
		assertTrue(responseAllEvents != null);
	}

}
