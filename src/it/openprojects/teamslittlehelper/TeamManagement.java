package it.openprojects.teamslittlehelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;


public class TeamManagement {
	
	public static Team players = new Team();
	static Context context;
	public static String[] tipi;
	
	public TeamManagement() {
		super();
		players.clear();
		// TODO Auto-generated constructor stub
	}	
	
	public static void sortPlayers() {
		Collections.sort(players, new CustomComparator());	
	}
	
	public static ArrayList<String> getNames() {
		ArrayList<String> data = new ArrayList<String>();
		data.clear();
		for (int i = 0; i < players.size(); i++) {
			data.add(players.get(i).getNome());
		}
		return data;
    }
	
	public static void reset() {
		// INITIALIZE
		players.clear();
	}
	
	public static void initPresence() {
		// SET ALL PRESENT
		for (int i = 0; i < players.size(); i++) {
			TeamManagement.players.get(i).setPresenza(Player.PRESENTE);
		}
	}
	
	public static void setNames(ArrayList<String> data) {
    	// LOAD PLAYERS NAME
		players.clear();
    	Player player;
		for (int i = 0; i < data.size(); i++) {
			player = new Player();
			player.setNome(data.get(i));
			players.add(player);
		}
    }
	
	
	
	public static boolean renameTeam (String oldPath, String newPath) {
		File file = new File(oldPath);
		File newFile = new File(newPath);
		if (!file.exists()) {
			return false;
		}
    	return file.renameTo(newFile);
	}	
	
	
	
}

class CustomComparator implements Comparator<Player> {
    @Override
    public int compare(Player o1, Player o2) {
        //return o1.getStartDate().compareTo(o2.getStartDate());
    	return o1.getNome().compareTo(o2.getNome());
    }
}
