package com.example.a09_blauzahn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a09_blauzahn.model.BTDevice;
import com.example.a09_blauzahn.model.BTSession;
import com.example.a09_blauzahn.model.BTSighting;
import com.example.a09_blauzahn.model.WifiSession;
import com.example.a09_blauzahn.model.WifiSighting;
import com.example.a09_blauzahn.util.DBHelper;
import com.example.a09_blauzahn.util.Settings;
import com.example.aTTS.AppTTS;

/**
 * @author stpa
 * @see for further reading, 99 pages of "Bluetooth for Programmers"
 * <a href="http://people.csail.mit.edu/rudolph/Teaching/Articles/BTBook-march.pdf"
 * >http://people.csail.mit.edu/rudolph/Teaching/Articles/BTBook-march.pdf</a>
 */
public class AppBlauzahn
extends AppTTS {

	////////////////////////////////////////////
	// global constants
	////////////////////////////////////////////

	public static final String EXTRA_LIST_TYPE   = "listType";
	public static final String EXTRA_LIST_LABEL   = "listLabel";
	public static final String EXTRA_LIST_BTDEVICE = "listBTDevice";
	public static final String EXTRA_LIST_BTSESSION = "listBTSession";
	public static final String EXTRA_LIST_BTSIGHTING = "listBTSighting";
	public static final int LIST_TYPE_BTSIGHTINGS = 0;
	public static final int LIST_TYPE_BTDEVICES   = 1;
	public static final int LIST_TYPE_BTSESSIONS  = 2;

	/** whether or not to perform a database import from the project's assets. */
	private static final boolean IMPORT = false;
	/** file name of the sqlite database to be imported upon startup. */
	private static final String IMPORT_DB_FROM_ASSETS = "2012.02.02_22.11.37-blauzahn.sqlite";
//	private static final String IMPORT_DB_FROM_ASSETS = "2014.12.04_08.42.26-blauzahn.sqlite";

	/** folder path on the sd card used for exporting the sql database. */
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
	public static final SimpleDateFormat DATETIMESTAMP = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss",LOCALE);

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
	/** information about the currently running bluetooth-session. */
	protected BTSession session;
	/** information about the currently running wifi-session. */
	protected WifiSession sessionWifi;
	/** application-wide bluetooth adapter to avoid reinitializations. */
	protected BluetoothAdapter ba;
	/** application-wide broadcast receiver to avoid reinitializations. */
	protected BroadcastReceiver br;
	/** system service for timed action execution. */
	protected AlarmManager am;
	/** user defined settings for this application. */
	protected Settings settings;
	/** application-wide wifi manager. */
	protected WifiManager wm;
	/** wifi receiver. */
	protected BroadcastReceiver wr;

	////////////////////////////////////////////
	// gui elements
	////////////////////////////////////////////

	/** label for displaying the log. */
	protected TextView tvLog;
	/** utility pointer to the main activity's connect button. */
	protected Button btConnect;
//	/** for monitoring network connections */
//	private ConnectivityManager cm;

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
	 * @param context {@link Context}
	 * @param tvLog {@link TextView}
	 * @param btConnect {@link Button}
	 */
	protected void init(
		Context context,
		TextView tvLog,
		Button btConnect
	) {
		this.tvLog = tvLog;
		this.btConnect = btConnect;
		if (context != null) {
			if (db == null) db = new DBHelper(context);
			if (IMPORT) dbImportFromAssets(IMPORT_DB_FROM_ASSETS);
			if (ba == null) ba = BluetoothAdapter.getDefaultAdapter();
			if (am == null) am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			if (wm == null) wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//			if (cm == null) cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			this.context = context;
		}
		if (settings == null) settings = db.getSettings();
//		log("settings from " + DATETIMESTAMP.format(settings.getValidFrom()) + "\n" + getDescription(settings));
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

	/**
	 * returns a multiline text description of the given object.
	 * @param device {@link BTDevice}
	 * @return {@link String}
	 */
	public static final String getDescription(BTDevice device) {
		return new StringBuffer()
		.append(String.format("average signal strength = %.1fdb\n",device.getAvgRssi()))
		.append("number of sessions = ").append(device.getSessionCount()).append("\n")
		.append("known names = ").append(AppBlauzahn.getNameListAsText(device.getNames())).append("\n")
		.append("address = [").append(device.getAddress()).append("]\n")
		.append("first encounter = ").append(DATETIMESTAMP.format(device.getFirstTime())).append("\n")
		.append("last encounter = ").append(DATETIMESTAMP.format(device.getLastTime())).append("\n")
		.toString();
	}

	/**
	 * returns a multiline text description of the given object.
	 * @param device {@link BTSession}
	 * @return {@link String}
	 */
	public static String getDescription(BTSession session) {
		return new StringBuffer()
		.append("session id = ").append(session.getId()).append("\n")
		.append("number of sightings = ").append(session.getBTSightingsCount()).append("\n")
		.append("sighted names = ")
		.append(AppBlauzahn.getNameListAsText(session.getBTSightingsNames())).append("\n")
		.append("duration = ").append(session.getDuration()).append("ms\n")
		.append("start time = ").append(DATETIMESTAMP.format(session.getStart())).append("\n")
		.append("stop time = ").append(DATETIMESTAMP.format(session.getStop())).append("\n")
		.toString();
	}

	/**
	 * returns a multiline text description of the given object.
	 * @param sighting {@link BTSighting}
	 * @return {@link String}
	 */
	public static String getDescription(BTSighting sighting) {
		return new StringBuffer()
		.append("sighting id = ").append(Long.toString(sighting.getId())).append("\n")
		.append("session id = ").append(Long.toString(sighting.getBTSessionId())).append("\n")
		.append("time = ").append(DATETIMESTAMP.format(sighting.getTime())).append("\n")
		.append("name = ").append(DBHelper.nullValue(sighting.getName())).append("\n")
		.append("address = ").append(sighting.getAddress()).append("\n")
		.append("rssi = ").append(Long.toString(sighting.getRssi())).append("db\n")
		.toString();
	}

	/**
	 * returns a multiline text description of the given object.
	 * @param sighting {@link WifiSighting}
	 * @return {@link String}
	 */
	public static String getDescription(WifiSighting sighting) {
		return new StringBuffer()
		.append("sighting id = ").append(Long.toString(sighting.getId())).append("\n")
		.append("session id = ").append(Long.toString(sighting.getWifiSessionId())).append("\n")
		.append("bssid = ").append(sighting.getBSSID()).append("\n")
		.append("capabilities = ").append(sighting.getCapabilities()).append("\n")
		.append("frequency = ").append(Long.toString(sighting.getFrequency())).append("\n")
		.append("level = ").append(sighting.getLevel()).append("db\n")
		.append("ssid = ").append(sighting.getSSID()).append("\n")
		.append("time = ").append(DATETIMESTAMP.format(new Date(sighting.getTimestamp()))).append("\n")
		.toString();
	}

	/**
	 * returns a multiline text description of the given object.
	 * @param settings {@link Settings}
	 * @return {@link String}
	 */
	public static String getDescription(Settings settings) {
		return new StringBuffer()
		.append("id = ").append(settings.getId()).append("\n")
		.append("validFrom = ").append(DATETIMESTAMP.format(settings.getValidFrom())).append("\n")
		.append("btOn = ").append(Boolean.toString(settings.isBtOn())).append("\n")
		.append("btAuto = ").append(Boolean.toString(settings.isBtAuto())).append("\n")
		.append("btDisable = ").append(Boolean.toString(settings.isBtDisable())).append("\n")
		.append("btInterval = ").append(Long.toString(settings.getBtInterval())).append("\n")
		.append("btName = ").append(settings.getBtName()).append("\n")
		.append("wifiOn = ").append(Boolean.toString(settings.isWifiOn())).append("\n")
		.append("wifiAuto = ").append(Boolean.toString(settings.isWifiAuto())).append("\n")
		.append("wifiDisable = ").append(Boolean.toString(settings.isWifiDisable())).append("\n")
		.append("wifiInterval = ").append(Long.toString(settings.getWifiInterval())).append("\n")
		.append("wifiName = ").append(settings.getWifiName()).append("\n")
		.toString();
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
		tvLog.setText(log);
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
			toast("start bluetooth discovery");
			// there should only be one receiver, so check if it's there already
			if (br == null) {
				// create a receiver for bluetooth
				br = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						log(action);
						if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
							log("bluetooth discovery started");
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
							log(BluetoothAdapter.ACTION_STATE_CHANGED);
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
			if (!ba.isDiscovering()) {
				ba.startDiscovery();
			}
		} else {
			toast("error: expected active adapter");
		}
	}

	protected void scanWifi() {
		if (!wm.isWifiEnabled()) {
			toast("turning on wifi");
			wm.setWifiEnabled(true);
		}
		if (wm.isWifiEnabled()) {
			toast("start wifi discovery");
			if (wr == null) {
				wr = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						log(action);
						Bundle bundle = intent.getExtras();
						if (bundle != null) {
							Set<String> keys = bundle.keySet();
							for (String key : keys) {
								log("key [" + key + "] value [" + bundle.getInt(key) + "]");
							}
						}
						if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
							log("WIFI SCAN RESULTS AVAILABLE");
							List<ScanResult> results = wm.getScanResults();
							startWifiSession();
							for (ScanResult result : results) {
								String msg = String.format(
									LOCALE,
									"wifi: %s [%s] %ddb",
									result.BSSID,
									result.SSID,
									result.level
								);
								WifiSighting s = new WifiSighting(
									-1,
									sessionWifi.getId(),
									result
								);
								s.setId(db.addWifiSighting(s));
								toast(msg);
								Log.d(
									TAG,
									msg
								);
								log(AppBlauzahn.getDescription(s));
							}
							// TODO handle wifi events
						} else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
							log("WIFI STATE CHANGED");
							int state = bundle.getInt(WifiManager.EXTRA_NEW_STATE);
							if (WifiManager.WIFI_STATE_ENABLED == state) {
								startWifiSession();
							} else if (state == WifiManager.WIFI_STATE_DISABLED) {
								stopWifiSession();
							} else if (state == WifiManager.WIFI_STATE_DISABLING) {
								stopWifiSession();
							}
						} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
							log("wifi supplicant state changed".toUpperCase(LOCALE));
							// TODO handle wifi events
						} else if (WifiManager.NETWORK_IDS_CHANGED_ACTION.equals(action)) {
							log("network ids changed".toUpperCase(LOCALE));
							// TODO handle wifi events
						} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
							log("wifi rssi changed".toUpperCase(LOCALE));
							// TODO handle wifi events
						}
					}
				};
				registerReceiver(wr,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				registerReceiver(wr,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
				registerReceiver(wr,new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
//				registerReceiver(wr,new IntentFilter(WifiManager.NETWORK_IDS_CHANGED_ACTION));
//				registerReceiver(wr,new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
			}
			wm.startScan();
		}
	}

	private void startWifiSession() {
		if (sessionWifi == null) {
			Date now = new Date();
			sessionWifi = new WifiSession(-1,now,now,null);
			sessionWifi.setId(db.addWifiSession(sessionWifi));
		}
	}

	private void stopWifiSession() {
		if (sessionWifi != null) {
			Date now = new Date();
			sessionWifi.setStop(now);
			db.setWifiSession(sessionWifi);
			sessionWifi = null;
			toast("wifi session stopped");
		} else {
			toast("no wifi session to stop");
		}
	}

	/** update the verbal bluetooth-status display. */
	protected void showStatus() {
		log(AppBlauzahn.getDescription(ba));
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
		if (wm != null && wm.isWifiEnabled()) {
			wm.setWifiEnabled(false);
			toast("wifi disabled");
		}
		if (wr != null) {
			toast("unregister Wifi receiver");
			stopWifiSession();
			unregisterReceiver(wr);
			wr = null;
		}
		log("disconnection complete");
	}

	/**
	 * calls {@link Settings#setValidFrom(Date)} with the current
	 * time and writes the settings to the database and updates
	 * the id value of the settings.
	 */
	public void updateSettings() {
		Date now = new Date();
		this.settings.setValidFrom(now);
		this.settings.setId(
			this.db.addSettings(this.settings)
		);
//		log("settings added " + DATETIMESTAMP.format(now) + "\n" + getDescription(settings));
	}

	/**
	 * copy the given file or folder from app's assets to sd card.
	 * @param source {@link String} the source file's name in the app's assets
	 * @param target {@link String} complete target path on sd card.
	 */
	private void copyAssetToSD(String source,String target) {
		try {
			// make sure the target folder(s) exist
			File targetFolder = new File(target).getParentFile();
			if (!targetFolder.canRead()) {
				targetFolder.mkdirs();
			}
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

	/**
	 * a comma-separated {@link String} containing
	 * the names (not the addresses!) of the sighted
	 * devices during this bluetooth session.
	 * @param names {@link List}<{@link String}> list of names that can be <code>null</code>
	 * @return {@link String}
	 */
	public static String getNameListAsText(List<String> names) {
		StringBuffer s = new StringBuffer();
		if (names != null) {
			Iterator<String> iterator = names.iterator();
			while (iterator.hasNext()) {
				String name = iterator.next();
				if (name == null || name.length() == 0) {
					s.append(DBHelper.NULL_VALUE);
				} else {
					s.append("\'")
					.append(name)
					.append("\'");
				}
				if (iterator.hasNext()) {
					s.append(", ");
				}
			}
		}
		return s.toString();
	}
}
