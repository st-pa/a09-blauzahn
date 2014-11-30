package com.example.a09_blauzahn;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * @author stpa
 */
public final class ActivityMain
extends ActionBarActivity
implements OnClickListener {

	////////////////////////////////////////////
	// local constants
	////////////////////////////////////////////

	/** the name of this bluetooth-device that is "visible" to others. */
	private static final String BT_NAME        = "GT-Ixxxx";
	/** used as result code of the enable-bluetooth-request-action, unique value in this app. */
	private static final int REQUEST_ENABLE_BT = 42;
	/** whether or not the button {@link #btResetDb} should be clickable. */
	private static final boolean ENABLE_RESET  = false;

	////////////////////////////////////////////
	// local fields
	////////////////////////////////////////////

	/** access to convenience methods for this app. */
	private AppBlauzahn app;

	////////////////////////////////////////////
	// gui-elements
	////////////////////////////////////////////

	private TextView tvLabel;
	private TextView tvLog;
	private Button btConnect;
	private Button btDisconnect;
	private Button btRefresh;
	private Button btResetDb;
	private Button btShowSightings;
	private Button btShowDevices;
	private Button btShowSessions;
	private CheckBox cbWifi;
	private CheckBox cbAuto;

	////////////////////////////////////////////
	// methods and functions
	////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvLabel   = (TextView) findViewById(R.id.tvLabel);
		tvLog      = (TextView) findViewById(R.id.tvLog);
		btConnect   = (Button) findViewById(R.id.btConnect);
		btDisconnect = (Button) findViewById(R.id.btDisconnect);
		btRefresh     = (Button) findViewById(R.id.btRefresh);
		btResetDb      = (Button) findViewById(R.id.btResetDb);
		btShowSightings = (Button) findViewById(R.id.btShowSightings);
		btShowDevices   = (Button) findViewById(R.id.btShowDevices);
		btShowSessions  = (Button) findViewById(R.id.btShowSessions);

		app = (AppBlauzahn) getApplication();
		app.init(this,tvLabel,btConnect);
		if (app.ba != null) {
			app.ba.setName(BT_NAME);
		}

		btConnect      .setOnClickListener(this);
		btDisconnect   .setOnClickListener(this);
		btRefresh      .setOnClickListener(this);
		btResetDb      .setOnClickListener(this);
		btResetDb.setEnabled(ENABLE_RESET);
		btShowSightings.setOnClickListener(this);
		btShowDevices  .setOnClickListener(this);
		btShowSessions .setOnClickListener(this);

		cbAuto = (CheckBox) findViewById(R.id.cbAutoScan);
		cbWifi = (CheckBox) findViewById(R.id.cbWifi);
		cbAuto.setOnClickListener(this);
		cbWifi.setOnClickListener(this);

		app.showStatus();
		if (app.isLogEmpty()) {
			// the very first log entry
			log(
				"there were " + app.db.getMaxSessionId() +
				" sessions with " + app.db.getMaxSightingId() +
				" sightings so far"
			);
		} else {
			// restore existing log, e.g. when screen was flipped
			tvLog.setText(app.getLog());
		}
		enable(true);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			// the request to enable bluetooth was answered
			if (app.ba.isEnabled()) {
				app.scan();
			} else {
				// in this case the user declined to allow bluetooth
				btConnect.setEnabled(true);
			}
			app.showStatus();
		}
	}

//	/** discover bluetooth devices. */
//	private void scan() {
//		if (app.ba.isEnabled()) {
//			toast("start discovery");
//			// there should only be one receiver, so check if it's there already
//			if (app.br == null) {
//				// create a receiver for bluetooth
//				app.br = new BroadcastReceiver() {
//					@Override
//					public void onReceive(Context context, Intent intent) {
//						String action = intent.getAction();
//						log(action);
//						if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//							log("discovery started");
//							if (app.session != null) {
//								log("error: double discovery session");
//							}
//							Date now = new Date();
//							app.session = new Session(-1,now,now,null);
//							app.session.setId(app.db.addSession(app.session));
//						} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//							log("discovery finished");
//							if (app.session != null) {
//								Date now = new Date();
//								app.session.setStop(now);
//								app.db.setSession(app.session);
//								app.session = null;
//							} else log("error: missing discovery session");
//							btConnect.setEnabled(true);
//						} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
//							showStatus();
//						} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//							Date now = new Date();
//							BluetoothDevice device = intent
//							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//							short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
//							String msg = String.format(
//								LOCALE,
//								"found: %s [%s] %ddb",
//								device.getAddress(),
//								device.getName(),
//								rssi
//							);
//							Sighting s = new Sighting(
//								-1,
//								app.session.getId(),
//								now,
//								device.getName(),
//								device.getAddress(),
//								rssi
//							);
//							s.setId(app.db.addSighting(s));
//							toast(msg);
//							Log.d(
//								TAG,
//								msg
//							);
//		//					if (!ba.getBondedDevices().contains(device)) {
//		//						doPairDevice(device);
//		//					}
//						}
//						showStatus();
//					}
//				};
//				registerReceiver(app.br,new IntentFilter(BluetoothDevice.ACTION_FOUND));
////				registerReceiver(app.br,new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
////				registerReceiver(app.br,new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
//				registerReceiver(app.br,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
//				registerReceiver(app.br,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
//				registerReceiver(app.br,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
////				registerReceiver(app.br,new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
//			}
//			// start to scan for devices
//			app.ba.startDiscovery();
//		} else {
//			toast("error: expected active adapter");
//		}
//	}

	@Override
	public void onDestroy() {
		clickedBtDisconnect();
		super.onDestroy();
	}
