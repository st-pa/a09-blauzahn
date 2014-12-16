package com.example.a09_blauzahn.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a09_blauzahn.AppBlauzahn;
import com.example.a09_blauzahn.R;
import com.example.a09_blauzahn.model.WifiSighting;
import com.example.a09_blauzahn.util.DBHelper;

/**
 * for displaying {@link WifiSighting}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterWifiSighting
extends AbstractAdapter<WifiSighting> {

	/** inner convenience class for speeding up list display. */
	static class ViewHolder {
		TextView label1;
		TextView label2;
	}

	/** Constructor. */
	public AdapterWifiSighting(
		Context context,
		int layout,
		List<WifiSighting> list
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
			holder.label1 = (TextView) convertView.findViewById(R.id.tvList5label1);
			holder.label2 = (TextView) convertView.findViewById(R.id.tvList5label2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// now set the view holder's values
		WifiSighting s = this.getItem(position);
		holder.label1.setText(
			String.format(
				"#%d (%d) %s [%s] %ddb",
				s.getId(),
				s.getWifiSessionId(),
				AppBlauzahn.DATETIMESTAMP.format(
					s.getTimestamp()
				),
				s.getBSSID(),
				s.getLevel()
			)
		);
		String name = DBHelper.nullValue(s.getSSID());
		if (name != null && name.length() > 0) {
			holder.label2.setText(name);
			holder.label2.setVisibility(View.VISIBLE);
		} else {
			holder.label2.setVisibility(View.GONE);
		}
		// and give back the modified view
		return convertView;
	}
}
