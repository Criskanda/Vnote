package reboot.vnote;

import java.util.Locale;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class AppPreferences extends PreferenceActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		final ListPreference prefListThemes = (ListPreference) findPreference("pref_lenguage");
		
		prefListThemes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
		    public boolean onPreferenceChange(Preference preference, Object newValue) {
		    	SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				changeLanguage(SP.getString(preference.getKey(), "4").toLowerCase(Locale.US));
		        return true;
		    }
		});
		}
	
	private void changeLanguage(String lenguage){
		switch (lenguage) {
		case "Spanish ": //cambiar a que coga el numero, es más facil
			
			break;

		default:
			break;
		}
		
	}

}