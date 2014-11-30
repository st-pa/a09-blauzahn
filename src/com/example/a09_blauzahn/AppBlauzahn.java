package com.example.a09_blauzahn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.app.AlarmManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a09_blauzahn.model.Session;
import com.example.a09_blauzahn.model.Sighting;
import com.example.a09_blauzahn.util.DBHelper;
import com.example.a09_blauzahn.util.Settings;
import com.example.aTTS.AppTTS;

/**
 * @author stpa
 */
public class AppBlauzahn
extends AppTTS {

	////////////////////////////////////////////
	// global constants
	////////////////////////////////////////////

	public static final String EXTRA_LIST_TYPE  = "listType";
	public static final int LIST_TYPE_SIGHTINGS = 0;
	public static final int LIST_TYPE_DEVICES   = 1;
	public static final int LIST_TYPE_SESSIONS  = 2;

	/** tag for LogCat-messages. */
	private static final String TAG = "Blauzahn";
	/** predefined {@link Locale} for use in {@link String#format(Locale,String,Object...)}. */
	private static final Locale LOCALE = new Locale("DE");
	/** time-format for use in timestamps. */
	public static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("HH:mm:ss,SS ",LOCALE);
	/** date/time-format for use in timestamps. */
	public static final SimpleDateFormat DATETIMESTAMP = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss",LOCALE);

	////////////////////////////////////////////
	// local constants
	////////////////////////////////////////////

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
	/** system service for timed action execution. */
	protected AlarmManager am;
	/** user defined settings for this application. */
	protected Settings settings;
	/** label for showing status of bluetooth and wifi. */
	protected TextView tvLabel;
	protected Button btConnect;

	////////////////////////////////////////////
	// methods and functions
	////////////////////////////////////////////

	@Override
	public void onTerminate() {
		db.close();
		super.onTerminate();
	}

	/** try to initialize the database. 
	 * @param btConnect 
	 * @param tvLabel */
	protected void init(Context context, TextView tvLabel, Button btConnect) {
		this.tvLabel = tvLabel;
		this.btConnect = btConnect;
		if (context != null) {
			if (db == null) db = new DBHelper(context);
			if (ba == null) ba = BluetoothAdapter.getDefaultAdapter();
			if (am == null) am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			if (settings == null) settings = db.getSettings();
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
			.append("address = \"").append(ba.getAddress()).append("\"\n")
			.append("name = \"")   .append(ba.getName())   .append("\"\n")
			.append("searching : ").append(ba.isDiscovering() ? "yes" : "no").append("\n")
			.append("activated : ").append(ba.isEnabled() ? "yes" : "no").append("\n")
			.append("scanMode = ") .append(CONST_SCANMODE.get(ba.getScanMode())).append("\n")
			.append("state = ")    .append(CONST_STATE.get(ba.getState())).append("\n")
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
			.append("describeContents = ").append(Integer.toBinaryString(info.describeContents())).append("\n")
			.append("getExtraInfo = \"")  .append(info.getExtraInfo()).append("\"\n")
			.append("getReason = \"")     .append(info.getReason()).append("\"\n")
			.append("getSubType = ")      .append(info.getSubtype()).append(", ")
			.append("getSubTypeName = \"").append(info.getSubtypeName()).append("\"\n")
			.append("getType = ")         .append(info.getType()).append(", ")
			.append("getTypeName = \"")   .append(info.getTypeName()).append("\"\n")
			.append("getState.name = \"") .append(info.getState().name()).append("\"\n")
			.append("getDetailedState.name = \"").append(info.getDetailedState().name()).append("\"\n")
			.append("isAvailable = ")     .append(info.isAvailable()).append("\n")
			.append("isConnected = ")     .append(info.isConnected()).append("\n")
			.append("isConnectedOrConnecting = ").append(info.isConnectedOrConnecting()).append("\n")
			.append("isFailover = ")      .append(info.isFailover()).append("\n")
			.append("isRoaming = ")       .append(info.isRoaming()).append("\n")
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

	/** discover bluetooth devices. */
	protected void scan() {
		if (ba.isEnabled()) {
			toast("start discovery");
			// there should only be one receiver, so check if it's there already
			if (br == null) {
				// create a receiver for bluetooth
				br = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						log(action);
						if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
							log("discovery started");
							if (session != null) {
								log("error: double discovery session");
							}
							Date now = new Date();
							session = new Session(-1,now,now,null);
							session.setId(db.addSession(session));
						} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
							log("discovery finished");
							if (session != null) {
								Date now = new Date();
								session.setStop(now);
								db.setSession(session);
								session = null;
							} else log("error: missing discovery session");
							btConnect.setEnabled(true);
						} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
							showStatus();
						} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
							Date now = new Date();
							BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
							short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
							String msg = String.format(
								LOCALE,
								"found: %s [%s] %ddb",
								device.getAddress(),
								device.getName(),
								rssi
							);
							Sighting s = new Sighting(
								-1,
								session.getId(),
								now,
								device.getName(),
								device.getAddress(),
								rssi
							);
							s.setId(db.addSighting(s));
							toast(msg);
							Log.d(
								TAG,
								msg
							);
		//					if (!ba.getBondedDevices().contains(device)) {
		//						doPairDevice(device);
		//					}
						}
						showStatus();
					}
				};
				registerReceiver(br,new IntentFilter(BluetoothDevice.ACTION_FOUND));
//				registerReceiver(br,new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
//				registerReceiver(br,new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
				registerReceiver(br,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
				registerReceiver(br,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
				registerReceiver(br,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
//				registerReceiver(br,new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
			}
			// start to scan for devices
			ba.startDiscovery();
		} else {
			toast("error: expected active adapter");
		}
	}

	/** update the verbal bluetooth-status display. */
	protected void showStatus() {
		tvLabel.setText(AppBlauzahn.getDescription(ba));
		tvLabel.refreshDrawableState();
	}

	/**
	 * show a short toast message.
	 * @param text {@link String}
	 */
	protected void toast(String text) {
		log(text);
		Toast.makeText(
			this,
			text,
			Toast.LENGTH_SHORT
		).show();
	}
}
