package it.openprojects.teamslittlehelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TeamsActivity extends Activity {

	private ListView listView;
	private Button btnAddTeam;
	private ArrayList<String> values;
	private ArrayAdapter<String> adapter;
	final static int RENAME = 0;
	final static int DELETE = 1;
	private int currentSelected = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView( R.layout.teams );
		TextView sectionTitle = (TextView) findViewById(R.id.sectionTitle);
		sectionTitle.setText(this.getString(R.string.sectionTeams));
		btnAddTeam = (Button)findViewById(R.id.add_team);
		values = GameManagement.getSavedTeams();
		Collections.sort(values);
		listView = (ListView)findViewById(R.id.teams);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,android.R.id.text1, values);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		
		currentSelected = values.indexOf(GameManagement.teamDir);
		if (currentSelected == -1) {
			refresh();
		}
		listView.setItemChecked(currentSelected, true);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int i, long l) {
				// TODO Auto-generated method stub
				GameManagement.teamDir = values.get(i);
			}
		});
		
		btnAddTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(TeamsActivity.this);
				input.addTextChangedListener(new CustomTextWatcher(input));
				input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
			
				new AlertDialog.Builder(TeamsActivity.this)
				    .setTitle(R.string.add_team)
				    .setMessage(R.string.team_name)
				    .setView(input)
				    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	String newTeam = input.getText().toString();
				    		values.add(input.getText().toString());
				    		boolean created = createNewTeam(newTeam);
				    		refresh();
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
	
	private boolean createNewTeam(String newTeam) {
		// TODO: SHOULD CHECK OVERWRITE!!!
		String actualTeam = GameManagement.teamDir;
		Team actualPlayers = TeamManagement.players;
		GameManagement.teamDir = newTeam;
		TeamManagement.reset();
		GameManagement.saveGame(GameManagement.ROSTER_FILE);
		GameManagement.teamDir = actualTeam;
		TeamManagement.players = actualPlayers;
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle(values.get(info.position));
	    String[] menuItems = getResources().getStringArray(R.array.item_options);
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	    // DONT REMOVE LAST TEAM
	    if (values.size() == 1) {
	    	menu.removeItem(DELETE);
	    }
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.item_options);
		String menuItemName = menuItems[menuItemIndex];
		final String listItemName = values.get(info.position);

        if(menuItemIndex == DELETE) {
			AlertDialog.Builder adb=new AlertDialog.Builder(TeamsActivity.this);
			adb.setTitle(R.string.delete);
	        adb.setMessage(R.string.delete_confirm);
	        adb.setNegativeButton(android.R.string.no, null);
	        adb.setPositiveButton(android.R.string.yes, new AlertDialog.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	// DELETE saved file
	            	File file = new File(GameManagement.DATA_DIRECTORY + listItemName);
	            	boolean deleted = DeleteRecursive(file);
	            	if (deleted) {
	            		Toast.makeText(getApplicationContext(),R.string.delete_ok, Toast.LENGTH_SHORT).show();
	            	} else {
	            		Toast.makeText(getApplicationContext(),R.string.delete_ko, Toast.LENGTH_SHORT).show();
	            	}
	            	values.remove(info.position);
	            	if (currentSelected>info.position) {
	            		currentSelected=currentSelected-1;
	            	}
	            	refresh();
	            }});
	        adb.show();
        }
        else if(menuItemIndex == RENAME) {
        	final EditText input = new EditText(TeamsActivity.this);
			input.addTextChangedListener(new CustomTextWatcher(input));
			input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		
			new AlertDialog.Builder(TeamsActivity.this)
			    .setTitle(R.string.rename_team)
			    .setView(input)
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			        	String newTeam = input.getText().toString();
			        	boolean renamed = TeamManagement.renameTeam(GameManagement.DATA_DIRECTORY + listItemName, GameManagement.DATA_DIRECTORY + newTeam);
			        	values.set(info.position, newTeam);
			        	refresh();
			        }
			    })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            // Do nothing.
			        }
			    })
			    .show();
        }  

        else {return false;}
  
    return true;  
    }  
	
	
	boolean DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);
	    return fileOrDirectory.delete();
	}

	public void refresh() {
		Collections.sort(values);
		if (currentSelected < 0 || currentSelected >= values.size()) {
			currentSelected = 0;
		}
		GameManagement.teamDir = values.get(currentSelected);
		listView.setItemChecked(currentSelected, true);
	}


}
