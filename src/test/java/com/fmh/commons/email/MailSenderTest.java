package com.fmh.commons.email;

import org.junit.Test;

import javax.mail.MessagingException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by minghui.fan on 2017/2/14.
 */
public class MailSenderTest {

	@Test
	public void test1() throws MessagingException {
		MailSender mailSender = new MailSender("fmh_hmf@163.com", "fanminghui5193");
		mailSender.send("971416100@qq.com","测试","测试代码");
	}

	@Test
	public void test2() throws MessagingException {
		MailSender mailSender = new MailSender("fmh_hmf@163.com", "fanminghui5193");
		mailSender.send(Arrays.asList("971416100@qq.com","fmh_hmf@163.com"),"测试","测试群发邮件");
	}

	@Test
	public void test3() throws UnsupportedEncodingException, MessagingException {
		MailSender mailSender = new MailSender("fmh_hmf@163.com", "fanminghui5193");
		mailSender.send("971416100@qq.com","测试","测试代码", Arrays.asList(new File("D:/Alibaba Java Development Manual.pdf")));
	}

}
