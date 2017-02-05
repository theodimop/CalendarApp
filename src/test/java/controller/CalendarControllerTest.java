package controller;

import gui.CalendarController;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CalendarControllerTest {

	public static CalendarController controller = new CalendarController();
	private static final String VALID_USER = "admin@admin.com";
	private static final String INVALID_USER = "invalid@invalid.com";

	/**
	 * Test valid user Authentication in controller.
	 */
// This test has been commented out as it needs the backend to run.
// Possible solutions are:
// 1. To run the back end first.
// 2. Create a mock object for the back end.
// 3. Create an interface and pass in values.
// These were not implemented due to time constrains.
//	@Test
//	public void testUserAuthenticationSuccess() throws Exception {
//		Method method = controller.getClass().getDeclaredMethod("authenticateUser", Optional.class);
//		method.setAccessible(true);
//		boolean res = (boolean) method.invoke(controller, Optional.of(VALID_USER));
//
//		assertTrue(res);
//	}

	/**
	 * Test invalid user Authentication in controller.
	 */
	@Test
	public void testUserAuthenticationFail() throws Exception {
		Method method = controller.getClass().getDeclaredMethod("authenticateUser", Optional.class);
		method.setAccessible(true);
		boolean res = (boolean) method.invoke(controller, Optional.of(INVALID_USER));

		assertFalse(res);
	}

	@Test
	public void testAuthentication() throws Exception {
		String username = "-1";
		assertFalse(controller.authenticateUser(Optional.of(username)));
	}


}
