package modelTests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.Location;

public class LocationTests {

	Location location;
	
	@Test
	public void createLocation() {
		location = new Location("Location One", 5);
		assertEquals(location.getLocationName(), "Location One");
		assertEquals(location.getCapacity(), 5);
	}

}
