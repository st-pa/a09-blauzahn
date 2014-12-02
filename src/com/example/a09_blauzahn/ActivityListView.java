package com.example.a09_blauzahn;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
			adapter = new AdapterSighting(
				this,
				R.layout.list_btsighting,
				app.db.getListBTSightings(LIMIT)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_BTDEVICES) {
			adapter = new AdapterDevice(
				this,
				R.layout.list_btdevice,
				app.db.getListBTDevices(LIMIT)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_BTSESSIONS) {
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

	private void itemClickBTSighting(BTSighting item) {
		// TODO Auto-generated method stub
	}

	private void itemClickBTSession(BTSession item) {
		// TODO Auto-generated method stub
	}

	/** react to a click on a listed bluetooth device. */
	private void itemClickBTDevice(final BTDevice item) {
		final Dialog dialog = new Dialog(this);
		final Button btBTDeviceExit = (Button) dialog.findViewById(R.id.btBTDeviceExit);
		final Button btBTDevicePairing = (Button) dialog.findViewById(R.id.btBTDevicePairing);
		final Button btBTDeviceSessions = (Button) dialog.findViewById(R.id.btBTDeviceSessions);
		final Button btBTDeviceSightings = (Button) dialog.findViewById(R.id.btBTDeviceSightings);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == btBTDevicePairing) {
					// TODO try pairing with the bluetooth device
				} else if (v == btBTDeviceSessions) {
					// TODO list the sessions containing this device
				} else if (v == btBTDeviceSightings) {
					// show only sightings containing this device
					Intent intent = new Intent(ActivityListView.this,ActivityListView.class);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_TYPE,AppBlauzahn.LIST_TYPE_BTSIGHTINGS);
					intent.putExtra(AppBlauzahn.EXTRA_LIST_LABEL,getString(R.string.labelListBTSightings));
					intent.putExtra(AppBlauzahn.EXTRA_LIST_BTDEVICE,item);
					startActivity(intent);
				}
				// close the dialog no matter which button was clicked
				dialog.dismiss();
			}
		};
		btBTDeviceExit.setOnClickListener(listener);
		btBTDevicePairing.setOnClickListener(listener);
		btBTDeviceSessions.setOnClickListener(listener);
		btBTDeviceSightings.setOnClickListener(listener);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.dialog_listitem_btdevice);
		dialog.show();
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		if (v == btCloseListView) {
			finish();
		}
	}
}
