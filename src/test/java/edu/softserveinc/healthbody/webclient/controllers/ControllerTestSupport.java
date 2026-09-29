package edu.softserveinc.healthbody.webclient.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import edu.softserveinc.healthbody.webclient.healthbody.webservice.HealthBodyService;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.HealthBodyServiceImplService;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.UserDTO;

/**
 * Runs controllers through Spring MVC with a mocked SOAP client and a logged-in user.
 */
public abstract class ControllerTestSupport {

	protected static final String LOGIN = "user1";

	protected HealthBodyServiceImplService healthBody;
	protected HealthBodyService service;

	@BeforeMethod
	public void mockSoapClientAndLogIn() {
		service = mock(HealthBodyService.class);
		healthBody = mock(HealthBodyServiceImplService.class);
		when(healthBody.getHealthBodyServiceImplPort()).thenReturn(service);
		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(LOGIN, "password"));
	}

	@AfterMethod(alwaysRun = true)
	public void logOut() {
		SecurityContextHolder.clearContext();
	}

	protected static MockMvc mockMvc(Object... controllers) {
		return MockMvcBuilders.standaloneSetup(controllers)
				.setViewResolvers(new InternalResourceViewResolver("/WEB-INF/views/", ".jsp")).build();
	}

	/**
	 * The controllers take the SOAP client as a model attribute, so the request
	 * passes the mock in as a flash attribute, which Spring MVC adds to the model.
	 */
	protected MockHttpServletRequestBuilder get(String url) {
		return MockMvcRequestBuilders.get(url).flashAttr("healthBodyServiceImplService", healthBody);
	}

	protected MockHttpServletRequestBuilder post(String url) {
		return MockMvcRequestBuilders.post(url).flashAttr("healthBodyServiceImplService", healthBody);
	}

	protected static UserDTO user(String login, String roleName) {
		UserDTO user = new UserDTO();
		user.setLogin(login);
		user.setRoleName(roleName);
		return user;
	}
}
