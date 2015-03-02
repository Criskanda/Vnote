package ddbb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class Conexion extends SQLiteOpenHelper{
	
	String sql ="CREATE TABLE notes (id INT PRIMARYKEY AUTO INCREMENT, title TEXT, content TEXT, date DATE)";
	public Conexion(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void InsertNote (SQLiteDatabase db, String title, String content, String date){
	SQLiteStatement pst = db.compileStatement("INSERT INTO notes (title, content,date) VALUES (?,?,?)");
	pst.bindString(1, title);
	pst.bindString(2, content);
	pst.bindString(3, date);

	pst.execute();
	}
	
	public void deleteNote(String title) {
	    SQLiteDatabase db = getWritableDatabase();
	    db.delete("notes", "title='"+title+"'", null);
	    db.close();  
	}
	public String getToday(){
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
