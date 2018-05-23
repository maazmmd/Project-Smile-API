package com.utils.projectSmile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
					emailSubject = null, cc = null, sendSMS4Excel = null, excelFilePath = null, logString = null;
			// Initially wait for 3 Seconds if not waiting mentioned in Config.xml
			long waitInSec = 3000;
			int columnIndex = 2;
			File psConfig = new File("/Users/mohammmedmaaz/Documents/Projects-Workspace/myGitRepo/Project-Smile-API/lib/ProjectSmile_MailSMSConfig.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(psConfig);
			NodeList smsConfigurationList = doc.getElementsByTagName("sms");
			for (int i = 0; i < smsConfigurationList.getLength(); i++) {
				Node configNode = smsConfigurationList.item(i);
				Element eElement = (Element) configNode;
				sendSMS4Excel = eElement.getElementsByTagName("sendSMSOnlyforExcelNumbers").item(0).getTextContent().trim();
				excelFilePath = eElement.getElementsByTagName("excelFilePath").item(0).getTextContent().trim();
				columnIndex = Integer.parseInt(eElement.getElementsByTagName("excelColumnIndexOfMobileNumbers").item(0).getTextContent().trim());
			}
			if (sendSMS4Excel.equals("Yes")) {
				List<String> mobileNumberList = extractExcelContentByColumnIndex(columnIndex, excelFilePath);
				for (String mobileNumber : mobileNumberList) {
					SMSHandler smsHandler = new SMSHandler();
					smsHandler.sendMessage(mobileNumber, null);
					logString =  "SMS Sent sucessfully to Mobile Number (Using Excel): " + mobileNumber;
					System.out.println("SMS Sent sucessfully to Mobile Number (Using Excel): " + mobileNumber);
				}
			} else {
				NodeList mailConfigurationList = doc.getElementsByTagName("mailConfiguration");
				for (int i = 0; i < mailConfigurationList.getLength(); i++) {
					Node configNode = mailConfigurationList.item(i);
					Element eElement = (Element) configNode;
					smtpServer = eElement.getElementsByTagName("smtpServer").item(0).getTextContent().trim();
					sendSMS4Excel = eElement.getElementsByTagName("sendSMSOnlyforExcelNumbers").item(0).getTextContent()
							.trim();
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
					cc = eElement.getElementsByTagName("CC").item(0).getTextContent().trim();
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
				emailDetailMap.put("Cc", cc);

				File folder = new File(attachmentPath);
				File[] listOfFiles = folder.listFiles();
				for (File file : listOfFiles) {
					if (file.isFile()) {
						String fileNameWithEmailSMS = file.getName();
						if (fileNameWithEmailSMS.contains(".pdf")) {
							if (fileNameWithEmailSMS.split(";").length > 1) {
								String mobileNumber = "";
								if (fileNameWithEmailSMS.split(";").length == 3) {
									SMSHandler smsHandler = new SMSHandler();
									mobileNumber = fileNameWithEmailSMS.split(";")[2];
									mobileNumber = mobileNumber.substring(0, mobileNumber.length() - 4);
									smsHandler.sendMessage(mobileNumber, null);
									System.out.println("SMS Sent sucessfully to Mobile Number : " + mobileNumber);
								}
								String toEmailID = fileNameWithEmailSMS.split(";")[1];
								if (fileNameWithEmailSMS.split(";").length == 2) {
									toEmailID = toEmailID.substring(0, toEmailID.length() - 4);
								} else {
									toEmailID = fileNameWithEmailSMS.split(";")[1];
								}
								emailDetailMap.put("To", toEmailID);

								// Renaming File Name
								String rename = fileNameWithEmailSMS.split(";")[0] + ".pdf";
								File renamedFile = new File(attachmentPath + rename);
								if (renamedFile.exists()) {
									//System.out.println("Falied to send Email to : " + toEmailID + " as Recepiant email dispatched with reference (fileName) : " + rename);
									logString = "Falied to send Email to : " + toEmailID + " as Recepiant email dispatched with reference (fileName / renamed after sending) : " + rename; 
									break;
									// throw new java.io.IOException("file exists");
								}
								file.renameTo(renamedFile);
								// Setting Email Subject and appending with Receipt Number
								emailDetailMap.put("Subject", emailSubject + fileNameWithEmailSMS.split(";")[0]);

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

								// Success String in Log File
								logString = "\n" + new java.util.Date().toString() + "      " + fileNameWithEmailSMS + "\nEmail Send Sucessfully to " + toEmailID;
								if (fileNameWithEmailSMS.split(";").length == 3) {
									logString = logString + "\nSMS Sent sucessfully to Mobile Number : " + mobileNumber;
								}

							} else {
								//System.out.println("Falied to send Email/SMS as Recepiant notification send already with reference (fileName) : " + fileNameWithEmailSMS.split(";")[0]);
								// Error String in Log File
								logString = "\n" + new java.util.Date().toString() + "\nFailed to Send Email/SMS Reference : " + fileNameWithEmailSMS;
							}
						}
					}
				}
			}
			// Enter in Status File (log) Email/SMS successfully Send
			File statusFile = new File(attachmentPath + "Status.txt");
			if (!statusFile.exists()) {
				statusFile.createNewFile();
			}
			FileUtils.writeStringToFile(statusFile, "\n" + logString, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> extractExcelContentByColumnIndex(int columnIndex, String excelFilePath) {
		List<String> columndata = new ArrayList<String>();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(excelFilePath);

			// Using XSSF for xlsx format, for xls use HSSF
			Workbook workbook = new XSSFWorkbook(fis);

			int numberOfSheets = workbook.getNumberOfSheets();

			// looping over each workbook sheet
			for (int i = 0; i < numberOfSheets; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				Iterator rowIterator = sheet.iterator();
				// iterating over each row
				while (rowIterator.hasNext()) {
					Row row = (Row) rowIterator.next();
					Iterator cellIterator = row.cellIterator();
					// Iterating over each cell (column wise) in a particular
					// row.
					while (cellIterator.hasNext()) {
						Cell cell = (Cell) cellIterator.next();
						if (row.getRowNum() > 0) { // To filter column headings
							if (cell.getColumnIndex() == columnIndex) {// To match column index
								switch (cell.getCellType()) {
								case Cell.CELL_TYPE_NUMERIC:
									columndata.add(String.valueOf(Double.valueOf(cell.getNumericCellValue()).longValue()));
									break;
								case Cell.CELL_TYPE_STRING:
									columndata.add(cell.getStringCellValue());
									break;
								}
							}
						}
					}
				}
				fis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columndata;
	}
}
