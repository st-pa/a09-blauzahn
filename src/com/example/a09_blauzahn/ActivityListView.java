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
import com.example.a09_blauzahn.view.AdapterDevice;
import com.example.a09_blauzahn.view.AdapterSession;
import com.example.a09_blauzahn.view.AdapterSighting;

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
			BTDevice device = (BTDevice) extras.getSerializable(AppBlauzahn.EXTRA_LIST_BTDEVICE);
			// retrieve an optional session from bundled intent extras to filter the resulting list
			BTSession session = (BTSession) extras.getSerializable(AppBlauzahn.EXTRA_LIST_BTSESSION);
			adapter = new AdapterSighting(
				this,
				R.layout.list_btsighting,
				app.db.getListBTSightings(LIMIT,device,session)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_BTDEVICES) {
			// retrieve an optional session from bundled intent extras to filter the resulting list
			BTSession session = (BTSession) extras.getSerializable(AppBlauzahn.EXTRA_LIST_BTSESSION);
			adapter = new AdapterDevice(
				this,
				R.layout.list_btdevice,
				app.db.getListBTDevices(LIMIT,session)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_BTSESSIONS) {
			// retrieve an optional device from bundled intent extras to filter the resulting list
			BTDevice device = (BTDevice) extras.getSerializable(AppBlauzahn.EXTRA_LIST_BTDEVICE);
			adapter = new AdapterSession(
				this,
				R.layout.list_btsession,
				app.db.getListBTSessions(LIMIT,device)
			);
		}
		// set the list adapter
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sighting_list, menu);
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (listType == AppBlauzahn.LIST_TYPE_BTDEVICES) {
			itemClickBTDevice((BTDevice) adapter.getItem(position));
		} else if (listType == AppBlauzahn.LIST_TYPE_BTSESSIONS) {
			itemClickBTSession((BTSession) adapter.getItem(position));
		} else if (listType == AppBlauzahn.LIST_TYPE_BTSIGHTINGS) {
			itemClickBTSighting((BTSighting) adapter.getItem(position));
		}
	}

	/** react to a click on a listed bluetooth sighting. */
	private void itemClickBTSighting(BTSighting item) {
		// TODO Auto-generated method stub
	}

	/**
	 * react to a click on a listed bluetooth session by displaying a dialog with some option buttons.
	 * @see <a href="http://stackoverflow.com/questions/2644134/android-how-to-create-a-dialog-without-a-title"
	 * >http://stackoverflow.com/questions/2644134/android-how-to-create-a-dialog-without-a-title</a>
	 */
	private void itemClickBTSession(final BTSession item) {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_listitem_btsession);
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
					Intent intent = new Intent(ActivityListView.this,ActivityListView.class);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_BTDEVICES);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_BTSESSION,item);
					intent.putExtra(
						AppBlauzahn.EXTRA_LIST_LABEL,
						String.format(
							getString(R.string.labelListBTDevicesSession),
							item.getId()
						)
					);
					startActivity(intent);
				} else if (v == btBTSessionSightings) {
					// list the sightings during this session (devices could be sighted twice)
					Intent intent = new Intent(ActivityListView.this,ActivityListView.class);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_BTSIGHTINGS);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_BTSESSION,item);
					intent.putExtra(
						AppBlauzahn.EXTRA_LIST_LABEL,
						String.format(
							getString(R.string.labelListBTSightingsSession),
							item.getId()
						)
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
		btBTSessionSightings.setOnClickListener(listener);
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
		dialog.setContentView(R.layout.dialog_listitem_btdevice);
		// display some information about the selected device
		TextView tvBTDeviceLabel = (TextView) dialog.findViewById(R.id.tvBTDeviceLabel);
		tvBTDeviceLabel.setText(AppBlauzahn.getDescription(item));
		// define click-behaviour for dialog buttons
		final Button btBTDeviceExit = (Button) dialog.findViewById(R.id.btBTDeviceExit);
		final Button btBTDevicePairing = (Button) dialog.findViewById(R.id.btBTDevicePairing);
		final Button btBTDeviceSessions = (Button) dialog.findViewById(R.id.btBTDeviceSessions);
		final Button btBTDeviceSightings = (Button) dialog.findViewById(R.id.btBTDeviceSightings);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.log("clicked on " + ((Button) v).getText());
				if (v == btBTDevicePairing) {
					// TODO try pairing with the bluetooth device
					app.toast("no bluetooth pairing as of yet, sorry.");
				} else if (v == btBTDeviceSessions) {
					// list the sessions containing this device
					Intent intent = new Intent(ActivityListView.this,ActivityListView.class);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_BTSESSIONS);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_BTDEVICE,item);
					intent.putExtra(
						AppBlauzahn.EXTRA_LIST_LABEL,
						String.format(
							getString(R.string.labelListBTSessionsDevice),
							item.getAddress()
						)
					);
					startActivity(intent);
				} else if (v == btBTDeviceSightings) {
					// show only sightings containing this device
					Intent intent = new Intent(ActivityListView.this,ActivityListView.class);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_BTSIGHTINGS);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_BTDEVICE,item);
					intent.putExtra(
						AppBlauzahn.EXTRA_LIST_LABEL,
						String.format(
							getString(R.string.labelListBTSightingsDevice),
							item.getAddress()
						)
					);
					startActivity(intent);
				}
				// close the dialog no matter which button was clicked
				dialog.dismiss();
				dialog = null;
			}
		};
		btBTDeviceExit.setOnClickListener(listener);
		btBTDevicePairing.setOnClickListener(listener);
		btBTDeviceSessions.setOnClickListener(listener);
		btBTDeviceSightings.setOnClickListener(listener);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		app.log("clicked on " + ((Button) v).getText());
		if (v == btCloseListView) {
			finish();
		}
	}
}
