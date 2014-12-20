package com.example.a09_blauzahn.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a09_blauzahn.AppBlauzahn;
import com.example.a09_blauzahn.R;
import com.example.a09_blauzahn.model.WifiDevice;
import com.example.a09_blauzahn.model.WifiSighting;

/**
 * for displaying {@link WifiSighting}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterWifiDevice
extends AbstractAdapter<WifiDevice> {

	/** inner convenience class for speeding up list display. */
	static class ViewHolder {
		TextView label1;
		TextView label2;
	}

	/** Constructor. */
	public AdapterWifiDevice(
		Context context,
		int layout,
		List<WifiDevice> list
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
			holder.label1 = (TextView) convertView.findViewById(R.id.tvList6label1);
			holder.label2  = (TextView) convertView.findViewById(R.id.tvList6label2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// now set the view holder's values
		convertView.setBackgroundColor(this.getColor(position));
		WifiDevice d = this.getItem(position);
		holder.label1.setText(
			String.format(
				"#%d (x%d) Ø%.2fdb first:%s\n[%s] last:%s",
				position,
				d.getSessionCount(),
				d.getAvgLevel(),
				AppBlauzahn.DATETIMESTAMP.format(d.getFirstTime()),
				d.getBSSID(),
				AppBlauzahn.DATETIMESTAMP.format(d.getLastTime())
			)
		);
		holder.label2.setText(
			AppBlauzahn.getNameListAsText(
				d.getNames()
			)
		);
		// and give back the modified view
		return convertView;
	}
}
