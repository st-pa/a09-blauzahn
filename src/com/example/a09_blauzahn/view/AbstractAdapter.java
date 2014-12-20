package com.example.a09_blauzahn.view;

import java.util.List;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

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

	/** R.id of the layout for this adapter. */
	protected int layout;

	/** needed to retrieve resources. */
	protected Context context;

	/** Constructor. */
	public AbstractAdapter(
		Context context,
		int layout,
		List<T> list
	) {
		super(context,layout,list);
		AbstractAdapter.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = layout;
		this.context = context;
	}

	/**
	 * gives back one color for odd and another for even positions.
	 * @param position {@link Integer} position of an item in a list.
	 * @return {@link Integer} pointing to  resource id.
	 */
	protected int getColor(int position) {
		return (position % 2 == 0) ?
		context.getResources().getColor(R.color.background_light) :
		context.getResources().getColor(R.color.background_dark);
	}
}
