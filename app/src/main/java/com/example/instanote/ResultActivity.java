package com.example.instanote;

import android.bluetooth.BluetoothAdapter;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.sql.SQLException;

public class ResultActivity extends AppCompatActivity {

    BluetoothShare bluetoothShare;
    boolean pinned = false;
    private ConstraintLayout resLayout;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        String title = getIntent().getStringExtra(PinnedNotes.TITLE);
        String text = getIntent().getStringExtra(PinnedNotes.TEXT);
        final String link = getIntent().getStringExtra(PinnedNotes.LINK);
        id = getIntent().getIntExtra(PinnedNotes.ID, 0);
        EditText resText = findViewById(R.id.res_text);
        EditText resTitle = findViewById(R.id.res_title);
        TextView resLink = findViewById(R.id.res_link);
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
        MenuItem star = menu.findItem(R.id.save);
        if (pinned) {
            star.setIcon(R.drawable.ic_star_white_24dp);
        } else {
            star.setIcon(R.drawable.ic_star_border_white_24dp);
        }
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (!pinned) {
                    item.setIcon(R.drawable.ic_star_white_24dp);
                    pinned = true;
                } else {
                    item.setIcon(R.drawable.ic_star_border_white_24dp);
                    pinned = false;
                }
                return true;
            case R.id.share:
                shareThroughBluetooth();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!pinned && id != 0) {
            DbManager dbManager = new DbManager(this);
            try {
                dbManager.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                dbManager.deleteSelectedNote(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void shareThroughBluetooth() {
        //make BluetoothShare class obj here
        bluetoothShare = new BluetoothShare(this, this);
        BluetoothAdapter bluetoothAdapter = bluetoothShare.getBluetoothAdapter();
        bluetoothShare.startBluetooth(bluetoothAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == BluetoothShare.getRequestEnableBt() && resultCode == RESULT_OK) {
                bluetoothShare.getDetails();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}