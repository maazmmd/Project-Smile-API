package com.utils.projectSmile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SMSHandler {
	String url = "";
	String username_label = "";
	String username = "";
	String pwd_label = "";
	String pwd = "";
	String ctry_label = "";
	String pNo_label = "";
	String mess_label = "";
	String sender_label = "";
	String sender = "";
	String flashMessage_label = "";
	String flashMessage = "";
	String param1 = "", param2 = "", param3 = "", param4 = "", param5 = "";
	String param1_value = "", param2_value = "", param3_value = "", param4_value = "", param5_value = "";
	String messageToSent = "";

	public SMSHandler() {
		try {
			File psConfig = new File("/Users/mohammmedmaaz/Documents/projectSmileAPI/ProjectSmile_MailSMSConfig.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(psConfig);
			NodeList mailConfigurationList = doc.getElementsByTagName("sms");
			for (int i = 0; i < mailConfigurationList.getLength(); i++) {
				Node configNode = mailConfigurationList.item(i);
				Element eElement = (Element) configNode;
				url = eElement.getElementsByTagName("server_url").item(0).getTextContent().trim();
				username_label = eElement.getElementsByTagName("username_label").item(0).getTextContent().trim();
				username = eElement.getElementsByTagName("username").item(0).getTextContent().trim();
				pwd_label = eElement.getElementsByTagName("password_label").item(0).getTextContent().trim();
				pwd = eElement.getElementsByTagName("password").item(0).getTextContent().trim();
				ctry_label = eElement.getElementsByTagName("countryCode_label").item(0).getTextContent().trim();
				pNo_label = eElement.getElementsByTagName("phonenumber_label").item(0).getTextContent().trim();
				mess_label = eElement.getElementsByTagName("message_label").item(0).getTextContent().trim();
				sender_label = eElement.getElementsByTagName("sender_label").item(0).getTextContent().trim();
				sender = eElement.getElementsByTagName("sender").item(0).getTextContent().trim();
				flashMessage_label = eElement.getElementsByTagName("flashMessage_label").item(0).getTextContent()
						.trim();
				flashMessage = eElement.getElementsByTagName("flashMessage").item(0).getTextContent().trim();
				param1 = eElement.getElementsByTagName("param1").item(0).getTextContent().trim();
				param1_value = eElement.getElementsByTagName("param1_value").item(0).getTextContent().trim();
				param2 = eElement.getElementsByTagName("param2").item(0).getTextContent().trim();
				param2_value = eElement.getElementsByTagName("param2_value").item(0).getTextContent().trim();
				param3 = eElement.getElementsByTagName("param3").item(0).getTextContent().trim();
				param3_value = eElement.getElementsByTagName("param3_value").item(0).getTextContent().trim();
				param4 = eElement.getElementsByTagName("param4").item(0).getTextContent().trim();
				param4_value = eElement.getElementsByTagName("param4_value").item(0).getTextContent().trim();
				param5 = eElement.getElementsByTagName("param5").item(0).getTextContent().trim();
				param5_value = eElement.getElementsByTagName("param5_value").item(0).getTextContent().trim();
				messageToSent = eElement.getElementsByTagName("messageToSent").item(0).getTextContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String sendMessage(String mobileNumber, String countryCode) throws IOException {
		String result = "";

		String message = messageToSent;

		String phoneNo = mobileNumber;
		String contryCodeNumber = countryCode;
		String encodedMess = URLEncoder.encode(message, "UTF-8");

		encodedMess = encodedMess.replace("%C2%A0", "+");
		encodedMess = encodedMess.replace("%A0", "+");
		StringBuffer finalURL = new StringBuffer();
		finalURL.append(url).append("?");
		finalURL.append(username_label).append("=").append(this.username).append("&");
		finalURL.append(pwd_label).append("=").append(this.pwd).append("&");
		if (ctry_label != null && ctry_label != "") {
			finalURL.append(this.ctry_label).append("=").append(contryCodeNumber).append("&");
		}
		if (pNo_label != null && pNo_label != "") {
			finalURL.append(this.pNo_label).append("=").append(phoneNo).append("&");
		}
		if (mess_label != null && mess_label != "") {
			finalURL.append(this.mess_label).append("=").append(encodedMess).append("&");
		}
		if (sender_label != null && sender_label != "") {
			finalURL.append(this.sender_label).append("=").append(this.sender).append("&");
		}
		if (flashMessage_label != null && flashMessage_label != "") {
			finalURL.append(this.flashMessage_label).append("=").append(this.flashMessage);
		}
		if (param1 != null && param1 != "") {
			finalURL.append("&").append(this.param1).append("=").append(this.param1_value);
		}
		if (param2 != null && param2 != "") {
			finalURL.append("&").append(this.param2).append("=").append(this.param2_value);
		}
		if (param3 != null && param3 != "") {
			finalURL.append("&").append(this.param3).append("=").append(this.param3_value);
		}
		if (param4 != null && param4 != "") {
			finalURL.append("&").append(this.param4).append("=").append(this.param4_value);
		}
		if (param5 != null && param5 != "") {
			finalURL.append("&").append(this.param5).append("=").append(this.param5_value);
		}
		//System.out.println("Final URL firing for sending the SMS : " + finalURL);
		URL conn = new URL(finalURL.toString());

		URLConnection yc = conn.openConnection();
		yc.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		while ((result = in.readLine()) != null) {
			// Print XML- UnComment below System.out Line or Do Nothing
			// System.out.println(result);
		}
		in.close();
		return result;
	}
}
