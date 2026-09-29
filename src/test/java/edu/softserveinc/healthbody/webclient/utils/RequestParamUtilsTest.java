package edu.softserveinc.healthbody.webclient.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class RequestParamUtilsTest {

	@Test
	public void missingAndEmptyParametersAreEmpty() {
		assertTrue(RequestParamUtils.isEmptyParam(null));
		assertTrue(RequestParamUtils.isEmptyParam(""));
		assertFalse(RequestParamUtils.isEmptyParam("1"));
	}

	@Test
	public void parsesWholeNumbers() {
		assertEquals(RequestParamUtils.getIntegerParam("12"), 12);
		assertEquals(RequestParamUtils.getIntegerParam("-3"), -3);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void rejectsParametersThatAreNotWholeNumbers() {
		RequestParamUtils.getIntegerParam("1.5");
	}
}
