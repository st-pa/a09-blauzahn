package com.example.a09_blauzahn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
	private static final String BT_NAME = "Hallo Welt!";
	/** soll als resultat der blauzahn-einschalte-aktivität zurückkommen. */
	private static final int REQUEST_ENABLE_BT = 42;

	private BroadcastReceiver br;
	private BluetoothAdapter ba;
	private TextView tvLabel;
	private Button btConnect,btDisconnect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvLabel = (TextView) findViewById(R.id.tvLabel);
		btConnect = (Button) findViewById(R.id.btConnect);
		btDisconnect = (Button) findViewById(R.id.btDisconnect);
		
		btConnect.setOnClickListener(this);
		btDisconnect.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			Toast.makeText(this,"Bluetooth active.",Toast.LENGTH_SHORT).show();
			tvLabel.setText(Helper.getDescription(ba));
			if (ba.isEnabled()) {
				doDiscoverBT();
			}
		}
	}

	/** entdecke Blauzahn-Geräte. */
	private void doDiscoverBT() {
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					Log.d(
						TAG,
						String.format(
							"gefunden: %s [%s]",
							device.getName(),
							device.getAddress()
						)
					);
					if (!ba.getBondedDevices().contains(device)) {
						doPairDevice(device);
					}
					Toast.makeText(
						getApplication(),
						Helper.getDescription(ba),
						Toast.LENGTH_SHORT
					).show();
				}
			}
		};
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(br,filter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(br);
		super.onDestroy();
	}

	private void doPairDevice(BluetoothDevice device) {
		try {
			Method m = device.getClass().getMethod("createBond", (Class[]) null);
			m.invoke(device,(Class[]) null);
			Toast.makeText(
				getApplication(),
				String.format(
					"paired with %s [%s]",
					device.getName(),
					device.getAddress()
				),
				Toast.LENGTH_SHORT
			).show();
			
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
		if (v == btConnect) {
			clickedBtConnect();
		} else if (v == btDisconnect) {
			clickedBtDisconnect();
		}
		
	}

	private void clickedBtDisconnect() {
		if (ba != null) {
			ba.cancelDiscovery();
			ba.disable();
		}
	}

	private void clickedBtConnect() {
		StringBuffer s = new StringBuffer();
		ba = BluetoothAdapter.getDefaultAdapter();
		if (ba != null) {
			s.append("Blauzahn gefunden.");
//			ba.setName(BLAUZAHN_NAME);
			
			if (!ba.isEnabled()) {
				Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// @see #onActivityResult()
				startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BT);
			}
		} else {
			s.append("Kein Blauzahn gefunden.");
		}
		tvLabel.setText(s.toString());
	}
}
