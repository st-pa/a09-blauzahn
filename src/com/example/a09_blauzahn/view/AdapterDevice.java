package com.example.a09_blauzahn.view;

import java.util.Iterator;
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
import com.example.a09_blauzahn.model.Device;
import com.example.a09_blauzahn.model.Sighting;

/**
 * for displaying {@link Sighting}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterDevice
extends ArrayAdapter<Device> {

	/** inner convenience class for speeding up list display. */
	static class ViewHolder {
		TextView label1;
		TextView label2;
		TextView names;
	}

	/** for convenience, store the {@link LayoutInflater}. */
	private static LayoutInflater inflater;

	/** a {@link List} of the {@link Sighting} instances to be displayed. */
	private List<Device> list;

	/** Constructor. */
	public AdapterDevice(
		Context context,
		List<Device> list
	) {
		super(context,R.layout.list_device,list);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// initialize the view holder
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_device, parent, false);
			holder = new ViewHolder();
			holder.label1 = (TextView) convertView.findViewById(R.id.tvList2label1);
			holder.label2 = (TextView) convertView.findViewById(R.id.tvList2label2);
			holder.names  = (TextView) convertView.findViewById(R.id.tvList2names);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// now set the view holder's values
		Device d = list.get(position);
		holder.label1.setText(
			String.format(
				"#%d (x%d) Ø%.2fdb first:%s",
				position,
				d.getSessionCount(),
				d.getAvgRssi(),
				AppBlauzahn.DATETIMESTAMP.format(d.getFirstTime())
			)
		);
		holder.label2.setText(
			String.format(
				"[%s] last:%s",
				d.getAddress(),
				AppBlauzahn.DATETIMESTAMP.format(d.getLastTime())
			)
		);
/*		holder.id.setText(
			String.format(
				"#%d",
				position
			)
		);
*/		StringBuffer names = new StringBuffer();
		Iterator<String> iterator = d.getNames().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			names
			.append("'")
			.append(name)
			.append("'");
			if (iterator.hasNext()) {
				names.append(", ");
			}
		}
		holder.names.setText(names);
		// and give back the modified view
		return convertView;
	}
}
