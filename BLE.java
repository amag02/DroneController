package com.example.amag0.dronecontroller2;

import android.bluetooth.*;
import android.bluetooth.le.*;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Alex Magyari on 11/19/2019.
 * BlueTooth Low Energy Handler For Android.
 * Most learning for this tutorial was provided by
 * @martijn.van.welie on the Medium Blog
 */

public class BLE {
    String name = "test";
    BluetoothAdapter adapter;
    BluetoothLeScanner scanner;
    List<ScanFilter> filters;
    ScanSettings scanSettings = new ScanSettings.Builder()
                                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                                    .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                                    .setReportDelay(5000)
                                    .build();

    public String getName(){
        return name;
    }
    public boolean blueToothExists(){ return adapter != null; }
    public boolean blueToothIsEnabled(){ return adapter.isEnabled(); }
    public boolean blueToothLEExists(){ return scanner != null; }

    public BLE(){
        BLEInit();
        startScanner();
    }

    private void BLEInit(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        scanner = adapter.getBluetoothLeScanner();
        filters = null;
    }

    private void startScanner(){
        if (scanner != null) {
            scanner.startScan(filters, scanSettings, scanCallback);
            Log.d(TAG, "scan started");
        }  else {
            Log.e(TAG, "could not get scanner object");
        }
    }

    //Scans are done in batches
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            // Not used. Batch Scan Used Instead
            // BluetoothDevice device = result.getDevice();
            // Log.d(TAG, "Device Found: " + device.getName());
            // ...do whatever you want with this found device
            Log.d("TAG", "Single Scan");
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d("TAG", "BATCHING: " + results.size());
            int nullCounter = 0;
            BluetoothDevice device;
            for (ScanResult result : results){
                device = result.getDevice();
                if (device.getName() == null){
                    nullCounter ++;
                }
                else {
                    Log.d("TAG", "Device Found: " + device.getName());
                }
            }
            Log.d("TAG", "Found " + nullCounter + " devices with no name.");
        }

        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
            Log.d("Tag", "Scan Failed: " + errorCode);

        }
    };
}
