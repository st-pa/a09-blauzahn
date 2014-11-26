package com.example.a09_blauzahn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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


public final class MainActivity
extends ActionBarActivity
implements OnClickListener {

	/** Kennzeichnung von Log-Meldungen. */
	private static final String TAG = "Blauzahn";
	/** ist der "sichtbare" name dieses blauzahns für andere geräte. */
	private static final String BT_NAME = "Hallihallo Welt!";
	/** soll als resultat der blauzahn-einschalte-aktivität zurückkommen. */
	private static final int REQUEST_ENABLE_BT = 42;
	/** Zeitstempelformat. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("hh:MM:ss,SS ",new Locale("DE"));

	private StringBuffer log = new StringBuffer();
	private BroadcastReceiver br;
	private BluetoothAdapter ba;
	private TextView tvLabel,tvLog;
	private Button btConnect,btDisconnect,btRefresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ba = BluetoothAdapter.getDefaultAdapter();
		ba.setName(BT_NAME);

		tvLabel = (TextView) findViewById(R.id.tvLabel);
		tvLog = (TextView) findViewById(R.id.tvLog);
		btConnect = (Button) findViewById(R.id.btConnect);
		btDisconnect = (Button) findViewById(R.id.btDisconnect);
		btRefresh = (Button) findViewById(R.id.btRefresh);
		
		btConnect.setOnClickListener(this);
		btDisconnect.setOnClickListener(this);
		btRefresh.setOnClickListener(this);

		showStatus();
		enable(true);
	}

	private void showStatus() {
//		log("status update");
		tvLabel.setText(Helper.getDescription(ba));
		tvLabel.refreshDrawableState();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			toast("Bluetooth active.");
			if (ba.isEnabled()) {
				doDiscoverBT();
			}
			showStatus();
		}
	}

	/** entdecke Blauzahn-Geräte. */
	private void doDiscoverBT() {
		toast("start discovery");
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				log(action);
				if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
					showStatus();
				} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String msg = String.format(
						"found device: %s [%s]",
						device.getName(),
						device.getAddress()
					);
					toast(msg);
					Log.d(
						TAG,
						msg
					);
					if (!ba.getBondedDevices().contains(device)) {
						doPairDevice(device);
					}
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
		ba.startDiscovery();
	}

	@Override
	public void onDestroy() {
		clickedBtDisconnect();
		super.onDestroy();
	}

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
		}
	}

	private void clickedBtDisconnect() {
		showStatus();
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
		showStatus();
		if (br != null) {
			toast("unregister Bluetooth receiver");
			unregisterReceiver(br);
			br = null;
		}
		showStatus();
		enable(true);
	}

	private void enable(boolean enable) {
		btDisconnect.setEnabled(!enable);
		btConnect.setEnabled(enable);
	}

	private void clickedBtConnect() {
		StringBuffer s = new StringBuffer();
		if (ba != null) {
			toast("bluetooth found.");
			Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// @see #onActivityResult()
			startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT);
		} else {
			toast("no bluetooth found.");
		}
		showStatus();
		enable(false);
	}

	private void toast(String text) {
		log(text);
		Toast.makeText(
			getApplication(),
			text,
			Toast.LENGTH_SHORT
		).show();
	}

	private String now() {
		return SDF.format(new Date());
	}

	private void log(String text) {
		log.insert(
			0,
			new StringBuffer()
			.append(now())
			.append(text)
			.append("\n")
		);
		tvLog.setText(log.toString());
	}
}
