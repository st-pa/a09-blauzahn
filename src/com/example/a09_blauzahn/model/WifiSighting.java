package com.example.a09_blauzahn.model;

import java.io.Serializable;

public class WifiSighting
implements Serializable {

	/** default serial id. */
	private static final long serialVersionUID = 1L;

	private long   id;
	private long   wifiSessionId;
	private String BSSID;
	private String capabilities;
	private long   frequency;
	private long   level;
	private String SSID;
	private long   timestamp;

	/** Constructor. */
	public WifiSighting(
		long id,
		long wifiSessionId,
		String BSSID,
		String capabilities,
		long frequency, long level,
		String SSID, long timestamp
	) {
		super();
		this.id = id;
		this.wifiSessionId = wifiSessionId;
		this.BSSID = BSSID;
		this.capabilities = capabilities;
		this.frequency = frequency;
		this.level = level;
		this.SSID = SSID;
		this.timestamp = timestamp;
	}

	/**
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * @return the wifiSessionId
	 */
	public final long getWifiSessionId() {
		return wifiSessionId;
	}

	/**
	 * @return the bSSID
	 */
	public final String getBSSID() {
		return BSSID;
	}

	/**
	 * @return the capabilities
	 */
	public final String getCapabilities() {
		return capabilities;
	}

	/**
	 * @return the frequency
	 */
	public final long getFrequency() {
		return frequency;
	}

	/**
	 * @return the level
	 */
	public final long getLevel() {
		return level;
	}

	/**
	 * @return the sSID
	 */
	public final String getSSID() {
		return SSID;
	}

	/**
	 * @return the timestamp
	 */
	public final long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(long id) {
		this.id = id;
	}

	/**
	 * @param wifiSessionId the wifiSessionId to set
	 */
	public final void setWifiSessionId(long wifiSessionId) {
		this.wifiSessionId = wifiSessionId;
	}

	/**
	 * @param bSSID the bSSID to set
	 */
	public final void setBSSID(String bSSID) {
		BSSID = bSSID;
	}

	/**
	 * @param capabilities the capabilities to set
	 */
	public final void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public final void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	/**
	 * @param level the level to set
	 */
	public final void setLevel(long level) {
		this.level = level;
	}

	/**
	 * @param sSID the sSID to set
	 */
	public final void setSSID(String sSID) {
		SSID = sSID;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public final void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
