package edu.softserveinc.healthbody.webclient.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.mockito.MockedStatic;
import org.testng.annotations.Test;

import edu.softserveinc.healthbody.webclient.healthbody.webservice.CompetitionDTO;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.UserDTO;
import edu.softserveinc.healthbody.webclient.utils.GoogleFitUtils;

public class UserCabinetControllerTest extends ControllerTestSupport {

	@Test
	public void showsTheUserAndTheirCompetitions() throws Exception {
		UserDTO user = user(LOGIN, "user");
		List<CompetitionDTO> competitions = Collections.singletonList(new CompetitionDTO());
		when(service.getUserByLogin(LOGIN)).thenReturn(user);
		when(service.getAllCompetitionsByUser(1, Integer.MAX_VALUE, LOGIN)).thenReturn(competitions);

		try (MockedStatic<GoogleFitUtils> googleFit = mockStatic(GoogleFitUtils.class)) {
			googleFit.when(() -> GoogleFitUtils.postForAccessToken(any())).thenReturn("access-token");
			googleFit.when(() -> GoogleFitUtils.post(anyString(), anyLong(), anyLong())).thenReturn("fit-data");
			googleFit.when(() -> GoogleFitUtils.getStepCount("fit-data")).thenReturn("100");

			mockMvc(new UserCabinetController()).perform(get("/userCabinet.html"))
					.andExpect(view().name("userCabinet")).andExpect(model().attribute("user", user))
					.andExpect(model().attribute("usercompetitions", competitions));
		}
	}
}
