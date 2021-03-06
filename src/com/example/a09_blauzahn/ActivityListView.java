package com.example.a09_blauzahn;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a09_blauzahn.model.BTDevice;
import com.example.a09_blauzahn.model.BTSession;
import com.example.a09_blauzahn.model.BTSighting;
import com.example.a09_blauzahn.model.WifiDevice;
import com.example.a09_blauzahn.model.WifiSession;
import com.example.a09_blauzahn.model.WifiSighting;
import com.example.a09_blauzahn.view.AdapterBTDevice;
import com.example.a09_blauzahn.view.AdapterBTSession;
import com.example.a09_blauzahn.view.AdapterBTSighting;
import com.example.a09_blauzahn.view.AdapterWifiDevice;
import com.example.a09_blauzahn.view.AdapterWifiSession;
import com.example.a09_blauzahn.view.AdapterWifiSighting;

/**
 * activity for displaying simple lists. the
 * type of information to be displayed is
 * determined by a bundle parameter received
 * from the calling activity.
 * @author stpa
 */
public class ActivityListView
extends ActionBarActivity
implements OnItemClickListener, OnClickListener {

	/** maximum number of list entries to be displayed. */
	private static final int LIMIT = 256;

	private AppBlauzahn app;
	private TextView tvListLabel;
	private ListView listView;
	private Button btCloseListView;

	private ArrayAdapter<?> adapter;

	private int listType = 0;
	private String listLabel = "";
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		app = (AppBlauzahn) getApplication();

		tvListLabel = (TextView) findViewById(R.id.tvListLabel);
		btCloseListView = (Button) findViewById(R.id.btCloseList);
		btCloseListView.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.listView);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// retrieve the type of list to be displayed from extras bundle
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			listType  = extras.getInt(AppBlauzahn.EXTRA_LIST_TYPE);
			listLabel = extras.getString(AppBlauzahn.EXTRA_LIST_LABEL);
			tvListLabel.setText(listLabel);
		}
		// decide on which list adapter to use
		if (listType == AppBlauzahn.LIST_TYPE_BTSIGHTINGS) {
			// retrieve an optional device from bundled intent extras to filter the resulting list
			BTDevice device = (BTDevice) extras.getSerializable(AppBlauzahn.EXTRA_BTDEVICE);
			// retrieve an optional session from bundled intent extras to filter the resulting list
			BTSession session = (BTSession) extras.getSerializable(AppBlauzahn.EXTRA_BTSESSION);
			// retrieve an optional session from bundled intent extras to filter the resulting list
			BTSighting sighting = (BTSighting) extras.getSerializable(AppBlauzahn.EXTRA_BTSIGHTING);
			adapter = new AdapterBTSighting(
				this,
				R.layout.list_btsighting,
				app.db.getListBTSightings(LIMIT,device,session,sighting)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_BTDEVICES) {
			// retrieve an optional session from bundled intent extras to filter the resulting list
			BTSession session = (BTSession) extras.getSerializable(AppBlauzahn.EXTRA_BTSESSION);
			adapter = new AdapterBTDevice(
				this,
				R.layout.list_btdevice,
				app.db.getListBTDevices(LIMIT,session)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_BTSESSIONS) {
			// retrieve an optional device from bundled intent extras to filter the resulting list
			BTDevice device = (BTDevice) extras.getSerializable(AppBlauzahn.EXTRA_BTDEVICE);
			// retrieve an optional sighting from bundled intent extras to filter the resulting list
			BTSighting sighting = (BTSighting) extras.getSerializable(AppBlauzahn.EXTRA_BTSIGHTING);
			adapter = new AdapterBTSession(
				this,
				R.layout.list_btsession,
				app.db.getListBTSessions(LIMIT,device,sighting)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_WIFISIGHTINGS) {
			WifiDevice device = (WifiDevice) extras.getSerializable(AppBlauzahn.EXTRA_WIFIDEVICE);
			WifiSession session = (WifiSession) extras.getSerializable(AppBlauzahn.EXTRA_WIFISESSION);
			WifiSighting sighting = (WifiSighting) extras.getSerializable(AppBlauzahn.EXTRA_WIFISIGHTING);
			adapter = new AdapterWifiSighting(
				this,
				R.layout.list_wifisighting,
				app.db.getListWifiSightings(LIMIT,device,session,sighting)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_WIFISESSIONS) {
			WifiSighting sighting = (WifiSighting) extras.getSerializable(AppBlauzahn.EXTRA_WIFISIGHTING);
			adapter = new AdapterWifiSession(
				this,
				R.layout.list_wifisession,
				app.db.getListWifiSessions(LIMIT,sighting)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_WIFIDEVICES) {
			WifiSession session = (WifiSession) extras.getSerializable(AppBlauzahn.EXTRA_WIFISESSION);
			adapter = new AdapterWifiDevice(
				this,
				R.layout.list_wifidevice,
				app.db.getListWifiDevices(LIMIT,session)
			);
		}
		// set the list adapter
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_sighting_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (listType == AppBlauzahn.LIST_TYPE_BTDEVICES) {
			itemClickBTDevice((BTDevice) adapter.getItem(position));
		} else if (listType == AppBlauzahn.LIST_TYPE_BTSESSIONS) {
			itemClickBTSession((BTSession) adapter.getItem(position));
		} else if (listType == AppBlauzahn.LIST_TYPE_BTSIGHTINGS) {
			itemClickBTSighting((BTSighting) adapter.getItem(position));
		} else if (listType == AppBlauzahn.LIST_TYPE_WIFIDEVICES) {
			itemClickWifiDevice((WifiDevice) adapter.getItem(position));
		} else if (listType == AppBlauzahn.LIST_TYPE_WIFISESSIONS) {
			itemClickWifiSession((WifiSession) adapter.getItem(position));
		} else if (listType == AppBlauzahn.LIST_TYPE_WIFISIGHTINGS) {
			itemClickWifiSighting((WifiSighting) adapter.getItem(position));
		}
	}

	/**
	 * convenience method to generate intents for various list views.
	 * @param listType {@link Integer} see {@link AppBlauzahn#LIST_TYPE_BTDEVICES} and others.
	 * @param label {@link String} label to be displayed over the list
	 * @param args {@link Object} additional objects like {@link WifiSession}, {@link BTDevice}, etc.
	 * @return {@link Intent}
	 */
	private Intent makeIntent(int listType, String label, Object... args) {
		Intent intent = new Intent(ActivityListView.this,ActivityListView.class);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,listType);
		intent.putExtra(AppBlauzahn.EXTRA_LIST_LABEL,label);
		for (Object item : args) {
			if (item instanceof BTDevice) intent.putExtra(AppBlauzahn.EXTRA_BTDEVICE,(BTDevice) item);
			else if (item instanceof BTSession) intent.putExtra(AppBlauzahn.EXTRA_BTSESSION,(BTSession) item);
			else if (item instanceof BTSighting) intent.putExtra(AppBlauzahn.EXTRA_BTSIGHTING,(BTSighting) item);
			else if (item instanceof WifiDevice) intent.putExtra(AppBlauzahn.EXTRA_WIFIDEVICE,(WifiDevice) item);
			else if (item instanceof WifiSession) intent.putExtra(AppBlauzahn.EXTRA_WIFISESSION,(WifiSession) item);
			else if (item instanceof WifiSighting) intent.putExtra(AppBlauzahn.EXTRA_WIFISIGHTING,(WifiSighting) item);
		}
		return intent;
	}

	/** TODO check {@link #itemClickWifiSighting} */
	private void itemClickWifiSighting(final WifiSighting item) {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_listitem_sighting);
		// display some information about the selected sighting
		TextView tvSightingLabel = (TextView) dialog.findViewById(R.id.tvSightingLabel);
		tvSightingLabel.setText(AppBlauzahn.getDescription(item));
		// define click-behaviour for dialog buttons
		final Button btSightingExit = (Button) dialog.findViewById(R.id.btSightingExit);
		final Button btSightingSessions = (Button) dialog.findViewById(R.id.btSightingSessions);
		final Button btSightingSightings = (Button) dialog.findViewById(R.id.btSightingSightings);
		final Button btSightingPairing = (Button) dialog.findViewById(R.id.btSightingPairing);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == btSightingSessions) {
					// list all the sessions containing the sighted device
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_WIFISESSIONS,
						String.format(
							getString(R.string.labelListWifiSessionsDevice),
							item.getBSSID()
						),item
					);
					startActivity(intent);
				} else if (v == btSightingSightings) {
					// list all the sessions containing the sighted device
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_WIFISIGHTINGS,
						String.format(
							getString(R.string.labelListWifiSightingsDevice),
							item.getBSSID()
						),item
					);
					startActivity(intent);
				} else if (v == btSightingPairing) {
					pairingWifi(item.getBSSID());
				}
				// close the dialog no matter which button was clicked
				dialog.dismiss();
				dialog = null;
			}
		};
		btSightingExit.setOnClickListener(listener);
		btSightingSessions.setOnClickListener(listener);
		btSightingSightings.setOnClickListener(listener);
		btSightingPairing.setOnClickListener(listener);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * TODO check {@link #itemClickWifiSession}
	 */
	private void itemClickWifiSession(final WifiSession item) {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_listitem_session);
		// display some information about the selected session
		TextView tvWifiSessionLabel = (TextView) dialog.findViewById(R.id.tvBTSessionLabel);
		tvWifiSessionLabel.setText(AppBlauzahn.getDescription(item));
		// define click-behaviour for dialog buttons
		final Button btSessionExit = (Button) dialog.findViewById(R.id.btBTSessionExit);
		final Button btSessionDevices = (Button) dialog.findViewById(R.id.btBTSessionDevices);
		final Button btSessionSightings = (Button) dialog.findViewById(R.id.btBTSessionSightings);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == btSessionDevices) {
					// list the devices contained in this session
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_WIFIDEVICES,
						String.format(
							getString(R.string.labelListWifiDevicesSession),
							item.getId()
						),item
					);
					startActivity(intent);
				} else if (v == btSessionSightings) {
					// list the sightings during this session (devices could be sighted twice)
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_WIFISIGHTINGS,
						String.format(
							getString(R.string.labelListWifiSightingsSession),
							item.getId()
						),item
					);
					startActivity(intent);
				}
				// close the dialog no matter which button was clicked
				dialog.dismiss();
				dialog = null;
			}
		};
		btSessionExit.setOnClickListener(listener);
		btSessionDevices.setOnClickListener(listener);
		btSessionDevices.setEnabled(item.getWifiSightingsCount() > 0);
		btSessionSightings.setOnClickListener(listener);
		btSessionSightings.setEnabled(item.getWifiSightingsCount() > 0);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void itemClickWifiDevice(final WifiDevice item) {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_listitem_device);
		// display some information about the selected device
		TextView tvDeviceLabel = (TextView) dialog.findViewById(R.id.tvDeviceLabel);
		tvDeviceLabel.setText(AppBlauzahn.getDescription(item));
		// define click-behaviour for dialog buttons
		final Button btDeviceExit = (Button) dialog.findViewById(R.id.btDeviceExit);
		final Button btDevicePairing = (Button) dialog.findViewById(R.id.btSightingPairing);
		final Button btDeviceSessions = (Button) dialog.findViewById(R.id.btDeviceSessions);
		final Button btDeviceSightings = (Button) dialog.findViewById(R.id.btDeviceSightings);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == btDevicePairing) {
					pairingWifi(item.getBSSID());
				} else if (v == btDeviceSessions) {
					// list the sessions containing this device
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_WIFISESSIONS,
						String.format(
							getString(R.string.labelListWifiSessionsDevice),
							item.getBSSID()
						),
						item
					);
					startActivity(intent);
				} else if (v == btDeviceSightings) {
					// show only sightings containing this device
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_WIFISIGHTINGS,
						String.format(
							getString(R.string.labelListWifiSightingsDevice),
							item.getBSSID()
						),item
					);
					startActivity(intent);
				}
				// close the dialog no matter which button was clicked
				dialog.dismiss();
				dialog = null;
			}
		};
		btDeviceExit.setOnClickListener(listener);
		btDevicePairing.setOnClickListener(listener);
		btDeviceSessions.setOnClickListener(listener);
		btDeviceSightings.setOnClickListener(listener);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/** react to a click on a listed bluetooth sighting. */
	private void itemClickBTSighting(final BTSighting item) {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_listitem_sighting);
		// display some information about the selected sighting
		TextView tvBTSightingLabel = (TextView) dialog.findViewById(R.id.tvSightingLabel);
		tvBTSightingLabel.setText(AppBlauzahn.getDescription(item));
		// define click-behaviour for dialog buttons
		final Button btBTSightingExit = (Button) dialog.findViewById(R.id.btSightingExit);
		final Button btBTSightingSessions = (Button) dialog.findViewById(R.id.btSightingSessions);
		final Button btBTSightingSightings = (Button) dialog.findViewById(R.id.btSightingSightings);
		final Button btBTSightingPairing = (Button) dialog.findViewById(R.id.btSightingPairing);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == btBTSightingSessions) {
					// list all the sessions containing the sighted device
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_BTSESSIONS,
						String.format(
							getString(R.string.labelListBTSessionsDevice),
							item.getAddress()
						),item
					);
					startActivity(intent);
				} else if (v == btBTSightingSightings) {
					// list all the sessions containing the sighted device
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_BTSIGHTINGS,
						String.format(
							getString(R.string.labelListBTSightingsDevice),
							item.getAddress()
						),item
					);
					startActivity(intent);
				} else if (v == btBTSightingPairing) {
					pairingBT();
				}
				// close the dialog no matter which button was clicked
				dialog.dismiss();
				dialog = null;
			}
		};
		btBTSightingExit.setOnClickListener(listener);
		btBTSightingSessions.setOnClickListener(listener);
		btBTSightingSightings.setOnClickListener(listener);
		btBTSightingPairing.setOnClickListener(listener);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * react to a click on a listed bluetooth session by displaying a dialog with some option buttons.
	 * @see <a href="http://stackoverflow.com/questions/2644134/android-how-to-create-a-dialog-without-a-title"
	 * >http://stackoverflow.com/questions/2644134/android-how-to-create-a-dialog-without-a-title</a>
	 */
	private void itemClickBTSession(final BTSession item) {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_listitem_session);
		// display some information about the selected session
		TextView tvBTSessionLabel = (TextView) dialog.findViewById(R.id.tvBTSessionLabel);
		tvBTSessionLabel.setText(AppBlauzahn.getDescription(item));
		// define click-behaviour for dialog buttons
		final Button btBTSessionExit = (Button) dialog.findViewById(R.id.btBTSessionExit);
		final Button btBTSessionDevices = (Button) dialog.findViewById(R.id.btBTSessionDevices);
		final Button btBTSessionSightings = (Button) dialog.findViewById(R.id.btBTSessionSightings);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == btBTSessionDevices) {
					// list the devices contained in this session
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_BTDEVICES,
						String.format(
							getString(R.string.labelListBTDevicesSession),
							item.getId()
						),item
					);
					startActivity(intent);
				} else if (v == btBTSessionSightings) {
					// list the sightings during this session (devices could be sighted twice)
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_BTSIGHTINGS,
						String.format(
							getString(R.string.labelListBTSightingsSession),
							item.getId()
						),item
					);
					startActivity(intent);
				}
				// close the dialog no matter which button was clicked
				dialog.dismiss();
				dialog = null;
			}
		};
		btBTSessionExit.setOnClickListener(listener);
		btBTSessionDevices.setOnClickListener(listener);
		btBTSessionDevices.setEnabled(item.getBTSightingsCount() > 0);
		btBTSessionSightings.setOnClickListener(listener);
		btBTSessionSightings.setEnabled(item.getBTSightingsCount() > 0);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * react to a click on a listed bluetooth device by displaying a dialog with some option buttons.
	 * @see <a href="http://stackoverflow.com/questions/2644134/android-how-to-create-a-dialog-without-a-title"
	 * >http://stackoverflow.com/questions/2644134/android-how-to-create-a-dialog-without-a-title</a>
	 */
	private void itemClickBTDevice(final BTDevice item) {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_listitem_device);
		// display some information about the selected device
		TextView tvDeviceLabel = (TextView) dialog.findViewById(R.id.tvDeviceLabel);
		tvDeviceLabel.setText(AppBlauzahn.getDescription(item));
		// define click-behaviour for dialog buttons
		final Button btDeviceExit = (Button) dialog.findViewById(R.id.btDeviceExit);
		final Button btDevicePairing = (Button) dialog.findViewById(R.id.btSightingPairing);
		final Button btDeviceSessions = (Button) dialog.findViewById(R.id.btDeviceSessions);
		final Button btDeviceSightings = (Button) dialog.findViewById(R.id.btDeviceSightings);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == btDevicePairing) {
					pairingBT();
				} else if (v == btDeviceSessions) {
					// list the sessions containing this device
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_BTSESSIONS,
						String.format(
							getString(R.string.labelListBTSessionsDevice),
							item.getAddress()
						),
						item
					);
					startActivity(intent);
				} else if (v == btDeviceSightings) {
					// show only sightings containing this device
					Intent intent = makeIntent(
						AppBlauzahn.LIST_TYPE_BTSIGHTINGS,
						String.format(
							getString(R.string.labelListBTSightingsDevice),
							item.getAddress()
						),item
					);
					startActivity(intent);
				}
				// close the dialog no matter which button was clicked
				dialog.dismiss();
				dialog = null;
			}
		};
		btDeviceExit.setOnClickListener(listener);
		btDevicePairing.setOnClickListener(listener);
		btDeviceSessions.setOnClickListener(listener);
		btDeviceSightings.setOnClickListener(listener);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * TODO try pairing with the bluetooth device
	 */
	private void pairingBT() {
		app.toast("no bluetooth pairing as of yet, sorry.");
	}

	/**
	 * TODO try pairing with the wifi device
	 * @param address {@link String}
	 */
	private void pairingWifi(String address) {
		app.toast("no wifi pairing as of yet, sorry.");
	}

	@Override
	public void onClick(View v) {
		if (v == btCloseListView) {
			finish();
		}
	}
}
