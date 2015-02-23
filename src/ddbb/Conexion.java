package ddbb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class Conexion extends SQLiteOpenHelper{
	
	String sql ="CREATE TABLE notes (id INT PRIMARYKEY AUTO INCREMENT, title TEXT, content TEXT)";
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
	
	public void InsertNote (SQLiteDatabase db, String title, String content){
	SQLiteStatement pst = db.compileStatement("INSERT INTO notes (title, content) VALUES (?,?)");
	pst.bindString(1, title);
	pst.bindString(2, content);
	pst.execute();
	}
	
	public void deleteNote(int id) {
	    SQLiteDatabase db = getWritableDatabase();
	    db.delete("notes", "id="+id, null);
	    db.close();  
	}

}
