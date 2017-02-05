package modelTests;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import model.Event;

public class EventTests {

	Event event;

	@Test
	public void test() {
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = startTime;
		endTime.add(Calendar.HOUR, 2);
		event = new Event("Description", startTime, endTime, "Location", "Owner");
		assertEquals(event.getTitle(), "Description");
		assertEquals(event.getEndDate(), endTime);
		assertEquals(event.getStartDate(), startTime);
		assertEquals(event.getLocation(), "Location");
		assertEquals(event.getOwner(), "Owner");
	}

}
