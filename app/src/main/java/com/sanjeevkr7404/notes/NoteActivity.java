package com.sanjeevkr7404.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

public class NoteActivity extends AppCompatActivity {

    public static final String rId = "com.example.notes.id";
    public static final String rTitle = "com.example.notes.title";
    public static final String rDescription = "com.example.notes.description";
    private int id;
    private TextView titleText, descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    private void initialize() {
        Intent intent = getIntent();
        titleText = findViewById(R.id.title);
        descriptionText = findViewById(R.id.description);
        id = intent.getIntExtra("id", -1);
        setTitle(intent.getStringExtra("menu"));
        titleText.setText(intent.getStringExtra("title"));
        descriptionText.setText(intent.getStringExtra("description"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.save:
                returnResult();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        returnResult();
    }

    private void returnResult() {
        Intent intent = new Intent();
        intent.putExtra(rId, id);
        intent.putExtra(rTitle, titleText.getText().toString());
        intent.putExtra(rDescription, descriptionText.getText().toString());
        setResult(2, intent);
        finish();
    }
}
