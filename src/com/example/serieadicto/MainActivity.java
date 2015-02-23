package com.example.serieadicto;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ddbb.Conexion;
import ddbb.Note;

public class MainActivity extends Activity{
	
	ListView lvNotas,lvNotes;
	ArrayList<Note> list = new ArrayList<Note>();
	Note nota;
	SQLiteDatabase db;
	TextView tvNotes;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		tvNotes =(TextView)findViewById(R.id.tv_notes);
		lvNotes = (ListView) findViewById(R.id.lv_notes);

		Conexion con = new Conexion(getApplicationContext(), "DBNotes.db", null, 1);
		db = con.getWritableDatabase();
		
		Cursor c = db.rawQuery("SELECT * FROM notes", null);
				
		if(c.moveToFirst()){
			do{
				nota = new Note(c.getShort(0),c.getString(1), c.getString(2));
				list.add(nota);			
			}while(c.moveToNext());
		}		
		
		ArrayAdapter<Note> adapt = new ArrayAdapter<Note>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
		lvNotas.setAdapter(adapt);		
		lvNotas.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
					
					String sql = "DELETE FROM notes WHERE id="+list.get(arg2).getId();
					db.execSQL(sql);
					Intent delete = new Intent(MainActivity.this,MainActivity.class);
					startActivity(delete);
					finish();
					return false;
			}
		});	
		
		//ActionBar and back button.
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
	    case android.R.id.home:
	        // app icon in action bar clicked; goto parent activity.
	        this.finish();
	        return true;
	    case R.id.search:
            //metodoSearch()
            return true;
        case R.id.edit:
            //metodoEdit()
            return true;
        case R.id.delete:
            //metodoDelete()
            return true;
        case R.id.add:
            metodoAdd();        	
        	return true;
	    default:
	        return super.onOptionsItemSelected(item);
		}
	}
	
	private void metodoAdd(){
		//Intent newSerie =new Intent(MainActivity.this, NuevaSerie.class);
		//startActivity(newSerie);
	}
}
