package oodp_mystars;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import oodp_mystars.Enumerator.*;

/**
 * @author Audrey KinSum Kelvin JianHao
 * @version 1.0
 * @since 2017-04-13
 */
public class Notifier
{

	private static String senderEmail = "cz2002.fsp2.agrp3@gmail.com";
	private static String senderPassword = "Assignmentgrp3";

	// Send notification to student to notify them that they are in waitlist or
	// officially registered under course.
	public static void sendEmail(String studentEmail, String courseId, int indexNo, Group_Status message)
	{
		// Setup Properties for the email session
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "25");
		properties.setProperty("mail.smtp.starttls.enable", "true");

		// Establish connection with the account you wish to login to
		javax.mail.Session emailSession = Session.getDefaultInstance(properties, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(senderEmail, senderPassword);
			}
		});
		
		MimeMessage email;
		try
		{
			email = new MimeMessage(emailSession);
			email.setFrom(senderEmail);
			email.addRecipient(RecipientType.TO, new InternetAddress(studentEmail));

			// Switch between Messages for waitlist or notification depending on
			// Notifier_Type
			switch (message)
			{
			case WAITLIST:
			{ // Set subject and message for Waitlist Notification
				email.setSubject("Waitlist Notification");
				email.setText("Dear Student," + "\n" + "\n"
						+ "This message has been sent to inform you that you have been put into the waiting list "
						+ "of the Course " + courseId + " for Group " + indexNo + ".\n" + "\n"
						+ "We hope that you can obtain the Course of your choice.\n" + "\n" + "Thank you.\n" + "\n"
						+ "Signed\n" + "\n" + "MySTARS System");
				break;
			}
			case REGISTERED:
			{ // Set subject and message for Registration Notification
				email.setSubject("Registration Notification");
				email.setText("Dear Student," + "\n" + "\n"
						+ "This message has been sent to inform you that you have successful been registered under "
						+ "Course " + courseId + " for Group " + indexNo + ".\n" + "\n"
						+ "We hope you manage to recieve the schedule of you choice.\n" + "\n" + "Thank you.\n" + "\n"
						+ "Signed\n" + "\n" + "MySTARS System");
				break;
			}
			}
			Transport.send(email);
			System.out.println("[System] Mail have been sent successfully.");
		} catch (MessagingException e)
		{
			e.printStackTrace();
			System.out.println("[System] Mail cannot be sent.");
		}
	}

	public static void sendSMS(String phoneNo)
	{
		System.out.println("SMS was sent to " + phoneNo);
	}
}
