package reboot.vnote;

import java.util.Locale;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;

public class AppPreferences extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		final ListPreference prefListLenguage = (ListPreference) findPreference("pref_lenguage");
	
		prefListLenguage
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						changeLanguage(newValue.toString());
						return true;
					}
				});
	}

	private void changeLanguage(String lenguage) {
		Locale newLocale = new Locale(lenguage.toLowerCase(Locale.US), lenguage);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = newLocale;
		res.updateConfiguration(conf, dm);
		Intent refresh = new Intent(this, AppPreferences.class);
		Intent thisIntent = getIntent();
		refresh.putExtra("PreviusActivity", thisIntent.getExtras()
				.getParcelable("PreviusActivity"));
		startActivity(refresh);
		finish();

	}

	/**
	 * Listener for items of menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent thisIntent = getIntent();
			Intent a = thisIntent.getExtras().getParcelable("PreviusActivity");
			startActivity(a);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent thisIntent = getIntent();
		Intent a = thisIntent.getExtras().getParcelable("PreviusActivity");
		startActivity(a);
		finish();
	}
}