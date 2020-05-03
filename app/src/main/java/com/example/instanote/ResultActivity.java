package com.example.instanote;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ResultActivity extends AppCompatActivity {

    private EditText resTitle;
    private EditText resText;
    private TextView resLink;
    boolean pinned = false;
    private ConstraintLayout resLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        String title = getIntent().getStringExtra(PinnedNotes.TITLE);
        String text = getIntent().getStringExtra(PinnedNotes.TEXT);
        final String link = getIntent().getStringExtra(PinnedNotes.LINK);
        resText = findViewById(R.id.res_text);
        resTitle = findViewById(R.id.res_title);
        resLink = findViewById(R.id.res_link);
        resTitle.setText(title);
        resLink.setText(link);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            resText.setText(Html.fromHtml(text));
        }

        resLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        pinned = getIntent().getBooleanExtra(PinnedNotes.PINNED, false);
        inflater.inflate(R.menu.menu, menu);
        final MenuItem star = menu.findItem(R.id.save);
        if (pinned) {
            star.setIcon(R.drawable.ic_star_white_24dp);
        } else {
            star.setIcon(R.drawable.ic_star_border_white_24dp);
        }
        star.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!pinned) {
                    star.setIcon(R.drawable.ic_star_white_24dp);
                    pinned = true;
                } else {
                    star.setIcon(R.drawable.ic_star_border_white_24dp);
                    pinned = false;
                }
                return true;
            }
        });
        return true;
    }
}