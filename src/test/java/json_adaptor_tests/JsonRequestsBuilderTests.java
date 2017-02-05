package json_adaptor_tests;

import json_adaptor.JsonRequestBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class JsonRequestsBuilderTests {

	String addEvent = "{\"addEvent\":{\"description\":\"Test\",\"startTime\":\"01-01-00 00:00:00\",\"endTime\":\"02-01-00 00:00:00\",\"location\":\"Location Name\",\"owner\":\"example@email.com\"}}";
	String addEventWithUser = "{\"addEvent\":{\"description\":\"Test\",\"startTime\":\"01-01-00 00:00:00\",\"endTime\":\"02-01-00 00:00:00\",\"location\":\"Location Name\",\"owner\":\"example@email.com\",\"invited\":[\"example2@email.com\"]}}";

	String addUser = "{\"addUser\":{\"name\":\"Tester\",\"email\":\"example@email.com\"}}";

	String addLocation = "{\"addLocation\":{\"name\":\"Room\",\"capacity\":5}}";

	String getAllEvents = "{\"getAllEvents\":{}}";

	String getAllEventsOnDate = "{\"getAllEvents\":{\"email\":\"admin@admin.com\",\"date\":\"2000-01-01\"}}";

	String getAllEventsByUser = "{\"getAllEvents\":{\"email\":\"example@email.com\"}}";

	String getGetAllEventsAtLocation = "{\"getAllEvents\":{\"location\":\"Location Name\"}}";

	String userExists = "{\"userExists\":{\"email\":\"example@email.com\"}}";

	JsonRequestBuilder jrb;

	@Before
	public void setUp() {
		jrb = new JsonRequestBuilder();
	}

	@Test
	public void addEventTest() {
		String json = jrb.makeAddEventRequest("Test", LocalDate.of(2000, 01, 01), LocalTime.of(0, 0, 0),
				LocalDate.of(2000, 01, 02), LocalTime.of(0, 0, 0), "Location Name", "example@email.com", null,null,0);
		assertEquals(addEvent, json);
	}

	@Test
	public void addEventTestWithInvited() {
		String json = jrb.makeAddEventRequest("Test", LocalDate.of(2000, 01, 01), LocalTime.of(0, 0, 0),
				LocalDate.of(2000, 01, 02), LocalTime.of(0, 0, 0), "Location Name", "example@email.com", new String[]{"example2@email.com"}, null, 0);
		assertEquals(addEventWithUser, json);
	}

	@Test
	public void addUserTest() {
		String json = jrb.makeAddUserRequest("Tester", "example@email.com");
		assertEquals(addUser, json);
	}

	@Test
	public void addLocationTest() {
		String json = jrb.makeAddLocationRequest("Room", 5);
		assertEquals(addLocation, json);
	}

	@Test
	public void getAllEvents() {
		String json = jrb.getAllEvents();
		assertEquals(getAllEvents, json);
	}

	@Test
	public void getAllEventsOnDateTest() {
		String json = jrb.getAllEventsOnDate("admin@admin.com",LocalDate.of(2000, 01, 01));
		assertEquals(getAllEventsOnDate, json);
	}

	@Test
	public void getAllEventsByUserTest() {
		String json = jrb.getAllEventsByUser("example@email.com");
		assertEquals(getAllEventsByUser, json);
	}

	@Test
	public void getAllEventsAtLocationTest() {
		String json = jrb.getAllEventsAtLocation("Location Name");
		assertEquals(getGetAllEventsAtLocation, json);
	}

	@Test
	public void userExistsTest() {
		String json = jrb.userExists("example@email.com");
		assertEquals(userExists, json);
	}

}
