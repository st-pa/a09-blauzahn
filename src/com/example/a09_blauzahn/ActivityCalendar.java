package com.example.a09_blauzahn;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

/**
 * lets the user change the system date/time
 * and updates all time related database columns
 * accordingly.
 * @author stpa
 */
public class ActivityCalendar
extends ActionBarActivity
implements OnClickListener, OnTimeChangedListener, OnTouchListener, OnHoverListener {

	////////////////////////////////////////////
	// local fields
	////////////////////////////////////////////

	/** access to convenience methods for this app. */
	private AppBlauzahn app;

	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	////////////////////////////////////////////
	// gui-elements
	////////////////////////////////////////////

	private TextView tvCalendarDifference;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private Button btOkay;
	private Button btCancel;

	////////////////////////////////////////////
	// methods and functions
	////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		app = (AppBlauzahn) getApplication();

		datePicker = (DatePicker) findViewById(R.id.datePicker);
		datePicker.setOnClickListener(this);
		datePicker.setOnTouchListener(this);
		datePicker.setOnHoverListener(this);

		timePicker = (TimePicker) findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		tvCalendarDifference = (TextView) findViewById(R.id.tvCalendarDifference);
		year = datePicker.getYear();
		month = datePicker.getMonth();
		day = datePicker.getDayOfMonth();
		hour = timePicker.getCurrentHour();
		minute = timePicker.getCurrentMinute();
		showDifference();

		btOkay = (Button) findViewById(R.id.btCalendarOkay);
		btCancel = (Button) findViewById(R.id.btCalendarCancel);
		btOkay.setOnClickListener(this);
		btCancel.setOnClickListener(this);
	}

	/** calculate the difference between system time and selected time for display. */
	private void showDifference() {
		year = datePicker.getYear();
		month = datePicker.getMonth();
		day = datePicker.getDayOfMonth();
		Calendar cal = new GregorianCalendar(
			year,
			month,
			day,
			hour,
			minute
		);
		tvCalendarDifference.setText(
			String.format(
				getString(R.string.tvCalendarDifference),
				new Date().getTime() - cal.getTime().getTime()
			)
		);
		app.toast(AppBlauzahn.DATETIMESTAMP.format(cal.getTime()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_calendar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		int i = 0;
		if (v == btCancel) {
			finish();
		} else if (v == btOkay) {
			// TODO react to calendar change
			app.toast("no calendar change as of yet, sorry.");
		} else if (v == datePicker) {
			year = datePicker.getYear();
			month = datePicker.getMonth();
			day = datePicker.getDayOfMonth();
			showDifference();
		}
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		this.hour = hourOfDay;
		this.minute = minute;
		showDifference();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		showDifference();
		return true;
	}

	@Override
	public boolean onHover(View v, MotionEvent event) {
		showDifference();
		return false;
	}
}
