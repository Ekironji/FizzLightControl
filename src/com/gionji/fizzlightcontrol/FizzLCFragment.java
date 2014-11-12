package com.gionji.fizzlightcontrol;

import java.nio.ByteBuffer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aidilab.ble.interfaces.FizzlyActivity;
import com.aidilab.ble.interfaces.FizzlyFragment;
import com.aidilab.ble.utils.Numbers;
import com.aidilab.ble.utils.Point3D;
import com.aidilab.ble.utils.SensorsValues;
import com.gionji.fizzlightcontrol.udp.UDPSendCommandThread;

public class FizzLCFragment extends FizzlyFragment {

	private FizzlyActivity mActivity;
	
	private double angle = 0;
	private double prevAngle = 0;
	double t = 0;
	private double prevT = 0;
	private boolean isFirstSample = true;
	
	private int count = 0;
	private int COUNT_LIMIT = 4;
	
	TextView mTextViewAngle = null;
	RelativeLayout mRelativeLayout = null;
	View view = null;
	
	String ipAddress = null;
	
	boolean isOn = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    mInstance = this;
	    mActivity = (FizzlyActivity) getActivity();
    
	    // The last two arguments ensure LayoutParams are inflated properly.	    
	    view = inflater.inflate(R.layout.fragment_fizz_lcmain, container, false);
		
	    mTextViewAngle = (TextView) view.findViewById(R.id.degree);
	    mRelativeLayout= (RelativeLayout) view.findViewById(R.id.backFreg);
	    
		// Notify activity that UI has been inflated
	    mActivity.onViewInflated(view);
		return view;
	}
	
	@Override
	public void onClick(View arg0) {}

	@Override
	public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {}

	@Override
	public void onCharacteristicChanged(String uuidStr, SensorsValues sv) {}

	@Override
	public void onGestureDetected(int gestureId) {
		isOn = !isOn;
	}

	int color = 0;
	
	public void gyroscopeChanged(Point3D v){
		Log.i("","gyr: " + v.x + "   y: " + v.y + "   z: " + v.z);
		t = System.currentTimeMillis();
		
		if(!isFirstSample && Math.abs(v.z) > 0.15){
			angle = angle + ((v.z / 100) * (t - prevT));
		}
		mTextViewAngle.setText("" +  String.format("%.01f", 
				Math.abs(angle % 360)));
		
		color = Numbers.degreeAngleToColor(Math.abs(angle % 360));
		
		if((count % COUNT_LIMIT) == 0 && angle != prevAngle){
			if(isOn){
				mActivity.playColor(0, color);
				mRelativeLayout.setBackgroundColor(color);
				if(ipAddress != null)
					new UDPSendCommandThread(ipAddress, ByteBuffer.allocate(4).putInt(color).array()).start();
			}
			else {
				mActivity.playColorBlink(500, 1 ,Color.BLACK);
				mRelativeLayout.setBackgroundColor(Color.BLACK);
				if(ipAddress != null)
					new UDPSendCommandThread(ipAddress, ByteBuffer.allocate(4).putInt(Color.BLACK).array()).start();
			}	
		}
		
		count++;
		
		prevT = t;
		prevAngle = angle;
		isFirstSample = false;
	}

	public void setIpAddressTextView(String ipAddress) {
		this.ipAddress = ipAddress;
		((TextView) view.findViewById(R.id.ipTextview)).setText(ipAddress);
		
	}

}
