package com.fmh.commons.email;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by minghui.fan on 2017/2/14.
 */
public class MailAuthenticator extends Authenticator {
	private String userAddress;
	private String password;

	public MailAuthenticator(String userAddress, String password) {
		this.userAddress = userAddress;
		this.password = password;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userAddress, password);
	}

	public String getPassword() {
		return password;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
}
