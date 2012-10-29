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
		return "Name: " + contactName + newLine() + "Number: " + phoneNumber + newLine()
				+ "Email Ids: " + emailIds + newLine();
	}

	private String newLine() {
		return "\n";
	}
}
