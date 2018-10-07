package it.openprojects.teamslittlehelper;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
 

public class RosterActivity extends Activity {
	
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private static ArrayList<String> data = new ArrayList<String>();
	private Button btnAddPlayer;
	//private Spinner spnMeetingType;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView( R.layout.roster );
		
		TextView sectionTitle = (TextView) findViewById(R.id.sectionTitle);
		sectionTitle.setText(this.getString(R.string.sectionRoster) + " " + GameManagement.teamDir);
		listView = (ListView)findViewById(R.id.roster);
		btnAddPlayer = (Button)findViewById(R.id.add_player);
		//spnMeetingType = (Spinner)findViewById(R.id.spinner_meeting);
        
		// LOAD ROSTER TEAM
		GameManagement.loadGame(GameManagement.ROSTER_FILE);
		data.clear();
		data = TeamManagement.getNames();
		// SORT
		Collections.sort(data);
		
		
		adapter = new ArrayAdapter <String>(getApplicationContext(), android.R.layout.simple_list_item_1, data);
		listView.setAdapter(adapter);
		/*
		spnMeetingType.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posizione, long arg3) {
				//TeamManagement.info.setTipo(TeamManagement.tipi[posizione]);
			}
		});
		*/
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				AlertDialog.Builder adb=new AlertDialog.Builder(RosterActivity.this);
				adb.setTitle(R.string.delete);
		        adb.setMessage(R.string.delete_confirm);
		        final int positionToRemove = position;
		        adb.setNegativeButton(android.R.string.no, null);
		        adb.setPositiveButton(android.R.string.yes, new AlertDialog.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	data.remove(positionToRemove);
		            	TeamManagement.players.remove(positionToRemove);
		            	adapter.notifyDataSetChanged();
		            }});
		        adb.show();
				return false;
			}
		});

		btnAddPlayer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(RosterActivity.this);
				input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
			
				new AlertDialog.Builder(RosterActivity.this)
				    .setTitle(R.string.add_player)
				    .setMessage(R.string.player_name)
				    .setView(input)
				    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				    		data.add(input.getText().toString());
				            // SORT
				    		Collections.sort(data);
				        	TeamManagement.players.add(new Player(input.getText().toString(),null,0));
				            adapter.notifyDataSetChanged();

				        }
				    })
				    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            // Do nothing.
				        }
				    })
				    .show();
			}
		});
		
	}
	

	protected void onPause() {
		super.onPause();
		quit();
	}
	
	public void quit() {
		GameManagement.saveGame(GameManagement.ROSTER_FILE);
		finish();
	}
}
