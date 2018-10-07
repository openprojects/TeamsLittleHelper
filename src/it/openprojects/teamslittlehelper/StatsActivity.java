package it.openprojects.teamslittlehelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


public class StatsActivity extends Activity {
	
	private TreeMap<String, playerStats> pStats = new TreeMap<String, playerStats>();
	private Button btnSaveStats;
	private ArrayList<String> games;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.stats);
		
		btnSaveStats = (Button)findViewById(R.id.save_stats);
		TextView sectionTitle = (TextView) findViewById(R.id.sectionTitle);
		sectionTitle.setText(this.getString(R.string.sectionStats) + " " + GameManagement.teamDir);
		// GET ALL GAMES
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Boolean prefStatsFuture = prefs.getBoolean("prefStatsFuture", true);
		games = GameManagement.getSavedGamesName(prefStatsFuture);
		pStats.clear();
		
		calculateStats();
		
		btnSaveStats.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Context gac = getApplicationContext();
				try {
				    // Write 20 Strings
					String actualDate = android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString();
					File FILENAME = new File (GameManagement.DATA_DIRECTORY + GameManagement.teamDir + "/","stats-" + actualDate + ".html");
					
					FileOutputStream fOut = new FileOutputStream(FILENAME);
					OutputStreamWriter out = new OutputStreamWriter(fOut);
				    // HTML headers
			        out.append("<html><body>");
			        out.append("<h1>" + gac.getString(R.string.app_name) + "</h1>\n");

			        // team name
			        out.append("<h2>" + gac.getString(R.string.sectionStats) + " " + String.valueOf(GameManagement.teamDir) + " (" + actualDate + ")</h2>");
			    
			        // Player in roster
			        out.append("<h3>" + gac.getString(R.string.total_saved) + " " + String.valueOf(games.size()) + "</h3>\n");
			        
			        // Create table
			        out.append("<table cellspacing=\"0\">");
			        out.append("<tr>");
			        out.append("<th>" + gac.getString(R.string.player) + "</th>");
			        out.append("<th>" + gac.getString(R.string.in_roster) + "</th>");
			        out.append("<th>" + gac.getString(R.string.present) + "</th>");
			        out.append("<th>" + gac.getString(R.string.late) + "</th>");
			        out.append("<th>" + gac.getString(R.string.absent) + "</th>\n");
			        
			        // Populate table
			        int i=0;
			        String cell_style = "\"border-bottom: 1px solid grey\""; 
			        for (Map.Entry<String,playerStats> entry : pStats.entrySet()) {
			        	i+=1;
			            String key = entry.getKey();
			            out.append("<tr>");
			            out.append("<td style=" + cell_style + ">" + key + "</td>");
			            out.append("<td style=" + cell_style + ">" + String.valueOf(pStats.get(key).getInRoster()) + "</td>");
			            out.append("<td style=" + cell_style + ">" + printStat(pStats.get(key).getPresent(),pStats.get(key).getInRoster()) + "</td>");
			            out.append("<td style=" + cell_style + ">" + printStat(pStats.get(key).getLate(),pStats.get(key).getInRoster()) + "</td>");
			            out.append("<td style=" + cell_style + ">" + printStat(pStats.get(key).getAbsent(),pStats.get(key).getInRoster()) + "</td>");
			            out.append("</tr>\n");
			        }

			        out.append("</table>\n");
			        
			        // close HTML headers
			        out.append("<h6>" + gac.getString(R.string.stats_credits) + "</h6>\n");
			        out.append("</body></html>");
			        
				    out.close();
				    String okmsg = gac.getString(R.string.stats_saved_folder) + " " + GameManagement.DATA_DIRECTORY + GameManagement.teamDir;
				    Toast.makeText(gac,okmsg, Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					Toast.makeText(gac,gac.getString(R.string.delete_ko), Toast.LENGTH_LONG).show();
				    //Log.i("Data Input Sample", "I/O Error");
				}
			}
		});
		
		// NOW ADD ROWS
        TableLayout tl = (TableLayout)findViewById(R.id.statsTable);
        TextView tv;
        
        // SET TOTAL GAMES
        tv = (TextView) findViewById(R.id.totalSavedValue);
        tv.setText(String.valueOf(games.size()));
        
        // Create a new row to be added.
        int i=0;
        for (Map.Entry<String,playerStats> entry : pStats.entrySet()) {
        	i+=1;
            String key = entry.getKey();
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            if ((i % 2) == 0) {	tr.setBackgroundColor(R.color.NiceButtonEndColor); }
            //tr.setPadding(0, 0, 0, 2);
            // PLAYER NAME
            tv = new TextView(this);
            tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
            tv.setText(key);
            tr.addView(tv);
            // IN ROSTER
            tv = new TextView(this);
            tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
            tv.setText(String.valueOf(pStats.get(key).getInRoster()));
            tr.addView(tv);
            // PRESENT
            tv = new TextView(this);
            tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
            tv.setText(printStat(pStats.get(key).getPresent(),pStats.get(key).getInRoster()));
            tr.addView(tv);
            // LATE
            tv = new TextView(this);
            tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
            tv.setText(printStat(pStats.get(key).getLate(),pStats.get(key).getInRoster()));
            tr.addView(tv);
            // ABSENT
            tv = new TextView(this);
            tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
            tv.setText(printStat(pStats.get(key).getAbsent(),pStats.get(key).getInRoster()));
            tr.addView(tv);
            
            // Add row to TableLayout. 
            tl.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }
			
	}
	
	public void calculateStats() {
		for(int g=0;g<games.size();g++){
			// LOAD GAME
			GameManagement.loadGame(games.get(g));
			// LOAD PLAYER DATA
			playerStats ps;
			Player player;
			for(int i=0;i<TeamManagement.players.size();i++){
				player = TeamManagement.players.get(i);
	            ps = new playerStats();
	            if (pStats.containsKey(player.getNome())) {
	            	ps = pStats.get(player.getNome());
	            }
	            ps.setInRoster(ps.getInRoster()+1);
	            switch (player.getPresenza()){
	            	case GameActivity.PRESENTE: 
	            		ps.setPresent(ps.getPresent()+1);
	            		break;
	            	case GameActivity.RITARDO: 
	            		ps.setLate(ps.getLate()+1);
	            		break;
	            	case GameActivity.ASSENTE: 
	            		ps.setAbsent(ps.getAbsent()+1);
	            		break;
	            }
	            pStats.put(player.getNome(), ps);
			}
		}
	}
	
	public String printStat(int num, int den) {
		return String.valueOf(num) + " ("+String.valueOf((num*100)/den)+ "%)";
	}
	
}

class playerStats {
	private int inRoster = 0;
	private int present = 0;
	private int late = 0;
	private int absent = 0;
	public void setInRoster(int inRoster) {
		this.inRoster = inRoster;
	}
	public int getInRoster() {
		return inRoster;
	}
	public void setPresent(int present) {
		this.present = present;
	}
	public int getPresent() {
		return present;
	}
	public void setLate(int late) {
		this.late = late;
	}
	public int getLate() {
		return late;
	}
	public void setAbsent(int absent) {
		this.absent = absent;
	}
	public int getAbsent() {
		return absent;
	}
}
