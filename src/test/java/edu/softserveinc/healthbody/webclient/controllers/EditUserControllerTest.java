package edu.softserveinc.healthbody.webclient.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import edu.softserveinc.healthbody.webclient.healthbody.webservice.UserDTO;

public class EditUserControllerTest extends ControllerTestSupport {

	@Test
	public void showsTheLoggedInUserForEditing() throws Exception {
		UserDTO user = user(LOGIN, "user");
		when(service.getUserByLogin(LOGIN)).thenReturn(user);

		mockMvc(new EditUserController()).perform(get("/editUser.html")).andExpect(view().name("editUser"))
				.andExpect(model().attribute("userToEdit", user));
	}

	@Test
	public void savesTheEditedProfileFields() throws Exception {
		UserDTO stored = user(LOGIN, "user");
		stored.setEmail("user1@example.com");
		when(service.getUserByLogin(LOGIN)).thenReturn(stored);

		mockMvc(new EditUserController())
				.perform(post("/editUser.html").param("firstname", "Anna").param("lastname", "Koval")
						.param("age", "30").param("weight", "60").param("gender", "female").param("health", "good"))
				.andExpect(view().name("userCabinet")).andExpect(model().attribute("user", stored));

		verify(service).updateUser(stored);
		assertEquals(stored.getFirstname(), "Anna");
		assertEquals(stored.getLastname(), "Koval");
		assertEquals(stored.getAge(), "30");
		assertEquals(stored.getWeight(), "60");
		assertEquals(stored.getGender(), "female");
		assertEquals(stored.getHealth(), "good");
		assertEquals(stored.getEmail(), "user1@example.com");
	}
}
