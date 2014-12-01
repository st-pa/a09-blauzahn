package com.example.a09_blauzahn.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.example.a09_blauzahn.model.Sighting;

/**
 * abstract ancestor of listview adapters,
 * in order to avoid code duplication.
 * this can later be useful for extending functionality
 * like adding/removing entries while the list is
 * being displayed.
 * @author stpa
 * @param <T>
 */
public abstract class AbstractAdapter<T>
extends ArrayAdapter<T> {

	/** for convenience, store the {@link LayoutInflater}. */
	protected static LayoutInflater inflater;

	/** a {@link List} of the {@link Sighting} instances to be displayed. */
	protected List<T> list;

	/** R.id of the layout for this adapter. */
	protected int layout;

	/** Constructor. */
	public AbstractAdapter(
		Context context,
		int layout,
		List<T> list
	) {
		super(context,layout,list);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}


}
