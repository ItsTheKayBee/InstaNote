package com.example.instanote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;

public class ResultActivity extends AppCompatActivity {

    BluetoothShare bluetoothShare;
    boolean pinned = false;
    private int id;
    private String ids;
    private static int change;
    TextView resLink;
    EditText resText, resTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setChange(0);
        String title = getIntent().getStringExtra(PinnedNotes.TITLE);
        String text = getIntent().getStringExtra(PinnedNotes.TEXT);
        final String link = getIntent().getStringExtra(PinnedNotes.LINK);
        id = getIntent().getIntExtra(PinnedNotes.ID, 0);
        ids = Integer.toString(id);
        resText = findViewById(R.id.res_text);
        resTitle = findViewById(R.id.res_title);
        resLink = findViewById(R.id.res_link);
        resLink.setText(link);
        resTitle.setText(title);

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

        bluetoothShare = new BluetoothShare(this, this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothShare.myReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        pinned = getIntent().getBooleanExtra(PinnedNotes.PINNED, false);
        inflater.inflate(R.menu.menu, menu);
        MenuItem star = menu.findItem(R.id.save);
        if (pinned) {
            star.setIcon(R.drawable.ic_star_white_24dp);
        } else {
            star.setIcon(R.drawable.ic_star_border_white_24dp);
        }
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
//            case R.id.share:
//                shareThroughBluetooth();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            dbManager.close();
            setChange(1);
        }

        unregisterReceiver(bluetoothShare.myReceiver);

        String content;
        String titles;
        String links;

        if (id != 0 && pinned) {
            DbManager dbManager = new DbManager(this);
            try {
                dbManager.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            content = Html.toHtml(resText.getText());
            titles = resTitle.getText().toString();
            dbManager.updateData(ids, titles, content);
            dbManager.close();
            setChange(2);
        }

        if (id == 0 && pinned) {
            DbManager dbManager = new DbManager(this);
            try {
                dbManager.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            content = Html.toHtml(resText.getText());
            titles = resTitle.getText().toString();
            links = resLink.getText().toString();
            dbManager.insertData(titles, links, content);
            dbManager.close();
            setChange(3);
        }
    }

    private void shareThroughBluetooth() {
        bluetoothShare = new BluetoothShare(this, this);
        BluetoothAdapter bluetoothAdapter = bluetoothShare.getBluetoothAdapter();
        bluetoothShare.startBluetooth(bluetoothAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == BluetoothShare.getRequestEnableBt() && resultCode == RESULT_OK) {
                bluetoothShare.startSearching();
                Toast.makeText(this, "Bluetooth turned on", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth not turned on", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        ResultActivity.change = change;
    }
}