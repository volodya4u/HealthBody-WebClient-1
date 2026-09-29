package edu.softserveinc.healthbody.webclient.servlets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

public class XslTransformerServletTest {

	@Test
	public void rendersTheSampleCompetitionsResponseAsAnHtmlTable() throws Exception {
		ByteArrayOutputStream html = new ByteArrayOutputStream();

		new XslTransformerServlet().transform(html);

		String page = new String(html.toByteArray(), StandardCharsets.UTF_8);
		assertTrue(page.contains("<h2>List of competitions</h2>"), page);
		assertTrue(page.contains("<td>Name competition 2</td><td>Description of competition 2</td>"
				+ "<td>2016-07-12</td><td>2016-07-26</td><td>2</td>"), page);
		Matcher rows = Pattern.compile("<tr>").matcher(page);
		int count = 0;
		while (rows.find()) {
			count++;
		}
		// A header row plus one row for each of the 20 competitions in CompetitionsTest.xml.
		assertEquals(count, 21);
	}
}
