package com.gionji.fizzlightcontrol;

import android.content.Intent;
import android.os.Bundle;

import com.aidilab.ble.interfaces.FizzlyDeviceScanActivity;

public class FizzLCScanActivity extends FizzlyDeviceScanActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.onScanViewReady();
	}
		
	@Override
	protected void startDeviceActivity() {
		this.mDeviceIntent = new Intent(this, FizzLCMainActivity.class); 
		mDeviceIntent.putExtra(FizzLCMainActivity.EXTRA_DEVICE, this.mBluetoothDevice); 
		startActivityForResult(mDeviceIntent, FizzlyDeviceScanActivity.REQ_DEVICE_ACT);
	}

}
