package edu.softserveinc.healthbody.webclient.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Calendar;

import org.testng.annotations.Test;

public class CustomDateFormaterTest {

	@Test
	public void convertsADateToMidnightInMilliseconds() {
		Calendar expected = Calendar.getInstance();
		expected.clear();
		expected.set(2016, Calendar.AUGUST, 25);

		assertEquals(CustomDateFormater.getDateInMilliseconds("2016-08-25").longValue(), expected.getTimeInMillis());
	}

	@Test
	public void fallsBackToTheCurrentTimeForInvalidDates() {
		long before = System.currentTimeMillis();

		long result = CustomDateFormater.getDateInMilliseconds("not a date");

		assertTrue(result >= before && result <= System.currentTimeMillis());
	}
}
