package com.example.a09_blauzahn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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


public final class MainActivity
extends ActionBarActivity
implements OnClickListener {

	/** Kennzeichnung von Log-Meldungen. */
	private static final String TAG = "Blauzahn";
	/** ist der "sichtbare" name dieses blauzahns für andere geräte. */
	private static final String BT_NAME = "Hallihallo Welt!";
	/** soll als resultat der blauzahn-einschalte-aktivität zurückkommen. */
	private static final int REQUEST_ENABLE_BT = 42;
	/** Locale. */
	private static final Locale LOCALE = new Locale("DE");

	private AppBlauzahn app;
	private Session session;

	private BroadcastReceiver br;
	private BluetoothAdapter ba;
	private TextView tvLabel,tvLog;
	private Button btConnect,btDisconnect,btRefresh;
	private Button btShowDevices,btResetDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app = (AppBlauzahn) getApplication();
		app.init(this);

		ba = BluetoothAdapter.getDefaultAdapter();
		if (ba != null) {
			ba.setName(BT_NAME);
		}

		tvLabel = (TextView) findViewById(R.id.tvLabel);
		tvLog = (TextView) findViewById(R.id.tvLog);
		btConnect = (Button) findViewById(R.id.btConnect);
		btDisconnect = (Button) findViewById(R.id.btDisconnect);
		btRefresh = (Button) findViewById(R.id.btRefresh);
		btResetDb = (Button) findViewById(R.id.btResetDb);
		btShowDevices = (Button) findViewById(R.id.btShowDevices);
		
		btConnect.setOnClickListener(this);
		btDisconnect.setOnClickListener(this);
		btRefresh.setOnClickListener(this);
		btResetDb.setOnClickListener(this);
		btResetDb.setEnabled(false);
		btShowDevices.setOnClickListener(this);

		showStatus();
		log(
			"there were " + app.db.getMaxSessionId() +
			" sessions with " + app.db.getMaxSightingId() +
			" sightings so far"
		);
		enable(true);
	}

	/** aktualisiert die verbale bluetooth-status-anzeige. */
	private void showStatus() {
//		log("status update");
		tvLabel.setText(AppBlauzahn.getDescription(ba));
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

	/** entdecke Blauzahn-Geräte. */
	private void scan() {
		if (ba.isEnabled()) {
			toast("start discovery");
			// receiver nicht mehrfach registrieren!
			if (br == null) {
				// konstruiere empfänger für signale
				br = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						log(action);
						if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
							log("discovery started");
							if (session != null) {
								log("error: double discovery session");
							}
							Date now = new Date();
							session = new Session(-1,now,now);
							session.setId(app.db.insertSession(session));
						} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
							log("discovery finished");
							if (session != null) {
								Date now = new Date();
								session.setStop(now);
								app.db.updateSession(session);
								session = null;
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
								"found: %s [%s] %d db",
								device.getName(),
								device.getAddress(),
								rssi
							);
							Sighting s = new Sighting(
								-1,
								session.getId(),
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
				registerReceiver(br,new IntentFilter(BluetoothDevice.ACTION_FOUND));
				registerReceiver(br,new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
				registerReceiver(br,new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
				registerReceiver(br,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
				registerReceiver(br,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
				registerReceiver(br,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
				registerReceiver(br,new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
			}
			// starte scan
			ba.startDiscovery();
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
			showStatus();
		} else if (v == btResetDb) {
			app.db.reset();
		}
	}

	/** der trenn-knopf wurde gedrückt. */
	private void clickedBtDisconnect() {
		if (ba != null) {
			toast("disconnect Bluetooth adapter");
			if (ba.isDiscovering()) {
				toast("cancel Bluetooth discovery");
				ba.cancelDiscovery();
			}
			if (ba.isEnabled()) {
				toast("disable Bluetooth");
				ba.disable();
			}
		}
		if (br != null) {
			toast("unregister Bluetooth receiver");
			unregisterReceiver(br);
			br = null;
		}
		showStatus();
		enable(true);
	}

	/** macht scan- & trenn-buttons (un-)benutzbar. */
	private void enable(boolean enable) {
		btDisconnect.setEnabled(!enable);
		btConnect.setEnabled(enable);
	}

	/** der scan-/verbinden-button wurde gedrückt. */
	private void clickedBtConnect() {
		if (ba != null) {
			if (ba.isEnabled()) {
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

	/** zeigt einen kurzen toast an. */
	private void toast(String text) {
		log(text);
		Toast.makeText(
			getApplication(),
			text,
			Toast.LENGTH_SHORT
		).show();
	}

	/** fügt einen gezeitstempelten text ins log ein. */
	private void log(String text) {
		app.log(text);
		tvLog.setText(app.getLog());
	}
}
