package com.example.a09_blauzahn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a09_blauzahn.util.DBHelper;

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
	/** used as result code of the enable-wifi-request-action, unique value in this app. */
	private static final int REQUEST_ENABLE_WIFI = 43;
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

	private TextView tvLog;
	private Button btConnect;
	private Button btDisconnect;
	private Button btRefresh;
	private Button btResetDb;
	private Button btBtSightings;
	private Button btBtDevices;
	private Button btBtSessions;
	private Button btWifiSightings;
	private Button btWifiDevices;
	private Button btWifiSessions;
	private Button btExport;
	private Button btExit;
	private Button btCalendar;
	private CheckBox cbWifi;
	private CheckBox cbWifiAuto;
	private CheckBox cbBt;
	private CheckBox cbBtAuto;
	private MenuItem miEnableResetDb;
	private MenuItem miWifi;
	private MenuItem miAuto;
	private MenuItem miBt;
	private MenuItem miBtAuto;
	private Dialog dialog;

	////////////////////////////////////////////
	// methods and functions
	////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvLog      = (TextView) findViewById(R.id.tvLog);
		btConnect   = (Button) findViewById(R.id.btConnect);
		btDisconnect = (Button) findViewById(R.id.btDisconnect);
		btRefresh     = (Button) findViewById(R.id.btRefresh);
		btResetDb     = (Button) findViewById(R.id.btResetDb);
		btBtSightings = (Button) findViewById(R.id.btShowBtSightings);
		btBtDevices   = (Button) findViewById(R.id.btShowBtDevices);
		btBtSessions   = (Button) findViewById(R.id.btShowBtSessions);
		btWifiSightings = (Button) findViewById(R.id.btShowWifiSightings);
		btWifiSessions = (Button) findViewById(R.id.btShowWifiSessions);
		btWifiDevices = (Button) findViewById(R.id.btShowWifiDevices);
		btExport     = (Button) findViewById(R.id.btExport);
		btExit      = (Button) findViewById(R.id.btExit);
		btCalendar = (Button) findViewById(R.id.btCalendar);

		app = (AppBlauzahn) getApplication();
		app.init(this,tvLog,btConnect);
		if (app.ba != null) {
			app.ba.setName(BT_NAME);
		}

		btConnect      .setOnClickListener(this);
		btDisconnect   .setOnClickListener(this);
		btRefresh      .setOnClickListener(this);
		btResetDb      .setOnClickListener(this);
		btResetDb.setEnabled(ENABLE_RESET);
		btBtSightings  .setOnClickListener(this);
		btBtDevices    .setOnClickListener(this);
		btBtSessions   .setOnClickListener(this);
		btWifiSightings.setOnClickListener(this);
		btWifiDevices  .setOnClickListener(this);
		btWifiSessions .setOnClickListener(this);
		btExport       .setOnClickListener(this);
		btExit         .setOnClickListener(this);
		btCalendar     .setOnClickListener(this);

		// reference checkboxes
		cbWifiAuto = (CheckBox) findViewById(R.id.cbWifiAuto);
		cbWifi    = (CheckBox) findViewById(R.id.cbWifi);
		cbBtAuto = (CheckBox) findViewById(R.id.cbBtAuto);
		cbBt    = (CheckBox) findViewById(R.id.cbBt);
		// check the checkboxes according to user settings
		cbWifiAuto.setChecked(this.app.settings.isBtAuto());
		cbWifi.setChecked(this.app.settings.isWifiOn());
		cbBtAuto.setChecked(this.app.settings.isBtAuto());
		cbBt.setChecked(app.settings.isBtOn());
		// register click listeners AFTER checking to avoid mayhem
		cbWifiAuto.setOnClickListener(this);
		cbWifi.setOnClickListener(this);
		cbBtAuto.setOnClickListener(this);
		cbBt.setOnClickListener(this);

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
			log("scanning for bluetooth");
		} else if (requestCode == REQUEST_ENABLE_WIFI) {
			// the request to enable wifi was answered
			if (app.wm.isWifiEnabled()) {
				app.scanWifi();
			} else {
				// in this case the user declined to allow wifi
				btConnect.setEnabled(true);
				app.scanWifi();
			}
			log("scanning for wifi");
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

		// reference menu items when they are created
		miEnableResetDb = menu.findItem(R.id.menu_check_reset_db);
		miWifi = menu.findItem(R.id.menu_check_wifi);
		miAuto = menu.findItem(R.id.menu_check_wifi_auto);
		miBt = menu.findItem(R.id.menu_check_bt);
		miBtAuto = menu.findItem(R.id.menu_check_bt_auto);

		// show proper settings in the menu
		miAuto.setChecked(app.settings.isBtAuto());
		miWifi.setChecked(app.settings.isWifiOn());
		miBt.setChecked(app.settings.isBtOn());
		miBtAuto.setChecked(app.settings.isBtAuto());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (item == miAuto) {
			clickedCbAuto();
			return true;
		} else if (item == miWifi) {
			clickedCbWifi();
			return true;
		} else if (item == miEnableResetDb) {
			clickedCbEnableReset();
			return true;
		} else if (item == miBt) {
			clickedCbBt();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** react to click on bluetooth menu item. */
	private void clickedCbBt() {
		app.settings.setBtOn(!app.settings.isBtOn());
		miBt.setChecked(app.settings.isBtOn());
		this.app.updateSettings();
	}

	/** react to click on bluetooth menu item. */
	private void clickedCbBtAuto() {
		app.settings.setBtAuto(!app.settings.isBtAuto());
		miBtAuto.setChecked(app.settings.isBtAuto());
		this.app.updateSettings();
	}

	/** react to click on menu item for enabling database reset. */
	private void clickedCbEnableReset() {
		miEnableResetDb.setChecked(!miEnableResetDb.isChecked());
		btResetDb.setEnabled(miEnableResetDb.isChecked());
	}

	@Override
	public void onClick(View v) {
//		toast(String.format("button %s clicked.",((Button) v).getText()));
//		app.log("clicked on " + ((TextView) v).getText());
		if (v == btConnect) {
			clickedBtConnect();
		} else if (v == btDisconnect) {
			clickedBtDisconnect();
		} else if (v == btRefresh) {
			clickedBtRefresh();
		} else if (v == btResetDb) {
			clickedBtResetDb();
		} else if (v == btBtSightings) {
			clickedBtShowSightings();
		} else if (v == btBtDevices) {
			clickedBtShowDevices();
		} else if (v == btBtSessions) {
			clickedBtShowSessions();
		} else if (v == cbWifiAuto) {
			clickedCbAuto();
		} else if (v == cbWifi) {
			clickedCbWifi();
		} else if (v == cbBt) {
			clickedCbBt();
		} else if (v == cbBtAuto) {
			clickedCbBtAuto();
		} else if (v == btExport) {
			clickedBtExport();
		} else if (v == btExit) {
			clickedBtExit();
		} else if (v == btCalendar) {
			clickedBtCalendar();
		} else if (v == btWifiDevices) {
			clickedBtWifiDevices();
		} else if (v == btWifiSessions) {
			clickedBtWifiSessions();
		} else if (v == btWifiSightings) {
			clickedBtWifiSightings();
		}
	}

	private void clickedBtWifiSightings() {
		Intent intent = new Intent(
			ActivityMain.this,
			ActivityListView.class
		);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_WIFISIGHTINGS);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_LABEL,getString(R.string.labelListWifiSightings));
		startActivity(intent);
	}

	private void clickedBtWifiSessions() {
		Intent intent = new Intent(
			ActivityMain.this,
			ActivityListView.class
		);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_WIFISESSIONS);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_LABEL,getString(R.string.labelListWifiSessions));
		startActivity(intent);
	}

	private void clickedBtWifiDevices() {
		Intent intent = new Intent(
			ActivityMain.this,
			ActivityListView.class
		);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_WIFIDEVICES);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_LABEL,getString(R.string.labelListWifiDevices));
		startActivity(intent);
	}

	/**
	 * react to click on {@link #btCalendar} by
	 * allowing the user to change the system time
	 * and at the same time to update all time
	 * related database columns.
	 */
	private void clickedBtCalendar() {
		Intent intent = new Intent(this,ActivityCalendar.class);
		startActivity(intent);
	}

	/**
	 * react to click on {@link #btExit} by ending this activity
	 * (and thus, hopefully, the whole app) after asking for
	 * user confirmation.
	 */
	private void clickedBtExit() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(R.string.dialogExitMessage);
		adb.setTitle(R.string.dialogExitTitle);
		adb.setNegativeButton(R.string.dialogExitNo,null);
		adb.setPositiveButton(
			R.string.dialogExitYes,
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}
		);
		adb.create().show();
	}

	/**
	 * react to click on {@link #btExport} by exporting
	 * the entire database to external storage with
	 * user confirmation dialog.
	 */
	private void clickedBtExport() {
		// ask for user permission to export entire database to external storage
		dialog = new Dialog(this);
		dialog.setCanceledOnTouchOutside(true);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_export);
		final Button btExportYes = (Button) dialog.findViewById(R.id.btExportYes);
		final Button btExportNo = (Button) dialog.findViewById(R.id.btExportNo);
		final EditText etExportTarget = (EditText) dialog.findViewById(R.id.etExportTarget);
		etExportTarget.setText(DBHelper.DB_NAME);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.log("clicked on " + ((Button) v).getText());
				if (v == btExportYes) {
					app.dbExport(
						etExportTarget.getText().toString()
					);
				}
				dialog.dismiss();
				dialog = null;
			}
		};
		btExportNo.setOnClickListener(listener);
		btExportYes.setOnClickListener(listener);
		dialog.show();
	}

	/** react to click on {@link #cbWifi} by updating settings. */
	private void clickedCbWifi() {
		app.settings.setWifiOn(!app.settings.isWifiOn());
		cbWifi.setChecked(app.settings.isWifiOn());
		miWifi.setChecked(app.settings.isWifiOn());
		this.app.updateSettings();
	}

	/** react to click on {@link #cbWifiAuto} by updating settings. */
	private void clickedCbAuto() {
		app.settings.setBtAuto(!app.settings.isBtAuto());
		cbWifiAuto.setChecked(app.settings.isBtAuto());
		miAuto.setChecked(app.settings.isBtAuto());
		this.app.updateSettings();
	}

	private void clickedBtShowSessions() {
		Intent intent = new Intent(
			ActivityMain.this,
			ActivityListView.class
		);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_BTSESSIONS);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_LABEL,getString(R.string.labelListBTSessions));
		startActivity(intent);
	}

	/** react to click on {@link #btBtSightings}. */
	private void clickedBtShowDevices() {
		Intent intent = new Intent(
			ActivityMain.this,
			ActivityListView.class
		);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_BTDEVICES);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_LABEL,getString(R.string.labelListBTDevices));
		startActivity(intent);
	}

	/** react to click on {@link #btBtSightings}. */
	private void clickedBtShowSightings() {
		Intent intent = new Intent(
			ActivityMain.this,
			ActivityListView.class
		);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_BTSIGHTINGS);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_LABEL,getString(R.string.labelListBTSightings));
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

	/**
	 * react to a click on {@link #btResetDb} by
	 * deleting and recreating all tables and
	 * then writing current settings back to db.
	 */
	private void clickedBtResetDb() {
		app.db.reset();
		app.updateSettings();
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
		log("connect");
		if (app.settings.isBtOn() && app.ba != null) {
			if (app.ba.isEnabled()) {
				app.scan();
			} else {
				Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// @see #onActivityResult()
				startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT);
			}
		} else {
			log("no bluetooth found.");
		}
		if (app.settings.isWifiOn() && app.wm != null) {
			app.scanWifi();
		} else {
			log("no wifi found.");
		}
		enable(false);
	}

	/**
	 * add a timestamped message to the app's log.
	 * @param text {@link String}
	 */
	protected void log(String text) {
		app.log(text);
		tvLog.setText(app.getLog());
	}

	/**
	 * convenience method to call {@link #log(String)}.
	 * @param s {@link StringBuffer} should not be <code>null</code>.
	 */
	protected void log(StringBuffer s) {
		log(s.toString());
	}
}
