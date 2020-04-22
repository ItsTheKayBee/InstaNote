package com.example.instanote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    EditText resTitle;
    EditText resText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        String title = getIntent().getStringExtra(PinnedNotes.TITLE);
        String text = getIntent().getStringExtra(PinnedNotes.TEXT);
        resText = findViewById(R.id.resText);
        resTitle = findViewById(R.id.resTitle);
        resText.setText(text);
        resTitle.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        boolean pinned = getIntent().getBooleanExtra(PinnedNotes.PINNED, false);
        if (pinned) {
            inflater.inflate(R.menu.menu, menu);
        }
        return true;
    }
}
