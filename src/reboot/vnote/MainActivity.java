package reboot.vnote;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import ddbb.Conexion;
import ddbb.Note;

public class MainActivity extends Activity {

	ListView lvNotes;
	ArrayList<Note> list = new ArrayList<Note>();
	Note nota;
	SQLiteDatabase db;
	TextView tvNotes;
	Conexion con;
	private String lastQuery;

	private AudioManager audio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvNotes = (TextView) findViewById(R.id.tv_notes);
		lvNotes = (ListView) findViewById(R.id.lv_notes);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// ActionBar and back button.
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		setLastQuery("SELECT title FROM notes ORDER BY date DESC"); // default
																	// query
		FillListView();

		lvNotes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				OpenNote(list.get(position));
			}
		});

		lvNotes.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// Coje los datos del activity menu creada
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.context_main, menu);
			}
		});
	}

	/**
	 * MenuOptions and search
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		SearchView search = (SearchView) menu.findItem(R.id.search)
				.getActionView();

		search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
		search.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.trim().equals("")) {
					setLastQuery("SELECT title FROM notes ORDER BY date DESC"); // default
				} else {
					setLastQuery("SELECT title FROM notes WHERE title LIKE '%"
							+ newText + "%'");
				}
				FillListView();
				return false;
			}

		});
		return true;
	}

	/**
	 * Listener for items of menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.edit:
			SelectItemsActivity();
			return true;
		case R.id.add:
			metodoAdd();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Listener of the context menu items
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.send:
			// method listen
			return true;
		case R.id.delete:
			EraseOneNote(menuInfo.position);
			return true;
		case R.id.listen:
			// method listen
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		FillListView();
	}

	/**
	 * This method show a dialog and ask for confirmation for delete a note
	 * 
	 * @param position
	 *            for search in the list
	 */
	private void EraseOneNote(int position) {
		final Note note = list.get(position);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Do you want to DELETE the following note?");
		final TextView tv_alert = new TextView(this);
		tv_alert.setText(note.getTitle());
		alert.setView(tv_alert);

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				con.deleteNote(note.getTitle());
				FillListView();
			}
		});
		alert.setNegativeButton("No", null);
		alert.show();
	}

	/**
	 * Start the activity to NoteActivity class
	 */
	private void metodoAdd() {
		Intent newSerie = new Intent(MainActivity.this, NoteActivity.class);
		startActivity(newSerie);
	}

	/**
	 * Start the activity to NoteActivity class and put in the intent the title
	 * of the note
	 */
	private void OpenNote(Note note) {
		Intent i = new Intent(MainActivity.this, NoteActivity.class);
		i.putExtra("title", note.getTitle());
		startActivity(i);
	}

	/**
	 * Fill the listview
	 */
	private void FillListView() {
		con = new Conexion(getApplicationContext(), "DBNotes.db", null, 1);
		db = con.getWritableDatabase();
		TEST_INSERT();
		Cursor a = db.rawQuery(getLastQuery(), null);
		list.clear();
		if (a.moveToFirst()) {
			do {
				nota = new Note(a.getString(0));
				list.add(nota);
			} while (a.moveToNext());
		}
		ArrayAdapter<Note> adapt = new ArrayAdapter<Note>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				list);
		lvNotes.setAdapter(adapt);
	}

	/**
	 * Start the activity to SelectItems class
	 */
	private void SelectItemsActivity() {
		Intent newSerie = new Intent(MainActivity.this, SelectItems.class);
		newSerie.putExtra("lastQuery", getLastQuery());
		startActivity(newSerie);
	}

	private void TEST_INSERT() {
		con.InsertNote(db, con.getToday(), con.getToday(), con.getToday());
	}

	/**
	 *Keylistener for up and down audio media  
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

	/**
	 * @return the lastQuery
	 */
	public String getLastQuery() {
		return lastQuery;
	}

	/**
	 * @param lastQuery
	 *            the lastQuery to set
	 */
	public void setLastQuery(String lastQuery) {
		this.lastQuery = lastQuery;
	}

}
