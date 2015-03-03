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
import android.os.Bundle;
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
import ddbb.Conexion;
import ddbb.Note;

public class MainActivity extends Activity {

	ListView lvNotes;
	ArrayList<Note> list = new ArrayList<Note>();
	Note nota;
	SQLiteDatabase db;
	TextView tvNotes;
	Conexion con;
	private String lastQuery = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvNotes = (TextView) findViewById(R.id.tv_notes);
		lvNotes = (ListView) findViewById(R.id.lv_notes);
		// ActionBar and back button.
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		setLastQuery("SELECT id, title FROM notes ORDER BY date DESC"); // default
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
				if (query.trim().equals("")) {
					setLastQuery("SELECT id, title FROM notes ORDER BY date DESC"); // default
				} else {
					setLastQuery("SELECT title FROM notes WHERE title LIKE '%"
							+ query + "%'");
				}
				FillListView();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.trim().equals("")) {
					setLastQuery("SELECT id, title FROM notes ORDER BY date DESC"); // default
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

	private void metodoAdd() {
		Intent newSerie = new Intent(MainActivity.this, NoteActivity.class);
		startActivity(newSerie);
	}

	private void OpenNote(Note note) {
		Intent i = new Intent(MainActivity.this, NoteActivity.class);
		i.putExtra("title", note.getTitle());
		startActivity(i);
	}

	private void FillListView() {
		con = new Conexion(getApplicationContext(), "DBNotes.db", null, 1);
		db = con.getWritableDatabase();
		TEST_INSERT();
		Cursor a = db.rawQuery(getLastQuery(), null);
		list.clear();
		if (a.moveToFirst()) {
			do {
				nota = new Note(a.getString(1));
				list.add(nota);
			} while (a.moveToNext());
		}
		ArrayAdapter<Note> adapt = new ArrayAdapter<Note>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				list);
		lvNotes.setAdapter(adapt);
	}

	private void SelectItemsActivity() {
		Intent newSerie = new Intent(MainActivity.this, SelectItems.class);
		newSerie.putExtra("lastQuery", getLastQuery());
		startActivity(newSerie);
	}

	private void TEST_INSERT() {
		con.InsertNote(db, con.getToday(), con.getToday(), con.getToday());
	}

	public String getLastQuery() {
		return lastQuery;
	}

	public void setLastQuery(String lastQuery) {
		this.lastQuery = lastQuery;
	}
}
