package edu.softserveinc.healthbody.webclient.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.softserveinc.healthbody.webclient.dto.StatisticsDTO;
import edu.softserveinc.healthbody.webclient.service.StatisticsService;

public class LoginStatisticsControllerTest extends ControllerTestSupport {

	private static final long MINUTE = 60 * 1000;

	private StatisticsService statisticsService;

	@BeforeMethod
	public void mockStatisticsService() {
		statisticsService = mock(StatisticsService.class);
	}

	private <T> T withStatistics(T controller) {
		ReflectionTestUtils.setField(controller, "statisticsService", statisticsService);
		return controller;
	}

	@Test
	public void loginRecordsTheLoginTimeAndOpensTheMainPage() throws Exception {
		mockMvc(withStatistics(new AddLoginStatisticsController())).perform(get("/addLoginStatistics.html"))
				.andExpect(redirectedUrl("/main.html"));

		ArgumentCaptor<StatisticsDTO> statistics = ArgumentCaptor.forClass(StatisticsDTO.class);
		verify(statisticsService).addStatistics(statistics.capture());
		assertEquals(statistics.getValue().getUserLogin(), LOGIN);
		assertNotNull(statistics.getValue().getLoginDate());
		assertNull(statistics.getValue().getLogoutDate());
	}

	@Test
	public void logoutRecordsTheLogoutTimeAndClearsTheSession() throws Exception {
		mockMvc(withStatistics(new LogoutController())).perform(get("/logout")).andExpect(view().name("login"));

		verify(statisticsService).updateStatistics(any(Date.class), eq(LOGIN));
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	public void pieChartSumsTheMinutesOfFinishedSessionsPerUser() throws Exception {
		Date login = new Date();
		when(statisticsService.getAllUsersLogin()).thenReturn(Arrays.asList("anna", "bob"));
		when(statisticsService.getStatisticsByUserLogin("anna")).thenReturn(Arrays.asList(
				new StatisticsDTO("1", "anna", login, new Date(login.getTime() + 30 * MINUTE)),
				new StatisticsDTO("2", "anna", login, new Date(login.getTime() + 15 * MINUTE)),
				new StatisticsDTO("3", "anna", login, null)));
		when(statisticsService.getStatisticsByUserLogin("bob")).thenReturn(Collections.<StatisticsDTO>emptyList());

		Map<String, Long> expected = new HashMap<>();
		expected.put("anna", 45L);
		expected.put("bob", 0L);
		mockMvc(withStatistics(new UserDurationOnAppController())).perform(get("/pieChart.html"))
				.andExpect(view().name("pieChart")).andExpect(model().attribute("login", LOGIN))
				.andExpect(model().attribute("userDurationOnApp", expected));
	}
}
