package reboot.vnote;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ddbb.Conexion;
import ddbb.Note;

public class SelectItems extends Activity {

	ListView lvNotes;
	ArrayList<Note> list = new ArrayList<Note>();
	Note nota;
	SQLiteDatabase db;
	TextView tvNotes;
	Conexion con;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectitems);
		tvNotes = (TextView) findViewById(R.id.tv_notes);
		lvNotes = (ListView) findViewById(R.id.lv_notes);
		// ActionBar and back button.
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		FillListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.search:
			// metodoSearch()
			return true;
		case R.id.delete:
			EraseNote();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

	private void FillListView() {
		con = new Conexion(getApplicationContext(), "DBNotes.db", null, 1);
		db = con.getWritableDatabase();
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
				android.R.layout.simple_list_item_checked, list);

		lvNotes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lvNotes.setAdapter(adapt);
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

	private void TEST_INSERT() {
		con.InsertNote(db, con.getToday(), con.getToday(), con.getToday());
	}
}
