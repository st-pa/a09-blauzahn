package com.example.a09_blauzahn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.example.a09_blauzahn.model.Session;
import com.example.aTTS.AppTTS;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

/**
 * @author stpa
 */
public class AppBlauzahn
extends AppTTS {

	////////////////////////////////////////////
	// local constants
	////////////////////////////////////////////

	/** date/time-format for use in timestamps. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("hh:MM:ss,SS ",new Locale("DE"));

	/** String-values for Integer-constants. */
	private static final Map<Integer,String> CONST_SCANMODE;
	static {
		CONST_SCANMODE = new TreeMap<Integer,String>();
		CONST_SCANMODE.put(BluetoothAdapter.SCAN_MODE_NONE,"ScanModeNone");
		CONST_SCANMODE.put(BluetoothAdapter.SCAN_MODE_CONNECTABLE,"ScanModeConnectable");
		CONST_SCANMODE.put(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,"ScanModeConnectableDiscoverable");
	}

	/** String-values for Integer-constants. */
	private static final Map<Integer,String> CONST_STATE;
	static {
		CONST_STATE = new TreeMap<Integer,String>();
		CONST_STATE.put(BluetoothAdapter.STATE_OFF,"off");
		CONST_STATE.put(BluetoothAdapter.STATE_TURNING_ON,"turning on");
		CONST_STATE.put(BluetoothAdapter.STATE_ON,"on");
		CONST_STATE.put(BluetoothAdapter.STATE_TURNING_OFF,"turning off");
	}

	////////////////////////////////////////////
	// local fields
	////////////////////////////////////////////

	/** for easier access to the database. */
	protected DBHelper db;
	/** for convenience. */
	protected Context context;
	/** for collecting log messages. */
	protected StringBuffer log = new StringBuffer();
	/** informationen about the currently running bluetooth-session. */
	protected Session session;

	////////////////////////////////////////////
	// methods and functions
	////////////////////////////////////////////

	@Override
	public void onTerminate() {
		db.close();
		super.onTerminate();
	}

	/** try to initialize the database. */
	protected void init(Context context) {
		if (context != null) {
			db = new DBHelper(context);
			this.context = context;
		}
	}

	/**
	 * returns a multiline text description of the given object.
	 * @param ba {@link BluetoothAdapter}
	 * @return {@link String}
	 */
	public static final String getDescription(BluetoothAdapter ba) {
		StringBuffer s = new StringBuffer();
		if (ba != null) {
			s
			.append("address = \"")
			.append(ba.getAddress())
			.append("\"\n")

			.append("name = \"")
			.append(ba.getName())
			.append("\"\n")

			.append("searching : ")
			.append(ba.isDiscovering() ? "yes" : "no")
			.append("\n")

			.append("activated : ")
			.append(ba.isEnabled() ? "yes" : "no")
			.append("\n")

			.append("scanMode = ")
			.append(CONST_SCANMODE.get(ba.getScanMode()))
			.append("\n")

			.append("state = ")
			.append(CONST_STATE.get(ba.getState()))
			.append("\n")

//			.append("LEAdvertiser : ")
//			.append(ba.getBluetoothLeAdvertiser() == null ? "nicht " : " ")
//			.append("vorhanden\n")
//
//			.append("multi-ad-support : ")
//			.append(ba.isMultipleAdvertisementSupported() ? "ja" : "nein")
//			.append("\n")
//
//			.append("offload filtering : ")
//			.append(ba.isOffloadedFilteringSupported() ? "ja" : "nein")
//			.append("\n")
			;
		} else {
			s.append("no bluetooth adapter found.\n");
		}
		return s.toString();
	}

	/** make a formatted timestamp. */
	private String now() {
		return SDF.format(new Date());
	}

	/** insert a message to the top of the log. */
	public void log(String text) {
		this.log.insert(
			0,
			new StringBuffer()
			.append(now())
			.append(text)
			.append("\n")
			.toString()
		);
	}

	/** returns the contents of the app's log. */
	public String getLog() {
		return this.log.toString();
	}

	/** <code>true</code> if the app's log is empty. */
	public boolean isLogEmpty() {
		return this.log.length() == 0;
	}
}
