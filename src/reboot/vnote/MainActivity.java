package reboot.vnote;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
	boolean EDITMODE = false;

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

		FillListView();
		lvNotes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (EDITMODE) {
					SparseBooleanArray checked = lvNotes
							.getCheckedItemPositions();
					if (checked.get(position) == false) {
						lvNotes.setItemChecked(position, false);
					} else {
						lvNotes.setItemChecked(position, true);
					}
				} else {
					SparseBooleanArray checked = lvNotes
							.getCheckedItemPositions();
					if (checked.get(position) == false) {
						lvNotes.setItemChecked(position, true);
						OpenNote(list.get(position));

					} else {
						lvNotes.setItemChecked(position, false);
						OpenNote(list.get(position));

					}
				}
			}
		});

		lvNotes.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				SparseBooleanArray checked = lvNotes.getCheckedItemPositions();
				if (checked.get(position) == true) {
					lvNotes.setItemChecked(position, false);
					return true;
				} else {
					lvNotes.setItemChecked(position, true);
					return true;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; goto parent activity.
			this.finish();
			return true;
		case R.id.search:
			// metodoSearch()
			return true;
		case R.id.edit:
			// Añadir metodo de borrar aqui
			ActivateEditMode();
			return true;
		case R.id.add:
			metodoAdd();
			return true;
		case R.id.delete:
			EraseNote();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

	private void ActivateEditMode() {
		if (EDITMODE) {
			EDITMODE = false;
		}else {
			EDITMODE = true;
		}
	}

	private void EraseNote() {
		String listItemsSelected = getSelectedItems();
		if (!listItemsSelected.equals("")) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Do you want to DELETE the following notes?");
			final TextView tv_alert = new TextView(this);
			tv_alert.setText(listItemsSelected);
			alert.setView(tv_alert);

			alert.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							int len = lvNotes.getCount();
							final SparseBooleanArray checked = lvNotes
									.getCheckedItemPositions();
							for (int i = 0; i < len; i++) {
								if (checked.get(i)) {
									Note item = list.get(i);
									con.deleteNote(item.getTitle());
								}
							}
							FillListView();
						}
					});

			alert.setNegativeButton("No", null);
			alert.show();

		}

	}

	private void FillListView() {
		con = new Conexion(getApplicationContext(), "DBNotes.db", null, 1);
		db = con.getWritableDatabase();
		TEST_INSERT();
		TEST_INSERT();
		TEST_INSERT();
		TEST_INSERT();

		Cursor a = db.rawQuery(
				"SELECT id, title FROM notes ORDER BY date DESC", null);
		list.clear();
		if (a.moveToFirst()) {
			do {
				nota = new Note(a.getShort(0), a.getString(1));
				list.add(nota);
			} while (a.moveToNext());
		}

		ArrayAdapter<Note> adapt = new ArrayAdapter<Note>(
				getApplicationContext(),
				android.R.layout.simple_list_item_multiple_choice, list);

		lvNotes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lvNotes.setAdapter(adapt);
	}

	private String getSelectedItems() {
		String auxstr = "";
		int len = lvNotes.getCount();
		final SparseBooleanArray checked = lvNotes.getCheckedItemPositions();
		for (int i = 0; i < len; i++) {
			if (checked.get(i)) {
				Note item = list.get(i);
				auxstr += item.getTitle() + "\n";
			}
		}
		return auxstr;
	}

	private void TEST_INSERT() {
		con.InsertNote(db, con.getToday(), con.getToday(), con.getToday());
	}
}
