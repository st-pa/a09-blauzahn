package com.example.a09_blauzahn.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

/**
 * receiver for broadcast events related to wifi.
 * @author stpa
 * @see com.ows.OpenWifiStatistics.Services
 */
public class WifiBReceiver extends BroadcastReceiver {

	private WifiManager manager;
	private Handler handler = null;

	public WifiBReceiver(WifiManager manager, Handler handler) {
		super();
		this.manager = manager;
		this.handler = handler;
	}

	@Override
	public void onReceive(Context c, Intent intent) {
		Message message = new Message();
		message.what = 0;
		message.obj = manager.getScanResults();
		handler.sendMessage(message);
	}
}