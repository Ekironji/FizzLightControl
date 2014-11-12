package com.gionji.fizzlightcontrol;

import static com.aidilab.ble.sensor.Fizzly.UUID_ALL_DATA;
import static com.aidilab.ble.sensor.Fizzly.UUID_GYR_DATA;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aidilab.ble.interfaces.FizzlyActivity;
import com.aidilab.ble.sensor.FizzlySensor;
import com.aidilab.ble.utils.Point3D;
import com.aidilab.ble.utils.SensorsValues;
import com.gionji.fizzlightcontrol.udp.UDPClientBroadcastAsyncTask;
import com.gionji.fizzlightcontrol.udp.UDPClientBroadcastAsyncTask.IPAddressServerListener;

public class FizzLCMainActivity extends FizzlyActivity {

	FizzLCFragment mFizzLCFragment         = null;
	FizzLCGestureDetector mGestureDetector = null;
	
	String ipAddress = "";
	boolean isServerDetected = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fizz_lcmain);
		
		mFizzLCFragment  = new FizzLCFragment();
		mGestureDetector = new FizzLCGestureDetector(this);
		mGestureDetector.setCyclesTimeout(8);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container,mFizzLCFragment).commit();
		}
		
		WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled()){
			//wifi is enabled
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (mWifi.isConnected()) {
				searchGionjiHome();	
			}
			else{
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		}
		else{
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		}
		
		
		
		setGestureDetector(mGestureDetector);
		setSensorPeriod(100);
		enableSensors(FizzlySensor.ACC_MAG_BUTT_BATT, FizzlySensor.GYROSCOPE);	
		
		
	}
	
	
	public void setConnectionStatus(String address){
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.fizz_lcmain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onGestureDetected(int gestureId) {	
		if(gestureId == FizzLCGestureDetector.HIT_GESTURE){
			mFizzLCFragment.onGestureDetected(gestureId);
		}
	}

	
	@Override
	public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {
		Point3D v = null;
		SensorsValues sv = null;
		
		if (uuidStr.equals(UUID_GYR_DATA.toString())) {
	  		v = FizzlySensor.GYROSCOPE.convert(rawValue);
	  		mFizzLCFragment.gyroscopeChanged(v);
	  	}		
		if (uuidStr.equals(UUID_ALL_DATA.toString())) {
	  		sv = FizzlySensor.ACC_MAG_BUTT_BATT.unpack(rawValue);
	  		this.detectSequence(sv);
	  	}
	}
	
	
	private void searchGionjiHome(){
		UDPClientBroadcastAsyncTask task = new UDPClientBroadcastAsyncTask(this);
		task.setIPAddressServerListener(new IPAddressServerListener() {
			@Override
			public void IPAddressServerFounded(String response) {
				Toast.makeText(getApplicationContext(),
						"GionjiHome " /*+ getId(response) */ + " found! ip: " + getIpAddress(response), Toast.LENGTH_SHORT)
						.show();
			
				ipAddress = getIpAddress(response);
				mFizzLCFragment.setIpAddressTextView(ipAddress);
			}

			@Override
			public void IPAddressServerFailed() {
				Toast.makeText(getApplicationContext(),
						"Abajour not found :-(", Toast.LENGTH_SHORT).show();
			}
		});
		task.setProgressDialogMessage("Wait until abajour is found...");
		task.execute();
		
	}
	
	
	static public String getIpAddress(String msg){
		return msg.split("@")[0];
	}
	
	static public String getId(String msg){
		//return msg.split("#")[1].split("@")[0];
		return "Fizzly";
	}
	
}
