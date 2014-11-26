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

	/**
	 * liefert eine mehrzeilige Text-Beschreibung des gegebenen objektes.
	 * @param ba {@link BluetoothAdapter}
	 * @return
	 */
	public static final String getDescription(BluetoothAdapter ba) {
		StringBuffer s = new StringBuffer();
		if (ba != null) {
			s
			.append("Adresse = \"")
			.append(ba.getAddress())
			.append("\"\n")

			.append("Name = \"")
			.append(ba.getName())
			.append("\"\n")

			.append("suchend : ")
			.append(ba.isDiscovering() ? "ja" : "nein")
			.append("\n")

			.append("aktiviert : ")
			.append(ba.isEnabled() ? "ja" : "nein")
			.append("\n")

			.append("scanMode = ")
			.append(CONST_SCANMODE.get(ba.getScanMode()))
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
			s.append("Kein Blauzahn-Anschluß vorhanden.\n");
		}
		return s.toString();
	}
}
