package com.example.a09_blauzahn.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WifiDevice
implements Serializable {

	/** default serial id. */
	private static final long serialVersionUID = 1L;

	private String BSSID;
	private List<String> names;
	private Date firstTime,lastTime;
	private long sessionCount;
	private double avgLevel;

	public WifiDevice(
		String bssid, List<String> names, Date firstTime,
		Date lastTime, long sessionCount, double avgLevel
	) {
		super();
		this.BSSID = bssid;
		this.names = names;
		this.firstTime = firstTime;
		this.lastTime = lastTime;
		this.sessionCount = sessionCount;
		this.avgLevel = avgLevel;
	}

	/**
	 * @return the serialversionuid
	 */
	public static final long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the bssid
	 */
	public final String getBSSID() {
		return BSSID;
	}

	/**
	 * @return the names
	 */
	public final List<String> getNames() {
		return names;
	}

	/**
	 * @return the firstTime
	 */
	public final Date getFirstTime() {
		return firstTime;
	}

	/**
	 * @return the lastTime
	 */
	public final Date getLastTime() {
		return lastTime;
	}

	/**
	 * @return the sessionCount
	 */
	public final long getSessionCount() {
		return sessionCount;
	}

	/**
	 * @return the avgLevel
	 */
	public final double getAvgLevel() {
		return avgLevel;
	}

	/**
	 * @param bssid the bssid to set
	 */
	public final void setBssid(String bssid) {
		this.BSSID = bssid;
	}

	/**
	 * @param names the names to set
	 */
	public final void setNames(List<String> names) {
		this.names = names;
	}

	/**
	 * @param firstTime the firstTime to set
	 */
	public final void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}

	/**
	 * @param lastTime the lastTime to set
	 */
	public final void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	/**
	 * @param sessionCount the sessionCount to set
	 */
	public final void setSessionCount(long sessionCount) {
		this.sessionCount = sessionCount;
	}

	/**
	 * @param avgLevel the avgLevel to set
	 */
	public final void setAvgLevel(double avgLevel) {
		this.avgLevel = avgLevel;
	}
}
