package edu.softserveinc.healthbody.webclient.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.softserveinc.healthbody.webclient.healthbody.webservice.CompetitionDTO;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.HealthBodyServiceImplService;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.UserCompetitionsDTO;
import edu.softserveinc.healthbody.webclient.utils.GoogleFitUtils;
import edu.softserveinc.healthbody.webclient.validator.CompetitionValidator;

public class CompetitionControllerTest extends ControllerTestSupport {

	private MockMvc mvc;
	private MockedConstruction<HealthBodyServiceImplService> newSoapClients;

	@BeforeMethod
	public void setUp() {
		CompetitionController controller = new CompetitionController();
		ReflectionTestUtils.setField(controller, "competitionValidator", new CompetitionValidator());
		mvc = mockMvc(controller);
		// The POST handlers and the validator create their own SOAP client.
		newSoapClients = mockConstruction(HealthBodyServiceImplService.class,
				(client, context) -> when(client.getHealthBodyServiceImplPort()).thenReturn(service));
	}

	@AfterMethod(alwaysRun = true)
	public void closeConstructionMock() {
		newSoapClients.close();
	}

	private static CompetitionDTO competition(String id) {
		CompetitionDTO competition = new CompetitionDTO();
		competition.setIdCompetition(id);
		competition.setName("Competition " + id);
		competition.setStartDate("2016-08-01");
		return competition;
	}

	private static List<CompetitionDTO> competitions(int count) {
		List<CompetitionDTO> competitions = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			competitions.add(competition("c" + i));
		}
		return competitions;
	}

	@Test
	public void adminsSeeAPageOfAllCompetitions() throws Exception {
		List<CompetitionDTO> secondPage = competitions(2);
		when(service.getUserByLogin(LOGIN)).thenReturn(user(LOGIN, "admin"));
		when(service.getAllCompetitions(1, Integer.MAX_VALUE)).thenReturn(competitions(7));
		when(service.getAllCompetitions(2, 5)).thenReturn(secondPage);

		mvc.perform(get("/listCompetitions.html").param("partNumber", "2"))
				.andExpect(view().name("listCompetitionsAdmin")).andExpect(model().attribute("currentPage", 2))
				.andExpect(model().attribute("lastPartNumber", 2))
				.andExpect(model().attribute("getCompetitions", secondPage));
		verify(service, never()).getAllActiveCompetitions(1, Integer.MAX_VALUE);
	}

	@Test
	public void usersSeeAPageOfActiveCompetitions() throws Exception {
		List<CompetitionDTO> firstPage = competitions(5);
		when(service.getUserByLogin(LOGIN)).thenReturn(user(LOGIN, "user"));
		when(service.getAllActiveCompetitions(1, Integer.MAX_VALUE)).thenReturn(competitions(11));
		when(service.getAllActiveCompetitions(1, 5)).thenReturn(firstPage);

		mvc.perform(get("/listCompetitions.html")).andExpect(view().name("listCompetitions"))
				.andExpect(model().attribute("currentPage", 1)).andExpect(model().attribute("lastPartNumber", 3))
				.andExpect(model().attribute("getCompetitions", firstPage));
		verify(service, never()).getAllCompetitions(1, Integer.MAX_VALUE);
	}

	@Test
	public void participantsCanLeaveACompetition() throws Exception {
		when(service.getAllCompetitionsByUser(1, Integer.MAX_VALUE, LOGIN))
				.thenReturn(Arrays.asList(competition("c1"), competition("c2")));

		mvc.perform(get("/competition.html").param("idCompetition", "c2")).andExpect(view().name("leaveCompetition"));
	}

	@Test
	public void otherUsersCanJoinACompetition() throws Exception {
		when(service.getAllCompetitionsByUser(1, Integer.MAX_VALUE, LOGIN))
				.thenReturn(Collections.singletonList(competition("c1")));

		mvc.perform(get("/competition.html").param("idCompetition", "c2")).andExpect(view().name("joinCompetition"));
	}

	@Test
	public void joiningStoresTheStepsCountedByGoogleFitSinceTheStart() throws Exception {
		UserCompetitionsDTO participation = new UserCompetitionsDTO();
		when(service.getUserByLogin(LOGIN)).thenReturn(user(LOGIN, "user"));
		when(service.getCompetitionViewById("c1")).thenReturn(competition("c1"));
		when(service.getUserCompetition("c1", LOGIN)).thenReturn(participation);

		try (MockedStatic<GoogleFitUtils> googleFit = mockStatic(GoogleFitUtils.class)) {
			googleFit.when(() -> GoogleFitUtils.postForAccessToken(any())).thenReturn("access-token");
			googleFit.when(() -> GoogleFitUtils.post(anyString(), anyLong(), anyLong())).thenReturn("fit-data");
			googleFit.when(() -> GoogleFitUtils.getStepCount("fit-data")).thenReturn("4321");

			mvc.perform(get("/joinCompetition.html").param("idCompetition", "c1"))
					.andExpect(view().name("userCabinet"));
		}

		verify(service).addUserInCompetitionView("c1", LOGIN);
		verify(service).updateUserCompetition(participation);
		assertEquals(participation.getUserScore(), "4321");
	}

	@Test
	public void leavingRemovesTheParticipation() throws Exception {
		mvc.perform(get("/leaveCompetition.html").param("idCompetition", "c1")).andExpect(view().name("userCabinet"));

		verify(service).deleteUserCompetition("c1", LOGIN);
	}

	@Test
	public void validCompetitionsAreCreatedWithANewId() throws Exception {
		mvc.perform(post("/createCompetition.html").param("name", "Spring run").param("description", "5 km a day")
				.param("startDate", "2016-09-01").param("finishDate", "2016-09-30"))
				.andExpect(redirectedUrl("/listCompetitions.html"));

		ArgumentCaptor<CompetitionDTO> created = ArgumentCaptor.forClass(CompetitionDTO.class);
		verify(service).createCompetition(created.capture());
		assertEquals(created.getValue().getName(), "Spring run");
		assertEquals(created.getValue().getStartDate(), "2016-09-01");
		assertNotNull(created.getValue().getIdCompetition());
	}

	@Test
	public void invalidCompetitionsAreShownAgainWithErrors() throws Exception {
		mvc.perform(post("/createCompetition.html").param("name", "Spring run").param("description", "5 km a day")
				.param("startDate", "2016-09-30").param("finishDate", "2016-09-01"))
				.andExpect(view().name("createCompetition"))
				.andExpect(model().attributeHasFieldErrorCode("competitionToCreate", "startDate", "startDate.incorrect"));

		verify(service, never()).createCompetition(any());
	}

	@Test
	public void editingUpdatesTheStoredCompetition() throws Exception {
		CompetitionDTO stored = competition("c1");
		when(service.getCompetitionViewById("c1")).thenReturn(stored);

		mvc.perform(post("/editCompetition.html").param("idCompetition", "c1").param("name", "Autumn run")
				.param("description", "10 km a day").param("startDate", "2016-10-01")
				.param("finishDate", "2016-10-31")).andExpect(redirectedUrl("/listCompetitions.html"));

		verify(service).updateCompetition(stored);
		assertEquals(stored.getName(), "Autumn run");
		assertEquals(stored.getFinishDate(), "2016-10-31");
	}
}
