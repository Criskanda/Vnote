package reboot.vnote;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class NoteActivity extends Activity{
	EditText tittle,content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		RelativeLayout ventana = (RelativeLayout) findViewById(R.id.main_activity);
		ventana.setBackgroundColor(Color.BLACK);
		
		tittle = (EditText)findViewById(R.id.et_title);
	
	}

}
