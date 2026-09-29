package edu.softserveinc.healthbody.webclient.soap;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SOAPHandler {

	private final ClassLoader LOADER = getClass().getClassLoader();
	private static final String DEFAULT_URL = "http://localhost:8080/lv185/HealthBodyService";
	private static final String NAME_PACKAGE = "http://webservice.healthbody.softserveinc.edu/";

	private String partNumber;
	private String partSize;
	private String wsURL;

	public SOAPHandler() {
	}

	public SOAPHandler(String wsURL) {
		this.wsURL = wsURL;
	}

	// Getters
	public String getPartNumber() {
		return partNumber;
	}

	public String getPartSize() {
		return partSize;
	}

	public String getAllRequestEntityFromWS(int partNumber, int partSize, String nameMethod, String nameXml)
			throws MalformedURLException, IOException, ParserConfigurationException, SAXException {

		/*
		 * Make a web service HTTP request
		 */
		String responseString = "";
		StringBuilder outputSB = new StringBuilder();
		String outputString;
		URL url;
		if (wsURL != null) {
			url = new URL(wsURL);
		} else {
			url = new URL(DEFAULT_URL);
		}
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput = " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
						+ " xmlns:web=\""+NAME_PACKAGE+"\">\n" 
						+ " <soapenv:Header/>\n" 
						+ " <soapenv:Body>\n"
						+ " <web:" + nameMethod + ">\n" 
						+ " <partNumber>" + partNumber + "</partNumber>\n" 
						+ " <partSize>" + partSize + "</partSize>\n" 
						+ " </web:" + nameMethod + ">\n" 
						+ " </soapenv:Body>\n"
						+ " </soapenv:Envelope>";

		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction = DEFAULT_URL + "/" + nameMethod;

		/*
		 * Set the appropriate HTTP parameters.
		 */
		httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		out.write(b);

		/*
		 * Read the response.
		 */
		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);

		/*
		 * Write the SOAP message response to a String.
		 */
		while ((responseString = in.readLine()) != null) {
			outputSB.append(responseString);
		}
		outputString = outputSB.toString();

		/*
		 * Write response into file xml
		 */
		File file = new File(nameXml);
		if (!file.exists()){
			file.createNewFile();			
		}
	
		FileOutputStream fop = new FileOutputStream(file);			
		byte[] contentInBytes = outputString.getBytes();
		fop.write(contentInBytes);
		fop.flush();
		fop.close();

		/*
		 * Parse the String output to a org.w3c.dom.Document and be able to
		 * reach every node with the org.w3c.dom API.
		 */
		Document document = parseXmlFile(outputString);
		NodeList nodeLst = document.getElementsByTagName("ns2:" + nameMethod + "Response");
		String entityInXml = nodeLst.item(0).getTextContent();
		out.close();
		return entityInXml;
	}

	private Document parseXmlFile(String in) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(in));
		return db.parse(is);
	}

	public boolean convertXmlToHtml(String xml, String xsl, String htmlReport) throws SOAPCustomException {
		boolean result = false;
		try {
			StreamSource streamSource = new StreamSource(LOADER.getResourceAsStream(xsl));

			SAXReader saxReader = new SAXReader();
			org.dom4j.Document document = saxReader.read(xml);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer(streamSource);

			DocumentSource xmlSource = new DocumentSource(document);
			DocumentResult outputTarget = new DocumentResult();

			transformer.transform(xmlSource, outputTarget);

			org.dom4j.Document htmlDoc = outputTarget.getDocument();

			XMLWriter writer;			
			OutputFormat format = OutputFormat.createPrettyPrint();
			format = OutputFormat.createCompactFormat();
	        writer = new XMLWriter( new FileWriter(htmlReport), format );
	        writer.write(htmlDoc);
	        writer.close();

			Desktop.getDesktop().browse(new File(htmlReport).toURI());

			result = true;

		} catch (Exception e) {
			throw new SOAPCustomException("Problem with convertation xml "+xml+" to html "+htmlReport, e);
		}
		return result;
	}
}