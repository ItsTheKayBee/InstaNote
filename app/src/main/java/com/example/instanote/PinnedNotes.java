package com.example.instanote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class PinnedNotes extends AppCompatActivity implements PinnedAdapter.CardClickListener, PinnedAdapter.CardLongClickListener {

    public static final String TITLE = "TITLE";
    public static final String TEXT = "TEXT";
    public static final String PINNED = "PINNED";
    public static final String LINK = "LINK";
    public static final String ID = "ID";
    RecyclerView recyclerView;
    private PinnedAdapter adapter;
    private ActionMode mode = null;
    private ArrayList<Integer> selectedArray;
    private ArrayList<String> titlesList;
    private ArrayList<String> textList;
    private ArrayList<String> linkList;
    private ArrayList<Integer> idList;
    ArrayList<String> newTitles;
    ArrayList<String> newContent;
    ArrayList<String> newLinks;
    ArrayList<Integer> newIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned_notes);

        titlesList = new ArrayList<>();
        textList = new ArrayList<>();
        linkList = new ArrayList<>();
        idList = new ArrayList<>();
        selectedArray = new ArrayList<>();

        DbManager dbManager = new DbManager(this);
        try {
            dbManager.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        titlesList = dbManager.getAllPinnedNotes("title");
        textList = dbManager.getAllPinnedNotes("content");
        linkList = dbManager.getAllPinnedNotes("link");
        idList = dbManager.getAllIds();
        dbManager.close();
        setRecyclerView(titlesList, linkList, textList);
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

    private void setRecyclerView(ArrayList<String> title, ArrayList<String> link, ArrayList<String> text) {
        recyclerView = findViewById(R.id.pinned_notes);
        int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(), 180);
        recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        adapter = new PinnedAdapter(this, title, link, text, idList);
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
                        DbManager dbManager = new DbManager(PinnedNotes.this);
                        try {
                            dbManager.open();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Collections.sort(selectedArray);
                        int count = 0;
                        int size = selectedArray.size();
                        for (int j : selectedArray) {
                            try {
                                dbManager.deleteSelectedNote(idList.get(j - count));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            textList.remove(j - count);
                            titlesList.remove(j - count);
                            linkList.remove(j - count);
                            idList.remove(j - count);
                            adapter.notifyItemRemoved(j - count);
                            count++;
                        }
                        dbManager.close();
                        selectedArray.clear();
                        mode.finish();

                        View view = findViewById(R.id.unsave);
                        String snackText;
                        if (size == 1) {
                            snackText = size + " note deleted";
                        } else {
                            snackText = size + " notes deleted";
                        }
                        Snackbar.make(view, snackText, Snackbar.LENGTH_SHORT)
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
            intent.putExtra(LINK, adapter.getCardLink(position));
            intent.putExtra(ID, adapter.getCardId(position));
            intent.putExtra(PINNED, true);
            startActivity(intent);
        } else {
            selectCard(view, position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        DbManager dbManager = new DbManager(this);
        try {
            dbManager.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        newTitles = dbManager.getAllPinnedNotes("title");
        newContent = dbManager.getAllPinnedNotes("content");
        newLinks = dbManager.getAllPinnedNotes("link");
        newIds = dbManager.getAllIds();
        titlesList.clear();
        textList.clear();
        linkList.clear();
        idList.clear();
        titlesList.addAll(newTitles);
        textList.addAll(newContent);
        linkList.addAll(newLinks);
        idList.addAll(newIds);
        dbManager.close();
        adapter.notifyDataSetChanged();
        ResultActivity resultActivity = new ResultActivity();
        int change = resultActivity.getChange();
        if (change != 0) {
            View view = getWindow().getDecorView().findViewById(android.R.id.content);
            String snackbarText = "";
            if (change == 1)
                snackbarText = "Note unpinned and removed";
            else if (change == 2)
                snackbarText = "Note updated";
            Snackbar.make(view, snackbarText, Snackbar.LENGTH_SHORT).show();
            resultActivity.setChange(0);
        }
    }
}