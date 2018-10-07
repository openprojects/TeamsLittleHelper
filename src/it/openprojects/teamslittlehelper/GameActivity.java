package it.openprojects.teamslittlehelper;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends ListActivity {
	
    static final int DATE_DIALOG_ID = 0;
    static final int PRESENTE = 0;
    static final int RITARDO = 1;
    static final int ASSENTE = 2;

    private SimpleAdapter adapter;
	private ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
	private HashMap<String,Object> personMap=new HashMap<String, Object>();
	private String dateFile;
	private Integer saved;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView( R.layout.game );
		
		Intent intent= getIntent();
		Bundle b = intent.getExtras();
		dateFile = b.getString("dateFile");
		saved = b.getInt("saved");

		TextView sectionTitle = (TextView) findViewById(R.id.sectionTitle);
		sectionTitle.setText(this.getString(R.string.sectionGame) + " - " + dateFile);
		
        Button btnSave = (Button) findViewById(R.id.save);
        
        // INIT PRESENCE IF NOT SAVED DATA
        if (saved == 1 ) {
        	GameManagement.loadGame(dateFile);
        } else {
        	GameManagement.loadGame(GameManagement.ROSTER_FILE);
        	TeamManagement.initPresence(); 
        }
        // SORT PLAYERS
        TeamManagement.sortPlayers();
        
		for(int i=0;i<TeamManagement.players.size();i++){
            Player p=TeamManagement.players.get(i);
            personMap=new HashMap<String, Object>();
            //personMap.put("image", p.getPhotoRes());
            personMap.put("name", p.getNome());
            personMap.put("presence", p.getPresenzaIcon());
            data.add(personMap);
		}
		
		String[] from={"name","presence"};
        int[] to={R.id.player,R.id.icon};
        adapter=new SimpleAdapter(getApplicationContext(), data, R.layout.rowlayout, from, to);
		setListAdapter(adapter);	
        
        // SAVE
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	GameManagement.saveGame(dateFile);
       			Toast.makeText(getApplicationContext(), R.string.salvato, Toast.LENGTH_SHORT).show();
       			finish();
            }
        });

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int val;
		val = TeamManagement.players.get(position).getPresenza();
		if (val == 2 ) { val = 0; }
		else { val+=1; }
	
		TeamManagement.players.get(position).setPresenza(val);
		data.get(position).put("presence", TeamManagement.players.get(position).getPresenzaIcon());
		adapter.notifyDataSetChanged();
	}
            
}
