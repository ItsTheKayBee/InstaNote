package com.example.instanote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PinnedNotes extends AppCompatActivity implements PinnedAdapter.CardClickListener {

    public static final String TITLE = "TITLE";
    public static final String TEXT = "TEXT";
    public static final String PINNED = "PINNED";
    PinnedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned_notes);

        RecyclerView recyclerView = findViewById(R.id.pinned_notes);
        int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(), 180);
        recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        String[] title = {"Note1 lorem ipsum is is lorem ipsum is lorem ipsum lorem is lorem ipsum is is lorem ipsum is lorem ipsum lorem is", "Note2", "Note3", "Note4", "Note1", "Note2", "Note3", "Note4", "Note1", "Note2", "Note3", "Note4"};
        String[] text = {"lorem lorem ipsum is is lorem ipsum is lorem ipsum lorem is lorem ipsum is is lorem ipsum is lorem ipsum lorem is lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum edefe efwewfwe", "Noterrevre1 edefe lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum", "Noterrevre1 edefe efwewfwe", "Noterrevre1 edefe efwewfwe", "Noterrevre1 edefe efwewfwe", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum edefe efwewfwe", "Note4", "Note1", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum"};
        adapter = new PinnedAdapter(this, title, text);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCardClick(View view, int position) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(TITLE, adapter.getCardTitle(position));
        intent.putExtra(TEXT, adapter.getCardText(position));
        intent.putExtra(PINNED, true);
        startActivity(intent);
    }
}
