package it.openprojects.teamslittlehelper;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
	//final static boolean MT = true;
	//static final int EDIT_ROSTER_REQUEST = 0;
	String state = Environment.getExternalStorageState();

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        Button btnRosterManagement = (Button)findViewById(R.id.btnRosterManagement);
        Button btnTeams = (Button)findViewById(R.id.btnTeams);
        Button openButton = (Button)findViewById(R.id.openButton);
        Button btnStats = (Button) findViewById(R.id.btnStats);

        
        

        // INIT TEAMS
		ArrayList<String> values = GameManagement.getSavedTeams();
		
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// FIRST LAUNCH, NO TEAMS
			if (values.isEmpty()) {
				GameManagement.saveGame(GameManagement.ROSTER_FILE);
				values = GameManagement.getSavedTeams();
			}
			GameManagement.teamDir = values.get(0);

		} else {
			//Toast.makeText(getApplicationContext(),R.string.noSDCard, Toast.LENGTH_LONG).show();
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.noSDCard);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.esci,new DialogInterface.OnClickListener(){
			        public void onClick(DialogInterface dialog, int id){
			                dialog.dismiss();
			                finish();
			                }
			        });
			AlertDialog alert=builder.create();
			alert.show();
			
		}
		
		
        
		// TEAMS
        btnTeams.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), TeamsActivity.class);
	        	//myIntent.putExtra("dateFile", GameManagement.ROSTER_FILE);
				startActivity(myIntent);
			}
		});
        
        // ROSTER MANAGEMENT
        btnRosterManagement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), RosterActivity.class);
	        	//myIntent.putExtra("dateFile", GameManagement.ROSTER_FILE);
				startActivity(myIntent);
			}
		});

        // LOAD CALENDAR
        openButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),CalendarActivity.class);
				startActivity(myIntent);
			}
		});

        // STATS
        btnStats.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), StatsActivity.class);
				startActivity(myIntent);
			}
		});
        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Intent myIntent = new Intent(getApplicationContext(), Preferences.class);
		startActivity(myIntent);
		return false;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TeamManagement.reset();
	}
	
	
	
	public void quit() {

		finish();
	}
	
}