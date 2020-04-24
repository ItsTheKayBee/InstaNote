package com.example.instanote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class PinnedNotes extends AppCompatActivity implements PinnedAdapter.CardClickListener, PinnedAdapter.CardLongClickListener {

    public static final String TITLE = "TITLE";
    public static final String TEXT = "TEXT";
    public static final String PINNED = "PINNED";
    RecyclerView recyclerView;
    private PinnedAdapter adapter;
    private ActionMode mode = null;
    private ArrayList<Integer> selectedArray;
    private ArrayList<String> titlesList;
    private ArrayList<String> textList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned_notes);

        String[] title = {"Note1 lorem ipsum is is lorem ipsum is lorem ipsum lorem is lorem ipsum is is lorem ipsum is lorem ipsum lorem is", "Note2", "Note3", "Note4", "Note1", "Note2", "Note3", "Note4", "Note1", "Note2", "Note3", "Note4"};
        titlesList = new ArrayList<>(Arrays.asList(title));
        String[] text = {"lorem lorem ipsum is is lorem ipsum is lorem ipsum lorem is lorem ipsum is is lorem ipsum is lorem ipsum lorem is lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum edefe efwewfwe", "Noterrevre1 edefe lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum", "Noterrevre1 edefe efwewfwe", "Noterrevre1 edefe efwewfwe", "Noterrevre1 edefe efwewfwe", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum edefe efwewfwe", "Note4", "Note1", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum", "lorem ipsum is is lorem ipsum is lorem ipsum lorem is ipsum lorem ipsum is lorem ipsum lorem ipsum lorem is ipsum lorem ipsum"};
        textList = new ArrayList<>(Arrays.asList(text));

        setRecyclerView(titlesList, textList);
        selectedArray = new ArrayList<>();
    }

    private ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.notes_action_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.unsave:
                    unsave();
                    return true;
                case R.id.palette:
                    showToast();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            selectedArray.clear();
            adapter.setSelectedList(selectedArray);
            adapter.notifyDataSetChanged();
            mode = null;
        }
    };

    @Override
    public void onCardLongClick(View view, int position) {
        if (mode != null) {
            selectCard(view, position);
        } else {
            mode = startActionMode(actionCallback);
            selectCard(view, position);
        }
    }

    private void setRecyclerView(ArrayList<String> title, ArrayList<String> text) {
        recyclerView = findViewById(R.id.pinned_notes);
        int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(), 180);
        recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        adapter = new PinnedAdapter(this, title, text);
        adapter.setClickListener(this);
        adapter.setLongClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void selectCard(View view, int position) {
        if (selectedArray.contains(position)) {
            selectedArray.remove(Integer.valueOf(position));
            if (selectedArray.size() == 0) {
                mode.finish();
                mode = null;
            }
        } else {
            selectedArray.add(position);
        }
        if (selectedArray.size() > 0) {
            if (mode != null) {
                if (selectedArray.size() == 1)
                    mode.setTitle(selectedArray.size() + " note selected");
                else
                    mode.setTitle(selectedArray.size() + " notes selected");
            }
        }
        adapter.setSelectedList(selectedArray);
        adapter.notifyItemChanged(position);
    }

    private void showToast() {
        Toast.makeText(this, "color option selected", Toast.LENGTH_SHORT).show();
    }

    private void unsave() {

        new MaterialAlertDialogBuilder(PinnedNotes.this, R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("Are you sure?")
                .setMessage("The selected notes will be deleted")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Collections.sort(selectedArray);
                        int count = 0;
                        int size = selectedArray.size();
                        for (int j : selectedArray) {
                            textList.remove(j - count);
                            titlesList.remove(j - count);
                            adapter.notifyItemRemoved(j - count);
                            count++;
                        }
                        selectedArray.clear();
                        mode.finish();
                        View view = findViewById(R.id.unsave);
                        String snackText = "";
                        if (size == 1) {
                            snackText = size + " notes deleted";
                        } else {
                            snackText = size + " notes deleted";
                        }
                        Snackbar.make(view, snackText, BaseTransientBottomBar.LENGTH_SHORT)
                                .show();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onCardClick(View view, int position) {
        if (mode == null) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra(TITLE, adapter.getCardTitle(position));
            intent.putExtra(TEXT, adapter.getCardText(position));
            intent.putExtra(PINNED, true);
            startActivity(intent);
        } else {
            selectCard(view, position);
        }
    }

}
