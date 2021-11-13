package com.prospect.faisalrestorant.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class DeviceList extends Activity
{
    protected static final String TAG = "TAG";
    private BluetoothAdapter mBluetoothAdapter;
    @Override
    protected void onCreate(Bundle mSavedInstanceState)
    {
        super.onCreate(mSavedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setResult(Activity.RESULT_CANCELED);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String mNoDevices = "InnerPrinter"+ "\n"+ "00:11:22:33:44:55";
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.enable();
        String mDeviceInfo =mNoDevices;
       String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
        Log.v(TAG, "Device_Address " + mDeviceAddress);
        Bundle mBundle = new Bundle();
        mBundle.putString("DeviceAddress", mDeviceAddress);
        Intent mBackIntent = new Intent();
        mBackIntent.putExtras(mBundle);
        setResult(Activity.RESULT_OK, mBackIntent);
        finish();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mBluetoothAdapter != null)
        {
            mBluetoothAdapter.cancelDiscovery();
        }
    }
}