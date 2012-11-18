package com.eyespies.service;

public class ContactInformation {
	
	private final String contactName;
	private final String phoneNumber;
	private final String emailIds;

	public ContactInformation(String contactName, String phoneNumber, String emailId) {
		this.contactName = contactName;
		this.phoneNumber = phoneNumber;
		this.emailIds = emailId;
	}

	public String getInfo() {
		String info = "Name: " + contactName + newLine() + "Number: " + phoneNumber + newLine();
		if(emailIds!=null && !"".equals(emailIds)) {
			info += "Email Ids: " + emailIds + newLine();
		}
		info += newLine();
		return info;
				
	}

	private String newLine() {
		return "\n";
	}
}
