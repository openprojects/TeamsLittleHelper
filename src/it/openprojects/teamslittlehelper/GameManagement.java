package it.openprojects.teamslittlehelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.os.Environment;

public class GameManagement {

	public static File EXT_SD_CARD = Environment.getExternalStorageDirectory();
	public static String DATA_DIRECTORY = EXT_SD_CARD + "/TeamsLittleHelper/";
	public static final String ROSTER_FILE = "roster";
	public static final String TLH_EXT = ".tlh";
	public static String DEFAULT_TEAM_DIR = "team00";
	
	public static String teamDir = DEFAULT_TEAM_DIR;
	
	public GameManagement() {
		// TODO Auto-generated constructor stub
	}

	public static ArrayList<String> getSavedTeams() {
		ArrayList<String> items = new ArrayList<String>();
		String name ="";
		File dir = new File(DATA_DIRECTORY);
    	File[] filelist = dir.listFiles();
    	items.clear();
    	if (filelist != null) {
    		for ( int i = 0;i<filelist.length;i++) {
    			name=filelist[i].getName();
    			items.add(name);
    		}
    	}
		return items;
	}
	
	
	public static ArrayList<String> getSavedGamesName() {
		ArrayList<String> items = new ArrayList<String>();
		String name ="";
		File dir = new File(DATA_DIRECTORY+teamDir + "/" );
    	File[] filelist = dir.listFiles();
    	items.clear();
    	if (filelist != null) {
    		for ( int i = 0;i<filelist.length;i++) {
    			name=filelist[i].getName();
    			items.add(name.substring(0, name.indexOf(".")));
    		}
    	}
    	items.remove(ROSTER_FILE);
		return items;
	}
	
	
	public static ArrayList<String> getSavedGamesName(Boolean prefStatsFuture) {
    	ArrayList<String> cleanItems = new ArrayList<String>();
    	ArrayList<String> items = new ArrayList<String>();
    	items = getSavedGamesName();
    	cleanItems.clear();
    	Date today = new Date();
    	Long timeToday = today.getTime();
    	Date actualDate;
    	Long timeActualDate;
    	if (!prefStatsFuture) {
    		for ( int i = 0;i<items.size();i++) {
    			actualDate = CalendarActivity.ConvertToDate(items.get(i),"yyyy-MM-dd");
    			timeActualDate = actualDate.getTime();
    			if (timeActualDate<timeToday) {
    				cleanItems.add(items.get(i));
    			}
    		}
    		items.clear();
    		items = cleanItems;
    	}
    	return items;
	}
	
	public static void saveGame (String dateFile) {
		// CHECK if dir exist or create it
		File file = new File(DATA_DIRECTORY + teamDir + "/");
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            //Log.e("TeamsLittleHelperLog :: ", "Problem creating folder");
	        }
	    }
		try {
			FileOutputStream fos = new FileOutputStream(DATA_DIRECTORY + teamDir + "/" + dateFile + TLH_EXT);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(TeamManagement.players);
			os.close();
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	public static void loadGame (String dateFile) {
		TeamManagement.reset();
		Team rplayers = new Team();
		try {
			FileInputStream fis = new FileInputStream(DATA_DIRECTORY + teamDir + "/" + dateFile + TLH_EXT);
			ObjectInputStream is = new ObjectInputStream(fis);
			rplayers = (Team) is.readObject();
			is.close();
			TeamManagement.players = rplayers;
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
