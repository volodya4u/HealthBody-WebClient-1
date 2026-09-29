package edu.softserveinc.healthbody.webclient.servlets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.mockito.MockedConstruction;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.softserveinc.healthbody.webclient.healthbody.webservice.CompetitionDTO;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.HealthBodyService;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.HealthBodyServiceImplService;

public class CompetitionsViewServletTest {

	private static CompetitionDTO competition(String name) {
		CompetitionDTO competition = new CompetitionDTO();
		competition.setName(name);
		return competition;
	}

	private MockHttpServletResponse get(HealthBodyService service, MockHttpServletRequest request) throws Exception {
		try (MockedConstruction<HealthBodyServiceImplService> clients = mockConstruction(
				HealthBodyServiceImplService.class,
				(client, context) -> when(client.getHealthBodyServiceImplPort()).thenReturn(service))) {
			MockHttpServletResponse response = new MockHttpServletResponse();
			new CompetitionsViewServlet().service(request, response);
			return response;
		}
	}

	@Test
	public void returnsTheRequestedPageOfCompetitionsAsJson() throws Exception {
		HealthBodyService service = mock(HealthBodyService.class);
		when(service.getAllCompetitions(2, 3)).thenReturn(Arrays.asList(competition("Run"), competition("Swim")));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/competitions");
		request.setParameter("partNumber", "2");
		request.setParameter("partSize", "3");

		MockHttpServletResponse response = get(service, request);

		assertEquals(response.getContentType(), "application/json");
		JsonArray competitions = new JsonParser().parse(response.getContentAsString()).getAsJsonArray();
		assertEquals(competitions.size(), 2);
		assertEquals(competitions.get(0).getAsJsonObject().get("name").getAsString(), "Run");
	}

	@Test
	public void defaultsToTheFirstPageOfFive() throws Exception {
		HealthBodyService service = mock(HealthBodyService.class);
		when(service.getAllCompetitions(1, 5)).thenReturn(Arrays.asList(competition("Run")));

		MockHttpServletResponse response = get(service, new MockHttpServletRequest("GET", "/competitions"));

		assertEquals(new JsonParser().parse(response.getContentAsString()).getAsJsonArray().size(), 1);
	}
}
