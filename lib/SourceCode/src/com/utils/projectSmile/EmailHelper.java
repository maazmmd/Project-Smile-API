package com.utils.projectSmile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import sun.misc.BASE64Decoder;

/**
 * @author mohammmedmaaz
 *
 */

public class EmailHelper {

	private String smtpServer, port, auth, username, password;
	private static String imageProcessingPath;

	public void sendEmail(Map emailDetailsMap) throws IOException {
		try {
			smtpServer = (String) emailDetailsMap.get("Server");
			port = (String) emailDetailsMap.get("port");
			auth = (String) emailDetailsMap.get("auth");
			imageProcessingPath = (String) emailDetailsMap.get("imageProcessingPath");
			username = (String) emailDetailsMap.get("userName");
			password = (String) emailDetailsMap.get("password");
			String cc = (String) emailDetailsMap.get("Cc");
			String bcc = (String) emailDetailsMap.get("Bcc");
			String attachmentPath = (String) emailDetailsMap.get("AttachmentPath");
			List attachmentList = (ArrayList) emailDetailsMap.get("AttachmentList");

			Session session = getMailSession();
			MimeMessage email = new MimeMessage(session);

			addReceipient(email, Message.RecipientType.TO, (String) emailDetailsMap.get("To"));
			if (cc != null && (!cc.trim().equals(""))) {
				addReceipient(email, Message.RecipientType.CC, cc);
			}
			if (bcc != null && (!bcc.trim().equals(""))) {
				addReceipient(email, Message.RecipientType.BCC, bcc);
			}
			email.setSubject((String) emailDetailsMap.get("Subject"), "UTF-8");
			email.setFrom(new InternetAddress((String) emailDetailsMap.get("From")));

			Multipart emailMimeMultipart = new MimeMultipart("related");
			String htmlMessageBody = (String) emailDetailsMap.get("Body");
			processHTMLBody(htmlMessageBody, emailMimeMultipart);
			if (attachmentPath != null && (!attachmentPath.trim().equals(""))) {
				attachmentPath = (String) emailDetailsMap.get("AttachmentPath");
			}
			if ((attachmentList != null) && (attachmentList.size() > 0)) {
				MimeBodyPart emailAttachment = null;
				String attachmentName = null;
				String attachmentFile = null;
				DataSource emailAttachmentSource = null;
				for (Iterator it = attachmentList.iterator(); it.hasNext();) {
					attachmentName = (String) it.next();
					attachmentFile = attachmentPath + attachmentName;
					emailAttachmentSource = new FileDataSource(attachmentFile);
					emailAttachment = new MimeBodyPart();
					emailAttachment.setFileName(attachmentName);
					emailAttachment.setDataHandler(new DataHandler(emailAttachmentSource));
					emailMimeMultipart.addBodyPart(emailAttachment);
				}
			}
			//email.setHeader("Content-Type", "text/plain; charset=\"utf-8\"");
			email.setContent(emailMimeMultipart);
			Transport.send(email);
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}

	private void addReceipient(MimeMessage email, Message.RecipientType receipientType, String receipientList) {
		try {
			if (receipientList != null && (!receipientList.trim().equals(""))) {
				String[] receipientArray = receipientList.split(";");
				for (int i = 0; i < receipientArray.length; i++) {
					String receipientAddress = receipientArray[i];
					email.addRecipient(receipientType, new InternetAddress(receipientAddress));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Session getMailSession() throws MessagingException {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", smtpServer);
		properties.put("mail.smtp.auth", auth);
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.user", username);
	    properties.put("mail.smtp.starttls.enable","true");
	    properties.put("mail.smtp.debug", "true");
	    properties.put("mail.smtp.socketFactory.port", port);
	    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    properties.put("mail.smtp.socketFactory.fallback", "false");
		if (port != null && (!port.trim().equals(""))) {
			properties.put("mail.smtp.port", port);
		}
		properties.put("mail.mime.charset", "UTF-8");
		Authenticator authenticator = null;
		if (auth.equalsIgnoreCase("true")) {
			authenticator = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};
		}
		return Session.getInstance(properties, authenticator);
	}

	private static void processHTMLBody(String htmlMessageBody, Multipart emailMimeMultipart)
			throws IOException, MessagingException {
		List mimeBodyPartList = new ArrayList();
		Document doc = Jsoup.parse(htmlMessageBody);
		HashMap imageSourceContendIdMap = new HashMap();
		int cidCount = 0;
		String fileName = null;
		for (Element img : doc.select("img[src]")) {
			String imageSourceContent = img.attr("src");
			if (imageSourceContent.indexOf("http") == -1) {
				if(imageSourceContent.contains("base64")){
					String[] srcContentArray = imageSourceContent.split(",");
					String formatEncodingString = srcContentArray[0];
					String imageString = srcContentArray[1];
					String imageFormat = formatEncodingString.split(";")[0];
					imageFormat = imageFormat.split("/")[1];
	
					BufferedImage image = null;
	
					BASE64Decoder decoder = new BASE64Decoder();
					byte[] imageByte = decoder.decodeBuffer(imageString);
					ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
					image = ImageIO.read(bis);
					bis.close();
	
					String currentNanoTime = String.valueOf(System.nanoTime());
					fileName = "image" + currentNanoTime + "." + imageFormat;
					File outputfile = new File(imageProcessingPath + fileName);
					ImageIO.write(image, imageFormat, outputfile);
				}else{
					fileName = imageSourceContent;
				}
				cidCount++;
				img.attr("src", "cid:image" + cidCount);
				String imageCidTag = img.toString();
				imageCidTag = img.attr("src");
				imageSourceContendIdMap.put(imageSourceContent, imageCidTag);
				MimeBodyPart imagePart = new MimeBodyPart();
				DataSource fds = new FileDataSource(imageProcessingPath + fileName);
				imagePart.setDataHandler(new DataHandler(fds));
				imagePart.setHeader("Content-ID", "<image" + cidCount + ">");
				imagePart.setDisposition("inline");
				mimeBodyPartList.add(imagePart);
			}
		}
		if (imageSourceContendIdMap.size() > 0) {
			Iterator it = imageSourceContendIdMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry sourceContentIdPair = (Map.Entry) it.next();
				String imageSource = (String) sourceContentIdPair.getKey();
				String imageContentId = (String) sourceContentIdPair.getValue();
				htmlMessageBody = htmlMessageBody.replace(imageSource, imageContentId);
			}
		}
		String htmlUnicodeStr = convertToUnicode(htmlMessageBody);
		MimeBodyPart messageBody = new MimeBodyPart();
		messageBody.setDataHandler(new DataHandler(new ByteArrayDataSource(htmlUnicodeStr, "text/html")));
		emailMimeMultipart.addBodyPart(messageBody);
		Iterator it;
		if ((mimeBodyPartList != null) && (mimeBodyPartList.size() > 0)) {
			for (it = mimeBodyPartList.iterator(); it.hasNext();) {
				emailMimeMultipart.addBodyPart((MimeBodyPart) it.next());
			}
		}
	}

	private static String convertToUnicode(String htmlMessageBody) {
		int htmlBodyStrSize = htmlMessageBody.length();
		String[] codePointAt = new String[htmlBodyStrSize];
		for (int j = 0; j < htmlBodyStrSize; j++) {
			int charactercode = Character.codePointAt(htmlMessageBody, j);
			if ((charactercode > 0) && (charactercode < 128)) {
				codePointAt[j] = String.valueOf(htmlMessageBody.charAt(j));
			} else {
				codePointAt[j] = ("&#" + String.valueOf(charactercode) + ";");
			}
		}
		StringBuilder htmlUnicodeStr = new StringBuilder();
		for (int i = 0; i < codePointAt.length; i++) {
			htmlUnicodeStr.append(codePointAt[i]);
		}
		return htmlUnicodeStr.toString();
	}

}
