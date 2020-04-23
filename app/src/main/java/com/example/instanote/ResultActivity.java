package com.example.instanote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    EditText resTitle;
    EditText resText;
    boolean pinned = false;

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
        pinned = getIntent().getBooleanExtra(PinnedNotes.PINNED, false);
        inflater.inflate(R.menu.menu, menu);
        final MenuItem star = menu.findItem(R.id.save);
        star.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (pinned == true) {
                    star.setIcon(R.drawable.ic_star_white_24dp);
                    pinned = false;
                } else {
                    star.setIcon(R.drawable.ic_star_border_white_24dp);
                    pinned = true;
                }
                return true;
            }
        });
        return true;
    }
}
