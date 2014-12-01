package com.example.a09_blauzahn.view;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a09_blauzahn.AppBlauzahn;
import com.example.a09_blauzahn.R;
import com.example.a09_blauzahn.model.Device;
import com.example.a09_blauzahn.model.Sighting;
import com.example.a09_blauzahn.util.DBHelper;

/**
 * for displaying {@link Sighting}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterDevice
extends AbstractAdapter<Device> {

	/** inner convenience class for speeding up list display. */
	static class ViewHolder {
		TextView label;
		TextView names;
	}

	/** Constructor. */
	public AdapterDevice(
		Context context,
		int layout,
		List<Device> list
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
			holder.label = (TextView) convertView.findViewById(R.id.tvList2label);
			holder.names  = (TextView) convertView.findViewById(R.id.tvList2names);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// now set the view holder's values
		Device d = this.getItem(position);
		holder.label.setText(
			String.format(
				"#%d (x%d) Ø%.2fdb first:%s\n[%s] last:%s",
				position,
				d.getSessionCount(),
				d.getAvgRssi(),
				AppBlauzahn.DATETIMESTAMP.format(d.getFirstTime()),
				d.getAddress(),
				AppBlauzahn.DATETIMESTAMP.format(d.getLastTime())
			)
		);
		StringBuffer names = new StringBuffer();
		Iterator<String> iterator = d.getNames().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			if (name == null) {
				names.append(DBHelper.NULL_VALUE);
			} else {
				names
				.append("'")
				.append(name)
				.append("'");
			}
			if (iterator.hasNext()) {
				names.append(", ");
			}
		}
		holder.names.setText(names);
		// and give back the modified view
		return convertView;
	}
}
