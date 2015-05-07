package cris.programa.vnote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import android.widget.Toast;

public class MainActivity extends Activity {

	ListView lvNotes;
	ArrayList<Note> list = new ArrayList<Note>();
	TextView tvNotes;
	Conexion con;
	SQLiteDatabase db;
	private String lastQuery;
	SharedPreferences SP;
	SearchView search ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvNotes = (TextView) findViewById(R.id.tv_notes);
		lvNotes = (ListView) findViewById(R.id.lv_notes);
		con = new Conexion(getApplicationContext(), "DBNotes.db", null, 1);
		db = con.getWritableDatabase();

		setDefaultQuery();
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

		search = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
		search.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				if (query.trim().equals("")) {
					setDefaultQuery();
					SelectItemsActivity();
				} else {
					setSearchQuery(query);
					SelectItemsActivity();
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.trim().equals("")) {
					setDefaultQuery();
				} else {
					setSearchQuery(newText);
				}
				FillListView();
				return false;
			}

		});
		return true;
	}

	private void setDefaultQuery() {
		SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String order = SP.getString("pref_order", "date")
				.toLowerCase(Locale.US);

		if (order.equals("title")) {
			setLastQuery("SELECT title FROM notes ORDER BY " + order + " "); // default
		} else {
			setLastQuery("SELECT title FROM notes ORDER BY " + order + " DESC"); // default
		}
	}

	private void setSearchQuery(String search) {
		SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String order = SP.getString("pref_order", "date")
				.toLowerCase(Locale.US);

		if (order.equals("title")) {
			setLastQuery("SELECT title FROM notes WHERE title LIKE '%" + search
					+ "%' ORDER BY " + order + " "); // default
		} else {
			setLastQuery("SELECT title FROM notes WHERE title LIKE '%" + search
					+ "%' ORDER BY " + order + " DESC"); // default
		}
	}

	/**
	 * Listener for items of menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit:
			SelectItemsActivity();
			return true;
		case R.id.add:
			metodoAdd();
			return true;
		case R.id.action_import:
			try {
				importFromLastVersion();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.action_settings:
			Intent i = new Intent(this, AppPreferences.class);
			i.putExtra("PreviusActivity", getIntent());
			startActivity(i);
			finish();
			
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
			SendOneNote(menuInfo.position);
			return true;
		case R.id.delete:
			EraseOneNote(menuInfo.position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/** On resume refresh the listview */
	@Override
	public void onResume() {
		super.onResume();
		try {
			if (search.getQuery() =="" || search.getQuery() ==null) {
				setDefaultQuery();
			}else{
				setSearchQuery(search.getQuery().toString());
			}
			FillListView();
		} catch (Exception e) {
			FillListView();
		}
	}

	/** On resume refresh the listview */
	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}
	
	/** Import from the last **/
	private void importFromLastVersion() throws IOException {
		String sdCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File dir = new File(sdCardRoot + "/Notas de vnote");
		String[] noteTitles = null;
		try {
			noteTitles = dir.list();
		} catch (Exception e) {
			return;
		}

		if (noteTitles.length > 0) {
			for (int i = 0; i < noteTitles.length; i++) {
				File f2 = new File(dir, noteTitles[i]);
				BufferedReader fin = new BufferedReader(new InputStreamReader(
						new FileInputStream(f2)));
				String content = "";
				String line;

				while ((line = fin.readLine()) != null) {
					content += line + "\n";
				}
				fin.close();
				f2.delete();
				con = new Conexion(getApplicationContext(), "DBNotes.db", null,
						1);
				db = con.getWritableDatabase();
				con.InsertNote(db, noteTitles[i].replace(".txt", ""), content,
						con.getToday());
			}
		} else {
			Toast.makeText(this, R.string.files_not_found,
					Toast.LENGTH_SHORT).show();
		}

	}

	private void SendOneNote(int position) {
		Cursor a = db.rawQuery("SELECT title,content FROM notes WHERE title ='"
				+ list.get(position).getTitle() + "'", null);
		list.clear();
		Note note = null;
		if (a.moveToFirst()) {
			do {
				note = new Note(a.getString(0), a.getString(1));
				list.add(note);
			} while (a.moveToNext());
		}

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				note.getTitle());
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				note.getContent());
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
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
		alert.setTitle(R.string.warning_delete);
		final TextView tv_alert = new TextView(this);
		tv_alert.setText(note.getTitle());
		alert.setView(tv_alert);

		alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				con.deleteNote(note.getTitle());
				FillListView();
			}
		});
		alert.setNegativeButton(R.string.no, null);
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
		finish();
	}

	/**
	 * Fill the listview
	 */
	private void FillListView() {
		//TEST_INSERT();
		Cursor a = db.rawQuery(getLastQuery(), null);
		list.clear();
		if (a.moveToFirst()) {
			do {
				Note note = new Note(a.getString(0));
				list.add(note);
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
		Intent selecNotes = new Intent(MainActivity.this, SelectItems.class);
		selecNotes.putExtra("lastQuery", getLastQuery());
		startActivity(selecNotes);
		finish();
	}

//	private void TEST_INSERT() {
//		con.InsertNote(db, con.getToday(), con.getToday(), con.getToday());
//	}

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
