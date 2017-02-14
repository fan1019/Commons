package com.fmh.commons.email;

import org.junit.Test;

import javax.mail.MessagingException;

/**
 * Created by minghui.fan on 2017/2/14.
 */
public class MailSenderTest {

	@Test
	public void test1() throws MessagingException {
		MailSender mailSender = new MailSender("fmh_hmf@163.com", "fanminghui5193");
		mailSender.send("971416100@qq.com","测试","测试代码");
	}

}
