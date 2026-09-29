package edu.softserveinc.healthbody.webclient.validator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.mockito.MockedConstruction;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.softserveinc.healthbody.webclient.healthbody.webservice.CompetitionDTO;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.HealthBodyService;
import edu.softserveinc.healthbody.webclient.healthbody.webservice.HealthBodyServiceImplService;

public class CompetitionValidatorTest {

	private HealthBodyService service;
	private MockedConstruction<HealthBodyServiceImplService> soapClients;

	@BeforeMethod
	public void mockSoapClient() {
		service = mock(HealthBodyService.class);
		soapClients = mockConstruction(HealthBodyServiceImplService.class,
				(client, context) -> when(client.getHealthBodyServiceImplPort()).thenReturn(service));
	}

	@AfterMethod(alwaysRun = true)
	public void closeSoapClientMock() {
		soapClients.close();
	}

	private static CompetitionDTO competition(String name, String startDate, String finishDate) {
		CompetitionDTO competition = new CompetitionDTO();
		competition.setName(name);
		competition.setDescription("Walk every day");
		competition.setStartDate(startDate);
		competition.setFinishDate(finishDate);
		return competition;
	}

	private static Errors validate(CompetitionDTO competition) {
		Errors errors = new BeanPropertyBindingResult(competition, "competition");
		new CompetitionValidator().validate(competition, errors);
		return errors;
	}

	@Test
	public void acceptsANewCompetition() {
		assertFalse(validate(competition("Autumn walk", "2016-09-01", "2016-09-30")).hasErrors());
	}

	@Test
	public void rejectsNamesShorterThanTwoOrLongerThanTwentyCharacters() {
		assertEquals(validate(competition("A", "2016-09-01", "2016-09-30")).getFieldError("name").getCode(),
				"name.tooLong");
		assertEquals(validate(competition("A walk that is far too long", "2016-09-01", "2016-09-30"))
				.getFieldError("name").getCode(), "name.tooLong");
	}

	@Test
	public void rejectsNamesThatAlreadyExist() {
		when(service.getCompetitionViewByName("Autumn walk")).thenReturn(new CompetitionDTO());

		assertEquals(validate(competition("Autumn walk", "2016-09-01", "2016-09-30")).getFieldError("name").getCode(),
				"name.incorrect");
	}

	@Test
	public void rejectsAStartDateAfterTheFinishDate() {
		Errors errors = validate(competition("Autumn walk", "2016-09-30", "2016-09-01"));

		assertTrue(errors.hasFieldErrors("startDate"));
		assertEquals(errors.getFieldError("startDate").getCode(), "startDate.incorrect");
	}

	@Test
	public void supportsOnlyCompetitions() {
		assertTrue(new CompetitionValidator().supports(CompetitionDTO.class));
		assertFalse(new CompetitionValidator().supports(String.class));
	}
}
