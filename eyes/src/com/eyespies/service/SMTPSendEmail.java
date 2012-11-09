package com.eyespies.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
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

	  public void sendEmailTo(String user, String password, String hostname, String recipient, String body)  throws AddressException, MessagingException{

	    mUserName=user;
	    mPassword=password;
	    mHostName=hostname;

	    Properties props = getProperties();

	    System.out.println("printing*************");
	    // this object will handle the authentication
	    Session session=Session.getInstance(props,this);
	    MimeMessage emailMessage=new MimeMessage(session);
	    BodyPart msgBody=new MimeBodyPart();
	    MimeMultipart bodyMultipart=new MimeMultipart();


	    emailMessage.setFrom(new InternetAddress(mUserName));
	    emailMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient));  

	    emailMessage.setSubject("new email");    
	    msgBody.setText(body);   
	    bodyMultipart.addBodyPart(msgBody);
	    emailMessage.setContent(bodyMultipart);
	    Transport transport = session.getTransport("smtp");

	    transport.connect(user, password);
	    transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
	    transport.close();
	    System.out.println("sent the message!!");
	  }

	  private static Properties getProperties(){
	    Properties props = new Properties(); 
	   
	    props.put("mail.smtp.host", mHostName);   
	    props.put("mail.smtp.auth", "true");           
	    // default SMTP port
//	    props.put("mail.smtp.port", "25"); 
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.port", "587");
	    return props;
	  }

	  @Override 
	  public PasswordAuthentication getPasswordAuthentication() { 
	    return new PasswordAuthentication(mUserName, mPassword); 
	  } 
	}
