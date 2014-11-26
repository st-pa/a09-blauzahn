package com.example.a09_blauzahn;

import java.util.Map;
import java.util.TreeMap;

import android.bluetooth.BluetoothAdapter;

public class Helper {

	private static final Map<Integer,String> CONST_SCANMODE;
	static {
		CONST_SCANMODE = new TreeMap<Integer,String>();
		CONST_SCANMODE.put(BluetoothAdapter.SCAN_MODE_NONE,"ScanModeNone");
		CONST_SCANMODE.put(BluetoothAdapter.SCAN_MODE_CONNECTABLE,"ScanModeConnectable");
		CONST_SCANMODE.put(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,"ScanModeConnectableDiscoverable");
	}
	private static final Map<Integer,String> CONST_STATE;
	static {
		CONST_STATE = new TreeMap<Integer,String>();
		CONST_STATE.put(BluetoothAdapter.STATE_OFF,"off");
		CONST_STATE.put(BluetoothAdapter.STATE_TURNING_ON,"turning on");
		CONST_STATE.put(BluetoothAdapter.STATE_ON,"on");
		CONST_STATE.put(BluetoothAdapter.STATE_TURNING_OFF,"turning off");
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
}