/*
	private void doPairDevice(BluetoothDevice device) {
		try {
			Method m = device.getClass().getMethod("createBond", (Class[]) null);
			m.invoke(device,(Class[]) null);
			toast(
				String.format(
					"paired with %s [%s]",
					device.getName(),
					device.getAddress()
				)
			);
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
//		toast(String.format("button %s clicked.",((Button) v).getText()));
		if (v == btConnect) {
			clickedBtConnect();
		} else if (v == btDisconnect) {
			clickedBtDisconnect();
		} else if (v == btRefresh) {
			clickedBtRefresh();
		} else if (v == btResetDb) {
			clickedBtResetDb();
		} else if (v == btShowSightings) {
			clickedBtShowSightings();
		} else if (v == btShowDevices) {
			clickedBtShowDevices();
		} else if (v == btShowSessions) {
			clickedBtShowSessions();
		} else if (v == cbAuto) {
			clickedCbAuto();
		} else if (v == cbWifi) {
			clickedCbWifi();
		}
	}

	private void clickedCbWifi() {
		// TODO Auto-generated method stub
	}

	private void clickedCbAuto() {
		// TODO Auto-generated method stub
	}

	private void clickedBtShowSessions() {
		Intent intent = new Intent(
			ActivityMain.this,
			ActivityListView.class
		);
		intent.putExtra(
			AppBlauzahn.EXTRA_LIST_TYPE,
			AppBlauzahn.LIST_TYPE_SESSIONS
		);
		startActivity(intent);
	}

	/** react to click on {@link #btShowSightings}. */
	private void clickedBtShowDevices() {
		Intent intent = new Intent(
			ActivityMain.this,
			ActivityListView.class
		);
		intent.putExtra(
			AppBlauzahn.EXTRA_LIST_TYPE,
			AppBlauzahn.LIST_TYPE_DEVICES
		);
		startActivity(intent);
	}

	/** react to click on {@link #btShowSightings}. */
	private void clickedBtShowSightings() {
		Intent intent = new Intent(
			ActivityMain.this,
			ActivityListView.class
		);
		intent.putExtra(
			AppBlauzahn.EXTRA_LIST_TYPE,
			AppBlauzahn.LIST_TYPE_SIGHTINGS
		);
		startActivity(intent);
	}

//	/** react to a click on {@link #btShowNetInfo}. */
//	private void clickedBtShowNetInfo() {
//		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo[] infos = cm.getAllNetworkInfo();
//		StringBuffer s = new StringBuffer("network information:\n");
//		int i = 0;
//		for (NetworkInfo info : infos) {
//			i++;
//			s.append(
//				String.format(
//					"\n==> network #%d(%d) <==\n",
//					i,infos.length
//				)
//			).append(AppBlauzahn.getDescription(info));
//		}
//		log(s);
//	}

	/** react to a click on {@link #btRefresh}. */
	private void clickedBtRefresh() {
		app.showStatus();
	}

	/** react to a click on {@link #btResetDb}. */
	private void clickedBtResetDb() {
		app.db.reset();
	}

	/** react to a click on {@link #btDisconnect}. */
	private void clickedBtDisconnect() {
		if (app.ba != null) {
			app.toast("disconnect Bluetooth adapter");
			if (app.ba.isDiscovering()) {
				app.toast("cancel Bluetooth discovery");
				app.ba.cancelDiscovery();
			}
			if (app.ba.isEnabled()) {
				app.toast("disable Bluetooth");
				app.ba.disable();
			}
		}
		if (app.br != null) {
			app.toast("unregister Bluetooth receiver");
			unregisterReceiver(app.br);
			app.br = null;
		}
		app.showStatus();
		enable(true);
	}

	/** enable or disable the buttons {@link #btConnect} and {@link #btDisconnect}. */
	private void enable(boolean enable) {
		btDisconnect.setEnabled(!enable);
		btConnect.setEnabled(enable);
	}

	/** react to a click on {@link #btConnect}. */
	private void clickedBtConnect() {
		if (app.ba != null) {
			if (app.ba.isEnabled()) {
				app.scan();
			} else {
				Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// @see #onActivityResult()
				startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT);
			}
		} else {
			app.toast("no bluetooth found.");
		}
		app.showStatus();
		enable(false);
	}

	/**
	 * add a timestamped message to the app's log.
	 * @param text {@link String}
	 */
	private void log(String text) {
		app.log(text);
		tvLog.setText(app.getLog());
	}

	/**
	 * convenience method to call {@link #log(String)}.
	 * @param s {@link StringBuffer} should not be <code>null</code>.
	 */
	private void log(StringBuffer s) {
		log(s.toString());
	}
}
