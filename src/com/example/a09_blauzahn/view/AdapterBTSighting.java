package com.example.a09_blauzahn.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a09_blauzahn.AppBlauzahn;
import com.example.a09_blauzahn.R;
import com.example.a09_blauzahn.model.BTSighting;
import com.example.a09_blauzahn.util.DBHelper;

/**
 * for displaying {@link BTSighting}-information in a customized {@link ListView}.
 * @author stpa
 */
public class AdapterBTSighting
extends AbstractAdapter<BTSighting> {

	/** inner convenience class for speeding up list display. */
	static class ViewHolder {
		TextView label;
		TextView name;
	}

	/** Constructor. */
	public AdapterBTSighting(
		Context context,
		int layout,
		List<BTSighting> list
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
			holder.label = (TextView) convertView.findViewById(R.id.tvList1label);
			holder.name  = (TextView) convertView.findViewById(R.id.tvList1name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// now set the view holder's values
		convertView.setBackgroundColor(this.getColor(position));
		BTSighting s = this.getItem(position);
		holder.label.setText(
			String.format(
				"#%d (%d) %s [%s] %ddb",
				s.getId(),
				s.getBTSessionId(),
				AppBlauzahn.DATETIMESTAMP.format(
					s.getTime()
				),
				s.getAddress(),
				s.getRssi()
			)
		);
		String name = DBHelper.nullValue(s.getName());
		if (name != null && name.length() > 0) {
			holder.name.setText(name);
			holder.name.setVisibility(View.VISIBLE);
		} else {
			holder.name.setVisibility(View.GONE);
		}
		// and give back the modified view
		return convertView;
	}

}
