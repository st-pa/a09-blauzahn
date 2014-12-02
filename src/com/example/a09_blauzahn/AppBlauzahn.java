package com.example.a09_blauzahn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.app.AlarmManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a09_blauzahn.model.BTSession;
import com.example.a09_blauzahn.model.BTSighting;
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

	/** folder on the sd card used for exporting the sql database. */
	public static final String TARGET_FOLDER = new StringBuffer()
		.append(Environment.getExternalStorageDirectory().getAbsolutePath())
		.append(DBHelper.SEPARATOR)
		.append(DBHelper.DB_NAME)
		.append(DBHelper.SEPARATOR)
		.toString()
	;

	/** tag for LogCat-messages. */
	public static final String TAG = "Blauzahn";
	/** predefined {@link Locale} for use in {@link String#format(Locale,String,Object...)}. */
	public static final Locale LOCALE = new Locale("DE");
	/** time-format for use in timestamps. */
	public static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("HH:mm:ss,SS ",LOCALE);
	/** date/time-format for use in timestamps. */
	public static final SimpleDateFormat DATETIMESTAMP = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss",LOCALE);

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

	protected ComponentName service;
	/** for easier access to the database. */
	protected DBHelper db;
	/** for convenience. */
	protected Context context;
	/** for collecting log messages. */
	protected StringBuffer log = new StringBuffer();
	/** informationen about the currently running bluetooth-session. */
	protected BTSession session;
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
	/** utility pointer to the main activity's connect button. */
	protected Button btConnect;

	////////////////////////////////////////////
	// methods and functions
	////////////////////////////////////////////

	@Override
	public void onCreate() {
		super.onCreate();
		service = startService(new Intent(this,ServiceScan.class));
	}

	@Override
	public void onTerminate() {
		db.close();
		disconnect();
		stopService(new Intent(this,ServiceScan.class));
		super.onTerminate();
	}

	/**
	 * try to initialize the database. 
	 * @param btConnect 
	 * @param tvLabel
	 */
	protected void init(Context context, TextView tvLabel, Button btConnect) {
		this.tvLabel = tvLabel;
		this.btConnect = btConnect;
		if (context != null) {
			if (db == null) db = new DBHelper(context);
//			dbImportFromAssets("2012.01.31-21.44.08-blauzahn.sqlite");
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
	public static final String timestamp() {
		return TIMESTAMP.format(new Date());
	}

	/** make a formatted date-timestamp. */
	public static final String datetimestamp() {
		return DATETIMESTAMP.format(new Date());
	}

	/** insert a message to the top of the log. */
	public void log(String text) {
		String timestamped = new StringBuffer()
		.append(timestamp())
		.append(text)
		.append("\n")
		.toString();
		System.out.println(timestamped);
		this.log.insert(0,timestamped);
		
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
							session = new BTSession(-1,now,now,null);
							session.setId(db.addBTSession(session));
						} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
							log("discovery finished");
							if (session != null) {
								Date now = new Date();
								session.setStop(now);
								db.setBTSession(session);
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
							BTSighting s = new BTSighting(
								-1,
								session.getId(),
								now,
								device.getName(),
								device.getAddress(),
								rssi
							);
							s.setId(db.addBTSighting(s));
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

	public void disconnect() {
		if (ba != null) {
			toast("disconnect Bluetooth adapter");
			if (ba.isDiscovering()) {
				toast("cancel Bluetooth discovery");
				ba.cancelDiscovery();
			}
			if (ba.isEnabled()) {
				toast("disable Bluetooth");
				ba.disable();
			}
		}
		if (br != null) {
			toast("unregister Bluetooth receiver");
			unregisterReceiver(br);
			br = null;
		}
		showStatus();
	}

	/**
	 * calls {@link Settings#setValidFrom(Date)} with the current
	 * time and writes the settings to the database and updates
	 * the id value of the settings.
	 */
	public void updateSettings() {
		this.settings.setValidFrom(new Date());
		this.settings.setId(
			this.db.addSettings(this.settings)
		);
	}

	/**
	 * copy the given file or folder from app's assets to sd card.
	 * @param source {@link String} the source file's name in the app's assets
	 * @param target {@link String} complete target path on sd card.
	 */
	private void copyAssetToSD(String source,String target) {
		try {
			// make sure the target folder(s) exist
			File targetFolder = new File(target);
			targetFolder.mkdirs();
			// copy from source to target
			InputStream is = this.getAssets().open(source);
			OutputStream os = new FileOutputStream(target);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = is.read(buffer)) != -1) {
				os.write(buffer,0,read);
			}
			// wrap up nice and neat
			is.close();
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * export the current database to {@link #TARGET_FOLDER}.
	 * @param string 
	 */
	public void dbExport(String targetName) {
		db.dbExport(TARGET_FOLDER,targetName);
	}

	/**
	 * import the given database file from the app's assets folder
	 * by first copying it to {@link #TARGET_FOLDER} and from there
	 * to the location of the current database.
	 * @param fileName {@link String} a file name in the app's assets folder.
	 */
	protected void dbImportFromAssets(String fileName) {
		copyAssetToSD(fileName,TARGET_FOLDER + fileName);
		db.dbImport(TARGET_FOLDER + fileName);
	}
}
