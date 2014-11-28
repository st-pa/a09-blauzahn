package com.example.a09_blauzahn;

import java.util.Date;
import java.util.Locale;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a09_blauzahn.model.Session;
import com.example.a09_blauzahn.model.Sighting;

/**
 * @author stpa
 */
public final class ActivityMain
extends ActionBarActivity
implements OnClickListener {

	////////////////////////////////////////////
	// local constants
	////////////////////////////////////////////

	/** tag for LogCat-messages. */
	private static final String TAG            = "Blauzahn";
	/** the name of this bluetooth-device that is "visible" to others. */
	private static final String BT_NAME        = "GT-Ixxxx";
	/** used as result code of the enable-bluetooth-request-action, unique value in this app. */
	private static final int REQUEST_ENABLE_BT = 42;
	/** predefined {@link Locale} for use in {@link String#format(Locale,String,Object...)}. */
	private static final Locale LOCALE         = new Locale("DE");
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
	private Button btShowNetInfo;
	private Button btResetDb;
	private Button btShowSightings;
	private Button btShowDevices;

	////////////////////////////////////////////
	// methods and functions
	////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app = (AppBlauzahn) getApplication();
		app.init(this);
		if (app.ba != null) {
			app.ba.setName(BT_NAME);
		}

		tvLabel   = (TextView) findViewById(R.id.tvLabel);
		tvLog      = (TextView) findViewById(R.id.tvLog);
		btConnect   = (Button) findViewById(R.id.btConnect);
		btDisconnect = (Button) findViewById(R.id.btDisconnect);
		btRefresh    = (Button) findViewById(R.id.btRefresh);
		btResetDb     = (Button) findViewById(R.id.btResetDb);
		btShowNetInfo  = (Button) findViewById(R.id.btShowNetInfo);
		btShowSightings = (Button) findViewById(R.id.btShowSightings);
		btShowDevices   = (Button) findViewById(R.id.btShowDevices);
		
		btConnect.setOnClickListener(this);
		btDisconnect.setOnClickListener(this);
		btRefresh.setOnClickListener(this);
		btShowNetInfo.setOnClickListener(this);
		btResetDb.setOnClickListener(this);
		btResetDb.setEnabled(ENABLE_RESET);
		btShowSightings.setOnClickListener(this);
		btShowDevices.setOnClickListener(this);

		showStatus();
		if (app.isLogEmpty()) {
			log(
				"there were " + app.db.getMaxSessionId() +
				" sessions with " + app.db.getMaxSightingId() +
				" sightings so far"
			);
		} else {
			tvLog.setText(app.getLog());
		}
		enable(true);
	}

	/** update the verbal bluetooth-status display. */
	private void showStatus() {
//		log("status update");
		tvLabel.setText(AppBlauzahn.getDescription(app.ba));
		tvLabel.refreshDrawableState();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			// die blauzahn-anforderung wurde erfüllt
			scan();
			showStatus();
		}
	}

	/** discover bluetooth devices. */
	private void scan() {
		if (app.ba.isEnabled()) {
			toast("start discovery");
			// receiver nicht mehrfach registrieren!
			if (app.br == null) {
				// konstruiere empfänger für signale
				app.br = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						log(action);
						if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
							log("discovery started");
							if (app.session != null) {
								log("error: double discovery session");
							}
							Date now = new Date();
							app.session = new Session(-1,now,now);
							app.session.setId(app.db.insertSession(app.session));
						} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
							log("discovery finished");
							if (app.session != null) {
								Date now = new Date();
								app.session.setStop(now);
								app.db.updateSession(app.session);
								app.session = null;
							} else log("error: missing discovery session");
							btConnect.setEnabled(true);
						} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
							showStatus();
						} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
							Date now = new Date();
							BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
							short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
							String msg = String.format(
								LOCALE,
								"found: %s [%s] %ddb",
								device.getAddress(),
								device.getName(),
								rssi
							);
							Sighting s = new Sighting(
								-1,
								app.session.getId(),
								now,
								device.getName(),
								device.getAddress(),
								rssi
							);
							s.setId(app.db.insertSighting(s));
							toast(msg);
							Log.d(
								TAG,
								msg
							);
		//					if (!ba.getBondedDevices().contains(device)) {
		//						doPairDevice(device);
		//					}
						}
						showStatus();
					}
				};
				registerReceiver(app.br,new IntentFilter(BluetoothDevice.ACTION_FOUND));
				registerReceiver(app.br,new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
				registerReceiver(app.br,new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
				registerReceiver(app.br,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
				registerReceiver(app.br,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
				registerReceiver(app.br,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
				registerReceiver(app.br,new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
			}
			// starte scan
			app.ba.startDiscovery();
		} else {
			toast("error:expected active adapter");
		}
	}

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
		} else if (v == btShowNetInfo) {
			clickedBtShowNetInfo();
		} else if (v == btShowSightings) {
			clickedBtShowSightings();
		} else if (v == btShowDevices) {
			clickedBtShowDevices();
		}
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

	/** react to a click on {@link #btShowNetInfo}. */
	private void clickedBtShowNetInfo() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] infos = cm.getAllNetworkInfo();
		StringBuffer s = new StringBuffer("network information:\n");
		int i = 0;
		for (NetworkInfo info : infos) {
			i++;
			s.append(
				String.format(
					"\n==> network #%d(%d) <==\n",
					i,infos.length
				)
			).append(AppBlauzahn.getDescription(info));
		}
		log(s);
	}

	/** react to a click on {@link #btRefresh}. */
	private void clickedBtRefresh() {
		showStatus();
	}

	/** react to a click on {@link #btResetDb}. */
	private void clickedBtResetDb() {
		app.db.reset();
	}

	/** react to a click on {@link #btDisconnect}. */
	private void clickedBtDisconnect() {
		if (app.ba != null) {
			toast("disconnect Bluetooth adapter");
			if (app.ba.isDiscovering()) {
				toast("cancel Bluetooth discovery");
				app.ba.cancelDiscovery();
			}
			if (app.ba.isEnabled()) {
				toast("disable Bluetooth");
				app.ba.disable();
			}
		}
		if (app.br != null) {
			toast("unregister Bluetooth receiver");
			unregisterReceiver(app.br);
			app.br = null;
		}
		showStatus();
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
				scan();
			} else {
				Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// @see #onActivityResult()
				startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT);
			}
		} else {
			toast("no bluetooth found.");
		}
		showStatus();
		enable(false);
	}

	/**
	 * show a short toast message.
	 * @param text {@link String}
	 */
	private void toast(String text) {
		log(text);
		Toast.makeText(
			getApplication(),
			text,
			Toast.LENGTH_SHORT
		).show();
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
