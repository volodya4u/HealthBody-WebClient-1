package edu.softserveinc.healthbody.webclient.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import edu.softserveinc.healthbody.webclient.healthbody.webservice.UserDTO;

public class UsersControllerTest extends ControllerTestSupport {

	private List<UserDTO> users(int count) {
		List<UserDTO> users = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			users.add(user("user" + i, "user"));
		}
		return users;
	}

	@Test
	public void showsTheRequestedPageOfFourUsers() throws Exception {
		List<UserDTO> secondPage = users(4);
		when(service.getAllUsers(1, Integer.MAX_VALUE)).thenReturn(users(9));
		when(service.getAllUsers(2, 4)).thenReturn(secondPage);

		mockMvc(new UsersController()).perform(get("/userlist.html").param("partNumber", "2"))
				.andExpect(view().name("userList")).andExpect(model().attribute("login", LOGIN))
				.andExpect(model().attribute("currentPage", 2)).andExpect(model().attribute("lastPartNumber", 3))
				.andExpect(model().attribute("AllUsers", secondPage));
	}

	@Test
	public void pageNumbersPastTheEndShowTheLastPage() throws Exception {
		List<UserDTO> lastPage = users(1);
		when(service.getAllUsers(1, Integer.MAX_VALUE)).thenReturn(users(9));
		when(service.getAllUsers(3, 4)).thenReturn(lastPage);

		mockMvc(new UsersController()).perform(get("/userlist.html").param("partNumber", "7"))
				.andExpect(model().attribute("currentPage", 3)).andExpect(model().attribute("AllUsers", lastPage));
	}

	@Test
	public void missingOrNegativePageNumbersShowTheFirstPage() throws Exception {
		when(service.getAllUsers(1, Integer.MAX_VALUE)).thenReturn(users(9));
		when(service.getAllUsers(1, 4)).thenReturn(Collections.<UserDTO>emptyList());

		mockMvc(new UsersController()).perform(get("/userlist.html")).andExpect(model().attribute("currentPage", 1));
		mockMvc(new UsersController()).perform(get("/userlist.html").param("partNumber", "-2"))
				.andExpect(model().attribute("currentPage", 1));
	}
}
