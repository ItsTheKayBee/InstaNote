package com.example.instanote;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import static com.example.instanote.SearchAppWidget.FOCUS;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView searchView;
    private ObjectAnimator objectAnimator;
    private RelativeLayout darkSearchLayout;
    private Menu menu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchView = findViewById(R.id.search_bar);
        darkSearchLayout = findViewById(R.id.dim_layout);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(searchView, "y", 50f);
                    animation.setDuration(300);
                    animation.start();
                    darkSearchLayout.setVisibility(View.VISIBLE);
                } else {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(searchView, "translationY", 0);
                    animation.setDuration(300);
                    animation.start();
                    searchView.clearFocus();
                    darkSearchLayout.setVisibility(View.GONE);
                }
            }
        });

        boolean focus = getIntent().getBooleanExtra(FOCUS, false);
        if (focus) {
            searchView.requestFocus();
        }

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (!isOpen) {
                    searchView.clearFocus();
                }
            }
        });

        searchView.setOnQueryTextListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean focus = getIntent().getBooleanExtra(FOCUS, false);
        if (focus) {
            searchView.requestFocus();
        }
        ResultActivity resultActivity = new ResultActivity();
        int change = resultActivity.getChange();
        if (change == 3) {
            View view = getWindow().getDecorView().findViewById(android.R.id.content);
            String snackbarText = "Note pinned";
            Snackbar.make(view, snackbarText, Snackbar.LENGTH_SHORT).show();
            resultActivity.setChange(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainpage_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.pin) {
            Intent i = new Intent(MainActivity.this, PinnedNotes.class);
            searchView.clearFocus();
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        searchView.clearFocus();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        SearchResults results = new SearchResults(MainActivity.this, MainActivity.this, menu, s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        SearchResults suggestions = new SearchResults(s);
        return true;
    }
}
