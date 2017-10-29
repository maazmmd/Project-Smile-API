package com.utils.projectSmile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author mohammmedmaaz
 *
 */
public class projectSmileEmailSMSClient {

	private static HashMap emailDetailMap = new HashMap();
	static EmailHelper emailHelper = new EmailHelper();

	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {

		try {
			String smtpServer = null, port = null, auth = null, userName = null, password = null,
					imageProcessingPath = null, attachmentPath = null, htmlBodyTemplateName = null, from = null,
					emailSubject = null;
			// Initially wait for 10 Seconds if not waiting mentioned in
			// MailConfig.xml
			long waitInSec = 3000;
			// Mac File Path -
			// /Users/mohammmedmaaz/Documents/projectSmileAPI/ProjectSmile_MailConfig.xml
			File psConfig = new File("D:/projectSmileAPI/ProjectSmile_MailSMSConfig.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(psConfig);
			NodeList mailConfigurationList = doc.getElementsByTagName("mailConfiguration");
			for (int i = 0; i < mailConfigurationList.getLength(); i++) {
				Node configNode = mailConfigurationList.item(i);
				Element eElement = (Element) configNode;
				smtpServer = eElement.getElementsByTagName("smtpServer").item(0).getTextContent().trim();
				port = eElement.getElementsByTagName("port").item(0).getTextContent().trim();
				auth = eElement.getElementsByTagName("authentication").item(0).getTextContent().trim();
				userName = eElement.getElementsByTagName("userName").item(0).getTextContent().trim();
				password = eElement.getElementsByTagName("password").item(0).getTextContent().trim();
				imageProcessingPath = eElement.getElementsByTagName("emailBodyImagesPath").item(0).getTextContent()
						.trim();
				attachmentPath = eElement.getElementsByTagName("attachmentPath").item(0).getTextContent().trim();
				htmlBodyTemplateName = eElement.getElementsByTagName("emailBodyTemplateFileName").item(0)
						.getTextContent().trim();
				from = eElement.getElementsByTagName("fromEmailAddress").item(0).getTextContent().trim();
				emailSubject = eElement.getElementsByTagName("emailSubject").item(0).getTextContent();
				waitInSec = Long
						.parseLong(eElement.getElementsByTagName("waitingPeriod").item(0).getTextContent().trim());
			}

			// Set SMTP Server Details and Attachment Details
			emailDetailMap.put("Server", smtpServer);
			emailDetailMap.put("port", port);
			emailDetailMap.put("auth", auth);
			emailDetailMap.put("userName", userName);
			emailDetailMap.put("password", password);
			emailDetailMap.put("From", from);
			emailDetailMap.put("imageProcessingPath", imageProcessingPath);
			emailDetailMap.put("AttachmentPath", attachmentPath);

			File folder = new File(attachmentPath);
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					String fileNameWithEmailID = file.getName();
					if (fileNameWithEmailID.contains(".pdf")) {
						if (fileNameWithEmailID.split(";").length > 0) {
							if (fileNameWithEmailID.split(";").length > 1) {
								SMSHandler smsHandler = new SMSHandler();
								String mobileNumber = fileNameWithEmailID.split(";")[2];
								smsHandler.sendMessage(mobileNumber, null);
								System.out.println("SMS Sent sucessfully to Mobile Number : "+ mobileNumber);
							}
							String toEmailID = fileNameWithEmailID.split(";")[1];
							toEmailID = toEmailID.substring(0, toEmailID.length() - 4);
							emailDetailMap.put("To", toEmailID);

							// Renaming File Name
							String rename = fileNameWithEmailID.split(";")[0] + ".pdf";
							File renamedFile = new File(attachmentPath + rename);
							if (renamedFile.exists()) {
								System.out.println("Falied to send Email to : " + toEmailID
										+ " as Recepiant email dispatched with reference (fileName) : " + rename);
								break;
								// throw new java.io.IOException("file exists");
							}
							file.renameTo(renamedFile);
							// Setting Email Subject and appending with Receipt
							// Number
							emailDetailMap.put("Subject", emailSubject + fileNameWithEmailID.split(";")[0]);

							// Fetch e-Mail Body from Html BNody Template
							File bodyTemplate = new File(imageProcessingPath + htmlBodyTemplateName);

							String HTMLStr = FileUtils.readFileToString(bodyTemplate, "UTF-8");
							emailDetailMap.put("Body", HTMLStr);
							List attachmentList = new ArrayList();
							attachmentList.add(rename);
							emailDetailMap.put("AttachmentList", attachmentList);
							System.out.println("Sending Email To : " + toEmailID + " Attached File : " + rename);
							emailHelper.sendEmail(emailDetailMap);
							// Pause for seconds mentioned in Config.xml
							Thread.sleep(waitInSec); // Resume after waiting Period
						} else {
							System.out.println("Falied to send Email as Recepiant email dispatched with reference (fileName) : " + fileNameWithEmailID.split(";")[0]);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
