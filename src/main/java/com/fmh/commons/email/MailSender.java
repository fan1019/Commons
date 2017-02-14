package com.fmh.commons.email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

}
