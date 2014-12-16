package com.example.a09_blauzahn.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a09_blauzahn.AppBlauzahn;
import com.example.a09_blauzahn.R;
import com.example.a09_blauzahn.model.WifiSession;

/**
 * for displaying {@link WifiSession}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterWifiSession
extends AbstractAdapter<WifiSession> {

	/** inner convenience class for speeding up list display. */
	static class ViewHolder {
		TextView label1;
		TextView label2;
		TextView label3;
	}

	/** Constructor. */
	public AdapterWifiSession(
		Context context,
		int layout,
		List<WifiSession> list
	) {
		super(context,layout,list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// initialize the view holder
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(
				this.layout,
				parent,
				false
			);
			holder = new ViewHolder();
			holder.label1 = (TextView) convertView.findViewById(R.id.tvList4label1);
			holder.label2 = (TextView) convertView.findViewById(R.id.tvList4label2);
			holder.label3 = (TextView) convertView.findViewById(R.id.tvList4label3);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// now set the view holder's values
		WifiSession s = this.getItem(position);
		holder.label1.setText(
			String.format(
				"#%d (%.1f sec)",
				s.getId(),
				s.getDuration() / 1000f
			)
		);
		holder.label2.setText(
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
		// get names of sighted devices
		holder.label3.setText(
			String.format(
				"(%d) %s",
				s.getWifiSightingsCount(),
				AppBlauzahn.getNameListAsText(s.getWifiSightingsNames())
			)
		);
		// and give back the modified view
		return convertView;
	}
}
