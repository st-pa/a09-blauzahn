package com.example.a09_blauzahn.model;

import com.example.a09_blauzahn.R;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * for displaying {@link Sighting}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterSightingComplete
extends ArrayAdapter<Sighting> {

	/** Constructor. */
	public AdapterSightingComplete(Context context) {
		super(context,R.layout.list_sighting_complete,R.id.lv1sightingComplete);
	}

	
}
