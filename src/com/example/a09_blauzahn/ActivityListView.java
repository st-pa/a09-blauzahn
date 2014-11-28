package com.example.a09_blauzahn;

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

import com.example.a09_blauzahn.model.AdapterDevice;
import com.example.a09_blauzahn.model.AdapterSighting;

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
	private static final int LIMIT = 512;

	private AppBlauzahn app;
	private ListView listView;
	private Button btCloseListView;

	private ArrayAdapter<?> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		app = (AppBlauzahn) getApplication();

		btCloseListView = (Button) findViewById(R.id.btCloseList);
		btCloseListView.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.listView);

		// retrieve the type of list to be displayed
		Bundle extras = getIntent().getExtras();
		int listType = 0;
		if (extras != null) {
			listType = extras.getInt(AppBlauzahn.EXTRA_LIST_TYPE);
		}
		if (listType == AppBlauzahn.LIST_TYPE_SIGHTINGS) {
			adapter = new AdapterSighting(
				this,
				app.db.getListSightings(LIMIT)
			);
		} else if (listType == AppBlauzahn.LIST_TYPE_DEVICES) {
			adapter = new AdapterDevice(
				this,
				this.getParent(),
				app.db.getListDevices(LIMIT)
			);
		}
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
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		if (v == btCloseListView) {
			finish();
		}
	}
}
