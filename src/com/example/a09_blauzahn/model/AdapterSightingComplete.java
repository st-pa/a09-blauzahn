package com.example.a09_blauzahn.model;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a09_blauzahn.AppBlauzahn;
import com.example.a09_blauzahn.R;

/**
 * for displaying {@link Sighting}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterSightingComplete
extends ArrayAdapter<Sighting> {

	/** inner convenience class for speeding up list display. */
	static class ViewHolder {
		TextView label,name;
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
		super(context,R.layout.list_sighting_complete,list);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// initialize the view holder
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_sighting_complete, null);
			holder = new ViewHolder();
			holder.label     = (TextView) convertView.findViewById(R.id.tvList1label);
			holder.name      = (TextView) convertView.findViewById(R.id.tvList1name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// now set the view holder's values
		Sighting s = list.get(position);
		holder.label.setText(
			String.format(
				"#%d (%d) %s [%s] %ddb",
				s.getId(),
				s.getSessionId(),
				AppBlauzahn.DATETIMESTAMP.format(
					s.getTime()
				),
				s.getAddress(),
				s.getRssi()
			)
		);
		String name = s.getName();
		if (name != null && name.length() > 0) {
			holder.name.setText(s.getName());
		} else {
			holder.name.setVisibility(View.GONE);
		}
		// and give back the modified view
		return convertView;
	}

}
