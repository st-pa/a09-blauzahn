package com.example.a09_blauzahn.model;

import java.io.Serializable;
import java.util.Date;

public class BTSighting
implements Serializable {

	/** default serial id. */
	private static final long serialVersionUID = 1L;

	private long id;
	private long btSessionId;
	private Date time;
	private String name;
	private String address;
	private long rssi;

	/** constructor. */
	public BTSighting(long id, long btSessionId, Date time, String name, String address, long rssi) {
		super();
		this.id = id;
		this.btSessionId = btSessionId;
		this.time = time;
		this.name = name;
		this.address = address;
		this.rssi = rssi;
	}

	/**
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * @return the id
	 */
	public final long getBTSessionId() {
		return btSessionId;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the address
	 */
	public final String getAddress() {
		return address;
	}

	/**
	 * @return the rssi
	 */
	public final long getRssi() {
		return rssi;
	}

	/**
	 * @param id the id to set
	 */
	public final void setBTSessionId(long btSessionId) {
		this.btSessionId = btSessionId;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @param address the address to set
	 */
	public final void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @param rssi the rssi to set
	 */
	public final void setRssi(long rssi) {
		this.rssi = rssi;
	}

	/**
	 * @return the time
	 */
	public final Date getTime() {
		return time;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(long id) {
		this.id = id;
	}

	/**
	 * @param time the time to set
	 */
	public final void setTime(Date time) {
		this.time = time;
	}

	
}
