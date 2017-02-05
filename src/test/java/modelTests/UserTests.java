package modelTests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.User;

public class UserTests {
	User user;

	@Test
	public void CreateUser() {
		user = new User("Mr John Smith", "johnsmith@example.com");
		assertEquals(user.getEmail(), "johnsmith@example.com");
		assertEquals(user.getName(), "Mr John Smith");
	}

}
