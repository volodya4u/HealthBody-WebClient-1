package edu.softserveinc.healthbody.webclient.controllers;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.mockito.MockedConstruction;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.annotations.Test;

import edu.softserveinc.healthbody.webclient.healthbody.webservice.CompetitionDTO;
import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

public class MainPageControllerTest extends ControllerTestSupport {

	private static final String LVIV_WEATHER = "{\"cod\":200,\"name\":\"Lviv\",\"main\":{\"temp\":12.5,\"humidity\":81},"
			+ "\"weather\":[{\"icon\":\"04d\"}],\"wind\":{\"speed\":3.1}}";

	private static CompetitionDTO competition(String id) {
		CompetitionDTO competition = new CompetitionDTO();
		competition.setIdCompetition(id);
		return competition;
	}

	@Test
	public void mainPageShowsActiveCompetitionsAndTheWeatherInLviv() throws Exception {
		List<CompetitionDTO> active = Collections.singletonList(competition("c1"));
		when(service.getAllActiveCompetitions(1, Integer.MAX_VALUE)).thenReturn(active);
		when(service.getAllActiveCompetitions(1, 10)).thenReturn(active);
		when(service.getAllActiveCompetitionsByUser(1, 10, LOGIN)).thenReturn(active);
		CurrentWeather weather = new OpenWeatherMap("test-key").currentWeatherFromRawResponse(LVIV_WEATHER);

		try (MockedConstruction<OpenWeatherMap> weatherServices = mockConstruction(OpenWeatherMap.class,
				(weatherService, context) -> when(weatherService.currentWeatherByCityName("Lviv")).thenReturn(weather))) {
			mockMvc(new MainPageController())
					.perform(get("/main.html").principal(SecurityContextHolder.getContext().getAuthentication()))
					.andExpect(view().name("main")).andExpect(model().attribute("login", LOGIN))
					.andExpect(model().attribute("getAllComp", active))
					.andExpect(model().attribute("getAllCompTakePart", active))
					.andExpect(model().attribute("city_name", "Lviv")).andExpect(model().attribute("temp", "12.5"))
					.andExpect(model().attribute("humidity", "81")).andExpect(model().attribute("weather_icon", "04d"))
					.andExpect(model().attribute("wind", "3.1"));
		}
	}

	@Test
	public void participantsAreOfferedToLeaveACompetition() throws Exception {
		when(service.getAllActiveCompetitionsByUser(1, Integer.MAX_VALUE, LOGIN))
				.thenReturn(Collections.singletonList(competition("c1")));

		mockMvc(new MainPageController()).perform(get("/check_take_part.html").param("idCompetition", "c1"))
				.andExpect(view().name("getOutOfCompetition"));
	}

	@Test
	public void otherUsersAreOfferedToJoinACompetition() throws Exception {
		when(service.getAllActiveCompetitionsByUser(1, Integer.MAX_VALUE, LOGIN))
				.thenReturn(Collections.singletonList(competition("c2")));

		mockMvc(new MainPageController()).perform(get("/check_take_part.html").param("idCompetition", "c1"))
				.andExpect(view().name("joinCompetition"));
	}

	@Test
	public void leavingACompetitionReturnsToTheMainPage() throws Exception {
		mockMvc(new MainPageController()).perform(get("/getOutOfCompetition.html").param("idCompetition", "c1"))
				.andExpect(redirectedUrl("main.html"));

		verify(service).removeUserFromCompetition("c1", LOGIN);
	}
}
