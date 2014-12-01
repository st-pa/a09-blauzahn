package com.example.a09_blauzahn;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
	private Button btExport;
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
		btExport       = (Button) findViewById(R.id.btExport);

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
		btExport       .setOnClickListener(this);

		// reference checkboxes
		cbAuto = (CheckBox) findViewById(R.id.cbAutoScan);
		cbWifi = (CheckBox) findViewById(R.id.cbWifi);
		// check the checkboxes according to user settings
		cbAuto.setChecked(this.app.settings.isBtAuto());
		cbWifi.setChecked(this.app.settings.isWifiOn());
		// register click listeners AFTER checking to avoid mayhem
		cbAuto.setOnClickListener(this);
		cbWifi.setOnClickListener(this);

		app.showStatus();
		if (app.isLogEmpty()) {
			// the very first log entry
			log(
				"there were " + app.db.getMaxBTSessionId() +
				" sessions with " + app.db.getMaxBTSightingId() +
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
		} else if (v == btExport) {
			clickedBtExport();
		}
	}

	/**
	 * react to click on {@link #btExport} by exporting
	 * the entire database to external storage.
	 */
	private void clickedBtExport() {
		app.db.exportDB();
	}

	/** react to click on {@link #cbWifi} by updating settings. */
	private void clickedCbWifi() {
		this.app.settings.setWifiOn(this.cbWifi.isChecked());
		this.app.updateSettings();
		// TODO Auto-generated method stub
	}

	/** react to click on {@link #cbAuto} by updating settings. */
	private void clickedCbAuto() {
		this.app.settings.setBtAuto(this.cbAuto.isChecked());
		this.app.updateSettings();
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
	protected void clickedBtDisconnect() {
		app.disconnect();
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
