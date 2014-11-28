package com.example.a09_blauzahn.model;

import java.util.Date;
import java.util.List;

/**
 * represents a collection of sightings of the same hardware address.
 * @author stpa
 */
public class Device {

	private String address;
	private List<String> names;
	private Date firstTime,lastTime;
	private long sessionCount;
	private double avgRssi;

	/** Constructor. */
	public Device(
		String address,
		List<String> names,
		Date firstTime,
		Date lastTime,
		long sessionCount,
		double avgRssi
	) {
		super();
		this.address = address;
		this.names = names;
		this.firstTime = firstTime;
		this.lastTime = lastTime;
		this.sessionCount = sessionCount;
		this.avgRssi = avgRssi;
	}

	public final String getAddress() {
		return address;
	}

	public final List<String> getNames() {
		return names;
	}

	public final Date getFirstTime() {
		return firstTime;
	}

	public final Date getLastTime() {
		return lastTime;
	}

	public final long getSessionCount() {
		return sessionCount;
	}

	public final double getAvgRssi() {
		return avgRssi;
	}

	public final void setAddress(String address) {
		this.address = address;
	}

	public final void setNames(List<String> names) {
		this.names = names;
	}

	public final void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}

	public final void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public final void setSessionCount(long sessionCount) {
		this.sessionCount = sessionCount;
	}

	public final void setAvgRssi(double avgRssi) {
		this.avgRssi = avgRssi;
	}
}
