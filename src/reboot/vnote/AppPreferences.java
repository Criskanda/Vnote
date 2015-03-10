package reboot.vnote;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

public class AppPreferences extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		final ListPreference prefListThemes = (ListPreference) findPreference("pref_lenguage");

		prefListThemes
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						SharedPreferences SP = PreferenceManager
								.getDefaultSharedPreferences(getBaseContext());
						changeLanguage(SP.getString("pref_lenguage", "ES")
								.toLowerCase(Locale.US));
						return true;
					}
				});
	}

	private void changeLanguage(String lenguage) {
		Locale myLocale = new Locale(lenguage);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		Intent refresh = new Intent(this, AppPreferences.class);
		startActivity(refresh);

	}

}