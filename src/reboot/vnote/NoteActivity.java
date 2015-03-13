package reboot.vnote;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import ddbb.Conexion;
import ddbb.Note;

public class NoteActivity extends Activity {

	EditText ETtittle, ETcontent;
	Note noteOpen;
	Conexion con;
	SQLiteDatabase db;
	private AudioManager audio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		ETtittle = (EditText) findViewById(R.id.et_title);
		ETcontent = (EditText) findViewById(R.id.et_content);
		Intent thisIntent = getIntent();
		con = new Conexion(getApplicationContext(), "DBNotes.db", null, 1);
		db = con.getWritableDatabase();

		if (thisIntent.hasExtra("title")) {
			noteOpen = FillFields(thisIntent.getExtras().getString("title"));
		}

	}

	private Note FillFields(String title) {

		Cursor c = db.rawQuery("SELECT * FROM notes WHERE title = ?",
				new String[] { title });
		System.out.println(c.getCount());
		Note note_from_select = null;
		if (c.moveToFirst()) {
			do {
				note_from_select = new Note(c.getInt(0), c.getString(1),
						c.getString(2));
			} while (c.moveToNext());
		}
		ETtittle.setText(note_from_select.getTitle());
		ETcontent.setText(note_from_select.getContent());
		return note_from_select;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			Intent a = new Intent(this, MainActivity.class);
			startActivity(a);
			finish();
			
			return true;
		case R.id.save:
			CheckTypeNoteAndSave();
			return true;
		case R.id.speach:
			// metodoSpeach()
			return true;
		case R.id.listen:
			// metodoListen()
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent a = new Intent(this, MainActivity.class);
		startActivity(a);
	}

	private void CheckTypeNoteAndSave() {
		if (CheckFields()) {
			if (noteOpen != null) {
				if (UpdateNote()) {
					Toast.makeText(this, R.string.save_ok, Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(this, R.string.note_exists,
							Toast.LENGTH_SHORT).show();
				}
			} else if (InsertNote()) {
				Toast.makeText(this, "Save Successfully", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this, R.string.note_exists, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private boolean CheckFields() {
		String s1 = ETtittle.getText().toString().trim();
		String s2 = ETcontent.getText().toString().trim();

		if (s1.length() == 0 || s2.length() == 0) {
			Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else {
			return true;
		}

	}

	private boolean UpdateNote() {
		String idNote = noteOpen.getId() + "";
		String title = ETtittle.getText().toString().trim();
		title = title.substring(0, 1).toUpperCase() + title.substring(1);
		String content = ETcontent.getText().toString().trim();
		if (!title.equals(noteOpen.getTitle())) { // Si el titulo en el ET NO es
													// igual al original
													// COMPRUEBA
			if (!titleExist(title)) {
				ContentValues upd = new ContentValues();
				upd.put("id", idNote); // These Fields should be your String
										// values of actual column names
				upd.put("title", title);
				upd.put("content", content);
				upd.put("date", noteOpen.getDate());
				db.update("notes", upd, "title " + "='" + noteOpen.getTitle()
						+ "'", null);
				return true;
			} else {
				return false;
			}
		} else { // Si el titulo en el ED ES igual al original NO COMPRUEBA
			ContentValues upd = new ContentValues();
			upd.put("id", idNote); // These Fields should be your String values
									// of actual column names
			upd.put("title", title);
			upd.put("content", content);
			upd.put("date", noteOpen.getDate());
			db.update("notes", upd,
					"title " + "='" + noteOpen.getTitle() + "'", null);

			return true;
		}

	}

	/**
	 * Insert the note in the ddbb
	 * 
	 * @return true if the note is insert successfully
	 */
	private boolean InsertNote() {
		String title = ETtittle.getText().toString().trim();
		title = title.substring(0, 1).toUpperCase() + title.substring(1);

		String content = ETcontent.getText().toString().trim();

		if (!titleExist(title)) {
			try {
				con.InsertNote(db, title, content, con.getToday());
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			Toast.makeText(this, R.string.note_exists, Toast.LENGTH_SHORT)
					.show();
			return false;
		}

	}

	/**
	 * 
	 * @param title
	 *            for check in ddbb
	 * @return true if a note with that title exists
	 */
	private boolean titleExist(String title) {
		Cursor cursor = db.rawQuery("SELECT 1 FROM notes WHERE title=?",
				new String[] { title });
		boolean exist = cursor.moveToFirst();
		cursor.close();
		return exist;
	}

	/**
	 * Keylistener for up and down audio media
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		// Para controlar el volumen
		case KeyEvent.KEYCODE_VOLUME_UP:
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
			return true;
		default:
			return true;
		}

	}

}
