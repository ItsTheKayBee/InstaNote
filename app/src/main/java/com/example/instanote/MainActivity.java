package com.example.instanote;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import static com.example.instanote.SearchAppWidget.FOCUS;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView searchView;
    private ObjectAnimator objectAnimator;
    private RelativeLayout darkSearchLayout;
    private float yOrg;
    private Menu menu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchView = findViewById(R.id.search_bar);
        darkSearchLayout = findViewById(R.id.dim_layout);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        yOrg = displayMetrics.heightPixels / displayMetrics.density;

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchView.animate().translationY(-yOrg).setDuration(300).start();
                    darkSearchLayout.setVisibility(View.VISIBLE);
                } else {
                    searchView.animate().translationY(0).setDuration(300).start();
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
