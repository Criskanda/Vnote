package reboot.vnote;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);

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
			this.finish();
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

	private void CheckTypeNoteAndSave() {
		if (CheckFields()) {
			if (noteOpen != null) {
				if (UpdateNote()) {
					Toast.makeText(this, "Save Successfully", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT)
							.show();
				}
			} else if (InsertNote()) {
				Toast.makeText(this, "Save Successfully", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private boolean CheckFields() {
		String s1 = ETtittle.getText().toString().trim();
		String s2 = ETcontent.getText().toString().trim();

		if (s1.length() == 0 || s2.length() == 0) {
			Toast.makeText(this, "Someone fields is empty", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else {
			return true;
		}

	}

	private boolean UpdateNote() {
		String idNote = noteOpen.getId() + "";
		String tittle = ETtittle.getText().toString().trim();
		String content = ETcontent.getText().toString().trim();
		if (!titleExist(tittle)) {
			try {
				db.rawQuery("UPDATE notes set id=" + idNote + ",title='"
						+ tittle + "',content ='" + content + "' WHERE "
						+ "id=" + idNote, null);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}

	}

	private boolean InsertNote() {
		String tittle = ETtittle.getText().toString().trim();
		String content = ETcontent.getText().toString().trim();
		if (!titleExist(tittle)) {
			try {
				db.rawQuery("INSERT INTO notes VALUES ('','" + tittle + "','"
						+ content + "')", null);
				return true;
			} catch (Exception e) {
				System.out
						.println("ERROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOR");
				return false;
			}
		} else {
			return false;
		}

	}

	private boolean titleExist(String title) {
		 Cursor cursor = db.rawQuery("SELECT 1 FROM notes WHERE title=?", new String[] {title});
		 cursor.close();
		 return cursor.moveToFirst();
		
	}
}
