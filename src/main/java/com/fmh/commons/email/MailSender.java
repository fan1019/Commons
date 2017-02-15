package com.fmh.commons.email;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by minghui.fan on 2017/2/14.
 */
public class MailSender {
	private Properties properties = System.getProperties();
	private MailAuthenticator authenticator;
	private Session session;

	public MailSender(String userAddress, String password) {
		Objects.requireNonNull(userAddress);
		String hostName = "smtp." + userAddress.split("@")[1];
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", hostName);

		authenticator = new MailAuthenticator(userAddress, password);
		session = Session.getInstance(properties, authenticator);
	}

	public void send(String recipient, String subject, Object content) throws MessagingException {
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(authenticator.getUserAddress()));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		message.setSubject(subject);
		message.setContent(content.toString(), "text/html;charset=utf-8");
		Transport.send(message);
	}

	public void send(List<String> recipients, String subject, Object content) throws MessagingException {
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(authenticator.getUserAddress()));
		InternetAddress[] addresses = new InternetAddress[recipients.size()];
		for (int i=0; i<addresses.length; i++){
			addresses[i] = new InternetAddress(recipients.get(i));
		}
		message.setRecipients(Message.RecipientType.TO, addresses);
		message.setSubject(subject);
		message.setContent(content.toString(), "text/html;charset=utf-8");
		Transport.send(message);
	}

	public void send(String recipient, String subject, Object content, List<File> attachments) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(authenticator.getUserAddress()));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		message.setSubject(subject);
		Multipart multipart = new MimeMultipart();
		BodyPart contentPart = new MimeBodyPart();
		contentPart.setContent(content, "text/html;charset=utf-8");
		multipart.addBodyPart(contentPart);
		if (attachments != null) {
			for (File attachment : attachments) {
				BodyPart attachmentFile = new MimeBodyPart();
				DataSource dataSource = new FileDataSource(attachment);
				attachmentFile.setDataHandler(new DataHandler(dataSource));
				attachmentFile.setFileName(MimeUtility.encodeWord(attachment.getName()));
				multipart.addBodyPart(attachmentFile);
			}
		}
		message.setContent(multipart);
		message.saveChanges();
		Transport.send(message);
	}

}
