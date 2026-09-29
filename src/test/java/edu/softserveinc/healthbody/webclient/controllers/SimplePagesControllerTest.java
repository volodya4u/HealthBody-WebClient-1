package edu.softserveinc.healthbody.webclient.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SimplePagesControllerTest extends ControllerTestSupport {

	private MockMvc mvc;

	@BeforeMethod
	public void setUp() {
		mvc = mockMvc(new LoginController(), new HomePageController());
	}

	@Test
	public void loginPathShowsTheLoginPage() throws Exception {
		mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"))
				.andExpect(forwardedUrl("/WEB-INF/views/login.jsp"));
	}

	@Test
	public void loginHtmlShowsTheLoginPage() throws Exception {
		mvc.perform(get("/login.html")).andExpect(view().name("login"));
	}

	@Test
	public void homePageShowsTheHomePage() throws Exception {
		mvc.perform(get("/homePage.html")).andExpect(status().isOk()).andExpect(view().name("homePage"));
	}
}
