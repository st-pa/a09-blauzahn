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

public class AppBlauzahn
extends AppTTS {

	/** Zeitstempelformat. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("hh:MM:ss,SS ",new Locale("DE"));

	/** StringWerte für IntegerKonstanten. */
	private static final Map<Integer,String> CONST_SCANMODE;
	static {
		CONST_SCANMODE = new TreeMap<Integer,String>();
		CONST_SCANMODE.put(BluetoothAdapter.SCAN_MODE_NONE,"ScanModeNone");
		CONST_SCANMODE.put(BluetoothAdapter.SCAN_MODE_CONNECTABLE,"ScanModeConnectable");
		CONST_SCANMODE.put(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,"ScanModeConnectableDiscoverable");
	}

	/** StringWerte für IntegerKonstanten. */
	private static final Map<Integer,String> CONST_STATE;
	static {
		CONST_STATE = new TreeMap<Integer,String>();
		CONST_STATE.put(BluetoothAdapter.STATE_OFF,"off");
		CONST_STATE.put(BluetoothAdapter.STATE_TURNING_ON,"turning on");
		CONST_STATE.put(BluetoothAdapter.STATE_ON,"on");
		CONST_STATE.put(BluetoothAdapter.STATE_TURNING_OFF,"turning off");
	}

	/** zum Zugriff auf die Datenbank. */
	protected DBHelper db;
	/** zum Anzeigen von Toasts. */
	protected Context context;
	/** zum akkumulieren von Meldungen. */
	protected StringBuffer log = new StringBuffer();
	/** Informationen über die laufende Bluetooth-Sitzung. */
	protected Session session;

	@Override
	public void onTerminate() {
		db.close();
		super.onTerminate();
	}

	/** versucht, die Datenbank zu initialisieren. */
	protected void init(Context context) {
		if (context != null) {
			db = new DBHelper(context);
			this.context = context;
		}
	}

	/**
	 * liefert eine mehrzeilige Text-Beschreibung des gegebenen objektes.
	 * @param ba {@link BluetoothAdapter}
	 * @return
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

	/** gibt einen formatierten zeitstempel zurück. */
	private String now() {
		return SDF.format(new Date());
	}

	/** fügt einen Text ins log ein. */
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

	/** gibt das gesammelte Log zurück. */
	public String getLog() {
		return this.log.toString();
	}

	/** gibt wahr zurück, wenn noch nichts im log steht. */
	public boolean isLogEmpty() {
		return this.log.length() == 0;
	}
}
