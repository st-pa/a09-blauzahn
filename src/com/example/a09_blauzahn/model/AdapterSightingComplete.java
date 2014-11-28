package com.example.a09_blauzahn.model;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a09_blauzahn.R;

/**
 * for displaying {@link Sighting}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterSightingComplete
extends ArrayAdapter<Sighting> {

	/** inner convenience class for speeding up list display. */
	static class ViewHolder {
		TextView id,sessionId,time,name,address,rssi;
	}

	/** for convenience, store the {@link LayoutInflater}. */
	private static LayoutInflater inflater;

	/** a {@link List} of the {@link Sighting} instances to be displayed. */
	private List<Sighting> list;

	/** Constructor. */
	public AdapterSightingComplete(
		Context context,
		List<Sighting> list
	) {
		super(context,R.layout.list_sighting_complete,R.id.lv1sightingComplete);
		inflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// initialize the view holder
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_sighting_complete, null);
			holder = new ViewHolder();
			holder.id        = (TextView) convertView.findViewById(R.id.tvList1id);
			holder.sessionId = (TextView) convertView.findViewById(R.id.tvList1sessionId);
			holder.time      = (TextView) convertView.findViewById(R.id.tvList1time);
			holder.name      = (TextView) convertView.findViewById(R.id.tvList1name);
			holder.address   = (TextView) convertView.findViewById(R.id.tvList1address);
			holder.rssi      = (TextView) convertView.findViewById(R.id.tvList1rssi);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// now set the view holder's values
		Sighting s = list.get(position);
		holder.id.setText(Long.toString(s.getId()));
		holder.sessionId.setText(Long.toString(s.getSessionId()));
		holder.time.setText(Long.toString(s.getTime().getTime()));
		holder.address.setText(s.getAddress());
		holder.name.setText(s.getName());
		holder.id.setText(Long.toString(s.getRssi()));
		// and give back the modified view
		return convertView;
	}

}
