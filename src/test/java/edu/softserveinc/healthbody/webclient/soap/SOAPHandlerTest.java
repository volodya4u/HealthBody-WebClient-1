package edu.softserveinc.healthbody.webclient.soap;

import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sun.net.httpserver.HttpServer;

public class SOAPHandlerTest {

	private HttpServer webService;
	private String requestBody;
	private String soapAction;
	private File savedResponse;

	private static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int length;
		while ((length = in.read(buffer)) != -1) {
			out.write(buffer, 0, length);
		}
		return out.toByteArray();
	}

	/** Answers every request with the sample getAllCompetitions response. */
	@BeforeMethod
	public void startStubWebService() throws Exception {
		final byte[] response = read(getClass().getClassLoader().getResourceAsStream("CompetitionsTest.xml"));
		webService = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		webService.createContext("/HealthBodyService", exchange -> {
			requestBody = new String(read(exchange.getRequestBody()), StandardCharsets.UTF_8);
			soapAction = exchange.getRequestHeaders().getFirst("SOAPAction");
			exchange.getResponseHeaders().set("Content-Type", "text/xml; charset=utf-8");
			exchange.sendResponseHeaders(200, response.length);
			try (OutputStream body = exchange.getResponseBody()) {
				body.write(response);
			}
		});
		webService.start();
		savedResponse = File.createTempFile("competitions", ".xml");
	}

	@AfterMethod(alwaysRun = true)
	public void stopStubWebService() {
		webService.stop(0);
		savedResponse.delete();
	}

	@Test
	public void requestsAPageOfEntitiesAndReadsTheResponse() throws Exception {
		SOAPHandler handler = new SOAPHandler(
				"http://127.0.0.1:" + webService.getAddress().getPort() + "/HealthBodyService");

		String entities = handler.getAllRequestEntityFromWS(2, 20, "getAllCompetitions", savedResponse.getPath());

		assertTrue(requestBody.contains("<web:getAllCompetitions>"), requestBody);
		assertTrue(requestBody.contains("<partNumber>2</partNumber>"), requestBody);
		assertTrue(requestBody.contains("<partSize>20</partSize>"), requestBody);
		assertTrue(soapAction.endsWith("/getAllCompetitions"), soapAction);
		assertTrue(entities.contains("Name competition 2"), entities);
		assertTrue(entities.contains("Description of competition 15"), entities);
		String saved = new String(Files.readAllBytes(savedResponse.toPath()), StandardCharsets.UTF_8);
		assertTrue(saved.contains("<ns2:getAllCompetitionsResponse"), saved);
	}
}
