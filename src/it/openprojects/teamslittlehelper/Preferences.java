package it.openprojects.teamslittlehelper;


import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.FrameLayout;

public class Preferences extends PreferenceActivity  {
	Context c;
	String app_ver = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		c = this;
		addPreferencesFromResource(R.xml.preferences);
		try { app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName; }
		catch (NameNotFoundException e)	{ }
		// SET VERSION
		Preference ver = (Preference)findPreference("verPref");
		ver.setSummary("Ver. " + app_ver);
		Preference button = (Preference)findPreference("howToPref");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
            	createDialog();
            	  
                return true;
            }
        });
		
	}

	void createDialog() {
		FrameLayout fl = new FrameLayout(c);
		AlertDialog.Builder builder = new AlertDialog.Builder(c).setView(fl);
		builder.setMessage(R.string.howToMsg);
		builder.show(); 
	}
	
}