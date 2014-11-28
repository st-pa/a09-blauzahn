package com.example.a09_blauzahn;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.a09_blauzahn.model.AdapterSightingComplete;

public class ActivitySightingList
extends ActionBarActivity
implements OnItemClickListener, OnClickListener {

	/** maximum number of list entries to be displayed. */
	private static final int LIMIT = 512;

	private AppBlauzahn app;
	private ListView lv1sightingComplete;
	private Button btCloseListSightingComplete;

	private AdapterSightingComplete adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sighting_list);

		app = (AppBlauzahn) getApplication();

		btCloseListSightingComplete = (Button) findViewById(R.id.btCloseListSightingComplete);
		btCloseListSightingComplete.setOnClickListener(this);

		lv1sightingComplete = (ListView) findViewById(R.id.lv1sightingComplete);
		adapter = new AdapterSightingComplete(
			this,
			app.db.getListSightingComplete(LIMIT)
		);
		lv1sightingComplete.setAdapter(adapter);
		lv1sightingComplete.setOnItemClickListener(this);
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
		if (v == btCloseListSightingComplete) {
			finish();
		}
	}
}
