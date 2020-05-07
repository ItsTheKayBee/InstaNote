package com.example.instanote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Set;

class BluetoothShare {

    private static final int REQUEST_ENABLE_BT = 400;
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Found, add to a device list
            }
        }
    };
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private Activity activity;

    BluetoothShare(Activity activity, Context context) {
        this.context = context;
        this.activity = activity;
    }

    static int getRequestEnableBt() {
        return REQUEST_ENABLE_BT;
    }

    private Set<BluetoothDevice> getPairedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    BluetoothAdapter getBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter;
    }

    void startBluetooth(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Toast.makeText(context, "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Bluetooth is not supported", Toast.LENGTH_SHORT).show();
        }
    }

    void getDetails() {
        Set<BluetoothDevice> pairedDevices = getPairedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
            }
        }
    }

    void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        activity.startActivity(discoverableIntent);
    }

    void startSearching() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }

    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue;
    }
}