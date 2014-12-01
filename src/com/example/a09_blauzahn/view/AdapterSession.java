package com.example.a09_blauzahn.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a09_blauzahn.AppBlauzahn;
import com.example.a09_blauzahn.R;
import com.example.a09_blauzahn.model.Session;

/**
 * for displaying {@link Session}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterSession
extends AbstractAdapter<Session> {

	/** inner convenience class for speeding up list display. */
	static class ViewHolder {
		TextView id;
		TextView label;
		TextView names;
	}

	/** Constructor. */
	public AdapterSession(
		Context context,
		int layout,
		List<Session> list
	) {
		super(context,layout,list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// initialize the view holder
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(layout, parent, false);
			holder = new ViewHolder();
			holder.id = (TextView) convertView.findViewById(R.id.tvList3id);
			holder.label = (TextView) convertView.findViewById(R.id.tvList3label);
			holder.names = (TextView) convertView.findViewById(R.id.tvList3names);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// now set the view holder's values
		Session s = list.get(position);
		holder.id.setText(
			String.format(
				"#%d (%.1f sec)",
				s.getId(),
				s.getDuration() / 1000f
			)
		);
		holder.label.setText(
			String.format(
				"start: %s\nstop: %s",
				AppBlauzahn.DATETIMESTAMP.format(
					s.getStart()
				),
				AppBlauzahn.DATETIMESTAMP.format(
					s.getStop()
				)
			)
		);
		// TODO get names of sighted devices
		holder.names.setText(
			String.format(
				" %d %s",
				s.getSightingsCount(),
				s.getSightingsNames()
			)
		);
		// and give back the modified view
		return convertView;
	}
}
