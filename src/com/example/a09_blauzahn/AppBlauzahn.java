package com.example.a09_blauzahn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.NetworkInfo;

import com.example.a09_blauzahn.model.Session;
import com.example.aTTS.AppTTS;

/**
 * @author stpa
 */
public class AppBlauzahn
extends AppTTS {

	////////////////////////////////////////////
	// local constants
	////////////////////////////////////////////

	/** time-format for use in timestamps. */
	public static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("HH:MM:ss,SS ",new Locale("DE"));
	/** date/time-format for use in timestamps. */
	public static final SimpleDateFormat DATETIMESTAMP = new SimpleDateFormat("yy-mm-dd HH:MM:ss,SS ",new Locale("DE"));

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
	/** application-wide bluetooth adapter to avoid reinitializations. */
	protected BluetoothAdapter ba;
	/** application-wide broadcast receiver to avoid reinitializations. */
	protected BroadcastReceiver br;

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
			if (db == null) db = new DBHelper(context);
			if (ba == null) ba = BluetoothAdapter.getDefaultAdapter();
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

	/**
	 * returns a multiline text description of the given object.
	 * @param info {@link NetworkInfo}
	 * @return {@link String}
	 */
	public static final String getDescription(NetworkInfo info) {
		StringBuffer s = new StringBuffer();
		if (info != null) {
			s
			.append("describeContents = ")
			.append(Integer.toBinaryString(info.describeContents()))
			.append("\n")

			.append("getExtraInfo = \"")
			.append(info.getExtraInfo())
			.append("\"\n")

			.append("getReason = \"")
			.append(info.getReason())
			.append("\"\n")

			.append("getSubType = ")
			.append(info.getSubtype())
			.append(", ")

			.append("getSubTypeName = \"")
			.append(info.getSubtypeName())
			.append("\"\n")

			.append("getType = ")
			.append(info.getType())
			.append(", ")

			.append("getTypeName = \"")
			.append(info.getTypeName())
			.append("\"\n")

			.append("getState.name = \"")
			.append(info.getState().name())
			.append("\"\n")

			.append("getDetailedState.name = \"")
			.append(info.getDetailedState().name())
			.append("\"\n")

			.append("isAvailable = ")
			.append(info.isAvailable())
			.append("\n")

			.append("isConnected = ")
			.append(info.isConnected())
			.append("\n")

			.append("isConnectedOrConnecting = ")
			.append(info.isConnectedOrConnecting())
			.append("\n")

			.append("isFailover = ")
			.append(info.isFailover())
			.append("\n")

			.append("isRoaming = ")
			.append(info.isRoaming())
			.append("\n")
			;
		} else {
			s.append("no network information found.");
		}
		return s.toString();
	}

	/** make a formatted timestamp. */
	private String now() {
		return TIMESTAMP.format(new Date());
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
